package no.nav.bidrag.beregn.samvaersfradrag.beregning;

import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning;

public class SamvaersfradragBeregningImpl implements SamvaersfradragBeregning {

  @Override
  public ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregningPeriodisert.getSjablonListe(), grunnlagBeregningPeriodisert.getSamvaersklasse(),
        grunnlagBeregningPeriodisert.getSoknadBarnAlder());

    var belopFradrag = sjablonNavnVerdiMap.get(SjablonNavn.SAMVAERSFRADRAG.getNavn());

//    System.out.println("Samværsfradrag: " + belopFradrag);
//    System.out.println("Alder: " + grunnlagBeregningPeriodisert.getSoknadBarnAlder());

    return new ResultatBeregning(belopFradrag, byggSjablonResultatListe(sjablonNavnVerdiMap));
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<Sjablon> sjablonListe, String samvaersklasse, int soknadBarnAlder) {

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Samværsfradrag
    sjablonNavnVerdiMap.put(SjablonNavn.SAMVAERSFRADRAG.getNavn(), SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.SAMVAERSFRADRAG,
        singletonList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), samvaersklasse)), SjablonNokkelNavn.ALDER_TOM, soknadBarnAlder,
        SjablonInnholdNavn.FRADRAG_BELOP));

    return sjablonNavnVerdiMap;
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap) {
    var sjablonNavnVerdiListe = new ArrayList<SjablonNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) -> sjablonNavnVerdiListe.add(new SjablonNavnVerdi(sjablonNavn, sjablonVerdi)));
    return sjablonNavnVerdiListe;
  }
}
