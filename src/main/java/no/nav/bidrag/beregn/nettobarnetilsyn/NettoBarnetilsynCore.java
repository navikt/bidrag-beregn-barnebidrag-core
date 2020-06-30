package no.nav.bidrag.beregn.nettobarnetilsyn;

import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynGrunnlagCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynResultatCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode;

public interface NettoBarnetilsynCore {

  BeregnNettoBarnetilsynResultatCore beregnNettoBarnetilsyn (
      BeregnNettoBarnetilsynGrunnlagCore beregnNettoBarnetilsynGrunnlagCore);

  static NettoBarnetilsynCore getInstance() {
    return new NettoBarnetilsynCoreImpl(NettoBarnetilsynPeriode.getInstance());
  }
}
