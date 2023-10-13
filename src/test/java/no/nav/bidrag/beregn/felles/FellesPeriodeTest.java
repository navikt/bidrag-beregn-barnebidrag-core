package no.nav.bidrag.beregn.felles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Periode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FellesPeriodeTest extends FellesPeriode {

  @Test
  @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  void testPeriodisering() {

    List<Periode> periodeListeMedJustering = new ArrayList<>();
    periodeListeMedJustering.add(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")));
    periodeListeMedJustering.add(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")));
    periodeListeMedJustering.add(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")));
    periodeListeMedJustering.add(new Periode(LocalDate.parse("2019-01-01"), null));

    List<Periode> periodeListeUtenJustering = new ArrayList<>();
    periodeListeUtenJustering.add(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")));
    periodeListeUtenJustering.add(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")));
    periodeListeUtenJustering.add(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")));

    mergeSluttperiode(periodeListeMedJustering, LocalDate.parse("2020-01-01"));
    mergeSluttperiode(periodeListeUtenJustering, LocalDate.parse("2020-01-01"));

    assertAll(
        () -> assertThat(periodeListeMedJustering).isNotNull(),
        () -> assertThat(periodeListeMedJustering.size()).isEqualTo(3),
        () -> assertThat(periodeListeMedJustering.get(0).getDatoFom()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(periodeListeMedJustering.get(0).getDatoTil()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(periodeListeMedJustering.get(1).getDatoFom()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(periodeListeMedJustering.get(1).getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(periodeListeMedJustering.get(2).getDatoFom()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(periodeListeMedJustering.get(2).getDatoTil()).isNull(),

        () -> assertThat(periodeListeUtenJustering).isNotNull(),
        () -> assertThat(periodeListeUtenJustering.size()).isEqualTo(3),
        () -> assertThat(periodeListeUtenJustering.get(0).getDatoFom()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(periodeListeUtenJustering.get(0).getDatoTil()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(periodeListeUtenJustering.get(1).getDatoFom()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(periodeListeUtenJustering.get(1).getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(periodeListeUtenJustering.get(2).getDatoFom()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(periodeListeUtenJustering.get(2).getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01"))
    );
  }
}
