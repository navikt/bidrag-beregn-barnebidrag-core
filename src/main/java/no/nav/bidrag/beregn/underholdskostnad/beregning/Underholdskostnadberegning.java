package no.nav.bidrag.beregn.underholdskostnad.beregning;

import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;

public interface Underholdskostnadberegning {

  ResultatBeregning beregn(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  Double beregnBarnetilsynMedStonad(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  Double beregnBarnetilsynFaktiskUtgift(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  Double beregnNettoBarnetilsyn(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  static Underholdskostnadberegning getInstance(){
    return new UnderholdskostnadberegningImpl();
  }


}

