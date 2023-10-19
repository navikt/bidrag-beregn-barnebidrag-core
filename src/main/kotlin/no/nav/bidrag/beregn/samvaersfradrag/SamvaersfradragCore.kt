package no.nav.bidrag.beregn.samvaersfradrag

import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragGrunnlagCore
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnetSamvaersfradragResultatCore
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode

fun interface SamvaersfradragCore {
    fun beregnSamvaersfradrag(grunnlag: BeregnSamvaersfradragGrunnlagCore): BeregnetSamvaersfradragResultatCore

    companion object {
        fun getInstance(): SamvaersfradragCore = SamvaersfradragCoreImpl(SamvaersfradragPeriode.getInstance())
    }
}
