package no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnBPsAndelUnderholdskostnadGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val underholdskostnadPeriodeListe: List<UnderholdskostnadPeriode>,
    val inntektBPPeriodeListe: List<InntektPeriode>,
    val inntektBMPeriodeListe: List<InntektPeriode>,
    val inntektBBPeriodeListe: List<InntektPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>,
)

// Resultat periode
data class BeregnetBPsAndelUnderholdskostnadResultat(
    val resultatPeriodeListe: List<ResultatPeriode>,
)

data class ResultatPeriode(
    val soknadsbarnPersonId: Int,
    val periode: Periode,
    val resultat: ResultatBeregning,
    val grunnlag: GrunnlagBeregning,
)

data class ResultatBeregning(
    val andelProsent: BigDecimal,
    val andelBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean,
    val sjablonListe: List<SjablonPeriodeNavnVerdi>,
)

// Grunnlag beregning
data class GrunnlagBeregning(
    val underholdskostnad: Underholdskostnad,
    val inntektBPListe: List<Inntekt>,
    val inntektBMListe: List<Inntekt>,
    val inntektBBListe: List<Inntekt>,
    val sjablonListe: List<SjablonPeriode>,
)

data class Underholdskostnad(
    val referanse: String,
    val belop: BigDecimal,
)

data class Inntekt(
    val referanse: String,
    val type: String,
    val belop: BigDecimal,
    val deltFordel: Boolean,
    val skatteklasse2: Boolean,
)

// Hjelpeklasser
data class BeregnBPsAndelUnderholdskostnadListeGrunnlag(
    val periodeResultatListe: MutableList<ResultatPeriode> = mutableListOf(),
    var justertUnderholdskostnadPeriodeListe: List<UnderholdskostnadPeriode> = listOf(),
    var justertInntektBPPeriodeListe: List<InntektPeriode> = listOf(),
    var justertInntektBMPeriodeListe: List<InntektPeriode> = listOf(),
    var justertInntektBBPeriodeListe: List<InntektPeriode> = listOf(),
    var justertSjablonPeriodeListe: List<SjablonPeriode> = listOf(),
    var bruddPeriodeListe: MutableList<Periode> = mutableListOf(),
)
