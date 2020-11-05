package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import java.math.BigDecimal;
import java.util.List;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public interface NettoBarnetilsynBeregning {

  List<ResultatBeregning> beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  BigDecimal beregnFradragsbelopPerBarn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert,
      int antallBarn, int antallBarnMedTilsynsutgift,
      BigDecimal tilsynsbelop);

  static NettoBarnetilsynBeregning getInstance(){
    return new NettoBarnetilsynBeregningImpl();
  }

}