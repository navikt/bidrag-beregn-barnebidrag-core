package no.nav.bidrag.beregn.forholdsmessigfordeling.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag
data class BeregnForholdsmessigFordelingGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val bidragsevnePeriodeListe: List<BidragsevnePeriodeCore>,
    val beregnetBidragPeriodeListe: List<BeregnetBidragSakPeriodeCore>
)

data class BidragsevnePeriodeCore(
    val periode: PeriodeCore,
    val belop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class BeregnetBidragSakPeriodeCore(
    val saksnr: Int,
    val periode: PeriodeCore,
    val grunnlagPerBarnListe: List<GrunnlagPerBarnCore>
)

// Resultat
data class BeregnForholdsmessigFordelingResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val periode: PeriodeCore,
    val resultatBeregningListe: List<ResultatBeregningCore>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisertCore
)

data class ResultatBeregningCore(
    val saksnr: Int,
    val resultatPerBarnListe: List<ResultatPerBarnCore>
)

data class ResultatPerBarnCore(
    val barnPersonId: Int,
    val belop: BigDecimal,
    val kode: String
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisertCore(
    val bidragsevne: BidragsevneCore,
    val beregnetBidragSakListe: List<BeregnetBidragSakCore>
)

data class BidragsevneCore(
    val belop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class BeregnetBidragSakCore(
    val saksnr: Int,
    val grunnlagPerBarnListe: List<GrunnlagPerBarnCore>
)

data class GrunnlagPerBarnCore(
    val barnPersonId: Int,
    val bidragBelop: BigDecimal
)
