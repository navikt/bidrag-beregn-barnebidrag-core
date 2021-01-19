package no.nav.bidrag.beregn.forholdsmessigfordeling.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnForholdsmessigFordelingGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val bidragsevnePeriodeListe: List<BidragsevnePeriodeCore>,
    val beregnetBidragPeriodeListe: List<BeregnetBidragSakPeriodeCore>
)

data class BidragsevnePeriodeCore(
    val bidragsevneDatoFraTil: PeriodeCore,
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class BeregnetBidragSakPeriodeCore(
    val saksnr: Int,
    val periodeDatoFraTil: PeriodeCore,
    val barnPersonId: Int,
    val bidragBelop: BigDecimal,
    val resultatkode: ResultatKode
)

// Resultatperiode
data class BeregnForholdsmessigFordelingResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregningListe: List<ResultatBeregningCore>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisertCore
)

data class ResultatBeregningCore(
    val saksnr: Int,
    val barnPersonId: Int,
    val resultatBarnebidragBelop: BigDecimal,
    val resultatkode: String
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisertCore(
    val bidragsevne: BidragsevneCore,
    val beregnetBidragSakListe: List<BeregnetBidragSakCore>,
)

data class BidragsevneCore(
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class BeregnetBidragSakCore(
    val saksnr: Int,
    val barnPersonId: Int,
    val bidragBelop: BigDecimal
)