package no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.enums.InntektType
import java.math.BigDecimal

data class InntektPeriode(
    val inntektDatoFraTil: Periode,
    val inntektType: InntektType,
    val inntektBelop: BigDecimal,
    val deltFordel: Boolean,
    val skatteklasse2: Boolean) : PeriodisertGrunnlag {

  constructor(inntektPeriode: InntektPeriode) : this(
      inntektPeriode.inntektDatoFraTil.justerDatoer(),
      inntektPeriode.inntektType,
      inntektPeriode.inntektBelop,
      inntektPeriode.deltFordel,
      inntektPeriode.skatteklasse2)

  override fun getDatoFraTil(): Periode {
    return inntektDatoFraTil
  }
}

data class UnderholdskostnadPeriode(
    val underholdskostnadDatoFraTil: Periode,
    val underholdskostnadBelop: BigDecimal) : PeriodisertGrunnlag {

  constructor(underholdskostnadPeriode: UnderholdskostnadPeriode) : this(
      underholdskostnadPeriode.underholdskostnadDatoFraTil.justerDatoer(),
      underholdskostnadPeriode.underholdskostnadBelop)

  override fun getDatoFraTil(): Periode {
    return underholdskostnadDatoFraTil
  }
}