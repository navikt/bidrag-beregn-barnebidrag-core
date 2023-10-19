package no.nav.bidrag.beregn.bpsandelunderholdskostnad

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadGrunnlagCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnetBPsAndelUnderholdskostnadResultatCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode

fun interface BPsAndelUnderholdskostnadCore {
    fun beregnBPsAndelUnderholdskostnad(grunnlag: BeregnBPsAndelUnderholdskostnadGrunnlagCore): BeregnetBPsAndelUnderholdskostnadResultatCore

    companion object {
        fun getInstance(): BPsAndelUnderholdskostnadCore = BPsAndelUnderholdskostnadCoreImpl(BPsAndelUnderholdskostnadPeriode.getInstance())
    }
}
