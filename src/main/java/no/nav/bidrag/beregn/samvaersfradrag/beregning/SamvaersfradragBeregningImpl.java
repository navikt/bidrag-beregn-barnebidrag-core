package no.nav.bidrag.beregn.samvaersfradrag.beregning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;

public class SamvaersfradragBeregningImpl implements SamvaersfradragBeregning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

    BigDecimal belopFradrag = BigDecimal.ZERO;

    sjablonNokkelListe.add(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(),
          grunnlagBeregningPeriodisert.getSamvaersklasse()));
    belopFradrag = SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(), SjablonNavn.SAMVAERSFRADRAG,
              sjablonNokkelListe, SjablonNokkelNavn.ALDER_TOM, grunnlagBeregningPeriodisert.getSoknadBarnAlder(),
              SjablonInnholdNavn.FRADRAG_BELOP);

    System.out.println("Samværsfradrag: " + belopFradrag);
    System.out.println("Alder: " + grunnlagBeregningPeriodisert.getSoknadBarnAlder());

    return new ResultatBeregning(belopFradrag);

  }

}
