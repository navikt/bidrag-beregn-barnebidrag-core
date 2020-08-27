package no.nav.bidrag.beregn.samvaersfradrag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregningImpl;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlagPeriodisert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av samværsfradrag")
public class SamvaersfradragBeregningTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Test av beregning av samvaersfradrag for fireåring")
  @Test
  void testFireAar() {
    SamvaersfradragBeregningImpl samvaersfradragBeregning = new SamvaersfradragBeregningImpl();
    BeregnSamvaersfradragGrunnlagPeriodisert beregnSamvaersfradragGrunnlagPeriodisert
        = new BeregnSamvaersfradragGrunnlagPeriodisert(4, "03",
        sjablonListe
    );

    assertEquals(2082d,
        samvaersfradragBeregning.beregn(beregnSamvaersfradragGrunnlagPeriodisert)
            .getResultatSamvaersfradragBelop());
  }

  @DisplayName("Test av beregning av samvaersfradrag for seksåring")
  @Test
  void testSeksAar() {
    SamvaersfradragBeregningImpl samvaersfradragBeregning = new SamvaersfradragBeregningImpl();
    BeregnSamvaersfradragGrunnlagPeriodisert beregnSamvaersfradragGrunnlagPeriodisert
        = new BeregnSamvaersfradragGrunnlagPeriodisert(6, "04",
        sjablonListe
    );

    assertEquals(3184d,
        samvaersfradragBeregning.beregn(beregnSamvaersfradragGrunnlagPeriodisert)
            .getResultatSamvaersfradragBelop());
  }

}

