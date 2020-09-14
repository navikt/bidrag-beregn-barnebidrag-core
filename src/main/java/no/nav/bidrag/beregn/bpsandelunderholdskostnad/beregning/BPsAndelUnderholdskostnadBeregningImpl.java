package no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class BPsAndelUnderholdskostnadBeregningImpl implements BPsAndelUnderholdskostnadBeregning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert) {

    BigDecimal andelProsent;
    BigDecimal andelBelop = BigDecimal.valueOf(0);

    // Legger sammen inntektene
    var inntektBP = beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntektBP()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(Double.valueOf(0), Double::sum);

    // Legger sammen inntektene
    var inntektBM = beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntektBM()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(Double.valueOf(0), Double::sum);

    // Legger sammen inntektene
    var inntektBB = beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntektBB()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(Double.valueOf(0), Double::sum);

    // Test på om barnets inntekt er høyere enn 100 ganger sats for forhøyet forskudd. Hvis så så skal ikke BPs andel regnes ut.
    if (inntektBB > SjablonUtil
        .hentSjablonverdi(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.FORSKUDDSSATS_BELOP) * 100) {
      andelProsent = BigDecimal.valueOf(0.0);
    } else {
      andelProsent = BigDecimal.valueOf(
          inntektBP / (inntektBP + inntektBM + inntektBB)).multiply(BigDecimal.valueOf(100));

      andelProsent = andelProsent.setScale(1, RoundingMode.HALF_UP);

      // Utregnet andel skal ikke være større en 5/6

      if (andelProsent.compareTo(BigDecimal.valueOf(83.3)) > 0) {
        andelProsent = BigDecimal.valueOf(83.3);
      }

      andelBelop =
          BigDecimal.valueOf(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getUnderholdskostnadBelop())
          .multiply(andelProsent).divide(BigDecimal.valueOf(100));

      System.out.println("Andel Beløp før avrunding : " + andelBelop);

      andelBelop = andelBelop.setScale(1, RoundingMode.HALF_UP);
      System.out.println("Andel Beløp etter avrunding : " + andelBelop);

    }

    return new ResultatBeregning(andelProsent.doubleValue(), andelBelop.doubleValue());

  }

  @Override
  public ResultatBeregning beregnMedGamleRegler(
      BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert) {

    BigDecimal andelProsent;
    BigDecimal andelBelop = BigDecimal.valueOf(0);

    // Legger sammen inntektene
    var inntektBP = beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntektBP()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(Double.valueOf(0), Double::sum);

    // Legger sammen inntektene
    var inntektBM = beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntektBM()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(Double.valueOf(0), Double::sum);

    // Legger sammen inntektene
    var inntektBB = beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntektBB()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(Double.valueOf(0), Double::sum);

    // Test på om barnets inntekt er høyere enn 100 ganger sats for forhøyet forskudd. Hvis så så skal ikke BPs andel regnes ut.
    if ((inntektBB > SjablonUtil.hentSjablonverdi(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert
            .getSjablonListe(),
            SjablonTallNavn.FORSKUDDSSATS_BELOP) * 100)) {
      andelProsent = BigDecimal.valueOf(0.0);
    } else {
      andelProsent = BigDecimal.valueOf(
          inntektBP / (inntektBP + inntektBM + inntektBB)).multiply(BigDecimal.valueOf(100));

      var sjettedeler = new ArrayList<BigDecimal>();

      sjettedeler.add(BigDecimal.valueOf((1.0 / 6)).multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf((2.0 / 6)).multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf((3.0 / 6)).multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf((4.0 / 6)).multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf((5.0 / 6)).multiply(BigDecimal.valueOf(100)));

//      BigDecimal finalAndel = andel;
      BigDecimal finalAndel = andelProsent;
      andelProsent = sjettedeler.stream()
          .min(Comparator.comparing(a -> finalAndel.subtract(a).abs()))
          .orElseThrow(() -> new IllegalArgumentException("Empty collection"));

      andelProsent = andelProsent.setScale(1, RoundingMode.HALF_UP);

      // Utregnet andel skal ikke være større en 5/6

      if (andelProsent.compareTo(BigDecimal.valueOf(83.3)) > 0) {
        andelProsent = BigDecimal.valueOf(83.3);
      }
      andelBelop =
          BigDecimal.valueOf(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getUnderholdskostnadBelop())
              .multiply(andelProsent).divide(BigDecimal.valueOf(100));

      System.out.println("Andel Beløp før avrunding : " + andelBelop);

      andelBelop = andelBelop.setScale(1, RoundingMode.HALF_UP);
      System.out.println("Andel Beløp etter avrunding : " + andelBelop);

    }

    return new ResultatBeregning(andelProsent.doubleValue(), andelBelop.doubleValue());

    }

}



