package no.nav.bidrag.beregn.bidragsevne;

import no.nav.bidrag.beregn.bidragsevne.BidragsevneCoreImpl;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneGrunnlagAltCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneResultatCore;
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode;

public interface BidragsevneCore {

  BeregnBidragsevneResultatCore beregnBidragsevne(
      BeregnBidragsevneGrunnlagAltCore beregnBidragsevneGrunnlagAltCore);

  static BidragsevneCore getInstance() { return new BidragsevneCoreImpl(
      BidragsevnePeriode.getInstance());
  }
}

