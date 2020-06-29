package no.nav.bidrag.beregn.underholdskostnad;

import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadResultatCore;
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode;

public interface UnderholdskostnadCore {

  BeregnUnderholdskostnadResultatCore beregnUnderholdskostnad (
      BeregnUnderholdskostnadGrunnlagCore beregnUnderholdskostnadGrunnlagCore);

  static UnderholdskostnadCore getInstance() {
    return new UnderholdskostnadCoreImpl(UnderholdskostnadPeriode.getInstance());
  }
}
