package no.nav.bidrag.beregn.underholdskostnad

import no.nav.bidrag.beregn.TestUtil.BARNETILSYN_MED_STONAD_REFERANSE
import no.nav.bidrag.beregn.TestUtil.FORPLEINING_UTGIFT_REFERANSE
import no.nav.bidrag.beregn.TestUtil.NETTO_BARNETILSYN_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SOKNADSBARN_REFERANSE
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.Soknadsbarn
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode.Companion.getInstance
import no.nav.bidrag.domain.enums.AvvikType
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonNokkelNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

internal class UnderholdskostnadPeriodeTest {

    private val underholdskostnadPeriode = getInstance()

    @Test
    @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
    fun testPeriodisering() {
        val resultat = underholdskostnadPeriode.beregnPerioder(lagGrunnlag("2018-07-01", "2020-01-01"))

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(6) },

            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].grunnlag.soknadsbarn.alder).isEqualTo(10) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.belop).isEqualTo(BigDecimal.valueOf(4491)) },

            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].grunnlag.soknadsbarn.alder).isEqualTo(10) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultat.belop).isEqualTo(BigDecimal.valueOf(5602)) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].grunnlag.forpleiningUtgift.belop).isEqualTo(BigDecimal.valueOf(123)) },

            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2019-03-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].grunnlag.soknadsbarn.alder).isEqualTo(10) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultat.belop).isEqualTo(BigDecimal.valueOf(4380)) },

            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoFom).isEqualTo(LocalDate.parse("2019-03-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoTil).isEqualTo(LocalDate.parse("2019-04-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].grunnlag.soknadsbarn.alder).isEqualTo(10) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultat.belop).isEqualTo(BigDecimal.valueOf(4380)) },

            Executable { assertThat(resultat.resultatPeriodeListe[4].periode.datoFom).isEqualTo(LocalDate.parse("2019-04-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].periode.datoTil).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].grunnlag.soknadsbarn.alder).isEqualTo(10) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].resultat.belop).isEqualTo(BigDecimal.valueOf(4491)) },

            Executable { assertThat(resultat.resultatPeriodeListe[5].periode.datoFom).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[5].periode.datoTil).isNull() },
            Executable { assertThat(resultat.resultatPeriodeListe[5].grunnlag.soknadsbarn.alder).isEqualTo(11) },
            Executable { assertThat(resultat.resultatPeriodeListe[5].resultat.belop).isEqualTo(BigDecimal.valueOf(5477)) }
        )
    }

    @Test
    @DisplayName("Test at barnetrygd ikke trekkes fra i barnets fødselsmåned, og at barnetrygd trekkes fra i påfølgende periode som normalt")
    fun testAtBarnetrygdIkkeTrekkesFraIFodselsmaaned() {
        val barnetilsynMedStonadPeriodeListe = listOf(
            BarnetilsynMedStonadPeriode(
                referanse = BARNETILSYN_MED_STONAD_REFERANSE,
                barnetilsynMedStonadPeriode = Periode(datoFom = LocalDate.parse("2019-03-01"), datoTil = null),
                tilsynType = "DU",
                stonadType = "64"
            )
        )

        val nettoBarnetilsynPeriodeListe = listOf(
            NettoBarnetilsynPeriode(
                referanse = NETTO_BARNETILSYN_REFERANSE,
                nettoBarnetilsynPeriode = Periode(datoFom = LocalDate.parse("2018-04-01"), datoTil = null),
                belop = BigDecimal.valueOf(2000)
            )
        )

        val forpleiningUtgiftPeriodeListe = listOf(
            ForpleiningUtgiftPeriode(
                referanse = FORPLEINING_UTGIFT_REFERANSE,
                forpleiningUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-02-01"), datoTil = LocalDate.parse("2020-10-01")),
                belop = BigDecimal.ZERO
            )
        )

        val sjablonPeriodeListe = listOf(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1054)))
                )
            ),
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2021-07-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1354)))
                )
            )
        )

        val grunnlag = BeregnUnderholdskostnadGrunnlag(
            beregnDatoFra = LocalDate.parse("2019-07-01"),
            beregnDatoTil = LocalDate.parse("2020-07-01"),
            soknadsbarn = Soknadsbarn(referanse = SOKNADSBARN_REFERANSE, personId = 1, fodselsdato = LocalDate.parse("2019-07-29")),
            barnetilsynMedStonadPeriodeListe = barnetilsynMedStonadPeriodeListe,
            nettoBarnetilsynPeriodeListe = nettoBarnetilsynPeriodeListe,
            forpleiningUtgiftPeriodeListe = forpleiningUtgiftPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )

        val resultat = underholdskostnadPeriode.beregnPerioder(grunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-08-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.belop.compareTo(BigDecimal.valueOf(2000))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-08-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isNull() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultat.belop.compareTo(BigDecimal.valueOf(2000 - 1054))).isZero() }
        )
    }

    @Test
    @DisplayName("Test forhøyet barnetrygd. Barnet fyller 6 år etter at forhøyet barnetrygd er innført, men fødselsdato settes til 1/7. Gir ikke forhøyet barnetrygd")
    fun testForhoyetBarneTrygd1() {
        val barnetilsynMedStonadPeriodeListe = listOf(
            BarnetilsynMedStonadPeriode(
                referanse = BARNETILSYN_MED_STONAD_REFERANSE,
                barnetilsynMedStonadPeriode = Periode(datoFom = LocalDate.parse("2019-03-01"), datoTil = null),
                tilsynType = "DU",
                stonadType = "64"
            )
        )

        val nettoBarnetilsynPeriodeListe = listOf(
            NettoBarnetilsynPeriode(
                referanse = NETTO_BARNETILSYN_REFERANSE,
                nettoBarnetilsynPeriode = Periode(datoFom = LocalDate.parse("2018-04-01"), datoTil = null),
                belop = BigDecimal.valueOf(2000)
            )
        )

        val forpleiningUtgiftPeriodeListe = listOf(
            ForpleiningUtgiftPeriode(
                referanse = FORPLEINING_UTGIFT_REFERANSE,
                forpleiningUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-02-01"), datoTil = null),
                belop = BigDecimal.ZERO
            )
        )

        val sjablonPeriodeListe = listOf(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(
                            navn = SjablonInnholdNavn.SJABLON_VERDI.navn,
                            verdi = BigDecimal.valueOf(1054)
                        )
                    )
                )
            ),
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2021-07-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(
                            navn = SjablonInnholdNavn.SJABLON_VERDI.navn,
                            verdi = BigDecimal.valueOf(1354)
                        )
                    )
                )
            )
        )

        val grunnlag = BeregnUnderholdskostnadGrunnlag(
            beregnDatoFra = LocalDate.parse("2021-03-01"),
            beregnDatoTil = LocalDate.parse("2022-07-01"),
            soknadsbarn = Soknadsbarn(referanse = SOKNADSBARN_REFERANSE, personId = 1, fodselsdato = LocalDate.parse("2015-10-29")),
            barnetilsynMedStonadPeriodeListe = barnetilsynMedStonadPeriodeListe,
            nettoBarnetilsynPeriodeListe = nettoBarnetilsynPeriodeListe,
            forpleiningUtgiftPeriodeListe = forpleiningUtgiftPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )

        val resultat = underholdskostnadPeriode.beregnPerioder(grunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(2) }, // Første periode er før innføring av forhøyet barnetrygd -> ordinær barnetrygd brukes

            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2021-03-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2021-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.belop.compareTo(BigDecimal.valueOf(2000 - 1054))).isZero() }, // Forhøyet barnetrygd er innført, barnet regnes som 6 år fordi fødselsdato settes til 01.07. -> ordinær barnetrygd brukes

            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2021-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isNull() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultat.belop.compareTo(BigDecimal.valueOf(2000 - 1054))).isZero() }
        )
    }

    @Test
    @DisplayName("Test forhøyet barnetrygd. Barnet fyller 6 år året etter at forhøyet barnetrygd er innført")
    fun testForhoyetBarneTrygd2() {
        val barnetilsynMedStonadPeriodeListe = listOf(
            BarnetilsynMedStonadPeriode(
                referanse = BARNETILSYN_MED_STONAD_REFERANSE,
                barnetilsynMedStonadPeriode = Periode(datoFom = LocalDate.parse("2019-03-01"), datoTil = null),
                tilsynType = "DU",
                stonadType = "64"
            )
        )

        val nettoBarnetilsynPeriodeListe = listOf(
            NettoBarnetilsynPeriode(
                referanse = NETTO_BARNETILSYN_REFERANSE,
                nettoBarnetilsynPeriode = Periode(datoFom = LocalDate.parse("2018-04-01"), datoTil = null),
                belop = BigDecimal.valueOf(2000)
            )
        )

        val forpleiningUtgiftPeriodeListe = listOf(
            ForpleiningUtgiftPeriode(
                referanse = FORPLEINING_UTGIFT_REFERANSE,
                forpleiningUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-02-01"), datoTil = null),
                belop = BigDecimal.ZERO
            )
        )

        val sjablonPeriodeListe = listOf(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(
                            navn = SjablonInnholdNavn.SJABLON_VERDI.navn,
                            verdi = BigDecimal.valueOf(1054)
                        )
                    )
                )
            ),
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2021-07-01"), datoTil = null),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(
                        SjablonInnhold(
                            navn = SjablonInnholdNavn.SJABLON_VERDI.navn,
                            verdi = BigDecimal.valueOf(1354)
                        )
                    )
                )
            )
        )

        val grunnlag = BeregnUnderholdskostnadGrunnlag(
            beregnDatoFra = LocalDate.parse("2021-03-01"),
            beregnDatoTil = LocalDate.parse("2023-07-01"),
            soknadsbarn = Soknadsbarn(referanse = SOKNADSBARN_REFERANSE, personId = 1, fodselsdato = LocalDate.parse("2016-10-29")),
            barnetilsynMedStonadPeriodeListe = barnetilsynMedStonadPeriodeListe,
            nettoBarnetilsynPeriodeListe = nettoBarnetilsynPeriodeListe,
            forpleiningUtgiftPeriodeListe = forpleiningUtgiftPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )

        val resultat = underholdskostnadPeriode.beregnPerioder(grunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(3) }, // Første periode er før innføring av forhøyet barnetrygd -> ordinær barnetrygd brukes

            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2021-03-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2021-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.belop.compareTo(BigDecimal.valueOf(2000 - 1054))).isZero() }, // Forhøyet barnetrygd er innført, barnet har ikke fylt 6 år -> forhøyet barnetrygd brukes

            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2021-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2022-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultat.belop.compareTo(BigDecimal.valueOf(2000 - 1354))).isZero() }, // Forhøyet barnetrygd er innført, barnet regnes som 6 år fordi fødselsdato settes til 01.07. -> ordinær barnetrygd brukes

            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2022-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoTil).isNull() },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultat.belop.compareTo(BigDecimal.valueOf(2000 - 1054))).isZero() }
        )
    }

    @Test
    @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
    fun testGrunnlagMedAvvik() {
        val avvikListe = underholdskostnadPeriode.validerInput(lagGrunnlag("2015-01-01", "2021-01-01"))

        assertAll(
            Executable { assertThat(avvikListe).isNotEmpty() },
            Executable { assertThat(avvikListe).hasSize(4) },

            Executable { assertThat(avvikListe[0].avvikTekst).isEqualTo("Første dato i barnetilsynMedStonadPeriodeListe (2018-01-01) er etter beregnDatoFra (2015-01-01)") },
            Executable { assertThat(avvikListe[0].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },

            Executable { assertThat(avvikListe[1].avvikTekst).isEqualTo("Siste dato i barnetilsynMedStonadPeriodeListe (2020-12-01) er før beregnDatoTil (2021-01-01)") },
            Executable { assertThat(avvikListe[1].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },

            Executable { assertThat(avvikListe[2].avvikTekst).isEqualTo("Første dato i nettoBarnetilsynPeriodeListe (2016-01-01) er etter beregnDatoFra (2015-01-01)") },
            Executable { assertThat(avvikListe[2].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },

            Executable { assertThat(avvikListe[3].avvikTekst).isEqualTo("Siste dato i forpleiningUtgiftPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)") },
            Executable { assertThat(avvikListe[3].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) }
        )
    }

    private fun lagGrunnlag(beregnDatoFra: String, beregnDatoTil: String) =
        BeregnUnderholdskostnadGrunnlag(
            beregnDatoFra = LocalDate.parse(beregnDatoFra),
            beregnDatoTil = LocalDate.parse(beregnDatoTil),
            soknadsbarn = Soknadsbarn(referanse = SOKNADSBARN_REFERANSE, personId = 1, fodselsdato = LocalDate.parse("2008-01-29")),
            barnetilsynMedStonadPeriodeListe = lagBarnetilsynMedStonadGrunnlag(),
            nettoBarnetilsynPeriodeListe = lagNettoBarnetilsynGrunnlag(),
            forpleiningUtgiftPeriodeListe = lagForpleiningUtgiftGrunnlag(),
            sjablonPeriodeListe = lagSjablonGrunnlag()
        )

    private fun lagBarnetilsynMedStonadGrunnlag() = listOf(
        BarnetilsynMedStonadPeriode(
            referanse = BARNETILSYN_MED_STONAD_REFERANSE + "_1",
            barnetilsynMedStonadPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2018-07-01")),
            tilsynType = "DU",
            stonadType = "64"
        ),
        BarnetilsynMedStonadPeriode(
            referanse = BARNETILSYN_MED_STONAD_REFERANSE + "_2",
            barnetilsynMedStonadPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-01-01")),
            tilsynType = "DU",
            stonadType = "64"
        ),
        BarnetilsynMedStonadPeriode(
            referanse = BARNETILSYN_MED_STONAD_REFERANSE + "_3",
            barnetilsynMedStonadPeriode = Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-03-01")),
            tilsynType = "DU",
            stonadType = "64"
        ),
        BarnetilsynMedStonadPeriode(
            referanse = BARNETILSYN_MED_STONAD_REFERANSE + "_4",
            barnetilsynMedStonadPeriode = Periode(datoFom = LocalDate.parse("2019-03-01"), datoTil = LocalDate.parse("2019-07-01")),
            tilsynType = "DU",
            stonadType = "64"
        ),
        BarnetilsynMedStonadPeriode(
            referanse = BARNETILSYN_MED_STONAD_REFERANSE + "_5",
            barnetilsynMedStonadPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-12-01")),
            tilsynType = "DU",
            stonadType = "64"
        )
    )

    private fun lagNettoBarnetilsynGrunnlag() = listOf(
        NettoBarnetilsynPeriode(
            referanse = NETTO_BARNETILSYN_REFERANSE + "_1",
            nettoBarnetilsynPeriode = Periode(datoFom = LocalDate.parse("2016-01-01"), datoTil = LocalDate.parse("2019-01-01")),
            belop = BigDecimal.valueOf(555)
        ),
        NettoBarnetilsynPeriode(
            referanse = NETTO_BARNETILSYN_REFERANSE + "_2",
            nettoBarnetilsynPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-04-01")),
            belop = BigDecimal.valueOf(1666)
        ),
        NettoBarnetilsynPeriode(
            referanse = NETTO_BARNETILSYN_REFERANSE + "_3",
            nettoBarnetilsynPeriode = Periode(datoFom = LocalDate.parse("2019-04-01"), datoTil = LocalDate.parse("2020-01-01")),
            belop = BigDecimal.valueOf(1777)
        ),
        NettoBarnetilsynPeriode(
            referanse = NETTO_BARNETILSYN_REFERANSE + "_4",
            nettoBarnetilsynPeriode = Periode(datoFom = LocalDate.parse("2020-01-01"), datoTil = null),
            belop = BigDecimal.valueOf(1)
        )
    )

    private fun lagForpleiningUtgiftGrunnlag() = listOf(
        ForpleiningUtgiftPeriode(
            referanse = FORPLEINING_UTGIFT_REFERANSE + "_1",
            forpleiningUtgiftPeriode = Periode(datoFom = LocalDate.parse("2001-01-01"), datoTil = LocalDate.parse("2017-01-01")),
            belop = BigDecimal.valueOf(123)
        ),
        ForpleiningUtgiftPeriode(
            referanse = FORPLEINING_UTGIFT_REFERANSE + "_2",
            forpleiningUtgiftPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2019-02-01")),
            belop = BigDecimal.valueOf(123)
        ),
        ForpleiningUtgiftPeriode(
            referanse = FORPLEINING_UTGIFT_REFERANSE + "_3",
            forpleiningUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-02-01"), datoTil = LocalDate.parse("2020-01-01")),
            belop = BigDecimal.valueOf(1345)
        )
    )

    private fun lagSjablonGrunnlag() = listOf(

        // Sjablontall
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2018-12-31")),
            sjablon = Sjablon(
                navn = SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1054)))
            )
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = null),
            sjablon = Sjablon(
                navn = SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1054)))
            )
        ),

        // Forbruksutgifter
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon = Sjablon(
                navn = SjablonNavn.FORBRUKSUTGIFTER.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNokkelNavn.ALDER_TOM.navn, verdi = "18")),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, verdi = BigDecimal.valueOf(6985)))
            )
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon = Sjablon(
                navn = SjablonNavn.FORBRUKSUTGIFTER.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNokkelNavn.ALDER_TOM.navn, verdi = "5")),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, verdi = BigDecimal.valueOf(3661)))
            )
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon = Sjablon(
                navn = SjablonNavn.FORBRUKSUTGIFTER.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNokkelNavn.ALDER_TOM.navn, verdi = "99")),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, verdi = BigDecimal.valueOf(6985)))
            )
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon = Sjablon(
                navn = SjablonNavn.FORBRUKSUTGIFTER.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNokkelNavn.ALDER_TOM.navn, verdi = "10")),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, verdi = BigDecimal.valueOf(5113)))
            )
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon = Sjablon(
                navn = SjablonNavn.FORBRUKSUTGIFTER.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNokkelNavn.ALDER_TOM.navn, verdi = "14")),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, verdi = BigDecimal.valueOf(6099)))
            )
        )
    )
}
