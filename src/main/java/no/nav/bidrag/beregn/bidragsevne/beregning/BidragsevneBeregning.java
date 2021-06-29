package no.nav.bidrag.beregn.bidragsevne.beregning;

import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning;

public interface BidragsevneBeregning {

  ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning);

  static BidragsevneBeregning getInstance() {
    return new BidragsevneBeregningImpl();
  }
}
