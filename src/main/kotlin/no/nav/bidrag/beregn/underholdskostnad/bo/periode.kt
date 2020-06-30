package no.nav.bidrag.beregn.underholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag

data class BarnetilsynMedStonadPeriode(
    val barnetilsynMedStonadDatoFraTil: Periode,
    val barnetilsynMedStonadTilsynType: String,
    val barnetilsynStonadType: String) : PeriodisertGrunnlag {
  constructor(barnetilsynMedStonadPeriode: BarnetilsynMedStonadPeriode)
      : this(barnetilsynMedStonadPeriode.barnetilsynMedStonadDatoFraTil.justerDatoer(),
      barnetilsynMedStonadPeriode.barnetilsynMedStonadTilsynType,
      barnetilsynMedStonadPeriode.barnetilsynStonadType)
  override fun getDatoFraTil(): Periode {
    return barnetilsynMedStonadDatoFraTil
  }
}

data class NettoBarnetilsynPeriode(
    val nettoBarnetilsynDatoFraTil: Periode,
    val nettoBarnetilsynBelop: Double) : PeriodisertGrunnlag {
  constructor(nettoBarnetilsynPeriode: NettoBarnetilsynPeriode)
      : this(nettoBarnetilsynPeriode.nettoBarnetilsynDatoFraTil.justerDatoer(),
      nettoBarnetilsynPeriode.nettoBarnetilsynBelop)
  override fun getDatoFraTil(): Periode {
    return nettoBarnetilsynDatoFraTil
  }
}

data class ForpleiningUtgiftPeriode(
    val forpleiningUtgiftDatoFraTil: Periode,
    val forpleiningUtgiftBelop: Double) : PeriodisertGrunnlag {
  constructor(forpleiningUtgiftPeriode: ForpleiningUtgiftPeriode)
      : this(forpleiningUtgiftPeriode.forpleiningUtgiftDatoFraTil.justerDatoer(),
      forpleiningUtgiftPeriode.forpleiningUtgiftBelop)
  override fun getDatoFraTil(): Periode {
    return forpleiningUtgiftDatoFraTil
  }
}
