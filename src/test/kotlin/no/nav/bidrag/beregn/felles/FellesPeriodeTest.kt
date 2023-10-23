package no.nav.bidrag.beregn.felles

import no.nav.bidrag.beregn.felles.bo.Periode
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.time.LocalDate

internal class FellesPeriodeTest : FellesPeriode() {

    @Test
    @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
    fun testPeriodisering() {
        val periodeListeMedJustering = mutableListOf(
            Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
            Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
            Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
            Periode(LocalDate.parse("2019-01-01"), null)
        )
        val periodeListeUtenJustering = mutableListOf(
            Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
            Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
            Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01"))
        )

        mergeSluttperiode(periodeListe = periodeListeMedJustering, datoTil = LocalDate.parse("2020-01-01"))
        mergeSluttperiode(periodeListe = periodeListeUtenJustering, datoTil = LocalDate.parse("2020-01-01"))

        assertAll(
            Executable { Assertions.assertThat(periodeListeMedJustering).isNotNull() },
            Executable { Assertions.assertThat(periodeListeMedJustering.size).isEqualTo(3) },
            Executable { Assertions.assertThat(periodeListeMedJustering[0].datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { Assertions.assertThat(periodeListeMedJustering[0].datoTil).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { Assertions.assertThat(periodeListeMedJustering[1].datoFom).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { Assertions.assertThat(periodeListeMedJustering[1].datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { Assertions.assertThat(periodeListeMedJustering[2].datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { Assertions.assertThat(periodeListeMedJustering[2].datoTil).isNull() },

            Executable { Assertions.assertThat(periodeListeUtenJustering).isNotNull() },
            Executable { Assertions.assertThat(periodeListeUtenJustering.size).isEqualTo(3) },
            Executable { Assertions.assertThat(periodeListeUtenJustering[0].datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { Assertions.assertThat(periodeListeUtenJustering[0].datoTil).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { Assertions.assertThat(periodeListeUtenJustering[1].datoFom).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { Assertions.assertThat(periodeListeUtenJustering[1].datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { Assertions.assertThat(periodeListeUtenJustering[2].datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { Assertions.assertThat(periodeListeUtenJustering[2].datoTil).isEqualTo(LocalDate.parse("2020-01-01")) }
        )
    }
}
