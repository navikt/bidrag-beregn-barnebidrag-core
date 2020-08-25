package no.nav.bidrag.beregn.samvaersfradrag.beregning;

import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlagPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;

public interface SamvaersfradragBeregning {

  ResultatBeregning beregn(
      BeregnSamvaersfradragGrunnlagPeriodisert beregnSamvaersfradragGrunnlagPeriodisert);

  static SamvaersfradragBeregning getInstance(){
    return new SamvaersfradragBeregningImpl();
  }

}


