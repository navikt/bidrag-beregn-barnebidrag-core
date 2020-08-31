
package no.nav.bidrag.beregn.barnebidrag.beregning;

import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlagPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;

public interface BarnebidragBeregning {

  ResultatBeregning beregn(
      BeregnBarnebidragGrunnlagPeriodisert beregnBarnebidragGrunnlagPeriodisert);

  static BarnebidragBeregning getInstance(){
    return new BarnebidragBeregningImpl();
  }

}
