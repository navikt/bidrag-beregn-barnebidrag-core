package no.nav.bidrag.beregn.samvaersfradrag

import no.nav.bidrag.beregn.TestUtil.SAMVAERSKLASSE_REFERANSE
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
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnetSamvaersfradragResultat
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode
import no.nav.bidrag.beregn.samvaersfradrag.bo.Samvaersklasse
import no.nav.bidrag.beregn.samvaersfradrag.bo.SoknadsbarnAlder
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragGrunnlagCore
import no.nav.bidrag.beregn.samvaersfradrag.dto.SamvaersklassePeriodeCore
import no.nav.bidrag.beregn.samvaersfradrag.dto.SoknadsbarnCore
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode
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
class SamvaersfradragCoreTest {

    private lateinit var samvaersfradragCoreWithMock: SamvaersfradragCore

    @Mock
    private lateinit var samvaersfradragPeriodeMock: SamvaersfradragPeriode

    private lateinit var beregnSamvaersfradragGrunnlagCore: BeregnSamvaersfradragGrunnlagCore
    private lateinit var samvaersfradragPeriodeResultat: BeregnetSamvaersfradragResultat
    private lateinit var avvikListe: List<Avvik>

    @BeforeEach
    fun initMocksAndService() {
        samvaersfradragCoreWithMock = SamvaersfradragCoreImpl(samvaersfradragPeriodeMock)
    }

    @Test
    @DisplayName("Skal beregne samvaersfradrag")
    fun skalBeregneSamvaersfradrag() {
        byggSamvaersfradragPeriodeGrunnlagCore()
        byggSamvaersfradragPeriodeResultat()

        `when`(samvaersfradragPeriodeMock.beregnPerioder(any())).thenReturn(samvaersfradragPeriodeResultat)

        val beregnSamvaersfradragResultatCore = samvaersfradragCoreWithMock.beregnSamvaersfradrag(beregnSamvaersfradragGrunnlagCore)

        assertAll(
            Executable { assertThat(beregnSamvaersfradragResultatCore).isNotNull() },
            Executable { assertThat(beregnSamvaersfradragResultatCore.avvikListe).isEmpty() },
            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe.size).isEqualTo(3) },

            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[0].resultat.belop).isEqualTo(BigDecimal.valueOf(666)) },
            Executable {
                assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[0]).isEqualTo(
                    SAMVAERSKLASSE_REFERANSE
                )
            },
            Executable {
                assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[1]).isEqualTo(
                    SOKNADSBARN_REFERANSE
                )
            },

            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[1].resultat.belop).isEqualTo(BigDecimal.valueOf(667)) },

            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe[2].resultat.belop).isEqualTo(BigDecimal.valueOf(668)) }
        )
    }

    @Test
    @DisplayName("Skal ikke beregne samvaersfradrag ved avvik")
    fun skalIkkeBeregneAndelVedAvvik() {
        byggSamvaersfradragPeriodeGrunnlagCore()
        byggAvvik()

        `when`(samvaersfradragPeriodeMock.validerInput(any())).thenReturn(avvikListe)

        val beregnSamvaersfradragResultatCore = samvaersfradragCoreWithMock.beregnSamvaersfradrag(beregnSamvaersfradragGrunnlagCore)

        assertAll(
            Executable { assertThat(beregnSamvaersfradragResultatCore).isNotNull() },
            Executable { assertThat(beregnSamvaersfradragResultatCore.avvikListe).isNotEmpty() },
            Executable { assertThat(beregnSamvaersfradragResultatCore.avvikListe).hasSize(1) },
            Executable { assertThat(beregnSamvaersfradragResultatCore.avvikListe[0].avvikTekst).isEqualTo("beregnDatoTil må være etter beregnDatoFra") },
            Executable { assertThat(beregnSamvaersfradragResultatCore.avvikListe[0].avvikType).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()) },
            Executable { assertThat(beregnSamvaersfradragResultatCore.resultatPeriodeListe).isEmpty() }
        )
    }

    private fun byggSamvaersfradragPeriodeGrunnlagCore() {
        val soknadsbarn = SoknadsbarnCore(referanse = SOKNADSBARN_REFERANSE, personId = 1, fodselsdato = LocalDate.parse("2017-08-17"))

        val samvaersklassePeriodeListe = listOf(
            SamvaersklassePeriodeCore(
                referanse = SAMVAERSKLASSE_REFERANSE,
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                samvaersklasse = "03"
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

        beregnSamvaersfradragGrunnlagCore = BeregnSamvaersfradragGrunnlagCore(
            beregnDatoFra = LocalDate.parse("2017-01-01"),
            beregnDatoTil = LocalDate.parse("2020-01-01"),
            soknadsbarn = soknadsbarn,
            samvaersklassePeriodeListe = samvaersklassePeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )
    }

    private fun byggSamvaersfradragPeriodeResultat() {
        val periodeResultatListe = listOf(
            ResultatPeriode(
                soknadsbarnPersonId = 1,
                periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2018-01-01")),
                resultat = ResultatBeregning(
                    belop = BigDecimal.valueOf(666),
                    sjablonListe = listOf(
                        SjablonPeriodeNavnVerdi(
                            periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                            verdi = BigDecimal.valueOf(1600)
                        )
                    )
                ),
                grunnlag = GrunnlagBeregning(
                    soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 4),
                    samvaersklasse = Samvaersklasse(referanse = SAMVAERSKLASSE_REFERANSE, samvaersklasse = "03"),
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
                    belop = BigDecimal.valueOf(667),
                    sjablonListe = listOf(
                        SjablonPeriodeNavnVerdi(
                            periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                            verdi = BigDecimal.valueOf(1600)
                        )
                    )
                ),
                grunnlag = GrunnlagBeregning(
                    soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 4),
                    samvaersklasse = Samvaersklasse(referanse = SAMVAERSKLASSE_REFERANSE, samvaersklasse = "03"),
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
                    belop = BigDecimal.valueOf(668),
                    sjablonListe = listOf(
                        SjablonPeriodeNavnVerdi(
                            periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                            navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                            verdi = BigDecimal.valueOf(1600)
                        )
                    )
                ),
                grunnlag = GrunnlagBeregning(
                    soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 4),
                    samvaersklasse = Samvaersklasse(referanse = SAMVAERSKLASSE_REFERANSE, samvaersklasse = "03"),
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
            )
        )

        samvaersfradragPeriodeResultat = BeregnetSamvaersfradragResultat(periodeResultatListe)
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
