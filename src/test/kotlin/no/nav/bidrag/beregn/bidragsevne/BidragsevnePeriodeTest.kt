package no.nav.bidrag.beregn.bidragsevne

import no.nav.bidrag.beregn.TestUtil.BARN_I_HUSSTAND_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BOSTATUS_REFERANSE
import no.nav.bidrag.beregn.TestUtil.INNTEKT_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SAERFRADRAG_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SKATTEKLASSE_REFERANSE
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstandPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag
import no.nav.bidrag.beregn.bidragsevne.bo.BostatusPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.InntektPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.SaerfradragPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.SkatteklassePeriode
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode.Companion.getInstance
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.domain.enums.AvvikType
import no.nav.bidrag.domain.enums.BostatusKode
import no.nav.bidrag.domain.enums.SaerfradragKode
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

internal class BidragsevnePeriodeTest {

    private val bidragsevnePeriode = getInstance()

    @Test
    @DisplayName("Test med OK grunnlag")
    fun testMedOKGrunnlag() {
        val resultat = bidragsevnePeriode.beregnPerioder(lagGrunnlag())

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe).hasSize(6) },

            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.belop).isEqualTo(BigDecimal.valueOf(3749)) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].grunnlag.inntektListe[0].belop).isEqualTo(BigDecimal.valueOf(444000)) },

            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultat.belop).isEqualTo(BigDecimal.valueOf(15604)) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].grunnlag.bostatus.kode).isEqualTo(BostatusKode.ALENE) },

            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2019-02-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2019-04-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultat.belop).isEqualTo(BigDecimal.valueOf(20536)) },

            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoFom).isEqualTo(LocalDate.parse("2019-04-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoTil).isEqualTo(LocalDate.parse("2019-05-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultat.belop).isEqualTo(BigDecimal.valueOf(20536)) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].grunnlag.inntektListe[0].belop).isEqualTo(BigDecimal.valueOf(666001)) },

            Executable { assertThat(resultat.resultatPeriodeListe[4].periode.datoFom).isEqualTo(LocalDate.parse("2019-05-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].periode.datoTil).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].resultat.belop).isEqualTo(BigDecimal.valueOf(20536)) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].grunnlag.inntektListe[0].belop).isEqualTo(BigDecimal.valueOf(666001)) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].grunnlag.inntektListe[1].belop).isEqualTo(BigDecimal.valueOf(2)) },

            Executable { assertThat(resultat.resultatPeriodeListe[5].periode.datoFom).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[5].periode.datoTil).isNull() },
            Executable { assertThat(resultat.resultatPeriodeListe[5].resultat.belop).isEqualTo(BigDecimal.valueOf(20063)) },
            Executable { assertThat(resultat.resultatPeriodeListe[5].grunnlag.inntektListe[0].belop).isEqualTo(BigDecimal.valueOf(666001)) },
            Executable { assertThat(resultat.resultatPeriodeListe[5].grunnlag.inntektListe[1].belop).isEqualTo(BigDecimal.valueOf(2)) },
            Executable { assertThat(resultat.resultatPeriodeListe[5].grunnlag.inntektListe[2].belop).isEqualTo(BigDecimal.valueOf(3)) }
        )
    }

    @Test
    @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
    fun testGrunnlagMedAvvik() {
        val avvikListe = bidragsevnePeriode.validerInput(lagGrunnlagMedAvvik())

        assertAll(
            Executable { assertThat(avvikListe).isNotEmpty() },
            Executable { assertThat(avvikListe).hasSize(6) },
            Executable { assertThat(avvikListe[0].avvikTekst).isEqualTo("Første dato i inntektPeriodeListe (2003-01-01) er etter beregnDatoFra (2001-07-01)") },
            Executable { assertThat(avvikListe[0].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },
            Executable { assertThat(avvikListe[1].avvikTekst).isEqualTo("Siste dato i inntektPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)") },
            Executable { assertThat(avvikListe[1].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },
            Executable { assertThat(avvikListe[2].avvikTekst).isEqualTo("Første dato i skatteklassePeriodeListe (2003-01-01) er etter beregnDatoFra (2001-07-01)") },
            Executable { assertThat(avvikListe[2].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },
            Executable { assertThat(avvikListe[3].avvikTekst).isEqualTo("Siste dato i bostatusPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)") },
            Executable { assertThat(avvikListe[3].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },
            Executable { assertThat(avvikListe[4].avvikTekst).isEqualTo("Siste dato i barnIHusstandPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)") },
            Executable { assertThat(avvikListe[4].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },
            Executable { assertThat(avvikListe[5].avvikTekst).isEqualTo("Siste dato i saerfradragPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)") },
            Executable { assertThat(avvikListe[5].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) }
        )
    }

    private fun lagGrunnlag() =
        BeregnBidragsevneGrunnlag(
            beregnDatoFra = LocalDate.parse("2018-07-01"),
            beregnDatoTil = LocalDate.parse("2020-01-01"),
            inntektPeriodeListe = lagInntektGrunnlag(),
            skatteklassePeriodeListe = lagSkatteklasseGrunnlag(),
            bostatusPeriodeListe = lagBostatusGrunnlag(),
            barnIHusstandPeriodeListe = lagBarnIHusstandGrunnlag(),
            saerfradragPeriodeListe = lagSaerfradragGrunnlag(),
            sjablonPeriodeListe = lagSjablonGrunnlag()
        )

    private fun lagGrunnlagMedAvvik() =
        BeregnBidragsevneGrunnlag(
            beregnDatoFra = LocalDate.parse("2001-07-01"),
            beregnDatoTil = LocalDate.parse("2021-01-01"),
            inntektPeriodeListe = lagInntektGrunnlag(),
            skatteklassePeriodeListe = lagSkatteklasseGrunnlag(),
            bostatusPeriodeListe = lagBostatusGrunnlag(),
            barnIHusstandPeriodeListe = lagBarnIHusstandGrunnlag(),
            saerfradragPeriodeListe = lagSaerfradragGrunnlag(),
            sjablonPeriodeListe = lagSjablonGrunnlag()
        )

    private fun lagInntektGrunnlag() = listOf(
        InntektPeriode(
            referanse = INNTEKT_REFERANSE + "_1",
            inntektPeriode = Periode(datoFom = LocalDate.parse("2003-01-01"), datoTil = LocalDate.parse("2004-01-01")),
            type = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
            belop = BigDecimal.valueOf(666000)
        ),
        InntektPeriode(
            referanse = INNTEKT_REFERANSE + "_2",
            inntektPeriode = Periode(datoFom = LocalDate.parse("2004-01-01"), datoTil = LocalDate.parse("2016-01-01")),
            type = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
            belop = BigDecimal.valueOf(555000)
        ),
        InntektPeriode(
            referanse = INNTEKT_REFERANSE + "_3",
            inntektPeriode = Periode(datoFom = LocalDate.parse("2016-01-01"), datoTil = LocalDate.parse("2019-01-01")),
            type = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
            belop = BigDecimal.valueOf(444000)
        ),
        InntektPeriode(
            referanse = INNTEKT_REFERANSE + "_4",
            inntektPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-04-01")),
            type = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
            belop = BigDecimal.valueOf(666000)
        ),
        InntektPeriode(
            referanse = INNTEKT_REFERANSE + "_5",
            inntektPeriode = Periode(datoFom = LocalDate.parse("2019-04-01"), datoTil = LocalDate.parse("2020-01-01")),
            type = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
            belop = BigDecimal.valueOf(666001)
        ),
        InntektPeriode(
            referanse = INNTEKT_REFERANSE + "_6",
            inntektPeriode = Periode(datoFom = LocalDate.parse("2019-05-01"), datoTil = LocalDate.parse("2020-01-01")),
            type = "OVERGANGSSTONAD",
            belop = BigDecimal.valueOf(2)
        ),
        InntektPeriode(
            referanse = INNTEKT_REFERANSE + "_7",
            inntektPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-01-01")),
            type = "KONTANTSTOTTE",
            belop = BigDecimal.valueOf(3)
        )
    )

    private fun lagSkatteklasseGrunnlag() = listOf(
        SkatteklassePeriode(
            referanse = SKATTEKLASSE_REFERANSE + "_1",
            skatteklassePeriode = Periode(datoFom = LocalDate.parse("2003-01-01"), datoTil = LocalDate.parse("2004-01-01")),
            skatteklasse = 2
        ),
        SkatteklassePeriode(
            referanse = SKATTEKLASSE_REFERANSE + "_2",
            skatteklassePeriode = Periode(datoFom = LocalDate.parse("2004-01-01"), datoTil = LocalDate.parse("2016-01-01")),
            skatteklasse = 2
        ),
        SkatteklassePeriode(
            referanse = SKATTEKLASSE_REFERANSE + "_3",
            skatteklassePeriode = Periode(datoFom = LocalDate.parse("2016-01-01"), datoTil = LocalDate.parse("2019-01-01")),
            skatteklasse = 1
        ),
        SkatteklassePeriode(
            referanse = SKATTEKLASSE_REFERANSE + "_4",
            skatteklassePeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2019-04-01")),
            skatteklasse = 1
        ),
        SkatteklassePeriode(
            referanse = SKATTEKLASSE_REFERANSE + "_5",
            skatteklassePeriode = Periode(datoFom = LocalDate.parse("2019-04-01"), datoTil = LocalDate.parse("2020-01-01")),
            skatteklasse = 1
        ),
        SkatteklassePeriode(
            referanse = SKATTEKLASSE_REFERANSE + "_6",
            skatteklassePeriode = Periode(datoFom = LocalDate.parse("2020-01-01"), datoTil = null),
            skatteklasse = 1
        )
    )

    private fun lagBostatusGrunnlag() = listOf(
        BostatusPeriode(
            referanse = BOSTATUS_REFERANSE + "_1",
            bostatusPeriode = Periode(datoFom = LocalDate.parse("2001-01-01"), datoTil = LocalDate.parse("2017-01-01")),
            kode = BostatusKode.MED_ANDRE
        ),
        BostatusPeriode(
            referanse = BOSTATUS_REFERANSE + "_2",
            bostatusPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2019-02-01")),
            kode = BostatusKode.ALENE
        ),
        BostatusPeriode(
            referanse = BOSTATUS_REFERANSE + "_3",
            bostatusPeriode = Periode(datoFom = LocalDate.parse("2019-02-01"), datoTil = LocalDate.parse("2020-01-01")),
            kode = BostatusKode.MED_ANDRE
        )
    )

    private fun lagBarnIHusstandGrunnlag() = listOf(
        BarnIHusstandPeriode(
            referanse = BARN_I_HUSSTAND_REFERANSE + "_1",
            barnIHusstandPeriode = Periode(datoFom = LocalDate.parse("2001-01-01"), datoTil = LocalDate.parse("2017-01-01")),
            antallBarn = 1.0
        ),
        BarnIHusstandPeriode(
            referanse = BARN_I_HUSSTAND_REFERANSE + "_2",
            barnIHusstandPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
            antallBarn = 2.0
        )
    )

    private fun lagSaerfradragGrunnlag() = listOf(
        SaerfradragPeriode(
            referanse = SAERFRADRAG_REFERANSE + "_1",
            saerfradragPeriode = Periode(datoFom = LocalDate.parse("2001-01-01"), datoTil = LocalDate.parse("2017-01-01")),
            kode = SaerfradragKode.HELT
        ),
        SaerfradragPeriode(
            referanse = SAERFRADRAG_REFERANSE + "_2",
            saerfradragPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
            kode = SaerfradragKode.HELT
        )
    )

    private fun lagSjablonGrunnlag() = listOf(
        SjablonPeriode(
            Periode(LocalDate.parse("2003-01-01"), LocalDate.parse("2003-12-31")),
            Sjablon(
                SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(8848)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2013-01-01"), null),
            Sjablon(
                SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.ZERO
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2003-01-01"), LocalDate.parse("2013-12-31")),
            Sjablon(
                SjablonTallNavn.TRYGDEAVGIFT_PROSENT.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(7.8)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2014-01-01"), null),
            Sjablon(
                SjablonTallNavn.TRYGDEAVGIFT_PROSENT.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(8.2)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
            Sjablon(
                SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(3417)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-07-01"), null),
            Sjablon(
                SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(3487)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2005-01-01"), LocalDate.parse("2005-05-31")),
            Sjablon(
                SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(57400)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2017-07-01"), LocalDate.parse("2017-12-31")),
            Sjablon(
                SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(75000)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-06-30")),
            Sjablon(
                SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(75000)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
            Sjablon(
                SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(83000)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-07-01"), null),
            Sjablon(
                SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(85050)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("9999-12-31")),
            Sjablon(
                SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(31)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
            Sjablon(
                SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(54750)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-07-01"), null),
            Sjablon(
                SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(56550)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
            Sjablon(
                SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(54750)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-07-01"), null),
            Sjablon(
                SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(56550)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
            Sjablon(
                SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(13132)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-01-01"), null),
            Sjablon(
                SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(12977)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
            Sjablon(
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(23)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-01-01"), null),
            Sjablon(
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.SJABLON_VERDI.navn,
                        BigDecimal.valueOf(22)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(169000)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(1.4)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(237900)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(3.3)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(598050)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(12.4)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(962050)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(15.4)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-12-31")),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(174500)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(1.9)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-12-31")),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(245650)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(4.2)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-12-31")),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(617500)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(13.2)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-12-31")),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(964800)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(16.2)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2020-01-01"), null),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(180800)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(1.9)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2020-01-01"), null),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(254500)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(4.2)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2020-01-01"), null),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(639750)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(13.2)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2020-01-01"), null),
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn,
                        BigDecimal.valueOf(999550)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.SKATTESATS_PROSENT.navn,
                        BigDecimal.valueOf(16.2)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
            Sjablon(
                SjablonNavn.BIDRAGSEVNE.navn,
                listOf(
                    SjablonNokkel(
                        SjablonNokkelNavn.BOSTATUS.navn,
                        "EN"
                    )
                ),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.BOUTGIFT_BELOP.navn,
                        BigDecimal.valueOf(9303)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.UNDERHOLD_BELOP.navn,
                        BigDecimal.valueOf(8657)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
            Sjablon(
                SjablonNavn.BIDRAGSEVNE.navn,
                listOf(
                    SjablonNokkel(
                        SjablonNokkelNavn.BOSTATUS.navn,
                        "GS"
                    )
                ),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.BOUTGIFT_BELOP.navn,
                        BigDecimal.valueOf(5698)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.UNDERHOLD_BELOP.navn,
                        BigDecimal.valueOf(7330)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-07-01"), null),
            Sjablon(
                SjablonNavn.BIDRAGSEVNE.navn,
                listOf(
                    SjablonNokkel(
                        SjablonNokkelNavn.BOSTATUS.navn,
                        "EN"
                    )
                ),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.BOUTGIFT_BELOP.navn,
                        BigDecimal.valueOf(9591)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.UNDERHOLD_BELOP.navn,
                        BigDecimal.valueOf(8925)
                    )
                )
            )
        ),
        SjablonPeriode(
            Periode(LocalDate.parse("2019-07-01"), null),
            Sjablon(
                SjablonNavn.BIDRAGSEVNE.navn,
                listOf(
                    SjablonNokkel(
                        SjablonNokkelNavn.BOSTATUS.navn,
                        "GS"
                    )
                ),
                listOf(
                    SjablonInnhold(
                        SjablonInnholdNavn.BOUTGIFT_BELOP.navn,
                        BigDecimal.valueOf(5875)
                    ),
                    SjablonInnhold(
                        SjablonInnholdNavn.UNDERHOLD_BELOP.navn,
                        BigDecimal.valueOf(7557)
                    )
                )
            )
        )
    )
}
