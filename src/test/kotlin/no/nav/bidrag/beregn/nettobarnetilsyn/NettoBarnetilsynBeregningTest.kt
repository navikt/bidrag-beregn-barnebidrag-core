package no.nav.bidrag.beregn.nettobarnetilsyn

import no.nav.bidrag.beregn.TestUtil
import no.nav.bidrag.beregn.TestUtil.FAKTISK_UTGIFT_REFERANSE
import no.nav.bidrag.beregn.nettobarnetilsyn.beregning.NettoBarnetilsynBeregning.Companion.getInstance
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregning
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal

@DisplayName("Test av beregning av netto barnetilsyn")
internal class NettoBarnetilsynBeregningTest {
    private var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe()
    private val nettoBarnetilsynBeregning = getInstance()

    @Test
    @DisplayName("Beregning med ett barn under maks tilsynsbeløp, resultatet skal da beregnes fra innsendt faktisk utgift-beløp")
    fun testEttBarnEttBelopUnderMaksTilsynsbelop() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                faktiskUtgiftListe =
                listOf(
                    FaktiskUtgift(
                        soknadsbarnPersonId = 1,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(2500),
                    ),
                ),
                sjablonListe = sjablonPeriodeListe,
            )

        val resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.size).isEqualTo(1) },
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(1978))).isZero() },
        )
    }

    @Test
    @DisplayName("Beregning med to barn, beløp under maks tilsynsbeløp, resultatet skal da beregnes fra innsendt faktisk utgift-beløp")
    fun testToBarnUnderMaksTilsynsbelop() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                faktiskUtgiftListe =
                listOf(
                    FaktiskUtgift(
                        soknadsbarnPersonId = 1,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(2500),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 2,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 5,
                        belop = BigDecimal.valueOf(5000),
                    ),
                ),
                sjablonListe = sjablonPeriodeListe,
            )

        val resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.size).isEqualTo(2) },
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(2083))).isZero() },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(4583))).isZero() },
        )
    }

    @Test
    @DisplayName("Test at barn med flere innsendte faktiske utgifter kun får ett summert beløp i resultatet")
    fun testAtFaktiskUtgiftSummeresPerBarn() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                faktiskUtgiftListe =
                listOf(
                    FaktiskUtgift(
                        soknadsbarnPersonId = 2,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 5,
                        belop = BigDecimal.valueOf(2000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 2,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 5,
                        belop = BigDecimal.valueOf(1000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 1,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(500),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 2,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 5,
                        belop = BigDecimal.valueOf(2000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 1,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(2000),
                    ),
                ),
                sjablonListe = sjablonPeriodeListe,
            )

        val resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.size).isEqualTo(2) },
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(2083))).isZero() },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(4583))).isZero() },
        )
    }

    @Test
    @DisplayName(
        "Beregning med to barn, beløp over maks tilsynsbeløp, og fradragsbeløp over maks fradragsbeløp,  " +
            "resultatet skal da beregnes fra sjablon maks tilsynsbeløp for to barn og sjablon maks fradragsbeløp for to barn",
    )
    fun testToBarnOverMaksTilsynsbelopogMaksFradragsbelop() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                faktiskUtgiftListe =
                listOf(
                    FaktiskUtgift(
                        soknadsbarnPersonId = 1,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 5,
                        belop = BigDecimal.valueOf(7000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 2,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 9,
                        belop = BigDecimal.valueOf(3000),
                    ),
                ),
                sjablonListe = (sjablonPeriodeListe),
            )

        val resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning)

        // Utregning: For to barn er maks tilsynsbeløp 8109.- og innsendte faktiske utgifter på 7000 + 3000 reduseres heretter.
        // Justeringen skal være forholdsmessig, dvs barn 1 skal ha 70% av maks tilsynsbeløp (5676,3), barn 2 skal ha 30% (2432,7).
        // Fradrag skattefordel: Skattesats 25,05% av maks tilsynsbeløp 8109 = 2031.3045. Dette er høyere enn 25,05% av maks fradragsbeløp (blir 834,9165).
        // 834,9165 skal derfor brukes og fordeles likt mellom barna.
        // Netto barnetilsyn for barn 1 skal da bli 5676,3 - (834,9165/2) = 5258,84175, avrundes til 5258,84,
        // for barn 2: 2432,7 - (834,9165/2) = 2015,24175, avrundes til 2015,24

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.size).isEqualTo(2) },
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(5367))).isZero() },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(2062))).isZero() },
        )
    }

    @Test
    @DisplayName(
        (
            "Beregning med tre barn, beløp over maks tilsynsbeløp, og fradragsbeløp over maks fradragsbeløp,  " +
                "resultatet skal da beregnes fra sjablon maks tilsynsbeløp for tre barn og sjablon maks fradragsbeløp for tre barn"
            ),
    )
    fun testTreBarnOverMaksTilsynsbelopOgMaksFradragsbelop() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                faktiskUtgiftListe =
                listOf(
                    FaktiskUtgift(
                        soknadsbarnPersonId = 1,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(5000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 2,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 8,
                        belop = BigDecimal.valueOf(3000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 3,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 3,
                        belop = BigDecimal.valueOf(2000),
                    ),
                ),
                sjablonListe = (sjablonPeriodeListe),
            )

        val resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning)

        // Utregning: For tre barn er maks tilsynsbeløp 9189.- og innsendte faktiske utgifter på 5000 + 3000 + 2000 reduseres heretter.
        // Justeringen skal være forholdsmessig, dvs barn 1 skal ha 50% av maks tilsynsbeløp (4594,5), barn 2 skal ha 30% (2756,7) og barn 3 20% (1837,8).
        // Fradrag skattefordel: Skattesats 25,05% av maks tilsynsbeløp 9189 = 2301.8445. Dette er høyere enn 25,05% av maks fradragsbeløp (blir 1148,0415).
        // 1148,0415 skal derfor brukes og fordeles likt mellom barna.
        // Netto barnetilsyn for barn 1 skal da bli 4594,5 - (1148,0415/3) = 4211,8195, avrundes til 4211,82,
        // for barn 2: 2756,7 - (1148,0415/3) = 2374,0195, avrundes til 2374,02, for barn 3: 1837,8 - (1148,0415/3) = 1455,1195, avrundes til 1455,12

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.size).isEqualTo(3) },
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(4299))).isZero() },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(2427))).isZero() },
            Executable { assertThat(resultat[2].belop.compareTo(BigDecimal.valueOf(1490))).isZero() },
        )
    }

    @Test
    @DisplayName("Beregning med ett barn, beløp så lavt at beregnet fradragsbeløp blir brukt i stedet for sjablon")
    fun testEttBarnBeregnetFradragsbelopLavereEnnSjablonverdi() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                faktiskUtgiftListe =
                listOf(
                    FaktiskUtgift(
                        soknadsbarnPersonId = 1,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(1000),
                    ),
                ),
                sjablonListe = (sjablonPeriodeListe),
            )

        val resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning)

        // Utregning: For ett barn er maks tilsynsbeløp 6214.-, innsendt beløp 1000 er lavere og dette brukes videre.
        // Fradrag skattefordel: Skattesats 25,05% av innsendt tilsynsbeløp 1000 = 250,5.
        // Netto barnetilsyn for barn 1 skal da bli 1000 - 250,5 = 749,5
        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.size).isEqualTo(1) },
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(750))).isZero() },
        )
    }

    @Test
    @DisplayName("Test sortering på input på søknadsbarn personid")
    fun testSorteringPaaSoknadsbarnPersonId() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                faktiskUtgiftListe =
                listOf(
                    FaktiskUtgift(
                        soknadsbarnPersonId = 3,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(1000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 1,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(2000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 2,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(3000),
                    ),
                ),
                sjablonListe = (sjablonPeriodeListe),
            )

        val resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.size).isEqualTo(3) },
            Executable { assertThat(resultat[0].soknadsbarnPersonId).isEqualTo(1) },
            Executable { assertThat(resultat[1].soknadsbarnPersonId).isEqualTo(2) },
            Executable { assertThat(resultat[2].soknadsbarnPersonId).isEqualTo(3) },
        )
    }

    @Test
    @DisplayName("Test summering på søknadsbarns personid")
    fun testSummeringPaaSoknadsbarnPersonId() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                faktiskUtgiftListe =
                listOf(
                    FaktiskUtgift(
                        soknadsbarnPersonId = 2,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(1000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 3,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(2000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 2,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(2000),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 1,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.valueOf(5000),
                    ),
                ),
                sjablonListe = (sjablonPeriodeListe),
            )

        val resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.size).isEqualTo(3) },
            Executable { assertThat(resultat[0].soknadsbarnPersonId).isEqualTo(1) },
            Executable { assertThat(resultat[1].soknadsbarnPersonId).isEqualTo(2) },
            Executable { assertThat(resultat[2].soknadsbarnPersonId).isEqualTo(3) },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(2427))).isZero() },
        )
    }

    @Test
    @DisplayName("Test netto barnetilsyn")
    fun testNettoBarnetilsyn() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                faktiskUtgiftListe =
                listOf(
                    FaktiskUtgift(
                        soknadsbarnPersonId = 1,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 10,
                        belop = BigDecimal.ZERO,
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 2,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 8,
                        belop = BigDecimal.valueOf(2500),
                    ),
                    FaktiskUtgift(
                        soknadsbarnPersonId = 3,
                        referanse = FAKTISK_UTGIFT_REFERANSE,
                        soknadsbarnAlder = 14,
                        belop = BigDecimal.ZERO,
                    ),
                ),
                sjablonListe = (sjablonPeriodeListe),
            )

        val resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.ZERO)).isZero() },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(1874))).isZero() },
        )
    }
}
