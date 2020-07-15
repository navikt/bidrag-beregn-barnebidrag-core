package no.nav.bidrag.beregn.nettobarnetilsyn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.nettobarnetilsyn.beregning.NettoBarnetilsynBeregningImpl;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av Netto Barnetilsyn")
class NettoBarnetilsynBeregningTest {
  private List<FaktiskUtgift> faktiskUtgiftListe = new ArrayList<>();
  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Beregning med ett barn under maks tilsynsbeløp, resultatet skal da beregnes fra innsendt faktisk utgift-beløp")
  @Test
  void testEttBarnEttBelopUnderMaksTilsynsbelop() {
    NettoBarnetilsynBeregningImpl nettoBarnetilsynBeregning = new NettoBarnetilsynBeregningImpl();

    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),1,2500d));

    BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert
        = new BeregnNettoBarnetilsynGrunnlagPeriodisert(faktiskUtgiftListe, sjablonListe);

    var resultat = nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatBeregningListe().get(0).getResultatBelop()).isEqualTo(1978.13)
    );
  }

  @DisplayName("Beregning med to barn, beløp under maks tilsynsbeløp, resultatet skal da beregnes fra innsendt faktisk utgift-beløp")
  @Test
  void testToBarnUnderMaksTilsynsbelop() {

    NettoBarnetilsynBeregningImpl nettoBarnetilsynBeregning = new NettoBarnetilsynBeregningImpl();

    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),1,2500d));
    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2015-05-17"),2,5000d));

    BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert
        = new BeregnNettoBarnetilsynGrunnlagPeriodisert(faktiskUtgiftListe, sjablonListe
    );

    var resultat = nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatBeregningListe().get(0).getResultatBelop()).isEqualTo(2082.54),
        () -> assertThat(resultat.getResultatBeregningListe().get(1).getResultatBelop()).isEqualTo(4582.54)
    );
  }

  @DisplayName("Beregning med to barn, beløp over maks tilsynsbeløp, resultatet skal da beregnes fra sjablon maks tilsynsbeløp")
  @Test
  void testToBarnOverMaksTilsynsbelop() {

    NettoBarnetilsynBeregningImpl nettoBarnetilsynBeregning = new NettoBarnetilsynBeregningImpl();

    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),1,7000d));
    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2011-01-01"),2,3000d));

    BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert
        = new BeregnNettoBarnetilsynGrunnlagPeriodisert(faktiskUtgiftListe, sjablonListe
    );

    var resultat = nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert);

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
        () -> assertThat(resultat.getResultatBeregningListe().get(0).getResultatBelop()).isEqualTo(5258.84),
        () -> assertThat(resultat.getResultatBeregningListe().get(1).getResultatBelop()).isEqualTo(2015.24)
    );
  }

  @DisplayName("Beregning med tre barn, beløp over maks tilsynsbeløp, resultatet skal da beregnes fra sjablon maks tilsynsbeløp for tre barn")
  @Test
  void testTreBarnOverMaksTilsynsbelop() {

    NettoBarnetilsynBeregningImpl nettoBarnetilsynBeregning = new NettoBarnetilsynBeregningImpl();

    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),1,5000d));
    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2012-05-15"),2,3000d));
    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2017-03-17"),3,2000d));

    BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert
        = new BeregnNettoBarnetilsynGrunnlagPeriodisert(faktiskUtgiftListe, sjablonListe
    );

    var resultat = nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert);

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
        () -> assertThat(resultat.getResultatBeregningListe().get(0).getResultatBelop()).isEqualTo(4211.82),
        () -> assertThat(resultat.getResultatBeregningListe().get(1).getResultatBelop()).isEqualTo(2374.02),
        () -> assertThat(resultat.getResultatBeregningListe().get(2).getResultatBelop()).isEqualTo(1455.12)
    );
  }

  @DisplayName("Beregning med ett barn, beløp så lavt at beregnet fradragsbeløp blir brukt i stedet for sjablon")
  @Test
  void testEttBarnBeregnetFradragsbelopLavereEnnSjablonverdi() {

    NettoBarnetilsynBeregningImpl nettoBarnetilsynBeregning = new NettoBarnetilsynBeregningImpl();

    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),1,1000d));

    BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert
        = new BeregnNettoBarnetilsynGrunnlagPeriodisert(faktiskUtgiftListe, sjablonListe
    );

    var resultat = nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert);

    /*
    Utregning: For ett barn er maks tilsynsbeløp 6214.-, innsendt beløp 1000 er lavere og dette brukes videre.
    Fradrag skattefordel: Skattesats 25,05% av innsendt tilsynsbeløp 1000 = 250,5.
    Netto barnetilsyn for barn 1 skal da bli 1000 - 250,5 = 749,5
     */
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatBeregningListe().get(0).getResultatBelop()).isEqualTo(749.5)
    );
  }

  @DisplayName("Test sortering på input på personid")
  @Test
  void testSorteringPaaSoknadsbarnPersonId() {

    NettoBarnetilsynBeregningImpl nettoBarnetilsynBeregning = new NettoBarnetilsynBeregningImpl();

    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),3,1000d));
    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),1,2000d));
    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),2,3000d));

    BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert
        = new BeregnNettoBarnetilsynGrunnlagPeriodisert(faktiskUtgiftListe, sjablonListe
    );

    var resultat = nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatBeregningListe().get(0).getResultatPersonIdSoknadsbard()).isEqualTo(1),
        () -> assertThat(resultat.getResultatBeregningListe().get(1).getResultatPersonIdSoknadsbard()).isEqualTo(2),
        () -> assertThat(resultat.getResultatBeregningListe().get(2).getResultatPersonIdSoknadsbard()).isEqualTo(3));
  }

/*
  @DisplayName("Test at barn med flere innsendte faktiske utgifter kun får ett summert beløp i resultatet")
  @Test
  void testAtFaktiskUtgiftSummeresPerBarn() {

    NettoBarnetilsynBeregningImpl nettoBarnetilsynBeregning = new NettoBarnetilsynBeregningImpl();

    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),1,1000d));
    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),1,2000d));
    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),1,3000d));

    BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert
        = new BeregnNettoBarnetilsynGrunnlagPeriodisert(faktiskUtgiftListe, sjablonListe
    );

    var resultat = nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert);

    assertEquals(Double.valueOf(1), resultat.getResultatBeregningListe().size());
  }
*/

}