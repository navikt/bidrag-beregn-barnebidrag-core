package no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning;

import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlagPeriodisert;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning;

public interface KostnadsberegnetBidragBeregning {

  ResultatBeregning beregn(
      BeregnKostnadsberegnetBidragGrunnlagPeriodisert beregnKostnadsberegnetBidragGrunnlagPeriodisert);

  static KostnadsberegnetBidragBeregning getInstance(){
    return new KostnadsberegnetBidragBeregningImpl();
  }

}


