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
    val bidragsevne: Bidragsevne,
    val grunnlagPerBarnListe: List<GrunnlagBeregningPerBarn>,
    val barnetilleggForsvaret: Boolean,
    val sjablonListe: List<Sjablon>
)

data class Bidragsevne(
    val bidragsevneBelop: Double,
    val tjuefemProsentInntekt: Double
)

data class GrunnlagBeregningPerBarn(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnad: BPsAndelUnderholdskostnad,
    val samvaersfradrag: Double,
    val deltBosted: Boolean,
    val barnetilleggBP: Barnetillegg,
    val barnetilleggBM: Barnetillegg
)

data class BPsAndelUnderholdskostnad(
    val bPsAndelUnderholdskostnadProsent: Double,
    val bPsAndelUnderholdskostnadBelop: Double
)

data class Barnetillegg(
    val barnetilleggBelop: Double,
    val barnetilleggSkattProsent: Double
)
