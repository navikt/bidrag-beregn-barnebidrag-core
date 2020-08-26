package no.nav.bidrag.beregn.samvaersfradrag.beregning;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlagPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;

public class SamvaersfradragBeregningImpl implements SamvaersfradragBeregning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      BeregnSamvaersfradragGrunnlagPeriodisert beregnSamvaersfradragGrunnlagPeriodisert) {

    List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

    double belopFradrag = 0.0d;

    sjablonNokkelListe.add(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(),
          beregnSamvaersfradragGrunnlagPeriodisert.getSamvaersklasse()));
    belopFradrag = SjablonUtil
        .hentSjablonverdi(beregnSamvaersfradragGrunnlagPeriodisert.getSjablonListe(), SjablonNavn.SAMVAERSFRADRAG,
              sjablonNokkelListe, SjablonNokkelNavn.ALDER_TOM, beregnSamvaersfradragGrunnlagPeriodisert.getSoknadBarnAlder(),
              SjablonInnholdNavn.FRADRAG_BELOP);

    System.out.println("Samværsfradrag: " + belopFradrag);
    System.out.println("Alder: " + beregnSamvaersfradragGrunnlagPeriodisert.getSoknadBarnAlder());

    return new ResultatBeregning(belopFradrag);

  }

}
