package no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning;

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;

public interface BPsAndelUnderholdskostnadBeregning {

  ResultatBeregning beregn(
      BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);


  static BPsAndelUnderholdskostnadBeregning getInstance(){
    return new BPsAndelUnderholdskostnadBeregningImpl();
  }

}