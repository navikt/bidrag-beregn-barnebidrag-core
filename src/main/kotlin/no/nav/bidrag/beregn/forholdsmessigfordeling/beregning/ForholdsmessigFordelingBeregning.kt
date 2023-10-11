package no.nav.bidrag.beregn.forholdsmessigfordeling.beregning

import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning

fun interface ForholdsmessigFordelingBeregning {
    fun beregn(grunnlag: GrunnlagBeregningPeriodisert): List<ResultatBeregning>

    companion object {
        fun getInstance(): ForholdsmessigFordelingBeregning {
            return ForholdsmessigFordelingBeregningImpl()
        }
    }
}
