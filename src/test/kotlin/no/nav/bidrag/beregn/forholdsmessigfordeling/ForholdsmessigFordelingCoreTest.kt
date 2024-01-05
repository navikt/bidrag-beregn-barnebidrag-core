package no.nav.bidrag.beregn.forholdsmessigfordeling

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingResultat
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSak
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.Bidragsevne
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagPerBarn
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPerBarn
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPeriode
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnForholdsmessigFordelingGrunnlagCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnetBidragSakPeriodeCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BidragsevnePeriodeCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.GrunnlagPerBarnCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.periode.ForholdsmessigFordelingPeriode
import no.nav.bidrag.domene.enums.beregning.Avvikstype
import no.nav.bidrag.domene.enums.beregning.ResultatkodeBarnebidrag
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
internal class ForholdsmessigFordelingCoreTest {
    private lateinit var forholdsmessigFordelingCoreWithMock: ForholdsmessigFordelingCore

    @Mock
    private lateinit var forholdsmessigFordelingPeriodeMock: ForholdsmessigFordelingPeriode

    private lateinit var beregnForholdsmessigFordelingGrunnlagCore: BeregnForholdsmessigFordelingGrunnlagCore
    private lateinit var beregnForholdsmessigFordelingResultat: BeregnForholdsmessigFordelingResultat
    private lateinit var avvikListe: List<Avvik>

    @BeforeEach
    fun initMocksAndService() {
        forholdsmessigFordelingCoreWithMock = ForholdsmessigFordelingCoreImpl(forholdsmessigFordelingPeriodeMock)
    }

    @Test
    @DisplayName("Skal beregne forholdsmessig fordeling")
    fun skalBeregneForholdsmessigFordeling() {
        byggForholdsmessigFordelingPeriodeGrunnlagCore()
        byggForholdsmessigFordelingPeriodeResultat()

        `when`(forholdsmessigFordelingPeriodeMock.beregnPerioder(any())).thenReturn(beregnForholdsmessigFordelingResultat)

        val beregnForholdsmessigFordelingResultatCore =
            forholdsmessigFordelingCoreWithMock.beregnForholdsmessigFordeling(
                beregnForholdsmessigFordelingGrunnlagCore,
            )

        assertAll(
            Executable { assertThat(beregnForholdsmessigFordelingResultatCore).isNotNull() },
            Executable { assertThat(beregnForholdsmessigFordelingResultatCore.avvikListe).isEmpty() },
            Executable { assertThat(beregnForholdsmessigFordelingResultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(beregnForholdsmessigFordelingResultatCore.resultatPeriodeListe.size).isEqualTo(1) },
            Executable {
                assertThat(
                    beregnForholdsmessigFordelingResultatCore.resultatPeriodeListe[0].periode.datoFom,
                ).isEqualTo(LocalDate.parse("2017-01-01"))
            },
            Executable {
                assertThat(
                    beregnForholdsmessigFordelingResultatCore.resultatPeriodeListe[0].periode.datoTil,
                ).isEqualTo(LocalDate.parse("2018-01-01"))
            },
        )
    }

    @Test
    @DisplayName("Skal ikke beregne forholdsmessig fordeling ved avvik")
    fun skalIkkeBeregneForholdsmessigFordelingVedAvvik() {
        byggForholdsmessigFordelingPeriodeGrunnlagCore()
        byggAvvik()

        `when`(forholdsmessigFordelingPeriodeMock.validerInput(any())).thenReturn(avvikListe)

        val beregnForholdsmessigFordelingResultatCore =
            forholdsmessigFordelingCoreWithMock.beregnForholdsmessigFordeling(
                beregnForholdsmessigFordelingGrunnlagCore,
            )

        assertAll(
            Executable { assertThat(beregnForholdsmessigFordelingResultatCore).isNotNull() },
            Executable { assertThat(beregnForholdsmessigFordelingResultatCore.avvikListe).isNotEmpty() },
            Executable { assertThat(beregnForholdsmessigFordelingResultatCore.avvikListe).hasSize(1) },
            Executable {
                assertThat(
                    beregnForholdsmessigFordelingResultatCore.avvikListe[0].avvikTekst,
                ).isEqualTo("beregnDatoTil må være etter beregnDatoFra")
            },
            Executable {
                assertThat(
                    beregnForholdsmessigFordelingResultatCore.avvikListe[0].avvikType,
                ).isEqualTo(Avvikstype.DATO_FOM_ETTER_DATO_TIL.toString())
            },
            Executable { assertThat(beregnForholdsmessigFordelingResultatCore.resultatPeriodeListe).isEmpty() },
        )
    }

    private fun byggForholdsmessigFordelingPeriodeGrunnlagCore() {
        val bidragsevnePeriodeListe =
            listOf(
                BidragsevnePeriodeCore(
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    belop = BigDecimal.valueOf(100000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(20000),
                ),
            )

        val beregnetBidragSakPeriodeListe =
            listOf(
                BeregnetBidragSakPeriodeCore(
                    saksnr = 1,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    grunnlagPerBarnListe = listOf(GrunnlagPerBarnCore(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(20000))),
                ),
            )

        beregnForholdsmessigFordelingGrunnlagCore =
            BeregnForholdsmessigFordelingGrunnlagCore(
                beregnDatoFra = LocalDate.parse("2017-01-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                bidragsevnePeriodeListe = bidragsevnePeriodeListe,
                beregnetBidragPeriodeListe = beregnetBidragSakPeriodeListe,
            )
    }

    private fun byggForholdsmessigFordelingPeriodeResultat() {
        val periodeResultatListe =
            listOf(
                ResultatPeriode(
                    periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2018-01-01")),
                    resultatBeregningListe =
                    listOf(
                        ResultatBeregning(
                            saksnr = 1,
                            resultatPerBarnListe =
                            listOf(
                                ResultatPerBarn(
                                    barnPersonId = 1,
                                    belop = BigDecimal.valueOf(1),
                                    kode = ResultatkodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG,
                                ),
                            ),
                        ),
                    ),
                    resultatGrunnlag =
                    GrunnlagBeregningPeriodisert(
                        bidragsevne = Bidragsevne(belop = BigDecimal.valueOf(1000), tjuefemProsentInntekt = BigDecimal.valueOf(12000)),
                        beregnetBidragSakListe =
                        listOf(
                            BeregnetBidragSak(
                                saksnr = 1,
                                grunnlagPerBarnListe =
                                listOf(
                                    GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(1000)),
                                ),
                            ),
                        ),
                    ),
                ),
            )

        beregnForholdsmessigFordelingResultat = BeregnForholdsmessigFordelingResultat(periodeResultatListe)
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
