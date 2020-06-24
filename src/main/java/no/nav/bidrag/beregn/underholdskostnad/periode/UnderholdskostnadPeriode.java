package no.nav.bidrag.beregn.underholdskostnad.periode;

import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;

public interface UnderholdskostnadPeriode {



  Integer beregnSoknadbarnAlder(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag);


}
