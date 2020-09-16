package no.nav.bidrag.beregn.barnebidrag.beregning;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;

public class BarnebidragBeregningImpl implements BarnebidragBeregning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    double barnebidragBelop = 8000.0d;


    return new ResultatBeregning(1, barnebidragBelop, ResultatKode.KOSTNADSBEREGNET_BIDRAG);

  }

}
