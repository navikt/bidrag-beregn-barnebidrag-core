package no.nav.bidrag.beregn.barnebidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.math.BigDecimal

data class BidragsevnePeriode(
    val bidragsevneDatoFraTil: Periode,
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal) : PeriodisertGrunnlag {

  constructor(bidragsevnePeriode: BidragsevnePeriode)
      : this(bidragsevnePeriode.bidragsevneDatoFraTil.justerDatoer(),
      bidragsevnePeriode.bidragsevneBelop,
      bidragsevnePeriode.tjuefemProsentInntekt)

  override fun getPeriode(): Periode {
    return bidragsevneDatoFraTil
  }
  fun getDatoFraTil(): Periode {
    return bidragsevneDatoFraTil
  }
}

data class BPsAndelUnderholdskostnadPeriode(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnadDatoFraTil: Periode,
    val bPsAndelUnderholdskostnadProsent: BigDecimal,
    val bPsAndelUnderholdskostnadBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean) : PeriodisertGrunnlag {

  constructor(bPsAndelUnderholdskostnadPeriode: BPsAndelUnderholdskostnadPeriode)
      : this(
      bPsAndelUnderholdskostnadPeriode.soknadsbarnPersonId,
      bPsAndelUnderholdskostnadPeriode.bPsAndelUnderholdskostnadDatoFraTil.justerDatoer(),
      bPsAndelUnderholdskostnadPeriode.bPsAndelUnderholdskostnadProsent,
      bPsAndelUnderholdskostnadPeriode.bPsAndelUnderholdskostnadBelop,
      bPsAndelUnderholdskostnadPeriode.barnetErSelvforsorget,
  )

  override fun getPeriode(): Periode {
    return bPsAndelUnderholdskostnadDatoFraTil
  }
  fun getDatoFraTil(): Periode {
    return bPsAndelUnderholdskostnadDatoFraTil
  }
}

data class SamvaersfradragPeriode(
    val soknadsbarnPersonId: Int,
    val samvaersfradragDatoFraTil: Periode,
    val samvaersfradragBelop: BigDecimal) : PeriodisertGrunnlag {

  constructor(samvaersfradragPeriode: SamvaersfradragPeriode)
      : this(samvaersfradragPeriode.soknadsbarnPersonId,
      samvaersfradragPeriode.samvaersfradragDatoFraTil.justerDatoer(),
      samvaersfradragPeriode.samvaersfradragBelop)

  override fun getPeriode(): Periode {
    return samvaersfradragDatoFraTil
  }
  fun getDatoFraTil(): Periode {
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

  override fun getPeriode(): Periode {
    return deltBostedDatoFraTil
  }
  fun getDatoFraTil(): Periode {
    return deltBostedDatoFraTil
  }
}

data class BarnetilleggPeriode(
    val soknadsbarnPersonId: Int,
    val barnetilleggDatoFraTil: Periode,
    val barnetilleggBelop: BigDecimal,
    val barnetilleggSkattProsent: BigDecimal) : PeriodisertGrunnlag {

  constructor(barnetilleggPeriode: BarnetilleggPeriode)
      : this(barnetilleggPeriode.soknadsbarnPersonId, barnetilleggPeriode.barnetilleggDatoFraTil.justerDatoer(),
      barnetilleggPeriode.barnetilleggBelop, barnetilleggPeriode.barnetilleggSkattProsent)

  override fun getPeriode(): Periode {
    return barnetilleggDatoFraTil
  }
  fun getDatoFraTil(): Periode {
    return barnetilleggDatoFraTil
  }
}

data class BarnetilleggForsvaretPeriode(
    val barnetilleggForsvaretDatoFraTil: Periode,
    val barnetilleggForsvaretIPeriode: Boolean) : PeriodisertGrunnlag {

  constructor(barnetilleggForsvaretPeriode: BarnetilleggForsvaretPeriode)
      : this(barnetilleggForsvaretPeriode.barnetilleggForsvaretDatoFraTil.justerDatoer(),
      barnetilleggForsvaretPeriode.barnetilleggForsvaretIPeriode)

  override fun getPeriode(): Periode {
    return barnetilleggForsvaretDatoFraTil
  }
  fun getDatoFraTil(): Periode {
    return barnetilleggForsvaretDatoFraTil
  }
}

data class AndreLopendeBidragPeriode(
  val andreLopendeBidragDatoFraTil: Periode,
  val barnPersonId: Int,
  val bidragBelop: BigDecimal,
  val samvaersfradragBelop: BigDecimal) : PeriodisertGrunnlag {

  constructor(andreLopendeBidragPeriode: AndreLopendeBidragPeriode)
      : this(andreLopendeBidragPeriode.andreLopendeBidragDatoFraTil.justerDatoer(),
    andreLopendeBidragPeriode.barnPersonId,
    andreLopendeBidragPeriode.bidragBelop,
    andreLopendeBidragPeriode.samvaersfradragBelop)

  override fun getPeriode(): Periode {
    return andreLopendeBidragDatoFraTil
  }
  fun getDatoFraTil(): Periode {
    return andreLopendeBidragDatoFraTil
  }
}


