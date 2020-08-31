package no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning;


import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning;

public interface KostnadsberegnetBidragBeregning {

  ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  static KostnadsberegnetBidragBeregning getInstance(){
    return new KostnadsberegnetBidragBeregningImpl();
  }

}


