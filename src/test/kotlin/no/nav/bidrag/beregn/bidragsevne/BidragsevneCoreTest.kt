package no.nav.bidrag.beregn.bidragsevne

import no.nav.bidrag.beregn.TestUtil.BARN_I_HUSSTAND_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BOSTATUS_REFERANSE
import no.nav.bidrag.beregn.TestUtil.INNTEKT_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SAERFRADRAG_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SKATTEKLASSE_REFERANSE
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstand
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnetBidragsevneResultat
import no.nav.bidrag.beregn.bidragsevne.bo.Bostatus
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.Saerfradrag
import no.nav.bidrag.beregn.bidragsevne.bo.Skatteklasse
import no.nav.bidrag.beregn.bidragsevne.dto.BarnIHusstandPeriodeCore
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneGrunnlagCore
import no.nav.bidrag.beregn.bidragsevne.dto.BostatusPeriodeCore
import no.nav.bidrag.beregn.bidragsevne.dto.InntektPeriodeCore
import no.nav.bidrag.beregn.bidragsevne.dto.SaerfradragPeriodeCore
import no.nav.bidrag.beregn.bidragsevne.dto.SkatteklassePeriodeCore
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.domene.enums.beregning.Avvikstype
import no.nav.bidrag.domene.enums.beregning.Særfradragskode
import no.nav.bidrag.domene.enums.person.Bostatuskode
import no.nav.bidrag.domene.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
internal class BidragsevneCoreTest {
    private lateinit var bidragsevneCoreWithMock: BidragsevneCore

    @Mock
    private lateinit var bidragsevnePeriodeMock: BidragsevnePeriode

    private lateinit var beregnBidragsevneGrunnlagCore: BeregnBidragsevneGrunnlagCore
    private lateinit var bidragsevnePeriodeResultat: BeregnetBidragsevneResultat
    private lateinit var avvikListe: List<Avvik>

    @BeforeEach
    fun initMocksAndService() {
        bidragsevneCoreWithMock = BidragsevneCoreImpl(bidragsevnePeriodeMock)
    }

