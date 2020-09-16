package no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt
import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.felles.enums.InntektType
import java.time.LocalDate

// Grunnlag periode
data class BeregnBPsAndelUnderholdskostnadGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val underholdskostnadPeriodeListe: List<UnderholdskostnadPeriodeCore>,
    val inntektBPPeriodeListe: List<InntektPeriodeCore>,
    val inntektBMPeriodeListe: List<InntektPeriodeCore>,
    val inntektBBPeriodeListe: List<InntektPeriodeCore>,
    var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class InntektPeriodeCore(
    val inntektPeriodeDatoFraTil: PeriodeCore,
    val inntektType: String,
    val inntektBelop: Double
)

data class UnderholdskostnadPeriodeCore(
    val underholdskostnadDatoFraTil: PeriodeCore,
    val underholdskostnadBelop: Double
)

data class ResultatGrunnlagCore(
    val soknadsbarnPersonId: Int,
    val underholdskostnadBelop: Double,
    val inntektBP: List<InntektCore>,
    val inntektBM: List<InntektCore>,
    val inntektBB: List<InntektCore>,
    val sjablonListe: List<SjablonCore>
)

data class InntektCore(
    val inntektType: String,
    val inntektBelop: Double
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
