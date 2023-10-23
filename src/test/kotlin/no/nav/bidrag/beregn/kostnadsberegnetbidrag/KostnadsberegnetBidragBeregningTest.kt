package no.nav.bidrag.beregn.kostnadsberegnetbidrag

import no.nav.bidrag.beregn.TestUtil.BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE
import no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregning.Companion.getInstance
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnad
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Samvaersfradrag
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Underholdskostnad
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Test av beregning av kostnadsberegnet bidrag")
internal class KostnadsberegnetBidragBeregningTest {

    private val kostnadsberegnetBidragBeregning = getInstance()

    @DisplayName("Test av beregning av kostnadsberegnet bidrag")
    @Test
    fun testBeregningUtenSamvaer() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(10000)),
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.20)
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.ZERO)
            )

        val (belop) = kostnadsberegnetBidragBeregning.beregn(grunnlagBeregning)

        assertThat(belop.compareTo(BigDecimal.valueOf(2000))).isZero()
    }

    @DisplayName("Test av beregning av kostnadsberegnet bidrag med samværsfradrag")
    @Test
    fun testBeregningMedSamvaer() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(10000)),
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.20)
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.valueOf(100))
            )

        val (belop) = kostnadsberegnetBidragBeregning.beregn(grunnlagBeregning)

        assertThat(belop.compareTo(BigDecimal.valueOf(1900))).isZero()
    }

    @DisplayName("Test av at resultatet rundes av til nærmeste tier. 606,06 -> 610")
    @Test
    fun testResultatRundesOpp() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(666)),
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.91)
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.ZERO)
            )

        val (belop) = kostnadsberegnetBidragBeregning.beregn(grunnlagBeregning)

        assertThat(belop.compareTo(BigDecimal.valueOf(610))).isZero()
    }

    @DisplayName("Test av at resultatet rundes ned til nærmeste tier. 72,0 -> 70,0 ")
    @Test
    fun testResultatRundesNed() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                underholdskostnad = Underholdskostnad(referanse = UNDERHOLDSKOSTNAD_REFERANSE, belop = BigDecimal.valueOf(1000)),
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.172)
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.valueOf(100))
            )

        val (belop) = kostnadsberegnetBidragBeregning.beregn(grunnlagBeregning)

        assertThat(belop.compareTo(BigDecimal.valueOf(70))).isZero()
    }
}
