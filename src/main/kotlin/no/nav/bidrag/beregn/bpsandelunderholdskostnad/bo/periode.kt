package no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag

data class InntekterPeriode(
    val inntekterDatoFraTil: Periode,
    val inntektBP: Double,
    val inntektBM: Double,
    val inntektBB: Double) : PeriodisertGrunnlag {
  constructor(inntekterPeriode: InntekterPeriode)
      : this(inntekterPeriode.inntekterDatoFraTil.justerDatoer(),
      inntekterPeriode.inntektBP,
      inntekterPeriode.inntektBM,
      inntekterPeriode.inntektBB)
  override fun getDatoFraTil(): Periode {
    return inntekterDatoFraTil
  }
}