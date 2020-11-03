package no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import java.math.BigDecimal
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
    val underholdskostnadBelop: BigDecimal
)

data class BPsAndelUnderholdskostnadPeriodeCore(
    val bPsAndelUnderholdskostnadPeriodeDatoFraTil: PeriodeCore,
    val bPsAndelUnderholdskostnadProsent: BigDecimal
)

data class SamvaersfradragPeriodeCore(
    val samvaersfradragDatoPeriodeFraTil: PeriodeCore,
    val samvaersfradrag: BigDecimal
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
    val resultatKostnadsberegnetBidragBelop: BigDecimal
)

// Grunnlag beregning
data class ResultatGrunnlagCore(
    val underholdskostnadBelop: BigDecimal,
    val bPsAndelUnderholdskostnadProsent: BigDecimal,
    val samvaersfradragBelop: BigDecimal?)