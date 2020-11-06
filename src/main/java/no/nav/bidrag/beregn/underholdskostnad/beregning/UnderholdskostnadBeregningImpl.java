package no.nav.bidrag.beregn.underholdskostnad.beregning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
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

    List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

    // Legger til sjablonverdi for forbruksutgifter basert på barnets alder
    BigDecimal tempBeregnetUnderholdskostnad = SjablonUtil.hentSjablonverdi(
        beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
        SjablonNavn.FORBRUKSUTGIFTER,
        beregnUnderholdskostnadGrunnlagPeriodisert.getSoknadBarnAlder());

    // Sjablonverdi for boutgifter legges til
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        SjablonUtil.hentSjablonverdi(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP));

    // Legger til eventuell støtte til barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnBarnetilsynMedStonad(beregnUnderholdskostnadGrunnlagPeriodisert));

    // Legger til eventuelt netto barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnUnderholdskostnadGrunnlagPeriodisert.getNettoBarnetilsynBelop());

    // Trekker fra forpleiningsutgifter
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.subtract(
        beregnUnderholdskostnadGrunnlagPeriodisert.getForpleiningUtgiftBelop());

    // Setter underholdskostnad til 0 hvis beregnet beløp er under 0
    if (tempBeregnetUnderholdskostnad.compareTo(BigDecimal.valueOf(0)) < 0) {
      tempBeregnetUnderholdskostnad = BigDecimal.valueOf(0.0);
    }

    return new ResultatBeregning(tempBeregnetUnderholdskostnad);
  }

  // Denne metoden beregner for perioder frem til 01.07.2021
  @Override
  public ResultatBeregning beregnMedOrdinaerBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {

    List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

    // Legger til sjablonverdi for forbruksutgifter basert på barnets alder
    BigDecimal tempBeregnetUnderholdskostnad = SjablonUtil.hentSjablonverdi(
        beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
        SjablonNavn.FORBRUKSUTGIFTER,
        beregnUnderholdskostnadGrunnlagPeriodisert.getSoknadBarnAlder());

    // Sjablonverdi for boutgifter legges til
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        SjablonUtil.hentSjablonverdi(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP));

    // Legger til eventuell støtte til barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnBarnetilsynMedStonad(beregnUnderholdskostnadGrunnlagPeriodisert));

    // Legger til eventuelt netto barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnUnderholdskostnadGrunnlagPeriodisert.getNettoBarnetilsynBelop());

    // Trekker fra barnetrygd
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.subtract(
        SjablonUtil.hentSjablonverdi(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP));

    // Trekker fra forpleiningsutgifter
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.subtract(
        beregnUnderholdskostnadGrunnlagPeriodisert.getForpleiningUtgiftBelop());

    // Setter underholdskostnad til 0 hvis beregnet beløp er under 0
    if (tempBeregnetUnderholdskostnad.compareTo(BigDecimal.valueOf(0.0)) < 0) {
      tempBeregnetUnderholdskostnad = BigDecimal.valueOf(0.0);
    }

    return new ResultatBeregning(tempBeregnetUnderholdskostnad);
  }

  // Denne metoden beregner for perioder fra 01.07.2021 og fremover, inkluderer både ordinær og forhøyet barnetrygd
  @Override
  public ResultatBeregning beregnMedForhoyetBarnetrygd(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {

    List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

    // Legger til sjablonverdi for forbruksutgifter basert på barnets alder
    BigDecimal tempBeregnetUnderholdskostnad = SjablonUtil.hentSjablonverdi(
        beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
        SjablonNavn.FORBRUKSUTGIFTER,
        beregnUnderholdskostnadGrunnlagPeriodisert.getSoknadBarnAlder());

    // Sjablonverdi for boutgifter legges til
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        SjablonUtil.hentSjablonverdi(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP));

    // Legger til eventuell støtte til barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnBarnetilsynMedStonad(beregnUnderholdskostnadGrunnlagPeriodisert));

    // Legger til eventuelt netto barnetilsyn
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.add(
        beregnUnderholdskostnadGrunnlagPeriodisert.getNettoBarnetilsynBelop());

    // Trekker fra forhøyet barnetrygd
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.subtract(
        SjablonUtil.hentSjablonverdi(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP));

    // Trekker fra forpleiningsutgifter
    tempBeregnetUnderholdskostnad = tempBeregnetUnderholdskostnad.subtract(
        beregnUnderholdskostnadGrunnlagPeriodisert.getForpleiningUtgiftBelop());

    // Setter underholdskostnad til 0 hvis beregnet beløp er under 0
    if (tempBeregnetUnderholdskostnad.compareTo(BigDecimal.valueOf(0.0)) < 0) {
      tempBeregnetUnderholdskostnad = BigDecimal.valueOf(0.0);
    }

    return new ResultatBeregning(tempBeregnetUnderholdskostnad);
  }


  @Override
  public BigDecimal beregnBarnetilsynMedStonad(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {

    if (beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad() != null) {
//        .getBarnetilsynMedStonadTilsynType() != null) {
      List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();
      sjablonNokkelListe.add(new SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.getNavn(),
          beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad()
              .getBarnetilsynMedStonadTilsynType()));
      sjablonNokkelListe.add(new SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.getNavn(),
          beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad()
              .getBarnetilsynMedStonadStonadType()));

      var tempBarnetilsynBelop =
          SjablonUtil.hentSjablonverdi(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
              SjablonNavn.BARNETILSYN, sjablonNokkelListe, SjablonInnholdNavn.BARNETILSYN_BELOP);

      return tempBarnetilsynBelop;
    } else {
      return BigDecimal.valueOf(0.0d);
    }
  }
}