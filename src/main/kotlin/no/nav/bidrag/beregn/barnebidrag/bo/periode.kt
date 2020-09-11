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

// Del av grunnlaget som angis per barn i søknaden
  data class GrunnlagPerBarnPeriode(
    val grunnlagPerBarnPeriodeDatoFraTil: Periode,
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriode>,
    val kostnadsberegnetBidragPeriodeListe: List<KostnadsberegnetBidragPeriode>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriode>,
    val deltBostedPeriodeListe: List<DeltBostedPeriode>) : PeriodisertGrunnlag {
  constructor(grunnlagPerBarnPeriode: GrunnlagPerBarnPeriode)
      : this(grunnlagPerBarnPeriode.grunnlagPerBarnPeriodeDatoFraTil.justerDatoer(),
      grunnlagPerBarnPeriode.bPsAndelUnderholdskostnadPeriodeListe,
      grunnlagPerBarnPeriode.kostnadsberegnetBidragPeriodeListe,
      grunnlagPerBarnPeriode.samvaersfradragPeriodeListe,
      grunnlagPerBarnPeriode.deltBostedPeriodeListe)
  override fun getDatoFraTil(): Periode {
    return grunnlagPerBarnPeriodeDatoFraTil
  }
}

data class BPsAndelUnderholdskostnadPeriode(
    val bPsAndelUnderholdskostnadDatoFraTil: Periode,
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnadProsent: Double,
    val bPsAndelUnderholdskostnadBelop: Double) : PeriodisertGrunnlag {
  constructor(bPsAndelUnderholdskostnadPeriode: BPsAndelUnderholdskostnadPeriode)
      : this(bPsAndelUnderholdskostnadPeriode.bPsAndelUnderholdskostnadDatoFraTil.justerDatoer(),
      bPsAndelUnderholdskostnadPeriode.soknadsbarnPersonId,
      bPsAndelUnderholdskostnadPeriode.bPsAndelUnderholdskostnadProsent,
      bPsAndelUnderholdskostnadPeriode.bPsAndelUnderholdskostnadBelop)
  override fun getDatoFraTil(): Periode {
    return bPsAndelUnderholdskostnadDatoFraTil
  }
}

data class KostnadsberegnetBidragPeriode(
    val kostnadsberegnetBidragDatoFraTil: Periode,
    val soknadsbarnPersonId: Int,
    val kostnadsberegnetBidragBelop: Double) : PeriodisertGrunnlag {
  constructor(kostnadsberegnetBidragPeriode: KostnadsberegnetBidragPeriode)
      : this(kostnadsberegnetBidragPeriode.kostnadsberegnetBidragDatoFraTil.justerDatoer(),
      kostnadsberegnetBidragPeriode.soknadsbarnPersonId,
      kostnadsberegnetBidragPeriode.kostnadsberegnetBidragBelop)
  override fun getDatoFraTil(): Periode {
    return kostnadsberegnetBidragDatoFraTil
  }
}

data class SamvaersfradragPeriode(
    val samvaersfradragDatoFraTil: Periode,
    val soknadsbarnPersonId: Int,
    val samvaersfradrag: Double?) : PeriodisertGrunnlag {
  constructor(samvaersfradragPeriode: SamvaersfradragPeriode)
      : this(samvaersfradragPeriode.samvaersfradragDatoFraTil.justerDatoer(),
      samvaersfradragPeriode.soknadsbarnPersonId,
      samvaersfradragPeriode.samvaersfradrag)
  override fun getDatoFraTil(): Periode {
    return samvaersfradragDatoFraTil
  }
}

data class DeltBostedPeriode(
    val deltBostedDatoFraTil: Periode,
    val soknadsbarnPersonId: Int,
    val deltBosted: Boolean) : PeriodisertGrunnlag {
  constructor(deltBostedPeriode: DeltBostedPeriode)
      : this(deltBostedPeriode.deltBostedDatoFraTil.justerDatoer(),
      deltBostedPeriode.soknadsbarnPersonId,
      deltBostedPeriode.deltBosted)
  override fun getDatoFraTil(): Periode {
    return deltBostedDatoFraTil
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
