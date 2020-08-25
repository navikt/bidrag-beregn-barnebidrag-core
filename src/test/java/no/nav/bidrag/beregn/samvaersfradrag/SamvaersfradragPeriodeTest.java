package no.nav.bidrag.beregn.samvaersfradrag;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersklassePeriode;
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SamvaersfradragPeriodeTest {

    private SamvaersfradragPeriode samvaersfradragPeriode = SamvaersfradragPeriode.getInstance();

    @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
    @Test
    void testPeriodisering() {
      System.out.println("Starter test");
      var beregnDatoFra = LocalDate.parse("2018-07-01");
      var beregnDatoTil = LocalDate.parse("2020-08-01");
      var soknadsbarnFodselsdato = LocalDate.parse("2017-08-17");

      // Lag inntekter
      var samvaersklassePeriodeListe = new ArrayList<SamvaersklassePeriode>();
      samvaersklassePeriodeListe.add(new SamvaersklassePeriode(
          new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
          "03"));

      // Lag sjabloner
      var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
      sjablonPeriodeListe.add(new SjablonPeriode(
          new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
          new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
              Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                  1600d)))));
      sjablonPeriodeListe.add(new SjablonPeriode(
          new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30")),
          new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
              Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                  1640d)))));
      sjablonPeriodeListe.add(new SjablonPeriode(
          new Periode(LocalDate.parse("2020-07-01"), null),
          new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
              Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                  1670d)))));

      BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag =
          new BeregnSamvaersfradragGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnFodselsdato,
              samvaersklassePeriodeListe, sjablonPeriodeListe);

      var resultat = samvaersfradragPeriode.beregnPerioder(beregnSamvaersfradragGrunnlag);

      assertAll(
          () -> assertThat(resultat).isNotNull(),
          () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
          () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(3),

          () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatSamvaersfradragBelop()).isEqualTo(2082.0d),

          () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-07-01")),

          () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-07-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isNull()
      );

      printGrunnlagResultat(resultat);
    }




  private void printGrunnlagResultat(
        BeregnSamvaersfradragResultat beregnSamvaersfradragResultat) {
      beregnSamvaersfradragResultat.getResultatPeriodeListe().stream().sorted(
          Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
          .forEach(sortedPR -> System.out
              .println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: "
                  + sortedPR.getResultatDatoFraTil().getDatoTil()
                  + "; " + "Samvaersfradragsbeløp: " + sortedPR.getResultatBeregning().getResultatSamvaersfradragBelop()));
    }
}
