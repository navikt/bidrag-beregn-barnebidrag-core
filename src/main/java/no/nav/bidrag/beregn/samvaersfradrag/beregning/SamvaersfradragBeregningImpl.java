package no.nav.bidrag.beregn.samvaersfradrag.beregning;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.FellesBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;

public class SamvaersfradragBeregningImpl extends FellesBeregning implements SamvaersfradragBeregning {

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregning.getSjablonListe(),
        grunnlagBeregning.getSamvaersklasse().getSamvaersklasse(), grunnlagBeregning.getSoknadsbarn().getAlder());

    var belopFradrag = sjablonNavnVerdiMap.get(SjablonNavn.SAMVAERSFRADRAG.getNavn());

    return new ResultatBeregning(belopFradrag, byggSjablonResultatListe(sjablonNavnVerdiMap, grunnlagBeregning.getSjablonListe()));
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<SjablonPeriode> sjablonPeriodeListe, String samvaersklasse, int soknadBarnAlder) {

    var sjablonListe = sjablonPeriodeListe.stream()
        .map(SjablonPeriode::getSjablon)
        .collect(toList());

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Samværsfradrag
    sjablonNavnVerdiMap.put(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.SAMVAERSFRADRAG,
            singletonList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), samvaersklasse)),
            SjablonNokkelNavn.ALDER_TOM, soknadBarnAlder, SjablonInnholdNavn.FRADRAG_BELOP));

    return sjablonNavnVerdiMap;
  }
}
