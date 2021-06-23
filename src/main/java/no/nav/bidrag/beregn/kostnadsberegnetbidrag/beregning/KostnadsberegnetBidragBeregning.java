package no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning;

import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning;

public interface KostnadsberegnetBidragBeregning {

  ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning);

  static KostnadsberegnetBidragBeregning getInstance(){
    return new KostnadsberegnetBidragBeregningImpl();
  }
}
