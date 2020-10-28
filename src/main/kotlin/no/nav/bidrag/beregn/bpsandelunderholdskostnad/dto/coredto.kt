package no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.math.BigDecimal
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
    val inntektBelop: BigDecimal
)

data class UnderholdskostnadPeriodeCore(
    val underholdskostnadDatoFraTil: PeriodeCore,
    val underholdskostnadBelop: BigDecimal
)

data class InntektCore(
    val inntektType: String,
    val inntektBelop: BigDecimal
)

// Resultat
data class BeregnBPsAndelUnderholdskostnadResultatCore(
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
    val resultatAndelProsent: BigDecimal,
    val resultatAndelBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)

data class ResultatGrunnlagCore(
    val underholdskostnadBelop: BigDecimal,
    val inntektBPListe: List<InntektCore>,
    val inntektBMListe: List<InntektCore>,
    val inntektBBListe: List<InntektCore>,
    val sjablonListe: List<SjablonCore>,
)
