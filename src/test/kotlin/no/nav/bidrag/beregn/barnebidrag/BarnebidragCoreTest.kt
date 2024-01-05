package no.nav.bidrag.beregn.barnebidrag

import no.nav.bidrag.beregn.TestUtil.ANDRE_LOPENDE_BIDRAG_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_BM_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_BP_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_FORSVARET_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BIDRAGSEVNE_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.TestUtil.DELT_BOSTED_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SAMVÆRSFRADRAG_REFERANSE
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidrag
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaret
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBosted
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.Samvaersfradrag
import no.nav.bidrag.beregn.barnebidrag.dto.AndreLopendeBidragPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.BPsAndelUnderholdskostnadPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggForsvaretPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnBarnebidragGrunnlagCore
import no.nav.bidrag.beregn.barnebidrag.dto.BidragsevnePeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.DeltBostedPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.SamvaersfradragPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode
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
import no.nav.bidrag.domene.enums.beregning.ResultatkodeBarnebidrag
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
internal class BarnebidragCoreTest {
    private lateinit var barnebidragCoreWithMock: BarnebidragCore

    @Mock
    private lateinit var barnebidragPeriodeMock: BarnebidragPeriode

    private lateinit var beregnBarnebidragGrunnlagCore: BeregnBarnebidragGrunnlagCore
    private lateinit var beregnBarnebidragPeriodeResultat: BeregnBarnebidragResultat
    private lateinit var avvikListe: List<Avvik>

    @BeforeEach
    fun initMocksAndService() {
        barnebidragCoreWithMock = BarnebidragCoreImpl(barnebidragPeriodeMock)
    }

