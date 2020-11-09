package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragResultat;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class KostnadsberegnetBidragPeriodeTest {

    private KostnadsberegnetBidragPeriode kostnadsberegnetBidragPeriode =
        KostnadsberegnetBidragPeriode.getInstance();

    @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
    @Test
    void testPeriodisering() {
      System.out.println("Starter test");
      var beregnDatoFra = LocalDate.parse("2018-07-01");
      var beregnDatoTil = LocalDate.parse("2020-08-01");
      var soknadsbarnPersonId = 1;

      // Lag input
      var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
      underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
          new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-08-01")),
          BigDecimal.valueOf(10000)));

      underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
          new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-08-01")),
          BigDecimal.valueOf(1000)));

      var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
      bPsAndelUnderholdskostnadPeriodeListe.add(new BPsAndelUnderholdskostnadPeriode(
          new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
          BigDecimal.valueOf(20)));

      var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriode>();
      samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(
          new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
          BigDecimal.valueOf(100)));


      // Sjabloner brukes ikke i beregning av kostnadsberegnet bidrag

      BeregnKostnadsberegnetBidragGrunnlag beregnKostnadsberegnetBidragGrunnlag =
          new BeregnKostnadsberegnetBidragGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId,
              underholdskostnadPeriodeListe, bPsAndelUnderholdskostnadPeriodeListe, samvaersfradragPeriodeListe);

      var resultat = kostnadsberegnetBidragPeriode.beregnPerioder(beregnKostnadsberegnetBidragGrunnlag);

      assertAll(
          () -> assertThat(resultat).isNotNull(),
          () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
          () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(2),

          () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-08-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatkostnadsberegnetbidragBelop().doubleValue())
              .isEqualTo(1900d),

          () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-08-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-08-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatkostnadsberegnetbidragBelop().doubleValue())
              .isEqualTo(100d)
      );

      printGrunnlagResultat(resultat);
    }



  private void printGrunnlagResultat(
        BeregnKostnadsberegnetBidragResultat beregnKostnadsberegnetBidragResultat) {
      beregnKostnadsberegnetBidragResultat.getResultatPeriodeListe().stream().sorted(
          Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
          .forEach(sortedPR -> System.out
              .println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: "
                  + sortedPR.getResultatDatoFraTil().getDatoTil()
                  + "; " + "Prosentandel: " + sortedPR.getResultatBeregning().getResultatkostnadsberegnetbidragBelop()));
    }
}
