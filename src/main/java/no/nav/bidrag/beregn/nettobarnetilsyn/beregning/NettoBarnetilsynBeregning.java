package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import java.util.List;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public interface NettoBarnetilsynBeregning {

  List<ResultatBeregning> beregn(GrunnlagBeregning grunnlagBeregning);

  static NettoBarnetilsynBeregning getInstance(){
    return new NettoBarnetilsynBeregningImpl();
  }
}
