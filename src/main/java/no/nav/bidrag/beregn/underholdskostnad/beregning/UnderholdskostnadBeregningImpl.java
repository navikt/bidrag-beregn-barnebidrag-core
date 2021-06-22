package no.nav.bidrag.beregn.underholdskostnad.beregning;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.FellesBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.underholdskostnad.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;

public class UnderholdskostnadBeregningImpl extends FellesBeregning implements UnderholdskostnadBeregning {

  protected static final String ORDINAER_BARNETRYGD = "O";
  protected static final String FORHOYET_BARNETRYGD = "F";

  // Uten barnetrygd: Blir kun kalt for å beregne for barnets fødselsmåned
  // Ordinær barnetrygd: Beregner for perioder frem til 01.07.2021
  // Forhøyet barnetrygd: Beregner for perioder fra 01.07.2021 og fremover, inkluderer både ordinær og forhøyet barnetrygd
  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning, String barnetrygdIndikator) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregning.getSjablonListe(),
        grunnlagBeregning.getSoknadsbarn().getAlder(), grunnlagBeregning.getBarnetilsynMedStonad().getTilsynType(),
        grunnlagBeregning.getBarnetilsynMedStonad().getStonadType(), barnetrygdIndikator);

    var beregnetUnderholdskostnad = sjablonNavnVerdiMap.get(SjablonNavn.FORBRUKSUTGIFTER.getNavn()) //Forbruksutgifter
        .add(sjablonNavnVerdiMap.get(SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP.getNavn())) //Boutgifter
        .add(sjablonNavnVerdiMap.get(SjablonNavn.BARNETILSYN.getNavn())) //Barnetilsyn
        .add(grunnlagBeregning.getNettoBarnetilsyn().getBelop()) //Netto barnetilsyn
        .subtract(hentBarnetrygdBelop(sjablonNavnVerdiMap, barnetrygdIndikator)) //Barnetrygd
        .subtract(grunnlagBeregning.getForpleiningUtgift().getBelop()); //Forpleiningsutgifter

    // Setter underholdskostnad til 0 hvis beregnet beløp er under 0
    if (beregnetUnderholdskostnad.compareTo(BigDecimal.ZERO) < 0) {
      beregnetUnderholdskostnad = BigDecimal.ZERO;
    }

    return new ResultatBeregning(beregnetUnderholdskostnad, byggSjablonResultatListe(sjablonNavnVerdiMap, grunnlagBeregning.getSjablonListe()));
  }

  private BigDecimal hentBarnetrygdBelop(Map<String, BigDecimal> sjablonNavnVerdiMap, String barnetrygdIndikator) {
    return switch (barnetrygdIndikator) {
      case ORDINAER_BARNETRYGD -> sjablonNavnVerdiMap.get(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn());
      case FORHOYET_BARNETRYGD -> sjablonNavnVerdiMap.get(SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP.getNavn());
      default -> BigDecimal.ZERO;
    };
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<SjablonPeriode> sjablonPeriodeListe, int soknadBarnAlder, String tilsynType,
      String stonadType, String barnetrygdIndikator) {

    var sjablonListe = sjablonPeriodeListe.stream()
        .map(SjablonPeriode::getSjablon)
        .collect(toList());

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP));
    if (barnetrygdIndikator.equals(ORDINAER_BARNETRYGD)) {
      sjablonNavnVerdiMap.put(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(),
          SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP));
    } else if (barnetrygdIndikator.equals(FORHOYET_BARNETRYGD)) {
      sjablonNavnVerdiMap.put(SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP.getNavn(),
          SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP));
    }

    // Forbruksutgifter
    sjablonNavnVerdiMap
        .put(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.FORBRUKSUTGIFTER, soknadBarnAlder));

    // Barnetilsyn
    List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();
    sjablonNokkelListe.add(new SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.getNavn(), tilsynType));
    sjablonNokkelListe.add(new SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.getNavn(), stonadType));
    sjablonNavnVerdiMap.put(SjablonNavn.BARNETILSYN.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.BARNETILSYN, sjablonNokkelListe, SjablonInnholdNavn.BARNETILSYN_BELOP));

    return sjablonNavnVerdiMap;
  }
}
