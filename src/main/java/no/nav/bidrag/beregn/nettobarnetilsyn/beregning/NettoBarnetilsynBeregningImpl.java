package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;

public class NettoBarnetilsynBeregningImpl implements NettoBarnetilsynBeregning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert) {

    // Legger til sjablonverdi for forbruksutgifter basert på barnets alder
    Double tempBeregnetNettoBarnetilsyn = SjablonUtil.hentSjablonverdi(
        beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
        SjablonNavn.FORBRUKSUTGIFTER,
        beregnNettoBarnetilsynGrunnlagPeriodisert.getSoknadBarnAlder());

    // Sjablonverdi for boutgifter legges til
    tempBeregnetNettoBarnetilsyn +=
        SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP);


    // Legger til eventuelt netto barnetilsyn
    tempBeregnetNettoBarnetilsyn +=
        beregnNettoBarnetilsynGrunnlagPeriodisert.getNettoBarnetilsynBelop();

    // Trekker fra barnetrygd
    tempBeregnetNettoBarnetilsyn -=
        SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP);


    // Setter NettoBarnetilsyn til 0 hvis beregnet beløp er under 0
    if (tempBeregnetNettoBarnetilsyn.compareTo(0.0) < 0){
      tempBeregnetNettoBarnetilsyn = Double.valueOf(0.0);
    }

    return new ResultatBeregning(tempBeregnetNettoBarnetilsyn);
  }

}