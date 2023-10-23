package no.nav.bidrag.beregn.forholdsmessigfordeling

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingGrunnlag
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSakPeriode
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BidragsevnePeriode
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagPerBarn
import no.nav.bidrag.beregn.forholdsmessigfordeling.periode.ForholdsmessigFordelingPeriode.Companion.getInstance
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeBarnebidrag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

internal class ForholdsmessigFordelingPeriodeTest {

    private val forholdsmessigFordelingPeriode = getInstance()

    @Test
    @DisplayName("Test periodisering med to saker med to perioder og ett barn hver og full evne")
    fun testPeriodiseringToSakerToPerioderEttBarnHverFullEvne() {
        val beregnDatoFra = LocalDate.parse("2019-01-01")
        val beregnDatoTil = LocalDate.parse("2021-01-01")

        val bidragsevnePeriodeListe = listOf(
            BidragsevnePeriode(
                bidragsevneDatoFraTil = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2021-01-01")),
                bidragsevneBelop = BigDecimal.valueOf(15000),
                tjuefemProsentInntekt = BigDecimal.valueOf(14000)
            )
        )

        val beregnetBidragSakPeriodeListe = listOf(
            BeregnetBidragSakPeriode(
                saksnr = 1234567,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-09-01")),
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(1000)))
            ),
            BeregnetBidragSakPeriode(
                saksnr = 1234567,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2020-09-01"), datoTil = LocalDate.parse("2021-01-01")),
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(2000)))
            ),
            BeregnetBidragSakPeriode(
                saksnr = 7654321,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-06-01")),
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(500)))
            ),
            BeregnetBidragSakPeriode(
                saksnr = 7654321,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2020-06-01"), datoTil = LocalDate.parse("2020-11-01")),
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(1200)))
            )
        )

        val beregnForholdsmessigFordelingGrunnlag = BeregnForholdsmessigFordelingGrunnlag(
            beregnDatoFra = beregnDatoFra,
            beregnDatoTil = beregnDatoTil,
            bidragsevnePeriodeListe = bidragsevnePeriodeListe,
            beregnetBidragPeriodeListe = beregnetBidragSakPeriodeListe
        )

        val resultat = forholdsmessigFordelingPeriode.beregnPerioder(beregnForholdsmessigFordelingGrunnlag)

        assertAll(
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(5) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2020-06-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2020-06-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2020-09-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoFom).isEqualTo(LocalDate.parse("2020-09-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoTil).isEqualTo(LocalDate.parse("2020-11-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].periode.datoFom).isEqualTo(LocalDate.parse("2020-11-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[4].periode.datoTil).isEqualTo(LocalDate.parse("2021-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatBeregningListe[0].saksnr).isEqualTo(1234567) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatBeregningListe[1].saksnr).isEqualTo(7654321) },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[0].resultatBeregningListe[0].resultatPerBarnListe[0].belop
                        .compareTo(BigDecimal.valueOf(1000))
                ).isZero()
            },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[1].resultatBeregningListe[0].resultatPerBarnListe[0].belop
                        .compareTo(BigDecimal.valueOf(1000))
                ).isZero()
            },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[3].resultatBeregningListe[1].resultatPerBarnListe[0].belop
                        .compareTo(BigDecimal.valueOf(1200))
                ).isZero()
            },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatBeregningListe[0].resultatPerBarnListe[0].barnPersonId).isEqualTo(1) }
        )
    }

    @Test
    @DisplayName("Basic test som tester periodisering med én sak og ett barn, to perioder og full evne")
    fun testPeriodiseringEnSakEttBarnToPerioderManglendeEvne() {
        val beregnDatoFra = LocalDate.parse("2019-01-01")
        val beregnDatoTil = LocalDate.parse("2021-01-01")

        val bidragsevnePeriodeListe = listOf(
            BidragsevnePeriode(
                bidragsevneDatoFraTil = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2021-01-01")),
                bidragsevneBelop = BigDecimal.valueOf(15000),
                tjuefemProsentInntekt = BigDecimal.valueOf(17000)
            )
        )

        val beregnetBidragSakPeriodeListe = listOf(
            BeregnetBidragSakPeriode(
                saksnr = 1234567,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-09-01")),
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(1000)))
            ),
            BeregnetBidragSakPeriode(
                saksnr = 1234567,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2020-09-01"), datoTil = LocalDate.parse("2021-01-01")),
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(2000)))
            ),
            BeregnetBidragSakPeriode(
                saksnr = 7654321,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-06-01")),
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(15000)))
            ),
            BeregnetBidragSakPeriode(
                saksnr = 7654321,
                periodeDatoFraTil = Periode(datoFom = LocalDate.parse("2020-06-01"), datoTil = LocalDate.parse("2020-11-01")),
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(1200)))
            )
        )

        val beregnForholdsmessigFordelingGrunnlag = BeregnForholdsmessigFordelingGrunnlag(
            beregnDatoFra = beregnDatoFra,
            beregnDatoTil = beregnDatoTil,
            bidragsevnePeriodeListe = bidragsevnePeriodeListe,
            beregnetBidragPeriodeListe = beregnetBidragSakPeriodeListe
        )

        val resultat = forholdsmessigFordelingPeriode.beregnPerioder(beregnForholdsmessigFordelingGrunnlag)

        assertAll(
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(5) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatBeregningListe[0].saksnr).isEqualTo(1234567) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatBeregningListe[1].saksnr).isEqualTo(7654321) },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[0].resultatBeregningListe[0].resultatPerBarnListe[0].belop.compareTo(
                        BigDecimal.valueOf(
                            1000
                        )
                    )
                ).isZero()
            },
            Executable {
                assertThat(resultat.resultatPeriodeListe[0].resultatBeregningListe[0].resultatPerBarnListe[0].kode).isEqualTo(
                    ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_INGEN_ENDRING
                )
            },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[1].resultatBeregningListe[0].resultatPerBarnListe[0].belop.compareTo(
                        BigDecimal.valueOf(
                            940
                        )
                    )
                ).isZero()
            },
            Executable {
                assertThat(resultat.resultatPeriodeListe[1].resultatBeregningListe[0].resultatPerBarnListe[0].kode).isEqualTo(
                    ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET
                )
            },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[1].resultatBeregningListe[1].resultatPerBarnListe[0].belop.compareTo(
                        BigDecimal.valueOf(
                            14060
                        )
                    )
                ).isZero()
            },
            Executable {
                assertThat(resultat.resultatPeriodeListe[1].resultatBeregningListe[1].resultatPerBarnListe[0].kode).isEqualTo(
                    ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET
                )
            },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatBeregningListe[0].resultatPerBarnListe[0].barnPersonId).isEqualTo(1) }
        )
    }
}
