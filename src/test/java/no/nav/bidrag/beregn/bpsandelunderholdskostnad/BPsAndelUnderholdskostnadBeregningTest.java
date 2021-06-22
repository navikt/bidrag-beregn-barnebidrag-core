package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_BB_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_BM_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_BP_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning.BPsAndelUnderholdskostnadBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Underholdskostnad;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av BPs andel av underholdskostnad")
public class BPsAndelUnderholdskostnadBeregningTest {

  private final List<SjablonPeriode> sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

  private final BPsAndelUnderholdskostnadBeregning bPsAndelUnderholdskostnadBeregning = BPsAndelUnderholdskostnadBeregning.getInstance();

  @DisplayName("Beregning med inntekter for alle parter")
  @Test
  void testBeregningMedInntekterForAlle() {

    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(10000)),
        singletonList(new Inntekt(INNTEKT_BP_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(217666), false, false)),
        singletonList(new Inntekt(INNTEKT_BM_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(400000), false, false)),
        singletonList(new Inntekt(INNTEKT_BB_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(40000), false, false)),
        sjablonPeriodeListe);

    var resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlagBeregning, true);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getAndelProsent()).isEqualTo(BigDecimal.valueOf(35.2)),
        () -> assertThat(resultat.getAndelBelop()).isEqualTo(BigDecimal.valueOf(3520))
    );
  }

  @DisplayName("Beregning med flere inntekter for alle parter og fratrekk av 30 * forhøyet forskudd på barnets inntekt")
  @Test
  void testBeregningMedFlereInntekterForAlle() {

    var inntektBPListe = new ArrayList<Inntekt>();
    var inntektBMListe = new ArrayList<Inntekt>();
    var inntektBBListe = new ArrayList<Inntekt>();

    inntektBPListe.add(new Inntekt(INNTEKT_BP_REFERANSE + "_1", InntektType.LONN_SKE, BigDecimal.valueOf(200000), false, false));
    inntektBPListe.add(new Inntekt(INNTEKT_BP_REFERANSE + "_2", InntektType.LONN_SKE, BigDecimal.valueOf(17666), false, false));
    inntektBMListe.add(new Inntekt(INNTEKT_BM_REFERANSE + "_1", InntektType.LONN_SKE, BigDecimal.valueOf(100000), false, false));
    inntektBMListe.add(new Inntekt(INNTEKT_BM_REFERANSE + "_2", InntektType.LONN_SKE, BigDecimal.valueOf(200000), false, false));
    inntektBMListe.add(new Inntekt(INNTEKT_BM_REFERANSE + "_3", InntektType.LONN_SKE, BigDecimal.valueOf(100000), false, false));
    inntektBBListe.add(new Inntekt(INNTEKT_BB_REFERANSE + "_1", InntektType.LONN_SKE, BigDecimal.valueOf(10000), false, false));
    inntektBBListe.add(new Inntekt(INNTEKT_BB_REFERANSE + "_2", InntektType.LONN_SKE, BigDecimal.valueOf(10000), false, false));
    inntektBBListe.add(new Inntekt(INNTEKT_BB_REFERANSE + "_3", InntektType.LONN_SKE, BigDecimal.valueOf(10000), false, false));
    inntektBBListe.add(new Inntekt(INNTEKT_BB_REFERANSE + "_4", InntektType.LONN_SKE, BigDecimal.valueOf(10000), false, false));
    inntektBBListe.add(new Inntekt(INNTEKT_BB_REFERANSE + "_5", InntektType.LONN_SKE, BigDecimal.valueOf(10000), false, false));
    inntektBBListe.add(new Inntekt(INNTEKT_BB_REFERANSE + "_6", InntektType.LONN_SKE, BigDecimal.valueOf(10000), false, false));

    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
        inntektBPListe,
        inntektBMListe,
        inntektBBListe,
        sjablonPeriodeListe);

    var resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlagBeregning, true);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getAndelProsent()).isEqualTo(BigDecimal.valueOf(34.7)),
        () -> assertThat(resultat.getAndelBelop()).isEqualTo(BigDecimal.valueOf(347)),
        () -> assertThat(resultat.getBarnetErSelvforsorget()).isFalse()
    );
  }

  @DisplayName("Beregning der barnets inntekter er høyere enn 100 * forhøyet forskuddssats. Andel skal da bli 0")
  @Test
  void testAndelLikNullVedHoyInntektBarn() {

    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
        singletonList(new Inntekt(INNTEKT_BP_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(217666), false, false)),
        singletonList(new Inntekt(INNTEKT_BM_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(400000), false, false)),
        singletonList(new Inntekt(INNTEKT_BB_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(400000), false, false)),
        sjablonPeriodeListe);

    var resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlagBeregning, true);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getAndelProsent()).isEqualTo(BigDecimal.ZERO),
        () -> assertThat(resultat.getAndelBelop()).isEqualTo(BigDecimal.ZERO),
        () -> assertThat(resultat.getBarnetErSelvforsorget()).isTrue()
    );
  }

  @DisplayName("Test at beregnet andel ikke settes høyere enn 5/6 (83,3333333333). Legger inn 10 desimaler "
      + "etter ønske fra John for å få likt resultat som i Bidragskalkulator")
  @Test
  void testAtMaksAndelSettes() {

    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
        singletonList(new Inntekt(INNTEKT_BP_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(1000000), false, false)),
        singletonList(new Inntekt(INNTEKT_BM_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(40000), false, false)),
        singletonList(new Inntekt(INNTEKT_BB_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(40000), false, false)),
        sjablonPeriodeListe);

    var resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlagBeregning, true);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        // Beregnet andel skal bli 92,6%, overstyres til 5/6 (83,3333333333%)
        () -> assertThat(resultat.getAndelProsent()).isEqualTo(BigDecimal.valueOf(83.3333333333))
    );
  }

  @DisplayName("Beregning med 0 i inntekt for barn")
  @Test
  void testBeregningMedNullInntektBarn() {

    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
        singletonList(new Inntekt(INNTEKT_BP_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(502000), false, false)),
        singletonList(new Inntekt(INNTEKT_BM_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(500000), false, false)),
        singletonList(new Inntekt(INNTEKT_BB_REFERANSE, InntektType.LONN_SKE, BigDecimal.ZERO, false, false)),
        sjablonPeriodeListe);

    var resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlagBeregning, true);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getAndelProsent()).isEqualTo(BigDecimal.valueOf(50.1))
    );
  }

  @DisplayName("Beregning med gamle regler, beregnet andel skal avrundes til nærmeste sjettedel (maks 5/6)")
  @Test
  void testBeregningGamleReglerAvrundTreSjettedeler() {

    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
        singletonList(new Inntekt(INNTEKT_BP_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(502000), false, false)),
        singletonList(new Inntekt(INNTEKT_BM_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(500000), false, false)),
        singletonList(new Inntekt(INNTEKT_BB_REFERANSE, InntektType.LONN_SKE, BigDecimal.ZERO, false, false)),
        sjablonPeriodeListe);

    var resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlagBeregning, false);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getAndelProsent()).isEqualTo(BigDecimal.valueOf(50.0))
    );
  }

  @DisplayName("Beregning med gamle regler, andel skal rundes opp til 1/6")
  @Test
  void testBeregningGamleReglerAvrundEnSjettedel() {

    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
        singletonList(new Inntekt(INNTEKT_BP_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(2000), false, false)),
        singletonList(new Inntekt(INNTEKT_BM_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(500000), false, false)),
        singletonList(new Inntekt(INNTEKT_BB_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(1000), false, false)),
        sjablonPeriodeListe);

    var resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlagBeregning, false);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getAndelProsent()).isEqualTo(BigDecimal.valueOf(16.7))
    );
  }

  @DisplayName("Beregning med gamle regler, andel skal rundes ned til maks andel, 5/6")
  @Test
  void testBeregningGamleReglerAvrundFemSjettedeler() {

    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
        singletonList(new Inntekt(INNTEKT_BP_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(2000000), false, false)),
        singletonList(new Inntekt(INNTEKT_BM_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(2000), false, false)),
        singletonList(new Inntekt(INNTEKT_BB_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(1000), false, false)),
        sjablonPeriodeListe);

    var resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlagBeregning, false);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getAndelProsent()).isEqualTo(BigDecimal.valueOf(83.3333333333))
    );
  }

  @DisplayName("Test fra John")
  @Test
  void testFraJohn() {

    var grunnlagBeregning = new GrunnlagBeregning(
        new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(9355)),
        singletonList(new Inntekt(INNTEKT_BP_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(600000), false, false)),
        singletonList(new Inntekt(INNTEKT_BM_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(100000), false, false)),
        singletonList(new Inntekt(INNTEKT_BB_REFERANSE, InntektType.LONN_SKE, BigDecimal.ZERO, false, false)),
        sjablonPeriodeListe);

    var resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlagBeregning, false);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getAndelBelop()).isEqualTo(BigDecimal.valueOf(7796)),
        () -> assertThat(resultat.getAndelProsent()).isEqualTo(BigDecimal.valueOf(83.3333333333))
    );
  }
}
