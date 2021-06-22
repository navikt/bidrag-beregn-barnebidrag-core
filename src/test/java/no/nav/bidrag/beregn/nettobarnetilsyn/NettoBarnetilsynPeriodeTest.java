package no.nav.bidrag.beregn.nettobarnetilsyn;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.FAKTISK_UTGIFT_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnetNettoBarnetilsynResultat;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgiftPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NettoBarnetilsynPeriodeTest {

  private final NettoBarnetilsynPeriode nettoBarnetilsynPeriode = NettoBarnetilsynPeriode.getInstance();

  @Test
  @DisplayName("Test av periodisering for ett barn. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  void testPeriodiseringEttBarn() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-01-01")),
        LocalDate.parse("2008-03-17"), BigDecimal.valueOf(1000)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-08-01")),
        LocalDate.parse("2008-03-17"), BigDecimal.valueOf(2000)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2008-03-17"), BigDecimal.valueOf(5000)));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(3),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getFaktiskUtgiftListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(750))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getFaktiskUtgiftListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(2000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1499))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getFaktiskUtgiftListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(4478))).isZero()
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test av periodisering for to barn. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  void testPeriodiseringToBarn() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE, 
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-01-01")),
        LocalDate.parse("2008-03-17"), BigDecimal.valueOf(1000)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-08-01")),
        LocalDate.parse("2008-03-17"), BigDecimal.valueOf(2000)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(2, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-08-01")),
        LocalDate.parse("2012-02-18"), BigDecimal.valueOf(2000)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2008-03-17"), BigDecimal.valueOf(5000)));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(3),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getFaktiskUtgiftListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(750))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getFaktiskUtgiftListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(2000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1583))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getFaktiskUtgiftListe().get(1).getBelop()
            .compareTo(BigDecimal.valueOf(2000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(1).getBelop()
            .compareTo(BigDecimal.valueOf(1583))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(1).getSoknadsbarnPersonId())
            .isEqualTo(2),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getFaktiskUtgiftListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(4478))).isZero()
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test at netto barnetilsyn ikke beregnes hvis barnet er over 12 år")
  void testNullNettoBarnetilsynBarnOver12Aaar() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = singletonList(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-01-01")), LocalDate.parse("2005-03-17"), BigDecimal.valueOf(1000)));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.ZERO)).isZero()
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test at netto barnetilsyn ikke beregnes hvis faktisk utgift er 0.-")
  void testNullNettoBarnetilsynVed0IFaktiskUtgift() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = singletonList(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-01-01")), LocalDate.parse("2015-03-17"), BigDecimal.ZERO));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe()
            .isEmpty())
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test at faktiske utgifter med 0 i beløp tas med i beregning og resultat")
  void testAt0IFaktiskUtgiftIkkeFjernesFraGrunnlag() {
    var beregnDatoFra = LocalDate.parse("2019-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2015-03-17"), BigDecimal.valueOf(200)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(2, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2015-03-17"), BigDecimal.ZERO));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(3, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2015-03-17"), BigDecimal.valueOf(800)));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getFaktiskUtgiftListe().size())
            .isEqualTo(3)
    );

    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Test at det dannes nye perioder ved endring i faktisk utgiftbeløp")
  void testNyePerioderVedEndringFaktiskUtgift() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-02-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(4, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-11-01"), LocalDate.parse("2019-12-01")),
        LocalDate.parse("2015-03-17"), BigDecimal.valueOf(200)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(3, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2018-11-01"), LocalDate.parse("2018-12-01")),
        LocalDate.parse("2014-04-17"), BigDecimal.valueOf(800)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2019-04-01")),
        LocalDate.parse("2012-05-17"), BigDecimal.valueOf(800)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(2, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-09-01")),
        LocalDate.parse("2013-03-17"), BigDecimal.valueOf(200)));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-11-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(600))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getSoknadsbarnPersonId())
            .isEqualTo(3),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(600))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatListe().get(0).getSoknadsbarnPersonId())
            .isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(150))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatListe().get(0).getSoknadsbarnPersonId())
            .isEqualTo(2),

        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-11-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(150))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getResultatListe().get(0).getSoknadsbarnPersonId())
            .isEqualTo(4)
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test av periodisering for to barn med overlappende perioder")
  void testPeriodiseringToBarnOverlappendePerioder() {
    var beregnDatoFra = LocalDate.parse("2019-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2008-03-17"), BigDecimal.valueOf(1000)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(2, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2012-02-18"), BigDecimal.valueOf(2000)));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getSoknadsbarnPersonId())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(1).getSoknadsbarnPersonId())
            .isEqualTo(2)
    );

    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

    var beregnDatoFra = LocalDate.parse("2017-07-01");
    var beregnDatoTil = LocalDate.parse("2021-01-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-01-01")),
        LocalDate.parse("2008-03-17"), BigDecimal.valueOf(1000)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-08-01")),
        LocalDate.parse("2008-03-17"), BigDecimal.valueOf(2000)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2008-03-17"), BigDecimal.valueOf(5000)));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var avvikListe = nettoBarnetilsynPeriode.validerInput(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(2),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Første dato i faktiskUtgiftPeriodeListe (2018-07-01) er etter beregnDatoFra (2017-07-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i faktiskUtgiftPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
    );

    printAvvikListe(avvikListe);
  }

  @Test
  @DisplayName("Test at beregnet netto barnetilsyn for barn over 12 år settes til 0. Det dannes brudd i periode"
      + "01.01 det året bidragsbarnet fyller 13 år. I denne testen er 13års-dagen satt til 01.01.2019. For første "
      + "periode 2018-07-01 - 2019-01-01 beregnes da alder til 12 år. I neste periode 2019-01-01 - 2019-02-01"
      + "er alder beregnet til 13 år og netto barnetilsyn skal settes til 0.")
  void testAtBeregnetBelopSettesLikNullForBarnOver12Aar() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2019-02-01");

    var faktiskUtgiftPeriodeListe = singletonList(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-02-01")), LocalDate.parse("2006-02-18"), BigDecimal.valueOf(2000)));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(2),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getSoknadsbarnPersonId())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1499))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getSoknadsbarnPersonId())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.ZERO)).isZero()
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test at beregnet netto barnetilsyn for barn over 12 år settes til 0. Det dannes brudd i periode"
      + "01.01 det året bidragsbarnet fyller 13 år. Tester også at barn under 13 beregnes som normalt.")
  void testAtBeregnetBelopSettesLikNullForBarnOver12AarOgBeregningForAndreBarnFungerer() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2019-02-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-02-01")),
        LocalDate.parse("2006-02-18"), BigDecimal.valueOf(2000)));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(2, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-02-01")),
        LocalDate.parse("2016-02-18"), BigDecimal.valueOf(1000)));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe()).hasSize(2),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getSoknadsbarnPersonId())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1624))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getSoknadsbarnPersonId())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.ZERO)).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(1).getBelop()
            .compareTo(BigDecimal.valueOf(750))).isZero()
    );

    printGrunnlagResultat(resultat);
  }


  @DisplayName("Test eksempler fra John")
  @Test
  void testEksemplerFraJohn() {

    var beregnDatoFra = LocalDate.parse("2019-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = singletonList(new FaktiskUtgiftPeriode(1, FAKTISK_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")), LocalDate.parse("2010-03-17"), BigDecimal.valueOf(3000)));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe, lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(2478))).isZero()
    );

    printGrunnlagResultat(resultat);
  }


  private List<SjablonPeriode> lagSjablonGrunnlag() {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    // Maks fradrag
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "1")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(),
                BigDecimal.valueOf(2083.33))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "2")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(),
                BigDecimal.valueOf(3333))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "3")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(),
                BigDecimal.valueOf(4583))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "4")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(),
                BigDecimal.valueOf(5833))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "5")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(),
                BigDecimal.valueOf(7083))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "6")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(),
                BigDecimal.valueOf(8333))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "7")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(),
                BigDecimal.valueOf(9583))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "8")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(),
                BigDecimal.valueOf(10833))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "99")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(),
                BigDecimal.valueOf(12083))))));

    // Maks tilsyn
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_TILSYN.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "1")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(),
                BigDecimal.valueOf(6214))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_TILSYN.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "2")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(),
                BigDecimal.valueOf(8109))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_TILSYN.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "99")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(),
                BigDecimal.valueOf(9189))))));

    // Sjablontall
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(25.05))))));

    return sjablonPeriodeListe;
  }

  private void printGrunnlagResultat(BeregnetNettoBarnetilsynResultat beregnetNettoBarnetilsynResultat) {
    beregnetNettoBarnetilsynResultat.getResultatPeriodeListe().stream().sorted(
        Comparator.comparing(pR -> pR.getPeriode().getDatoFom()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getPeriode().getDatoFom() + "; " + "Dato til: "
                + sortedPR.getPeriode().getDatoTil()
                + "; " + "Beløp: " + sortedPR.getResultatListe()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
