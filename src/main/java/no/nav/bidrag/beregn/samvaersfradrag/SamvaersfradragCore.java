package no.nav.bidrag.beregn.samvaersfradrag;

import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragGrunnlagCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragResultatCore;
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode;

public interface SamvaersfradragCore {

  BeregnSamvaersfradragResultatCore beregnSamvaersfradrag(
      BeregnSamvaersfradragGrunnlagCore beregnSamvaersfradragGrunnlagCore);

  static SamvaersfradragCore getInstance() {
    return new SamvaersfradragCoreImpl(SamvaersfradragPeriode.getInstance());
  }
}