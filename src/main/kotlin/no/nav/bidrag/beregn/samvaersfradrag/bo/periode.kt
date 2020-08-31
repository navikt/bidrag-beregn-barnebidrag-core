package no.nav.bidrag.beregn.samvaersfradrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag


data class SamvaersklassePeriode(
    val samvaersklasseDatoFraTil: Periode,
    val samvaersklasse: String) : PeriodisertGrunnlag {
  constructor(samvaersklassePeriode: SamvaersklassePeriode)
      : this(samvaersklassePeriode.samvaersklasseDatoFraTil.justerDatoer(),
      samvaersklassePeriode.samvaersklasse)
  override fun getDatoFraTil(): Periode {
    return samvaersklasseDatoFraTil
  }
}