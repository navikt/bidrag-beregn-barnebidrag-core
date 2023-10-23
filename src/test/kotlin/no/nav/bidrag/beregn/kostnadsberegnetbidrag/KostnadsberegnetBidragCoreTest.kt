package no.nav.bidrag.beregn.kostnadsberegnetbidrag

import no.nav.bidrag.beregn.TestUtil.BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE
import no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnad
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnetKostnadsberegnetBidragResultat
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Samvaersfradrag
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Underholdskostnad
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BPsAndelUnderholdskostnadPeriodeCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnKostnadsberegnetBidragGrunnlagCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.SamvaersfradragPeriodeCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.UnderholdskostnadPeriodeCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode
import no.nav.bidrag.domain.enums.AvvikType
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
class KostnadsberegnetBidragCoreTest {

    private lateinit var kostnadsberegnetBidragCoreWithMock: KostnadsberegnetBidragCore

    @Mock
    private lateinit var kostnadsberegnetBidragPeriodeMock: KostnadsberegnetBidragPeriode

    private lateinit var beregnKostnadsberegnetBidragGrunnlagCore: BeregnKostnadsberegnetBidragGrunnlagCore
    private lateinit var kostnadsberegnetBidragPeriodeResultat: BeregnetKostnadsberegnetBidragResultat
    private lateinit var avvikListe: List<Avvik>

    @BeforeEach
    fun initMocksAndService() {
        kostnadsberegnetBidragCoreWithMock = KostnadsberegnetBidragCoreImpl(kostnadsberegnetBidragPeriodeMock)
    }

    @Test
    @DisplayName("Skal beregne kostnadsberegnet bidrag")
    fun skalBeregneKostnadsberegnetBidrag() {
        byggKostnadsberegnetBidragPeriodeGrunnlagCore()
        byggKostnadsberegnetBidragPeriodeResultat()

        `when`(kostnadsberegnetBidragPeriodeMock.beregnPerioder(any())).thenReturn(kostnadsberegnetBidragPeriodeResultat)

        val beregnKostnadsberegnetBidragResultatCore = kostnadsberegnetBidragCoreWithMock.beregnKostnadsberegnetBidrag(
            beregnKostnadsberegnetBidragGrunnlagCore
        )

        assertAll(
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore).isNotNull() },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.avvikListe).isEmpty() },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe.size).isEqualTo(3) },

            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2017-01-01")) },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[0].resultat.belop).isEqualTo(BigDecimal.valueOf(666)) },
            Executable {
                assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[0]).isEqualTo(
                    BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE
                )
            },
            Executable {
                assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[1]).isEqualTo(
                    SAMVAERSFRADRAG_REFERANSE
                )
            },
            Executable {
                assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[0].grunnlagReferanseListe[2]).isEqualTo(
                    UNDERHOLDSKOSTNAD_REFERANSE
                )
            },

            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2018-01-01")) },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[1].resultat.belop).isEqualTo(BigDecimal.valueOf(667)) },

            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe[2].resultat.belop).isEqualTo(BigDecimal.valueOf(668)) }
        )
    }

    @Test
    @DisplayName("Skal ikke beregne Kostnadsberegnet bidrag ved avvik")
    fun skalIkkeBeregneKostnadsberegnetBidragVedAvvik() {
        byggKostnadsberegnetBidragPeriodeGrunnlagCore()
        byggAvvik()

        `when`(kostnadsberegnetBidragPeriodeMock.validerInput(any())).thenReturn(avvikListe)

        val beregnKostnadsberegnetBidragResultatCore = kostnadsberegnetBidragCoreWithMock.beregnKostnadsberegnetBidrag(
            beregnKostnadsberegnetBidragGrunnlagCore
        )

        assertAll(
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore).isNotNull() },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.avvikListe).isNotEmpty() },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.avvikListe).hasSize(1) },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.avvikListe[0].avvikTekst).isEqualTo("beregnDatoTil må være etter beregnDatoFra") },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.avvikListe[0].avvikType).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()) },
            Executable { assertThat(beregnKostnadsberegnetBidragResultatCore.resultatPeriodeListe).isEmpty() }
        )
    }

    private fun byggKostnadsberegnetBidragPeriodeGrunnlagCore() {
        val underholdskostnadPeriodeListe = listOf(
            UnderholdskostnadPeriodeCore(
                referanse = UNDERHOLDSKOSTNAD_REFERANSE,
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.valueOf(10000)
            )
        )

        val bPsAndelUnderholdskostnadPeriodeListe = listOf(
            BPsAndelUnderholdskostnadPeriodeCore(
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                andelProsent = BigDecimal.valueOf(20)
            )
        )

        val samvaersfradragPeriodeListe = listOf(
            SamvaersfradragPeriodeCore(
                referanse = SAMVAERSFRADRAG_REFERANSE,
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.valueOf(100)
            )
        )

        beregnKostnadsberegnetBidragGrunnlagCore = BeregnKostnadsberegnetBidragGrunnlagCore(
            beregnDatoFra = LocalDate.parse("2017-01-01"),
            beregnDatoTil = LocalDate.parse("2020-01-01"),
            soknadsbarnPersonId = 1,
            underholdskostnadPeriodeListe = underholdskostnadPeriodeListe,
            bPsAndelUnderholdskostnadPeriodeListe = bPsAndelUnderholdskostnadPeriodeListe,
            samvaersfradragPeriodeListe = samvaersfradragPeriodeListe
        )
    }

    private fun byggKostnadsberegnetBidragPeriodeResultat() {
        val periodeResultatListe = listOf(
            ResultatPeriode(
                soknadsbarnPersonId = 1,
                periode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2018-01-01")),
                resultat = ResultatBeregning(belop = BigDecimal.valueOf(666)),
                grunnlag = GrunnlagBeregning(
                    underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(10000)),
                    bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                        referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                        andelProsent = BigDecimal.valueOf(20)
                    ),
                    samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.valueOf(100))
                )
            ),
            ResultatPeriode(
                soknadsbarnPersonId = 1,
                periode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2019-01-01")),
                resultat = ResultatBeregning(belop = BigDecimal.valueOf(667)),
                grunnlag = GrunnlagBeregning(
                    underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(10000)),
                    bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                        referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                        andelProsent = BigDecimal.valueOf(20)
                    ),
                    samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.valueOf(100))
                )
            ),
            ResultatPeriode(
                soknadsbarnPersonId = 1,
                periode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-01-01")),
                resultat = ResultatBeregning(belop = BigDecimal.valueOf(668)),
                grunnlag = GrunnlagBeregning(
                    underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(10000)),
                    bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                        referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                        andelProsent = BigDecimal.valueOf(20)
                    ),
                    samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.valueOf(100))
                )
            )
        )

        kostnadsberegnetBidragPeriodeResultat = BeregnetKostnadsberegnetBidragResultat(periodeResultatListe)
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
