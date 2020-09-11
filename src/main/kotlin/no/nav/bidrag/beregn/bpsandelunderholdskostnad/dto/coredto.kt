package no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.time.LocalDate

// Grunnlag periode
data class BeregnBPsAndelUnderholdskostnadGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val inntekterPeriodeListe: List<InntekterPeriodeCore>,
    var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class InntekterPeriodeCore(
    val inntekterPeriodeDatoFraTil: PeriodeCore,
    val inntektBP: Double,
    val inntektBM: Double,
    val inntektBB: Double
)

data class ResultatGrunnlagCore(
    val inntektBP: Double,
    val inntektBM: Double,
    val inntektBB: Double,
    val sjablonListe: List<SjablonCore>
)

// Resultat
data class BeregnBPsAndelUnderholdskostnadResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatAndelProsent: Double,
    val resultatAndelBelop: Double
)
