package no.nav.bidrag.beregn.underholdskostnad;

import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnetUnderholdskostnadResultatCore;
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode;

public interface UnderholdskostnadCore {

  BeregnetUnderholdskostnadResultatCore beregnUnderholdskostnad(BeregnUnderholdskostnadGrunnlagCore beregnUnderholdskostnadGrunnlagCore);

  static UnderholdskostnadCore getInstance() {
    return new UnderholdskostnadCoreImpl(UnderholdskostnadPeriode.Companion.getInstance());
  }
}
