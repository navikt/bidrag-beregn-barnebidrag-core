package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning.BPsAndelUnderholdskostnadBeregningImpl;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.bo.Sjablon;

import no.nav.bidrag.beregn.felles.enums.InntektType;
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

      Double underholdskostnad = Double.valueOf(1000);
      var inntektBP = new ArrayList<Inntekt>();
      var inntektBM = new ArrayList<Inntekt>();
      var inntektBB = new ArrayList<Inntekt>();

      inntektBP.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(217666)));
      inntektBM.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(400000)));
      inntektBB.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(40000)));

      var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
          new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(underholdskostnad, inntektBP, inntektBM, inntektBB, sjablonListe);

      ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

      assertAll(
          () -> assertThat(resultat).isNotNull(),
          () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(33.1)
      );
    }

  @DisplayName("Beregning der barnets inntekter er høyere enn 100 * forhøyet forskuddssats. Andel skal da bli 0")
  @Test
  void testAndelLikNullVedHoyInntektBarn() {
    var bPsAndelUnderholdskostnadBeregning = new BPsAndelUnderholdskostnadBeregningImpl();

    Double underholdskostnad = Double.valueOf(1000);
    var inntektBP = new ArrayList<Inntekt>();
    var inntektBM = new ArrayList<Inntekt>();
    var inntektBB = new ArrayList<Inntekt>();

    inntektBP.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(217666)));
    inntektBM.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(400000)));
    inntektBB.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(400000)));

    var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
        new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(underholdskostnad, inntektBP, inntektBM, inntektBB, sjablonListe);

    ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(0.0)
    );
  }


  @DisplayName("Test at beregnet andel ikke settes høyere enn 5/6 (83,3)")
  @Test
  void testAtMaksAndelSettes() {
    var bPsAndelUnderholdskostnadBeregning = new BPsAndelUnderholdskostnadBeregningImpl();

    Double underholdskostnad = Double.valueOf(1000);
    var inntektBP = new ArrayList<Inntekt>();
    var inntektBM = new ArrayList<Inntekt>();
    var inntektBB = new ArrayList<Inntekt>();

    inntektBP.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(1000000)));
    inntektBM.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(40000)));
    inntektBB.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(40000)));


    // Beregnet andel skal da bli 92,6%, overstyres til 5/6 (83,3%)
    var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
        new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(underholdskostnad, inntektBP, inntektBM, inntektBB, sjablonListe);

    ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(83.3)
    );
  }


  @DisplayName("Beregning med 0 i inntekt for barn")
  @Test
  void testBeregningMedNullInntektBarn() {
    var bPsAndelUnderholdskostnadBeregning = new BPsAndelUnderholdskostnadBeregningImpl();

    Double underholdskostnad = Double.valueOf(1000);
    var inntektBP = new ArrayList<Inntekt>();
    var inntektBM = new ArrayList<Inntekt>();
    var inntektBB = new ArrayList<Inntekt>();

    inntektBP.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(502000)));
    inntektBM.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(500000)));
    inntektBB.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(0)));

    var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
        new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(underholdskostnad, inntektBP, inntektBM, inntektBB, sjablonListe);

    ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(50.1)
    );
  }

  @DisplayName("Beregning med gamle regler, beregnet andel skal avrundes til nærmeste sjettedel (maks 5/6)")
  @Test
  void testBeregningGamleReglerAvrundTreSjettedeler() {
    var bPsAndelUnderholdskostnadBeregning = new BPsAndelUnderholdskostnadBeregningImpl();

    Double underholdskostnad = Double.valueOf(1000);
    var inntektBP = new ArrayList<Inntekt>();
    var inntektBM = new ArrayList<Inntekt>();
    var inntektBB = new ArrayList<Inntekt>();

    inntektBP.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(502000)));
    inntektBM.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(500000)));
    inntektBB.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(0)));

    var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
        new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(underholdskostnad, inntektBP, inntektBM, inntektBB, sjablonListe);

    ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregnMedGamleRegler(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(50.0)
    );
  }

  @DisplayName("Beregning med gamle regler, andel skal rundes opp til 1/6")
  @Test
  void testBeregningGamleReglerAvrundEnSjettedel() {
    var bPsAndelUnderholdskostnadBeregning = new BPsAndelUnderholdskostnadBeregningImpl();

    Double underholdskostnad = Double.valueOf(1000);
    var inntektBP = new ArrayList<Inntekt>();
    var inntektBM = new ArrayList<Inntekt>();
    var inntektBB = new ArrayList<Inntekt>();

    inntektBP.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(2000)));
    inntektBM.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(500000)));
    inntektBB.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(1000)));

    var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
        new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(underholdskostnad, inntektBP, inntektBM, inntektBB, sjablonListe);

    ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregnMedGamleRegler(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(16.7)
    );
  }

  @DisplayName("Beregning med gamle regler, andel skal rundes ned til maks andel, 5/6")
  @Test
  void testBeregningGamleReglerAvrundFemSjettedeler() {
    var bPsAndelUnderholdskostnadBeregning = new BPsAndelUnderholdskostnadBeregningImpl();

    Double underholdskostnad = Double.valueOf(1000);
    var inntektBP = new ArrayList<Inntekt>();
    var inntektBM = new ArrayList<Inntekt>();
    var inntektBB = new ArrayList<Inntekt>();

    inntektBP.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(2000000)));
    inntektBM.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(2000)));
    inntektBB.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(1000)));

    var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert =
        new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(underholdskostnad, inntektBP, inntektBM, inntektBB, sjablonListe);

    ResultatBeregning resultat = bPsAndelUnderholdskostnadBeregning.beregnMedGamleRegler(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatAndelProsent()).isEqualTo(83.3)
    );
  }


}
