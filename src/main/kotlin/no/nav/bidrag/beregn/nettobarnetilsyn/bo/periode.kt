package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.time.LocalDate

data class FaktiskUtgiftBarnetilsynPeriode(
    val faktiskUtgiftBarnetilsynDatoFraTil: Periode,
    val faktiskUtgiftBarnetilsynSoknadsbarnFodselsdato: LocalDate,
    val faktiskUtgiftBarnetilsynBelop: Double) : PeriodisertGrunnlag {
  constructor(faktiskUtgiftBarnetilsynPeriode: FaktiskUtgiftBarnetilsynPeriode)
      : this(faktiskUtgiftBarnetilsynPeriode.faktiskUtgiftBarnetilsynDatoFraTil.justerDatoer(),
      faktiskUtgiftBarnetilsynPeriode.faktiskUtgiftBarnetilsynSoknadsbarnFodselsdato,
      faktiskUtgiftBarnetilsynPeriode.faktiskUtgiftBarnetilsynBelop)
  override fun getDatoFraTil(): Periode {
    return faktiskUtgiftBarnetilsynDatoFraTil
  }
}

