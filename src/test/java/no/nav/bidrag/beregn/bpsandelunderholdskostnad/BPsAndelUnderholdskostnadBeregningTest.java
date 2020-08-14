package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning.BPsAndelUnderholdskostnadBeregningImpl;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekter;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.bo.Sjablon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av BPs andel av underholdskostnad")
public class BPsAndelUnderholdskostnadBeregningTest {

    private Double inntektBP = 0.0;
    private Double inntektBM = 0.0;
    private Double inntektBB = 0.0;
    private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

    @DisplayName("Beregning med inntekter for alle parter")
    @Test
    void testBeregningMedInntekterForAlle() {
      var bPsAndelUnderholdskostnadBeregning = new BPsAndelUnderholdskostnadBeregningImpl();

      inntektBP = Double.valueOf(217666);
      inntektBM = Double.valueOf(400000);
      inntektBB = Double.valueOf(400000);

      Inntekter inntekter = new Inntekter(inntektBP, inntektBM, inntektBB);

      var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
          new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(inntekter, sjablonListe);

      ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

      assertAll(
          () -> assertThat(resultat).isNotNull(),
          () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(0.214)
      );
    }

}
