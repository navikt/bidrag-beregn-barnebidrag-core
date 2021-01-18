package no.nav.bidrag.beregn.forholdsmessigfordeling.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnForholdsmessigFordelingGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val bidragsevnePeriodeListe: List<BidragsevnePeriode>,
    val beregnetBidragPeriodeListe: List<BeregnetBidragSakPeriode>
)

data class BidragsevnePeriode(
    val bidragsevneDatoFraTil: Periode,
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal) : PeriodisertGrunnlag {
    constructor(bidragsevnePeriode: BidragsevnePeriode)
        : this(bidragsevnePeriode.bidragsevneDatoFraTil.justerDatoer(),
        bidragsevnePeriode.bidragsevneBelop,
        bidragsevnePeriode.tjuefemProsentInntekt)
    override fun getDatoFraTil(): Periode {
        return bidragsevneDatoFraTil
    }
}

// Resultatperiode
data class BeregnForholdsmessigFordelingResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregningListe: List<ResultatBeregning>,
    val resultatGrunnlagListe: List<GrunnlagBeregningPeriodisert>
)

data class ResultatBeregning(
    val saksnr: Int,
    val barnPersonId: Int,
    val resultatBarnebidragBelop: BigDecimal,
    val resultatkode: ResultatKode
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val bidragsevne: Bidragsevne,
    val grunnlagPerSakListe: List<GrunnlagBeregningPerSak>
)

data class Bidragsevne(
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class GrunnlagBeregningPerSak(
    val saksnr: Int,
    val barnPersonId: Int,
    val bidragBelop: BigDecimal
)