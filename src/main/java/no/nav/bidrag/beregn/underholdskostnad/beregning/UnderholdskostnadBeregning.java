package no.nav.bidrag.beregn.underholdskostnad.beregning;

import java.math.BigDecimal;
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;

public interface UnderholdskostnadBeregning {

  ResultatBeregning beregnUtenBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  ResultatBeregning beregnMedOrdinaerBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  ResultatBeregning beregnMedForhoyetBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  BigDecimal beregnBarnetilsynMedStonad(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert);

  static UnderholdskostnadBeregning getInstance(){
    return new UnderholdskostnadBeregningImpl();
  }

}