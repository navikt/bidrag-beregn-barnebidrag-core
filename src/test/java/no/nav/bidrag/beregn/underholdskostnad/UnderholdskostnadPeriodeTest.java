package no.nav.bidrag.beregn.underholdskostnad;

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
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode;
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode;
import org.junit.jupiter.api.Test;

public class UnderholdskostnadPeriodeTest {
  private UnderholdskostnadPeriode underholdskostnadPeriode = UnderholdskostandPeriode.getInstance();

  @Test
  void lagGrunnlagTest() {
    System.out.println("Starter test");
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");
    var soknadsbarnFodselsdato = LocalDate.parse("2012-01-29");

    BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag =
        new BeregnUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnFodselsdato,
        lagBarnetilsynMedStonadGrunnlag(), lagNettoBarnetilsynGrunnlag(), lagForpleiningUtgiftGrunnlag()
            ,lagSjablonGrunnlag());

    var resultat = underholdskostnadPeriode.beregnPerioder(beregnUnderholdskostnadGrunnlag);

    assertThat(resultat).isNotNull();

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        //     () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(4),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBelopEvne()).isEqualTo(Double.valueOf(3749)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getInntektListe().get(0).equals(Double.valueOf(444000))),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBelopEvne()).isEqualTo(Double.valueOf(15604)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getBostatusKode()).isEqualTo(
            BostatusKode.ALENE),


    printGrunnlagResultat(resultat);

  }

  private List<BarnetilsynMedStonadPeriode> lagBarnetilsynMedStonadGrunnlag(){
    var barnetilsynMedStonadPeriodeListe = new ArrayList<BarnetilsynMedStonadPeriode>();

    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-02-01")),
    "DU", "64"));
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
        new Periode(LocalDate.parse("2017-02-01"), LocalDate.parse("2017-07-01")),
        "DU", "64"));
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
        new Periode(LocalDate.parse("2017-07-01"), LocalDate.parse("2018-01-01")),
        "DU", "64"));
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
        new Periode(LocalDate.parse("2003-01-01"), LocalDate.parse("2004-01-01")), 333d));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2004-01-01"), LocalDate.parse("2016-01-01")), 444d));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2016-01-01"), LocalDate.parse("2019-01-01")), 555d));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-04-01")), 666d));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2019-04-01"), LocalDate.parse("2020-01-01")), 777d));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2020-01-01"), null), 1));

    return nettoBarnetilsynPeriodeListe;

  }


  private List<ForpleiningUtgiftPeriode> lagForpleiningUtgiftGrunnlag(){

    var forpleiningUtgiftPeriodeListe = new ArrayList<ForpleiningUtgiftPeriode>();

    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2001-01-01"), LocalDate.parse("2017-01-01")), 123d));

    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-02-01")), 234d));

    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2020-01-01")), 345d));

    return forpleiningUtgiftPeriodeListe;

  }

  private List<SjablonPeriode> lagSjablonGrunnlag() {

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
        new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                23d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), null),
        new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                22d)))));

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
