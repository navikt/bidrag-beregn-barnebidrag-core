package no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import java.time.LocalDate

// Grunnlag periode
data class BeregnBPsAndelUnderholdskostnadGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val inntekterPeriodeListe: List<InntekterPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Grunnlag beregning
data class BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(
    val inntekter: Inntekter,
    val sjablonListe: List<Sjablon>)

data class Inntekter(
    val inntektBP: Double,
    val inntektBM: Double,
    val inntektBB: Double)


// Resultatperiode
data class BeregnBPsAndelUnderholdskostnadResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlag: BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert
)

data class ResultatBeregning(
    val resultatAndelProsent: Double,
    val resultatAndelBelop: Double
)
