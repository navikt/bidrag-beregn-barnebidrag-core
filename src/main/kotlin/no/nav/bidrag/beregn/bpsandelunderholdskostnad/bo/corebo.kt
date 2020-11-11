package no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.enums.InntektType
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnBPsAndelUnderholdskostnadGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val underholdskostnadListe: List<UnderholdskostnadPeriode>,
    val inntektBPPeriodeListe: List<InntektPeriode>,
    val inntektBMPeriodeListe: List<InntektPeriode>,
    val inntektBBPeriodeListe: List<InntektPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnBPsAndelUnderholdskostnadResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val soknadsbarnPersonId: Int,
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlagBeregning: GrunnlagBeregningPeriodisert
)

data class ResultatBeregning(
    val resultatAndelProsent: BigDecimal,
    val resultatAndelBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean,
    val sjablonListe: List<SjablonNavnVerdi>
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val underholdskostnadBelop: BigDecimal,
    val inntektBPListe: List<Inntekt>,
    val inntektBMListe: List<Inntekt>,
    val inntektBBListe: List<Inntekt>,
    val sjablonListe: List<Sjablon>
)

data class Inntekt(
    val inntektType: InntektType,
    val inntektBelop: BigDecimal
)
