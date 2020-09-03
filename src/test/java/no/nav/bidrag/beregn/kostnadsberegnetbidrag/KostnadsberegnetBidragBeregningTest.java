package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        = new GrunnlagBeregningPeriodisert(10000, 20d,
        0d);

    assertEquals(2000d,
        kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatkostnadsberegnetbidragBelop());
  }


  @DisplayName("Test av beregning av kostnadsberegnetBidrag med samværsfradrag")
  @Test
  void testBeregningMedSamvaer() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(10000, 20,
        100.0
    );

    assertEquals(1900d,
        kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatkostnadsberegnetbidragBelop());
  }

  @DisplayName("Test av resultatet rundes av til nærmeste tier. 606,06 -> 610")
  @Test
  void testResultatRundesOpp() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(666, 91d,
        0d
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
        = new GrunnlagBeregningPeriodisert(1000, 17.2,
        100d
    );

    assertEquals(70d,
        kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatkostnadsberegnetbidragBelop());
  }

}
