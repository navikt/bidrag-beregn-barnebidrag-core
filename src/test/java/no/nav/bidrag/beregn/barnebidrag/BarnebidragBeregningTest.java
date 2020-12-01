package no.nav.bidrag.beregn.barnebidrag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregningImpl;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg;
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av barnebidrag")
public class BarnebidragBeregningTest {

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();
  private final List<GrunnlagBeregningPerBarn> grunnlagBeregningPerBarnListe  = new ArrayList<>();

  @DisplayName("Beregner ved full evne, ett barn, ingen barnetillegg")
  @Test
  void testBeregningEttBarnMedFullEvneIngenBarnetillegg() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(8000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.KOSTNADSBEREGNET_BIDRAG)
    );
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
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    // 1700d-(1700d*10d/100)-100d

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(1430))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_BP)
    );
  }

  @DisplayName("Beregner ved full evne, to barn")
  @Test
  void testBeregning2BarnFullEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(20000), BigDecimal.valueOf(20000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(7000),
            false), BigDecimal.ZERO,false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(8000))).isZero(),
        () -> assertThat(resultat.get(1).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(7000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.KOSTNADSBEREGNET_BIDRAG)
    );
  }

  @DisplayName("Beregner for tre barn med for lav bidragsevne")
  @Test
  void testBeregning3BarnBegrensetAvBidragsevne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(8000), BigDecimal.valueOf(12000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(5000),
        false), BigDecimal.ZERO,false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(3000),
        false),
        BigDecimal.ZERO,false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(2000),
            false), BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(4000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(1).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(2400))).isZero(),
        () -> assertThat(resultat.get(1).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(2).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(1600))).isZero(),
        () -> assertThat(resultat.get(2).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE)
    );
  }

  @DisplayName("Beregner ved manglende evne, ett barn")
  @Test
  void testBeregning1BarnIkkeFullEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(2000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO,false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE)
    );
  }


  @DisplayName("Beregner ved manglende evne, to barn")
  @Test
  void testBeregning2BarnIkkeFullEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(20000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(7000),
            false), BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(5330))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(1).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(4670))).isZero(),
        () -> assertThat(resultat.get(1).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE)
    );
  }

  @DisplayName("Beregner for tre barn som begrenses av 25%-regel")
  @Test
  void testBeregning3BarnBegrensetAv25ProsentAvInntekt() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(12000), BigDecimal.valueOf(8000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(5000),
            false), BigDecimal.ZERO,false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(3000),
            false), BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(2000),
            false), BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(4000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT),
        () -> assertThat(resultat.get(1).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(2400))).isZero(),
        () -> assertThat(resultat.get(1).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT),
        () -> assertThat(resultat.get(2).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(1600))).isZero(),
        () -> assertThat(resultat.get(2).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT),

        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop()
            .add(resultat.get(1).getResultatBarnebidragBelop())
            .add(resultat.get(2).getResultatBarnebidragBelop())
            .compareTo(BigDecimal.valueOf(8000))).isZero()
    );
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
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.valueOf(1000), BigDecimal.valueOf(10))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(300))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM)
    );
  }

  @DisplayName("Beregner at bidrag settes likt barnetilleggBP der det også finnes barnetillegg BM."
      + "Barnetillegg for BP overstyrer barnetilleggBM")
  @Test
  void testBeregningBidragSettesLiktBarnetilleggBP() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(8000), BigDecimal.valueOf(12000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(200),
            false), BigDecimal.ZERO,false,
        new Barnetillegg(BigDecimal.valueOf(500), BigDecimal.valueOf(10)),
        new Barnetillegg(BigDecimal.valueOf(1000), BigDecimal.valueOf(10))));


    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(450))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_BP)
    );
  }

  @DisplayName("Beregner at bidrag settes likt BPs andel av underholdskostnad minus samværsfradrag")
  @Test
  void testBeregningFradragSamvaer() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(8000), BigDecimal.valueOf(12000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(2000),
            false), BigDecimal.valueOf(200), false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(1800))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.KOSTNADSBEREGNET_BIDRAG)
    );
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
            false), BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.valueOf(500), BigDecimal.valueOf(10)),
        new Barnetillegg(BigDecimal.valueOf(400), BigDecimal.valueOf(10))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(300),
            false), BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.valueOf(100), BigDecimal.valueOf(10))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(450))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_BP),
        () -> assertThat(resultat.get(1).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(290))).isZero(),
        () -> assertThat(resultat.get(1).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM)
    );
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
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(4667))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET)
    );
  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for tre barn. Sjablonverdier for barnetillegg skal da overstyre"
      + "alt i beregningen")
  @Test
  void testBeregning3BarnBarnetilleggForsvaret() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, true, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(2667))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET)
    );
  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for tre barn. Test på at samværsfradrag trekkes fra endelig bidragsbeløp")
  @Test
  void testBeregningBarnetilleggForsvaretFratrekkSamvaersfradrag() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.valueOf(1000), true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, true, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(2667))).isZero(),
        () -> assertThat(resultat.get(1).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(1667))).isZero(),
        () -> assertThat(resultat.get(2).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(2667))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET)
    );
  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for elleve barn. Sjekker avrunding")
  @Test
  void testBeregning11BarnBarnetilleggForsvaret() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(4,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(5,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(6,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(7,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(8,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(9,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(10,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(11,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false),BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, true, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(727))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET)
    );
  }

  @DisplayName("Beregner der delt bosted og barnetilleggBP er angitt, barnetillegget skal da ikke taes hensyn til")
  @Test
  void testBeregningDeltBostedOgBarnetilleggBP() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(1200));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(5000), BigDecimal.valueOf(10)),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(500))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(1).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(500))).isZero(),
        () -> assertThat(resultat.get(1).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE)
    );
  }

  @DisplayName("Beregner med to barn der det ene er selvforsørget, dvs har inntekt over 100 * sjablon for forhøyet forskudd."
      + "BPs andel skal da være 0, bidrag skal beregnes til 0 og resultatkode BARNET_ER_SELVFORSORGET skal angis")
  @Test
  void testBeregningSelvforsorgetBarn() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(1200));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, false,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(BigDecimal.ZERO, BigDecimal.ZERO,
            true), BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.valueOf(5000), BigDecimal.valueOf(10)),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(1).getResultatBarnebidragBelop().compareTo(BigDecimal.ZERO)).isZero(),
        () -> assertThat(resultat.get(1).getResultatkode()).isEqualTo(ResultatKode.BARNET_ER_SELVFORSORGET)
    );
  }

  @DisplayName("Resultatkode skal settes til BARNEBIDRAG_IKKE_BEREGNET_DELT_BOSTED ved delt bosted og "
      + "andel av U under 50%")
  @Test
  void testAtRiktigResultatkodeSettesVedDeltBostedOgAndelUnder50Prosent() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(12000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(0), BigDecimal.valueOf(0),
            false), BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BARNEBIDRAG_IKKE_BEREGNET_DELT_BOSTED)

    );
  }

  @DisplayName("Resultatkode skal settes til DELT_BOSTED ved delt bosted og andel av U over 50% der "
      + "ingen andre faktorer har redusert bidraget ")
  @Test
  void testAtRiktigResultatkodeSettesVedDeltBostedOgAndelOver50Prosent() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(12000));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.DELT_BOSTED)

    );
  }

  @DisplayName("Ved delt bosted og beregnet bidrag redusert av bidragsevne skal resultatkode reflektere"
      + "at det er lav evne")
  @Test
  void testAtRiktigResultatkodeSettesVedDeltBostedOgAndelOver50ProsentVedBegrensetEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(1200));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(80), BigDecimal.valueOf(8000),
            false), BigDecimal.ZERO, true,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(1000))).isZero()

    );
  }

/*  @DisplayName("Tester fra John")
  @Test
  void testerFraJohn() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(BigDecimal.valueOf(5603), BigDecimal.valueOf(8334));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(BigDecimal.valueOf(48.4),BigDecimal.valueOf(8684),
            false), BigDecimal.valueOf(0), true,
        new Barnetillegg(BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BigDecimal.valueOf(0), BigDecimal.valueOf(0))));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, false, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(0))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.DELT_BOSTED)
    );
  }*/
}
