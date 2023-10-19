package no.nav.bidrag.beregn.barnebidrag

import no.nav.bidrag.beregn.barnebidrag.dto.BeregnBarnebidragGrunnlagCore
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnetBarnebidragResultatCore
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode

fun interface BarnebidragCore {
    fun beregnBarnebidrag(grunnlag: BeregnBarnebidragGrunnlagCore): BeregnetBarnebidragResultatCore

    companion object {
        fun getInstance(): BarnebidragCore = BarnebidragCoreImpl(BarnebidragPeriode.getInstance())
    }
}
