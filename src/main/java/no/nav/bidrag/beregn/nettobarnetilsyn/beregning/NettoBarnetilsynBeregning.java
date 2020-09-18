package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import java.util.List;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public interface NettoBarnetilsynBeregning {

  List<ResultatBeregning> beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  Double beregnFradragsbelopPerBarn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert,
      int antallBarn, int antallBarnMedTilsynsutgift,
      double tilsynsbelop);

  static NettoBarnetilsynBeregning getInstance(){
    return new NettoBarnetilsynBeregningImpl();
  }

}