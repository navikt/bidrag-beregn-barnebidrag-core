package no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning

import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning

fun interface KostnadsberegnetBidragBeregning {
    fun beregn(grunnlag: GrunnlagBeregning): ResultatBeregning

    companion object {
        fun getInstance(): KostnadsberegnetBidragBeregning {
            return KostnadsberegnetBidragBeregningImpl()
        }
    }
}
