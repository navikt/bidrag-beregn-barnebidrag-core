package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntekterPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BPsAndelUnderholdskostnadPeriodeTest {

    private BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriode = BPsAndelUnderholdskostnadPeriode.getInstance();

    @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
    @Test
    void testPeriodisering() {
      System.out.println("Starter test");
      var beregnDatoFra = LocalDate.parse("2018-07-01");
      var beregnDatoTil = LocalDate.parse("2020-08-01");

      // Lag inntekter
      var inntekterPeriodeListe = new ArrayList<InntekterPeriode>();
      inntekterPeriodeListe.add(new InntekterPeriode(
          new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
          217666, 400000, 40000));

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

      BeregnBPsAndelUnderholdskostnadGrunnlag beregnBPsAndelUnderholdskostnadGrunnlag =
          new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, inntekterPeriodeListe,
              sjablonPeriodeListe);

      var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(beregnBPsAndelUnderholdskostnadGrunnlag);

      assertAll(
          () -> assertThat(resultat).isNotNull(),
          () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
          () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(3),

          () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent()).isEqualTo(33.1d),

          () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-07-01")),

          () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-07-01")),
          () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isNull()
      );

      printGrunnlagResultat(resultat);
    }

  @DisplayName("Test av beregning med gamle og nye regler. Resultat for perioder før 2009 skal angis i nærmeste sjettedel."
      + "Det skal også lages brudd i periode ved overgang til nye regler 01.01.2009")
  @Test
  void testBeregningMedGamleOgNyeRegler() {
    System.out.println("Starter test");
    var beregnDatoFra = LocalDate.parse("2008-01-01");
    var beregnDatoTil = LocalDate.parse("2009-07-01");

    // Lag inntekter
    var inntekterPeriodeListe = new ArrayList<InntekterPeriode>();
    inntekterPeriodeListe.add(new InntekterPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")),
        300000, 400000, 40000));
    inntekterPeriodeListe.add(new InntekterPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        3000, 400000, 4000000));

    // Lag sjabloner
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2008-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                1600d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2008-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                1700d)))));

    BeregnBPsAndelUnderholdskostnadGrunnlag beregnBPsAndelUnderholdskostnadGrunnlag =
        new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, inntekterPeriodeListe,
            sjablonPeriodeListe);

    var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(beregnBPsAndelUnderholdskostnadGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(4),

        // Gamle regler
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2008-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2008-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent()).isEqualTo(33.3d),

        // Gamle regler
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2008-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2009-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent()).isEqualTo(33.3d),

        // Nye regler
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2009-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2009-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatAndelProsent()).isEqualTo(40.5d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2009-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2009-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatAndelProsent()).isEqualTo(0.0d)
    );

    printGrunnlagResultat(resultat);
  }



  private void printGrunnlagResultat(
        BeregnBPsAndelUnderholdskostnadResultat beregnBPsAndelUnderholdskostnadResultat) {
      beregnBPsAndelUnderholdskostnadResultat.getResultatPeriodeListe().stream().sorted(
          Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
          .forEach(sortedPR -> System.out
              .println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: "
                  + sortedPR.getResultatDatoFraTil().getDatoTil()
                  + "; " + "Prosentandel: " + sortedPR.getResultatBeregning().getResultatAndelProsent()));
    }
}
