package no.nav.bidrag.beregn.nettobarnetilsyn

import no.nav.bidrag.beregn.TestUtil.FAKTISK_UTGIFT_REFERANSE
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnetNettoBarnetilsynResultat
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatPeriode
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynGrunnlagCore
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.FaktiskUtgiftPeriodeCore
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode
import no.nav.bidrag.domene.enums.beregning.Avvikstype
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
internal class NettoBarnetilsynCoreTest {
    private lateinit var nettoBarnetilsynCoreWithMock: NettoBarnetilsynCore

    @Mock
    private lateinit var nettoBarnetilsynPeriodeMock: NettoBarnetilsynPeriode

    private lateinit var beregnNettoBarnetilsynGrunnlagCore: BeregnNettoBarnetilsynGrunnlagCore
    private lateinit var nettoBarnetilsynPeriodeResultat: BeregnetNettoBarnetilsynResultat
    private lateinit var avvikListe: List<Avvik>

    @BeforeEach
    fun initMocksAndService() {
        nettoBarnetilsynCoreWithMock = NettoBarnetilsynCoreImpl(nettoBarnetilsynPeriodeMock)
    }

    @Test
    @DisplayName("Skal beregne netto barnetilsyn")
    fun skalBeregneNettoBarnetilsyn() {
        byggNettoBarnetilsynPeriodeGrunnlagCore()
        byggNettoBarnetilsynPeriodeResultat()

        `when`(nettoBarnetilsynPeriodeMock.beregnPerioder(any())).thenReturn(nettoBarnetilsynPeriodeResultat)

        val beregnNettoBarnetilsynResultatCore = nettoBarnetilsynCoreWithMock.beregnNettoBarnetilsyn(beregnNettoBarnetilsynGrunnlagCore)

        assertAll(
            Executable { assertThat(beregnNettoBarnetilsynResultatCore).isNotNull() },
            Executable { assertThat(beregnNettoBarnetilsynResultatCore.avvikListe).isEmpty() },
            Executable { assertThat(beregnNettoBarnetilsynResultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(beregnNettoBarnetilsynResultatCore.resultatPeriodeListe.size).isEqualTo(1) },
            Executable { assertThat(beregnNettoBarnetilsynResultatCore.sjablonListe).isNotEmpty() },
            Executable { assertThat(beregnNettoBarnetilsynResultatCore.sjablonListe.size).isEqualTo(1) },
            Executable { assertThat(beregnNettoBarnetilsynResultatCore.sjablonListe[0].verdi).isEqualTo(BigDecimal.valueOf(22)) },
            Executable {
                assertThat(
                    beregnNettoBarnetilsynResultatCore.resultatPeriodeListe[0].periode.datoFom,
                ).isEqualTo(LocalDate.parse("2017-01-01"))
            },
            Executable {
                assertThat(
                    beregnNettoBarnetilsynResultatCore.resultatPeriodeListe[0].periode.datoTil,
                ).isEqualTo(LocalDate.parse("2018-01-01"))
            },
        )
    }

    @Test
    @DisplayName("Skal ikke beregne NettoBarnetilsyn ved avvik")
    fun skalIkkeBeregneBidragsevneVedAvvik() {
        byggNettoBarnetilsynPeriodeGrunnlagCore()
        byggAvvik()

        `when`(nettoBarnetilsynPeriodeMock.validerInput(any())).thenReturn(avvikListe)

        val beregnbidragsevneResultatCore = nettoBarnetilsynCoreWithMock.beregnNettoBarnetilsyn(beregnNettoBarnetilsynGrunnlagCore)

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

    private fun byggNettoBarnetilsynPeriodeGrunnlagCore() {
        val faktiskUtgiftPeriodeListe =
            listOf(
                FaktiskUtgiftPeriodeCore(
                    referanse = FAKTISK_UTGIFT_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    soknadsbarnFodselsdato = LocalDate.parse("2010-01-01"),
                    soknadsbarnPersonId = 1,
                    belop = BigDecimal.valueOf(2),
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

        beregnNettoBarnetilsynGrunnlagCore =
            BeregnNettoBarnetilsynGrunnlagCore(
                beregnDatoFra = LocalDate.parse("2017-01-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                faktiskUtgiftPeriodeListe = faktiskUtgiftPeriodeListe,
                sjablonPeriodeListe = sjablonPeriodeListe,
            )
    }

    private fun byggNettoBarnetilsynPeriodeResultat() {
        val periodeResultatListe =
            listOf(
                ResultatPeriode(
                    periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2018-01-01")),
                    resultatListe =
                    listOf(
                        ResultatBeregning(
                            soknadsbarnPersonId = 1,
                            belop = BigDecimal.valueOf(1),
                            sjablonListe =
                            listOf(
                                SjablonPeriodeNavnVerdi(
                                    periode =
                                    Periode(
                                        datoFom = LocalDate.parse("2017-01-01"),
                                        datoTil = LocalDate.parse("9999-12-31"),
                                    ),
                                    navn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                                    verdi = BigDecimal.valueOf(22),
                                ),
                            ),
                        ),
                    ),
                    grunnlag =
                    GrunnlagBeregning(
                        faktiskUtgiftListe =
                        listOf(
                            FaktiskUtgift(
                                soknadsbarnPersonId = 1,
                                referanse = FAKTISK_UTGIFT_REFERANSE,
                                soknadsbarnAlder = 10,
                                belop = BigDecimal.valueOf(3),
                            ),
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

        nettoBarnetilsynPeriodeResultat = BeregnetNettoBarnetilsynResultat(periodeResultatListe)
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
