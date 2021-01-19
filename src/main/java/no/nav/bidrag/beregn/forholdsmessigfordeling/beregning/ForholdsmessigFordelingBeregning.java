
package no.nav.bidrag.beregn.forholdsmessigfordeling.beregning;

import java.util.List;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning;

public interface ForholdsmessigFordelingBeregning {

  List<ResultatBeregning> beregn(GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert);

  static ForholdsmessigFordelingBeregning getInstance(){
    return new ForholdsmessigFordelingBeregningImpl();
  }

}
