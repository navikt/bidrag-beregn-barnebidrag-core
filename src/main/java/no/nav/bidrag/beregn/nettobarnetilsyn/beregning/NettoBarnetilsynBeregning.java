package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregningListe;

public interface NettoBarnetilsynBeregning {

  ResultatBeregningListe beregn(
      BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert);

  Double beregnFradragsbelopPerBarn(
      BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert,
      int antallBarn,
      double tilsynsbelop);

  static NettoBarnetilsynBeregning getInstance(){
    return new NettoBarnetilsynBeregningImpl();
  }

}