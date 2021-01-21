package no.nav.bidrag.beregn.forholdsmessigfordeling.beregning;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

    var endeligBidragsevne = BigDecimal.ZERO;
    if (grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop().compareTo(
        grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt()) > 0){
      endeligBidragsevne = grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt();
      } else {
      endeligBidragsevne = grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop();
    }

    var resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;

    // Summerer beløp for alle saker i perioden. Hvis dette beløpet er høyere enn bidragsevnen så
    // skal det gjøres en forholdsmessig fordeling
    var samletBidragsbelopAlleSaker = grunnlagBeregningPeriodisert.getBeregnetBidragSakListe()
        .stream()
        .map(BeregnetBidragSak::getGrunnlagPerBarnListe)
        .flatMap(Collection::stream)
        .map(GrunnlagPerBarn::getBidragBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    System.out.println("Endelig bidragsevne: " + endeligBidragsevne);
    System.out.println("Samlet bidragsbeløp for alle saker: " + samletBidragsbelopAlleSaker);

    if (samletBidragsbelopAlleSaker.compareTo(endeligBidragsevne) > 0){
      // Gjør forholdsmessig fordeling
      System.out.println("Gjør forholdsmessig fordeling");
      var sakensAndelAvTotaltBelopAlleSakerProsent = BigDecimal.ZERO;
      var sakensAndelAvEndeligBidragsevneBelop = BigDecimal.ZERO;
      var barnetsAndelAvTotaltBelopForSakProsent = BigDecimal.ZERO;
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

        // Hvor stor prosentdel av det totale beløpet utgjør denne sakens samlede bidragsbeløp
        sakensAndelAvTotaltBelopAlleSakerProsent = samletBidragsbelopSak.divide(samletBidragsbelopAlleSaker,
        new MathContext(10, RoundingMode.HALF_UP));

        // Regner ut hvor mye prosentsatsen utgjør av bidragsevnen
        sakensAndelAvEndeligBidragsevneBelop = sakensAndelAvTotaltBelopAlleSakerProsent.multiply(endeligBidragsevne,
            new MathContext(10, RoundingMode.HALF_UP));

        var resultatPerBarnListe = new ArrayList<ResultatPerBarn>();
        // Leser hvert barn i saken og regner ut nytt, justert bidragsbeløp
        for (GrunnlagPerBarn grunnlagPerBarn : beregnetBidragSak.getGrunnlagPerBarnListe()){
          barnetsAndelAvTotaltBelopForSakProsent = grunnlagPerBarn.getBidragBelop().divide(samletBidragsbelopSak,
              new MathContext(10, RoundingMode.HALF_UP));
          beregnetBidragsbelop = barnetsAndelAvTotaltBelopForSakProsent.multiply(sakensAndelAvEndeligBidragsevneBelop,
              new MathContext(10, RoundingMode.HALF_UP));

          // Bidrag skal avrundes til nærmeste tier
          beregnetBidragsbelop = beregnetBidragsbelop.setScale(-1, RoundingMode.HALF_UP);

          resultatPerBarnListe.add(new ResultatPerBarn(grunnlagPerBarn.getBarnPersonId(),
              beregnetBidragsbelop, ResultatKode.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET));
        }
        resultatBeregningListe
            .add(new ResultatBeregning(
                beregnetBidragSak.getSaksnr(),
                resultatPerBarnListe));
        }
      } else {
      // Ingen forholdsmessig fordeling gjøres og originalt bidragsbeløp returneres
      System.out.println("Ingen forholdsmessig fordeling");

      for (BeregnetBidragSak beregnetBidragSak : grunnlagBeregningPeriodisert.getBeregnetBidragSakListe()) {
        var resultatPerBarnListe = new ArrayList<ResultatPerBarn>();
        for (GrunnlagPerBarn grunnlagPerBarn : beregnetBidragSak.getGrunnlagPerBarnListe()){
          resultatPerBarnListe.add(new ResultatPerBarn(grunnlagPerBarn.getBarnPersonId(),
              grunnlagPerBarn.getBidragBelop(), ResultatKode.FORHOLDSMESSIG_FORDELING_INGEN_ENDRING));
        }
        resultatBeregningListe
            .add(new ResultatBeregning(
                beregnetBidragSak.getSaksnr(),
                resultatPerBarnListe));
      }
    }

    return resultatBeregningListe;
  }


}
