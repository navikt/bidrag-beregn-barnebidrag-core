package no.nav.bidrag.beregn.forholdsmessigfordeling.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal

data class BidragsevnePeriode(
  val bidragsevneDatoFraTil: Periode,
  val bidragsevneBelop: BigDecimal,
  val tjuefemProsentInntekt: BigDecimal) : PeriodisertGrunnlag {
  constructor(bidragsevnePeriode: BidragsevnePeriode)
      : this(bidragsevnePeriode.bidragsevneDatoFraTil.justerDatoer(),
    bidragsevnePeriode.bidragsevneBelop,
    bidragsevnePeriode.tjuefemProsentInntekt)
  override fun getDatoFraTil(): Periode {
    return bidragsevneDatoFraTil
  }
}

data class BeregnetBidragSakPeriode(
    val saksnr: Int,
    val periodeDatoFraTil: Periode,
    val grunnlagPerBarnListe: List<GrunnlagPerBarn>) : PeriodisertGrunnlag {
  constructor(beregnetBidragSakPeriode: BeregnetBidragSakPeriode)
      : this(
      beregnetBidragSakPeriode.saksnr,
      beregnetBidragSakPeriode.periodeDatoFraTil.justerDatoer(),
      beregnetBidragSakPeriode.grunnlagPerBarnListe)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}