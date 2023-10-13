package no.nav.bidrag.beregn.barnebidrag;

import no.nav.bidrag.beregn.barnebidrag.dto.BeregnBarnebidragGrunnlagCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnetBarnebidragResultatCore;
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode;


public interface BarnebidragCore {

  BeregnetBarnebidragResultatCore beregnBarnebidrag(BeregnBarnebidragGrunnlagCore beregnBarnebidragGrunnlagCore);

  static BarnebidragCore getInstance() {
    return new BarnebidragCoreImpl(BarnebidragPeriode.Companion.getInstance());
  }
}

