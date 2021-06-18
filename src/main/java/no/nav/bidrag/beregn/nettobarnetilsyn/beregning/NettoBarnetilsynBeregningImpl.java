package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public class NettoBarnetilsynBeregningImpl implements NettoBarnetilsynBeregning {

  @Override
  public List<ResultatBeregning> beregn(GrunnlagBeregning grunnlagBeregning) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();
    var tempResultatBelop = BigDecimal.ZERO;
    var fradragsbelopPerBarn = BigDecimal.ZERO;
    var resultatBelop = BigDecimal.ZERO;

    // Summerer faktisk utgift pr barn
    var faktiskUtgiftListeSummertPerBarn = grunnlagBeregning.getFaktiskUtgiftListe().stream()
        .collect(groupingBy(FaktiskUtgift::getSoknadsbarnPersonId, reducing(BigDecimal.ZERO, FaktiskUtgift::getBelop, BigDecimal::add)));

    // Barn som er 13 år eller eldre skal ikke telles med ved henting av sjablon for maks tilsyn og fradrag
    var listeMedBarnUnder13Aar = grunnlagBeregning.getFaktiskUtgiftListe().stream()
        .filter(faktiskUtgift -> faktiskUtgift.getSoknadsbarnAlder() < 13)
        .collect(groupingBy(FaktiskUtgift::getSoknadsbarnPersonId, reducing(BigDecimal.ZERO, FaktiskUtgift::getBelop, BigDecimal::add)));

    var antallBarnIPerioden = listeMedBarnUnder13Aar.size();

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregning.getSjablonListe(), antallBarnIPerioden);

    var antallBarnMedTilsynsutgift = 0;
    var samletFaktiskUtgiftBelop = BigDecimal.ZERO;
    for (var tempFaktiskUtgift : faktiskUtgiftListeSummertPerBarn.entrySet()) {
      if (tempFaktiskUtgift.getValue().compareTo(BigDecimal.ZERO) > 0) {
        antallBarnMedTilsynsutgift++;
        samletFaktiskUtgiftBelop = samletFaktiskUtgiftBelop.add(tempFaktiskUtgift.getValue());
      }
    }

    var maksTilsynsbelop = sjablonNavnVerdiMap.get(SjablonNavn.MAKS_TILSYN.getNavn());

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

      resultatBeregningListe.add(new ResultatBeregning(faktiskUtgift.getKey(), resultatBelop, byggSjablonResultatListe(sjablonNavnVerdiMap,
          grunnlagBeregning.getSjablonListe())));
    }

    return resultatBeregningListe;
  }

  private BigDecimal beregnFradragsbelopPerBarn(int antallBarnMedTilsynsutgift, BigDecimal tilsynsbelop, Map<String, BigDecimal> sjablonNavnVerdiMap) {

    var maksFradrag = sjablonNavnVerdiMap.get(SjablonNavn.MAKS_FRADRAG.getNavn());
    var skattAlminneligInntektProsent = sjablonNavnVerdiMap.get(SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.getNavn())
        .divide(BigDecimal.valueOf(100), new MathContext(10, RoundingMode.HALF_UP));

    var fradragsbelop = tilsynsbelop.multiply(skattAlminneligInntektProsent);

    var maksFradragsbelop = maksFradrag.multiply(skattAlminneligInntektProsent);

    if (fradragsbelop.compareTo(maksFradragsbelop) > 0) {
      fradragsbelop = maksFradragsbelop;
    }

    var fradragsbelopPerBarn = BigDecimal.ZERO;

    if (antallBarnMedTilsynsutgift > 0) {
      fradragsbelopPerBarn = fradragsbelop.divide(BigDecimal.valueOf(antallBarnMedTilsynsutgift),
          new MathContext(10, RoundingMode.HALF_UP));
    }

    return fradragsbelopPerBarn;
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<SjablonPeriode> sjablonPeriodeListe, int antallBarnIPerioden) {

    var sjablonListe = sjablonPeriodeListe.stream()
        .map(SjablonPeriode::getSjablon)
        .collect(toList());

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT));

    // MaksTilsyn
    sjablonNavnVerdiMap.put(SjablonNavn.MAKS_TILSYN.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.MAKS_TILSYN, antallBarnIPerioden));

    // MaksFradrag
    sjablonNavnVerdiMap.put(SjablonNavn.MAKS_FRADRAG.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.MAKS_FRADRAG, antallBarnIPerioden));

    return sjablonNavnVerdiMap;
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonPeriodeNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap,
      List<SjablonPeriode> sjablonPeriodeListe) {
    var sjablonPeriodeNavnVerdiListe = new ArrayList<SjablonPeriodeNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) ->
        sjablonPeriodeNavnVerdiListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe, sjablonNavn), sjablonNavn, sjablonVerdi)));
    return sjablonPeriodeNavnVerdiListe.stream().sorted(comparing(SjablonPeriodeNavnVerdi::getNavn)).collect(toList());
  }

  private Periode hentPeriode(List<SjablonPeriode> sjablonPeriodeListe, String sjablonNavn) {
    return sjablonPeriodeListe.stream()
        .filter(sjablonPeriode -> sjablonPeriode.getSjablon().getNavn().equals(sjablonNavn))
        .map(SjablonPeriode::getPeriode)
        .findFirst()
        .orElse(new Periode(LocalDate.MIN, LocalDate.MAX));
  }
}
