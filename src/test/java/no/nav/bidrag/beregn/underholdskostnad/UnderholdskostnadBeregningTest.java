package no.nav.bidrag.beregn.underholdskostnad;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Test av beregning av underholdskostnad når barnet er 3 år gammelt")
  @Test
  void testBeregningAlder3MedKunSjablonverdier() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(3,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        sjablonListe
    );

    assertThat(underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad()
        .compareTo(BigDecimal.valueOf(5999))).isZero();
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 7 år gammelt")
  @Test
  void testBeregningAlder7MedKunSjablonverdier() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(7,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        sjablonListe
    );

    assertThat(underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad()
        .compareTo(BigDecimal.valueOf(7481))).isZero();

  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 10 år gammelt")
  @Test
  void testBeregningAlder10MedKunSjablonverdier() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(10,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        sjablonListe
    );

    assertThat(underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad()
        .compareTo(BigDecimal.valueOf(7481))).isZero();
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 11 år gammelt")
  @Test
  void testBeregningAlder11MedKunSjablonverdier() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        sjablonListe
    );

    assertThat(underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad()
        .compareTo(BigDecimal.valueOf(8684))).isZero();

  }
  
  @DisplayName("Test av beregning av underholdskostnad når barnet er 11 år gammelt  + stønad til tilsynsutgifter DU")
  @Test
  void testBeregningAlder11StonadBarnetilsynDU() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("DU", "64"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        sjablonListe
    );

    assertThat(underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad()
        .compareTo(BigDecimal.valueOf(6913 + 2825 + 257 - 1054))).isZero();
  }

  @DisplayName("Test at netto barnetilsynsutgifter blir lagt til underholdskostnad")
  @Test
  void testBeregningAlder11MedNettoBarnetilsyn() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        BigDecimal.valueOf(666),
        BigDecimal.ZERO,
        sjablonListe
    );

    assertThat(underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad()
        .compareTo(BigDecimal.valueOf(8684 + 666))).isZero();
  }

  @DisplayName("Test at forpleiningsutgifter blir trukket fra underholdskostnad")
  @Test
  void testBeregningAlder11MedForpleiningsutgifter() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(11,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        BigDecimal.ZERO,
        BigDecimal.valueOf(17.0),
        sjablonListe
    );

    assertThat(underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad()
        .compareTo(BigDecimal.valueOf(8684 - 17))).isZero();
  }

  @DisplayName("Test fra John")
  @Test
  void testFraJohn() {

    UnderholdskostnadBeregningImpl underholdskostnadberegning = new UnderholdskostnadBeregningImpl();

    BeregnUnderholdskostnadGrunnlagPeriodisert beregnUnderholdskostnadGrunnlagPeriodisert
        = new BeregnUnderholdskostnadGrunnlagPeriodisert(12,
        new BarnetilsynMedStonad("Ingen", "Ingen"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        sjablonListe
    );

    assertThat(underholdskostnadberegning.beregnMedOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert).getResultatBelopUnderholdskostnad()
        .compareTo(BigDecimal.valueOf(8684))).isZero();
  }

}
