package no.nav.bidrag.beregn.barnebidrag;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaretPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBostedPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode;

import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgiftPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BarnebidragPeriodeTest {

  private BeregnBarnebidragGrunnlag grunnlag;

  private BarnebidragPeriode barnebidragPeriode = BarnebidragPeriode.getInstance();

  public ArrayList<GrunnlagBeregningPerBarn> grunnlagBeregningPerBarnListe = new ArrayList<>();
  public ArrayList<SjablonPeriode> sjablonPeriodeListe = new ArrayList<>();

  @Test
  @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  void testPeriodisering() {

    LocalDate beregnDatoFra = LocalDate.parse("2019-08-01");
    LocalDate beregnDatoTil = LocalDate.parse("2020-01-01");

    lagSjablonliste();

    grunnlagBeregningPerBarnListe.clear();
    var bidragsevnePeriodeListe           = new ArrayList<BidragsevnePeriode>();
    var bPsAndelUnderholdskostnadListe    = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    var samvaersfradragPeriodeListe       = new ArrayList<SamvaersfradragPeriode>();
    var deltBostedPeriodeListe            = new ArrayList<DeltBostedPeriode>();
    var barnetilleggBPPeriodeListe        = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggBMPeriodeListe        = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        15000d, 15000d));

    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        80d, 1000d));

    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        0d));

    deltBostedPeriodeListe.add(new DeltBostedPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        false));

    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        0d, 0d));

    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        0d, 0d));

    barnetilleggForsvaretPeriodeListe.add(new BarnetilleggForsvaretPeriode(
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        false));

    BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag =
        new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
            bPsAndelUnderholdskostnadListe, samvaersfradragPeriodeListe, deltBostedPeriodeListe,
            barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe, barnetilleggForsvaretPeriodeListe,
            sjablonPeriodeListe);

    var resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01"))

    );

    printGrunnlagResultat(resultat);
  }

  private void lagSjablonliste(){
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2021-06-30")),
        new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                5667d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2021-06-30")),
        new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                2334d)))));
  }


  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

//    lagGrunnlag("2016-01-01", "2021-01-01");
    var avvikListe = barnebidragPeriode.validerInput(grunnlag);

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



  private void printGrunnlagResultat(
      BeregnBarnebidragResultat beregnBarnebidragResultat) {
    beregnBarnebidragResultat.getResultatPeriodeListe().stream().sorted(
        Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: "
                + sortedPR.getResultatDatoFraTil().getDatoTil()
                + "; " + "Prosentandel: " + sortedPR.getResultatBeregningListe().get(0)));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
