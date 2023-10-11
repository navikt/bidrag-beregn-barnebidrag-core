package no.nav.bidrag.beregn.forholdsmessigfordeling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.nav.bidrag.beregn.forholdsmessigfordeling.beregning.ForholdsmessigFordelingBeregningImpl;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSak;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.Bidragsevne;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagPerBarn;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning;
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeBarnebidrag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av forholdsmessig fordeling")
public class ForholdsmessigFordelingBeregningTest {

  private final List<BeregnetBidragSak> beregnetBidragSakListe  = new ArrayList<>();
//  private final List<GrunnlagPerBarn> grunnlagPerBarnListe  = new ArrayList<>();

  @DisplayName("Basic test")
  @Test
  void testEnSakEttBarn() {
    ForholdsmessigFordelingBeregningImpl forholdsmessigFordelingBeregning = new ForholdsmessigFordelingBeregningImpl();

    List<GrunnlagPerBarn> grunnlagPerBarnListe  = new ArrayList<>();
    grunnlagPerBarnListe.add(new GrunnlagPerBarn(1, BigDecimal.valueOf(1000)));
    beregnetBidragSakListe.add(new BeregnetBidragSak(1234567, grunnlagPerBarnListe));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
        new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(1000)),
        beregnetBidragSakListe);

    List<ResultatBeregning> resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getSaksnr()).isEqualTo(1234567),
        () -> assertThat(resultat.size()).isEqualTo(1),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_INGEN_ENDRING)
    );
  }

  @DisplayName("test med én sak og tre barn")
  @Test
  void testEnSakTreBarn() {
    ForholdsmessigFordelingBeregningImpl forholdsmessigFordelingBeregning = new ForholdsmessigFordelingBeregningImpl();

    List<GrunnlagPerBarn> grunnlagPerBarnListe  = new ArrayList<>();

    grunnlagPerBarnListe.add(new GrunnlagPerBarn(1, BigDecimal.valueOf(1000)));
    grunnlagPerBarnListe.add(new GrunnlagPerBarn(2, BigDecimal.valueOf(2000)));
    grunnlagPerBarnListe.add(new GrunnlagPerBarn(3, BigDecimal.valueOf(3000)));
    beregnetBidragSakListe.add(new BeregnetBidragSak(1234567, grunnlagPerBarnListe));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
        new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
        beregnetBidragSakListe);

    List<ResultatBeregning> resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getSaksnr()).isEqualTo(1234567),
        () -> assertThat(resultat.size()).isEqualTo(1),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().size()).isEqualTo(3),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(1).getBelop().compareTo(BigDecimal.valueOf(2000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(2).getBelop().compareTo(BigDecimal.valueOf(3000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_INGEN_ENDRING)
    );
  }

  @DisplayName("tester med to saker, tre barn og bidragsevnen dekker den totale bdragssummen")
  @Test
  void testToSakerTreBarnFullEvne() {
    ForholdsmessigFordelingBeregningImpl forholdsmessigFordelingBeregning = new ForholdsmessigFordelingBeregningImpl();

    beregnetBidragSakListe.add(new BeregnetBidragSak(1234567,
        Arrays.asList(new GrunnlagPerBarn(1, BigDecimal.valueOf(1000)),
                      new GrunnlagPerBarn(2, BigDecimal.valueOf(2000)))));

    beregnetBidragSakListe.add(new BeregnetBidragSak(7654321,
        Arrays.asList(new GrunnlagPerBarn(3, BigDecimal.valueOf(3000)))));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
        new Bidragsevne(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
        beregnetBidragSakListe);

    List<ResultatBeregning> resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getSaksnr()).isEqualTo(1234567),
        () -> assertThat(resultat.get(1).getSaksnr()).isEqualTo(7654321),
        () -> assertThat(resultat.size()).isEqualTo(2),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().size()).isEqualTo(2),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().size()).isEqualTo(1),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(1).getBelop().compareTo(BigDecimal.valueOf(2000))).isZero(),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(3000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_INGEN_ENDRING)
    );
  }

  @DisplayName("tester med to saker, tre barn og bidragsevnen ikke dekker den totale bidragssummen")
  @Test
  void testToSakerTreBarnBegrensetEvne() {
    ForholdsmessigFordelingBeregningImpl forholdsmessigFordelingBeregning = new ForholdsmessigFordelingBeregningImpl();

    beregnetBidragSakListe.add(new BeregnetBidragSak(1234567,
        Arrays.asList(new GrunnlagPerBarn(1, BigDecimal.valueOf(1000)),
                      new GrunnlagPerBarn(2, BigDecimal.valueOf(2000)))));

    beregnetBidragSakListe.add(new BeregnetBidragSak(7654321,
        Arrays.asList(new GrunnlagPerBarn(3, BigDecimal.valueOf(6000)))));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
        new Bidragsevne(BigDecimal.valueOf(6000), BigDecimal.valueOf(10000)),
        beregnetBidragSakListe);

    List<ResultatBeregning> resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getSaksnr()).isEqualTo(1234567),
        () -> assertThat(resultat.get(1).getSaksnr()).isEqualTo(7654321),
        () -> assertThat(resultat.size()).isEqualTo(2),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().size()).isEqualTo(2),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().size()).isEqualTo(1),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getBarnPersonId()).isEqualTo(1),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(1).getBarnPersonId()).isEqualTo(2),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(0).getBarnPersonId()).isEqualTo(3),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(670))).isZero(),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(1).getBelop().compareTo(BigDecimal.valueOf(1330))).isZero(),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(4000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(1).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET)
    );
  }

  @DisplayName("Test 1 fra John med to saker med ett barn hver")
  @Test
  void test1FraJohn() {
    ForholdsmessigFordelingBeregningImpl forholdsmessigFordelingBeregning = new ForholdsmessigFordelingBeregningImpl();

    beregnetBidragSakListe.add(new BeregnetBidragSak(1,
        Arrays.asList(new GrunnlagPerBarn(1, BigDecimal.valueOf(3425)))));

    beregnetBidragSakListe.add(new BeregnetBidragSak(2,
        Arrays.asList(new GrunnlagPerBarn(2, BigDecimal.valueOf(5980)))));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
        new Bidragsevne(BigDecimal.valueOf(5655), BigDecimal.valueOf(10000)),
        beregnetBidragSakListe);

    List<ResultatBeregning> resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getSaksnr()).isEqualTo(1),
        () -> assertThat(resultat.get(1).getSaksnr()).isEqualTo(2),
        () -> assertThat(resultat.size()).isEqualTo(2),

        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(2060))).isZero(),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(3600))).isZero(),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET)
    );
  }

  @DisplayName("Test 2 fra John med tre saker med ett barn hver")
  @Test
  void test2FraJohn() {
    ForholdsmessigFordelingBeregningImpl forholdsmessigFordelingBeregning = new ForholdsmessigFordelingBeregningImpl();

    beregnetBidragSakListe.add(new BeregnetBidragSak(1,
        Arrays.asList(new GrunnlagPerBarn(1, BigDecimal.valueOf(3425)))));

    beregnetBidragSakListe.add(new BeregnetBidragSak(2,
        Arrays.asList(new GrunnlagPerBarn(2, BigDecimal.valueOf(5980)))));

    beregnetBidragSakListe.add(new BeregnetBidragSak(3,
        Arrays.asList(new GrunnlagPerBarn(3, BigDecimal.valueOf(3856)))));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
        new Bidragsevne(BigDecimal.valueOf(5655), BigDecimal.valueOf(10000)),
        beregnetBidragSakListe);

    List<ResultatBeregning> resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getSaksnr()).isEqualTo(1),
        () -> assertThat(resultat.get(1).getSaksnr()).isEqualTo(2),
        () -> assertThat(resultat.get(2).getSaksnr()).isEqualTo(3),
        () -> assertThat(resultat.size()).isEqualTo(3),

        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(1460))).isZero(),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(2550))).isZero(),
        () -> assertThat(resultat.get(2).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(1640))).isZero(),
        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET)
    );
  }

  @DisplayName("Test 4 fra John med tre saker med totalt seks barn")
  @Test
  void test4FraJohn() {
    ForholdsmessigFordelingBeregningImpl forholdsmessigFordelingBeregning = new ForholdsmessigFordelingBeregningImpl();

    beregnetBidragSakListe.add(new BeregnetBidragSak(1,
        Arrays.asList(
            new GrunnlagPerBarn(1, BigDecimal.valueOf(3749)))));

    beregnetBidragSakListe.add(new BeregnetBidragSak(2,
        Arrays.asList(
            new GrunnlagPerBarn(1, BigDecimal.valueOf(5115)),
            new GrunnlagPerBarn(2, BigDecimal.valueOf(4557)))));

    beregnetBidragSakListe.add(new BeregnetBidragSak(3,
        Arrays.asList(
            new GrunnlagPerBarn(1, BigDecimal.valueOf(4134)),
            new GrunnlagPerBarn(2, BigDecimal.valueOf(3561)),
            new GrunnlagPerBarn(3, BigDecimal.valueOf(2856)))));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
        new Bidragsevne(BigDecimal.valueOf(10417), BigDecimal.valueOf(20000)),
        beregnetBidragSakListe);

    List<ResultatBeregning> resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getSaksnr()).isEqualTo(1),
        () -> assertThat(resultat.get(1).getSaksnr()).isEqualTo(2),
        () -> assertThat(resultat.get(2).getSaksnr()).isEqualTo(3),
        () -> assertThat(resultat.size()).isEqualTo(3),

        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(1630))).isZero(),

        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(2220))).isZero(),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(1).getBelop().compareTo(BigDecimal.valueOf(1980))).isZero(),

        () -> assertThat(resultat.get(2).getResultatPerBarnListe().get(0).getBelop().compareTo(BigDecimal.valueOf(1800))).isZero(),
        () -> assertThat(resultat.get(2).getResultatPerBarnListe().get(1).getBelop().compareTo(BigDecimal.valueOf(1550))).isZero(),
        () -> assertThat(resultat.get(2).getResultatPerBarnListe().get(2).getBelop().compareTo(BigDecimal.valueOf(1240))).isZero(),

        () -> assertThat(resultat.get(0).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),
        () -> assertThat(resultat.get(1).getResultatPerBarnListe().get(1).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),
        () -> assertThat(resultat.get(2).getResultatPerBarnListe().get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),
        () -> assertThat(resultat.get(2).getResultatPerBarnListe().get(1).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),
        () -> assertThat(resultat.get(2).getResultatPerBarnListe().get(2).getKode()).isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET)
    );
  }
}