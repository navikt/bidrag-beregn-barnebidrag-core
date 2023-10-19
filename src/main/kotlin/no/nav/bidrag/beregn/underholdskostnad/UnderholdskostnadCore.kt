package no.nav.bidrag.beregn.underholdskostnad

import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadGrunnlagCore
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnetUnderholdskostnadResultatCore
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode

fun interface UnderholdskostnadCore {
    fun beregnUnderholdskostnad(grunnlag: BeregnUnderholdskostnadGrunnlagCore): BeregnetUnderholdskostnadResultatCore

    companion object {
        fun getInstance(): UnderholdskostnadCore = UnderholdskostnadCoreImpl(UnderholdskostnadPeriode.getInstance())
    }
}
