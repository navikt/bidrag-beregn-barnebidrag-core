package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregningImpl;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregningPeriodisert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning kostnadsberegnet bidrag uten samværsfradrag")
public class KostnadsberegnetBidragBeregningTest {

  @DisplayName("Test av beregning av kostnadsberegnetBidrag")
  @Test
  void testBeregningUtenSamvaer() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(10000),
        BigDecimal.valueOf(20),
        BigDecimal.ZERO);

     assertThat(kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert).getResultatkostnadsberegnetbidragBelop()
         .compareTo(BigDecimal.valueOf(2000))).isZero();
  }


  @DisplayName("Test av beregning av kostnadsberegnetBidrag med samværsfradrag")
  @Test
  void testBeregningMedSamvaer() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(10000),
        BigDecimal.valueOf(20),
        BigDecimal.valueOf(100)
    );

    assertThat(kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert).getResultatkostnadsberegnetbidragBelop()
        .compareTo(BigDecimal.valueOf(1900))).isZero();
  }

  @DisplayName("Test av resultatet rundes av til nærmeste tier. 606,06 -> 610")
  @Test
  void testResultatRundesOpp() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(666),
        BigDecimal.valueOf(91),
        BigDecimal.ZERO
    );

    assertThat(kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert).getResultatkostnadsberegnetbidragBelop()
        .compareTo(BigDecimal.valueOf(610))).isZero();
  }

  @DisplayName("Test av resultatet rundes ned til nærmeste tier. 72,0 -> 70,0 ")
  @Test
  void testResultatRundesNed() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(1000),
        BigDecimal.valueOf(17.2),
        BigDecimal.valueOf(100)
    );

    assertThat(kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert).getResultatkostnadsberegnetbidragBelop()
        .compareTo(BigDecimal.valueOf(70))).isZero();
  }
}
