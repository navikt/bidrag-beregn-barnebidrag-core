package no.nav.bidrag.beregn.barnebidrag.beregning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode;

public class BarnebidragBeregningImpl implements BarnebidragBeregning {

  private List<ResultatBeregning> resultatBeregningListe = new ArrayList<>();
  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public List<ResultatBeregning> beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {


//    double maksBidragsbelop = 0d;
//    Double tjuefemProsentInntekt = grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt();
    BigDecimal tempBarnebidrag = BigDecimal.valueOf(0);
    ResultatKode resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;

/*    double totaltBelopUnderholdskostnad = grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe().stream()
        .map(GrunnlagBeregningPerBarn::getBPsAndelUnderholdskostnad).sum;*/

/*    var totaltBelopUnderholdskostnad = grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe().stream()
        .mapToDouble(BPsAndelUnderholdskostnad::getBPsAndelUnderholdskostnadBelop)
//        .map(BPsAndelUnderholdskostnad::getBPsAndelUnderholdskostnadBelop)
        .reduce(Double.valueOf(0), Double::sum);*/

/*    System.out.println("totaltBelopUnderholdskostnad: " + totaltBelopUnderholdskostnad);

    var maksBidragsbelop = 0d;

    if (grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop() <
        grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt()) {
      maksBidragsbelop = grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop();
    } else {
      maksBidragsbelop = grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt();
    }

    if (maksBidragsbelop < totaltBelopUnderholdskostnad) {



    }*/


    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn :
        grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()) {
      tempBarnebidrag = BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
          .getBPsAndelUnderholdskostnadBelop());

      var nettoBarnetilleggBP = grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop() -
          (grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop() *
              grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggSkattProsent() / 100);

      System.out.println("nettoBarnetilleggBP: " + nettoBarnetilleggBP);

      if (tempBarnebidrag.compareTo(BigDecimal.valueOf(nettoBarnetilleggBP)) < 0) {
        tempBarnebidrag = BigDecimal.valueOf(nettoBarnetilleggBP);
        resultatkode = ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGGBP;

      }


      resultatBeregningListe.add(new ResultatBeregning(
          grunnlagBeregningPerBarn.getSoknadsbarnPersonId(),
          tempBarnebidrag.doubleValue(), resultatkode));

    }

    return resultatBeregningListe;

  }

}
