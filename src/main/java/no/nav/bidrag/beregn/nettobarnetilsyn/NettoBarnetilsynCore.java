package no.nav.bidrag.beregn.nettobarnetilsyn;

import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynGrunnlagCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnetNettoBarnetilsynResultatCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode;

public interface NettoBarnetilsynCore {

  BeregnetNettoBarnetilsynResultatCore beregnNettoBarnetilsyn(BeregnNettoBarnetilsynGrunnlagCore beregnNettoBarnetilsynGrunnlagCore);

  static NettoBarnetilsynCore getInstance() {
    return new NettoBarnetilsynCoreImpl(NettoBarnetilsynPeriode.getInstance());
  }
}
