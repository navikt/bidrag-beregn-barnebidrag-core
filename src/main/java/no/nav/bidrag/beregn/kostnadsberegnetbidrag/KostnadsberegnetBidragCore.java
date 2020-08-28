package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnKostnadsberegnetBidragGrunnlagCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnKostnadsberegnetBidragResultatCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode;

public interface KostnadsberegnetBidragCore {

  BeregnKostnadsberegnetBidragResultatCore beregnKostnadsberegnetBidrag (
      BeregnKostnadsberegnetBidragGrunnlagCore beregnKostnadsberegnetBidragGrunnlagCore);

  static KostnadsberegnetBidragCore getInstance() {
    return new KostnadsberegnetBidragCoreImpl(KostnadsberegnetBidragPeriode.getInstance());
  }
}
