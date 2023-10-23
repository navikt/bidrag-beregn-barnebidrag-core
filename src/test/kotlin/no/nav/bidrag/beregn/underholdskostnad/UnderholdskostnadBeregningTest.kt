package no.nav.bidrag.beregn.underholdskostnad

import no.nav.bidrag.beregn.TestUtil.BARNETILSYN_MED_STONAD_REFERANSE
import no.nav.bidrag.beregn.TestUtil.FORPLEINING_UTGIFT_REFERANSE
import no.nav.bidrag.beregn.TestUtil.NETTO_BARNETILSYN_REFERANSE
import no.nav.bidrag.beregn.TestUtil.ORDINAER_BARNETRYGD
import no.nav.bidrag.beregn.TestUtil.SOKNADSBARN_REFERANSE
import no.nav.bidrag.beregn.TestUtil.byggSjablonPeriodeListe
import no.nav.bidrag.beregn.underholdskostnad.beregning.UnderholdskostnadBeregning.Companion.getInstance
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgift
import no.nav.bidrag.beregn.underholdskostnad.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsyn
import no.nav.bidrag.beregn.underholdskostnad.bo.SoknadsbarnAlder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Test av beregning av underholdskostnad")
internal class UnderholdskostnadBeregningTest {

    private val sjablonPeriodeListe = byggSjablonPeriodeListe()
    private val underholdskostnadberegning = getInstance()

