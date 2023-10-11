package no.nav.bidrag.beregn.underholdskostnad.beregning

import no.nav.bidrag.beregn.underholdskostnad.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning


fun interface UnderholdskostnadBeregning {
    fun beregn(grunnlag: GrunnlagBeregning, barnetrygdIndikator: String): ResultatBeregning

    companion object {
        fun getInstance(): UnderholdskostnadBeregning {
            return UnderholdskostnadBeregningImpl()
        }
    }
}

