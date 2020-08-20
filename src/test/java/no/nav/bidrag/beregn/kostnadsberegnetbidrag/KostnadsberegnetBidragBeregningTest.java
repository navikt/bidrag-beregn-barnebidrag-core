package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregningImpl;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlagPeriodisert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning kostnadsberegnet bidrag uten samværsfradrag")
public class KostnadsberegnetBidragBeregningTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Test av beregning av kostnadsberegnetBidrag")
  @Test
  void testBeregningUtenSamvaer() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    BeregnKostnadsberegnetBidragGrunnlagPeriodisert beregnKostnadsberegnetBidragGrunnlagPeriodisert
        = new BeregnKostnadsberegnetBidragGrunnlagPeriodisert(3, 10000,
        20.0, null,
        sjablonListe
    );

    assertEquals(8000d,
        kostnadsberegnetBidragBeregning.beregn(beregnKostnadsberegnetBidragGrunnlagPeriodisert)
            .getResultatKostnadsberegnetBidragBelop());
  }


  @DisplayName("Test av beregning av kostnadsberegnetBidrag med samværsfradrag")
  @Test
  void testBeregningMedSamvaer() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    BeregnKostnadsberegnetBidragGrunnlagPeriodisert beregnKostnadsberegnetBidragGrunnlagPeriodisert
        = new BeregnKostnadsberegnetBidragGrunnlagPeriodisert(19, 10000,
        20.0, "01",
        sjablonListe
    );

    assertEquals(10000d - 2000d - 460d,
        kostnadsberegnetBidragBeregning.beregn(beregnKostnadsberegnetBidragGrunnlagPeriodisert)
            .getResultatKostnadsberegnetBidragBelop());
  }

  @DisplayName("Test av resultatet rundes av til nærmeste tier. 5918 -> 5920")
  @Test
  void testResultatRundesOpp() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    BeregnKostnadsberegnetBidragGrunnlagPeriodisert beregnKostnadsberegnetBidragGrunnlagPeriodisert
        = new BeregnKostnadsberegnetBidragGrunnlagPeriodisert(4, 10000,
        20.0, "03",
        sjablonListe
    );

    assertEquals(5920d,
        kostnadsberegnetBidragBeregning.beregn(beregnKostnadsberegnetBidragGrunnlagPeriodisert)
            .getResultatKostnadsberegnetBidragBelop());
  }

  @DisplayName("Test av resultatet rundes av til nærmeste tier. 454,878 -> 450,0 ")
  @Test
  void testResultatRundesNed() {
    KostnadsberegnetBidragBeregningImpl kostnadsberegnetBidragBeregning = new KostnadsberegnetBidragBeregningImpl();
    BeregnKostnadsberegnetBidragGrunnlagPeriodisert beregnKostnadsberegnetBidragGrunnlagPeriodisert
        = new BeregnKostnadsberegnetBidragGrunnlagPeriodisert(4, 666,
        31.7, null,
        sjablonListe
    );

    assertEquals(450d,
        kostnadsberegnetBidragBeregning.beregn(beregnKostnadsberegnetBidragGrunnlagPeriodisert)
            .getResultatKostnadsberegnetBidragBelop());
  }

}

