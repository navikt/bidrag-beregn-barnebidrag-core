package no.nav.bidrag.beregn.underholdskostnad;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.underholdskostnad.beregning.UnderholdskostnadberegningImpl;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynFaktiskUtgiftBrutto;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgift;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning Underholdskostnad")
class UnderholdskostnadberegningTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Test hent av forbruksavgift for en gitt alder")
  @Test
  void testHentForbruksutgift() {

    UnderholdskostnadberegningImpl underholdskostnadberegning = new UnderholdskostnadberegningImpl();

    //
    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert("12",
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        new BarnetilsynFaktiskUtgiftBrutto(0.0),
        new ForpleiningUtgift(0.0),
        sjablonListe
    );

    assertEquals(6099d,
        underholdskostnadberegning.beregn(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());



  }

}
