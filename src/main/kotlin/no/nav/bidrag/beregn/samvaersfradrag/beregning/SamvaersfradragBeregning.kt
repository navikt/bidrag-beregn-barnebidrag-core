package no.nav.bidrag.beregn.samvaersfradrag.beregning

import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning

fun interface SamvaersfradragBeregning {
    fun beregn(grunnlag: GrunnlagBeregning): ResultatBeregning

    companion object {
        fun getInstance(): SamvaersfradragBeregning {
            return SamvaersfradragBeregningImpl()
        }
    }
}

