package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import static no.nav.bidrag.beregn.TestUtil.BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Samvaersfradrag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Underholdskostnad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av kostnadsberegnet bidrag")
public class KostnadsberegnetBidragBeregningTest {

  private final KostnadsberegnetBidragBeregning kostnadsberegnetBidragBeregning = KostnadsberegnetBidragBeregning.Companion.getInstance();

  @DisplayName("Test av beregning av kostnadsberegnet bidrag")
  @Test
  void testBeregningUtenSamvaer() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(10000)),
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.20)),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.ZERO)
    );

    var resultat = kostnadsberegnetBidragBeregning.beregn(grunnlagBeregning);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(2000))).isZero();
  }


  @DisplayName("Test av beregning av kostnadsberegnet bidrag med samværsfradrag")
  @Test
  void testBeregningMedSamvaer() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(10000)),
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.20)),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf(100))
    );

    var resultat = kostnadsberegnetBidragBeregning.beregn(grunnlagBeregning);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(1900))).isZero();
  }

  @DisplayName("Test av resultatet rundes av til nærmeste tier. 606,06 -> 610")
  @Test
  void testResultatRundesOpp() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(666)),
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.91)),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.ZERO)
    );

    var resultat = kostnadsberegnetBidragBeregning.beregn(grunnlagBeregning);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(610))).isZero();
  }

  @DisplayName("Test av resultatet rundes ned til nærmeste tier. 72,0 -> 70,0 ")
  @Test
  void testResultatRundesNed() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.172)),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf(100))
    );

    var resultat = kostnadsberegnetBidragBeregning.beregn(grunnlagBeregning);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(70))).isZero();
  }
}
