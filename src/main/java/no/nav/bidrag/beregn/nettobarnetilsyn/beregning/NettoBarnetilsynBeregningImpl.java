package no.nav.bidrag.beregn.nettobarnetilsyn.beregning;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregningListe;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatPeriode;

public class NettoBarnetilsynBeregningImpl implements NettoBarnetilsynBeregning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregningListe beregn(
      BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert) {

    var resultatBeregningListe = new ArrayList<ResultatBeregning>();
    Double resultatBelop = 0d;

    // Trekker fra barnetrygd
    var tempBeregnetNettoBarnetilsyn =
        SjablonUtil.hentSjablonverdi(beregnNettoBarnetilsynGrunnlagPeriodisert.getSjablonListe(),
            SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP);

    for (FaktiskUtgift faktiskUtgift: beregnNettoBarnetilsynGrunnlagPeriodisert.getFaktiskUtgiftBelopListe()) {
      resultatBelop += faktiskUtgift.getFaktiskUtgiftBelop();

      resultatBeregningListe.add(new ResultatBeregning(faktiskUtgift.getSoknadsbarnPersonId(), resultatBelop));


    }

/*    // Setter NettoBarnetilsyn til 0 hvis beregnet beløp er under 0
    if (tempBeregnetNettoBarnetilsyn.compareTo(0.0) < 0){
      tempBeregnetNettoBarnetilsyn = Double.valueOf(0.0);
    }*/


    return new ResultatBeregningListe(resultatBeregningListe);
  }

}