package no.nav.bidrag.beregn.barnebidrag.beregning;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidrag;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.FellesBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class BarnebidragBeregningImpl extends FellesBeregning implements BarnebidragBeregning {

  @Override
  public List<ResultatBeregning> beregn(GrunnlagBeregning grunnlagBeregning) {
    if (grunnlagBeregning.getBarnetilleggForsvaret().getBarnetilleggForsvaretIPeriode()) {
      return beregnVedBarnetilleggForsvaret(grunnlagBeregning);
    } else {
      return beregnOrdinaer(grunnlagBeregning);
    }
  }

  private List<ResultatBeregning> beregnOrdinaer(GrunnlagBeregning grunnlagBeregning) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();

    var totaltBelopUnderholdskostnad = BigDecimal.ZERO;
    for (GrunnlagBeregningPerBarn grunnlag : grunnlagBeregning.getGrunnlagPerBarnListe()) {
      totaltBelopUnderholdskostnad = totaltBelopUnderholdskostnad.add(grunnlag.getBPsAndelUnderholdskostnad().getAndelBelop());
    }

    var totaltBelopLopendeBidrag = BigDecimal.ZERO;
    for (AndreLopendeBidrag andreLopendeBidrag : grunnlagBeregning.getAndreLopendeBidragListe()) {
      totaltBelopLopendeBidrag = totaltBelopLopendeBidrag.add(andreLopendeBidrag.getBidragBelop().add(andreLopendeBidrag.getSamvaersfradragBelop()));
    }

    var maksBidragsbelop = BigDecimal.ZERO;
    var bidragRedusertAvBidragsevne = false;
    if (grunnlagBeregning.getBidragsevne().getBidragsevneBelop().compareTo(grunnlagBeregning.getBidragsevne().getTjuefemProsentInntekt()) < 0) {
      maksBidragsbelop = grunnlagBeregning.getBidragsevne().getBidragsevneBelop();
      bidragRedusertAvBidragsevne = true;
    } else {
      maksBidragsbelop = grunnlagBeregning.getBidragsevne().getTjuefemProsentInntekt();
    }

    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn : grunnlagBeregning.getGrunnlagPerBarnListe()) {

      var resultatkode = ResultatKode.KOSTNADSBEREGNET_BIDRAG;
      var tempBarnebidrag = BigDecimal.ZERO;

      // Beregner netto barnetillegg for BP og BM
      var nettoBarnetilleggBP = BigDecimal.ZERO;
      if (grunnlagBeregningPerBarn.getBarnetilleggBP() != null) {
        nettoBarnetilleggBP = grunnlagBeregningPerBarn.getBarnetilleggBP().getBelop()
            .subtract((grunnlagBeregningPerBarn.getBarnetilleggBP().getBelop()
                .multiply(grunnlagBeregningPerBarn.getBarnetilleggBP().getSkattProsent())
                .divide(BigDecimal.valueOf(100), new MathContext(10, RoundingMode.HALF_UP))));
      }

      var nettoBarnetilleggBM = BigDecimal.ZERO;
      if (grunnlagBeregningPerBarn.getBarnetilleggBM() != null) {
        nettoBarnetilleggBM = grunnlagBeregningPerBarn.getBarnetilleggBM().getBelop()
            .subtract((grunnlagBeregningPerBarn.getBarnetilleggBM().getBelop()
                .multiply(grunnlagBeregningPerBarn.getBarnetilleggBM().getSkattProsent())
                .divide(BigDecimal.valueOf(100), new MathContext(10, RoundingMode.HALF_UP))));
      }

      // Regner ut underholdskostnad ut fra andelsprosent og beløp. Skal ikke gjøres hvis disse er lik 0
      var underholdskostnad = BigDecimal.ZERO;
      if ((grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getAndelProsent().compareTo(BigDecimal.ZERO) > 0) &&
          (grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getAndelBelop().compareTo(BigDecimal.ZERO) > 0)) {
        underholdskostnad = grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getAndelBelop()
            .divide(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getAndelProsent(), new MathContext(10, RoundingMode.HALF_UP))
            .multiply(BigDecimal.valueOf(100))
            .setScale(0, RoundingMode.HALF_UP);
      }

      // Sjekker om totalt bidragsbeløp er større enn bidragsevne eller 25% av månedsinntekt
      if (maksBidragsbelop.compareTo(totaltBelopUnderholdskostnad) < 0) {
        // Bidraget skal begrenses forholdsmessig pga manglende evne/25%-regel
        var andelProsent = grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getAndelBelop()
            .divide(totaltBelopUnderholdskostnad, new MathContext(10, RoundingMode.HALF_UP));

        tempBarnebidrag = maksBidragsbelop.multiply(andelProsent);
        resultatkode = bidragRedusertAvBidragsevne ? ResultatKode.BIDRAG_REDUSERT_AV_EVNE : ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT;

      } else {
        tempBarnebidrag = grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getAndelBelop();
      }

      // Trekker fra samværsfradrag
      tempBarnebidrag = tempBarnebidrag.subtract(grunnlagBeregningPerBarn.getSamvaersfradrag().getBelop());

      // Sjekker mot særregler for barnetillegg BP/BM
      // Dersom beregnet bidrag etter samværsfradrag er lavere enn eventuelt barnetillegg for BP
      // så skal bidraget settes likt barnetillegget minus samværsfradrag. BarnetilleggBP skal ikke taes hensyn til ved delt bosted
      if ((!grunnlagBeregningPerBarn.getDeltBosted().getDeltBostedIPeriode()) &&
          (tempBarnebidrag.compareTo(nettoBarnetilleggBP) < 0) &&
          (nettoBarnetilleggBP.compareTo(BigDecimal.ZERO) > 0)) {
        tempBarnebidrag = nettoBarnetilleggBP.subtract(grunnlagBeregningPerBarn.getSamvaersfradrag().getBelop());
        resultatkode = ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_BP;

        // Regel for barnetilleggBP har ikke slått til. Sjekk om eventuelt barnetillegg for BM skal benyttes.
        // Bidrag settes likt underholdskostnad minus netto barnetilleggBM når beregnet bidrag er høyere enn
        // underholdskostnad minus netto barnetillegg for BM
      } else if (tempBarnebidrag.compareTo(underholdskostnad.subtract(nettoBarnetilleggBM)) > 0) {
        tempBarnebidrag = underholdskostnad
            .subtract(nettoBarnetilleggBM)
            .subtract(grunnlagBeregningPerBarn.getSamvaersfradrag().getBelop());
        resultatkode = ResultatKode.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM;
      }

      // Beløp for bidrag settes til 0 hvis bidraget er utregnet til negativt beløp etter samværsfradrag
      if (tempBarnebidrag.compareTo(BigDecimal.ZERO) <= 0) {
        tempBarnebidrag = BigDecimal.ZERO;
      }

      if (grunnlagBeregning.getBidragsevne().getBidragsevneBelop().compareTo(BigDecimal.ZERO) == 0) {
        resultatkode = ResultatKode.INGEN_EVNE;
      }

      // Hvis barnet har delt bosted og bidrag ikke er redusert under beregningen skal resultatkode settes til DELT_BOSTED
      if (grunnlagBeregningPerBarn.getDeltBosted().getDeltBostedIPeriode()) {
        if (grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getAndelProsent().compareTo(BigDecimal.ZERO) > 0) {
          if (resultatkode.equals(ResultatKode.KOSTNADSBEREGNET_BIDRAG)) {
            resultatkode = ResultatKode.DELT_BOSTED;
          }
        } else {
          resultatkode = ResultatKode.BARNEBIDRAG_IKKE_BEREGNET_DELT_BOSTED;
        }
      }

      if (grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBarnetErSelvforsorget()) {
        tempBarnebidrag = BigDecimal.ZERO;
        resultatkode = ResultatKode.BARNET_ER_SELVFORSORGET;
      }

      // Sjekker om bidragsevne dekker beregnet bidrag pluss løpende bidragsbeløp for andre eksisterende bidragssaker + samværsfradrag,
      // hvis ikke så skal bidragssaken merkes for forholdsmessig fordeling.
      if ((grunnlagBeregning.getBidragsevne().getBidragsevneBelop().compareTo(tempBarnebidrag.add(totaltBelopLopendeBidrag))) < 0 &&
          (!resultatkode.equals(ResultatKode.BARNEBIDRAG_IKKE_BEREGNET_DELT_BOSTED)) &&
          (!resultatkode.equals(ResultatKode.BARNET_ER_SELVFORSORGET)) &&
          (!resultatkode.equals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_BP)) &&
          (!resultatkode.equals(ResultatKode.INGEN_EVNE))) {
        resultatkode = ResultatKode.BEGRENSET_EVNE_FLERE_SAKER_UTFOER_FORHOLDSMESSIG_FORDELING;
      }

      // Bidrag skal avrundes til nærmeste tier
      tempBarnebidrag = tempBarnebidrag.setScale(-1, RoundingMode.HALF_UP);

      resultatBeregningListe.add(new ResultatBeregning(grunnlagBeregningPerBarn.getSoknadsbarnPersonId(), tempBarnebidrag, resultatkode,
          emptyList()));
    }

    return resultatBeregningListe;
  }

  private List<ResultatBeregning> beregnVedBarnetilleggForsvaret(GrunnlagBeregning grunnlagBeregning) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregning.getSjablonListe());

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();

    var totaltBelopLopendeBidrag = BigDecimal.ZERO;
    for (AndreLopendeBidrag andreLopendeBidrag : grunnlagBeregning.getAndreLopendeBidragListe()) {
      totaltBelopLopendeBidrag = totaltBelopLopendeBidrag
          .add(andreLopendeBidrag.getBidragBelop())
          .add(andreLopendeBidrag.getSamvaersfradragBelop());
    }

    var barnetilleggForsvaretForsteBarn = sjablonNavnVerdiMap.get(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn());
    var barnetilleggForsvaretOvrigeBarn = sjablonNavnVerdiMap.get(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn());

    var barnetilleggForsvaretPerBarn = BigDecimal.ZERO;
    if (grunnlagBeregning.getGrunnlagPerBarnListe().size() == 1) {
      barnetilleggForsvaretPerBarn = barnetilleggForsvaretForsteBarn;
    } else {
      barnetilleggForsvaretPerBarn = barnetilleggForsvaretForsteBarn
          .add(barnetilleggForsvaretOvrigeBarn)
          .divide(BigDecimal.valueOf(grunnlagBeregning.getGrunnlagPerBarnListe().size()), new MathContext(10, RoundingMode.HALF_UP))
          .setScale(0, RoundingMode.HALF_UP);
    }

    // Sjekker om bidragsevne dekker beregnet bidrag pluss løpende bidragsbeløp + samværsfradrag,
    // hvis ikke så skal bidragssaken merkes for forholdsmessig fordeling.
    var resultatkode = ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET;
    if (grunnlagBeregning.getBidragsevne().getBidragsevneBelop()
        .compareTo(barnetilleggForsvaretPerBarn
            .multiply(BigDecimal.valueOf(grunnlagBeregning.getGrunnlagPerBarnListe().size()))
            .add(totaltBelopLopendeBidrag)) < 0) {
      resultatkode = ResultatKode.BEGRENSET_EVNE_FLERE_SAKER_UTFOER_FORHOLDSMESSIG_FORDELING;
    }

    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn : grunnlagBeregning.getGrunnlagPerBarnListe()) {
      var barnebidragEtterSamvaersfradrag = barnetilleggForsvaretPerBarn.subtract(grunnlagBeregningPerBarn.getSamvaersfradrag().getBelop());
      resultatBeregningListe.add(new ResultatBeregning(grunnlagBeregningPerBarn.getSoknadsbarnPersonId(), barnebidragEtterSamvaersfradrag,
          resultatkode, byggSjablonResultatListe(sjablonNavnVerdiMap, grunnlagBeregning.getSjablonListe())));
    }
    return resultatBeregningListe;
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<SjablonPeriode> sjablonPeriodeListe) {

    var sjablonListe = sjablonPeriodeListe.stream()
        .map(SjablonPeriode::getSjablon)
        .collect(toList());

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP));
    sjablonNavnVerdiMap.put(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP));

    return sjablonNavnVerdiMap;
  }
}
