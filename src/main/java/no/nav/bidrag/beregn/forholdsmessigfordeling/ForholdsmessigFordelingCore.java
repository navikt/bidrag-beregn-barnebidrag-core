package no.nav.bidrag.beregn.forholdsmessigfordeling;

import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnForholdsmessigFordelingGrunnlagCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnForholdsmessigFordelingResultatCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.periode.ForholdsmessigFordelingPeriode;


public interface ForholdsmessigFordelingCore {

  BeregnForholdsmessigFordelingResultatCore beregnForholdsmessigFordeling(
      BeregnForholdsmessigFordelingGrunnlagCore beregnForholdsmessigFordelingGrunnlagCore);

  static ForholdsmessigFordelingCore getInstance() {
    return new ForholdsmessigFordelingCoreImpl(ForholdsmessigFordelingPeriode.Companion.getInstance());
  }
}

