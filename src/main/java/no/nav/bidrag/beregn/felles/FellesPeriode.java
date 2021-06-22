package no.nav.bidrag.beregn.felles;

import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Periode;

public abstract class FellesPeriode {

  protected void mergeSluttperiode(List<Periode> periodeListe, LocalDate datoTil) {
    if (periodeListe.size() > 1) {
      if ((periodeListe.get(periodeListe.size() - 2).getDatoTil().equals(datoTil)) &&
          (periodeListe.get(periodeListe.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(periodeListe.get(periodeListe.size() - 2).getDatoFom(), null);
        periodeListe.remove(periodeListe.size() - 1);
        periodeListe.remove(periodeListe.size() - 1);
        periodeListe.add(nyPeriode);
      }
    }
  }
}