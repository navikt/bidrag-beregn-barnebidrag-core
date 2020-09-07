package no.nav.bidrag.beregn.barnebidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import java.time.LocalDate

// Grunnlag periode
data class BeregnBarnebidragGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val bidragsevnePeriodeListe: List<BidragsevnePeriode>,
    val kostnadsberegnetBidragPeriodeListe: List<KostnadsberegnetBidragPeriode>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriode>,
    val barnetilleggBPPeriodeListe: List<BarnetilleggBPPeriode>,
    val barnetilleggBMPeriodeListe: List<BarnetilleggBMPeriode>,
    val barnetilleggForsvaretPeriodeListe: List<BarnetilleggForsvaretPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnBarnebidragResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlag: GrunnlagBeregningPeriodisert
)

data class ResultatBeregning(
    val resultatBarnebidragBelop: Double
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val bidragsevneBelop: Double,
    val kostnadsberegnetBidragBelop: Double,
    val samvaersfradrag: Double,
    val barnetilleggBP: BarnetilleggBP,
    val barnetilleggBM: BarnetilleggBM,
    val barnetilleggForsvaret: BarnetilleggForsvaret,
    val sjablonListe: List<Sjablon>)

data class BarnetilleggBP(
    val barnetilleggBPBelop: Double,
    val barnetilleggBPSkattProsent: Double
)

data class BarnetilleggBM(
    val barnetilleggBMBelop: Double,
    val barnetilleggBMSkattProsent: Double
)

data class BarnetilleggForsvaret(
    val barnetilleggForsvaretJaNei: Boolean,
    val antallBarn: Int
)