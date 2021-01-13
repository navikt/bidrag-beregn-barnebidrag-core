package no.nav.bidrag.beregn.forholdsmessigfordeling.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal

data class BeregnetBidragSakPeriode(
    val saksnr: Int,
    val periodeDatoFraTil: Periode,
    val barnPersonId: Int,
    val bidragBelop: BigDecimal,
    val resultatkode: ResultatKode) : PeriodisertGrunnlag {
  constructor(beregnetBidragSakPeriode: BeregnetBidragSakPeriode)
      : this(
      beregnetBidragSakPeriode.saksnr,
      beregnetBidragSakPeriode.periodeDatoFraTil.justerDatoer(),
      beregnetBidragSakPeriode.barnPersonId,
      beregnetBidragSakPeriode.bidragBelop,
      beregnetBidragSakPeriode.resultatkode)
  override fun getDatoFraTil(): Periode {
    return periodeDatoFraTil
  }
}