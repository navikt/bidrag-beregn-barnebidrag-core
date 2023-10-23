package no.nav.bidrag.beregn.kostnadsberegnetbidrag

import no.nav.bidrag.beregn.TestUtil.BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE
import no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersfradragPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode.Companion.getInstance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

internal class KostnadsberegnetBidragPeriodeTest {

    private val kostnadsberegnetBidragPeriode = getInstance()

    @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
    @Test
    fun testPeriodisering() {
        val underholdskostnadPeriodeListe = listOf(
            UnderholdskostnadPeriode(
                referanse = UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                underholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-08-01")),
                belop = BigDecimal.valueOf(10000)
            ),
            UnderholdskostnadPeriode(
                referanse = UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                underholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-08-01")),
                belop = BigDecimal.valueOf(1000)
            )
        )

        val bPsAndelUnderholdskostnadPeriodeListe = listOf(
            BPsAndelUnderholdskostnadPeriode(
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                andelProsent = BigDecimal.valueOf(0.20)
            )
        )

        val samvaersfradragPeriodeListe = listOf(
            SamvaersfradragPeriode(
                referanse = SAMVAERSFRADRAG_REFERANSE,
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                belop = BigDecimal.valueOf(100)
            )
        )

        // Sjabloner brukes ikke i beregning av kostnadsberegnet bidrag
        val grunnlag = BeregnKostnadsberegnetBidragGrunnlag(
            beregnDatoFra = LocalDate.parse("2018-07-01"),
            beregnDatoTil = LocalDate.parse("2020-08-01"),
            soknadsbarnPersonId = 1,
            underholdskostnadPeriodeListe = underholdskostnadPeriodeListe,
            bPsAndelUnderholdskostnadPeriodeListe = bPsAndelUnderholdskostnadPeriodeListe,
            samvaersfradragPeriodeListe = samvaersfradragPeriodeListe
        )

        val resultat = kostnadsberegnetBidragPeriode.beregnPerioder(grunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-08-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.belop.compareTo(BigDecimal.valueOf(1900))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-08-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2020-08-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultat.belop.compareTo(BigDecimal.valueOf(100))).isZero() }
        )
    }
}
