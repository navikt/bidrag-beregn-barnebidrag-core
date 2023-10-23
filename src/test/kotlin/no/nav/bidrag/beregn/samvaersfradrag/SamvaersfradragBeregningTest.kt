package no.nav.bidrag.beregn.samvaersfradrag

import no.nav.bidrag.beregn.TestUtil.SAMVAERSKLASSE_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SOKNADSBARN_REFERANSE
import no.nav.bidrag.beregn.TestUtil.byggSjablonPeriodeListe
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregning.Companion.getInstance
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.samvaersfradrag.bo.Samvaersklasse
import no.nav.bidrag.beregn.samvaersfradrag.bo.SoknadsbarnAlder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Test av beregning av samværsfradrag")
internal class SamvaersfradragBeregningTest {

    private val sjablonPeriodeListe = byggSjablonPeriodeListe()
    private val samvaersfradragBeregning = getInstance()

    @DisplayName("Test av beregning av samvaersfradrag for fireåring")
    @Test
    fun testFireAar() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 4),
                samvaersklasse = Samvaersklasse(referanse = SAMVAERSKLASSE_REFERANSE, samvaersklasse = "03"),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = samvaersfradragBeregning.beregn(grunnlagBeregning)

        assertThat(belop.compareTo(BigDecimal.valueOf(2272))).isZero()
    }

    @DisplayName("Test av beregning av samvaersfradrag for seksåring")
    @Test
    fun testSeksAar() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 6),
                samvaersklasse = Samvaersklasse(referanse = SAMVAERSKLASSE_REFERANSE, samvaersklasse = "03"),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = samvaersfradragBeregning.beregn(grunnlagBeregning)

        assertThat(belop.compareTo(BigDecimal.valueOf(2716))).isZero()
    }

    @DisplayName("Test av beregning av samvaersfradrag for fjortenåring")
    @Test
    fun testFjortenAar() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 14),
                samvaersklasse = Samvaersklasse(referanse = SAMVAERSKLASSE_REFERANSE, samvaersklasse = "01"),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = samvaersfradragBeregning.beregn(grunnlagBeregning)

        assertThat(belop.compareTo(BigDecimal.valueOf(457))).isZero()
    }
}
