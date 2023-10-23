package no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag
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

data class UnderholdskostnadPeriodeCore(
    val referanse: String,
    val periode: PeriodeCore,
    val belop: BigDecimal
)

data class InntektPeriodeCore(
    val referanse: String,
    val periode: PeriodeCore,
    val type: String,
    val belop: BigDecimal,
    val deltFordel: Boolean,
    val skatteklasse2: Boolean
)

// Resultat
data class BeregnetBPsAndelUnderholdskostnadResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val sjablonListe: List<SjablonResultatGrunnlagCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val soknadsbarnPersonId: Int,
    val periode: PeriodeCore,
    val resultat: ResultatBeregningCore,
    val grunnlagReferanseListe: List<String>
)

data class ResultatBeregningCore(
    val andelProsent: BigDecimal,
    val andelBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)
