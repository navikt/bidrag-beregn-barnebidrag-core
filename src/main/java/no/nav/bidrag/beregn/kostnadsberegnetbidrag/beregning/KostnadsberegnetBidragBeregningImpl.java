package no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlagPeriodisert;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning;

public class KostnadsberegnetBidragBeregningImpl implements KostnadsberegnetBidragBeregning{

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      BeregnKostnadsberegnetBidragGrunnlagPeriodisert beregnKostnadsberegnetBidragGrunnlagPeriodisert) {

    double belopFradrag = 0.0d;

    if (beregnKostnadsberegnetBidragGrunnlagPeriodisert.getSamvaersklasse()!= null) {
      sjablonNokkelListe.add(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(),
          beregnKostnadsberegnetBidragGrunnlagPeriodisert.getSamvaersklasse()));
      belopFradrag = SjablonUtil
          .hentSjablonverdi(beregnKostnadsberegnetBidragGrunnlagPeriodisert.getSjablonListe(), SjablonNavn.SAMVAERSFRADRAG,
              sjablonNokkelListe, SjablonNokkelNavn.ALDER_TOM, beregnKostnadsberegnetBidragGrunnlagPeriodisert.getSoknadBarnAlder(),
              SjablonInnholdNavn.FRADRAG_BELOP);

      System.out.println("Samværsfradrag: " + belopFradrag);
      System.out.println("Alder: " + beregnKostnadsberegnetBidragGrunnlagPeriodisert.getSoknadBarnAlder());

    } else {
      belopFradrag = 0.0d;
    }

    BigDecimal resultat = (BigDecimal.valueOf(
        beregnKostnadsberegnetBidragGrunnlagPeriodisert.getUnderholdskostnadBelop())
        .subtract(BigDecimal.valueOf(
        beregnKostnadsberegnetBidragGrunnlagPeriodisert.getUnderholdskostnadBelop())
        .multiply(BigDecimal.valueOf(beregnKostnadsberegnetBidragGrunnlagPeriodisert.getBPsAndelUnderholdskostnadProsent()/100)))
        .subtract(BigDecimal.valueOf(belopFradrag)));

    resultat = resultat.setScale(-1, RoundingMode.HALF_UP);

    return new ResultatBeregning(resultat.doubleValue());

  }

}