    @Test
    @DisplayName("Skal beregne barnebidrag")
    fun skalBeregneBarnebidrag() {
        byggBarnebidragPeriodeGrunnlagCore()
        byggBarnebidragPeriodeResultat()

        `when`(barnebidragPeriodeMock.beregnPerioder(any())).thenReturn(beregnBarnebidragPeriodeResultat)

        val beregnBarnebidragResultatCore = barnebidragCoreWithMock.beregnBarnebidrag(beregnBarnebidragGrunnlagCore)

        assertAll(
            Executable { assertThat(beregnBarnebidragResultatCore).isNotNull() },
            Executable { assertThat(beregnBarnebidragResultatCore.avvikListe).isEmpty() },
            Executable { assertThat(beregnBarnebidragResultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(beregnBarnebidragResultatCore.resultatPeriodeListe.size).isEqualTo(1) },
            Executable { assertThat(beregnBarnebidragResultatCore.sjablonListe).isNotEmpty() },
            Executable { assertThat(beregnBarnebidragResultatCore.sjablonListe.size).isEqualTo(1) },
            Executable {
                assertThat(
                    beregnBarnebidragResultatCore.resultatPeriodeListe[0].periode.datoFom,
                ).isEqualTo(LocalDate.parse("2017-01-01"))
            },
            Executable {
                assertThat(
                    beregnBarnebidragResultatCore.resultatPeriodeListe[0].periode.datoTil,
                ).isEqualTo(LocalDate.parse("2018-01-01"))
            },
            Executable {
                assertThat(beregnBarnebidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[0]).isEqualTo(
                    ANDRE_LOPENDE_BIDRAG_REFERANSE,
                )
            },
            Executable {
                assertThat(beregnBarnebidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[1]).isEqualTo(
                    BARNETILLEGG_BM_REFERANSE,
                )
            },
            Executable {
                assertThat(beregnBarnebidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[2]).isEqualTo(
                    BARNETILLEGG_BP_REFERANSE,
                )
            },
            Executable {
                assertThat(beregnBarnebidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[3]).isEqualTo(
                    BARNETILLEGG_FORSVARET_REFERANSE,
                )
            },
            Executable {
                assertThat(
                    beregnBarnebidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[4],
                ).isEqualTo(BIDRAGSEVNE_REFERANSE)
            },
            Executable {
                assertThat(beregnBarnebidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[5]).isEqualTo(
                    BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                )
            },
            Executable {
                assertThat(
                    beregnBarnebidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[6],
                ).isEqualTo(DELT_BOSTED_REFERANSE)
            },
            Executable {
                assertThat(beregnBarnebidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[7]).isEqualTo(
                    SAMVÆRSFRADRAG_REFERANSE,
                )
            },
        )
    }

    @Test
    @DisplayName("Skal ikke beregne barnebidrag ved avvik")
    fun skalIkkeBeregneBarnebidragVedAvvik() {
        byggBarnebidragPeriodeGrunnlagCore()
        byggAvvik()

        `when`(barnebidragPeriodeMock.validerInput(any())).thenReturn(avvikListe)

        val beregnbidragsevneResultatCore = barnebidragCoreWithMock.beregnBarnebidrag(beregnBarnebidragGrunnlagCore)

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

    private fun byggBarnebidragPeriodeGrunnlagCore() {
        val bidragsevnePeriodeListe =
            listOf(
                BidragsevnePeriodeCore(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    belop = BigDecimal.valueOf(100000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(20000),
                ),
            )

        val bPsAndelUnderholdskostnadPeriodeListe =
            listOf(
                BPsAndelUnderholdskostnadPeriodeCore(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    soknadsbarnPersonId = 1,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    andelProsent = BigDecimal.valueOf(100000),
                    andelBelop = BigDecimal.valueOf(20000),
                    barnetErSelvforsorget = false,
                ),
            )

        val samvaersfradragPeriodeListe =
            listOf(
                SamvaersfradragPeriodeCore(
                    referanse = SAMVÆRSFRADRAG_REFERANSE,
                    soknadsbarnPersonId = 1,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    belop = BigDecimal.valueOf(1000),
                ),
            )

        val deltBostedPeriodeListe =
            listOf(
                DeltBostedPeriodeCore(
                    referanse = DELT_BOSTED_REFERANSE,
                    soknadsbarnPersonId = 1,
                    periode = PeriodeCore(LocalDate.parse("2017-01-01"), null),
                    deltBostedIPeriode = false,
                ),
            )

        val barnetilleggBPPeriodeListe =
            listOf(
                BarnetilleggPeriodeCore(
                    referanse = BARNETILLEGG_BP_REFERANSE,
                    soknadsbarnPersonId = 1,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    belop = BigDecimal.valueOf(100),
                    skattProsent = BigDecimal.valueOf(10),
                ),
            )

        val barnetilleggBMPeriodeListe =
            listOf(
                BarnetilleggPeriodeCore(
                    referanse = BARNETILLEGG_BM_REFERANSE,
                    soknadsbarnPersonId = 1,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    belop = BigDecimal.valueOf(100),
                    skattProsent = BigDecimal.valueOf(10),
                ),
            )

        val barnetilleggForsvaretPeriodeListe =
            listOf(
                BarnetilleggForsvaretPeriodeCore(
                    referanse = BARNETILLEGG_FORSVARET_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    barnetilleggForsvaretIPeriode = false,
                ),
            )

        val andreLopendeBidragPeriodeListe =
            listOf(
                AndreLopendeBidragPeriodeCore(
                    referanse = ANDRE_LOPENDE_BIDRAG_REFERANSE,
                    periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = null),
                    barnPersonId = 1,
                    bidragBelop = BigDecimal.ZERO,
                    samvaersfradragBelop = BigDecimal.ZERO,
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

        beregnBarnebidragGrunnlagCore =
            BeregnBarnebidragGrunnlagCore(
                beregnDatoFra = LocalDate.parse("2017-01-01"),
                beregnDatoTil = LocalDate.parse("2020-01-01"),
                bidragsevnePeriodeListe = bidragsevnePeriodeListe,
                bPsAndelUnderholdskostnadPeriodeListe = bPsAndelUnderholdskostnadPeriodeListe,
                samvaersfradragPeriodeListe = samvaersfradragPeriodeListe,
                deltBostedPeriodeListe = deltBostedPeriodeListe,
                barnetilleggBPPeriodeListe = barnetilleggBPPeriodeListe,
                barnetilleggBMPeriodeListe = barnetilleggBMPeriodeListe,
                barnetilleggForsvaretPeriodeListe = barnetilleggForsvaretPeriodeListe,
                andreLopendeBidragPeriodeListe = andreLopendeBidragPeriodeListe,
                sjablonPeriodeListe = sjablonPeriodeListe,
            )
    }

    private fun byggBarnebidragPeriodeResultat() {
        val periodeResultatListe =
            listOf(
                ResultatPeriode(
                    periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2018-01-01")),
                    resultatListe =
                    listOf(
                        ResultatBeregning(
                            soknadsbarnPersonId = 1,
                            belop = BigDecimal.valueOf(1),
                            kode = ResultatkodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG,
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
                        bidragsevne =
                        Bidragsevne(
                            referanse = BIDRAGSEVNE_REFERANSE,
                            bidragsevneBelop = BigDecimal.valueOf(1000),
                            tjuefemProsentInntekt = BigDecimal.valueOf(12000),
                        ),
                        grunnlagPerBarnListe =
                        listOf(
                            GrunnlagBeregningPerBarn(
                                soknadsbarnPersonId = 1,
                                bPsAndelUnderholdskostnad =
                                BPsAndelUnderholdskostnad(
                                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                                    andelProsent = BigDecimal.valueOf(60),
                                    andelBelop = BigDecimal.valueOf(8000),
                                    barnetErSelvforsorget = false,
                                ),
                                samvaersfradrag =
                                Samvaersfradrag(
                                    referanse = SAMVÆRSFRADRAG_REFERANSE,
                                    belop = BigDecimal.valueOf(100),
                                ),
                                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = false),
                                barnetilleggBP =
                                Barnetillegg(
                                    referanse = BARNETILLEGG_BP_REFERANSE,
                                    belop = BigDecimal.valueOf(100),
                                    skattProsent = BigDecimal.valueOf(10),
                                ),
                                barnetilleggBM =
                                Barnetillegg(
                                    referanse = BARNETILLEGG_BM_REFERANSE,
                                    belop = BigDecimal.valueOf(1000),
                                    skattProsent = BigDecimal.valueOf(10),
                                ),
                            ),
                        ),
                        barnetilleggForsvaret =
                        BarnetilleggForsvaret(
                            referanse = BARNETILLEGG_FORSVARET_REFERANSE,
                            barnetilleggForsvaretIPeriode = false,
                        ),
                        andreLopendeBidragListe =
                        listOf(
                            AndreLopendeBidrag(
                                referanse = ANDRE_LOPENDE_BIDRAG_REFERANSE,
                                barnPersonId = 1,
                                bidragBelop = BigDecimal.ZERO,
                                samvaersfradragBelop = BigDecimal.ZERO,
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

        beregnBarnebidragPeriodeResultat = BeregnBarnebidragResultat(periodeResultatListe)
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
