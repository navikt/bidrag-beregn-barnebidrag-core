package no.nav.bidrag.beregn.underholdskostnad.beregning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;

public class UnderholdskostnadBeregningImpl implements UnderholdskostnadBeregning {

  // Denne metoden blir kun kallt for å beregne for barnets fødselsmåned
  @Override
  public ResultatBeregning beregnUtenBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
        beregnUnderholdskostnadGrunnlagPeriodisert.getSoknadBarnAlder(),
        beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad().getBarnetilsynMedStonadTilsynType(),
        beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad().getBarnetilsynMedStonadStonadType(), " "
    );

    // Legger til sjablonverdi for forbruksutgifter basert på barnets alder
    var tempBeregnetUnderholdskostnad = sjablonNavnVerdiMap.get(SjablonNavn.FORBRUKSUTGIFTER.getNavn());

    // Sjablonverdi for boutgifter legges til
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        sjablonNavnVerdiMap.get(SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP.getNavn()));

    // Legger til eventuell støtte til barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnBarnetilsynMedStonad(beregnUnderholdskostnadGrunnlagPeriodisert, sjablonNavnVerdiMap.get(SjablonNavn.BARNETILSYN.getNavn())));

    // Legger til eventuelt netto barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnUnderholdskostnadGrunnlagPeriodisert.getNettoBarnetilsynBelop());

    // Trekker fra forpleiningsutgifter
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.subtract(
        beregnUnderholdskostnadGrunnlagPeriodisert.getForpleiningUtgiftBelop());

    // Setter underholdskostnad til 0 hvis beregnet beløp er under 0
    if (tempBeregnetUnderholdskostnad.compareTo(BigDecimal.ZERO) < 0) {
      tempBeregnetUnderholdskostnad = BigDecimal.ZERO;
    }

    return new ResultatBeregning(tempBeregnetUnderholdskostnad, byggSjablonResultatListe(sjablonNavnVerdiMap));
  }

  // Denne metoden beregner for perioder frem til 01.07.2021
  @Override
  public ResultatBeregning beregnMedOrdinaerBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
        beregnUnderholdskostnadGrunnlagPeriodisert.getSoknadBarnAlder(),
        beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad().getBarnetilsynMedStonadTilsynType(),
        beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad().getBarnetilsynMedStonadStonadType(), "O"
    );

    // Legger til sjablonverdi for forbruksutgifter basert på barnets alder
    var tempBeregnetUnderholdskostnad = sjablonNavnVerdiMap.get(SjablonNavn.FORBRUKSUTGIFTER.getNavn());

    // Sjablonverdi for boutgifter legges til
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        sjablonNavnVerdiMap.get(SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP.getNavn()));

    // Legger til eventuell støtte til barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnBarnetilsynMedStonad(beregnUnderholdskostnadGrunnlagPeriodisert, sjablonNavnVerdiMap.get(SjablonNavn.BARNETILSYN.getNavn())));

    // Legger til eventuelt netto barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnUnderholdskostnadGrunnlagPeriodisert.getNettoBarnetilsynBelop());

    // Trekker fra barnetrygd
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.subtract(
        sjablonNavnVerdiMap.get(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn()));

    // Trekker fra forpleiningsutgifter
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.subtract(
        beregnUnderholdskostnadGrunnlagPeriodisert.getForpleiningUtgiftBelop());

    // Setter underholdskostnad til 0 hvis beregnet beløp er under 0
    if (tempBeregnetUnderholdskostnad.compareTo(BigDecimal.ZERO) < 0) {
      tempBeregnetUnderholdskostnad = BigDecimal.ZERO;
    }

    return new ResultatBeregning(tempBeregnetUnderholdskostnad, byggSjablonResultatListe(sjablonNavnVerdiMap));
  }

  // Denne metoden beregner for perioder fra 01.07.2021 og fremover, inkluderer både ordinær og forhøyet barnetrygd
  @Override
  public ResultatBeregning beregnMedForhoyetBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
        beregnUnderholdskostnadGrunnlagPeriodisert.getSoknadBarnAlder(),
        beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad().getBarnetilsynMedStonadTilsynType(),
        beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad().getBarnetilsynMedStonadStonadType(), "F"
    );

    // Legger til sjablonverdi for forbruksutgifter basert på barnets alder
    var tempBeregnetUnderholdskostnad = sjablonNavnVerdiMap.get(SjablonNavn.FORBRUKSUTGIFTER.getNavn());

    // Sjablonverdi for boutgifter legges til
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        sjablonNavnVerdiMap.get(SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP.getNavn()));

    // Legger til eventuell støtte til barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnBarnetilsynMedStonad(beregnUnderholdskostnadGrunnlagPeriodisert, sjablonNavnVerdiMap.get(SjablonNavn.BARNETILSYN.getNavn())));

    // Legger til eventuelt netto barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnUnderholdskostnadGrunnlagPeriodisert.getNettoBarnetilsynBelop());

    // Trekker fra forhøyet barnetrygd
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.subtract(
        sjablonNavnVerdiMap.get(SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP.getNavn()));

    // Trekker fra forpleiningsutgifter
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.subtract(
        beregnUnderholdskostnadGrunnlagPeriodisert.getForpleiningUtgiftBelop());

    // Setter underholdskostnad til 0 hvis beregnet beløp er under 0
    if (tempBeregnetUnderholdskostnad.compareTo(BigDecimal.ZERO) < 0) {
      tempBeregnetUnderholdskostnad = BigDecimal.ZERO;
    }

    return new ResultatBeregning(tempBeregnetUnderholdskostnad, byggSjablonResultatListe(sjablonNavnVerdiMap));
  }

  private BigDecimal beregnBarnetilsynMedStonad(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert, BigDecimal barnetilsynBelop) {

    if (beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad() != null) {
      return barnetilsynBelop;
    } else {
      return BigDecimal.ZERO;
    }
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<Sjablon> sjablonListe, int soknadBarnAlder, String tilsynType, String stonadType,
      String barnetrygdIndikator) {

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP));
    if (barnetrygdIndikator.equals("O")) {
      sjablonNavnVerdiMap.put(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(),
          SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP));
    } else if (barnetrygdIndikator.equals("F")) {
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

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap) {
    var sjablonNavnVerdiListe = new ArrayList<SjablonNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) -> sjablonNavnVerdiListe.add(new SjablonNavnVerdi(sjablonNavn, sjablonVerdi)));
    return sjablonNavnVerdiListe;
  }
}
