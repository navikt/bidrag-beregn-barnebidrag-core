package no.nav.bidrag.beregn.barnebidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.math.BigDecimal

data class BidragsevnePeriode(
  val referanse: String,
  val bidragsevnePeriode: Periode,
  val belop: BigDecimal,
  val tjuefemProsentInntekt: BigDecimal
) : PeriodisertGrunnlag {

  constructor(bidragsevnePeriode: BidragsevnePeriode) : this(
    bidragsevnePeriode.referanse,
    bidragsevnePeriode.bidragsevnePeriode.justerDatoer(),
    bidragsevnePeriode.belop,
    bidragsevnePeriode.tjuefemProsentInntekt
  )

  override fun getPeriode(): Periode {
    return bidragsevnePeriode
  }
}

data class BPsAndelUnderholdskostnadPeriode(
  val soknadsbarnPersonId: Int,
  val referanse: String,
  val bPsAndelUnderholdskostnadPeriode: Periode,
  val andelProsent: BigDecimal,
  val andelBelop: BigDecimal,
  val barnetErSelvforsorget: Boolean
) : PeriodisertGrunnlag {

  constructor(bPsAndelUnderholdskostnadPeriode: BPsAndelUnderholdskostnadPeriode) : this(
    bPsAndelUnderholdskostnadPeriode.soknadsbarnPersonId,
    bPsAndelUnderholdskostnadPeriode.referanse,
    bPsAndelUnderholdskostnadPeriode.bPsAndelUnderholdskostnadPeriode.justerDatoer(),
    bPsAndelUnderholdskostnadPeriode.andelProsent,
    bPsAndelUnderholdskostnadPeriode.andelBelop,
    bPsAndelUnderholdskostnadPeriode.barnetErSelvforsorget,
  )

  override fun getPeriode(): Periode {
    return bPsAndelUnderholdskostnadPeriode
  }
}

data class SamvaersfradragPeriode(
  val soknadsbarnPersonId: Int,
  val referanse: String,
  val samvaersfradragPeriode: Periode,
  val belop: BigDecimal
) : PeriodisertGrunnlag {

  constructor(samvaersfradragPeriode: SamvaersfradragPeriode) : this(
    samvaersfradragPeriode.soknadsbarnPersonId,
    samvaersfradragPeriode.referanse,
    samvaersfradragPeriode.samvaersfradragPeriode.justerDatoer(),
    samvaersfradragPeriode.belop
  )

  override fun getPeriode(): Periode {
    return samvaersfradragPeriode
  }
}

data class DeltBostedPeriode(
  val soknadsbarnPersonId: Int,
  val referanse: String,
  val deltBostedPeriode: Periode,
  val deltBostedIPeriode: Boolean
) : PeriodisertGrunnlag {

  constructor(deltBostedPeriode: DeltBostedPeriode) : this(
    deltBostedPeriode.soknadsbarnPersonId,
    deltBostedPeriode.referanse,
    deltBostedPeriode.deltBostedPeriode.justerDatoer(),
    deltBostedPeriode.deltBostedIPeriode
  )

  override fun getPeriode(): Periode {
    return deltBostedPeriode
  }
}

data class BarnetilleggPeriode(
  val soknadsbarnPersonId: Int,
  val referanse: String,
  val barnetilleggPeriode: Periode,
  val belop: BigDecimal,
  val skattProsent: BigDecimal
) : PeriodisertGrunnlag {

  constructor(barnetilleggPeriode: BarnetilleggPeriode) : this(
    barnetilleggPeriode.soknadsbarnPersonId,
    barnetilleggPeriode.referanse,
    barnetilleggPeriode.barnetilleggPeriode.justerDatoer(),
    barnetilleggPeriode.belop,
    barnetilleggPeriode.skattProsent
  )

  override fun getPeriode(): Periode {
    return barnetilleggPeriode
  }
}

data class BarnetilleggForsvaretPeriode(
  val referanse: String,
  val barnetilleggForsvaretPeriode: Periode,
  val barnetilleggForsvaretIPeriode: Boolean
) : PeriodisertGrunnlag {

  constructor(barnetilleggForsvaretPeriode: BarnetilleggForsvaretPeriode) : this(
    barnetilleggForsvaretPeriode.referanse,
    barnetilleggForsvaretPeriode.barnetilleggForsvaretPeriode.justerDatoer(),
    barnetilleggForsvaretPeriode.barnetilleggForsvaretIPeriode
  )

  override fun getPeriode(): Periode {
    return barnetilleggForsvaretPeriode
  }
}

data class AndreLopendeBidragPeriode(
  val referanse: String,
  val andreLopendeBidragPeriode: Periode,
  val barnPersonId: Int,
  val bidragBelop: BigDecimal,
  val samvaersfradragBelop: BigDecimal
) : PeriodisertGrunnlag {

  constructor(andreLopendeBidragPeriode: AndreLopendeBidragPeriode) : this(
    andreLopendeBidragPeriode.referanse,
    andreLopendeBidragPeriode.andreLopendeBidragPeriode.justerDatoer(),
    andreLopendeBidragPeriode.barnPersonId,
    andreLopendeBidragPeriode.bidragBelop,
    andreLopendeBidragPeriode.samvaersfradragBelop
  )

  override fun getPeriode(): Periode {
    return andreLopendeBidragPeriode
  }
}
