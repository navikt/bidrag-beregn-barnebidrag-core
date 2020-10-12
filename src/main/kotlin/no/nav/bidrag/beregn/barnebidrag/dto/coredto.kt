package no.nav.bidrag.beregn.barnebidrag.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
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
    val sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class BidragsevnePeriodeCore(
    val bidragsevneDatoFraTil: PeriodeCore,
    val bidragsevneBelop: Double,
    val tjuefemProsentInntekt: Double
)

data class BPsAndelUnderholdskostnadPeriodeCore(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnadDatoFraTil: PeriodeCore,
    val bPsAndelUnderholdskostnadProsent: Double,
    val bPsAndelUnderholdskostnadBelop: Double
)

data class SamvaersfradragPeriodeCore(
    val soknadsbarnPersonId: Int,
    val samvaersfradragDatoFraTil: PeriodeCore,
    val samvaersfradragBelop: Double
)

data class DeltBostedPeriodeCore(
    val soknadsbarnPersonId: Int,
    val deltBostedDatoFraTil: PeriodeCore,
    val deltBostedIPeriode: Boolean
)

data class BarnetilleggPeriodeCore(
    val soknadsbarnPersonId: Int,
    val barnetilleggDatoFraTil: PeriodeCore,
    val barnetilleggBelop: Double,
    val barnetilleggSkattProsent: Double
)

data class BarnetilleggForsvaretPeriodeCore(
    val barnetilleggForsvaretDatoFraTil: PeriodeCore,
    val barnetilleggForsvaretIPeriode: Boolean
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
    val resultatBarnebidragBelop: Double,
    val resultatkode: String
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisertCore(
    val bidragsevne: BidragsevneCore,
    val grunnlagPerBarnListe: List<GrunnlagBeregningPerBarnCore>,
    val barnetilleggForsvaret: Boolean,
    val sjablonListe: List<SjablonCore>
)

data class BidragsevneCore(
    val bidragsevneBelop: Double,
    val tjuefemProsentInntekt: Double
)

data class GrunnlagBeregningPerBarnCore(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnad: BPsAndelUnderholdskostnadCore,
    val samvaersfradrag: Double,
    val deltBosted: Boolean,
    val barnetilleggBP: BarnetilleggCore,
    val barnetilleggBM: BarnetilleggCore,
)

data class BPsAndelUnderholdskostnadCore(
    val bPsAndelUnderholdskostnadProsent: Double,
    val bPsAndelUnderholdskostnadBelop: Double
)

data class BarnetilleggCore(
    val barnetilleggBelop: Double,
    val barnetilleggSkattProsent: Double
)