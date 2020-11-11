package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public class NettoBarnetilsynBeregningImpl implements NettoBarnetilsynBeregning {

  @Override
  public List<ResultatBeregning> beregn(GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();
    var tempResultatBelop = BigDecimal.ZERO;
    var fradragsbelopPerBarn = BigDecimal.ZERO;
    var resultatBelop = BigDecimal.ZERO;

    var faktiskUtgiftListeSummertPerBarn = grunnlagBeregningPeriodisert
        .getFaktiskUtgiftListe()
        .stream()
        .collect(groupingBy(FaktiskUtgift::getFaktiskUtgiftSoknadsbarnPersonId,
            reducing(BigDecimal.ZERO, FaktiskUtgift::getFaktiskUtgiftBelop, BigDecimal::add)));

    // Barn som er 13 år eller eldre skal ikke telles med ved henting av sjablon for maks tilsyn og fradrag
    var listeMedBarnUnder13Aar = grunnlagBeregningPeriodisert
        .getFaktiskUtgiftListe()
        .stream()
        .filter(i -> i.getSoknadsbarnAlder() < 13)
        .collect(groupingBy(FaktiskUtgift::getFaktiskUtgiftSoknadsbarnPersonId,
            reducing(BigDecimal.ZERO, FaktiskUtgift::getFaktiskUtgiftBelop, BigDecimal::add)));

    var antallBarnIPerioden = listeMedBarnUnder13Aar.size();
//    System.out.println("Totalt antall barn under 13 år i perioden: " + antallBarnIPerioden);

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregningPeriodisert.getSjablonListe(), antallBarnIPerioden);

    int antallBarnMedTilsynsutgift = 0;
    var samletFaktiskUtgiftBelop = BigDecimal.ZERO;
    for (var tempFaktiskUtgift : faktiskUtgiftListeSummertPerBarn.entrySet()) {
      if (tempFaktiskUtgift.getValue().compareTo(BigDecimal.ZERO) > 0) {
        antallBarnMedTilsynsutgift++;
        samletFaktiskUtgiftBelop = samletFaktiskUtgiftBelop.add(tempFaktiskUtgift.getValue());
      }
    }
//    System.out.println("Antall barn med tilsynsutgifter i perioden: " + antallBarnMedTilsynsutgift);

    var maksTilsynsbelop = sjablonNavnVerdiMap.get(SjablonNavn.MAKS_TILSYN.getNavn());
//    System.out.println("Maks tilsynsbeløp: " + maksTilsynsbelop);

//    System.out.println("Samlet beløp for faktisk utgift: " + samletFaktiskUtgiftBelop);

    if (samletFaktiskUtgiftBelop.compareTo(maksTilsynsbelop) > 0) {
      fradragsbelopPerBarn = beregnFradragsbelopPerBarn(antallBarnMedTilsynsutgift, maksTilsynsbelop, sjablonNavnVerdiMap);
    } else {
      fradragsbelopPerBarn = beregnFradragsbelopPerBarn(antallBarnMedTilsynsutgift, samletFaktiskUtgiftBelop, sjablonNavnVerdiMap);
    }

    for (var faktiskUtgift : faktiskUtgiftListeSummertPerBarn.entrySet()) {
      if (samletFaktiskUtgiftBelop.compareTo(maksTilsynsbelop) > 0) {
        // Finner prosentandel av totalbeløp og beregner så andel av maks tilsynsbeløp
        tempResultatBelop = (faktiskUtgift.getValue()
            .divide(samletFaktiskUtgiftBelop, new MathContext(2, RoundingMode.HALF_UP))
            .multiply(maksTilsynsbelop));
      } else {
        tempResultatBelop = faktiskUtgift.getValue();
      }

      // Trekker fra beregnet fradragsbeløp
      tempResultatBelop = tempResultatBelop.subtract(fradragsbelopPerBarn);

      // Setter beregnet netto barnetilsynsbeløp til 0 hvis beregnet beløp er under 0
      if (tempResultatBelop.compareTo(BigDecimal.ZERO) < 0) {
        tempResultatBelop = BigDecimal.ZERO;
      }

      resultatBelop = tempResultatBelop;
      resultatBelop = resultatBelop.setScale(0, RoundingMode.HALF_UP);

      resultatBeregningListe.add(new ResultatBeregning(faktiskUtgift.getKey(), resultatBelop, byggSjablonResultatListe(sjablonNavnVerdiMap)));
//      System.out.println("Beregnet netto barnetilsynsbeløp: " + resultatBelop);
    }

    return resultatBeregningListe;
  }

  @Override
  public BigDecimal beregnFradragsbelopPerBarn(int antallBarnMedTilsynsutgift, BigDecimal tilsynsbelop, Map<String, BigDecimal> sjablonNavnVerdiMap) {

    var maksFradrag = sjablonNavnVerdiMap.get(SjablonNavn.MAKS_FRADRAG.getNavn());
    var skattAlminneligInntektProsent = sjablonNavnVerdiMap.get(SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.getNavn())
        .divide(BigDecimal.valueOf(100), new MathContext(10, RoundingMode.HALF_UP));

//    System.out.println("Tilsynsbeløp: " + tilsynsbelop);
//    System.out.println("Skattesats: " + skattAlminneligInntektProsent);
//    System.out.println("Maks fradragsbeløp: " + maksFradrag);

    var fradragsbelop = tilsynsbelop.multiply(skattAlminneligInntektProsent);
//    System.out.println("Beregnet fradragsbeløp for samlet tilsynsbeløp " + fradragsbelop);

    var maksFradragsbelop = maksFradrag.multiply(skattAlminneligInntektProsent);

    if (fradragsbelop.compareTo(maksFradragsbelop) > 0) {
      fradragsbelop = maksFradragsbelop;
    }

//    System.out.println("Maks fradragsbeløp beregnet vha sjabloner: " + maksFradragsbelop);
//    System.out.println("Endelig fradragsbeløp: " + fradragsbelop);

    var fradragsbelopPerBarn = BigDecimal.ZERO;

    if (antallBarnMedTilsynsutgift > 0) {
      fradragsbelopPerBarn = fradragsbelop.divide(BigDecimal.valueOf(antallBarnMedTilsynsutgift),
          new MathContext(10, RoundingMode.HALF_UP));
    }

//    System.out.println("Fradragsbeløp fordelt per barn: " + fradragsbelopPerBarn);

    return fradragsbelopPerBarn;
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<Sjablon> sjablonListe, int antallBarnIPerioden) {

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT));

    // MaksTilsyn
    sjablonNavnVerdiMap
        .put(SjablonNavn.MAKS_TILSYN.getNavn(), SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.MAKS_TILSYN, antallBarnIPerioden));

    // MaksFradrag
    sjablonNavnVerdiMap
        .put(SjablonNavn.MAKS_FRADRAG.getNavn(), SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.MAKS_FRADRAG, antallBarnIPerioden));

    return sjablonNavnVerdiMap;
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap) {
    var sjablonNavnVerdiListe = new ArrayList<SjablonNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) -> sjablonNavnVerdiListe.add(new SjablonNavnVerdi(sjablonNavn, sjablonVerdi)));
    return sjablonNavnVerdiListe;
  }
}
