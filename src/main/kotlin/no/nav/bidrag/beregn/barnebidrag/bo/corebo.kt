package no.nav.bidrag.beregn.barnebidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.time.LocalDate

// Grunnlag periode
data class BeregnBarnebidragGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val bidragsevnePeriodeListe: List<BidragsevnePeriode>,
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriode>,
    val kostnadsberegnetBidragPeriodeListe: List<KostnadsberegnetBidragPeriode>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriode>,
    val deltBostedPeriodeListe: List<DeltBostedPeriode>,
    val barnetilleggBPPeriodeListe: List<BarnetilleggBPPeriode>,
    val barnetilleggBMPeriodeListe: List<BarnetilleggBMPeriode>,
    val barnetilleggForsvaretPeriodeListe: List<BarnetilleggForsvaretPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val bidragsevneBelop: Double,
    val bPsAndelUnderholdskostnad: List<BPsAndelUnderholdskostnad>,
    val kostnadsberegnetBidragListe: List<KostnadsberegnetBidrag>,
    val samvaersfradrag: List<Samvaersfradrag>,
    val deltBosted: List<DeltBosted>,
    val barnetilleggBP: List<BarnetilleggBP>,
    val barnetilleggBM: List<BarnetilleggBM>,
    val barnetilleggForsvaret: List<BarnetilleggForsvaret>,
    val sjablonListe: List<Sjablon>
)

data class BPsAndelUnderholdskostnad(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnadProsent: Double,
    val bPsAndelUnderholdskostnadBelop: Double
)

data class KostnadsberegnetBidrag(
    val soknadsbarnPersonId: Int,
    val kostnadsberegnetBidragBelop: Double
)

data class Samvaersfradrag(
    val soknadsbarnPersonId: Int,
    val samvaersfradragBelop: Double
)

data class DeltBosted(
    val soknadsbarnPersonId: Int,
    val deltBosted: Boolean
)

data class BarnetilleggBP(
    val soknadsbarnPersonId: Int,
    val barnetilleggBPBelop: Double,
    val barnetilleggBPSkattProsent: Double
)

data class BarnetilleggBM(
    val soknadsbarnPersonId: Int,
    val barnetilleggBMBelop: Double,
    val barnetilleggBMSkattProsent: Double
)

data class BarnetilleggForsvaret(
    val soknadsbarnPersonId: Int,
    val barnetilleggForsvaretJaNei: Boolean,
    val antallBarn: Int
)

// Resultatperiode
data class BeregnBarnebidragResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: List<ResultatBeregning>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisert
)

data class ResultatBeregning(
    val soknadsbarnPersonId: Int,
    val resultatBarnebidragBelop: Double,
    val resultatkode: ResultatKode
)