package no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning

import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning
import java.math.BigDecimal
import java.math.RoundingMode

class KostnadsberegnetBidragBeregningImpl : KostnadsberegnetBidragBeregning {
    override fun beregn(grunnlag: GrunnlagBeregning): ResultatBeregning {
        val resultat = grunnlag.underholdskostnad.belop
            .multiply(grunnlag.bPsAndelUnderholdskostnad.andelProsent)
            .divide(BigDecimal.valueOf(1), -1, RoundingMode.HALF_UP)
            .subtract(grunnlag.samvaersfradrag.belop)

        return ResultatBeregning(resultat)
    }
}
