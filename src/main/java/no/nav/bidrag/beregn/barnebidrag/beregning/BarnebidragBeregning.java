
package no.nav.bidrag.beregn.barnebidrag.beregning;

import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;

public interface BarnebidragBeregning {

  ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  static BarnebidragBeregning getInstance(){
    return new BarnebidragBeregningImpl();
  }

}
