package no.nav.bidrag.beregn.underholdskostnad

import no.nav.bidrag.beregn.TestUtil.BARNETILSYN_MED_STØNAD_REFERANSE
import no.nav.bidrag.beregn.TestUtil.FORPLEINING_UTGIFT_REFERANSE
import no.nav.bidrag.beregn.TestUtil.NETTO_BARNETILSYN_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SOKNADSBARN_REFERANSE
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnetUnderholdskostnadResultat
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgift
import no.nav.bidrag.beregn.underholdskostnad.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsyn
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.SoknadsbarnAlder
import no.nav.bidrag.beregn.underholdskostnad.dto.BarnetilsynMedStonadPeriodeCore
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadGrunnlagCore
import no.nav.bidrag.beregn.underholdskostnad.dto.ForpleiningUtgiftPeriodeCore
import no.nav.bidrag.beregn.underholdskostnad.dto.NettoBarnetilsynPeriodeCore
import no.nav.bidrag.beregn.underholdskostnad.dto.SoknadsbarnCore
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode
import no.nav.bidrag.domene.enums.beregning.Avvikstype
import no.nav.bidrag.domene.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions
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
class UnderholdskostnadCoreTest {
    private lateinit var underholdskostnadCoreWithMock: UnderholdskostnadCore

    @Mock
    private lateinit var underholdskostnadPeriodeMock: UnderholdskostnadPeriode

    private lateinit var beregnUnderholdskostnadGrunnlagCore: BeregnUnderholdskostnadGrunnlagCore
    private lateinit var underholdskostnadPeriodeResultat: BeregnetUnderholdskostnadResultat
    private lateinit var avvikListe: List<Avvik>

    @BeforeEach
    fun initMocksAndService() {
        underholdskostnadCoreWithMock = UnderholdskostnadCoreImpl(underholdskostnadPeriodeMock)
    }

