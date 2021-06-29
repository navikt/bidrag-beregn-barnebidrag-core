package no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode;

import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning.BPsAndelUnderholdskostnadBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnetBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.felles.bo.Avvik;

public interface BPsAndelUnderholdskostnadPeriode {
  BeregnetBPsAndelUnderholdskostnadResultat beregnPerioder(BeregnBPsAndelUnderholdskostnadGrunnlag beregnBPsAndelUnderholdskostnadGrunnlag);

  List<Avvik> validerInput(BeregnBPsAndelUnderholdskostnadGrunnlag beregnBPsAndelUnderholdskostnadGrunnlag);

  static BPsAndelUnderholdskostnadPeriode getInstance() {
    return new BPsAndelUnderholdskostnadPeriodeImpl(BPsAndelUnderholdskostnadBeregning.getInstance());
  }
}
