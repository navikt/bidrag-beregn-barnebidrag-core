package no.nav.bidrag.beregn.underholdskostnad.periode;

import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.felles.bidragsevne.beregning.Bidragsevneberegning;
import no.nav.bidrag.beregn.felles.bidragsevne.bo.BeregnBidragsevneGrunnlagAlt;
import no.nav.bidrag.beregn.felles.bidragsevne.bo.BeregnBidragsevneResultat;
import no.nav.bidrag.beregn.felles.bidragsevne.periode.BidragsevnePeriode;
import no.nav.bidrag.beregn.felles.bidragsevne.periode.BidragsevnePeriodeImpl;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.underholdskostnad.beregning.Underholdskostnadberegning;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadResultat;

public interface UnderholdskostnadPeriode {
  BeregnUnderholdskostnadResultat beregnPerioder(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag);

  List<Avvik> validerInput(BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag);

  static UnderholdskostnadPeriode getInstance() {
    return new UnderholdskostnadPeriodeImpl(Underholdskostnadberegning.getInstance());
  }

  Integer beregnSoknadbarnAlder(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag,
      LocalDate beregnDatoFra);

}
