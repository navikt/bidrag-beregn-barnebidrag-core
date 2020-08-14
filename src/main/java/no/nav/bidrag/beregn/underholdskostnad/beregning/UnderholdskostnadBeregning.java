package no.nav.bidrag.beregn.underholdskostnad.beregning;

import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;

public interface UnderholdskostnadBeregning {

  ResultatBeregning beregn(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  Double beregnBarnetilsynMedStonad(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  static UnderholdskostnadBeregning getInstance(){
    return new UnderholdskostnadBeregningImpl();
  }

}