package no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import java.time.LocalDate

// Grunnlag periode
data class BeregnKostnadsberegnetBidragGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val underholdskostnadPeriodeListe: List<UnderholdskostnadPeriode>,
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriode>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriode>?
)

// Resultatperiode
data class BeregnKostnadsberegnetBidragResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val soknadsbarnPersonId: Int,
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlag: GrunnlagBeregningPeriodisert
)

data class ResultatBeregning(
    val resultatkostnadsberegnetbidragBelop: Double
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val underholdskostnadBelop: Double,
    val bPsAndelUnderholdskostnadProsent: Double,
    val samvaersfradrag: Double?)