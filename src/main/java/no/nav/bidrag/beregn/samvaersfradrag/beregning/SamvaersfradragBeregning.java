package no.nav.bidrag.beregn.samvaersfradrag.beregning;

import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;

public interface SamvaersfradragBeregning {

  ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning);

  static SamvaersfradragBeregning getInstance(){
    return new SamvaersfradragBeregningImpl();
  }
}
