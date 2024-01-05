package no.nav.bidrag.beregn.forholdsmessigfordeling

import no.nav.bidrag.beregn.forholdsmessigfordeling.beregning.ForholdsmessigFordelingBeregning.Companion.getInstance
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSak
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.Bidragsevne
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagPerBarn
import no.nav.bidrag.domene.enums.beregning.ResultatkodeBarnebidrag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal

@DisplayName("Test av beregning av forholdsmessig fordeling")
internal class ForholdsmessigFordelingBeregningTest {
    private val beregnetBidragSakListe = mutableListOf<BeregnetBidragSak>()
    private val forholdsmessigFordelingBeregning = getInstance()

    @DisplayName("test med én sak og ett barn")
    @Test
    fun testEnSakEttBarn() {
        val grunnlagPerBarnListe =
            listOf(
                GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(1000)),
            )
        beregnetBidragSakListe.add(BeregnetBidragSak(saksnr = 1234567, grunnlagPerBarnListe = grunnlagPerBarnListe))
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregningPeriodisert(
                bidragsevne = Bidragsevne(belop = BigDecimal.valueOf(1000), tjuefemProsentInntekt = BigDecimal.valueOf(1000)),
                beregnetBidragSakListe = beregnetBidragSakListe,
            )

        val resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].saksnr).isEqualTo(1234567) },
            Executable { assertThat(resultat.size).isEqualTo(1) },
            Executable { assertThat(resultat[0].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(1000))).isZero() },
            Executable {
                assertThat(
                    resultat[0].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_INGEN_ENDRING)
            },
        )
    }

    @DisplayName("test med én sak og tre barn")
    @Test
    fun testEnSakTreBarn() {
        val grunnlagPerBarnListe =
            listOf(
                GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(1000)),
                GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(2000)),
                GrunnlagPerBarn(barnPersonId = 3, bidragBelop = BigDecimal.valueOf(3000)),
            )
        beregnetBidragSakListe.add(BeregnetBidragSak(saksnr = 1234567, grunnlagPerBarnListe = grunnlagPerBarnListe))
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregningPeriodisert(
                bidragsevne = Bidragsevne(belop = BigDecimal.valueOf(10000), tjuefemProsentInntekt = BigDecimal.valueOf(10000)),
                beregnetBidragSakListe = beregnetBidragSakListe,
            )

        val resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].saksnr).isEqualTo(1234567) },
            Executable { assertThat(resultat.size).isEqualTo(1) },
            Executable { assertThat(resultat[0].resultatPerBarnListe.size).isEqualTo(3) },
            Executable { assertThat(resultat[0].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(1000))).isZero() },
            Executable { assertThat(resultat[0].resultatPerBarnListe[1].belop.compareTo(BigDecimal.valueOf(2000))).isZero() },
            Executable { assertThat(resultat[0].resultatPerBarnListe[2].belop.compareTo(BigDecimal.valueOf(3000))).isZero() },
            Executable {
                assertThat(
                    resultat[0].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_INGEN_ENDRING)
            },
        )
    }

    @DisplayName("tester med to saker, tre barn og bidragsevnen dekker den totale bdragssummen")
    @Test
    fun testToSakerTreBarnFullEvne() {
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 1234567,
                grunnlagPerBarnListe =
                listOf(
                    GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(1000)),
                    GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(2000)),
                ),
            ),
        )
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 7654321,
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 3, bidragBelop = BigDecimal.valueOf(3000))),
            ),
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregningPeriodisert(
                bidragsevne = Bidragsevne(belop = BigDecimal.valueOf(10000), tjuefemProsentInntekt = BigDecimal.valueOf(10000)),
                beregnetBidragSakListe = beregnetBidragSakListe,
            )

        val resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].saksnr).isEqualTo(1234567) },
            Executable { assertThat(resultat[1].saksnr).isEqualTo(7654321) },
            Executable { assertThat(resultat.size).isEqualTo(2) },
            Executable { assertThat(resultat[0].resultatPerBarnListe.size).isEqualTo(2) },
            Executable { assertThat(resultat[1].resultatPerBarnListe.size).isEqualTo(1) },
            Executable { assertThat(resultat[0].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(1000))).isZero() },
            Executable { assertThat(resultat[0].resultatPerBarnListe[1].belop.compareTo(BigDecimal.valueOf(2000))).isZero() },
            Executable { assertThat(resultat[1].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(3000))).isZero() },
            Executable {
                assertThat(
                    resultat[0].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_INGEN_ENDRING)
            },
        )
    }

    @DisplayName("tester med to saker, tre barn og bidragsevnen ikke dekker den totale bidragssummen")
    @Test
    fun testToSakerTreBarnBegrensetEvne() {
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 1234567,
                grunnlagPerBarnListe =
                listOf(
                    GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(1000)),
                    GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(2000)),
                ),
            ),
        )
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 7654321,
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 3, bidragBelop = BigDecimal.valueOf(6000))),
            ),
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregningPeriodisert(
                bidragsevne = Bidragsevne(belop = BigDecimal.valueOf(6000), tjuefemProsentInntekt = BigDecimal.valueOf(10000)),
                beregnetBidragSakListe = beregnetBidragSakListe,
            )

        val resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].saksnr).isEqualTo(1234567) },
            Executable { assertThat(resultat[1].saksnr).isEqualTo(7654321) },
            Executable { assertThat(resultat.size).isEqualTo(2) },
            Executable { assertThat(resultat[0].resultatPerBarnListe.size).isEqualTo(2) },
            Executable { assertThat(resultat[1].resultatPerBarnListe.size).isEqualTo(1) },
            Executable { assertThat(resultat[0].resultatPerBarnListe[0].barnPersonId).isEqualTo(1) },
            Executable { assertThat(resultat[0].resultatPerBarnListe[1].barnPersonId).isEqualTo(2) },
            Executable { assertThat(resultat[1].resultatPerBarnListe[0].barnPersonId).isEqualTo(3) },
            Executable { assertThat(resultat[0].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(670))).isZero() },
            Executable { assertThat(resultat[0].resultatPerBarnListe[1].belop.compareTo(BigDecimal.valueOf(1330))).isZero() },
            Executable { assertThat(resultat[1].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(4000))).isZero() },
            Executable {
                assertThat(
                    resultat[0].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
            Executable {
                assertThat(
                    resultat[0].resultatPerBarnListe[1].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
            Executable {
                assertThat(
                    resultat[1].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
        )
    }

    @DisplayName("test med to saker med ett barn hver")
    @Test
    fun testToSakerMedEttBarnHver() {
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 1,
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(3425))),
            ),
        )
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 2,
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(5980))),
            ),
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregningPeriodisert(
                bidragsevne = Bidragsevne(belop = BigDecimal.valueOf(5655), tjuefemProsentInntekt = BigDecimal.valueOf(10000)),
                beregnetBidragSakListe = beregnetBidragSakListe,
            )

        val resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].saksnr).isEqualTo(1) },
            Executable { assertThat(resultat[1].saksnr).isEqualTo(2) },
            Executable { assertThat(resultat.size).isEqualTo(2) },
            Executable { assertThat(resultat[0].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(2060))).isZero() },
            Executable { assertThat(resultat[1].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(3600))).isZero() },
            Executable {
                assertThat(
                    resultat[0].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
            Executable {
                assertThat(
                    resultat[1].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
        )
    }

    @DisplayName("test med tre saker med ett barn hver")
    @Test
    fun testTreSakerMedEttBarnHver() {
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 1,
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(3425))),
            ),
        )
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 2,
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(5980))),
            ),
        )
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 3,
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 3, bidragBelop = BigDecimal.valueOf(3856))),
            ),
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregningPeriodisert(
                bidragsevne = Bidragsevne(belop = BigDecimal.valueOf(5655), tjuefemProsentInntekt = BigDecimal.valueOf(10000)),
                beregnetBidragSakListe = beregnetBidragSakListe,
            )

        val resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].saksnr).isEqualTo(1) },
            Executable { assertThat(resultat[1].saksnr).isEqualTo(2) },
            Executable { assertThat(resultat[2].saksnr).isEqualTo(3) },
            Executable { assertThat(resultat.size).isEqualTo(3) },
            Executable { assertThat(resultat[0].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(1460))).isZero() },
            Executable { assertThat(resultat[1].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(2550))).isZero() },
            Executable { assertThat(resultat[2].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(1640))).isZero() },
            Executable {
                assertThat(
                    resultat[0].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
            Executable {
                assertThat(
                    resultat[1].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
        )
    }

    @DisplayName("test med tre saker med totalt seks barn")
    @Test
    fun testTreSakerMedTotaltSeksBarn() {
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 1,
                grunnlagPerBarnListe = listOf(GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(3749))),
            ),
        )
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 2,
                grunnlagPerBarnListe =
                listOf(
                    GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(5115)),
                    GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(4557)),
                ),
            ),
        )
        beregnetBidragSakListe.add(
            BeregnetBidragSak(
                saksnr = 3,
                grunnlagPerBarnListe =
                listOf(
                    GrunnlagPerBarn(barnPersonId = 1, bidragBelop = BigDecimal.valueOf(4134)),
                    GrunnlagPerBarn(barnPersonId = 2, bidragBelop = BigDecimal.valueOf(3561)),
                    GrunnlagPerBarn(barnPersonId = 3, bidragBelop = BigDecimal.valueOf(2856)),
                ),
            ),
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregningPeriodisert(
                bidragsevne = Bidragsevne(belop = BigDecimal.valueOf(10417), tjuefemProsentInntekt = BigDecimal.valueOf(20000)),
                beregnetBidragSakListe = beregnetBidragSakListe,
            )

        val resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].saksnr).isEqualTo(1) },
            Executable { assertThat(resultat[1].saksnr).isEqualTo(2) },
            Executable { assertThat(resultat[2].saksnr).isEqualTo(3) },
            Executable { assertThat(resultat.size).isEqualTo(3) },
            Executable { assertThat(resultat[0].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(1630))).isZero() },
            Executable { assertThat(resultat[1].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(2220))).isZero() },
            Executable { assertThat(resultat[1].resultatPerBarnListe[1].belop.compareTo(BigDecimal.valueOf(1980))).isZero() },
            Executable { assertThat(resultat[2].resultatPerBarnListe[0].belop.compareTo(BigDecimal.valueOf(1800))).isZero() },
            Executable { assertThat(resultat[2].resultatPerBarnListe[1].belop.compareTo(BigDecimal.valueOf(1550))).isZero() },
            Executable { assertThat(resultat[2].resultatPerBarnListe[2].belop.compareTo(BigDecimal.valueOf(1240))).isZero() },
            Executable {
                assertThat(
                    resultat[0].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
            Executable {
                assertThat(
                    resultat[1].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
            Executable {
                assertThat(
                    resultat[1].resultatPerBarnListe[1].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
            Executable {
                assertThat(
                    resultat[2].resultatPerBarnListe[0].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
            Executable {
                assertThat(
                    resultat[2].resultatPerBarnListe[1].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
            Executable {
                assertThat(
                    resultat[2].resultatPerBarnListe[2].kode,
                ).isEqualTo(ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET)
            },
        )
    }
}
