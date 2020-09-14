package no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag

data class InntektPeriode(
    val inntektDatoFraTil: Periode,
    val inntekt: Inntekt) : PeriodisertGrunnlag {
  constructor(inntektPeriode: InntektPeriode)
      : this(inntektPeriode.inntektDatoFraTil.justerDatoer(),
      inntektPeriode.inntekt)
  override fun getDatoFraTil(): Periode {
    return inntektDatoFraTil
  }
}

data class UnderholdskostnadPeriode(
    val underholdskostnadDatoFraTil: Periode,
    val underholdskostnadBelop: Double) : PeriodisertGrunnlag {
  constructor(underholdskostnadPeriode: UnderholdskostnadPeriode)
      : this(underholdskostnadPeriode.underholdskostnadDatoFraTil.justerDatoer(),
      underholdskostnadPeriode.underholdskostnadBelop)
  override fun getDatoFraTil(): Periode {
    return underholdskostnadDatoFraTil
  }
}