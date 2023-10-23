package no.nav.bidrag.beregn.bpsandelunderholdskostnad

import no.nav.bidrag.beregn.TestUtil.INNTEKT_BB_REFERANSE
import no.nav.bidrag.beregn.TestUtil.INNTEKT_BM_REFERANSE
import no.nav.bidrag.beregn.TestUtil.INNTEKT_BP_REFERANSE
import no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.TestUtil.byggSjablonPeriodeListe
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning.BPsAndelUnderholdskostnadBeregning.Companion.getInstance
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Underholdskostnad
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal

@DisplayName("Test av beregning av BPs andel av underholdskostnad")
class BPsAndelUnderholdskostnadBeregningTest {

    private val sjablonPeriodeListe = byggSjablonPeriodeListe()
    private val bPsAndelUnderholdskostnadBeregning = getInstance()

    @DisplayName("Beregning med inntekter for alle parter")
    @Test
    fun testBeregningMedInntekterForAlle() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(10000)),
                inntektBPListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BP_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(217666),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBMListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BM_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(400000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBBListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BB_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(40000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlag = grunnlagBeregning, beregnMedNyeRegler = true)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.352)) },
            Executable { assertThat(resultat.andelBelop).isEqualTo(BigDecimal.valueOf(3520)) }
        )
    }

    @DisplayName("Beregning med flere inntekter for alle parter og fratrekk av 30 * forhøyet forskudd på barnets inntekt")
    @Test
    fun testBeregningMedFlereInntekterForAlle() {
        val inntektBPListe = mutableListOf<Inntekt>()
        val inntektBMListe = mutableListOf<Inntekt>()
        val inntektBBListe = mutableListOf<Inntekt>()
        inntektBPListe.add(
            Inntekt(
                referanse = INNTEKT_BP_REFERANSE + "_1",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(200000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        inntektBPListe.add(
            Inntekt(
                referanse = INNTEKT_BP_REFERANSE + "_2",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(17666),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        inntektBMListe.add(
            Inntekt(
                referanse = INNTEKT_BM_REFERANSE + "_1",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(100000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        inntektBMListe.add(
            Inntekt(
                referanse = INNTEKT_BM_REFERANSE + "_2",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(200000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        inntektBMListe.add(
            Inntekt(
                referanse = INNTEKT_BM_REFERANSE + "_3",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(100000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        inntektBBListe.add(
            Inntekt(
                referanse = INNTEKT_BB_REFERANSE + "_1",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(10000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        inntektBBListe.add(
            Inntekt(
                referanse = INNTEKT_BB_REFERANSE + "_2",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(10000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        inntektBBListe.add(
            Inntekt(
                referanse = INNTEKT_BB_REFERANSE + "_3",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(10000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        inntektBBListe.add(
            Inntekt(
                referanse = INNTEKT_BB_REFERANSE + "_4",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(10000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        inntektBBListe.add(
            Inntekt(
                referanse = INNTEKT_BB_REFERANSE + "_5",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(10000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        inntektBBListe.add(
            Inntekt(
                referanse = INNTEKT_BB_REFERANSE + "_6",
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(10000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                inntektBPListe = inntektBPListe,
                inntektBMListe = inntektBMListe,
                inntektBBListe = inntektBBListe,
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlag = grunnlagBeregning, beregnMedNyeRegler = true)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.347)) },
            Executable { assertThat(resultat.andelBelop).isEqualTo(BigDecimal.valueOf(347)) },
            Executable { assertThat(resultat.barnetErSelvforsorget).isFalse() }
        )
    }

    @DisplayName("Beregning der barnets inntekter er høyere enn 100 * forhøyet forskuddssats. Andel skal da bli 0")
    @Test
    fun testAndelLikNullVedHoyInntektBarn() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                inntektBPListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BP_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(217666),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBMListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BM_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(400000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBBListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BB_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(400000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlag = grunnlagBeregning, beregnMedNyeRegler = true)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.andelProsent).isEqualTo(BigDecimal.ZERO) },
            Executable { assertThat(resultat.andelBelop).isEqualTo(BigDecimal.ZERO) },
            Executable { assertThat(resultat.barnetErSelvforsorget).isTrue() }
        )
    }

    @DisplayName(
        "Test at beregnet andel ikke settes høyere enn 5/6 (83,3333333333). Legger inn 10 desimaler for å få likt resultat som i Bidragskalkulator"
    )
    @Test
    fun testAtMaksAndelSettes() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                inntektBPListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BP_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(1000000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBMListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BM_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(40000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBBListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BB_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(40000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlag = grunnlagBeregning, beregnMedNyeRegler = true)

        assertAll(
            Executable { assertThat(resultat).isNotNull() }, // Beregnet andel skal bli 92,6%, overstyres til 5/6 (83,3333333333%)
            Executable { assertThat(resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.833333333333)) }
        )
    }

    @DisplayName("Beregning med 0 i inntekt for barn")
    @Test
    fun testBeregningMedNullInntektBarn() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                inntektBPListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BP_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(502000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBMListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BM_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(500000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBBListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BB_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.ZERO,
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlag = grunnlagBeregning, beregnMedNyeRegler = true)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.501)) }
        )
    }

    @DisplayName("Beregning med gamle regler, beregnet andel skal avrundes til nærmeste sjettedel (maks 5/6)")
    @Test
    fun testBeregningGamleReglerAvrundTreSjettedeler() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                inntektBPListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BP_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(502000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBMListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BM_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(500000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBBListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BB_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.ZERO,
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlag = grunnlagBeregning, beregnMedNyeRegler = false)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.500).setScale(3)) }
        )
    }

    @DisplayName("Beregning med gamle regler, andel skal rundes opp til 1/6")
    @Test
    fun testBeregningGamleReglerAvrundEnSjettedel() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                inntektBPListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BP_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(2000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBMListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BM_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(500000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBBListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BB_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(1000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlag = grunnlagBeregning, beregnMedNyeRegler = false)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.167)) }
        )
    }

    @DisplayName("Beregning med gamle regler, andel skal rundes ned til maks andel, 5/6")
    @Test
    fun testBeregningGamleReglerAvrundFemSjettedeler() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                inntektBPListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BP_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(2000000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBMListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BM_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(2000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBBListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BB_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(1000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlag = grunnlagBeregning, beregnMedNyeRegler = false)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.833333333333)) }
        )
    }

    @DisplayName("Test fem sjettedeler")
    @Test
    fun testFemSjettedeler() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(9355)),
                inntektBPListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BP_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(600000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBMListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BM_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.valueOf(100000),
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                inntektBBListe = listOf(
                    Inntekt(
                        referanse = INNTEKT_BB_REFERANSE,
                        type = "LONN_SKE",
                        belop = BigDecimal.ZERO,
                        deltFordel = false,
                        skatteklasse2 = false
                    )
                ),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlag = grunnlagBeregning, beregnMedNyeRegler = false)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.andelBelop).isEqualTo(BigDecimal.valueOf(7796)) },
            Executable { assertThat(resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.833333333333)) }
        )
    }
}
