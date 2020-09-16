package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public class NettoBarnetilsynBeregningImpl implements NettoBarnetilsynBeregning {

  @Override
  public List<ResultatBeregning> beregn(BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();
    Double tempResultatBelop = 0.0;
    Double fradragsbelopPerBarn = 0.0;
    BigDecimal resultatBelop = BigDecimal.valueOf(0);

    var faktiskUtgiftListeSummertPerBarn = beregnNettoBarnetilsynGrunnlagPeriodisert
        .getFaktiskUtgiftListe()
        .stream()
        .collect(groupingBy(FaktiskUtgift::getSoknadsbarnPersonId, summingDouble(FaktiskUtgift::getFaktiskUtgiftBelop)));

    var antallBarnIPerioden = faktiskUtgiftListeSummertPerBarn.size();
    System.out.println("Antall barn i perioden: " + antallBarnIPerioden);

    int antallBarnMedTilsynsutgift = 0;
    for (var tempFaktiskUtgift : faktiskUtgiftListeSummertPerBarn.entrySet()) {
      if (tempFaktiskUtgift.getValue() > 0d) {
        antallBarnMedTilsynsutgift ++;
      }
    }
    System.out.println("Antall barn med tilsynsutgifter i perioden: " + antallBarnMedTilsynsutgift);

    var maksTilsynsbelop = SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(), SjablonNavn.MAKS_TILSYN,
        antallBarnIPerioden);
    System.out.println("Maks tilsynsbeløp: " + maksTilsynsbelop);

    var samletFaktiskUtgiftBelop = faktiskUtgiftListeSummertPerBarn.values().stream().mapToDouble(Double::doubleValue).sum();
    System.out.println("Samlet beløp for faktisk utgift: " + samletFaktiskUtgiftBelop);

    if (samletFaktiskUtgiftBelop > maksTilsynsbelop) {
      fradragsbelopPerBarn = beregnFradragsbelopPerBarn(beregnNettoBarnetilsynGrunnlagPeriodisert,
          antallBarnIPerioden, antallBarnMedTilsynsutgift, maksTilsynsbelop);
    } else {
      fradragsbelopPerBarn = beregnFradragsbelopPerBarn(beregnNettoBarnetilsynGrunnlagPeriodisert,
          antallBarnIPerioden, antallBarnMedTilsynsutgift, samletFaktiskUtgiftBelop);
    }

    for (var faktiskUtgift : faktiskUtgiftListeSummertPerBarn.entrySet()) {
      if (samletFaktiskUtgiftBelop > maksTilsynsbelop) {
        // Finner prosentandel av totalbeløp og beregner så andel av maks tilsynsbeløp
        tempResultatBelop = (faktiskUtgift.getValue() / samletFaktiskUtgiftBelop) *
            SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(), SjablonNavn.MAKS_TILSYN, antallBarnIPerioden);
      } else {
        tempResultatBelop = faktiskUtgift.getValue();
      }

      // Trekker fra beregnet fradragsbeløp
      tempResultatBelop -= fradragsbelopPerBarn;

      // Setter beregnet netto barnetilsynsbeløp til 0 hvis beregnet beløp er under 0
      if (tempResultatBelop.compareTo(0.0) < 0) {
        tempResultatBelop = 0.0;
      }

      resultatBelop = BigDecimal.valueOf(tempResultatBelop);
      resultatBelop = resultatBelop.setScale(0, RoundingMode.HALF_UP);

      resultatBeregningListe.add(new ResultatBeregning(faktiskUtgift.getKey(), resultatBelop.doubleValue()));
      System.out.println("Beregnet netto barnetilsynsbeløp: " + Math.round(tempResultatBelop * 100.0) / 100.0);
    }

    return resultatBeregningListe;
  }

  @Override
  public Double beregnFradragsbelopPerBarn(BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert, int antallBarn,
      int antallBarnMedTilsynsutgift, double tilsynsbelop) {

    var skattAlminneligInntektProsent = SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
        SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT);
    var maksFradrag = SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(), SjablonNavn.MAKS_FRADRAG, antallBarn);

    System.out.println("Tilsynsbeløp: " + tilsynsbelop);
    System.out.println("Skattesats: " + skattAlminneligInntektProsent);
    System.out.println("Maks fradragsbeløp: " + maksFradrag);

    var fradragsbelop = tilsynsbelop * skattAlminneligInntektProsent / 100;
    System.out.println("Beregnet fradragsbeløp for samlet tilsynsbeløp " + fradragsbelop);

    var maksFradragsbelop = maksFradrag * skattAlminneligInntektProsent / 100;

    if (fradragsbelop > maksFradragsbelop) {
      fradragsbelop = maksFradragsbelop;
    }

    System.out.println("Maks fradragsbeløp beregnet vha sjabloner: " + maksFradragsbelop);
    System.out.println("Endelig fradragsbeløp: " + fradragsbelop);

    var fradragsbelopPerBarn = 0d;

    if (antallBarnMedTilsynsutgift > 0){
      fradragsbelopPerBarn = fradragsbelop / antallBarnMedTilsynsutgift;
    }

    System.out.println("Fradragsbeløp fordelt per barn: " + fradragsbelopPerBarn);

    return fradragsbelopPerBarn;
  }
}