package no.nav.bidrag.beregn.bidragsevne;

import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneGrunnlagCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneResultatCore;
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode;

public interface BidragsevneCore {

  BeregnBidragsevneResultatCore beregnBidragsevne(
      BeregnBidragsevneGrunnlagCore beregnBidragsevneGrunnlagCore);

  static BidragsevneCore getInstance() { return new BidragsevneCoreImpl(
      BidragsevnePeriode.getInstance());
  }
}

