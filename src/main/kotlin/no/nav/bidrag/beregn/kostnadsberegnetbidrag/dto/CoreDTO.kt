package no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag
data class BeregnKostnadsberegnetBidragGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val underholdskostnadPeriodeListe: List<UnderholdskostnadPeriodeCore>,
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriodeCore>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriodeCore>
)

data class UnderholdskostnadPeriodeCore(
    val referanse: String,
    val periode: PeriodeCore,
    val belop: BigDecimal
)

data class BPsAndelUnderholdskostnadPeriodeCore(
    val referanse: String,
    val periode: PeriodeCore,
    val andelProsent: BigDecimal
)

data class SamvaersfradragPeriodeCore(
    val referanse: String,
    val periode: PeriodeCore,
    val belop: BigDecimal
)

// Resultat
data class BeregnetKostnadsberegnetBidragResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val soknadsbarnPersonId: Int,
    val periode: PeriodeCore,
    val resultat: ResultatBeregningCore,
    val grunnlagReferanseListe: List<String>
)

data class ResultatBeregningCore(
    val belop: BigDecimal
)
