package no.nav.bidrag.beregn.forholdsmessigfordeling.beregning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSak;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagPerBarn;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPerBarn;

public class ForholdsmessigFordelingBeregningImpl implements ForholdsmessigFordelingBeregning {

  @Override
  public List<ResultatBeregning> beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();
    var resultatPerBarnListe = new ArrayList<ResultatPerBarn>();

    var endeligBidragsevne = BigDecimal.ZERO;
    if (grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop().compareTo(
        grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt()) > 0){
      endeligBidragsevne = grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop();
      } else {
      endeligBidragsevne = grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt();
    }

    var resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;

    // Summerer beløp for alle saker i perioden. Hvis dette beløpet er høyere enn bidragsevnen så
    // skal det gjøres en forholdsmessig fordeling
    var samletBidragsbelopAlleSaker = grunnlagBeregningPeriodisert.getBeregnetBidragSakListe()
        .stream()
        .map(BeregnetBidragSak::getGrunnlagPerBarnListe)
        .flatMap(Collection::stream)
//        .collect(Collectors.toList())
//        .stream()
        .map(GrunnlagPerBarn::getBidragBelop)
//        .flatMap(GrunnlagPerBarn::getBidragBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    System.out.println("Samlet bidragsbeløp for alle saker: " + samletBidragsbelopAlleSaker);

    if (samletBidragsbelopAlleSaker.compareTo(endeligBidragsevne) > 0){
      // Gjør forholdsmessig fordeling
      var sakensAndelAvTotaltBelopAlleSaker = BigDecimal.ZERO;
      var barnetsAndelAvTotaltBelopForSak = BigDecimal.ZERO;
      var beregnetBidragsbelop = BigDecimal.ZERO;
      var samletBidragsbelopSak = BigDecimal.ZERO;

      for (BeregnetBidragSak beregnetBidragSak : grunnlagBeregningPeriodisert.getBeregnetBidragSakListe()) {
        samletBidragsbelopSak = grunnlagBeregningPeriodisert.getBeregnetBidragSakListe()
            .stream()
            .filter(i -> i.getSaksnr() == beregnetBidragSak.getSaksnr())
            .map(BeregnetBidragSak::getGrunnlagPerBarnListe)
            .flatMap(Collection::stream)
            .map(GrunnlagPerBarn::getBidragBelop)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Samlet bidragsbeløp for sak: " + beregnetBidragSak.getSaksnr() + " = " + samletBidragsbelopSak);






      }




      } else {
      // Ingen forholdsmessig fordeling gjøres og originalt bidragsbeløp returneres
      for (BeregnetBidragSak beregnetBidragSak : grunnlagBeregningPeriodisert.getBeregnetBidragSakListe()) {
        for (GrunnlagPerBarn grunnlagPerBarn : beregnetBidragSak.getGrunnlagPerBarnListe()){
          resultatPerBarnListe.add(new ResultatPerBarn(grunnlagPerBarn.getBarnPersonId(),
              grunnlagPerBarn.getBidragBelop(), ResultatKode.KOSTNADSBEREGNET_BIDRAG));
        }
        resultatBeregningListe
            .add(new ResultatBeregning(
                beregnetBidragSak.getSaksnr(),
                resultatPerBarnListe));


        var tempBarnebidrag = BigDecimal.ZERO;

      }


    }


    return resultatBeregningListe;
  }


}
