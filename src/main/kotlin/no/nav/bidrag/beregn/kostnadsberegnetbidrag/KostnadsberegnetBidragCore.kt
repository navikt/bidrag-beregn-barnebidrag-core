package no.nav.bidrag.beregn.kostnadsberegnetbidrag

import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnKostnadsberegnetBidragGrunnlagCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnetKostnadsberegnetBidragResultatCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode

fun interface KostnadsberegnetBidragCore {
    fun beregnKostnadsberegnetBidrag(grunnlag: BeregnKostnadsberegnetBidragGrunnlagCore): BeregnetKostnadsberegnetBidragResultatCore

    companion object {
        fun getInstance(): KostnadsberegnetBidragCore = KostnadsberegnetBidragCoreImpl(KostnadsberegnetBidragPeriode.getInstance())
    }
}
