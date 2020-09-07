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

data class KostnadsberegnetBidragPeriode(
    val kostnadsberegnetBidragDatoFraTil: Periode,
    val kostnadsberegnetBidragBelop: Double) : PeriodisertGrunnlag {
  constructor(kostnadsberegnetBidragPeriode: KostnadsberegnetBidragPeriode)
      : this(kostnadsberegnetBidragPeriode.kostnadsberegnetBidragDatoFraTil.justerDatoer(),
      kostnadsberegnetBidragPeriode.kostnadsberegnetBidragBelop)
  override fun getDatoFraTil(): Periode {
    return kostnadsberegnetBidragDatoFraTil
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

data class BarnetilleggBPPeriode(
    val barnetilleggBPDatoFraTil: Periode,
    val barnetilleggBPBelop: Double,
    val barnetilleggBPSkattProsent: Double) : PeriodisertGrunnlag {
  constructor(barnetilleggBPPeriode: BarnetilleggBPPeriode)
      : this(barnetilleggBPPeriode.barnetilleggBPDatoFraTil.justerDatoer(),
      barnetilleggBPPeriode.barnetilleggBPBelop, barnetilleggBPPeriode.barnetilleggBPSkattProsent)
  override fun getDatoFraTil(): Periode {
    return barnetilleggBPDatoFraTil
  }
}

data class BarnetilleggBMPeriode(
    val barnetilleggBMDatoFraTil: Periode,
    val barnetilleggBMBelop: Double,
    val barnetilleggBMSkattProsent: Double) : PeriodisertGrunnlag {
  constructor(barnetilleggBMPeriode: BarnetilleggBMPeriode)
      : this(barnetilleggBMPeriode.barnetilleggBMDatoFraTil.justerDatoer(),
      barnetilleggBMPeriode.barnetilleggBMBelop, barnetilleggBMPeriode.barnetilleggBMSkattProsent)
  override fun getDatoFraTil(): Periode {
    return barnetilleggBMDatoFraTil
  }
}

data class BarnetilleggForsvaretPeriode(
    val barnetilleggForsvaretDatoFraTil: Periode,
    val barnetilleggForsvaretAntallBarn: Int,
    val barnetilleggForsvaretIPeriode: Boolean) : PeriodisertGrunnlag {
  constructor(barnetilleggForsvaretPeriode: BarnetilleggForsvaretPeriode)
      : this(barnetilleggForsvaretPeriode.barnetilleggForsvaretDatoFraTil.justerDatoer(),
      barnetilleggForsvaretPeriode.barnetilleggForsvaretAntallBarn,
      barnetilleggForsvaretPeriode.barnetilleggForsvaretIPeriode)
  override fun getDatoFraTil(): Periode {
    return barnetilleggForsvaretDatoFraTil
  }
}
