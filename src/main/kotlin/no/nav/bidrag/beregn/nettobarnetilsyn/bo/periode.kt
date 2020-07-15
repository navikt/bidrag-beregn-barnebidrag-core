package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.time.LocalDate

data class FaktiskUtgiftPeriode(
    val faktiskUtgiftDatoFraTil: Periode,
    val faktiskUtgiftSoknadsbarnFodselsdato: LocalDate,
    val faktiskUtgiftSoknadsbarnPersonId: Int,
    val faktiskUtgiftBelop: Double) : PeriodisertGrunnlag {
  constructor(faktiskUtgiftPeriode: FaktiskUtgiftPeriode)
      : this(faktiskUtgiftPeriode.faktiskUtgiftDatoFraTil.justerDatoer(),
      faktiskUtgiftPeriode.faktiskUtgiftSoknadsbarnFodselsdato,
      faktiskUtgiftPeriode.faktiskUtgiftSoknadsbarnPersonId,
      faktiskUtgiftPeriode.faktiskUtgiftBelop)
  override fun getDatoFraTil(): Periode {
    return faktiskUtgiftDatoFraTil
  }
}

