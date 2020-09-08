package no.nav.bidrag.beregn.barnebidrag.dto

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
//import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatGrunnlagCore
import java.time.LocalDate

// Grunnlag periode
data class BeregnBarnebidragGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val bidragsevnePeriodeListe: List<BidragsevnePeriodeCore>,
    val kostnadsberegnetBidragPeriodeListe: List<KostnadsberegnetBidragPeriodeCore>,
    val samvaerfradragPeriodeListe: List<SamvaersfradragPeriodeCore>,
    val barnetilleggBPPeriodeListe: List<BarnetilleggBPPeriodeCore>,
    val barnetilleggBMPeriodeListe: List<BarnetilleggBMPeriodeCore>,
    val barnetilleggForsvaretPeriodeListe: List<BarnetilleggForsvaretPeriodeCore>,
    val sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class BidragsevnePeriodeCore(
    val bidragsevneDatoFraTil: Periode,
    val bidragsevneBelop: Double,
    val tjuefemProsentInntekt: Double
)

data class KostnadsberegnetBidragPeriodeCore(
    val kostnadsberegnetBidragDatoFraTil: Periode,
    val kostnadsberegnetBidragBelop: Double
)

data class SamvaersfradragPeriodeCore(
    val samvaersfradragDatoFraTil: Periode,
    val samvaersfradrag: Double?
)

data class BarnetilleggBPPeriodeCore(
    val barnetilleggBPDatoFraTil: Periode,
    val barnetilleggBPBelop: Double,
    val barnetilleggBPSkattProsent: Double
)

data class BarnetilleggBMPeriodeCore(
    val barnetilleggBMDatoFraTil: Periode,
    val barnetilleggBMBelop: Double,
    val barnetilleggBMSkattProsent: Double
)

data class BarnetilleggForsvaretPeriodeCore(
    val barnetilleggForsvaretDatoFraTil: Periode,
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
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: GrunnlagBeregningPeriodisertCore
)

data class ResultatBeregningCore(
    val resultatBarnebidragBelop: Double
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisertCore(
    val bidragsevneBelop: Double,
    val kostnadsberegnetBidrag: Double,
    val samvaersfradrag: Double,
    val barnetilleggBP: BarnetilleggBPCore,
    val barnetilleggBM: BarnetilleggBMCore,
    val barnetilleggForsvaret: BarnetilleggForsvaretCore,
    val sjablonListe: List<Sjablon>)

data class BarnetilleggBPCore(
    val barnetilleggBPBelop: Double,
    val barnetilleggBPSkattProsent: Double
)

data class BarnetilleggBMCore(
    val barnetilleggBMBelop: Double,
    val barnetilleggBMSkattProsent: Double
)

data class BarnetilleggForsvaretCore(
    val barnetilleggForsvaretJaNei: Boolean,
    val antallBarn: Int
)