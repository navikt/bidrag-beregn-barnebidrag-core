package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import java.util.List;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public interface NettoBarnetilsynBeregning {

  List<ResultatBeregning> beregn(
      BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert);

  Double beregnFradragsbelopPerBarn(
      BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert,
      int antallBarn, int antallBarnMedTilsynsutgift,
      double tilsynsbelop);

  static NettoBarnetilsynBeregning getInstance(){
    return new NettoBarnetilsynBeregningImpl();
  }

}