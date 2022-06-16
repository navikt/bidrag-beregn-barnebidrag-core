package no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning;

public class KostnadsberegnetBidragBeregningImpl implements KostnadsberegnetBidragBeregning {

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning) {

    var resultat = grunnlagBeregning.getUnderholdskostnad().getBelop()
        .multiply(grunnlagBeregning.getBPsAndelUnderholdskostnad().getAndelProsent())
        .divide(BigDecimal.valueOf(1), -1, RoundingMode.HALF_UP)
        .subtract(grunnlagBeregning.getSamvaersfradrag().getBelop());

    return new ResultatBeregning(resultat);
  }
}
