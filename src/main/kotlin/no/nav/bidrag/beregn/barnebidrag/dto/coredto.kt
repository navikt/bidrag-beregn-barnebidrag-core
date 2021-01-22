package no.nav.bidrag.beregn.barnebidrag.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnBarnebidragGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val bidragsevnePeriodeListe: List<BidragsevnePeriodeCore>,
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriodeCore>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriodeCore>,
    val deltBostedPeriodeListe: List<DeltBostedPeriodeCore>,
    val barnetilleggBPPeriodeListe: List<BarnetilleggPeriodeCore>,
    val barnetilleggBMPeriodeListe: List<BarnetilleggPeriodeCore>,
    val barnetilleggForsvaretPeriodeListe: List<BarnetilleggForsvaretPeriodeCore>,
    val andreLopendeBidragPeriodeListe: List<AndreLopendeBidragPeriodeCore>,
    val sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class BidragsevnePeriodeCore(
    val bidragsevneDatoFraTil: PeriodeCore,
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class BPsAndelUnderholdskostnadPeriodeCore(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnadDatoFraTil: PeriodeCore,
    val bPsAndelUnderholdskostnadProsent: BigDecimal,
    val bPsAndelUnderholdskostnadBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)

data class SamvaersfradragPeriodeCore(
    val soknadsbarnPersonId: Int,
    val samvaersfradragDatoFraTil: PeriodeCore,
    val samvaersfradragBelop: BigDecimal
)

data class DeltBostedPeriodeCore(
    val soknadsbarnPersonId: Int,
    val deltBostedDatoFraTil: PeriodeCore,
    val deltBostedIPeriode: Boolean
)

data class BarnetilleggPeriodeCore(
    val soknadsbarnPersonId: Int,
    val barnetilleggDatoFraTil: PeriodeCore,
    val barnetilleggBelop: BigDecimal,
    val barnetilleggSkattProsent: BigDecimal
)

data class BarnetilleggForsvaretPeriodeCore(
    val barnetilleggForsvaretDatoFraTil: PeriodeCore,
    val barnetilleggForsvaretIPeriode: Boolean
)

data class AndreLopendeBidragPeriodeCore(
    val periodeDatoFraTil: PeriodeCore,
    val barnPersonId: Int,
    val bidragBelop: BigDecimal,
    val samvaersfradragBelop: BigDecimal
)

// Resultatperiode
data class BeregnBarnebidragResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregningListe: List<ResultatBeregningCore>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisertCore
)

data class ResultatBeregningCore(
    val soknadsbarnPersonId: Int,
    val resultatBarnebidragBelop: BigDecimal,
    val resultatkode: String
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisertCore(
    val bidragsevne: BidragsevneCore,
    val grunnlagPerBarnListe: List<GrunnlagBeregningPerBarnCore>,
    val barnetilleggForsvaret: Boolean,
    val andreLopendeBidragListe: List<AndreLopendeBidragCore>,
    val sjablonListe: List<SjablonNavnVerdiCore>
)

data class BidragsevneCore(
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class GrunnlagBeregningPerBarnCore(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnad: BPsAndelUnderholdskostnadCore,
    val samvaersfradrag: BigDecimal,
    val deltBosted: Boolean,
    val barnetilleggBP: BarnetilleggCore,
    val barnetilleggBM: BarnetilleggCore
)

data class BPsAndelUnderholdskostnadCore(
    val bPsAndelUnderholdskostnadProsent: BigDecimal,
    val bPsAndelUnderholdskostnadBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)

data class BarnetilleggCore(
    val barnetilleggBelop: BigDecimal,
    val barnetilleggSkattProsent: BigDecimal
)

data class AndreLopendeBidragCore(
    val barnPersonId: Int,
    val bidragBelop: BigDecimal,
    val samvaersfradragBelop: BigDecimal
)
