
package no.nav.bidrag.beregn.barnebidrag.beregning;

import java.math.BigDecimal;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;

public interface BarnebidragBeregning {

  List<ResultatBeregning> beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  List<ResultatBeregning> beregnVedBarnetilleggForsvaret(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

/*  BigDecimal finnTotalUnderholdskostnad(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);*/

  static BarnebidragBeregning getInstance(){
    return new BarnebidragBeregningImpl();
  }

}
