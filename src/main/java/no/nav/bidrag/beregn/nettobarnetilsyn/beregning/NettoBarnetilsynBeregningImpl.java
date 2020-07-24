package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

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
    Double resultatBelop = 0.0;
    Double fradragsbelopPerBarn = 0.0;

    var faktiskUtgiftListeSummertPerBarn = beregnNettoBarnetilsynGrunnlagPeriodisert
        .getFaktiskUtgiftListe()
        .stream()
        .collect(groupingBy(FaktiskUtgift::getSoknadsbarnPersonId, summingDouble(FaktiskUtgift::getFaktiskUtgiftBelop)));

    var antallBarnIPerioden = faktiskUtgiftListeSummertPerBarn.size();
    System.out.println("Antall barn i perioden: " + antallBarnIPerioden);

    var maksTilsynsbelop = SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(), SjablonNavn.MAKS_TILSYN,
        antallBarnIPerioden);
    System.out.println("Maks tilsynsbeløp: " + maksTilsynsbelop);

    var samletFaktiskUtgiftBelop = faktiskUtgiftListeSummertPerBarn.values().stream().mapToDouble(Double::doubleValue).sum();
    System.out.println("Samlet beløp for faktisk utgift: " + samletFaktiskUtgiftBelop);

    if (samletFaktiskUtgiftBelop > maksTilsynsbelop) {
      fradragsbelopPerBarn = beregnFradragsbelopPerBarn(beregnNettoBarnetilsynGrunnlagPeriodisert, antallBarnIPerioden, maksTilsynsbelop);
    } else {
      fradragsbelopPerBarn = beregnFradragsbelopPerBarn(beregnNettoBarnetilsynGrunnlagPeriodisert, antallBarnIPerioden, samletFaktiskUtgiftBelop);
    }

    for (var faktiskUtgift : faktiskUtgiftListeSummertPerBarn.entrySet()) {
      if (samletFaktiskUtgiftBelop > maksTilsynsbelop) {
        // Finner prosentandel av totalbeløp og beregner så andel av maks tilsynsbeløp
        resultatBelop = (faktiskUtgift.getValue() / samletFaktiskUtgiftBelop) *
            SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(), SjablonNavn.MAKS_TILSYN, antallBarnIPerioden);
      } else {
        resultatBelop = faktiskUtgift.getValue();
      }

      // Trekker fra beregnet fradragsbeløp
      resultatBelop -= fradragsbelopPerBarn;

      // Setter beregnet netto barnetilsynsbeløp til 0 hvis beregnet beløp er under 0
      if (resultatBelop.compareTo(0.0) < 0) {
        resultatBelop = 0.0;
      }

      resultatBeregningListe.add(new ResultatBeregning(faktiskUtgift.getKey(), Math.round(resultatBelop * 100.0) / 100.0));
      System.out.println("Beregnet netto barnetilsynsbeløp: " + Math.round(resultatBelop * 100.0) / 100.0);
    }

    return resultatBeregningListe;
  }

  @Override
  public Double beregnFradragsbelopPerBarn(BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert, int antallBarn,
      double tilsynsbelop) {

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

    var fradragsbelopPerBarn = fradragsbelop / antallBarn;
    System.out.println("Fradragsbeløp fordelt per barn: " + fradragsbelopPerBarn);

    return fradragsbelopPerBarn;
  }
}