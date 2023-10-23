package no.nav.bidrag.beregn.bpsandelunderholdskostnad

import no.nav.bidrag.beregn.TestUtil.INNTEKT_BB_REFERANSE
import no.nav.bidrag.beregn.TestUtil.INNTEKT_BM_REFERANSE
import no.nav.bidrag.beregn.TestUtil.INNTEKT_BP_REFERANSE
import no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnetBPsAndelUnderholdskostnadResultat
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Underholdskostnad
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadGrunnlagCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.InntektPeriodeCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.UnderholdskostnadPeriodeCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.domain.enums.AvvikType
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
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
class BPsAndelUnderholdskostnadCoreTest {

    private lateinit var bPsAndelunderholdskostnadCoreWithMock: BPsAndelUnderholdskostnadCore

    @Mock
    private lateinit var bPsAndelunderholdskostnadPeriodeMock: BPsAndelUnderholdskostnadPeriode

    private lateinit var beregnBPsAndelUnderholdskostnadGrunnlagCore: BeregnBPsAndelUnderholdskostnadGrunnlagCore
    private lateinit var bPsAndelunderholdskostnadPeriodeResultat: BeregnetBPsAndelUnderholdskostnadResultat
    private lateinit var avvikListe: List<Avvik>

    @BeforeEach
    fun initMocksAndService() {
        bPsAndelunderholdskostnadCoreWithMock = BPsAndelUnderholdskostnadCoreImpl(bPsAndelunderholdskostnadPeriodeMock)
    }

