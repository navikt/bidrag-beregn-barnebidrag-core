package no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class BPsAndelUnderholdskostnadBeregningImpl implements BPsAndelUnderholdskostnadBeregning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert) {

    BigDecimal andel;

    // Test på om barnets inntekt er høyere enn 100 ganger sats for forhøyet forskudd. Hvis så så skal ikke BPs andel regnes ut.
    if ((beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBB()
        > SjablonUtil
        .hentSjablonverdi(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.FORSKUDDSSATS_BELOP) * 100)) {
      andel = BigDecimal.valueOf(0.0);
    } else {
      andel = BigDecimal.valueOf(
          beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBP() /
              (beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBP() +
                  beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBM() +
                  beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBB()))
          .multiply(BigDecimal.valueOf(100));

      andel = andel.setScale(1, RoundingMode.HALF_UP);

      // Utregnet andel skal ikke være større en 5/6

      if (andel.compareTo(BigDecimal.valueOf(83.3)) > 0) {
        andel = BigDecimal.valueOf(83.3);
      }

    }

    return new ResultatBeregning(andel.doubleValue());

  }

  @Override
  public ResultatBeregning beregnMedGamleRegler(
      BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert) {

    BigDecimal andel;

    // Test på om barnets inntekt er høyere enn 100 ganger sats for forhøyet forskudd. Hvis så så skal ikke BPs andel regnes ut.
    if ((beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBB()
        > SjablonUtil
        .hentSjablonverdi(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.FORSKUDDSSATS_BELOP) * 100)) {
      andel = BigDecimal.valueOf(0.0);
    } else {
      andel = BigDecimal.valueOf(
          beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBP() /
              (beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBP() +
                  beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBM() +
                  beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBB()))
          .multiply(BigDecimal.valueOf(100));

      var sjettedeler = new ArrayList<BigDecimal>();

      sjettedeler.add(BigDecimal.valueOf((1.0 / 6)).multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf((2.0 / 6)).multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf((3.0 / 6)).multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf((4.0 / 6)).multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf((5.0 / 6)).multiply(BigDecimal.valueOf(100)));

//      BigDecimal finalAndel = andel;
      BigDecimal finalAndel = andel;
      andel = sjettedeler.stream()
          .min(Comparator.comparing(a -> finalAndel.subtract(a).abs()))
          .orElseThrow(() -> new IllegalArgumentException("Empty collection"));


      andel = andel.setScale(1, RoundingMode.HALF_UP);

      // Utregnet andel skal ikke være større en 5/6

      if (andel.compareTo(BigDecimal.valueOf(83.3)) > 0) {
        andel = BigDecimal.valueOf(83.3);
      }
    }

    return new ResultatBeregning(andel.doubleValue());

    }

}



