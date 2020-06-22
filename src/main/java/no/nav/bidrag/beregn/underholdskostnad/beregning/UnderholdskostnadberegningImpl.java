package no.nav.bidrag.beregn.underholdskostnad.beregning;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;

public class UnderholdskostnadberegningImpl implements Underholdskostnadberegning {
  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert){

    var beregnetTilsynsutgift =  beregnTilsynsutgift(beregnUnderholdskostnadGrunnlagPeriodisert);

    System.out.println("Start beregning av underholdskostnad");

    var tempBeregnetUnderholdskostnad = SjablonUtil.hentSjablonverdi(
        beregnUnderholdskostnadGrunnlagPeriodisert.getSjablonListe(),
        SjablonNavn.FORBRUKSUTGIFTER,
        beregnUnderholdskostnadGrunnlagPeriodisert.getSoknadBarnAlder());


    return new ResultatBeregning(tempBeregnetUnderholdskostnad);
  }

  @Override
  public Double beregnTilsynsutgift(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {

    return null;
  }

  @Override
  public Double beregnNettoBarnetilsyn(
      BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert) {
    return null;
  }
}

