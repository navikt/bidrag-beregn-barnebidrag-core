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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ForholdsmessigFordelingPeriodeTest {

  private final ForholdsmessigFordelingPeriode forholdsmessigFordelingPeriode = ForholdsmessigFordelingPeriode.getInstance();

  @Test
  @DisplayName("")
  void testPeriodisering() {
    var beregnDatoFra = LocalDate.parse("2019-01-01");
    var beregnDatoTil = LocalDate.parse("2021-01-01");

    var bidragsevnePeriodeListe       = new ArrayList<BidragsevnePeriode>();
    var beregnetBidragSakPeriodeListe = new ArrayList<BeregnetBidragSakPeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2021-01-01")),
        BigDecimal.valueOf(15000), BigDecimal.valueOf(16000)));

    beregnetBidragSakPeriodeListe.add(new BeregnetBidragSakPeriode(1,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2021-01-01")),
        Arrays.asList(new GrunnlagPerBarn( 1, BigDecimal.valueOf(1000)))));


    BeregnForholdsmessigFordelingGrunnlag beregnForholdsmessigFordelingGrunnlag =
        new BeregnForholdsmessigFordelingGrunnlag(beregnDatoFra, beregnDatoTil,
            bidragsevnePeriodeListe, beregnetBidragSakPeriodeListe);

    var resultat = forholdsmessigFordelingPeriode.beregnPerioder(beregnForholdsmessigFordelingGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2021-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatPerBarnListe().get(0).getBarnPersonId())
            .isEqualTo(1)


    );

    printGrunnlagResultat(resultat);
  }

  private void printGrunnlagResultat(
      BeregnForholdsmessigFordelingResultat beregnForholdsmessigFordelingResultat) {
    beregnForholdsmessigFordelingResultat.getResultatPeriodeListe().stream().sorted(
        Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: "
                + sortedPR.getResultatDatoFraTil().getDatoTil()
                + "; " + "Beløp: " + sortedPR.getResultatBeregningListe()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
