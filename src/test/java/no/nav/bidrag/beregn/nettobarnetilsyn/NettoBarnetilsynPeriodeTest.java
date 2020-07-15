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
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynResultat;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgiftPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NettoBarnetilsynPeriodeTest {
  private NettoBarnetilsynPeriode nettoBarnetilsynPeriode = NettoBarnetilsynPeriode.getInstance();

  @DisplayName("Test av periodisering for ett barn. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  @Test
  void testPeriodiseringEttBarn() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-01-01")),
        LocalDate.parse("2008-03-17"), 1, 1000d));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-08-01")),
        LocalDate.parse("2008-03-17"), 1, 2000d));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2008-03-17"), 1, 5000d));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe,
            lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertThat(resultat).isNotNull();

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getFaktiskUtgiftListe().get(0).getFaktiskUtgiftBelop())
            .isEqualTo(1000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeregningListe().get(0).getResultatBelop())
            .isEqualTo(Double.valueOf(749.5d)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getFaktiskUtgiftListe().get(0).getFaktiskUtgiftBelop())
            .isEqualTo(2000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeregningListe().get(0).getResultatBelop())
            .isEqualTo(Double.valueOf(1499d)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getFaktiskUtgiftListe().get(0).getFaktiskUtgiftBelop())
            .isEqualTo(5000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeregningListe().get(0).getResultatBelop())
            .isEqualTo(Double.valueOf(4478.13d))

    );

    printGrunnlagResultat(resultat);
  }

  @DisplayName("Test av periodisering for to barn. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  @Test
  void testPeriodiseringToBarn() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-01-01")),
        LocalDate.parse("2008-03-17"), 1, 1000d));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-08-01")),
        LocalDate.parse("2008-03-17"), 1, 2000d));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-08-01")),
        LocalDate.parse("2012-02-18"), 2, 2000d));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2008-03-17"), 1, 5000d));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe,
            lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertThat(resultat).isNotNull();

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getFaktiskUtgiftListe().get(0).getFaktiskUtgiftBelop())
            .isEqualTo(1000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeregningListe().get(0).getResultatBelop())
            .isEqualTo(Double.valueOf(749.5d)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getFaktiskUtgiftListe().get(0).getFaktiskUtgiftBelop())
            .isEqualTo(2000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeregningListe().get(0).getResultatBelop())
            .isEqualTo(Double.valueOf(1582.54d)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getFaktiskUtgiftListe().get(1).getFaktiskUtgiftBelop())
            .isEqualTo(2000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeregningListe().get(1).getResultatBelop())
            .isEqualTo(Double.valueOf(1582.54d)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeregningListe().get(1)
            .getResultatPersonIdSoknadsbard()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getFaktiskUtgiftListe().get(0).getFaktiskUtgiftBelop())
            .isEqualTo(5000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeregningListe().get(0).getResultatBelop())
            .isEqualTo(Double.valueOf(4478.13d))

    );

    printGrunnlagResultat(resultat);
  }

  @DisplayName("Test at netto barnetilsyn ikke beregnes hvis barnet er over 12 år")
  @Test
  void testNullNettoBarnetilsynBarnOver12Aaar() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2005-03-17"), 1, 1000d));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe,
            lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertThat(resultat).isNotNull();

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeregningListe().isEmpty())

    );

    printGrunnlagResultat(resultat);
  }

  @DisplayName("Test at netto barnetilsyn ikke beregnes hvis faktisk utgift er 0.-")
  @Test
  void testNullNettoBarnetilsynVed0IFaktiskUtgift() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2015-03-17"), 1, 0d));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe,
            lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertThat(resultat).isNotNull();

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeregningListe().isEmpty())

    );

    printGrunnlagResultat(resultat);
  }

  @DisplayName("Test at faktiske utgifter med 0 i beløp ikke tas med i beregning og resultat")
  @Test
  void testAt0IFaktiskUtgiftFjernesFraGrunnlag() {
    var beregnDatoFra = LocalDate.parse("2019-07-01");
    var beregnDatoTil = LocalDate.parse("2020-02-01");

    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2015-03-17"), 1, 200d));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2015-03-17"), 1, 0d));
    faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-01-01")),
        LocalDate.parse("2015-03-17"), 2, 800d));

    BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag =
        new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, faktiskUtgiftPeriodeListe,
            lagSjablonGrunnlag());

    var resultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);

    assertThat(resultat).isNotNull();

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getFaktiskUtgiftListe().size()).isEqualTo(2)

    );

    printGrunnlagResultat(resultat);
  }

  private List<SjablonPeriode> lagSjablonGrunnlag() {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    // Maks fradrag
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "1")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), 2083.33d)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "2")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), 3333d)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "3")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), 4583d)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "4")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), 5833d)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "5")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), 7083d)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "6")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), 8333d)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "7")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), 9583d)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "8")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), 10833d)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "99")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), 12083d)))));

    // Maks tilsyn
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_TILSYN.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "1")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(), 6214d)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_TILSYN.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "2")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(), 8109d)))));

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.MAKS_TILSYN.getNavn(), Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "99")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(), 9189d)))));

    // Sjablontall
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),new Sjablon(SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 25.05d)))));


    return sjablonPeriodeListe;
  }

  private void printGrunnlagResultat(BeregnNettoBarnetilsynResultat beregnNettoBarnetilsynResultat) {
    beregnNettoBarnetilsynResultat.getResultatPeriodeListe().stream().sorted(
        Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
        .forEach(sortedPR -> System.out
            .println("Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: "
                + sortedPR.getResultatDatoFraTil().getDatoTil()
                + "; " + "Beløp: " + sortedPR.getResultatBeregning().getResultatBeregningListe()));
  }
}
