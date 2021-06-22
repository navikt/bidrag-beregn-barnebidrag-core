package no.nav.bidrag.beregn.underholdskostnad;

import static no.nav.bidrag.beregn.TestUtil.BARNETILSYN_MED_STONAD_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.FORPLEINING_UTGIFT_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.NETTO_BARNETILSYN_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.ORDINAER_BARNETRYGD;
import static no.nav.bidrag.beregn.TestUtil.SOKNADSBARN_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.underholdskostnad.beregning.UnderholdskostnadBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgift;
import no.nav.bidrag.beregn.underholdskostnad.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsyn;
import no.nav.bidrag.beregn.underholdskostnad.bo.SoknadsbarnAlder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning Underholdskostnad")
class UnderholdskostnadBeregningTest {

  private final List<SjablonPeriode> sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

  private final UnderholdskostnadBeregning underholdskostnadberegning = UnderholdskostnadBeregning.getInstance();

  @DisplayName("Test av beregning av underholdskostnad når barnet er 3 år gammelt")
  @Test
  void testBeregningAlder3MedKunSjablonverdier() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 3),
        new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "Ingen", "Ingen"),
        new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.ZERO),
        new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.ZERO),
        sjablonPeriodeListe
    );

    var resultat = underholdskostnadberegning.beregn(grunnlagBeregning, ORDINAER_BARNETRYGD);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(5999))).isZero();
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 7 år gammelt")
  @Test
  void testBeregningAlder7MedKunSjablonverdier() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 7),
        new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "Ingen", "Ingen"),
        new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.ZERO),
        new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.ZERO),
        sjablonPeriodeListe
    );

    var resultat = underholdskostnadberegning.beregn(grunnlagBeregning, ORDINAER_BARNETRYGD);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(7481))).isZero();
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 10 år gammelt")
  @Test
  void testBeregningAlder10MedKunSjablonverdier() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 10),
        new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "Ingen", "Ingen"),
        new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.ZERO),
        new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.ZERO),
        sjablonPeriodeListe
    );

    var resultat = underholdskostnadberegning.beregn(grunnlagBeregning, ORDINAER_BARNETRYGD);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(7481))).isZero();
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 11 år gammelt")
  @Test
  void testBeregningAlder11MedKunSjablonverdier() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 11),
        new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "Ingen", "Ingen"),
        new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.ZERO),
        new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.ZERO),
        sjablonPeriodeListe
    );

    var resultat = underholdskostnadberegning.beregn(grunnlagBeregning, ORDINAER_BARNETRYGD);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(8684))).isZero();
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 12 år gammelt")
  @Test
  void testBeregningAlder12MedKunSjablonverdier() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 12),
        new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "Ingen", "Ingen"),
        new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.ZERO),
        new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.ZERO),
        sjablonPeriodeListe
    );

    var resultat = underholdskostnadberegning.beregn(grunnlagBeregning, ORDINAER_BARNETRYGD);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(8684))).isZero();
  }

  @DisplayName("Test av beregning av underholdskostnad når barnet er 11 år gammelt  + stønad til tilsynsutgifter DU")
  @Test
  void testBeregningAlder11StonadBarnetilsynDU() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 11),
        new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "DU", "64"),
        new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.ZERO),
        new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.ZERO),
        sjablonPeriodeListe
    );

    var resultat = underholdskostnadberegning.beregn(grunnlagBeregning, ORDINAER_BARNETRYGD);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(6913 + 2825 + 257 - 1054))).isZero();
  }

  @DisplayName("Test at netto barnetilsynsutgifter blir lagt til underholdskostnad")
  @Test
  void testBeregningAlder11MedNettoBarnetilsyn() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 11),
        new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "Ingen", "Ingen"),
        new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.valueOf(666)),
        new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.ZERO),
        sjablonPeriodeListe
    );

    var resultat = underholdskostnadberegning.beregn(grunnlagBeregning, ORDINAER_BARNETRYGD);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(8684 + 666))).isZero();
  }

  @DisplayName("Test at forpleiningsutgifter blir trukket fra underholdskostnad")
  @Test
  void testBeregningAlder11MedForpleiningsutgifter() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 11),
        new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "Ingen", "Ingen"),
        new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.ZERO),
        new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.valueOf(17)),
        sjablonPeriodeListe
    );

    var resultat = underholdskostnadberegning.beregn(grunnlagBeregning, ORDINAER_BARNETRYGD);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(8684 - 17))).isZero();
  }

  //TODO Legge til tester som bruker forhøyet barnetrygd eller ingen barnetrygd
}
