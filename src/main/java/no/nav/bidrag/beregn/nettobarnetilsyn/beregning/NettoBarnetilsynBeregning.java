package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public interface NettoBarnetilsynBeregning {

  ResultatBeregning beregn(
      BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert);

  static NettoBarnetilsynBeregning getInstance(){
    return new NettoBarnetilsynBeregningImpl();
  }

}