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

    // Legger til sjablonverdi for forbruksutgifter basert på barnets alder
    Double tempBeregnetUnderholdskostnad = SjablonUtil.hentSjablonverdi(
        beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
        SjablonNavn.FORBRUKSUTGIFTER,
        beregnUnderholdskostnadGrunnlagPeriodisert.getSoknadBarnAlder());

    // Sjablonverdi for boutgifter legges til
    tempBeregnetUnderholdskostnad +=
        SjablonUtil.hentSjablonverdi(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP);

    // Legger til eventuell støtte til barnetilsyn
    tempBeregnetUnderholdskostnad +=
        beregnBarnetilsynMedStonad(beregnUnderholdskostnadGrunnlagPeriodisert);

    // Legger til eventuelt netto barnetilsyn
    tempBeregnetUnderholdskostnad +=
        beregnUnderholdskostnadGrunnlagPeriodisert.getNettoBarnetilsynBelop();

    // Trekker fra barnetrygd
    tempBeregnetUnderholdskostnad -=
        SjablonUtil.hentSjablonverdi(beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP);

    // Legger til forpleiningsutgifter
    tempBeregnetUnderholdskostnad +=
        beregnUnderholdskostnadGrunnlagPeriodisert.getForpleiningUtgiftBelop();

    // Setter underholdskostnad til 0 hvis beregnet beløp er under 0
    if (tempBeregnetUnderholdskostnad.compareTo(0.0) < 0){
      tempBeregnetUnderholdskostnad = Double.valueOf(0.0);
    }

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
}