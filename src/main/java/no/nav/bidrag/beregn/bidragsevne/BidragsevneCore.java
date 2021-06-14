package no.nav.bidrag.beregn.bidragsevne;

import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneGrunnlagCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnetBidragsevneResultatCore;
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode;

public interface BidragsevneCore {

  BeregnetBidragsevneResultatCore beregnBidragsevne(
      BeregnBidragsevneGrunnlagCore beregnBidragsevneGrunnlagCore);

  static BidragsevneCore getInstance() { return new BidragsevneCoreImpl(
      BidragsevnePeriode.getInstance());
  }
}

