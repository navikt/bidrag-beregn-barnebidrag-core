package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public interface NettoBarnetilsynBeregning {

  List<ResultatBeregning> beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  BigDecimal beregnFradragsbelopPerBarn(
      int antallBarnMedTilsynsutgift, BigDecimal tilsynsbelop, Map<String, BigDecimal> sjablonNavnVerdiMap);

  static NettoBarnetilsynBeregning getInstance(){
    return new NettoBarnetilsynBeregningImpl();
  }
}
