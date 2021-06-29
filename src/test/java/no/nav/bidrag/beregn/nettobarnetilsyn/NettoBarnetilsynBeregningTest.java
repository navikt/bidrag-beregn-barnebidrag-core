package no.nav.bidrag.beregn.nettobarnetilsyn;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.FAKTISK_UTGIFT_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.beregning.NettoBarnetilsynBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av Netto Barnetilsyn")
class NettoBarnetilsynBeregningTest {

  private List<SjablonPeriode> sjablonPeriodeListe;

  private final NettoBarnetilsynBeregning nettoBarnetilsynBeregning = NettoBarnetilsynBeregning.getInstance();

  @BeforeEach
  void byggSjablonPeriodeListe() {
    sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();
  }

  @Test
  @DisplayName("Beregning med ett barn under maks tilsynsbeløp, resultatet skal da beregnes fra innsendt faktisk utgift-beløp")
  void testEttBarnEttBelopUnderMaksTilsynsbelop() {
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(2500))),
        sjablonPeriodeListe
    );
    var resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.size()).isEqualTo(1),
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(1978))).isZero()
    );
  }

  @Test
  @DisplayName("Beregning med to barn, beløp under maks tilsynsbeløp, resultatet skal da beregnes fra innsendt faktisk utgift-beløp")
  void testToBarnUnderMaksTilsynsbelop() {
    var grunnlagBeregning = new GrunnlagBeregning(
        asList(new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(2500)),
            new FaktiskUtgift(2, FAKTISK_UTGIFT_REFERANSE, 5, BigDecimal.valueOf(5000))),
        sjablonPeriodeListe
    );
    var resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.size()).isEqualTo(2),
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(2083))).isZero(),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(4583))).isZero()
    );
  }

  @Test
  @DisplayName("Test at barn med flere innsendte faktiske utgifter kun får ett summert beløp i resultatet")
  void testAtFaktiskUtgiftSummeresPerBarn() {
    var grunnlagBeregning = new GrunnlagBeregning(
        asList(new FaktiskUtgift(2, FAKTISK_UTGIFT_REFERANSE, 5, BigDecimal.valueOf(2000)),
            new FaktiskUtgift(2, FAKTISK_UTGIFT_REFERANSE, 5, BigDecimal.valueOf(1000)),
            new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(500)),
            new FaktiskUtgift(2, FAKTISK_UTGIFT_REFERANSE, 5, BigDecimal.valueOf(2000)),
            new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(2000))),
        sjablonPeriodeListe
    );
    var resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.size()).isEqualTo(2),
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(2083))).isZero(),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(4583))).isZero()
    );
  }

  @Test
  @DisplayName("Beregning med to barn, beløp over maks tilsynsbeløp, og fradragsbeløp over maks fradragsbeløp,  "
      + "resultatet skal da beregnes fra sjablon maks tilsynsbeløp for to barn og sjablon maks fradragsbeløp for to barn")
  void testToBarnOverMaksTilsynsbelopogMaksFradragsbelop() {
    var grunnlagBeregning = new GrunnlagBeregning(
        asList(new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 5, BigDecimal.valueOf(7000)),
            new FaktiskUtgift(2, FAKTISK_UTGIFT_REFERANSE, 9, BigDecimal.valueOf(3000))),
        sjablonPeriodeListe
    );
    var resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning);

    /*
    Utregning: For to barn er maks tilsynsbeløp 8109.- og innsendte faktiske utgifter på 7000 + 3000 reduseres heretter.
    Justeringen skal være forholdsmessig, dvs barn 1 skal ha 70% av maks tilsynsbeløp (5676,3),
    barn 2 skal ha 30% (2432,7).
    Fradrag skattefordel: Skattesats 25,05% av maks tilsynsbeløp 8109 = 2031.3045. Dette er høyere enn 25,05% av maks fradragsbeløp (blir 834,9165).
    834,9165 skal derfor brukes og fordeles likt mellom barna.
    Netto barnetilsyn for barn 1 skal da bli 5676,3 - (834,9165/2) = 5258,84175, avrundes til 5258,84,
    for barn 2: 2432,7 - (834,9165/2) = 2015,24175, avrundes til 2015,24
     */
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.size()).isEqualTo(2),
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(5367))).isZero(),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(2062))).isZero()
    );
  }

  @Test
  @DisplayName("Beregning med tre barn, beløp over maks tilsynsbeløp, og fradragsbeløp over maks fradragsbeløp,  "
      + "resultatet skal da beregnes fra sjablon maks tilsynsbeløp for tre barn og sjablon maks fradragsbeløp for tre barn")
  void testTreBarnOverMaksTilsynsbelopOgMaksFradragsbelop() {
    var grunnlagBeregning = new GrunnlagBeregning(
        asList(new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(5000)),
            new FaktiskUtgift(2, FAKTISK_UTGIFT_REFERANSE, 8, BigDecimal.valueOf(3000)),
            new FaktiskUtgift(3, FAKTISK_UTGIFT_REFERANSE, 3, BigDecimal.valueOf(2000))),
        sjablonPeriodeListe
    );
    var resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning);

    /*
    Utregning: For tre barn er maks tilsynsbeløp 9189.- og innsendte faktiske utgifter på 5000 + 3000 + 2000 reduseres heretter.
    Justeringen skal være forholdsmessig, dvs barn 1 skal ha 50% av maks tilsynsbeløp (4594,5),
    barn 2 skal ha 30% (2756,7) og barn 3 20% (1837,8).
    Fradrag skattefordel: Skattesats 25,05% av maks tilsynsbeløp 9189 = 2301.8445. Dette er høyere enn 25,05% av maks fradragsbeløp (blir 1148,0415).
    1148,0415 skal derfor brukes og fordeles likt mellom barna.
    Netto barnetilsyn for barn 1 skal da bli 4594,5 - (1148,0415/3) = 4211,8195, avrundes til 4211,82,
    for barn 2: 2756,7 - (1148,0415/3) = 2374,0195, avrundes til 2374,02, for barn 3: 1837,8 - (1148,0415/3) = 1455,1195, avrundes til 1455,12
     */
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.size()).isEqualTo(3),
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(4299))).isZero(),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(2427))).isZero(),
        () -> assertThat(resultat.get(2).getBelop().compareTo(BigDecimal.valueOf(1490))).isZero()
    );
  }

  @Test
  @DisplayName("Beregning med ett barn, beløp så lavt at beregnet fradragsbeløp blir brukt i stedet for sjablon")
  void testEttBarnBeregnetFradragsbelopLavereEnnSjablonverdi() {
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(1000))),
        sjablonPeriodeListe
    );
    var resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning);

    /*
    Utregning: For ett barn er maks tilsynsbeløp 6214.-, innsendt beløp 1000 er lavere og dette brukes videre.
    Fradrag skattefordel: Skattesats 25,05% av innsendt tilsynsbeløp 1000 = 250,5.
    Netto barnetilsyn for barn 1 skal da bli 1000 - 250,5 = 749,5
     */
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.size()).isEqualTo(1),
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(750))).isZero()
    );
  }

  @Test
  @DisplayName("Test sortering på input på personid")
  void testSorteringPaaSoknadsbarnPersonId() {
    var grunnlagBeregning = new GrunnlagBeregning(
        asList(new FaktiskUtgift(3, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(1000)),
            new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(2000)),
            new FaktiskUtgift(2, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(3000))),
        sjablonPeriodeListe
    );
    var resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.size()).isEqualTo(3),
        () -> assertThat(resultat.get(0).getSoknadsbarnPersonId()).isEqualTo(1),
        () -> assertThat(resultat.get(1).getSoknadsbarnPersonId()).isEqualTo(2),
        () -> assertThat(resultat.get(2).getSoknadsbarnPersonId()).isEqualTo(3)
    );
  }

  @Test
  @DisplayName("Test summering på søknadsbarns personid")
  void testSummeringPaaSoknadsbarnPersonId() {
    var grunnlagBeregning = new GrunnlagBeregning(
        asList(new FaktiskUtgift(2, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(1000)),
            new FaktiskUtgift(3, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(2000)),
            new FaktiskUtgift(2, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(2000)),
            new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(5000))),
        sjablonPeriodeListe
    );
    var resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.size()).isEqualTo(3),
        () -> assertThat(resultat.get(0).getSoknadsbarnPersonId()).isEqualTo(1),
        () -> assertThat(resultat.get(1).getSoknadsbarnPersonId()).isEqualTo(2),
        () -> assertThat(resultat.get(2).getSoknadsbarnPersonId()).isEqualTo(3),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(2427))).isZero()
    );
  }


  @Test
  @DisplayName("Test eksempler fra John")
  void testEksemplerFraJohn() {
    var grunnlagBeregning = new GrunnlagBeregning(
        asList(new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.ZERO),
            new FaktiskUtgift(2, FAKTISK_UTGIFT_REFERANSE, 8, BigDecimal.valueOf(2500)),
            new FaktiskUtgift(3, FAKTISK_UTGIFT_REFERANSE, 14, BigDecimal.ZERO)),
        sjablonPeriodeListe
    );
    var resultat = nettoBarnetilsynBeregning.beregn(grunnlagBeregning);
    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.ZERO)).isZero(),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(1874))).isZero()
    );
  }
}
