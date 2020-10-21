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

    BigDecimal totaltBelopUnderholdskostnad = BigDecimal.valueOf(grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()
        .stream()
        .map(GrunnlagBeregningPerBarn::getBPsAndelUnderholdskostnad)
        .mapToDouble(BPsAndelUnderholdskostnad::getBPsAndelUnderholdskostnadBelop).sum());

//    System.out.println("totaltBelopUnderholdskostnad: " + totaltBelopUnderholdskostnad);

    BigDecimal maksBidragsbelop = BigDecimal.valueOf(0);

    if (grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop() <
        grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt()) {
      maksBidragsbelop = BigDecimal.valueOf(grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop());
      bidragRedusertAvBidragsevne = true;
    } else {
      maksBidragsbelop = BigDecimal.valueOf(grunnlagBeregningPeriodisert.getBidragsevne().getTjuefemProsentInntekt());
    }
    System.out.println("maksBidragsbelop: " + maksBidragsbelop);

    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn :
        grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()) {

      ResultatKode resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;

      BigDecimal tempBarnebidrag = BigDecimal.valueOf(0);

      // Beregner nettobarnetilsyn for BP og BM
      var nettoBarnetilleggBP = grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop() -
          (grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop() *
              grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggSkattProsent() / 100);

      var nettoBarnetilleggBM = grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggBelop() -
          (grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggBelop() *
              grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggSkattProsent() / 100);

      // Regner ut underholdskostnad ut fra andelsprosent og beløp. Skal ikke gjøres hvis disse er lik 0
      BigDecimal underholdskostnad = BigDecimal.valueOf(0);
      if (grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadProsent() > 0 &&
          grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadBelop()   > 0) {
        underholdskostnad =
            BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
                .getBPsAndelUnderholdskostnadBelop()).divide(BigDecimal.valueOf(
                grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadProsent()),
                new MathContext(10, RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(100));
        underholdskostnad = underholdskostnad.setScale(0, RoundingMode.HALF_UP);
//        System.out.println("U: " + underholdskostnad);
      }

      // Sjekker om totalt bidragsbeløp er større enn bidragsevne eller 25% av månedsinntekt
      if (maksBidragsbelop.compareTo(totaltBelopUnderholdskostnad) < 0) {
        // Bidraget skal begrenses forholdsmessig pga manglende evne/25%-regel
        var andelProsent = BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
            .getBPsAndelUnderholdskostnadBelop()).divide(totaltBelopUnderholdskostnad,
        new MathContext(10, RoundingMode.HALF_UP));
        System.out.println("Andel av evne: " + andelProsent);
        tempBarnebidrag = maksBidragsbelop.multiply(andelProsent);
        if (bidragRedusertAvBidragsevne) {
          resultatkode = ResultatKode.BIDRAG_REDUSERT_AV_EVNE;
        }
          else { resultatkode = ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT;
        }
      } else {
        tempBarnebidrag = BigDecimal.valueOf(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
            .getBPsAndelUnderholdskostnadBelop());
      }

      // Trekker fra samværsfradrag
      tempBarnebidrag = tempBarnebidrag.subtract(BigDecimal.valueOf(grunnlagBeregningPerBarn.getSamvaersfradrag()));
      //      System.out.println("Bidrag etter samværsfradrag: " + tempBarnebidrag);

      // Sjekker mot særregler for barnetillegg BP/BM
      // Dersom beregnet bidrag etter samværsfradrag er lavere enn eventuelt barnetillegg for BP
      // så skal bidraget settes likt barnetillegget. BarnetilleggBP skal ikke taes hensyn til ved delt bosted
      if (!grunnlagBeregningPerBarn.getDeltBosted() &&
          tempBarnebidrag.compareTo(BigDecimal.valueOf(nettoBarnetilleggBP)) < 0
          && nettoBarnetilleggBP > 0d) {
        tempBarnebidrag = BigDecimal.valueOf(nettoBarnetilleggBP - grunnlagBeregningPerBarn.getSamvaersfradrag());
        resultatkode = ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_BP;
      } else {
        // Regel for barnetilleggBP har ikke slått til. Sjekk om eventuelt barnetillegg for BM skal benyttes.
        // Bidrag settes likt underholdskostnad minus netto barnetilleggBM når beregnet bidrag er høyere enn
        // underholdskostnad minus netto barnetillegg for BM
        if (tempBarnebidrag.compareTo(underholdskostnad.subtract(BigDecimal.valueOf(nettoBarnetilleggBM))) > 0) {
          tempBarnebidrag = underholdskostnad.subtract(BigDecimal.valueOf(nettoBarnetilleggBM));
          tempBarnebidrag = tempBarnebidrag.subtract(BigDecimal.valueOf(grunnlagBeregningPerBarn.getSamvaersfradrag()));
          resultatkode = ResultatKode.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM;
        }
      }

      // Beløp for bidrag settes til 0 hvis bidraget er utregnet til negativt beløp etter samværsfradrag
      if (tempBarnebidrag.compareTo(BigDecimal.valueOf(0)) <= 0) {
        tempBarnebidrag = BigDecimal.valueOf(0);
      }

      if (grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop() == 0d) {
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
          tempBarnebidrag.doubleValue(), resultatkode));

    }

    return resultatBeregningListe;

  }

  @Override
  public List<ResultatBeregning> beregnVedBarnetilleggForsvaret(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    List<ResultatBeregning> resultatBeregningListe = new ArrayList<>();

    BigDecimal barnetilleggForsvaretForsteBarn = BigDecimal.valueOf(SjablonUtil.hentSjablonverdi(
        grunnlagBeregningPeriodisert.getSjablonListe(),
        SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP));

    BigDecimal barnetilleggForsvaretOvrigeBarn = BigDecimal.valueOf(SjablonUtil.hentSjablonverdi(
        grunnlagBeregningPeriodisert.getSjablonListe(),
        SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP));

    BigDecimal barnetilleggForsvaretPerBarn = BigDecimal.valueOf(0);

    System.out.println("barnetillegg første barn: " + barnetilleggForsvaretForsteBarn);
    System.out.println("barnetillegg øvrige barn: " + barnetilleggForsvaretOvrigeBarn);

    if (grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe().size() == 1) {
      barnetilleggForsvaretPerBarn = barnetilleggForsvaretForsteBarn;
    } else {
      barnetilleggForsvaretPerBarn = barnetilleggForsvaretForsteBarn.add(barnetilleggForsvaretOvrigeBarn)
      .divide(BigDecimal.valueOf(grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe().size()),
          new MathContext(10, RoundingMode.HALF_UP));

      System.out.println("barnetilleggForsvaretPerBarn: " + barnetilleggForsvaretPerBarn.toString());

      barnetilleggForsvaretPerBarn = barnetilleggForsvaretPerBarn.setScale(0, RoundingMode.HALF_UP);
      System.out.println("barnetilleggForsvaretPerBarn etter avrunding: " + barnetilleggForsvaretPerBarn.toString());

    }

    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn :
        grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()) {

      var barnebidragEtterSamvaersfradrag =
          barnetilleggForsvaretPerBarn.subtract(BigDecimal.valueOf(grunnlagBeregningPerBarn.getSamvaersfradrag()));

      ResultatKode resultatkode = ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET;

      resultatBeregningListe.add(new ResultatBeregning(
          grunnlagBeregningPerBarn.getSoknadsbarnPersonId(),
          barnebidragEtterSamvaersfradrag.doubleValue(), resultatkode));
    }
    return resultatBeregningListe;
  }


  /*
  @Override
  // Metode for å beregne totalt beløp for underholdskostnad. Ved delt bosted skal BPs andel av underholdskostnad
  // reduseres med 50 prosentpoeng
  public BigDecimal finnTotalUnderholdskostnad(GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {
    BigDecimal totalUnderholdskostnad = BigDecimal.valueOf(0);

    System.out.println("finnTotalUnderholdskostnad");

    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn :
        grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()) {
      System.out.println("finnTotalUnderholdskostnad2");
      if (grunnlagBeregningPerBarn.getDeltBosted()) {
        System.out.println("finnTotalUnderholdskostnad3");
        if (grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
            .getBPsAndelUnderholdskostnadProsent() >= 50d) {
          BigDecimal omregnetUnderholdskostnad =
              BigDecimal.valueOf(
                  grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadBelop() /
                      grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadProsent())
                  .multiply(BigDecimal.valueOf(100));
          System.out.println("omregnetUnderholdskostnad: " + omregnetUnderholdskostnad.toString());
          omregnetUnderholdskostnad = omregnetUnderholdskostnad.multiply(BigDecimal.valueOf(
              grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadProsent() - 50d))
          .divide(BigDecimal.valueOf(100));
          System.out.println("justert omregnetUnderholdskostnad: " + omregnetUnderholdskostnad.toString());

          totalUnderholdskostnad = totalUnderholdskostnad.add(omregnetUnderholdskostnad);
        }
      } else {
        System.out.println("finnTotalUnderholdskostnad5");
        totalUnderholdskostnad = totalUnderholdskostnad.add(BigDecimal.valueOf(
            grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadBelop()));
      }
    }

    return totalUnderholdskostnad;
  }*/
}


































