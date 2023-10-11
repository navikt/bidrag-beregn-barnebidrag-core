package no.nav.bidrag.beregn.nettobarnetilsyn.beregning

import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning

fun interface NettoBarnetilsynBeregning {
    fun beregn(grunnlag: GrunnlagBeregning): List<ResultatBeregning>

    companion object {
        fun getInstance(): NettoBarnetilsynBeregning {
            return NettoBarnetilsynBeregningImpl()
        }
    }
}
