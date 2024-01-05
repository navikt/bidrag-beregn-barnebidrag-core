package no.nav.bidrag.beregn.nettobarnetilsyn

import no.nav.bidrag.beregn.TestUtil.FAKTISK_UTGIFT_REFERANSE
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgiftPeriode
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode.Companion.getInstance
import no.nav.bidrag.domene.enums.beregning.Avvikstype
import no.nav.bidrag.domene.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonNøkkelNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

internal class NettoBarnetilsynPeriodeTest {
    private val nettoBarnetilsynPeriode = getInstance()

    @Test
    @DisplayName("Test av periodisering for ett barn. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
    fun testPeriodiseringEttBarn() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2008-03-17"),
                    belop = BigDecimal.valueOf(1000),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-08-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2008-03-17"),
                    belop = BigDecimal.valueOf(2000),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2008-03-17"),
                    belop = BigDecimal.valueOf(5000),
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2018-07-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(3) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[0].grunnlag.faktiskUtgiftListe[0].belop.compareTo(BigDecimal.valueOf(1000)),
                ).isZero()
            },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.valueOf(750))).isZero() },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[1].grunnlag.faktiskUtgiftListe[0].belop.compareTo(BigDecimal.valueOf(2000)),
                ).isZero()
            },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].belop.compareTo(BigDecimal.valueOf(1499))).isZero() },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[2].grunnlag.faktiskUtgiftListe[0].belop.compareTo(BigDecimal.valueOf(5000)),
                ).isZero()
            },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatListe[0].belop.compareTo(BigDecimal.valueOf(4478))).isZero() },
        )
    }

    @Test
    @DisplayName("Test av periodisering for to barn. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
    fun testPeriodiseringToBarn() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2008-03-17"),
                    belop = BigDecimal.valueOf(1000),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-08-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2008-03-17"),
                    belop = BigDecimal.valueOf(2000),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 2,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-08-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2012-02-18"),
                    belop = BigDecimal.valueOf(2000),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2008-03-17"),
                    belop = BigDecimal.valueOf(5000),
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2018-07-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(3) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[0].grunnlag.faktiskUtgiftListe[0].belop.compareTo(BigDecimal.valueOf(1000)),
                ).isZero()
            },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.valueOf(750))).isZero() },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[1].grunnlag.faktiskUtgiftListe[0].belop.compareTo(BigDecimal.valueOf(2000)),
                ).isZero()
            },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].belop.compareTo(BigDecimal.valueOf(1583))).isZero() },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[1].grunnlag.faktiskUtgiftListe[1].belop.compareTo(BigDecimal.valueOf(2000)),
                ).isZero()
            },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[1].belop.compareTo(BigDecimal.valueOf(1583))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[1].soknadsbarnPersonId).isEqualTo(2) },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[2].grunnlag.faktiskUtgiftListe[0].belop.compareTo(BigDecimal.valueOf(5000)),
                ).isZero()
            },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatListe[0].belop.compareTo(BigDecimal.valueOf(4478))).isZero() },
        )
    }

    @Test
    @DisplayName("Test at netto barnetilsyn ikke beregnes hvis barnet er over 12 år")
    fun testNullNettoBarnetilsynBarnOver12Aaar() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2005-03-17"),
                    belop = BigDecimal.valueOf(1000),
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2018-07-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.ZERO)).isZero() },
        )
    }

    @Test
    @DisplayName("Test at netto barnetilsyn ikke beregnes hvis faktisk utgift er 0.-")
    fun testNullNettoBarnetilsynVed0IFaktiskUtgift() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2015-03-17"),
                    belop = BigDecimal.ZERO,
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2018-07-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe.isEmpty()) },
        )
    }

    @Test
    @DisplayName("Test at faktiske utgifter med 0 i beløp tas med i beregning og resultat")
    fun testAt0IFaktiskUtgiftIkkeFjernesFraGrunnlag() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2015-03-17"),
                    belop = BigDecimal.valueOf(200),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 2,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2015-03-17"),
                    belop = BigDecimal.ZERO,
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 3,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2015-03-17"),
                    belop = BigDecimal.valueOf(800),
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2019-07-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].grunnlag.faktiskUtgiftListe.size).isEqualTo(3) },
        )
    }

    @Test
    @DisplayName("Test at det dannes nye perioder ved endring i faktisk utgiftbeløp")
    fun testNyePerioderVedEndringFaktiskUtgift() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 4,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-11-01"), datoTil = LocalDate.parse("2019-12-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2015-03-17"),
                    belop = BigDecimal.valueOf(200),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 3,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-11-01"), datoTil = LocalDate.parse("2018-12-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2014-04-17"),
                    belop = BigDecimal.valueOf(800),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-02-01"), datoTil = LocalDate.parse("2019-04-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2012-05-17"),
                    belop = BigDecimal.valueOf(800),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 2,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-09-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2013-03-17"),
                    belop = BigDecimal.valueOf(200),
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2018-07-01"),
                beregnDatoTil = LocalDate.parse("2020-02-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2018-11-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2018-12-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].belop.compareTo(BigDecimal.valueOf(600))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].soknadsbarnPersonId).isEqualTo(3) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoFom).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoTil).isEqualTo(LocalDate.parse("2019-04-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultatListe[0].belop.compareTo(BigDecimal.valueOf(600))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultatListe[0].soknadsbarnPersonId).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[5].periode.datoFom).isEqualTo(LocalDate.parse("2019-08-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[5].periode.datoTil).isEqualTo(LocalDate.parse("2019-09-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[5].resultatListe[0].belop.compareTo(BigDecimal.valueOf(150))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[5].resultatListe[0].soknadsbarnPersonId).isEqualTo(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[7].periode.datoFom).isEqualTo(LocalDate.parse("2019-11-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[7].periode.datoTil).isEqualTo(LocalDate.parse("2019-12-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[7].resultatListe[0].belop.compareTo(BigDecimal.valueOf(150))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[7].resultatListe[0].soknadsbarnPersonId).isEqualTo(4) },
        )
    }

    @Test
    @DisplayName("Test av periodisering for to barn med overlappende perioder")
    fun testPeriodiseringToBarnOverlappendePerioder() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2008-03-17"),
                    belop = BigDecimal.valueOf(1000),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 2,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2012-02-18"),
                    belop = BigDecimal.valueOf(2000),
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2019-07-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].soknadsbarnPersonId).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[1].soknadsbarnPersonId).isEqualTo(2) },
        )
    }

    @Test
    @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
    fun testGrunnlagMedAvvik() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2008-03-17"),
                    belop = BigDecimal.valueOf(1000),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-08-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2008-03-17"),
                    belop = BigDecimal.valueOf(2000),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2008-03-17"),
                    belop = BigDecimal.valueOf(5000),
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2017-07-01"),
                beregnDatoTil = LocalDate.parse("2021-01-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val avvikListe = nettoBarnetilsynPeriode.validerInput(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(avvikListe).isNotEmpty() },
            Executable { assertThat(avvikListe).hasSize(17) },
            Executable {
                assertThat(
                    avvikListe[0].avvikTekst,
                ).isEqualTo("Første dato i faktiskUtgiftPeriodeListe (2018-07-01) er etter beregnDatoFom (2017-07-01)")
            },
            Executable { assertThat(avvikListe[0].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(
                    avvikListe[1].avvikTekst,
                ).isEqualTo("Siste dato i faktiskUtgiftPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)")
            },
            Executable { assertThat(avvikListe[1].avvikType).isEqualTo(Avvikstype.PERIODE_MANGLER_DATA) },
        )
    }

    @Test
    @DisplayName(
        "Test at beregnet netto barnetilsyn for barn over 12 år settes til 0. Det dannes brudd i periode" +
            "01.01 det året bidragsbarnet fyller 13 år. I denne testen er 13-års-dagen satt til 01.01.2019. For første " +
            "periode 2018-07-01 - 2019-01-01 beregnes da alder til 12 år. I neste periode 2019-01-01 - 2019-02-01" +
            "er alder beregnet til 13 år og netto barnetilsyn skal settes til 0.",
    )
    fun testAtBeregnetBelopSettesLikNullForBarnOver12Aar() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-02-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2006-02-18"),
                    belop = BigDecimal.valueOf(2000),
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2018-07-01"),
                beregnDatoTil = LocalDate.parse("2019-02-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe.size).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].soknadsbarnPersonId).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.valueOf(1499))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].soknadsbarnPersonId).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].belop.compareTo(BigDecimal.ZERO)).isZero() },
        )
    }

    @Test
    @DisplayName(
        (
            "Test at beregnet netto barnetilsyn for barn over 12 år settes til 0. Det dannes brudd i periode" +
                "01.01 det året bidragsbarnet fyller 13 år. Tester også at barn under 13 beregnes som normalt."
            ),
    )
    fun testAtBeregnetBelopSettesLikNullForBarnOver12AarOgBeregningForAndreBarnFungerer() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-02-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2006-02-18"),
                    belop = BigDecimal.valueOf(2000),
                ),
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 2,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-02-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2016-02-18"),
                    belop = BigDecimal.valueOf(1000),
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2018-07-01"),
                beregnDatoTil = LocalDate.parse("2019-02-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe.size).isEqualTo(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].soknadsbarnPersonId).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.valueOf(1624))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].soknadsbarnPersonId).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].belop.compareTo(BigDecimal.ZERO)).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[1].belop.compareTo(BigDecimal.valueOf(750))).isZero() },
        )
    }

    @DisplayName("Test eksempler fra John")
    @Test
    fun testNettoBarnetilsyn() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = 1,
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    faktiskUtgiftPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-01-01")),
                    soknadsbarnFodselsdato = LocalDate.parse("2010-03-17"),
                    belop = BigDecimal.valueOf(3000),
                ),
            )

        val beregnNettoBarnetilsynGrunnlag =
            BeregnNettoBarnetilsynGrunnlag(
                beregnDatoFra = LocalDate.parse("2019-07-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = lagSjablonGrunnlag(),
            )

        val resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)

        assertAll(
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.valueOf(2478))).isZero() },
        )
    }

    private fun lagSjablonGrunnlag() = listOf(
        // Maks fradrag
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_FRADRAG.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "1")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(2083.33)),
                ),
            ),
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_FRADRAG.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "2")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(3333)),
                ),
            ),
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_FRADRAG.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "3")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(4583)),
                ),
            ),
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_FRADRAG.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "4")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(5833)),
                ),
            ),
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_FRADRAG.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "5")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(7083)),
                ),
            ),
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_FRADRAG.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "6")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(8333)),
                ),
            ),
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_FRADRAG.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "7")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(9583)),
                ),
            ),
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_FRADRAG.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "8")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(10833)),
                ),
            ),
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_FRADRAG.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "99")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_FRADRAG_BELØP.navn, verdi = BigDecimal.valueOf(12083)),
                ),
            ),
        ),
        // Maks tilsyn
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_TILSYN.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "1")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_TILSYN_BELØP.navn, verdi = BigDecimal.valueOf(6214)),
                ),
            ),
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_TILSYN.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "2")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_TILSYN_BELØP.navn, verdi = BigDecimal.valueOf(8109)),
                ),
            ),
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonNavn.MAKS_TILSYN.navn,
                nokkelListe = listOf(SjablonNokkel(navn = SjablonNøkkelNavn.ANTALL_BARN_TOM.navn, verdi = "99")),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.MAKS_TILSYN_BELØP.navn, verdi = BigDecimal.valueOf(9189)),
                ),
            ),
        ),
        // Sjablontall
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-12-31")),
            sjablon =
            Sjablon(
                navn = SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.navn,
                nokkelListe = emptyList(),
                innholdListe =
                listOf(
                    SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(25.05)),
                ),
            ),
        ),
    )
}
