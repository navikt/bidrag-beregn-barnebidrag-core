package no.nav.bidrag.beregn.samvaersfradrag

import no.nav.bidrag.beregn.TestUtil.SAMVÆRSKLASSE_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SOKNADSBARN_REFERANSE
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersklassePeriode
import no.nav.bidrag.beregn.samvaersfradrag.bo.Soknadsbarn
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode.Companion.getInstance
import no.nav.bidrag.domene.enums.beregning.Avvikstype
import no.nav.bidrag.domene.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonNøkkelNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

internal class SamvaersfradragPeriodeTest {
    private val samvaersfradragPeriode = getInstance()

    @Test
    @DisplayName(
        "Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene, det skal lages brudd på søknadsbarnets fødselsmåned",
    )
    fun testToPerioder() {
        // Lag samværsinfo
        val samvaersklassePeriodeListe =
            listOf(
                SamvaersklassePeriode(
                    referanse = SAMVÆRSKLASSE_REFERANSE,
                    samvaersklassePeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-07-01")),
                    samvaersklasse = "02",
                ),
            )

        // Lag sjabloner
        val sjablonPeriodeListe =
            listOf(
                SjablonPeriode(
                    sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2020-06-30")),
                    sjablon =
                    Sjablon(
                        navn = SjablonNavn.SAMVÆRSFRADRAG.navn,
                        nokkelListe =
                        listOf(
                            SjablonNokkel(navn = SjablonNøkkelNavn.SAMVÆRSKLASSE.navn, verdi = "02"),
                            SjablonNokkel(navn = SjablonNøkkelNavn.ALDER_TOM.navn, verdi = "5"),
                        ),
                        innholdListe =
                        listOf(
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, verdi = BigDecimal.ZERO),
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, verdi = BigDecimal.valueOf(8)),
                            SjablonInnhold(navn = SjablonInnholdNavn.FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(727)),
                        ),
                    ),
                ),
                SjablonPeriode(
                    sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = null),
                    sjablon =
                    Sjablon(
                        navn = SjablonNavn.SAMVÆRSFRADRAG.navn,
                        nokkelListe =
                        listOf(
                            SjablonNokkel(navn = SjablonNøkkelNavn.SAMVÆRSKLASSE.navn, verdi = "02"),
                            SjablonNokkel(navn = SjablonNøkkelNavn.ALDER_TOM.navn, verdi = "10"),
                        ),
                        innholdListe =
                        listOf(
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, verdi = BigDecimal.ZERO),
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, verdi = BigDecimal.valueOf(8)),
                            SjablonInnhold(navn = SjablonInnholdNavn.FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(1052)),
                        ),
                    ),
                ),
            )

        val grunnlag =
            BeregnSamvaersfradragGrunnlag(
                beregnDatoFra = LocalDate.parse("2019-07-01"),
                beregnDatoTil = LocalDate.parse("2020-07-01"),
                soknadsbarn = Soknadsbarn(referanse = SOKNADSBARN_REFERANSE, personId = 1, fodselsdato = LocalDate.parse("2014-03-17")),
                samvaersklassePeriodeListe = samvaersklassePeriodeListe,
                sjablonPeriodeListe = sjablonPeriodeListe,
            )

        val resultat = samvaersfradragPeriode.beregnPerioder(grunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2020-04-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.belop.compareTo(BigDecimal.valueOf(727))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2020-04-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isNull() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultat.belop.compareTo(BigDecimal.valueOf(1052))).isZero() },
        )
    }

    @Test
    @DisplayName(
        (
            "Test at det opprettes ny periode ved flere perioder med samværsklasse i input, også ny periode ved barnets bursdag." +
                "Tester også at riktig verdi fpr samværsfradrag brukes når barnets alder passerer en av grensene for alder"
            ),
    )
    fun testFlereSamvaersklasser() {
        // Lag samværsinfo
        val samvaersklassePeriodeListe =
            listOf(
                SamvaersklassePeriode(
                    referanse = SAMVÆRSKLASSE_REFERANSE + "_1",
                    samvaersklassePeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2019-02-01")),
                    samvaersklasse = "01",
                ),
                SamvaersklassePeriode(
                    referanse = SAMVÆRSKLASSE_REFERANSE + "_2",
                    samvaersklassePeriode = Periode(datoFom = LocalDate.parse("2019-02-01"), datoTil = LocalDate.parse("2020-07-01")),
                    samvaersklasse = "02",
                ),
            )

        // Lag sjabloner
        val sjablonPeriodeListe =
            listOf(
                SjablonPeriode(
                    sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2020-06-30")),
                    sjablon =
                    Sjablon(
                        navn = SjablonNavn.SAMVÆRSFRADRAG.navn,
                        nokkelListe =
                        listOf(
                            SjablonNokkel(navn = SjablonNøkkelNavn.SAMVÆRSKLASSE.navn, verdi = "01"),
                            SjablonNokkel(navn = SjablonNøkkelNavn.ALDER_TOM.navn, verdi = "5"),
                        ),
                        innholdListe =
                        listOf(
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, verdi = BigDecimal.valueOf(3)),
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, verdi = BigDecimal.valueOf(3)),
                            SjablonInnhold(navn = SjablonInnholdNavn.FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(219)),
                        ),
                    ),
                ),
                SjablonPeriode(
                    sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2020-06-30")),
                    sjablon =
                    Sjablon(
                        navn = SjablonNavn.SAMVÆRSFRADRAG.navn,
                        nokkelListe =
                        listOf(
                            SjablonNokkel(navn = SjablonNøkkelNavn.SAMVÆRSKLASSE.navn, verdi = "01"),
                            SjablonNokkel(navn = SjablonNøkkelNavn.ALDER_TOM.navn, verdi = "10"),
                        ),
                        innholdListe =
                        listOf(
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, verdi = BigDecimal.valueOf(3)),
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, verdi = BigDecimal.valueOf(3)),
                            SjablonInnhold(navn = SjablonInnholdNavn.FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(318)),
                        ),
                    ),
                ),
                SjablonPeriode(
                    sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2020-06-30")),
                    sjablon =
                    Sjablon(
                        navn = SjablonNavn.SAMVÆRSFRADRAG.navn,
                        nokkelListe =
                        listOf(
                            SjablonNokkel(navn = SjablonNøkkelNavn.SAMVÆRSKLASSE.navn, verdi = "02"),
                            SjablonNokkel(navn = SjablonNøkkelNavn.ALDER_TOM.navn, verdi = "5"),
                        ),
                        innholdListe =
                        listOf(
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, verdi = BigDecimal.ZERO),
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, verdi = BigDecimal.valueOf(8)),
                            SjablonInnhold(navn = SjablonInnholdNavn.FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(727)),
                        ),
                    ),
                ),
                SjablonPeriode(
                    sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = null),
                    sjablon =
                    Sjablon(
                        navn = SjablonNavn.SAMVÆRSFRADRAG.navn,
                        nokkelListe =
                        listOf(
                            SjablonNokkel(navn = SjablonNøkkelNavn.SAMVÆRSKLASSE.navn, verdi = "02"),
                            SjablonNokkel(navn = SjablonNøkkelNavn.ALDER_TOM.navn, verdi = "10"),
                        ),
                        innholdListe =
                        listOf(
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, verdi = BigDecimal.ZERO),
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, verdi = BigDecimal.valueOf(8)),
                            SjablonInnhold(navn = SjablonInnholdNavn.FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(1052)),
                        ),
                    ),
                ),
            )

        val grunnlag =
            BeregnSamvaersfradragGrunnlag(
                beregnDatoFra = LocalDate.parse("2018-07-01"),
                beregnDatoTil = LocalDate.parse("2020-07-01"),
                soknadsbarn = Soknadsbarn(referanse = SOKNADSBARN_REFERANSE, personId = 1, fodselsdato = LocalDate.parse("2014-02-17")),
                samvaersklassePeriodeListe = samvaersklassePeriodeListe,
                sjablonPeriodeListe = sjablonPeriodeListe,
            )

        val resultat = samvaersfradragPeriode.beregnPerioder(grunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.belop.compareTo(BigDecimal.valueOf(219))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2019-03-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultat.belop.compareTo(BigDecimal.valueOf(727))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2019-03-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2020-03-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultat.belop.compareTo(BigDecimal.valueOf(727))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoFom).isEqualTo(LocalDate.parse("2020-03-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoTil).isNull() },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultat.belop.compareTo(BigDecimal.valueOf(1052))).isZero() },
        )
    }

    @Test
    @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
    fun testGrunnlagMedAvvik() {
        // Lag samværsinfo
        val samvaersklassePeriodeListe =
            listOf(
                SamvaersklassePeriode(
                    referanse = SAMVÆRSKLASSE_REFERANSE,
                    samvaersklassePeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-07-01")),
                    samvaersklasse = "02",
                ),
            )

        // Lag sjabloner
        val sjablonPeriodeListe =
            listOf(
                SjablonPeriode(
                    sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2020-06-30")),
                    sjablon =
                    Sjablon(
                        navn = SjablonNavn.SAMVÆRSFRADRAG.navn,
                        nokkelListe =
                        listOf(
                            SjablonNokkel(navn = SjablonNøkkelNavn.SAMVÆRSKLASSE.navn, verdi = "02"),
                            SjablonNokkel(navn = SjablonNøkkelNavn.ALDER_TOM.navn, verdi = "5"),
                        ),
                        innholdListe =
                        listOf(
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, verdi = BigDecimal.ZERO),
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, verdi = BigDecimal.valueOf(8)),
                            SjablonInnhold(navn = SjablonInnholdNavn.FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(727)),
                        ),
                    ),
                ),
                SjablonPeriode(
                    sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = null),
                    sjablon =
                    Sjablon(
                        navn = SjablonNavn.SAMVÆRSFRADRAG.navn,
                        nokkelListe =
                        listOf(
                            SjablonNokkel(navn = SjablonNøkkelNavn.SAMVÆRSKLASSE.navn, verdi = "02"),
                            SjablonNokkel(navn = SjablonNøkkelNavn.ALDER_TOM.navn, verdi = "10"),
                        ),
                        innholdListe =
                        listOf(
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, verdi = BigDecimal.ZERO),
                            SjablonInnhold(navn = SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, verdi = BigDecimal.valueOf(8)),
                            SjablonInnhold(navn = SjablonInnholdNavn.FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(1052)),
                        ),
                    ),
                ),
            )

        val grunnlag =
            BeregnSamvaersfradragGrunnlag(
                beregnDatoFra = LocalDate.parse("2018-07-01"),
                beregnDatoTil = LocalDate.parse("2021-01-01"),
                soknadsbarn = Soknadsbarn(referanse = SOKNADSBARN_REFERANSE, personId = 1, fodselsdato = LocalDate.parse("2014-03-17")),
                samvaersklassePeriodeListe = samvaersklassePeriodeListe,
                sjablonPeriodeListe = sjablonPeriodeListe,
            )

        val avvikListe = samvaersfradragPeriode.validerInput(grunnlag)

        assertAll(
            Executable { assertThat(avvikListe).isNotEmpty() },
            Executable { assertThat(avvikListe).hasSize(3) },
            Executable {
                assertThat(avvikListe[0].avvikTekst)
                    .isEqualTo("Første dato i samvaersklassePeriodeListe (2019-01-01) er etter beregnDatoFom (2018-07-01)")
            },
            Executable { assertThat(avvikListe[0].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(avvikListe[1].avvikTekst)
                    .isEqualTo("Siste dato i samvaersklassePeriodeListe (2020-07-01) er før beregnDatoTil (2021-01-01)")
            },
            Executable { assertThat(avvikListe[1].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
        )
    }
}
