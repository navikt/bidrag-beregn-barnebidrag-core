package no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning;

public class KostnadsberegnetBidragBeregningImpl implements KostnadsberegnetBidragBeregning {

  @Override
  public ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    double belopFradrag = 0.0d;

    belopFradrag = grunnlagBeregningPeriodisert.getSamvaersfradragBelop();

    BigDecimal resultat = (BigDecimal.valueOf(
        grunnlagBeregningPeriodisert.getUnderholdskostnadBelop())
        .multiply(BigDecimal.valueOf(
            grunnlagBeregningPeriodisert.getBPsAndelUnderholdskostnadProsent()))
        .divide(BigDecimal.valueOf(100))
        .subtract(BigDecimal.valueOf(belopFradrag)));

    System.out.println("Resultat: " + resultat);
    resultat = resultat.setScale(-1, RoundingMode.HALF_UP);

    return new ResultatBeregning(resultat.doubleValue());

  }

}
