package no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning;

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;

public interface BPsAndelUnderholdskostnadBeregning {

  ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  ResultatBeregning beregnMedGamleRegler(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  static BPsAndelUnderholdskostnadBeregning getInstance(){
    return new BPsAndelUnderholdskostnadBeregningImpl();
  }

}