    @Test
    @DisplayName("Skal beregne BPsAndel av underholdskostnad")
    fun skalBeregneBPsAndelunderholdskostnad() {
        byggBPsAndelUnderholdskostnadPeriodeGrunnlagCore()
        byggBPsAndelUnderholdskostnadPeriodeResultat()

        `when`(bPsAndelunderholdskostnadPeriodeMock.beregnPerioder(any())).thenReturn(bPsAndelunderholdskostnadPeriodeResultat)

        val beregnBPsAndelUnderholdskostnadResultatCore = bPsAndelunderholdskostnadCoreWithMock.beregnBPsAndelUnderholdskostnad(
            beregnBPsAndelUnderholdskostnadGrunnlagCore
        )

        assertAll(
            Executable { assertThat(beregnBPsAndelUnderholdskostnadResultatCore).isNotNull() },
            Executable { assertThat(beregnBPsAndelUnderholdskostnadResultatCore.avvikListe).isEmpty() },
            Executable { assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe.size).isEqualTo(3) },

            Executable { assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable {
                assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[0].resultat.andelProsent).isEqualTo(
                    BigDecimal.valueOf(
                        0.10
                    )
                )
            },
            Executable {
                assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[0]).isEqualTo(
                    INNTEKT_BB_REFERANSE
                )
            },
            Executable {
                assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[1]).isEqualTo(
                    INNTEKT_BM_REFERANSE
                )
            },
            Executable {
                assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[2]).isEqualTo(
                    INNTEKT_BP_REFERANSE
                )
            },
            Executable {
                assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[4]).isEqualTo(
                    UNDERHOLDSKOSTNAD_REFERANSE
                )
            },

            Executable { assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable {
                assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[1].resultat.andelProsent).isEqualTo(
                    BigDecimal.valueOf(
                        0.20
                    )
                )
            },

            Executable { assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable {
                assertThat(beregnBPsAndelUnderholdskostnadResultatCore.resultatPeriodeListe[2].resultat.andelProsent).isEqualTo(
                    BigDecimal.valueOf(
                        0.30
                    )
                )
            }
        )
    }

    @Test
    @DisplayName("Skal ikke beregne BPs andel av underholdskostnad ved avvik")
    fun skalIkkeBeregneAndelVedAvvik() {
        byggBPsAndelUnderholdskostnadPeriodeGrunnlagCore()
        byggAvvik()

        `when`(bPsAndelunderholdskostnadPeriodeMock.validerInput(any())).thenReturn(avvikListe)

        val beregnbidragsevneResultatCore = bPsAndelunderholdskostnadCoreWithMock.beregnBPsAndelUnderholdskostnad(
            beregnBPsAndelUnderholdskostnadGrunnlagCore
        )

        assertAll(
            Executable { assertThat(beregnbidragsevneResultatCore).isNotNull() },
            Executable { assertThat(beregnbidragsevneResultatCore.avvikListe).isNotEmpty() },
            Executable { assertThat(beregnbidragsevneResultatCore.avvikListe).hasSize(1) },
            Executable { assertThat(beregnbidragsevneResultatCore.avvikListe[0].avvikTekst).isEqualTo("beregnDatoTil må være etter beregnDatoFra") },
            Executable { assertThat(beregnbidragsevneResultatCore.avvikListe[0].avvikType).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()) },
            Executable { assertThat(beregnbidragsevneResultatCore.resultatPeriodeListe).isEmpty() }
        )
    }

    private fun byggBPsAndelUnderholdskostnadPeriodeGrunnlagCore() {
        val underholdskostnadPeriodeListe = listOf(
            UnderholdskostnadPeriodeCore(
                referanse = UNDERHOLDSKOSTNAD_REFERANSE,
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.valueOf(1000)
            )
        )

        val inntektBPPeriodeListe = listOf(
            InntektPeriodeCore(
                referanse = INNTEKT_BP_REFERANSE,
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(111),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val inntektBMPeriodeListe = listOf(
            InntektPeriodeCore(
                referanse = INNTEKT_BM_REFERANSE,
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(222),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val inntektBBPeriodeListe = listOf(
            InntektPeriodeCore(
                referanse = INNTEKT_BB_REFERANSE,
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(333),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val sjablonPeriodeListe = listOf(
            SjablonPeriodeCore(
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnholdCore(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1600)))
            )
        )
        beregnBPsAndelUnderholdskostnadGrunnlagCore = BeregnBPsAndelUnderholdskostnadGrunnlagCore(
            LocalDate.parse("2017-01-01"),
            LocalDate.parse("2020-01-01"),
            1,
            underholdskostnadPeriodeListe,
            inntektBPPeriodeListe,
            inntektBMPeriodeListe,
            inntektBBPeriodeListe,
            sjablonPeriodeListe
        )
    }

    private fun byggBPsAndelUnderholdskostnadPeriodeResultat() {
        val inntektBPListe = listOf(
            Inntekt(
                referanse = INNTEKT_BP_REFERANSE,
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(111),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val inntektBMListe = listOf(
            Inntekt(
                referanse = INNTEKT_BM_REFERANSE,
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(222),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val inntektBBListe = listOf(
            Inntekt(
                referanse = INNTEKT_BB_REFERANSE,
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(333),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val periodeResultatListe = listOf(
            ResultatPeriode(
                soknadsbarnPersonId = 1,
                periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2018-01-01")),
                resultat = ResultatBeregning(
                    andelProsent = BigDecimal.valueOf(0.10),
                    andelBelop = BigDecimal.valueOf(100),
                    barnetErSelvforsorget = false,
                    sjablonListe = listOf(
                        SjablonPeriodeNavnVerdi(
                            periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                            verdi = BigDecimal.valueOf(1600)
                        )
                    )
                ),
                grunnlag = GrunnlagBeregning(
                    underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                    inntektBPListe = inntektBPListe,
                    inntektBMListe = inntektBMListe,
                    inntektBBListe = inntektBBListe,
                    sjablonListe = listOf(
                        SjablonPeriode(
                            sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            sjablon = Sjablon(
                                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                                nokkelListe = emptyList(),
                                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1600)))
                            )
                        )
                    )
                )
            ),
            ResultatPeriode(
                soknadsbarnPersonId = 1,
                periode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-01-01")),
                resultat = ResultatBeregning(
                    andelProsent = BigDecimal.valueOf(0.20),
                    andelBelop = BigDecimal.valueOf(200),
                    barnetErSelvforsorget = false,
                    sjablonListe = listOf(
                        SjablonPeriodeNavnVerdi(
                            periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                            verdi = BigDecimal.valueOf(1600)
                        )
                    )
                ),
                grunnlag = GrunnlagBeregning(
                    underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                    inntektBPListe = inntektBPListe,
                    inntektBMListe = inntektBMListe,
                    inntektBBListe = inntektBBListe,
                    sjablonListe = listOf(
                        SjablonPeriode(
                            sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            sjablon = Sjablon(
                                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                                nokkelListe = emptyList(),
                                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1640)))
                            )
                        )
                    )
                )
            ),
            ResultatPeriode(
                soknadsbarnPersonId = 1,
                periode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                resultat = ResultatBeregning(
                    andelProsent = BigDecimal.valueOf(0.30),
                    andelBelop = BigDecimal.valueOf(300),
                    barnetErSelvforsorget = false,
                    sjablonListe = listOf(
                        SjablonPeriodeNavnVerdi(
                            periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                            verdi = BigDecimal.valueOf(1600)
                        )
                    )
                ),
                grunnlag = GrunnlagBeregning(
                    underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                    inntektBPListe = inntektBPListe,
                    inntektBMListe = inntektBMListe,
                    inntektBBListe = inntektBBListe,
                    sjablonListe = listOf(
                        SjablonPeriode(
                            sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            sjablon = Sjablon(
                                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                                nokkelListe = emptyList(),
                                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1680)))
                            )
                        )
                    )
                )
            )
        )

        bPsAndelunderholdskostnadPeriodeResultat = BeregnetBPsAndelUnderholdskostnadResultat(periodeResultatListe)
    }

    private fun byggAvvik() {
        avvikListe = listOf(
            Avvik(avvikTekst = "beregnDatoTil må være etter beregnDatoFra", avvikType = AvvikType.DATO_FOM_ETTER_DATO_TIL)
        )
    }

    companion object MockitoHelper {
        fun <T> any(): T = Mockito.any()
    }
}
