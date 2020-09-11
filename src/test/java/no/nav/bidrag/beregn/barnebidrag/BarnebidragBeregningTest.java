package no.nav.bidrag.beregn.barnebidrag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggBM;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggBP;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaret;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregningImpl;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av barnebidrag")
public class BarnebidragBeregningTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();


  @DisplayName("Test av beregning av barnebidrag")
  @Test
  void testBeregning() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(10000d, 2000d, 200d,
        new BarnetilleggBP(1000d, 17d),
        new BarnetilleggBM(1000d, 17d),
        new BarnetilleggForsvaret(true, 2),
        sjablonListe
    );

    assertEquals(8000d,
        barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatBarnebidragBelop());
  }


}

