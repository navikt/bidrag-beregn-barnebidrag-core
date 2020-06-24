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

  @DisplayName("Test av beregning av underholdskostnad når barnet er 3 år gammelt")
  @Test
  void testBeregningAlder3MedKunSjablonverdier() {

    UnderholdskostnadberegningImpl underholdskostnadberegning = new UnderholdskostnadberegningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(3,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        new BarnetilsynFaktiskUtgiftBrutto(0.0),
        new ForpleiningUtgift(0.0),
        sjablonListe
    );

    assertEquals(6436d,
        underholdskostnadberegning.beregn(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 7 år gammelt")
  @Test
  void testBeregningAlder7MedKunSjablonverdier() {

    UnderholdskostnadberegningImpl underholdskostnadberegning = new UnderholdskostnadberegningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(7,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        new BarnetilsynFaktiskUtgiftBrutto(0.0),
        new ForpleiningUtgift(0.0),
        sjablonListe
    );

    assertEquals(7888d,
        underholdskostnadberegning.beregn(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 10 år gammelt")
  @Test
  void testBeregningAlder10MedKunSjablonverdier() {

    UnderholdskostnadberegningImpl underholdskostnadberegning = new UnderholdskostnadberegningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(10,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        new BarnetilsynFaktiskUtgiftBrutto(0.0),
        new ForpleiningUtgift(0.0),
        sjablonListe
    );

    assertEquals(7888d,
        underholdskostnadberegning.beregn(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 11 år gammelt")
  @Test
  void testBeregningAlder11MedKunSjablonverdier() {

    UnderholdskostnadberegningImpl underholdskostnadberegning = new UnderholdskostnadberegningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        new BarnetilsynFaktiskUtgiftBrutto(0.0),
        new ForpleiningUtgift(0.0),
        sjablonListe
    );

    assertEquals(8874d,
        underholdskostnadberegning.beregn(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }


  @DisplayName("Test av hent av stønad til tilsynsutgifter DU")
  @Test
  void testStonadBarnetilsynDU() {

    UnderholdskostnadberegningImpl underholdskostnadberegning = new UnderholdskostnadberegningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("DU", "64"),
        new BarnetilsynFaktiskUtgiftBrutto(0.0),
        new ForpleiningUtgift(0.0),
        sjablonListe
    );

    assertEquals(258d,
        underholdskostnadberegning.beregnBarnetilsynMedStonad(beregnUnderholdskostnadGrunnlagPeriodisert));
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 11 år gammelt  + stønad til tilsynsutgifter DU")
  @Test
  void testBeregningAlder11StonadBarnetilsynDU() {

    UnderholdskostnadberegningImpl underholdskostnadberegning = new UnderholdskostnadberegningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("DU", "64"),
        new BarnetilsynFaktiskUtgiftBrutto(0.0),
        new ForpleiningUtgift(0.0),
        sjablonListe
    );

    assertEquals((6099d + 2775d + 258d),
        underholdskostnadberegning.beregn(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }
}
