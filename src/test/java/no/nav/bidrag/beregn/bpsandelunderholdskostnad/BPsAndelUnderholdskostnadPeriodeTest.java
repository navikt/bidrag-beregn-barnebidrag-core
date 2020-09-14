package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BPsAndelUnderholdskostnadPeriodeTest {

  private BeregnBPsAndelUnderholdskostnadGrunnlag grunnlag;

  private BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriode = BPsAndelUnderholdskostnadPeriode.getInstance();

  @Test
  @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  void testPeriodisering() {

    lagGrunnlag("2018-07-01", "2020-08-01");

    var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(grunnlag);

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

  @Test
  @DisplayName("Test av beregning med gamle og nye regler. Resultat for perioder før 2009 skal angis i nærmeste sjettedel."
      + "Det skal også lages brudd i periode ved overgang til nye regler 01.01.2009")
  void testBeregningMedGamleOgNyeRegler() {
    System.out.println("Starter test");
    var beregnDatoFra = LocalDate.parse("2008-01-01");
    var beregnDatoTil = LocalDate.parse("2009-07-01");

    // Lag inntekter
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    var inntektBPPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBMPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBBPeriodeListe = new ArrayList<InntektPeriode>();

    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")),
        1000d));

    inntektBPPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")),
        new Inntekt(InntektType.LØNNSINNTEKT, 300000d)));

    inntektBMPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")),
        new Inntekt(InntektType.LØNNSINNTEKT, 400000d)));

    inntektBBPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")),
        new Inntekt(InntektType.LØNNSINNTEKT, 40000d)));


    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        1000d));

    inntektBPPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        new Inntekt(InntektType.LØNNSINNTEKT, 3000d)));

    inntektBMPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        new Inntekt(InntektType.LØNNSINNTEKT, 400000d)));

    inntektBBPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        new Inntekt(InntektType.LØNNSINNTEKT, 4000000d)));

    // Lag sjabloner
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2008-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                1600d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2008-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                1700d)))));

    BeregnBPsAndelUnderholdskostnadGrunnlag beregnBPsAndelUnderholdskostnadGrunnlag =
        new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil,
            underholdskostnadPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe,
            inntektBBPeriodeListe, sjablonPeriodeListe);

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

  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

    lagGrunnlag("2016-01-01", "2021-01-01");
    var avvikListe = bPsAndelunderholdskostnadPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(6),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Første dato i inntektBPPeriodeListe (2018-01-01) er etter beregnDatoFra (2016-01-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i inntektBPPeriodeListe (2020-08-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
    );

    printAvvikListe(avvikListe);
  }

  private void lagGrunnlag(String beregnDatoFra, String beregnDatoTil) {

    // Lag inntekter
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    var inntektBPPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBMPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBBPeriodeListe = new ArrayList<InntektPeriode>();

    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        1000d));

    inntektBPPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        new Inntekt(InntektType.LØNNSINNTEKT, 217666d)));

    inntektBMPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        new Inntekt(InntektType.LØNNSINNTEKT, 400000d)));

    inntektBBPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        new Inntekt(InntektType.LØNNSINNTEKT, 40000d)));

    // Lag sjabloner
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                1600d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                1640d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2020-07-01"), null),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                1670d)))));

    grunnlag = new BeregnBPsAndelUnderholdskostnadGrunnlag(LocalDate.parse(beregnDatoFra), LocalDate.parse(beregnDatoTil),
        underholdskostnadPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe,
        sjablonPeriodeListe);
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

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
