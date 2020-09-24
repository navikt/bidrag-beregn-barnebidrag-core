package no.nav.bidrag.beregn.barnebidrag.beregning;

import java.math.BigDecimal;
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

    double totaltBelopUnderholdskostnad = grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()
        .stream()
        .map(GrunnlagBeregningPerBarn::getBPsAndelUnderholdskostnad)
        .mapToDouble(BPsAndelUnderholdskostnad::getBPsAndelUnderholdskostnadBelop).sum();

    System.out.println("totaltBelopUnderholdskostnad: " + totaltBelopUnderholdskostnad);

    var maksBidragsbelop = 0d;

    if (grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop() <
        grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt()) {
      maksBidragsbelop = grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop();
      resultatkode = ResultatKode.BIDRAG_REDUSERT_AV_EVNE;
    } else {
      maksBidragsbelop = grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt();
      resultatkode = ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT;
    }




    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn :
        grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()) {

      BigDecimal tempBarnebidrag = BigDecimal.valueOf(0);

      var nettoBarnetilleggBP = grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop() -
          (grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop() *
              grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggSkattProsent() / 100);

      var nettoBarnetilleggBM = grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggBelop() -
          (grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggBelop() *
              grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggSkattProsent() / 100);

      if (maksBidragsbelop < totaltBelopUnderholdskostnad) {
        // Bidraget skal begrenses forholdsmessig pga manglende evne/25%-regel
        var andelProsent = BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
        .getBPsAndelUnderholdskostnadBelop()).divide(BigDecimal.valueOf(totaltBelopUnderholdskostnad));
        System.out.println("Andel av evne: " + andelProsent);
        tempBarnebidrag = BigDecimal.valueOf(maksBidragsbelop).multiply(andelProsent);
      } else {
        tempBarnebidrag = BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
            .getBPsAndelUnderholdskostnadBelop());
      }




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
