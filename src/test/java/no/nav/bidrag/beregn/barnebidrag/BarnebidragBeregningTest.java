package no.nav.bidrag.beregn.barnebidrag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregningImpl;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av barnebidrag")
public class BarnebidragBeregningTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();
/*

  @DisplayName("Test av beregning av kostnadsberegnetBidrag")
  @Test
  void testBeregningUtenSamvaer() {
    BarnebidragBeregningImpl kostnadsberegnetBidragBeregning = new BarnebidragBeregningImpl();
    BeregnBarnebidragGrunnlagPeriodisert beregnBarnebidragGrunnlagPeriodisert
        = new BeregnBarnebidragGrunnlagPeriodisert(3, 10000,
        20.0, null,
        sjablonListe
    );

    assertEquals(8000d,
        kostnadsberegnetBidragBeregning.beregn(beregnBarnebidragGrunnlagPeriodisert)
            .getResultatBarnebidragBelop());
  }


  @DisplayName("Test av beregning av kostnadsberegnetBidrag med samværsfradrag")
  @Test
  void testBeregningMedSamvaer() {
    BarnebidragBeregningImpl kostnadsberegnetBidragBeregning = new BarnebidragBeregningImpl();
    BeregnBarnebidragGrunnlagPeriodisert beregnBarnebidragGrunnlagPeriodisert
        = new BeregnBarnebidragGrunnlagPeriodisert(19, 10000,
        20.0, "01",
        sjablonListe
    );

    assertEquals(10000d - 2000d - 460d,
        kostnadsberegnetBidragBeregning.beregn(beregnBarnebidragGrunnlagPeriodisert)
            .getResultatBarnebidragBelop());
  }

  @DisplayName("Test av resultatet rundes av til nærmeste tier. 5918 -> 5920")
  @Test
  void testResultatRundesOpp() {
    BarnebidragBeregningImpl kostnadsberegnetBidragBeregning = new BarnebidragBeregningImpl();
    BeregnBarnebidragGrunnlagPeriodisert beregnBarnebidragGrunnlagPeriodisert
        = new BeregnBarnebidragGrunnlagPeriodisert(4, 10000,
        20.0, "03",
        sjablonListe
    );

    assertEquals(5920d,
        kostnadsberegnetBidragBeregning.beregn(beregnBarnebidragGrunnlagPeriodisert)
            .getResultatBarnebidragBelop());
  }

  @DisplayName("Test av resultatet rundes av til nærmeste tier. 454,878 -> 450,0 ")
  @Test
  void testResultatRundesNed() {
    BarnebidragBeregningImpl kostnadsberegnetBidragBeregning = new BarnebidragBeregningImpl();
    BeregnBarnebidragGrunnlagPeriodisert beregnBarnebidragGrunnlagPeriodisert
        = new BeregnBarnebidragGrunnlagPeriodisert(4, 666,
        31.7, null,
        sjablonListe
    );

    assertEquals(450d,
        kostnadsberegnetBidragBeregning.beregn(beregnBarnebidragGrunnlagPeriodisert)
            .getResultatBarnebidragBelop());
  }
*/

}

