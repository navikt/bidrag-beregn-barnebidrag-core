package no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import java.time.LocalDate

// Grunnlag periode
data class BeregnKostnadsberegnetBidragGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val underholdskostnadPeriodeListe: List<UnderholdskostnadPeriodeCore>,
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriodeCore>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriodeCore>?
)

data class UnderholdskostnadPeriodeCore(
    val underholdskostnadPeriodeDatoFraTil: PeriodeCore,
    val underholdskostnadBelop: Double
)

data class BPsAndelUnderholdskostnadPeriodeCore(
    val bPsAndelUnderholdskostnadPeriodeDatoFraTil: PeriodeCore,
    val bPsAndelUnderholdskostnadProsent: Double
)

data class SamvaersfradragPeriodeCore(
    val samvaersfradragDatoPeriodeFraTil: PeriodeCore,
    val samvaersfradrag: Double
)

// Resultatperiode
data class BeregnKostnadsberegnetBidragResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val soknadsbarnPersonId: Int,
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatKostnadsberegnetBidragBelop: Double
)

// Grunnlag beregning
data class ResultatGrunnlagCore(
    val underholdskostnadBelop: Double,
    val bPsAndelUnderholdskostnadProsent: Double,
    val samvaersfradragBelop: Double?)