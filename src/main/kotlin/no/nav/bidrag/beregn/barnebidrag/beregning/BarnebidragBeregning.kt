package no.nav.bidrag.beregn.barnebidrag.beregning

import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning


fun interface BarnebidragBeregning {
    fun beregn(grunnlag: GrunnlagBeregning): List<ResultatBeregning>

    companion object {
        fun getInstance(): BarnebidragBeregning {
            return BarnebidragBeregningImpl()
        }
    }
}
