package no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning;

public class KostnadsberegnetBidragBeregningImpl implements KostnadsberegnetBidragBeregning {

  @Override
  public ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    var resultat = (grunnlagBeregningPeriodisert.getUnderholdskostnadBelop()
        .multiply(grunnlagBeregningPeriodisert.getBPsAndelUnderholdskostnadProsent())
        .divide(BigDecimal.valueOf(100), new MathContext(2, RoundingMode.HALF_UP))
        .subtract(grunnlagBeregningPeriodisert.getSamvaersfradragBelop()));

//    System.out.println("Resultat: " + resultat);
    resultat = resultat.setScale(-1, RoundingMode.HALF_UP);

    return new ResultatBeregning(resultat);

  }

}
