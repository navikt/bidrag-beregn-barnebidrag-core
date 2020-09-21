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
    val barnetilleggBPPeriodeListe: List<BarnetilleggPeriode>,
    val barnetilleggBMPeriodeListe: List<BarnetilleggPeriode>,
    val barnetilleggForsvaretPeriodeListe: List<BarnetilleggForsvaretPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnBarnebidragResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregningListe: List<ResultatBeregning>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisert
)

data class ResultatBeregning(
    val soknadsbarnPersonId: Int,
    val resultatBarnebidragBelop: Double,
    val resultatkode: ResultatKode
)


// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val bidragsevneBelop: Double,
    val grunnlagPerBarnListe: List<GrunnlagBeregningPerBarn>,
    val sjablonListe: List<Sjablon>
)
data class GrunnlagBeregningPerBarn(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnad: BPsAndelUnderholdskostnad,
    val kostnadsberegnetBidrag: Double,
    val samvaersfradrag: Double,
    val deltBosted: Boolean,
    val barnetilleggBP: Barnetillegg,
    val barnetilleggBM: Barnetillegg,
    val barnetilleggForsvaret: Boolean
)

data class BPsAndelUnderholdskostnad(
    val bPsAndelUnderholdskostnadProsent: Double,
    val bPsAndelUnderholdskostnadBelop: Double
)

data class Barnetillegg(
    val soknadsbarnPersonId: Int,
    val barnetilleggBelop: Double,
    val barnetilleggSkattProsent: Double
)
