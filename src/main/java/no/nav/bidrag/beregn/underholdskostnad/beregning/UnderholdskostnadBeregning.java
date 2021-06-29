package no.nav.bidrag.beregn.underholdskostnad.beregning;

import no.nav.bidrag.beregn.underholdskostnad.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;

public interface UnderholdskostnadBeregning {

  ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning, String barnetrygdIndikator);

  static UnderholdskostnadBeregning getInstance(){
    return new UnderholdskostnadBeregningImpl();
  }
}
