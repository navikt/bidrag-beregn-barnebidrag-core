package no.nav.bidrag.beregn.barnebidrag.beregning;

import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;

public interface BarnebidragBeregning {

  List<ResultatBeregning> beregn(GrunnlagBeregning grunnlagBeregning);

  static BarnebidragBeregning getInstance(){
    return new BarnebidragBeregningImpl();
  }
}
