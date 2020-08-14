package no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;

public class BPsAndelUnderholdskostnadBeregningImpl implements BPsAndelUnderholdskostnadBeregning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert){

    Double andel = beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBP() /
        (beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBP() +
         beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBM() +
         beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert.getInntekter().getInntektBB());

    andel = Math.round(andel * 1000.0) / 1000.0;

    return new ResultatBeregning(andel);

  }

}
