package no.nav.bidrag.beregn.barnebidrag;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    var bidragsevne = new Bidragsevne(10000d, 10000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(8000d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.KOSTNADSBEREGNET_BIDRAG, resultat.get(0).getResultatkode());
  }

  @DisplayName("Beregner ved full evne, ett barn, og barnetillegg for BP der barnetillegg er høyere enn beregnet bidrag"
      + "Endelig bidrag skal da settes likt barnetillegg for BP")
  @Test
  void testBeregning1BarnFullEvneBarnetilleggBP() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(10000d, 10000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 1000d),
        0d, 0d, false,
        new Barnetillegg(1700d, 10d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(1700d-(1700d*10d/100), resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGGBP, resultat.get(0).getResultatkode());
  }

  @DisplayName("Beregner ved full evne, to barn")
  @Test
  void testBeregning2BarnFullEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(20000d, 20000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(80d, 7000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(8000d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(7000d, resultat.get(1).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.KOSTNADSBEREGNET_BIDRAG, resultat.get(0).getResultatkode());

  }

  @DisplayName("Beregner for tre barn med for lav bidragsevne")
  @Test
  void testBeregning3BarnBegrensetAvBidragsevne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(8000d, 12000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 5000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(80d, 3000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(80d, 2000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4000d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(0).getResultatkode());

    assertEquals(2400d, resultat.get(1).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(1).getResultatkode());

    assertEquals(1600d, resultat.get(2).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(2).getResultatkode());

  }

  @DisplayName("Beregner ved manglende evne, ett barn")
  @Test
  void testBeregning1BarnIkkeFullEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(1000d, 2000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(1000d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(0).getResultatkode());
  }


  @DisplayName("Beregner ved manglende evne, to barn")
  @Test
  void testBeregning2BarnIkkeFullEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(10000d, 20000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(80d, 7000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(5330d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(0).getResultatkode());

    assertEquals(4670d, resultat.get(1).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_AV_EVNE, resultat.get(1).getResultatkode());

  }

  @DisplayName("Beregner for tre barn som begrenses av 25%-regel")
  @Test
  void testBeregning3BarnBegrensetAv25ProsentAvInntekt() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(12000d, 8000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 5000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(80d, 3000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(80d, 2000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4000d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT, resultat.get(0).getResultatkode());

    assertEquals(2400d, resultat.get(1).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT, resultat.get(1).getResultatkode());

    assertEquals(1600d, resultat.get(2).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT, resultat.get(2).getResultatkode());

    assertEquals(8000d, (resultat.get(0).getResultatBarnebidragBelop() +
    resultat.get(1).getResultatBarnebidragBelop() +
    resultat.get(2).getResultatBarnebidragBelop()));
  }

  @DisplayName("Beregner at bidrag settes likt barnetilleggBM. Dette skjer når beregnet bidrag er lavere"
      + "enn BPs andel av underholdskostnad minus netto barnetillegg for BM")
  @Test
  void testBeregningBidragSettesLiktBarnetilleggBM() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(8000d, 12000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 1000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(1000d, 10d),
        false));


    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(100d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGGBM, resultat.get(0).getResultatkode());


  }

  @DisplayName("Beregner at bidrag settes likt barnetilleggBP der det også finnes barnetillegg BM."
      + "Barnetillegg for BP overstyrer barnetilleggBM")
  @Test
  void testBeregningBidragSettesLiktBarnetilleggBP() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(8000d, 12000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 200d),
        0d, 0d, false,
        new Barnetillegg(500d, 10d),
        new Barnetillegg(1000d, 10d),
        false));


    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(450d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGGBP, resultat.get(0).getResultatkode());

  }
  @DisplayName("Beregner at bidrag settes likt BPs andel av underholdskostnad minus samværsfradrag")
  @Test
  void testBeregningFradragSamvaer() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(8000d, 12000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 2000d),
        0d, 200d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(1800d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.KOSTNADSBEREGNET_BIDRAG, resultat.get(0).getResultatkode());
  }

  @DisplayName("Beregner for tre barn der barna har barnetillegg BP eller BM")
  @Test
  void testBeregning3BarnBarnetilleggBPogBM() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(12000d, 800d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 500d),
        0d, 0d, false,
        new Barnetillegg(500d, 10d),
        new Barnetillegg(400d, 10d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(80d, 300d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(100d, 10d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(80d, 200d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(450d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGGBP, resultat.get(0).getResultatkode());

    assertEquals(210d, resultat.get(1).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGGBM, resultat.get(1).getResultatkode());

    assertEquals(160d, resultat.get(2).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT, resultat.get(2).getResultatkode());
  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for ett barn. Sjablonverdier for barnetillegg skal da overstyre"
      + "alt i beregningen")
  @Test
  void testBeregning1BarnBarnetilleggForsvaret() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(10000d, 10000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);
    assertEquals(5667d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGGFORSVARET, resultat.get(0).getResultatkode());

  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for tre barn. Sjablonverdier for barnetillegg skal da overstyre"
      + "alt i beregningen")
  @Test
  void testBeregning3BarnBarnetilleggForsvaret() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(10000d, 10000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);
    assertEquals(2667d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGGFORSVARET, resultat.get(0).getResultatkode());

  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for elleve barn. Sjekker avrunding")
  @Test
  void testBeregning11BarnBarnetilleggForsvaret() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(10000d, 10000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(4,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(5,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(6,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(7,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(8,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(9,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));


    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(10,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(11,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 1000d, true,
        new Barnetillegg(10000d, 0d),
        new Barnetillegg(0d, 0d),
        true));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);
    assertEquals(727d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGGFORSVARET, resultat.get(0).getResultatkode());

  }



/*  @DisplayName("Beregner der delt bosted er satt. BPs andel av underholdskostnad skal da reduseres med 50%")
  @Test
  void testBeregningDeltBosted() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(10000d, 10000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 0d, true,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(3000d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(ResultatKode.DELT_BOSTED, resultat.get(0).getResultatkode());

  }*/

}

