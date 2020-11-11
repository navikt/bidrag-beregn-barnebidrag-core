package no.nav.bidrag.beregn.barnebidrag.beregning;

import static java.util.Collections.emptyList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class BarnebidragBeregningImpl implements BarnebidragBeregning {

  @Override
  public List<ResultatBeregning> beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    var bidragRedusertAvBidragsevne = false;

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();

    var totaltBelopUnderholdskostnad = BigDecimal.ZERO;

    for (GrunnlagBeregningPerBarn grunnlag : grunnlagBeregningPeriodisert
        .getGrunnlagPerBarnListe()) {
      totaltBelopUnderholdskostnad =
          totaltBelopUnderholdskostnad
              .add(grunnlag.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadBelop());
    }

    var maksBidragsbelop = BigDecimal.ZERO;

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

      var resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;

      var tempBarnebidrag = BigDecimal.ZERO;

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
      var underholdskostnad = BigDecimal.ZERO;
      if (grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
          .getBPsAndelUnderholdskostnadProsent()
          .compareTo(BigDecimal.ZERO) > 0 &&
          grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
              .getBPsAndelUnderholdskostnadBelop()
              .compareTo(BigDecimal.ZERO) > 0) {
        underholdskostnad =
            grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
                .getBPsAndelUnderholdskostnadBelop().divide(
                grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad()
                    .getBPsAndelUnderholdskostnadProsent(),
                new MathContext(10, RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(100));
        underholdskostnad = underholdskostnad.setScale(0, RoundingMode.HALF_UP);
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

      // Sjekker mot særregler for barnetillegg BP/BM
      // Dersom beregnet bidrag etter samværsfradrag er lavere enn eventuelt barnetillegg for BP
      // så skal bidraget settes likt barnetillegget. BarnetilleggBP skal ikke taes hensyn til ved delt bosted
      if (!grunnlagBeregningPerBarn.getDeltBosted() &&
          tempBarnebidrag.compareTo(nettoBarnetilleggBP) < 0
          && nettoBarnetilleggBP.compareTo(BigDecimal.ZERO) > 0) {
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
      if (tempBarnebidrag.compareTo(BigDecimal.ZERO) <= 0) {
        tempBarnebidrag = BigDecimal.ZERO;
      }

      if (grunnlagBeregningPeriodisert.getBidragsevne().getBidragsevneBelop().compareTo(
          BigDecimal.ZERO) == 0) {
        resultatkode = ResultatKode.INGEN_EVNE;
      }

      if (grunnlagBeregningPerBarn.getDeltBosted()) {
        resultatkode = ResultatKode.DELT_BOSTED;
      }

      if (grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBarnetErSelvforsorget()) {
        tempBarnebidrag = BigDecimal.ZERO;
        resultatkode = ResultatKode.BARNET_ER_SELVFORSORGET;
      }

      // Bidrag skal avrundes til nærmeste tier
      tempBarnebidrag = tempBarnebidrag.setScale(-1, RoundingMode.HALF_UP);

      resultatBeregningListe
          .add(new ResultatBeregning(grunnlagBeregningPerBarn.getSoknadsbarnPersonId(), tempBarnebidrag, resultatkode, emptyList()));
    }

    return resultatBeregningListe;
  }

  @Override
  public List<ResultatBeregning> beregnVedBarnetilleggForsvaret(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregningPeriodisert.getSjablonListe());

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();

    var barnetilleggForsvaretForsteBarn = sjablonNavnVerdiMap.get(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn());
    var barnetilleggForsvaretOvrigeBarn = sjablonNavnVerdiMap.get(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn());

    var barnetilleggForsvaretPerBarn = BigDecimal.ZERO;

    System.out.println("barnetillegg første barn: " + barnetilleggForsvaretForsteBarn);
    System.out.println("barnetillegg øvrige barn: " + barnetilleggForsvaretOvrigeBarn);

    if (grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe().size() == 1) {
      barnetilleggForsvaretPerBarn = barnetilleggForsvaretForsteBarn;
    } else {
      barnetilleggForsvaretPerBarn = barnetilleggForsvaretForsteBarn
          .add(barnetilleggForsvaretOvrigeBarn)
          .divide(BigDecimal.valueOf(grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe().size()),
              new MathContext(10, RoundingMode.HALF_UP));

      System.out.println("barnetilleggForsvaretPerBarn: " + barnetilleggForsvaretPerBarn.toString());

      barnetilleggForsvaretPerBarn = barnetilleggForsvaretPerBarn.setScale(0, RoundingMode.HALF_UP);
      System.out.println("barnetilleggForsvaretPerBarn etter avrunding: " + barnetilleggForsvaretPerBarn.toString());
    }

    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn :
        grunnlagBeregningPeriodisert.getGrunnlagPerBarnListe()) {

      var barnebidragEtterSamvaersfradrag =
          barnetilleggForsvaretPerBarn.subtract(grunnlagBeregningPerBarn.getSamvaersfradrag());

      var resultatkode = ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET;

      resultatBeregningListe.add(
          new ResultatBeregning(grunnlagBeregningPerBarn.getSoknadsbarnPersonId(), barnebidragEtterSamvaersfradrag, resultatkode,
              byggSjablonResultatListe(sjablonNavnVerdiMap)));
    }

    return resultatBeregningListe;
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<Sjablon> sjablonListe) {
    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP));
    sjablonNavnVerdiMap.put(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP));

    return sjablonNavnVerdiMap;
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap) {
    var sjablonNavnVerdiListe = new ArrayList<SjablonNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) -> sjablonNavnVerdiListe.add(new SjablonNavnVerdi(sjablonNavn, sjablonVerdi)));
    return sjablonNavnVerdiListe;
  }
}
