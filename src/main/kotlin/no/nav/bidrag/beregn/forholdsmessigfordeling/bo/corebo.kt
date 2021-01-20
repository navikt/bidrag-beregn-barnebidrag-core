package no.nav.bidrag.beregn.forholdsmessigfordeling.bo

import no.nav.bidrag.beregn.felles.bo.Periode
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

// Resultatperiode
data class BeregnForholdsmessigFordelingResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregningListe: List<ResultatBeregning>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisert
)

data class ResultatBeregning(
    val saksnr: Int,
    val resultatPerBarnListe: List<ResultatPerBarn>
)

data class ResultatPerBarn(
    val barnPersonId: Int,
    val resultatBarnebidragBelop: BigDecimal,
    val resultatkode: ResultatKode
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val bidragsevne: Bidragsevne,
    val beregnetBidragSakListe: List<BeregnetBidragSak>
)

data class Bidragsevne(
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class BeregnetBidragSak(
    val saksnr: Int,
    val grunnlagPerBarnListe: List<GrunnlagPerBarn>
)

data class GrunnlagPerBarn(
    val barnPersonId: Int,
    val bidragBelop: BigDecimal
)