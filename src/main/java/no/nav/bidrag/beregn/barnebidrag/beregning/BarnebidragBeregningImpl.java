package no.nav.bidrag.beregn.barnebidrag.beregning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;

public class BarnebidragBeregningImpl implements BarnebidragBeregning {

  private List<ResultatBeregning> resultatBeregningListe = new ArrayList<>();
  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public List<ResultatBeregning> beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

//    BigDecimal tempBarnebidrag = BigDecimal.valueOf(0);
    ResultatKode resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;
    boolean bidragRedusertAvBidragsevne = false;
    boolean bidragRedusertAv25ProsentAvInntekt = false;

    double totaltBelopUnderholdskostnad = grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()
        .stream()
        .map(GrunnlagBeregningPerBarn::getBPsAndelUnderholdskostnad)
        .mapToDouble(BPsAndelUnderholdskostnad::getBPsAndelUnderholdskostnadBelop).sum();

    System.out.println("totaltBelopUnderholdskostnad: " + totaltBelopUnderholdskostnad);

    var maksBidragsbelop = 0d;

    if (grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop() <
        grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt()) {
      maksBidragsbelop = grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop();
      bidragRedusertAvBidragsevne = true;
    } else {
      maksBidragsbelop = grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt();
//      resultatkode = ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT;
      bidragRedusertAv25ProsentAvInntekt = true;
    }
    System.out.println("maksBidragsbelop: " + maksBidragsbelop);



    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn :
        grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()) {

      boolean bidragSattTilBarnetilleggBP = false;
      boolean bidragSattTilBarnetilleggBM = false;

      BigDecimal tempBarnebidrag = BigDecimal.valueOf(0);

      var nettoBarnetilleggBP = grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop() -
          (grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop() *
              grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggSkattProsent() / 100);

      System.out.println("nettobarnetilleggBP: " + nettoBarnetilleggBP);

      var nettoBarnetilleggBM = grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggBelop() -
          (grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggBelop() *
              grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggSkattProsent() / 100);
      System.out.println("nettobarnetilleggBM: " + nettoBarnetilleggBM);

      if (maksBidragsbelop < totaltBelopUnderholdskostnad) {
        // Bidraget skal begrenses forholdsmessig pga manglende evne/25%-regel
/*        var andelProsent = BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
        .getBPsAndelUnderholdskostnadBelop()).divide(BigDecimal.valueOf(totaltBelopUnderholdskostnad))
            .setScale(1, RoundingMode.HALF_UP);*/

        var andelProsent = BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
            .getBPsAndelUnderholdskostnadBelop() / totaltBelopUnderholdskostnad);
//            .setScale(10, RoundingMode.HALF_UP);


        System.out.println("Andel av evne: " + andelProsent);
        tempBarnebidrag = BigDecimal.valueOf(maksBidragsbelop).multiply(andelProsent);
      } else {
        tempBarnebidrag = BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
            .getBPsAndelUnderholdskostnadBelop());
      }

      tempBarnebidrag.subtract(BigDecimal.valueOf(grunnlagBeregningPerBarn.getSamvaersfradrag()));
      System.out.println("Bidrag etter samværsfradrag/kostnadsbasert bidrag: " + tempBarnebidrag);
/*
      // Hvis bidraget er redusert av evne eller 25% og er utregnet til negativt beløp etter samværsfradrag
      // skal det indikeres at BP har noe evne
      if (tempBarnebidrag.compareTo(BigDecimal.valueOf(0)) <= 0) {

      }*/

      // Dersom beregnet bidrag etter samværsfradrag er lavere enn eventuelt barnetillegg for BP
      // så skal bidraget settes likt barnetillegget
      if (tempBarnebidrag.compareTo(BigDecimal.valueOf(nettoBarnetilleggBP)) < 0) {
        tempBarnebidrag = BigDecimal.valueOf(nettoBarnetilleggBP);
        bidragSattTilBarnetilleggBP = true;
//        resultatkode = ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGGBP;
      } else {
        // Regel for barnetilleggBP har ikke slått til. Sjekk om eventuelt barnetillegg for BM skal benyttes
        if (tempBarnebidrag.compareTo(
            BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadBelop())
            .subtract(BigDecimal.valueOf(nettoBarnetilleggBM))) > 0) {
          tempBarnebidrag = BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadBelop())
              .subtract(BigDecimal.valueOf(nettoBarnetilleggBM));
          bidragSattTilBarnetilleggBM = true;

        }


      }


      tempBarnebidrag = tempBarnebidrag.setScale(-1, RoundingMode.HALF_UP);



      resultatBeregningListe.add(new ResultatBeregning(
          grunnlagBeregningPerBarn.getSoknadsbarnPersonId(),
          tempBarnebidrag.doubleValue(), resultatkode));

    }

    return resultatBeregningListe;

  }

}