    @Test
    @DisplayName("Skal beregne underholdskostnad")
    fun skalBeregneunderholdskostnad() {
        byggUnderholdskostnadPeriodeGrunnlagCore()
        byggUnderholdskostnadPeriodeResultat()

        `when`(underholdskostnadPeriodeMock.beregnPerioder(any())).thenReturn(underholdskostnadPeriodeResultat)

        val beregnUnderholdskostnadResultatCore = underholdskostnadCoreWithMock.beregnUnderholdskostnad(beregnUnderholdskostnadGrunnlagCore)

        assertAll(
            Executable { Assertions.assertThat(beregnUnderholdskostnadResultatCore).isNotNull() },
            Executable { Assertions.assertThat(beregnUnderholdskostnadResultatCore.avvikListe).isEmpty() },
            Executable { Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe.size).isEqualTo(3) },
            Executable {
                Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe[0].periode.datoFom)
                    .isEqualTo(LocalDate.parse("2017-01-01"))
            },
            Executable {
                Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe[0].periode.datoTil)
                    .isEqualTo(LocalDate.parse("2018-01-01"))
            },
            Executable {
                Assertions.assertThat(
                    beregnUnderholdskostnadResultatCore.resultatPeriodeListe[0].resultat.belop,
                ).isEqualTo(BigDecimal.valueOf(666))
            },
            Executable {
                Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[0])
                    .isEqualTo(BARNETILSYN_MED_STØNAD_REFERANSE)
            },
            Executable {
                Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[1])
                    .isEqualTo(FORPLEINING_UTGIFT_REFERANSE)
            },
            Executable {
                Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[2])
                    .isEqualTo(NETTO_BARNETILSYN_REFERANSE)
            },
            Executable {
                Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[3])
                    .isEqualTo(SOKNADSBARN_REFERANSE)
            },
            Executable {
                Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe[1].periode.datoFom)
                    .isEqualTo(LocalDate.parse("2018-01-01"))
            },
            Executable {
                Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe[1].periode.datoTil)
                    .isEqualTo(LocalDate.parse("2019-01-01"))
            },
            Executable {
                Assertions.assertThat(
                    beregnUnderholdskostnadResultatCore.resultatPeriodeListe[1].resultat.belop,
                ).isEqualTo(BigDecimal.valueOf(667))
            },
            Executable {
                Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe[2].periode.datoFom)
                    .isEqualTo(LocalDate.parse("2019-01-01"))
            },
            Executable {
                Assertions.assertThat(beregnUnderholdskostnadResultatCore.resultatPeriodeListe[2].periode.datoTil)
                    .isEqualTo(LocalDate.parse("2020-01-01"))
            },
            Executable {
                Assertions.assertThat(
                    beregnUnderholdskostnadResultatCore.resultatPeriodeListe[2].resultat.belop,
                ).isEqualTo(BigDecimal.valueOf(668))
            },
        )
    }

    @Test
    @DisplayName("Skal ikke beregne underholdskostnad ved avvik")
    fun skalIkkeBeregneBidragsevneVedAvvik() {
        byggUnderholdskostnadPeriodeGrunnlagCore()
        byggAvvik()

        `when`(underholdskostnadPeriodeMock.validerInput(any())).thenReturn(avvikListe)

        val beregnunderholdskostnadResultatCore = underholdskostnadCoreWithMock.beregnUnderholdskostnad(beregnUnderholdskostnadGrunnlagCore)

        assertAll(
            Executable { Assertions.assertThat(beregnunderholdskostnadResultatCore).isNotNull() },
            Executable { Assertions.assertThat(beregnunderholdskostnadResultatCore.avvikListe).isNotEmpty() },
            Executable { Assertions.assertThat(beregnunderholdskostnadResultatCore.avvikListe).hasSize(1) },
            Executable {
                Assertions.assertThat(beregnunderholdskostnadResultatCore.avvikListe[0].avvikTekst)
                    .isEqualTo("beregnDatoTil må være etter beregnDatoFra")
            },
            Executable {
                Assertions.assertThat(beregnunderholdskostnadResultatCore.avvikListe[0].avvikType)
                    .isEqualTo(Avvikstype.DATO_FOM_ETTER_DATO_TIL.toString())
            },
            Executable { Assertions.assertThat(beregnunderholdskostnadResultatCore.resultatPeriodeListe).isEmpty() },
        )
    }

    private fun byggUnderholdskostnadPeriodeGrunnlagCore() {
        val soknadsbarn = SoknadsbarnCore(referanse = SOKNADSBARN_REFERANSE, personId = 1, fodselsdato = LocalDate.parse("2017-01-01"))

        val barnetilsynMedStonadPeriodeListe =
            listOf(
                BarnetilsynMedStonadPeriodeCore(
                    referanse = BARNETILSYN_MED_STØNAD_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    tilsynType = "DU",
                    stonadType = "64",
                ),
            )

        val nettoBarnetilsynPeriodeListe =
            listOf(
                NettoBarnetilsynPeriodeCore(
                    referanse = NETTO_BARNETILSYN_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    belop = BigDecimal.valueOf(666),
                ),
            )

        val forpleiningUtgiftPeriodeListe =
            listOf(
                ForpleiningUtgiftPeriodeCore(
                    referanse = FORPLEINING_UTGIFT_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    belop = BigDecimal.valueOf(666),
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

        beregnUnderholdskostnadGrunnlagCore =
            BeregnUnderholdskostnadGrunnlagCore(
                beregnDatoFra = LocalDate.parse("2017-01-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                soknadsbarn = soknadsbarn,
                barnetilsynMedStonadPeriodeListe = barnetilsynMedStonadPeriodeListe,
                nettoBarnetilsynPeriodeListe = nettoBarnetilsynPeriodeListe,
                forpleiningUtgiftPeriodeListe = forpleiningUtgiftPeriodeListe,
                sjablonPeriodeListe = sjablonPeriodeListe,
            )
    }

    private fun byggUnderholdskostnadPeriodeResultat() {
        val periodeResultatListe =
            listOf(
                ResultatPeriode(
                    soknadsbarnPersonId = 1,
                    periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2018-01-01")),
                    resultat =
                    ResultatBeregning(
                        belop = BigDecimal.valueOf(666),
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
                        soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 7),
                        barnetilsynMedStonad =
                        BarnetilsynMedStonad(
                            referanse = BARNETILSYN_MED_STØNAD_REFERANSE,
                            tilsynType = "DU",
                            stonadType = "64",
                        ),
                        nettoBarnetilsyn =
                        NettoBarnetilsyn(
                            referanse = NETTO_BARNETILSYN_REFERANSE,
                            belop = BigDecimal.valueOf(666),
                        ),
                        forpleiningUtgift =
                        ForpleiningUtgift(
                            referanse = FORPLEINING_UTGIFT_REFERANSE,
                            belop = BigDecimal.valueOf(777),
                        ),
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
                    soknadsbarnPersonId = 1,
                    periode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-01-01")),
                    resultat =
                    ResultatBeregning(
                        belop = BigDecimal.valueOf(667),
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
                        soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 7),
                        barnetilsynMedStonad =
                        BarnetilsynMedStonad(
                            referanse = BARNETILSYN_MED_STØNAD_REFERANSE,
                            tilsynType = "DU",
                            stonadType = "64",
                        ),
                        nettoBarnetilsyn =
                        NettoBarnetilsyn(
                            referanse = NETTO_BARNETILSYN_REFERANSE,
                            belop = BigDecimal.valueOf(667),
                        ),
                        forpleiningUtgift =
                        ForpleiningUtgift(
                            referanse = FORPLEINING_UTGIFT_REFERANSE,
                            belop = BigDecimal.valueOf(778),
                        ),
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
                    soknadsbarnPersonId = 1,
                    periode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                    resultat =
                    ResultatBeregning(
                        belop = BigDecimal.valueOf(668),
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
                        soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 7),
                        barnetilsynMedStonad =
                        BarnetilsynMedStonad(
                            referanse = BARNETILSYN_MED_STØNAD_REFERANSE,
                            tilsynType = "DU",
                            stonadType = "64",
                        ),
                        nettoBarnetilsyn =
                        NettoBarnetilsyn(
                            referanse = NETTO_BARNETILSYN_REFERANSE,
                            belop = BigDecimal.valueOf(668),
                        ),
                        forpleiningUtgift =
                        ForpleiningUtgift(
                            referanse = FORPLEINING_UTGIFT_REFERANSE,
                            belop = BigDecimal.valueOf(778),
                        ),
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

        underholdskostnadPeriodeResultat = BeregnetUnderholdskostnadResultat(periodeResultatListe)
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
