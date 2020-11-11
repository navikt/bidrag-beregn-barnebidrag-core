package no.nav.bidrag.beregn.samvaersfradrag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregningImpl;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregningPeriodisert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av samværsfradrag")
public class SamvaersfradragBeregningTest {

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Test av beregning av samvaersfradrag for fireåring")
  @Test
  void testFireAar() {
    SamvaersfradragBeregningImpl samvaersfradragBeregning = new SamvaersfradragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(4, "03",
        sjablonListe
    );

    assertEquals(2272d,
        samvaersfradragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatSamvaersfradragBelop().doubleValue());
  }

  @DisplayName("Test av beregning av samvaersfradrag for seksåring")
  @Test
  void testSeksAar() {
    SamvaersfradragBeregningImpl samvaersfradragBeregning = new SamvaersfradragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(6, "03",
        sjablonListe
    );

    assertEquals(2716d,
        samvaersfradragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatSamvaersfradragBelop().doubleValue());
  }

  @DisplayName("Test fra John")
  @Test
  void testFraJohn() {
    SamvaersfradragBeregningImpl samvaersfradragBeregning = new SamvaersfradragBeregningImpl();
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(14, "01",
        sjablonListe
    );

    assertEquals(457d,
        samvaersfradragBeregning.beregn(grunnlagBeregningPeriodisert)
            .getResultatSamvaersfradragBelop().doubleValue());
  }

}
