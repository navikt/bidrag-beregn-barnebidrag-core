package no.nav.bidrag.beregn.forholdsmessigfordeling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingGrunnlag;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingResultat;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSakPeriode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagPerBarn;
import no.nav.bidrag.beregn.forholdsmessigfordeling.periode.ForholdsmessigFordelingPeriode;
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeBarnebidrag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ForholdsmessigFordelingPeriodeTest {

  private final ForholdsmessigFordelingPeriode forholdsmessigFordelingPeriode = ForholdsmessigFordelingPeriode.getInstance();

  @Test
  @DisplayName("Test periodisering med to saker med to perioder og ett barn hver og full evne")
  void testPeriodiseringToSakerToPerioderHverEttBarnFullEvne() {
    var beregnDatoFra = LocalDate.parse("2019-01-01");
    var beregnDatoTil = LocalDate.parse("2021-01-01");

    var bidragsevnePeriodeListe       = new ArrayList<BidragsevnePeriode>();
    var beregnetBidragSakPeriodeListe = new ArrayList<BeregnetBidragSakPeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2021-01-01")),
        BigDecimal.valueOf(15000), BigDecimal.valueOf(14000)));

    beregnetBidragSakPeriodeListe.add(new BeregnetBidragSakPeriode(1234567,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-09-01")),
        Arrays.asList(new GrunnlagPerBarn( 1, BigDecimal.valueOf(1000)))));
    beregnetBidragSakPeriodeListe.add(new BeregnetBidragSakPeriode(1234567,
        new Periode(LocalDate.parse("2020-09-01"), LocalDate.parse("2021-01-01")),
        Arrays.asList(new GrunnlagPerBarn( 1, BigDecimal.valueOf(2000)))));

    beregnetBidragSakPeriodeListe.add(new BeregnetBidragSakPeriode(7654321,
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-01")),
        Arrays.asList(new GrunnlagPerBarn( 2, BigDecimal.valueOf(500)))));
    beregnetBidragSakPeriodeListe.add(new BeregnetBidragSakPeriode(7654321,
        new Periode(LocalDate.parse("2020-06-01"), LocalDate.parse("2020-11-01")),
        Arrays.asList(new GrunnlagPerBarn( 2, BigDecimal.valueOf(1200)))));

    BeregnForholdsmessigFordelingGrunnlag beregnForholdsmessigFordelingGrunnlag =
        new BeregnForholdsmessigFordelingGrunnlag(beregnDatoFra, beregnDatoTil,
            bidragsevnePeriodeListe, beregnetBidragSakPeriodeListe);

    var resultat = forholdsmessigFordelingPeriode.beregnPerioder(beregnForholdsmessigFordelingGrunnlag);

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(5),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2020-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2020-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2020-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2020-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2020-11-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2020-11-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2021-01-01")),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getSaksnr()).isEqualTo(1234567),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(1).getSaksnr()).isEqualTo(7654321),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatPerBarnListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(0).getResultatPerBarnListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregningListe().get(1).getResultatPerBarnListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1200))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatPerBarnListe().get(0).getBarnPersonId())
            .isEqualTo(1)

    );

    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Basic test som tester periodisering med én sak og ett barn, to perioder og full evne")
  void testPeriodiseringEnSakEttBarnToPerioderManglendeEvne() {
    var beregnDatoFra = LocalDate.parse("2019-01-01");
    var beregnDatoTil = LocalDate.parse("2021-01-01");

    var bidragsevnePeriodeListe       = new ArrayList<BidragsevnePeriode>();
    var beregnetBidragSakPeriodeListe = new ArrayList<BeregnetBidragSakPeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2021-01-01")),
        BigDecimal.valueOf(15000), BigDecimal.valueOf(17000)));

    beregnetBidragSakPeriodeListe.add(new BeregnetBidragSakPeriode(1234567,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-09-01")),
        Arrays.asList(new GrunnlagPerBarn( 1, BigDecimal.valueOf(1000)))));
    beregnetBidragSakPeriodeListe.add(new BeregnetBidragSakPeriode(1234567,
        new Periode(LocalDate.parse("2020-09-01"), LocalDate.parse("2021-01-01")),
        Arrays.asList(new GrunnlagPerBarn( 1, BigDecimal.valueOf(2000)))));

    beregnetBidragSakPeriodeListe.add(new BeregnetBidragSakPeriode(7654321,
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-01")),
        Arrays.asList(new GrunnlagPerBarn( 2, BigDecimal.valueOf(15000)))));
    beregnetBidragSakPeriodeListe.add(new BeregnetBidragSakPeriode(7654321,
        new Periode(LocalDate.parse("2020-06-01"), LocalDate.parse("2020-11-01")),
        Arrays.asList(new GrunnlagPerBarn( 2, BigDecimal.valueOf(1200)))));

    BeregnForholdsmessigFordelingGrunnlag beregnForholdsmessigFordelingGrunnlag =
        new BeregnForholdsmessigFordelingGrunnlag(beregnDatoFra, beregnDatoTil,
            bidragsevnePeriodeListe, beregnetBidragSakPeriodeListe);

    var resultat = forholdsmessigFordelingPeriode.beregnPerioder(beregnForholdsmessigFordelingGrunnlag);

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(5),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getSaksnr()).isEqualTo(1234567),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(1).getSaksnr()).isEqualTo(7654321),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatPerBarnListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatPerBarnListe().get(0).getKode())
            .isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_INGEN_ENDRING),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(0).getResultatPerBarnListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(940))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(0).getResultatPerBarnListe().get(0).getKode())
        .isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(1).getResultatPerBarnListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(14060))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(1).getResultatPerBarnListe().get(0).getKode())
            .isEqualTo(ResultatKodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELOP_ENDRET),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatPerBarnListe().get(0).getBarnPersonId())
            .isEqualTo(1)

    );

    printGrunnlagResultat(resultat);
  }

  private void printGrunnlagResultat(
      BeregnForholdsmessigFordelingResultat beregnForholdsmessigFordelingResultat) {
    beregnForholdsmessigFordelingResultat.getResultatPeriodeListe().stream().sorted(
        Comparator.comparing(pR -> pR.getPeriode().getDatoFom()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getPeriode().getDatoFom() + "; " + "Dato til: "
                + sortedPR.getPeriode().getDatoTil()
                + "; " + "Beløp: " + sortedPR.getResultatBeregningListe()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