    @Test
    @DisplayName("Skal beregne bidragsevne")
    fun skalBeregneBidragsevne() {
        byggBidragsevnePeriodeGrunnlagCore()
        byggBidragsevnePeriodeResultat()

        `when`(bidragsevnePeriodeMock.beregnPerioder(any())).thenReturn(bidragsevnePeriodeResultat)

        val beregnBidragsevneResultatCore = bidragsevneCoreWithMock.beregnBidragsevne(beregnBidragsevneGrunnlagCore)

        assertAll(
            Executable { assertThat(beregnBidragsevneResultatCore).isNotNull() },
            Executable { assertThat(beregnBidragsevneResultatCore.avvikListe).isEmpty() },
            Executable { assertThat(beregnBidragsevneResultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(beregnBidragsevneResultatCore.resultatPeriodeListe.size).isEqualTo(3) },
            Executable { assertThat(beregnBidragsevneResultatCore.sjablonListe).isNotEmpty() },
            Executable { assertThat(beregnBidragsevneResultatCore.sjablonListe.size).isEqualTo(1) },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[0].periode.datoFom,
                ).isEqualTo(LocalDate.parse("2017-01-01"))
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[0].periode.datoTil,
                ).isEqualTo(LocalDate.parse("2018-01-01"))
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[0].resultat.belop,
                ).isEqualTo(BigDecimal.valueOf(666))
            },
            Executable {
                assertThat(beregnBidragsevneResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[0]).isEqualTo(
                    BARN_I_HUSSTAND_REFERANSE,
                )
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[1],
                ).isEqualTo(BOSTATUS_REFERANSE)
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[2],
                ).isEqualTo(INNTEKT_REFERANSE)
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[3],
                ).isEqualTo(SAERFRADRAG_REFERANSE)
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[4],
                ).isEqualTo(SKATTEKLASSE_REFERANSE)
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[1].periode.datoFom,
                ).isEqualTo(LocalDate.parse("2018-01-01"))
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[1].periode.datoTil,
                ).isEqualTo(LocalDate.parse("2019-01-01"))
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[1].resultat.belop,
                ).isEqualTo(BigDecimal.valueOf(667))
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[2].periode.datoFom,
                ).isEqualTo(LocalDate.parse("2019-01-01"))
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[2].periode.datoTil,
                ).isEqualTo(LocalDate.parse("2020-01-01"))
            },
            Executable {
                assertThat(
                    beregnBidragsevneResultatCore.resultatPeriodeListe[2].resultat.belop,
                ).isEqualTo(BigDecimal.valueOf(668))
            },
        )
    }

    @Test
    @DisplayName("Skal ikke beregne bidragsevne ved avvik")
    fun skalIkkeBeregneBidragsevneVedAvvik() {
        byggBidragsevnePeriodeGrunnlagCore()
        byggAvvik()

        `when`(bidragsevnePeriodeMock.validerInput(any())).thenReturn(avvikListe)

        val beregnbidragsevneResultatCore = bidragsevneCoreWithMock.beregnBidragsevne(beregnBidragsevneGrunnlagCore)

        assertAll(
            Executable { assertThat(beregnbidragsevneResultatCore).isNotNull() },
            Executable { assertThat(beregnbidragsevneResultatCore.avvikListe).isNotEmpty() },
            Executable { assertThat(beregnbidragsevneResultatCore.avvikListe).hasSize(1) },
            Executable {
                assertThat(
                    beregnbidragsevneResultatCore.avvikListe[0].avvikTekst,
                ).isEqualTo("beregnDatoTil må være etter beregnDatoFra")
            },
            Executable {
                assertThat(
                    beregnbidragsevneResultatCore.avvikListe[0].avvikType,
                ).isEqualTo(Avvikstype.DATO_FOM_ETTER_DATO_TIL.toString())
            },
            Executable { assertThat(beregnbidragsevneResultatCore.resultatPeriodeListe).isEmpty() },
        )
    }

    private fun byggBidragsevnePeriodeGrunnlagCore() {
        val inntektPeriodeListe =
            listOf(
                InntektPeriodeCore(
                    referanse = INNTEKT_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    type = "LONN_SKE",
                    belop = BigDecimal.valueOf(666000),
                ),
            )

        val skatteklassePeriodeListe =
            listOf(
                SkatteklassePeriodeCore(
                    referanse = SKATTEKLASSE_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    skatteklasse = 1,
                ),
            )

        val bostatusPeriodeListe =
            listOf(
                BostatusPeriodeCore(
                    referanse = BOSTATUS_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                    kode = Bostatuskode.IKKE_MED_FORELDER.toString(),
                ),
            )

        val barnIHusstandPeriodeListe =
            listOf(
                BarnIHusstandPeriodeCore(
                    referanse = BARN_I_HUSSTAND_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    antallBarn = 1.0,
                ),
            )

        val saerfradragPeriodeListe =
            listOf(
                SaerfradragPeriodeCore(
                    referanse = SAERFRADRAG_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    kode = Særfradragskode.HELT.toString(),
                ),
            )

        val sjablonPeriodeListe =
            listOf(
                SjablonPeriodeCore(
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                    navn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnholdCore(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(22))),
                ),
            )

        beregnBidragsevneGrunnlagCore =
            BeregnBidragsevneGrunnlagCore(
                beregnDatoFra = LocalDate.parse("2017-01-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                inntektPeriodeListe = inntektPeriodeListe,
                skatteklassePeriodeListe = skatteklassePeriodeListe,
                bostatusPeriodeListe = bostatusPeriodeListe,
                barnIHusstandPeriodeListe = barnIHusstandPeriodeListe,
                saerfradragPeriodeListe = saerfradragPeriodeListe,
                sjablonPeriodeListe = sjablonPeriodeListe,
            )
    }

    private fun byggBidragsevnePeriodeResultat() {
        val periodeResultatListe =
            listOf(
                ResultatPeriode(
                    periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2018-01-01")),
                    resultat =
                    ResultatBeregning(
                        belop = BigDecimal.valueOf(666),
                        inntekt25Prosent = BigDecimal.valueOf(166500),
                        sjablonListe =
                        listOf(
                            SjablonPeriodeNavnVerdi(
                                periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                                navn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                                verdi = BigDecimal.valueOf(22),
                            ),
                        ),
                    ),
                    grunnlag =
                    GrunnlagBeregning(
                        inntektListe =
                        listOf(
                            Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(666000)),
                        ),
                        skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                        bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.IKKE_MED_FORELDER),
                        barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
                        saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.HELT),
                        sjablonListe =
                        listOf(
                            SjablonPeriode(
                                sjablonPeriode =
                                Periode(
                                    datoFom = LocalDate.parse("2017-01-01"),
                                    datoTil = LocalDate.parse("9999-12-31"),
                                ),
                                sjablon =
                                Sjablon(
                                    navn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                                    nokkelListe = emptyList(),
                                    innholdListe =
                                    listOf(
                                        SjablonInnhold(
                                            navn = SjablonInnholdNavn.SJABLON_VERDI.navn,
                                            verdi = BigDecimal.valueOf(22),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                ResultatPeriode(
                    periode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-01-01")),
                    resultat =
                    ResultatBeregning(
                        belop = BigDecimal.valueOf(667),
                        inntekt25Prosent = BigDecimal.valueOf(166500),
                        sjablonListe =
                        listOf(
                            SjablonPeriodeNavnVerdi(
                                periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                                navn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                                verdi = BigDecimal.valueOf(22),
                            ),
                        ),
                    ),
                    grunnlag =
                    GrunnlagBeregning(
                        inntektListe =
                        listOf(
                            Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(500000)),
                        ),
                        skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                        bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.IKKE_MED_FORELDER),
                        barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
                        saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.HELT),
                        sjablonListe =
                        listOf(
                            SjablonPeriode(
                                sjablonPeriode =
                                Periode(
                                    datoFom = LocalDate.parse("2017-01-01"),
                                    datoTil = LocalDate.parse("9999-12-31"),
                                ),
                                sjablon =
                                Sjablon(
                                    navn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                                    nokkelListe = emptyList(),
                                    innholdListe =
                                    listOf(
                                        SjablonInnhold(
                                            navn = SjablonInnholdNavn.SJABLON_VERDI.navn,
                                            verdi = BigDecimal.valueOf(22),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                ResultatPeriode(
                    periode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                    resultat =
                    ResultatBeregning(
                        belop = BigDecimal.valueOf(668),
                        inntekt25Prosent = BigDecimal.valueOf(166500),
                        sjablonListe =
                        listOf(
                            SjablonPeriodeNavnVerdi(
                                periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                                navn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                                verdi = BigDecimal.valueOf(22),
                            ),
                        ),
                    ),
                    grunnlag =
                    GrunnlagBeregning(
                        inntektListe =
                        listOf(
                            Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(500000)),
                        ),
                        skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                        bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.IKKE_MED_FORELDER),
                        barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
                        saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.HELT),
                        sjablonListe =
                        listOf(
                            SjablonPeriode(
                                sjablonPeriode =
                                Periode(
                                    datoFom = LocalDate.parse("2017-01-01"),
                                    datoTil = LocalDate.parse("9999-12-31"),
                                ),
                                sjablon =
                                Sjablon(
                                    navn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                                    nokkelListe = emptyList(),
                                    innholdListe =
                                    listOf(
                                        SjablonInnhold(
                                            navn = SjablonInnholdNavn.SJABLON_VERDI.navn,
                                            verdi = BigDecimal.valueOf(22),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            )

        bidragsevnePeriodeResultat = BeregnetBidragsevneResultat(periodeResultatListe)
    }

    private fun byggAvvik() {
        avvikListe =
            listOf(
                Avvik(avvikTekst = "beregnDatoTil må være etter beregnDatoFra", avvikType = Avvikstype.DATO_FOM_ETTER_DATO_TIL),
            )
    }

    companion object MockitoHelper {
        fun <T> any(): T = Mockito.any()
    }
}
