package no.nav.bidrag.beregn.barnebidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag

data class BidragsevnePeriode(
    val bidragsevneDatoFraTil: Periode,
    val bidragsevneBelop: Double,
    val tjuefemProsentInntekt: Double) : PeriodisertGrunnlag {
  constructor(bidragsevnePeriode: BidragsevnePeriode)
      : this(bidragsevnePeriode.bidragsevneDatoFraTil.justerDatoer(),
      bidragsevnePeriode.bidragsevneBelop,
      bidragsevnePeriode.tjuefemProsentInntekt)
  override fun getDatoFraTil(): Periode {
    return bidragsevneDatoFraTil
  }
}

data class BPsAndelUnderholdskostnadPeriode(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnadDatoFraTil: Periode,
    val bPsAndelUnderholdskostnadProsent: Double,
    val bPsAndelUnderholdskostnadBelop: Double,
    val barnetErSelvforsorget: Boolean) : PeriodisertGrunnlag {
  constructor(bPsAndelUnderholdskostnadPeriode: BPsAndelUnderholdskostnadPeriode)
      : this(
      bPsAndelUnderholdskostnadPeriode.soknadsbarnPersonId,
      bPsAndelUnderholdskostnadPeriode.bPsAndelUnderholdskostnadDatoFraTil.justerDatoer(),
      bPsAndelUnderholdskostnadPeriode.bPsAndelUnderholdskostnadProsent,
      bPsAndelUnderholdskostnadPeriode.bPsAndelUnderholdskostnadBelop,
      bPsAndelUnderholdskostnadPeriode.barnetErSelvforsorget,
  )
  override fun getDatoFraTil(): Periode {
    return bPsAndelUnderholdskostnadDatoFraTil
  }
}

data class SamvaersfradragPeriode(
    val soknadsbarnPersonId: Int,
    val samvaersfradragDatoFraTil: Periode,
    val samvaersfradragBelop: Double) : PeriodisertGrunnlag {
  constructor(samvaersfradragPeriode: SamvaersfradragPeriode)
      : this(samvaersfradragPeriode.soknadsbarnPersonId,
      samvaersfradragPeriode.samvaersfradragDatoFraTil.justerDatoer(),
      samvaersfradragPeriode.samvaersfradragBelop)
  override fun getDatoFraTil(): Periode {
    return samvaersfradragDatoFraTil
  }
}

data class DeltBostedPeriode(
    val soknadsbarnPersonId: Int,
    val deltBostedDatoFraTil: Periode,
    val deltBostedIPeriode: Boolean) : PeriodisertGrunnlag {
  constructor(deltBostedPeriode: DeltBostedPeriode)
      : this(deltBostedPeriode.soknadsbarnPersonId,
      deltBostedPeriode.deltBostedDatoFraTil.justerDatoer(),
      deltBostedPeriode.deltBostedIPeriode)
  override fun getDatoFraTil(): Periode {
    return deltBostedDatoFraTil
  }
}

data class BarnetilleggPeriode(
    val soknadsbarnPersonId: Int,
    val barnetilleggDatoFraTil: Periode,
    val barnetilleggBelop: Double,
    val barnetilleggSkattProsent: Double) : PeriodisertGrunnlag {
  constructor(barnetilleggPeriode: BarnetilleggPeriode)
      : this(barnetilleggPeriode.soknadsbarnPersonId, barnetilleggPeriode.barnetilleggDatoFraTil.justerDatoer(),
      barnetilleggPeriode.barnetilleggBelop, barnetilleggPeriode.barnetilleggSkattProsent)
  override fun getDatoFraTil(): Periode {
    return barnetilleggDatoFraTil
  }
}

data class BarnetilleggForsvaretPeriode(
    val barnetilleggForsvaretDatoFraTil: Periode,
    val barnetilleggForsvaretIPeriode: Boolean) : PeriodisertGrunnlag {
  constructor(barnetilleggForsvaretPeriode: BarnetilleggForsvaretPeriode)
      : this(barnetilleggForsvaretPeriode.barnetilleggForsvaretDatoFraTil.justerDatoer(),
      barnetilleggForsvaretPeriode.barnetilleggForsvaretIPeriode)
  override fun getDatoFraTil(): Periode {
    return barnetilleggForsvaretDatoFraTil
  }
}
