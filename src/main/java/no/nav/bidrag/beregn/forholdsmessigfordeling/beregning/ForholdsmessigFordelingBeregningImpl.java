package no.nav.bidrag.beregn.forholdsmessigfordeling.beregning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSak;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning;

public class ForholdsmessigFordelingBeregningImpl implements ForholdsmessigFordelingBeregning {

  @Override
  public List<ResultatBeregning> beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();

    var bidragsevne = grunnlagBeregningPeriodisert.getBidragsevne();

    for (BeregnetBidragSak beregnetBidragSak : grunnlagBeregningPeriodisert.getBeregnetBidragSakListe()) {

      var resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;

      var tempBarnebidrag = BigDecimal.ZERO;

      resultatBeregningListe
          .add(new ResultatBeregning(
              beregnetBidragSak.getSaksnr(),
              beregnetBidragSak.getBarnPersonId(),
              beregnetBidragSak.getBidragBelop(),
              resultatkode));
    }

    return resultatBeregningListe;
  }


}
