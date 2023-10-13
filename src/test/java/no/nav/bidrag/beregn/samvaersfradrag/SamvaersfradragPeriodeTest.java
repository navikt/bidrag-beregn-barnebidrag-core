package no.nav.bidrag.beregn.samvaersfradrag;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static no.nav.bidrag.beregn.TestUtil.SAMVAERSKLASSE_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SOKNADSBARN_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnetSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersklassePeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.Soknadsbarn;
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode;
import no.nav.bidrag.domain.enums.AvvikType;
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn;
import no.nav.bidrag.domain.enums.sjablon.SjablonNavn;
import no.nav.bidrag.domain.enums.sjablon.SjablonNokkelNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av periodisert beregning av samværsfradrag")
public class SamvaersfradragPeriodeTest {

  private final SamvaersfradragPeriode samvaersfradragPeriode = SamvaersfradragPeriode.Companion.getInstance();

  @Test
  @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene, "
      + "det skal lages brudd på søknadsbarnets fødselsmåned")
  void testToPerioder() {

    var beregnDatoFra = LocalDate.parse("2019-07-01");
    var beregnDatoTil = LocalDate.parse("2020-07-01");
    var soknadsbarnFodselsdato = LocalDate.parse("2014-03-17");

    var soknadsbarn = new Soknadsbarn(SOKNADSBARN_REFERANSE, 1, soknadsbarnFodselsdato);

    // Lag samværsinfo
    var samvaersklassePeriodeListe = singletonList(new SamvaersklassePeriode(SAMVAERSKLASSE_REFERANSE,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-07-01")), "02"));

    // Lag sjabloner
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(
                new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(727))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), null),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(
                new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Arrays.asList(
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1052))))));

    var grunnlag = new BeregnSamvaersfradragGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarn, samvaersklassePeriodeListe, sjablonPeriodeListe);

    var resultat = samvaersfradragPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(2),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2020-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getBelop().compareTo(BigDecimal.valueOf(727))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2020-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultat().getBelop().compareTo(BigDecimal.valueOf(1052))).isZero()
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test at det opprettes ny periode ved flere perioder med samværsklasse i input, også ny periode ved barnets bursdag."
      + "Tester også at riktig verdi fpr samværsfradrag brukes når barnets alder passerer en av grensene for alder")
  void testFlereSamvaersklasser() {

    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-07-01");
    var soknadsbarnFodselsdato = LocalDate.parse("2014-02-17");

    var soknadsbarn = new Soknadsbarn(SOKNADSBARN_REFERANSE, 1, soknadsbarnFodselsdato);

    // Lag samværsinfo
    var samvaersklassePeriodeListe = new ArrayList<SamvaersklassePeriode>();

    samvaersklassePeriodeListe.add(new SamvaersklassePeriode(SAMVAERSKLASSE_REFERANSE + "_1",
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-02-01")), "01"));

    samvaersklassePeriodeListe.add(new SamvaersklassePeriode(SAMVAERSKLASSE_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2020-07-01")), "02"));

    // Lag sjabloner
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(
                new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(219))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(
                new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Arrays.asList(
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(318))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(
                new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(727))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), null),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1052))))));

    var grunnlag = new BeregnSamvaersfradragGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarn, samvaersklassePeriodeListe, sjablonPeriodeListe);

    var resultat = samvaersfradragPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getBelop().compareTo(BigDecimal.valueOf(219))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultat().getBelop().compareTo(BigDecimal.valueOf(727))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2020-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultat().getBelop().compareTo(BigDecimal.valueOf(727))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2020-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultat().getBelop().compareTo(BigDecimal.valueOf(1052))).isZero()
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2021-01-01");
    var soknadsbarnFodselsdato = LocalDate.parse("2014-03-17");

    var soknadsbarn = new Soknadsbarn(SOKNADSBARN_REFERANSE, 1, soknadsbarnFodselsdato);

    // Lag samværsinfo
    var samvaersklassePeriodeListe = singletonList(new SamvaersklassePeriode(SAMVAERSKLASSE_REFERANSE,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-07-01")), "02"));

    // Lag sjabloner
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(
                new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(727))))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), null),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(
                new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Arrays.asList(
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1052))))));

    var grunnlag = new BeregnSamvaersfradragGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarn, samvaersklassePeriodeListe, sjablonPeriodeListe);

    var avvikListe = samvaersfradragPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(2),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Første dato i samvaersklassePeriodeListe (2019-01-01) er etter beregnDatoFra (2018-07-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i samvaersklassePeriodeListe (2020-07-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
    );

    printAvvikListe(avvikListe);
  }


  private void printGrunnlagResultat(BeregnetSamvaersfradragResultat beregnetSamvaersfradragResultat) {
    beregnetSamvaersfradragResultat.getResultatPeriodeListe().stream()
        .sorted(comparing(pR -> pR.getPeriode().getDatoFom()))
        .forEach(sortedPR -> System.out.println("Dato fra: " + sortedPR.getPeriode().getDatoFom() + "; " +
            "Dato til: " + sortedPR.getPeriode().getDatoTil() + "; " + "Samvaersfradragsbeløp: " + sortedPR.getResultat().getBelop()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }

}
