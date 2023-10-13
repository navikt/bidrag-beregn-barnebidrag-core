package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnetBPsAndelUnderholdskostnadResultatCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode;

public interface BPsAndelUnderholdskostnadCore {

  BeregnetBPsAndelUnderholdskostnadResultatCore beregnBPsAndelUnderholdskostnad(
      BeregnBPsAndelUnderholdskostnadGrunnlagCore beregnBPsAndelUnderholdskostnadGrunnlagCore);

  static BPsAndelUnderholdskostnadCore getInstance() {
    return new BPsAndelUnderholdskostnadCoreImpl(BPsAndelUnderholdskostnadPeriode.Companion.getInstance());
  }
}