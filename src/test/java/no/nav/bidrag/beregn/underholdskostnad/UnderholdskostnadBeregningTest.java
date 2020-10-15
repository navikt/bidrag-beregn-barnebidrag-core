package no.nav.bidrag.beregn.underholdskostnad;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.underholdskostnad.beregning.UnderholdskostnadBeregningImpl;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning Underholdskostnad")
class UnderholdskostnadBeregningTest {
  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Test av beregning av underholdskostnad når barnet er 3 år gammelt")
  @Test
  void testBeregningAlder3MedKunSjablonverdier() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(3,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        0.0d,
        0.0d,
        sjablonListe
    );

    assertEquals(5999,
        underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 7 år gammelt")
  @Test
  void testBeregningAlder7MedKunSjablonverdier() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(7,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        0.0d,
        0.0d,
        sjablonListe
    );

    assertEquals(7481d,
        underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 10 år gammelt")
  @Test
  void testBeregningAlder10MedKunSjablonverdier() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(10,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        0.0d,
        0.0d,
        sjablonListe
    );

    assertEquals(7481d,
        underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 11 år gammelt")
  @Test
  void testBeregningAlder11MedKunSjablonverdier() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        0.0d,
        0.0d,
        sjablonListe
    );

    assertEquals(8684d,
        underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }


  @DisplayName("Test av hent av stønad til tilsynsutgifter DU")
  @Test
  void testStonadBarnetilsynDU() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("DU", "64"),
        0.0d,
        0.0d,
        sjablonListe
    );

    assertEquals(257d,
        underholdskostnadberegning.beregnBarnetilsynMedStonad(beregnUnderholdskostnadGrunnlagPeriodisert));
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 11 år gammelt  + stønad til tilsynsutgifter DU")
  @Test
  void testBeregningAlder11StonadBarnetilsynDU() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("DU", "64"),
        0.0d,
        0.0d,
        sjablonListe
    );
    assertEquals((6913d + 2825d + 257d - 1054d),
        underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }

  @DisplayName("Test at netto barnetilsynsutgifter blir lagt til underholdskostnad")
  @Test
  void testBeregningAlder11MedNettoBarnetilsyn() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        666d,
        0.0d,
        sjablonListe
    );

    assertEquals(8684d + 666d,
        underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }

  @DisplayName("Test at forpleiningsutgifter blir trukket fra underholdskostnad")
  @Test
  void testBeregningAlder11MedForpleiningsutgifter() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        0.0d,
        17.0d,
        sjablonListe
    );

    assertEquals(8684d - 17d,
        underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }

  @DisplayName("Test fra John")
  @Test
  void testFraJohn() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(12,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        0.0d,
        0.0d,
        sjablonListe
    );

    assertEquals(8684d,
        underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad());
  }

}
