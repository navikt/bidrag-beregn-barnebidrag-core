package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.math.BigDecimal
import java.time.LocalDate

data class FaktiskUtgiftPeriode(
    val faktiskUtgiftSoknadsbarnPersonId: Int,
    val faktiskUtgiftDatoFraTil: Periode,
    val faktiskUtgiftSoknadsbarnFodselsdato: LocalDate,
    val faktiskUtgiftBelop: BigDecimal) : PeriodisertGrunnlag {

  constructor(faktiskUtgiftPeriode: FaktiskUtgiftPeriode)
      : this(faktiskUtgiftPeriode.faktiskUtgiftSoknadsbarnPersonId,
      faktiskUtgiftPeriode.faktiskUtgiftDatoFraTil.justerDatoer(),
      faktiskUtgiftPeriode.faktiskUtgiftSoknadsbarnFodselsdato,
      faktiskUtgiftPeriode.faktiskUtgiftBelop)

  override fun getPeriode(): Periode {
    return faktiskUtgiftDatoFraTil
  }
  fun getDatoFraTil(): Periode {
    return faktiskUtgiftDatoFraTil
  }
}

