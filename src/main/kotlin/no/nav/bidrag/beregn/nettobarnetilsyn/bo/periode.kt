package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.time.LocalDate

data class FaktiskUtgiftPeriode(
    val faktiskUtgiftSoknadsbarnPersonId: Int,
    val faktiskUtgiftDatoFraTil: Periode,
    val faktiskUtgiftSoknadsbarnFodselsdato: LocalDate,
    val faktiskUtgiftBelop: Double) : PeriodisertGrunnlag {
  constructor(faktiskUtgiftPeriode: FaktiskUtgiftPeriode)
      : this(faktiskUtgiftPeriode.faktiskUtgiftSoknadsbarnPersonId,
      faktiskUtgiftPeriode.faktiskUtgiftDatoFraTil.justerDatoer(),
      faktiskUtgiftPeriode.faktiskUtgiftSoknadsbarnFodselsdato,
      faktiskUtgiftPeriode.faktiskUtgiftBelop)
  override fun getDatoFraTil(): Periode {
    return faktiskUtgiftDatoFraTil
  }
}

