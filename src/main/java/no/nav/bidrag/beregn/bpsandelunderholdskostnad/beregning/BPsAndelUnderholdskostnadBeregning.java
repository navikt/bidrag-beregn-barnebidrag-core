package no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning;

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;

public interface BPsAndelUnderholdskostnadBeregning {

  ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning, Boolean beregnMedNyeRegler);

  static BPsAndelUnderholdskostnadBeregning getInstance(){
    return new BPsAndelUnderholdskostnadBeregningImpl();
  }
}
