package no.nav.bidrag.beregn.barnebidrag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg;
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregningImpl;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av barnebidrag")
public class BarnebidragBeregningTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();
  private List<GrunnlagBeregningPerBarn> grunnlagBeregningPerBarnListe  = new ArrayList<>();

  @DisplayName("Beregner ved full evne, ett barn, ingen barnetillegg")
  @Test
  void testBeregningEttBarnMedFullEvneIngenBarnetillegg() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    // Tester mot Doubleverdi her fordi svaret returneres som 8.00E+3
    assertEquals(8000, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.KOSTNADSBEREGNET_BIDRAG, resultat.get(0).getResultatkode());
  }

  @DisplayName("Beregner ved full evne, ett barn, og barnetillegg for BP der barnetillegg er høyere enn beregnet bidrag"
      + "Endelig bidrag skal da settes likt barnetillegg for BP")
  @Test
  void testBeregning1BarnFullEvneBarnetilleggBP() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(1000),
       false), BigDecimal.valueOf(100), false,
        new Barnetillegg(BigDecimal.valueOf(1700), BigDecimal.valueOf(10)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    // 1700d-(1700d*10d/100)-100d
    // Tester mot Doubleverdi her fordi svaret returneres som 1.43E+3

    assertEquals(1430, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_BP, resultat.get(0).getResultatkode());
  }

  @DisplayName("Beregner ved full evne, to barn")
  @Test
  void testBeregning2BarnFullEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(20000), BigDecimal.valueOf(20000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(7000),
            false), BigDecimal.valueOf(0),false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(8000, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(7000, resultat.get(1).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.KOSTNADSBEREGNET_BIDRAG, resultat.get(0).getResultatkode());
  }

  @DisplayName("Beregner for tre barn med for lav bidragsevne")
  @Test
  void testBeregning3BarnBegrensetAvBidragsevne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(8000), BigDecimal.valueOf(12000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(5000),
        false), BigDecimal.valueOf(0),false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(3000),
        false),
        BigDecimal.valueOf(0),false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(2000),
            false), BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(4000, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(0).getResultatkode());

    assertEquals(2400, resultat.get(1).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(1).getResultatkode());

    assertEquals(1600, resultat.get(2).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(2).getResultatkode());

  }

  @DisplayName("Beregner ved manglende evne, ett barn")
  @Test
  void testBeregning1BarnIkkeFullEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(2000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(0),false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(1000, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(0).getResultatkode());
  }


  @DisplayName("Beregner ved manglende evne, to barn")
  @Test
  void testBeregning2BarnIkkeFullEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(20000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(7000),
            false), BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(5330, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(0).getResultatkode());

    assertEquals(4670, resultat.get(1).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(1).getResultatkode());

  }

  @DisplayName("Beregner for tre barn som begrenses av 25%-regel")
  @Test
  void testBeregning3BarnBegrensetAv25ProsentAvInntekt() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(12000), BigDecimal.valueOf(8000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(5000),
            false), BigDecimal.valueOf(0),false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(3000),
            false), BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(2000),
            false), BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4000, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT, resultat.get(0).getResultatkode());

    assertEquals(2400, resultat.get(1).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT, resultat.get(1).getResultatkode());

    assertEquals(1600, resultat.get(2).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT, resultat.get(2).getResultatkode());

    assertEquals(8000, (resultat.get(0).getResultatBarnebidragBelop()
        .add(resultat.get(1).getResultatBarnebidragBelop())
        .add(resultat.get(2).getResultatBarnebidragBelop())).doubleValue());
  }

  @DisplayName("Beregner at bidrag settes likt underholdskostnad minus nettobarnetilleggBM. Dette skjer "
      + "når beregnet bidrag er høyere enn underholdskostnad minus netto barnetillegg for BM. "
      + "Det skal trekkes fra for samvær også her ")
  @Test
  void testBeregningBidragSettesLiktBarnetilleggBM() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(8000), BigDecimal.valueOf(12000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(1000),
            false), BigDecimal.valueOf(50), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(1000), BigDecimal.valueOf(10d))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(300, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM, resultat.get(0).getResultatkode());

  }

  @DisplayName("Beregner at bidrag settes likt barnetilleggBP der det også finnes barnetillegg BM."
      + "Barnetillegg for BP overstyrer barnetilleggBM")
  @Test
  void testBeregningBidragSettesLiktBarnetilleggBP() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(8000), BigDecimal.valueOf(12000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(200),
            false), BigDecimal.valueOf(0),false,
        new Barnetillegg(BigDecimal.valueOf(500), BigDecimal.valueOf(10)),
        new Barnetillegg(BigDecimal.valueOf(1000), BigDecimal.valueOf(10))));


    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(450, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_BP, resultat.get(0).getResultatkode());

  }
  @DisplayName("Beregner at bidrag settes likt BPs andel av underholdskostnad minus samværsfradrag")
  @Test
  void testBeregningFradragSamvaer() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(8000), BigDecimal.valueOf(12000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(2000),
            false), BigDecimal.valueOf(200), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(1800, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.KOSTNADSBEREGNET_BIDRAG, resultat.get(0).getResultatkode());
  }

  @DisplayName("Beregner for tre barn der barna har barnetillegg BP eller BM. "
      + "Bidrag settes likt underholdskostnad minus netto barnetilleggBM når beregnet bidrag er høyere enn"
      + "underholdskostnad minus netto barnetillegg for BM")
  @Test
  void testBeregning3BarnBarnetilleggBPogBM() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(12000), BigDecimal.valueOf(8000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(400),
            false), BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(500), BigDecimal.valueOf(10)),
        new Barnetillegg(BigDecimal.valueOf(400), BigDecimal.valueOf(10))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(300),
            false), BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(100), BigDecimal.valueOf(10))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(450, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_BP, resultat.get(0).getResultatkode());

    assertEquals(290, resultat.get(1).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM, resultat.get(1).getResultatkode());

  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for ett barn. Sjablonverdier for barnetillegg skal da overstyre"
      + "alt i beregningen")
  @Test
  void testBeregning1BarnBarnetilleggForsvaret() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(1000), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(4667, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET, resultat.get(0).getResultatkode());

  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for tre barn. Sjablonverdier for barnetillegg skal da overstyre"
      + "alt i beregningen")
  @Test
  void testBeregning3BarnBarnetilleggForsvaret() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, true, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(2667, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET, resultat.get(0).getResultatkode());

  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for tre barn. Test på at samværsfradrag trekkes fra endelig bidragsbeløp")
  @Test
  void testBeregningBarnetilleggForsvaretFratrekkSamvaersfradrag() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(1000), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, true, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(2667, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(1667, resultat.get(1).getResultatBarnebidragBelop().doubleValue());
    assertEquals(2667, resultat.get(2).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET, resultat.get(0).getResultatkode());

  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for elleve barn. Sjekker avrunding")
  @Test
  void testBeregning11BarnBarnetilleggForsvaret() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(4,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(5,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(6,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(7,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(8,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(9,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(10,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(11,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, true, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);
    assertEquals(727, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET, resultat.get(0).getResultatkode());

  }

  @DisplayName("Beregner der delt bosted og barnetilleggBP er angitt, barnetillegget skal da ikke taes hensyn til")
  @Test
  void testBeregningDeltBostedOgBarnetilleggBP() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(1200));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(5000), BigDecimal.valueOf(10)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(500, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(500, resultat.get(1).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(0).getResultatkode());
    assertEquals(ResultatKode.DELT_BOSTED, resultat.get(1).getResultatkode());
  }

  @DisplayName("Beregner med to barn der det ene er selvforsørget, dvs har inntekt over 100 * sjablon for forhøyet forskudd."
      + "BPs andel skal da være 0, bidrag skal beregnes til 0 og resultatkode BARNET_ER_SELVFORSORGET skal angis")
  @Test
  void testBeregningSelvforsorgetBarn() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(1200));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(0), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(0), BigDecimal.valueOf(0),
            true), BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.valueOf(5000), BigDecimal.valueOf(10)),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    // Tester mot Doubleverdi her fordi svaret returneres med x.xxE+x
    assertEquals(1000, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(0, resultat.get(1).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(0).getResultatkode());
    assertEquals(ResultatKode.BARNET_ER_SELVFORSORGET, resultat.get(1).getResultatkode());

  }

  @DisplayName("Tester fra John")
  @Test
  void testerFraJohn() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(16536), BigDecimal.valueOf(12500));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(60),BigDecimal.valueOf(5210),
            false), BigDecimal.valueOf(457), false,
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
        new Barnetillegg(BigDecimal.valueOf(4000), BigDecimal.valueOf(10))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4750, resultat.get(0).getResultatBarnebidragBelop().doubleValue());
    assertEquals(ResultatKode.KOSTNADSBEREGNET_BIDRAG, resultat.get(0).getResultatkode());

  }
}