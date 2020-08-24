package no.nav.bidrag.beregn.barnebidrag.beregning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlagPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;

public class BarnebidragBeregningImpl implements BarnebidragBeregning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      BeregnBarnebidragGrunnlagPeriodisert beregnBarnebidragGrunnlagPeriodisert) {

    double belopFradrag = 0.0d;

    if (beregnBarnebidragGrunnlagPeriodisert.getSamvaersklasse()!= null) {
      sjablonNokkelListe.add(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(),
          beregnBarnebidragGrunnlagPeriodisert.getSamvaersklasse()));
      belopFradrag = SjablonUtil
          .hentSjablonverdi(beregnBarnebidragGrunnlagPeriodisert.getSjablonListe(), SjablonNavn.SAMVAERSFRADRAG,
              sjablonNokkelListe, SjablonNokkelNavn.ALDER_TOM, beregnBarnebidragGrunnlagPeriodisert.getSoknadBarnAlder(),
              SjablonInnholdNavn.FRADRAG_BELOP);

      System.out.println("Samværsfradrag: " + belopFradrag);
      System.out.println("Alder: " + beregnBarnebidragGrunnlagPeriodisert.getSoknadBarnAlder());

    } else {
      belopFradrag = 0.0d;
    }

    BigDecimal resultat = (BigDecimal.valueOf(
        beregnBarnebidragGrunnlagPeriodisert.getUnderholdskostnadBelop())
        .subtract(BigDecimal.valueOf(
        beregnBarnebidragGrunnlagPeriodisert.getUnderholdskostnadBelop())
        .multiply(BigDecimal.valueOf(
            beregnBarnebidragGrunnlagPeriodisert.getBPsAndelUnderholdskostnadProsent()/100)))
        .subtract(BigDecimal.valueOf(belopFradrag)));

    resultat = resultat.setScale(-1, RoundingMode.HALF_UP);

    return new ResultatBeregning(resultat.doubleValue());

  }

}
