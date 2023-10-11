package no.nav.bidrag.beregn.bidragsevne.beregning

import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning

fun interface BidragsevneBeregning {
    fun beregn(grunnlag: GrunnlagBeregning): ResultatBeregning

    companion object {
        fun getInstance(): BidragsevneBeregning {
            return BidragsevneBeregningImpl()
        }
    }
}
