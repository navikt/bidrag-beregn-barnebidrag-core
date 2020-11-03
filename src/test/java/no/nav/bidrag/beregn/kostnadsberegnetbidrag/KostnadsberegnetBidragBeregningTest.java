package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregningImpl;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregningPeriodisert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning kostnadsberegnet bidrag uten samværsfradrag")
public class KostnadsberegnetBidragBeregningTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Test av beregning av kostnadsberegnetBidrag")
  @Test
  void testBeregningUtenSamvaer() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(10000),
        BigDecimal.valueOf(20),
        BigDecimal.valueOf(0));

    assertEquals(BigDecimal.valueOf(2000),
        kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatkostnadsberegnetbidragBelop());
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

    assertEquals(BigDecimal.valueOf(1900),
        kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatkostnadsberegnetbidragBelop());
  }

  @DisplayName("Test av resultatet rundes av til nærmeste tier. 606,06 -> 610")
  @Test
  void testResultatRundesOpp() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(666),
        BigDecimal.valueOf(91),
        BigDecimal.valueOf(0)
    );

    assertEquals(610d,
        kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatkostnadsberegnetbidragBelop());
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

    assertEquals(BigDecimal.valueOf(70),
        kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatkostnadsberegnetbidragBelop());
  }

}

