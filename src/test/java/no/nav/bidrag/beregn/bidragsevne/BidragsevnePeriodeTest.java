package no.nav.bidrag.beregn.bidragsevne;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static no.nav.bidrag.beregn.TestUtil.BARN_I_HUSSTAND_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.BOSTATUS_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SAERFRADRAG_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SKATTEKLASSE_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstandPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnetBidragsevneResultat;
import no.nav.bidrag.beregn.bidragsevne.bo.BostatusPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.InntektPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.SaerfradragPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.SkatteklassePeriode;
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.domain.enums.AvvikType;
import no.nav.bidrag.domain.enums.BostatusKode;
import no.nav.bidrag.domain.enums.Formaal;
import no.nav.bidrag.domain.enums.Rolle;
import no.nav.bidrag.domain.enums.SaerfradragKode;
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn;
import no.nav.bidrag.domain.enums.sjablon.SjablonNavn;
import no.nav.bidrag.domain.enums.sjablon.SjablonNokkelNavn;
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BidragsevneperiodeTest")
class BidragsevnePeriodeTest {

  private BeregnBidragsevneGrunnlag grunnlag;

  private final BidragsevnePeriode bidragsevnePeriode = BidragsevnePeriode.getInstance();

  @Test
  @DisplayName("Test med OK grunnlag")
  void testMedOKGrunnlag() {

    lagGrunnlag();

    var resultat = bidragsevnePeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(3749)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(444000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(15604)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getBostatus().getKode())
            .isEqualTo(BostatusKode.ALENE),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(20536)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-05-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(20536)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(666001)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-05-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(20536)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(666001)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(2)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoTil())
            .isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(20063)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(666001)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(2)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektListe().get(2).getBelop())
            .isEqualTo(BigDecimal.valueOf(3))
    );

    printGrunnlagResultat(resultat);

  }

  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

    lagGrunnlagMedAvvik();
    var avvikListe = bidragsevnePeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(7),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Første dato i inntektPeriodeListe (2003-01-01) er etter beregnDatoFra (2001-07-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i inntektPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(2).getAvvikTekst())
            .isEqualTo("Første dato i skatteklassePeriodeListe (2003-01-01) er etter beregnDatoFra (2001-07-01)"),
        () -> assertThat(avvikListe.get(2).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(3).getAvvikTekst())
            .isEqualTo("Siste dato i bostatusPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(3).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(4).getAvvikTekst())
            .isEqualTo("Siste dato i barnIHusstandPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(4).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(5).getAvvikTekst())
            .isEqualTo("Siste dato i saerfradragPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(5).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(6).getAvvikTekst())
            .isEqualTo("inntektType KONTANTSTOTTE er ugyldig for formaal BIDRAG og rolle BIDRAGSPLIKTIG"),
        () -> assertThat(avvikListe.get(6).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE)
    );

    printAvvikListe(avvikListe);
  }

  @Test
  @DisplayName("Test med ugyldig inntekt som skal resultere i avvik")
  void testGrunnlagMedAvvikUgyldigInntekt() {

    lagGrunnlagMedAvvikUgyldigInntekt();
    var avvikListe = bidragsevnePeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(1),

        () -> assertThat(avvikListe.get(0).getAvvikTekst()).isEqualTo("inntektType " + "SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG" +
            " er ugyldig for formaal " + Formaal.BIDRAG + " og rolle " + Rolle.BIDRAGSPLIKTIG),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE)
    );
    printAvvikListe(avvikListe);
  }

  @Test
  @DisplayName("Test beregn perioder med justering av inntekter")
  void testBeregnPerioderGrunnlagMedJusteringAvInntekter() {

    lagGrunnlagMedInntektTilJustering();
    var resultat = bidragsevnePeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektListe().get(0).getType())
            .isEqualTo("INNTEKTSOPPLYSNINGER_ARBEIDSGIVER"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(200000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektListe().get(0).getType())
            .isEqualTo("INNTEKTSOPPLYSNINGER_ARBEIDSGIVER"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektListe().get(0).getType())
            .isEqualTo("INNTEKTSOPPLYSNINGER_ARBEIDSGIVER"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektListe().get(0).getType())
            .isEqualTo("SAKSBEHANDLER_BEREGNET_INNTEKT"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektListe().get(1).getType())
            .isEqualTo("KAPITALINNTEKT_EGNE_OPPLYSNINGER"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektListe().get(0).getType())
            .isEqualTo("SAKSBEHANDLER_BEREGNET_INNTEKT"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektListe().get(1).getType())
            .isEqualTo("KAPITALINNTEKT_EGNE_OPPLYSNINGER"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektListe().get(0).getType())
            .isEqualTo("KAPITALINNTEKT_EGNE_OPPLYSNINGER"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektListe().get(1).getType())
            .isEqualTo("ATTFORING_AAP"),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(250000))
    );
  }


  private void lagGrunnlag() {

    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    grunnlag = new BeregnBidragsevneGrunnlag(beregnDatoFra, beregnDatoTil, lagInntektGrunnlag(), lagSkatteklasseGrunnlag(), lagBostatusGrunnlag(),
        lagBarnIHusstandGrunnlag(), lagSaerfradragGrunnlag(), lagSjablonGrunnlag());
  }

  private void lagGrunnlagMedAvvik() {

    var beregnDatoFra = LocalDate.parse("2001-07-01");
    var beregnDatoTil = LocalDate.parse("2021-01-01");

    grunnlag = new BeregnBidragsevneGrunnlag(beregnDatoFra, beregnDatoTil, lagInntektGrunnlag(), lagSkatteklasseGrunnlag(), lagBostatusGrunnlag(),
        lagBarnIHusstandGrunnlag(), lagSaerfradragGrunnlag(), lagSjablonGrunnlag());
  }

  private void lagGrunnlagMedAvvikUgyldigInntekt() {

    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    grunnlag = new BeregnBidragsevneGrunnlag(beregnDatoFra, beregnDatoTil, lagUgyldigInntektGrunnlag(), lagSkatteklasseGrunnlag(),
        lagBostatusGrunnlag(), lagBarnIHusstandGrunnlag(), lagSaerfradragGrunnlag(), lagSjablonGrunnlag());
  }

  private void lagGrunnlagMedInntektTilJustering() {

    var beregnDatoFra = LocalDate.parse("2018-01-01");
    var beregnDatoTil = LocalDate.parse("2020-07-01");

    var skatteklassePeriodeListe = singletonList(
        new SkatteklassePeriode(SKATTEKLASSE_REFERANSE, new Periode(beregnDatoFra, beregnDatoTil), 2));
    var bostatusPeriodeListe = singletonList(
        new BostatusPeriode(BOSTATUS_REFERANSE, new Periode(beregnDatoFra, beregnDatoTil), BostatusKode.MED_ANDRE));
    var antallBarnIEgetHusholdPeriodeListe = singletonList(
        new BarnIHusstandPeriode(BARN_I_HUSSTAND_REFERANSE, new Periode(beregnDatoFra, beregnDatoTil), 1));
    var saerfradragPeriodeListe = singletonList(
        new SaerfradragPeriode(SAERFRADRAG_REFERANSE, new Periode(beregnDatoFra, beregnDatoTil), SaerfradragKode.HELT));

    grunnlag = new BeregnBidragsevneGrunnlag(beregnDatoFra, beregnDatoTil, lagJustertInntektGrunnlag(), skatteklassePeriodeListe,
        bostatusPeriodeListe, antallBarnIEgetHusholdPeriodeListe, saerfradragPeriodeListe, lagSjablonGrunnlag());
  }

  private List<InntektPeriode> lagInntektGrunnlag() {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();

    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_1", new Periode(LocalDate.parse("2003-01-01"), LocalDate.parse("2004-01-01")),
        "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER".toString(), BigDecimal.valueOf(666000)));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_2", new Periode(LocalDate.parse("2004-01-01"), LocalDate.parse("2016-01-01")),
        "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER", BigDecimal.valueOf(555000)));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_3", new Periode(LocalDate.parse("2016-01-01"), LocalDate.parse("2019-01-01")),
        "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER", BigDecimal.valueOf(444000)));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_4", new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-04-01")),
        "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER", BigDecimal.valueOf(666000)));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_5", new Periode(LocalDate.parse("2019-04-01"), LocalDate.parse("2020-01-01")),
        "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER", BigDecimal.valueOf(666001)));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_6", new Periode(LocalDate.parse("2019-05-01"), LocalDate.parse("2020-01-01")),
        "OVERGANGSSTONAD", BigDecimal.valueOf(2)));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_7", new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")),
        "KONTANTSTOTTE", BigDecimal.valueOf(3)));

    return inntektPeriodeListe;
  }

  private List<InntektPeriode> lagUgyldigInntektGrunnlag() {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();

    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE, new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-01-01")),
        "SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG", BigDecimal.valueOf(666001)));

    return inntektPeriodeListe;
  }

  private List<InntektPeriode> lagJustertInntektGrunnlag() {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();

    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_1", new Periode(LocalDate.parse("2018-01-01"), null),
        "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER", BigDecimal.valueOf(200000)));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_1", new Periode(LocalDate.parse("2018-06-01"), LocalDate.parse("2018-12-31")),
        "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER", BigDecimal.valueOf(150000)));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_1", new Periode(LocalDate.parse("2019-01-01"), null),
        "SAKSBEHANDLER_BEREGNET_INNTEKT", BigDecimal.valueOf(300000)));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_1", new Periode(LocalDate.parse("2019-01-01"), null),
        "KAPITALINNTEKT_EGNE_OPPLYSNINGER", BigDecimal.valueOf(100000)));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_1", new Periode(LocalDate.parse("2020-01-01"), null),
        "ATTFORING_AAP", BigDecimal.valueOf(250000)));

    return inntektPeriodeListe;
  }

  private List<SkatteklassePeriode> lagSkatteklasseGrunnlag() {
    var skatteklassePeriodeListe = new ArrayList<SkatteklassePeriode>();

    skatteklassePeriodeListe.add(new SkatteklassePeriode(SKATTEKLASSE_REFERANSE + "_1",
        new Periode(LocalDate.parse("2003-01-01"), LocalDate.parse("2004-01-01")), 2));
    skatteklassePeriodeListe.add(new SkatteklassePeriode(SKATTEKLASSE_REFERANSE + "_2",
        new Periode(LocalDate.parse("2004-01-01"), LocalDate.parse("2016-01-01")), 2));
    skatteklassePeriodeListe.add(new SkatteklassePeriode(SKATTEKLASSE_REFERANSE + "_3",
        new Periode(LocalDate.parse("2016-01-01"), LocalDate.parse("2019-01-01")), 1));
    skatteklassePeriodeListe.add(new SkatteklassePeriode(SKATTEKLASSE_REFERANSE + "_4",
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-04-01")), 1));
    skatteklassePeriodeListe.add(new SkatteklassePeriode(SKATTEKLASSE_REFERANSE + "_5",
        new Periode(LocalDate.parse("2019-04-01"), LocalDate.parse("2020-01-01")), 1));
    skatteklassePeriodeListe.add(new SkatteklassePeriode(SKATTEKLASSE_REFERANSE + "_6",
        new Periode(LocalDate.parse("2020-01-01"), null), 1));

    return skatteklassePeriodeListe;

  }


  private List<BostatusPeriode> lagBostatusGrunnlag() {

    var bostatusPeriodeListe = new ArrayList<BostatusPeriode>();

    bostatusPeriodeListe.add(new BostatusPeriode(BOSTATUS_REFERANSE + "_1", new Periode(LocalDate.parse("2001-01-01"), LocalDate.parse("2017-01-01")),
        BostatusKode.MED_ANDRE));
    bostatusPeriodeListe.add(new BostatusPeriode(BOSTATUS_REFERANSE + "_2", new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-02-01")),
        BostatusKode.ALENE));
    bostatusPeriodeListe.add(new BostatusPeriode(BOSTATUS_REFERANSE + "_3", new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2020-01-01")),
        BostatusKode.MED_ANDRE));

    return bostatusPeriodeListe;
  }

  private List<BarnIHusstandPeriode> lagBarnIHusstandGrunnlag() {

    var barnIHusstandPeriodeListe = new ArrayList<BarnIHusstandPeriode>();

    barnIHusstandPeriodeListe.add(new BarnIHusstandPeriode(BARN_I_HUSSTAND_REFERANSE + "_1",
        new Periode(LocalDate.parse("2001-01-01"), LocalDate.parse("2017-01-01")), 1));
    barnIHusstandPeriodeListe.add(new BarnIHusstandPeriode(BARN_I_HUSSTAND_REFERANSE + "_2",
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), 2));

    return barnIHusstandPeriodeListe;
  }

  private List<SaerfradragPeriode> lagSaerfradragGrunnlag() {

    var saerfradragPeriodeListe = new ArrayList<SaerfradragPeriode>();

    saerfradragPeriodeListe.add(new SaerfradragPeriode(SAERFRADRAG_REFERANSE + "_1",
        new Periode(LocalDate.parse("2001-01-01"), LocalDate.parse("2017-01-01")), SaerfradragKode.HELT));
    saerfradragPeriodeListe.add(new SaerfradragPeriode(SAERFRADRAG_REFERANSE + "_2",
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), SaerfradragKode.HELT));

    return saerfradragPeriodeListe;

  }

  private List<SjablonPeriode> lagSjablonGrunnlag() {

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2003-01-01"), LocalDate.parse("2003-12-31")),
        new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(8848))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2013-01-01"), null),
        new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.ZERO)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2003-01-01"), LocalDate.parse("2013-12-31")),
        new Sjablon(SjablonTallNavn.TRYGDEAVGIFT_PROSENT.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(7.8))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2014-01-01"), null),
        new Sjablon(SjablonTallNavn.TRYGDEAVGIFT_PROSENT.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(8.2))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(3417))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), null),
        new Sjablon(SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(3487))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2005-01-01"), LocalDate.parse("2005-05-31")),
        new Sjablon(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(57400))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-07-01"), LocalDate.parse("2017-12-31")),
        new Sjablon(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(75000))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-06-30")),
        new Sjablon(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(75000))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(83000))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), null),
        new Sjablon(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(85050))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(31))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(54750))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), null),
        new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(56550))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(54750))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), null),
        new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(56550))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
        new Sjablon(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(13132))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), null),
        new Sjablon(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(12977))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
        new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(23))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), null),
        new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(22))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(169000)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(1.4))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(237900)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(3.3))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(598050)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(12.4))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(962050)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(15.4))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(174500)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(1.9))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(245650)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(4.2))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(617500)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(13.2))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(964800)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(16.2))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2020-01-01"), null),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(180800)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(1.9))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2020-01-01"), null),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(254500)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(4.2))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2020-01-01"), null),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(639750)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(13.2))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2020-01-01"), null),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(),
                    BigDecimal.valueOf(999550)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(),
                    BigDecimal.valueOf(16.2))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonNavn.BIDRAGSEVNE.getNavn(), Collections.singletonList(new SjablonNokkel(
            SjablonNokkelNavn.BOSTATUS.getNavn(), "EN")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn(),
                    BigDecimal.valueOf(9303)),
                new SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn(),
                    BigDecimal.valueOf(8657))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonNavn.BIDRAGSEVNE.getNavn(), Collections.singletonList(new SjablonNokkel(
            SjablonNokkelNavn.BOSTATUS.getNavn(), "GS")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn(),
                    BigDecimal.valueOf(5698)),
                new SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn(),
                    BigDecimal.valueOf(7330))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), null),
        new Sjablon(SjablonNavn.BIDRAGSEVNE.getNavn(), Collections.singletonList(new SjablonNokkel(
            SjablonNokkelNavn.BOSTATUS.getNavn(), "EN")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn(),
                    BigDecimal.valueOf(9591)),
                new SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn(),
                    BigDecimal.valueOf(8925))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), null),
        new Sjablon(SjablonNavn.BIDRAGSEVNE.getNavn(), Collections.singletonList(new SjablonNokkel(
            SjablonNokkelNavn.BOSTATUS.getNavn(), "GS")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn(),
                    BigDecimal.valueOf(5875)),
                new SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn(),
                    BigDecimal.valueOf(7557))))));

    return sjablonPeriodeListe;
  }


  private void printGrunnlagResultat(BeregnetBidragsevneResultat beregnetBidragsevneResultat) {
    beregnetBidragsevneResultat.getResultatPeriodeListe().stream()
        .sorted(comparing(pR -> pR.getPeriode().getDatoFom()))
        .forEach(sortedPR -> System.out.println("Dato fra: " + sortedPR.getPeriode().getDatoFom() + "; " +
            "Dato til: " + sortedPR.getPeriode().getDatoTil() + "; " + "Beløp: " + sortedPR.getResultat().getBelop()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
