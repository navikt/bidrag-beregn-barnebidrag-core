package no.nav.bidrag.beregn.felles;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;

public class FellesBeregning {

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  protected List<SjablonPeriodeNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap,
      List<SjablonPeriode> sjablonPeriodeListe) {
    var sjablonPeriodeNavnVerdiListe = new ArrayList<SjablonPeriodeNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) ->
        sjablonPeriodeNavnVerdiListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe, sjablonNavn), sjablonNavn, sjablonVerdi)));
    return sjablonPeriodeNavnVerdiListe.stream().sorted(comparing(SjablonPeriodeNavnVerdi::getNavn)).collect(toList());
  }

  private Periode hentPeriode(List<SjablonPeriode> sjablonPeriodeListe, String sjablonNavn) {
    return sjablonPeriodeListe.stream()
        .filter(sjablonPeriode -> sjablonPeriode.getSjablon().getNavn().equals(modifiserSjablonNavn(sjablonNavn)))
        .map(SjablonPeriode::getPeriode)
        .findFirst()
        .orElse(new Periode(LocalDate.MIN, LocalDate.MAX));
  }

  // Enkelte sjablonnavn må justeres for å finne riktig dato
  private String modifiserSjablonNavn(String sjablonNavn) {
    if (sjablonNavn.equals("UnderholdBeløp")) {
      return "Bidragsevne";
    }
    if (sjablonNavn.equals("BoutgiftBeløp")) {
      return "Bidragsevne";
    }
    if (sjablonNavn.startsWith("Trinnvis")) {
      return "TrinnvisSkattesats";
    }
    return sjablonNavn;
  }
}
