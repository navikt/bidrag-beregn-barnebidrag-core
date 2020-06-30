package no.nav.bidrag.beregn.nettobarnetilsyn;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode;
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode;
import org.junit.jupiter.api.Test;

public class NettoBarnetilsynPeriodeTest {
  private UnderholdskostnadPeriode underholdskostnadPeriode = UnderholdskostnadPeriode.getInstance();

  @Test
  void lagGrunnlagTest() {
    System.out.println("Starter test");
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");
//    var beregnDatoTil = LocalDate.parse("2019-06-01");
    var soknadsbarnFodselsdato = LocalDate.parse("2008-01-29");

    BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag =
        new BeregnUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnFodselsdato,
        lagBarnetilsynMedStonadGrunnlag(), lagNettoBarnetilsynGrunnlag(), lagForpleiningUtgiftGrunnlag()
            ,lagSjablonGrunnlag());

    var resultat = underholdskostnadPeriode.beregnPerioder(beregnUnderholdskostnadGrunnlag);

    assertThat(resultat).isNotNull();

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSoknadBarnAlder()).isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBelopUnderholdskostnad()).isEqualTo(Double.valueOf(4737d)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getSoknadBarnAlder()).isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBelopUnderholdskostnad()).isEqualTo(Double.valueOf(5848d)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getForpleiningUtgiftBelop()).isEqualTo(Double.valueOf(123d)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getSoknadBarnAlder()).isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBelopUnderholdskostnad()).isEqualTo(Double.valueOf(7070d)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getSoknadBarnAlder()).isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatBelopUnderholdskostnad()).isEqualTo(Double.valueOf(7070d)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlag().getSoknadBarnAlder()).isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatBeregning().getResultatBelopUnderholdskostnad()).isEqualTo(Double.valueOf(7181d)),

        // Barnet har fyllt 11 år og skal ha høyere forbruksutgifter enn de første periodene
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlag().getSoknadBarnAlder()).isEqualTo(11),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatBeregning().getResultatBelopUnderholdskostnad()).isEqualTo(Double.valueOf(8167d))

    );

    printGrunnlagResultat(resultat);
  }

  private List<BarnetilsynMedStonadPeriode> lagBarnetilsynMedStonadGrunnlag(){
    var barnetilsynMedStonadPeriodeListe = new ArrayList<BarnetilsynMedStonadPeriode>();

    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-07-01")),
        "DU", "64"));
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-01-01")),
        "DU", "64"));
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-03-01")),
        "DU", "64"));
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
        new Periode(LocalDate.parse("2019-03-01"), LocalDate.parse("2019-07-01")),
        "DU", "64"));
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-12-01")),
        "DU", "64"));

    return barnetilsynMedStonadPeriodeListe;
  }

  private List<NettoBarnetilsynPeriode> lagNettoBarnetilsynGrunnlag(){
    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriode>();
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2016-01-01"), LocalDate.parse("2019-01-01")), 555d));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-04-01")), 1666d));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2019-04-01"), LocalDate.parse("2020-01-01")), 1777d));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2020-01-01"), null), 1));

    return nettoBarnetilsynPeriodeListe;
  }

  private List<ForpleiningUtgiftPeriode> lagForpleiningUtgiftGrunnlag(){
    var forpleiningUtgiftPeriodeListe = new ArrayList<ForpleiningUtgiftPeriode>();
    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2001-01-01"), LocalDate.parse("2017-01-01")), 123d));
    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-02-01")), 123d));
    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2020-01-01")), 1345d));

    return forpleiningUtgiftPeriodeListe;
  }

  private List<SjablonPeriode> lagSjablonGrunnlag() {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                1054d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), null),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                1054d)))));

    // Forbruksutgifter
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), 6985d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), 3661d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), 6985d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), 5113d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), 6099d)))));

    return sjablonPeriodeListe;
  }

  private void printGrunnlagResultat(BeregnUnderholdskostnadResultat beregnUnderholdskostnadResultat) {
    beregnUnderholdskostnadResultat.getResultatPeriodeListe().stream().sorted(
        Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: "
                + sortedPR.getResultatDatoFraTil().getDatoTil()
                + "; " + "Beløp: " + sortedPR.getResultatBeregning().getResultatBelopUnderholdskostnad()));
  }
}
