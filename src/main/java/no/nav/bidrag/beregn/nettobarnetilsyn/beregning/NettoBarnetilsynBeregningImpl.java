package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public class NettoBarnetilsynBeregningImpl implements NettoBarnetilsynBeregning {

  @Override
  public List<ResultatBeregning> beregn(GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();
    BigDecimal tempResultatBelop = BigDecimal.ZERO;
    BigDecimal fradragsbelopPerBarn = BigDecimal.ZERO;
    BigDecimal resultatBelop = BigDecimal.ZERO;

    var faktiskUtgiftListeSummertPerBarn = grunnlagBeregningPeriodisert
        .getFaktiskUtgiftListe()
        .stream()
        .collect(groupingBy(FaktiskUtgift::getFaktiskUtgiftSoknadsbarnPersonId,
            reducing(BigDecimal.ZERO, FaktiskUtgift::getFaktiskUtgiftBelop, BigDecimal::add)));

    // Barn som er 13 år eller eldre skal ikke telles med ved henting av sjablon for maks tilsyn og fradrag
    var listeMedBarnUnder13Aar = grunnlagBeregningPeriodisert
        .getFaktiskUtgiftListe()
        .stream()
        .filter(i-> i.getSoknadsbarnAlder() < 13)
        .collect(groupingBy(FaktiskUtgift::getFaktiskUtgiftSoknadsbarnPersonId,
            reducing(BigDecimal.ZERO, FaktiskUtgift::getFaktiskUtgiftBelop, BigDecimal::add)));

    var antallBarnIPerioden = listeMedBarnUnder13Aar.size();
    System.out.println("Totalt antall barn under 13 år i perioden: " + antallBarnIPerioden);

    int antallBarnMedTilsynsutgift = 0;
    var samletFaktiskUtgiftBelop = BigDecimal.ZERO;
    for (var tempFaktiskUtgift : faktiskUtgiftListeSummertPerBarn.entrySet()) {
      if (tempFaktiskUtgift.getValue().compareTo(BigDecimal.ZERO) > 0) {
        antallBarnMedTilsynsutgift ++;
        samletFaktiskUtgiftBelop = samletFaktiskUtgiftBelop.add(tempFaktiskUtgift.getValue());
      }
    }
    System.out.println("Antall barn med tilsynsutgifter i perioden: " + antallBarnMedTilsynsutgift);

    var maksTilsynsbelop = SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe()
        , SjablonNavn.MAKS_TILSYN, antallBarnIPerioden);
    System.out.println("Maks tilsynsbeløp: " + maksTilsynsbelop);

/*    var samletFaktiskUtgiftBelop = faktiskUtgiftListeSummertPerBarn.values()
        .stream()
//        .map(FaktiskUtgift::getFaktiskUtgiftBelop)
        .reduce(BigDecimal.ZERO, FaktiskUtgift::getFaktiskUtgiftBelop, BigDecimal::add));
//        .mapToDouble(Double::doubleValue).sum();*/

    System.out.println("Samlet beløp for faktisk utgift: " + samletFaktiskUtgiftBelop);

    if (samletFaktiskUtgiftBelop.compareTo(maksTilsynsbelop) > 0) {
      fradragsbelopPerBarn = beregnFradragsbelopPerBarn(grunnlagBeregningPeriodisert,
          antallBarnIPerioden, antallBarnMedTilsynsutgift, maksTilsynsbelop);
    } else {
      fradragsbelopPerBarn = beregnFradragsbelopPerBarn(grunnlagBeregningPeriodisert,
          antallBarnIPerioden, antallBarnMedTilsynsutgift, samletFaktiskUtgiftBelop);
    }

    for (var faktiskUtgift : faktiskUtgiftListeSummertPerBarn.entrySet()) {
      if (samletFaktiskUtgiftBelop.compareTo(maksTilsynsbelop) > 0) {
        // Finner prosentandel av totalbeløp og beregner så andel av maks tilsynsbeløp
        tempResultatBelop = (faktiskUtgift.getValue()
            .divide(samletFaktiskUtgiftBelop, new MathContext(2, RoundingMode.HALF_UP))
            .multiply(
            SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
                SjablonNavn.MAKS_TILSYN, antallBarnIPerioden)));
      } else {
        tempResultatBelop = faktiskUtgift.getValue();
      }

      // Trekker fra beregnet fradragsbeløp
      tempResultatBelop = tempResultatBelop.subtract(fradragsbelopPerBarn);

      // Setter beregnet netto barnetilsynsbeløp til 0 hvis beregnet beløp er under 0
      if (tempResultatBelop.compareTo(BigDecimal.valueOf(0.0)) < 0) {
        tempResultatBelop = BigDecimal.valueOf(0.0);
      }

      resultatBelop = tempResultatBelop;
      resultatBelop = resultatBelop.setScale(0, RoundingMode.HALF_UP);

      resultatBeregningListe.add(new ResultatBeregning(faktiskUtgift.getKey(), resultatBelop));
      System.out.println("Beregnet netto barnetilsynsbeløp: " + resultatBelop);
    }

    return resultatBeregningListe;
  }

  @Override
  public BigDecimal beregnFradragsbelopPerBarn(GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert, int antallBarn,
      int antallBarnMedTilsynsutgift, BigDecimal tilsynsbelop) {

    var skattAlminneligInntektProsent = SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
        SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT);
    var maksFradrag = SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(), SjablonNavn.MAKS_FRADRAG, antallBarn);

    System.out.println("Tilsynsbeløp: " + tilsynsbelop);
    System.out.println("Skattesats: " + skattAlminneligInntektProsent);
    System.out.println("Maks fradragsbeløp: " + maksFradrag);

    var fradragsbelop = tilsynsbelop.multiply(skattAlminneligInntektProsent.divide(BigDecimal.valueOf(100),
        new MathContext(10, RoundingMode.HALF_UP)));
    System.out.println("Beregnet fradragsbeløp for samlet tilsynsbeløp " + fradragsbelop);

    var maksFradragsbelop = maksFradrag.multiply(skattAlminneligInntektProsent.divide(BigDecimal.valueOf(100),
        new MathContext(10, RoundingMode.HALF_UP)));

    if (fradragsbelop.compareTo(maksFradragsbelop) > 0) {
      fradragsbelop = maksFradragsbelop;
    }

    System.out.println("Maks fradragsbeløp beregnet vha sjabloner: " + maksFradragsbelop);
    System.out.println("Endelig fradragsbeløp: " + fradragsbelop);

    var fradragsbelopPerBarn = BigDecimal.ZERO;

    if (antallBarnMedTilsynsutgift > 0){
      fradragsbelopPerBarn = fradragsbelop.divide(BigDecimal.valueOf(antallBarnMedTilsynsutgift),
          new MathContext(10, RoundingMode.HALF_UP));
    }

    System.out.println("Fradragsbeløp fordelt per barn: " + fradragsbelopPerBarn);

    return fradragsbelopPerBarn;
  }
}