    @DisplayName("Test av beregning av underholdskostnad når barnet er 3 år gammelt")
    @Test
    fun testBeregningAlder3MedKunSjablonverdier() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 3),
                barnetilsynMedStonad = BarnetilsynMedStonad(referanse = BARNETILSYN_MED_STONAD_REFERANSE, tilsynType = "Ingen", stonadType = "Ingen"),
                nettoBarnetilsyn = NettoBarnetilsyn(referanse = NETTO_BARNETILSYN_REFERANSE, belop = BigDecimal.ZERO),
                forpleiningUtgift = ForpleiningUtgift(referanse = FORPLEINING_UTGIFT_REFERANSE, belop = BigDecimal.ZERO),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = underholdskostnadberegning.beregn(grunnlag = grunnlagBeregning, barnetrygdIndikator = ORDINAER_BARNETRYGD)

        assertThat(belop.compareTo(BigDecimal.valueOf(5999))).isZero()
    }

    @DisplayName("Test av beregning av underholdskostnad når barnet er 7 år gammelt")
    @Test
    fun testBeregningAlder7MedKunSjablonverdier() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 7),
                barnetilsynMedStonad = BarnetilsynMedStonad(referanse = BARNETILSYN_MED_STONAD_REFERANSE, tilsynType = "Ingen", stonadType = "Ingen"),
                nettoBarnetilsyn = NettoBarnetilsyn(referanse = NETTO_BARNETILSYN_REFERANSE, belop = BigDecimal.ZERO),
                forpleiningUtgift = ForpleiningUtgift(referanse = FORPLEINING_UTGIFT_REFERANSE, belop = BigDecimal.ZERO),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = underholdskostnadberegning.beregn(grunnlag = grunnlagBeregning, barnetrygdIndikator = ORDINAER_BARNETRYGD)

        assertThat(belop.compareTo(BigDecimal.valueOf(7481))).isZero()
    }

    @DisplayName("Test av beregning av underholdskostnad når barnet er 10 år gammelt")
    @Test
    fun testBeregningAlder10MedKunSjablonverdier() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 10),
                barnetilsynMedStonad = BarnetilsynMedStonad(referanse = BARNETILSYN_MED_STONAD_REFERANSE, tilsynType = "Ingen", stonadType = "Ingen"),
                nettoBarnetilsyn = NettoBarnetilsyn(referanse = NETTO_BARNETILSYN_REFERANSE, belop = BigDecimal.ZERO),
                forpleiningUtgift = ForpleiningUtgift(referanse = FORPLEINING_UTGIFT_REFERANSE, belop = BigDecimal.ZERO),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = underholdskostnadberegning.beregn(grunnlag = grunnlagBeregning, barnetrygdIndikator = ORDINAER_BARNETRYGD)

        assertThat(belop.compareTo(BigDecimal.valueOf(7481))).isZero()
    }

    @DisplayName("Test av beregning av underholdskostnad når barnet er 11 år gammelt")
    @Test
    fun testBeregningAlder11MedKunSjablonverdier() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 11),
                barnetilsynMedStonad = BarnetilsynMedStonad(referanse = BARNETILSYN_MED_STONAD_REFERANSE, tilsynType = "Ingen", stonadType = "Ingen"),
                nettoBarnetilsyn = NettoBarnetilsyn(referanse = NETTO_BARNETILSYN_REFERANSE, belop = BigDecimal.ZERO),
                forpleiningUtgift = ForpleiningUtgift(referanse = FORPLEINING_UTGIFT_REFERANSE, belop = BigDecimal.ZERO),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = underholdskostnadberegning.beregn(grunnlag = grunnlagBeregning, barnetrygdIndikator = ORDINAER_BARNETRYGD)

        assertThat(belop.compareTo(BigDecimal.valueOf(8684))).isZero()
    }

    @DisplayName("Test av beregning av underholdskostnad når barnet er 12 år gammelt")
    @Test
    fun testBeregningAlder12MedKunSjablonverdier() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 12),
                barnetilsynMedStonad = BarnetilsynMedStonad(referanse = BARNETILSYN_MED_STONAD_REFERANSE, tilsynType = "Ingen", stonadType = "Ingen"),
                nettoBarnetilsyn = NettoBarnetilsyn(referanse = NETTO_BARNETILSYN_REFERANSE, belop = BigDecimal.ZERO),
                forpleiningUtgift = ForpleiningUtgift(referanse = FORPLEINING_UTGIFT_REFERANSE, belop = BigDecimal.ZERO),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = underholdskostnadberegning.beregn(grunnlag = grunnlagBeregning, barnetrygdIndikator = ORDINAER_BARNETRYGD)

        assertThat(belop.compareTo(BigDecimal.valueOf(8684))).isZero()
    }

    @DisplayName("Test av beregning av underholdskostnad når barnet er 11 år gammelt  + stønad til tilsynsutgifter DU")
    @Test
    fun testBeregningAlder11StonadBarnetilsynDU() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 11),
                barnetilsynMedStonad = BarnetilsynMedStonad(referanse = BARNETILSYN_MED_STONAD_REFERANSE, tilsynType = "DU", stonadType = "64"),
                nettoBarnetilsyn = NettoBarnetilsyn(referanse = NETTO_BARNETILSYN_REFERANSE, belop = BigDecimal.ZERO),
                forpleiningUtgift = ForpleiningUtgift(referanse = FORPLEINING_UTGIFT_REFERANSE, belop = BigDecimal.ZERO),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = underholdskostnadberegning.beregn(grunnlag = grunnlagBeregning, barnetrygdIndikator = ORDINAER_BARNETRYGD)

        assertThat(belop.compareTo(BigDecimal.valueOf((6913 + 2825 + 257 - 1054).toLong()))).isZero()
    }

    @DisplayName("Test av at netto barnetilsynsutgifter blir lagt til underholdskostnad")
    @Test
    fun testBeregningAlder11MedNettoBarnetilsyn() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 11),
                barnetilsynMedStonad = BarnetilsynMedStonad(referanse = BARNETILSYN_MED_STONAD_REFERANSE, tilsynType = "Ingen", stonadType = "Ingen"),
                nettoBarnetilsyn = NettoBarnetilsyn(referanse = NETTO_BARNETILSYN_REFERANSE, belop = BigDecimal.valueOf(666)),
                forpleiningUtgift = ForpleiningUtgift(referanse = FORPLEINING_UTGIFT_REFERANSE, belop = BigDecimal.ZERO),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = underholdskostnadberegning.beregn(grunnlag = grunnlagBeregning, barnetrygdIndikator = ORDINAER_BARNETRYGD)

        assertThat(belop.compareTo(BigDecimal.valueOf((8684 + 666).toLong()))).isZero()
    }

    @DisplayName("Test at forpleiningsutgifter blir trukket fra underholdskostnad")
    @Test
    fun testBeregningAlder11MedForpleiningsutgifter() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                soknadsbarn = SoknadsbarnAlder(referanse = SOKNADSBARN_REFERANSE, alder = 11),
                barnetilsynMedStonad = BarnetilsynMedStonad(referanse = BARNETILSYN_MED_STONAD_REFERANSE, tilsynType = "Ingen", stonadType = "Ingen"),
                nettoBarnetilsyn = NettoBarnetilsyn(referanse = NETTO_BARNETILSYN_REFERANSE, belop = BigDecimal.ZERO),
                forpleiningUtgift = ForpleiningUtgift(referanse = FORPLEINING_UTGIFT_REFERANSE, belop = BigDecimal.valueOf(17)),
                sjablonListe = sjablonPeriodeListe
            )

        val (belop) = underholdskostnadberegning.beregn(grunnlag = grunnlagBeregning, barnetrygdIndikator = ORDINAER_BARNETRYGD)

        assertThat(belop.compareTo(BigDecimal.valueOf((8684 - 17).toLong()))).isZero()
    }

// TODO Legge til tester som bruker forhøyet barnetrygd eller ingen barnetrygd
}
