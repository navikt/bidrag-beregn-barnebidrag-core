package no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag

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

data class BPsAndelUnderholdskostnadPeriode(
    val bPsAndelUnderholdskostnadDatoFraTil: Periode,
    val bPsAndelUnderholdskostnadProsent: Double) : PeriodisertGrunnlag {
  constructor(bPsAndelunderholdskostnadPeriode: BPsAndelUnderholdskostnadPeriode)
      : this(bPsAndelunderholdskostnadPeriode.bPsAndelUnderholdskostnadDatoFraTil.justerDatoer(),
      bPsAndelunderholdskostnadPeriode.bPsAndelUnderholdskostnadProsent)
  override fun getDatoFraTil(): Periode {
    return bPsAndelUnderholdskostnadDatoFraTil
  }
}

data class SamvaersfradragPeriode(
    val samvaersfradragDatoFraTil: Periode,
    val samvaersfradrag: Double?) : PeriodisertGrunnlag {
  constructor(samvaersfradragPeriode: SamvaersfradragPeriode)
      : this(samvaersfradragPeriode.samvaersfradragDatoFraTil.justerDatoer(),
      samvaersfradragPeriode.samvaersfradrag)
  override fun getDatoFraTil(): Periode {
    return samvaersfradragDatoFraTil
  }
}

