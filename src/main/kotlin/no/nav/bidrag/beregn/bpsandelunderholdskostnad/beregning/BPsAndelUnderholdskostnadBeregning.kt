package no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning

fun interface BPsAndelUnderholdskostnadBeregning {
    fun beregn(grunnlag: GrunnlagBeregning, beregnMedNyeRegler: Boolean): ResultatBeregning

    companion object {
        fun getInstance(): BPsAndelUnderholdskostnadBeregning {
            return BPsAndelUnderholdskostnadBeregningImpl()
        }
    }
}
