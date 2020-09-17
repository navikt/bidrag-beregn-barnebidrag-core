package no.nav.bidrag.beregn.barnebidrag.dto

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
//import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatGrunnlagCore
import java.time.LocalDate

// Grunnlag periode
data class BeregnBarnebidragGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val bidragsevnePeriodeListe: List<BidragsevnePeriodeCore>,
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriodeCore>,
    val kostnadsberegnetBidragPeriodeListe: List<KostnadsberegnetBidragPeriodeCore>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriodeCore>,
    val deltBostedPeriodeListe: List<DeltBostedPeriodeCore>,
    val barnetilleggBPPeriodeListe: List<BarnetilleggBPPeriodeCore>,
    val barnetilleggBMPeriodeListe: List<BarnetilleggBMPeriodeCore>,
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
    val bPsAndelUnderholdskostnadBelop: Double
)

data class KostnadsberegnetBidragPeriodeCore(
    val soknadsbarnPersonId: Int,
    val kostnadsberegnetBidragDatoFraTil: PeriodeCore,
    val kostnadsberegnetBidragBelop: Double
)

data class SamvaersfradragPeriodeCore(
    val soknadsbarnPersonId: Int,
    val samvaersfradragDatoFraTil: PeriodeCore,
    val samvaersfradrag: Double?
)

data class DeltBostedPeriodeCore(
    val soknadsbarnPersonId: Int,
    val deltBostedDatoFraTil: PeriodeCore,
    val deltBostedPeriodeCore: Boolean
)

data class BarnetilleggBPPeriodeCore(
    val soknadsbarnPersonId: Int,
    val barnetilleggBPDatoFraTil: PeriodeCore,
    val barnetilleggBPBelop: Double,
    val barnetilleggBPSkattProsent: Double
)

data class BarnetilleggBMPeriodeCore(
    val soknadsbarnPersonId: Int,
    val barnetilleggBMDatoFraTil: PeriodeCore,
    val barnetilleggBMBelop: Double,
    val barnetilleggBMSkattProsent: Double
)

data class BarnetilleggForsvaretPeriodeCore(
    val soknadsbarnPersonId: Int,
    val barnetilleggForsvaretDatoFraTil: PeriodeCore,
    val barnetilleggForsvaretAntallBarn: Int,
    val barnetilleggForsvaretIPeriode: Boolean
)



// Resultatperiode
data class BeregnBarnebidragResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: List<ResultatBeregningCore>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisertCore
)

data class ResultatBeregningCore(
    val soknadsbarnPersonId: Int,
    val resultatBarnebidragBelop: Double,
    val resultatkode: String
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisertCore(
    val bidragsevneBelop: Double,
    val GrunnlagPerBarnListe: List<GrunnlagBeregningPeriodisertPerBarnCore>,
    val sjablonListe: List<SjablonCore>
)

data class GrunnlagBeregningPeriodisertPerBarnCore(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnad: BPsAndelUnderholdskostnadCore,
    val kostnadsberegnetBidrag: Double,
    val samvaersfradrag: Double,
    val deltBosted: Boolean,
    val barnetilleggBP: Double,
    val barnetilleggBM: Double,
    val barnetilleggForsvaret: Double
)

data class BPsAndelUnderholdskostnadCore(
    val bPsAndelUnderholdskostnadProsent: Double,
    val bPsAndelUnderholdskostnadBelop: Double
)
