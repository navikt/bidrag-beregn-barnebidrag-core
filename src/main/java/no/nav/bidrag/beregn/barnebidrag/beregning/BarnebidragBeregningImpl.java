package no.nav.bidrag.beregn.barnebidrag.beregning;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class BarnebidragBeregningImpl implements BarnebidragBeregning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public List<ResultatBeregning> beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    boolean bidragRedusertAvBidragsevne = false;

    List<ResultatBeregning> resultatBeregningListe = new ArrayList<>();

    BigDecimal totaltBelopUnderholdskostnad = BigDecimal.valueOf(0);

    for (GrunnlagBeregningPerBarn grunnlag : grunnlagBeregningPeriodisert
        .getGrunnlagPerBarnListe()) {
      totaltBelopUnderholdskostnad =
          totaltBelopUnderholdskostnad
              .add(grunnlag.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadBelop());
    }

    BigDecimal maksBidragsbelop = BigDecimal.ZERO;

    if (grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop().compareTo(
        grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt()) < 0) {
      maksBidragsbelop = grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop();
      bidragRedusertAvBidragsevne = true;
    } else {
      maksBidragsbelop = grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt();
    }
    System.out.println("maksBidragsbelop: " + maksBidragsbelop);

    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn :
        grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()) {

      ResultatKode resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;

      BigDecimal tempBarnebidrag = BigDecimal.valueOf(0);

      // Beregner nettobarnetilsyn for BP og BM
      var nettoBarnetilleggBP = grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop()
          .subtract(
              (grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop()
                  .multiply(
                      grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggSkattProsent()
                          .divide(BigDecimal.valueOf(100),
                              new MathContext(10, RoundingMode.HALF_UP)))));

      var nettoBarnetilleggBM = grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggBelop()
          .subtract(
              (grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggBelop()
                  .multiply(
                      grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggSkattProsent())
                  .divide(BigDecimal.valueOf(100),
                      new MathContext(10, RoundingMode.HALF_UP))));

      // Regner ut underholdskostnad ut fra andelsprosent og beløp. Skal ikke gjøres hvis disse er lik 0
      BigDecimal underholdskostnad = BigDecimal.valueOf(0);
      if (grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
          .getBPsAndelUnderholdskostnadProsent()
          .compareTo(BigDecimal.valueOf(0)) > 0 &&
          grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
              .getBPsAndelUnderholdskostnadBelop()
              .compareTo(BigDecimal.valueOf(0)) > 0) {
        underholdskostnad =
            grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
                .getBPsAndelUnderholdskostnadBelop().divide(
                grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
                    .getBPsAndelUnderholdskostnadProsent(),
                new MathContext(10, RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(100));
        underholdskostnad = underholdskostnad.setScale(0, RoundingMode.HALF_UP);
//        System.out.println("U: " + underholdskostnad);
      }

      // Sjekker om totalt bidragsbeløp er større enn bidragsevne eller 25% av månedsinntekt
      if (maksBidragsbelop.compareTo(totaltBelopUnderholdskostnad) < 0) {
        // Bidraget skal begrenses forholdsmessig pga manglende evne/25%-regel
        var andelProsent = grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
            .getBPsAndelUnderholdskostnadBelop()
            .divide(totaltBelopUnderholdskostnad,
                new MathContext(10, RoundingMode.HALF_UP));
        System.out.println("Andel av evne: " + andelProsent);

        tempBarnebidrag = maksBidragsbelop.multiply(andelProsent);
        if (bidragRedusertAvBidragsevne) {
          resultatkode = ResultatKode.BIDRAG_REDUSERT_AV_EVNE;
        } else {
          resultatkode = ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT;
        }
      } else {
        tempBarnebidrag = grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
            .getBPsAndelUnderholdskostnadBelop();
      }

      // Trekker fra samværsfradrag
      tempBarnebidrag = tempBarnebidrag.subtract(grunnlagBeregningPerBarn.getSamvaersfradrag());
      //      System.out.println("Bidrag etter samværsfradrag: " + tempBarnebidrag);

      // Sjekker mot særregler for barnetillegg BP/BM
      // Dersom beregnet bidrag etter samværsfradrag er lavere enn eventuelt barnetillegg for BP
      // så skal bidraget settes likt barnetillegget. BarnetilleggBP skal ikke taes hensyn til ved delt bosted
      if (!grunnlagBeregningPerBarn.getDeltBosted() &&
          tempBarnebidrag.compareTo(nettoBarnetilleggBP) < 0
          && nettoBarnetilleggBP.compareTo(BigDecimal.valueOf(0)) > 0) {
        tempBarnebidrag = nettoBarnetilleggBP
            .subtract(grunnlagBeregningPerBarn.getSamvaersfradrag());
        resultatkode = ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_BP;
      } else {
        // Regel for barnetilleggBP har ikke slått til. Sjekk om eventuelt barnetillegg for BM skal benyttes.
        // Bidrag settes likt underholdskostnad minus netto barnetilleggBM når beregnet bidrag er høyere enn
        // underholdskostnad minus netto barnetillegg for BM
        if (tempBarnebidrag.compareTo(underholdskostnad.subtract(nettoBarnetilleggBM)) > 0) {
          tempBarnebidrag = underholdskostnad.subtract(nettoBarnetilleggBM);
          tempBarnebidrag = tempBarnebidrag.subtract(grunnlagBeregningPerBarn.getSamvaersfradrag());
          resultatkode = ResultatKode.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM;
        }
      }

      // Beløp for bidrag settes til 0 hvis bidraget er utregnet til negativt beløp etter samværsfradrag
      if (tempBarnebidrag.compareTo(BigDecimal.valueOf(0)) <= 0) {
        tempBarnebidrag = BigDecimal.valueOf(0);
      }

      if (grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop().compareTo(
          BigDecimal.valueOf(0)) == 0) {
        resultatkode = ResultatKode.INGEN_EVNE;
      }

      if (grunnlagBeregningPerBarn.getDeltBosted()) {
        resultatkode = ResultatKode.DELT_BOSTED;
      }

      if (grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBarnetErSelvforsorget()) {
        tempBarnebidrag = BigDecimal.valueOf(0);
        resultatkode = ResultatKode.BARNET_ER_SELVFORSORGET;
      }

      // Bidrag skal avrundes til nærmeste tier
      tempBarnebidrag = tempBarnebidrag.setScale(-1, RoundingMode.HALF_UP);

      resultatBeregningListe.add(new ResultatBeregning(
          grunnlagBeregningPerBarn.getSoknadsbarnPersonId(),
          tempBarnebidrag, resultatkode));

    }

    return resultatBeregningListe;

  }

  @Override
  public List<ResultatBeregning> beregnVedBarnetilleggForsvaret(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    List<ResultatBeregning> resultatBeregningListe = new ArrayList<>();

    BigDecimal barnetilleggForsvaretForsteBarn = SjablonUtil.hentSjablonverdi(
        grunnlagBeregningPeriodisert.getSjablonListe(),
        SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP);

    BigDecimal barnetilleggForsvaretOvrigeBarn = SjablonUtil.hentSjablonverdi(
        grunnlagBeregningPeriodisert.getSjablonListe(),
        SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP);

    BigDecimal barnetilleggForsvaretPerBarn = BigDecimal.valueOf(0);

    System.out.println("barnetillegg første barn: " + barnetilleggForsvaretForsteBarn);
    System.out.println("barnetillegg øvrige barn: " + barnetilleggForsvaretOvrigeBarn);

    if (grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe().size() == 1) {
      barnetilleggForsvaretPerBarn = barnetilleggForsvaretForsteBarn;
    } else {
      barnetilleggForsvaretPerBarn = barnetilleggForsvaretForsteBarn
          .add(barnetilleggForsvaretOvrigeBarn)
          .divide(BigDecimal.valueOf(grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe().size()),
              new MathContext(10, RoundingMode.HALF_UP));

      System.out
          .println("barnetilleggForsvaretPerBarn: " + barnetilleggForsvaretPerBarn.toString());

      barnetilleggForsvaretPerBarn = barnetilleggForsvaretPerBarn.setScale(0, RoundingMode.HALF_UP);
      System.out.println(
          "barnetilleggForsvaretPerBarn etter avrunding: " + barnetilleggForsvaretPerBarn
              .toString());

    }

    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn :
        grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()) {

      var barnebidragEtterSamvaersfradrag =
          barnetilleggForsvaretPerBarn.subtract(grunnlagBeregningPerBarn.getSamvaersfradrag());

      ResultatKode resultatkode = ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET;

      resultatBeregningListe.add(new ResultatBeregning(
          grunnlagBeregningPerBarn.getSoknadsbarnPersonId(),
          barnebidragEtterSamvaersfradrag, resultatkode));
    }
    return resultatBeregningListe;
  }
}
