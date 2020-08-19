package no.nav.bidrag.beregn.underholdskostnad.periode;

import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.underholdskostnad.beregning.UnderholdskostnadBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadResultat;

public interface UnderholdskostnadPeriode {
  BeregnUnderholdskostnadResultat beregnPerioder(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag);

  List<Avvik> validerInput(BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag);

  static UnderholdskostnadPeriode getInstance() {
    return new UnderholdskostnadPeriodeImpl(UnderholdskostnadBeregning.getInstance());
  }

  Integer beregnSoknadbarnAlder(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag,
      LocalDate beregnDatoFra);

}
