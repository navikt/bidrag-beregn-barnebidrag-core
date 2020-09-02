package no.nav.bidrag.beregn.underholdskostnad.beregning;

import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;

public interface UnderholdskostnadBeregning {

  ResultatBeregning beregnUtenBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  ResultatBeregning beregnOrdinaerBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  ResultatBeregning beregnForhoyetBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  Double beregnBarnetilsynMedStonad(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  static UnderholdskostnadBeregning getInstance(){
    return new UnderholdskostnadBeregningImpl();
  }

}