package no.nav.bidrag.beregn.forholdsmessigfordeling.beregning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning;

public class ForholdsmessigFordelingBeregningImpl implements ForholdsmessigFordelingBeregning {

  @Override
  public List<ResultatBeregning> beregn(
      List<GrunnlagBeregningPeriodisert> grunnlagBeregningPeriodisertListe) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();

    for (GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert : grunnlagBeregningPeriodisertListe) {

      var resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;

      var tempBarnebidrag = BigDecimal.ZERO;

      resultatBeregningListe
          .add(new ResultatBeregning(
              grunnlagBeregningPeriodisert.getSaksnr(),
              grunnlagBeregningPeriodisert.getBarnPersonId(),
              grunnlagBeregningPeriodisert.getBidragBelop(),
              resultatkode));
    }

    return resultatBeregningListe;
  }


}
