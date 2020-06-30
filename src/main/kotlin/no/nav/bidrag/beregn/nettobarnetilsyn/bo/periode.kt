package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.time.LocalDate

data class NettoBarnetilsynPeriode(
    val nettoBarnetilsynDatoFraTil: Periode,
    val nettoBarnetilsynSoknadsbarnFodselsdato: LocalDate,
    val nettoBarnetilsynBelop: Double) : PeriodisertGrunnlag {
  constructor(nettoBarnetilsynPeriode: NettoBarnetilsynPeriode)
      : this(nettoBarnetilsynPeriode.nettoBarnetilsynDatoFraTil.justerDatoer(),
      nettoBarnetilsynPeriode.nettoBarnetilsynSoknadsbarnFodselsdato,
      nettoBarnetilsynPeriode.nettoBarnetilsynBelop)
  override fun getDatoFraTil(): Periode {
    return nettoBarnetilsynDatoFraTil
  }
}

