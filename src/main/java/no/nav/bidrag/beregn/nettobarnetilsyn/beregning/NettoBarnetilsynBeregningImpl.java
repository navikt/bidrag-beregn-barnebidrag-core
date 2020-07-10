package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import static java.util.stream.Collectors.toCollection;

import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregningListe;

public class NettoBarnetilsynBeregningImpl implements NettoBarnetilsynBeregning {

//  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregningListe beregn(
      BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();
    Double resultatBelop;
    Double fradragsbelopPerBarn;
//    Double Belop = 0d;

    int antallBarn = beregnNettoBarnetilsynGrunnlagPeriodisert.getFaktiskUtgiftListe().size();

/*    int antallBarn = (int)beregnNettoBarnetilsynGrunnlagPeriodisert.getFaktiskUtgiftListe()
        .stream()
        .map(FaktiskUtgift::getSoknadsbarnPersonId)
        .distinct()
        .count();
    System.out.println("Antall barn: " + antallBarn);*/



    var faktiskUtgiftListeSortertPaaBarn = beregnNettoBarnetilsynGrunnlagPeriodisert
        .getFaktiskUtgiftListe()
        .stream()
        .sorted(Comparator.comparingInt(FaktiskUtgift::getSoknadsbarnPersonId))
        .collect(Collectors.toList());

        /*    ,
        toCollection(ArrayList::new));
        */


        //ArrayList<FaktiskUtgift>
        /*.collect(Collectors.groupingBy(FaktiskUtgift::getSoknadsbarnPersonId),
            Collectors.summingDouble(FaktiskUtgift::getFaktiskUtgiftBelop),
            Collectors.toCollection(ArrayList::new));
*/



    System.out.println("Antall barn: " + antallBarn);

    var maksTilsynsbelop = SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
        SjablonNavn.MAKS_TILSYN, antallBarn);
    System.out.println("Maks tilsynsbeløp: " + maksTilsynsbelop);

    Double samletFaktiskUtgiftBelop = faktiskUtgiftListeSortertPaaBarn
        .stream()
        .map(FaktiskUtgift::getFaktiskUtgiftBelop)
        .mapToDouble(Double::doubleValue).sum();

    System.out.println("Samlet beløp for faktisk utgift: " + samletFaktiskUtgiftBelop);

    Boolean brukMaksTilsynsbelop;

    if (samletFaktiskUtgiftBelop > SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
        SjablonNavn.MAKS_TILSYN, antallBarn)) {
      brukMaksTilsynsbelop = Boolean.TRUE;
      fradragsbelopPerBarn = beregnFradragsbelopPerBarn(beregnNettoBarnetilsynGrunnlagPeriodisert, antallBarn,
          maksTilsynsbelop);
    } else {
      brukMaksTilsynsbelop = Boolean.FALSE;
      fradragsbelopPerBarn = beregnFradragsbelopPerBarn(beregnNettoBarnetilsynGrunnlagPeriodisert, antallBarn,
          samletFaktiskUtgiftBelop);
    }

    for (FaktiskUtgift faktiskUtgift: faktiskUtgiftListeSortertPaaBarn) {
      if (brukMaksTilsynsbelop) {
        // Finner prosentandel av totalbeløp og beregner så andel av maks tilsynsbeløp
        resultatBelop = (faktiskUtgift.getFaktiskUtgiftBelop()/samletFaktiskUtgiftBelop)
            *
            SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
                SjablonNavn.MAKS_TILSYN, antallBarn);
      }
      else {
        resultatBelop = faktiskUtgift.getFaktiskUtgiftBelop();
      }

      // Trekker fra beregnet fradragsbeløp
      resultatBelop -= fradragsbelopPerBarn;

      // Setter beregnet netto barnetilsynsbeløp til 0 hvis beregnet beløp er under 0
      if (resultatBelop.compareTo(0.0) < 0){
        resultatBelop = Double.valueOf(0.0);
      }

      System.out.println("Beregnet netto barnetilsynsbeløp: " + Math.round(resultatBelop * 100.0) / 100.0);

      resultatBeregningListe.add(new ResultatBeregning(faktiskUtgift.getSoknadsbarnPersonId(),
          Math.round(resultatBelop * 100.0) / 100.0));

    }

    return new ResultatBeregningListe(resultatBeregningListe);
  }

  @Override
  public Double beregnFradragsbelopPerBarn(
      BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert, int antallBarn,
      double tilsynsbelop) {

    System.out.println("Tilsynsbeløp: " + tilsynsbelop);
    System.out.println("Skattesats: " + SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
        SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT)/100);
    System.out.println("Maks fradragsbeløp: " + SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
        SjablonNavn.MAKS_FRADRAG, antallBarn));

    var fradragsbelop = tilsynsbelop * (SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
        SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT)/100);
    System.out.println("Beregnet fradragsbeløp for samlet tilsynsbeløp " + fradragsbelop);

    var maksFradragsbelop = SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
            SjablonNavn.MAKS_FRADRAG, antallBarn)
            *
            (SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
                SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT)/100);

    if (fradragsbelop > maksFradragsbelop) {
      fradragsbelop = maksFradragsbelop;
    }

    System.out.println("Maks fradragsbeløp beregnet vha sjabloner: " + maksFradragsbelop);
    System.out.println("Endelig fradragsbeløp: " + fradragsbelop);

    var fradragsbelopPerBarn = fradragsbelop/antallBarn;
    System.out.println("Fradragsbeløp fordelt per barn: " + fradragsbelopPerBarn);

    return fradragsbelopPerBarn;
  }
}