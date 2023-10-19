package no.nav.bidrag.beregn.nettobarnetilsyn

import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynGrunnlagCore
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnetNettoBarnetilsynResultatCore
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode

fun interface NettoBarnetilsynCore {
    fun beregnNettoBarnetilsyn(grunnlag: BeregnNettoBarnetilsynGrunnlagCore): BeregnetNettoBarnetilsynResultatCore

    companion object {
        fun getInstance(): NettoBarnetilsynCore = NettoBarnetilsynCoreImpl(NettoBarnetilsynPeriode.getInstance())
    }
}
