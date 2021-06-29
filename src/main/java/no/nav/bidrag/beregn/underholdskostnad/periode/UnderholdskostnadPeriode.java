package no.nav.bidrag.beregn.underholdskostnad.periode;

import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.underholdskostnad.beregning.UnderholdskostnadBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnetUnderholdskostnadResultat;

public interface UnderholdskostnadPeriode {
  BeregnetUnderholdskostnadResultat beregnPerioder(BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag);

  List<Avvik> validerInput(BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag);

  static UnderholdskostnadPeriode getInstance() {
    return new UnderholdskostnadPeriodeImpl(UnderholdskostnadBeregning.getInstance());
  }
}
