package no.nav.bidrag.beregn.underholdskostnad.beregning;

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

public class UnderholdskostnadberegningImpl implements Underholdskostnadberegning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {

    // Sjablonverdi for forbruksutgifter basert på barnets alder legges til
    var tempBeregnetUnderholdskostnad = SjablonUtil.hentSjablonverdi(
        beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
        SjablonNavn.FORBRUKSUTGIFTER,
        beregnUnderholdskostnadGrunnlagPeriodisert.getSoknadBarnAlder());

    // Sjablonverdi for boutgifter legges til
    tempBeregnetUnderholdskostnad +=
        SjablonUtil.hentSjablonverdi(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP);

    // Legger til eventuell støtte til barnetilsyn
    // ! Har spurt John om ikke dette i stedet skal trekkes fra underholdskostnaden
    tempBeregnetUnderholdskostnad +=
        beregnBarnetilsynMedStonad(beregnUnderholdskostnadGrunnlagPeriodisert);

    // Legger til eventuelle faktiske utgifter til barnetilsyn
    tempBeregnetUnderholdskostnad +=
        beregnBarnetilsynFaktiskUtgift(beregnUnderholdskostnadGrunnlagPeriodisert);

    // Trekk fra barnetrygd
    tempBeregnetUnderholdskostnad -=
        SjablonUtil.hentSjablonverdi(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP);

    // Legger til forpleiningsutgifter
    tempBeregnetUnderholdskostnad +=
        beregnUnderholdskostnadGrunnlagPeriodisert.getForpleiningUtgiftBelop();

    return new ResultatBeregning(tempBeregnetUnderholdskostnad);
  }


  @Override
  public Double beregnBarnetilsynMedStonad(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {

    if (beregnUnderholdskostnadGrunnlagPeriodisert.getBarnetilsynMedStonad()
        .getBarnetilsynMedStonadTilsynType() != null) {
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
    } else
      return 0.0d;
  }

  @Override
  public Double beregnBarnetilsynFaktiskUtgift(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {

    beregnNettoBarnetilsyn(beregnUnderholdskostnadGrunnlagPeriodisert);

    return 0.0d;
  }

  @Override
  public Double beregnNettoBarnetilsyn(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {
      return null;


  }

}


