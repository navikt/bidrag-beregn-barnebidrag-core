package no.nav.bidrag.beregn.samvaersfradrag.beregning;

import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;

public interface SamvaersfradragBeregning {

  ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  static SamvaersfradragBeregning getInstance(){
    return new SamvaersfradragBeregningImpl();
  }

}


