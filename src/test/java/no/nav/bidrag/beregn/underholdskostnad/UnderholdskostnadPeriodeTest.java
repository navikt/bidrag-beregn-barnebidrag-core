package no.nav.bidrag.beregn.underholdskostnad;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UnderholdskostnadPeriodeTest {

  private BeregnUnderholdskostnadGrunnlag grunnlag;

  private UnderholdskostnadPeriode underholdskostnadPeriode = UnderholdskostnadPeriode
      .getInstance();

  @Test
  @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  void testPeriodisering() {

    lagGrunnlag("2018-07-01", "2020-01-01");

    var resultat = underholdskostnadPeriode.beregnPerioder(grunnlag);

    assertThat(resultat).isNotNull();

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSoknadBarnAlder())
            .isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning()
            .getResultatBelopUnderholdskostnad())
            .isEqualTo(BigDecimal.valueOf(4491)),

        () -> assertThat(
            resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getSoknadBarnAlder())
            .isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning()
            .getResultatBelopUnderholdskostnad())
            .isEqualTo(BigDecimal.valueOf(5602)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag()
            .getForpleiningUtgiftBelop()).isEqualTo(BigDecimal.valueOf(123)),

        () -> assertThat(
            resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-03-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(2).getResultatGrunnlag().getSoknadBarnAlder())
            .isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning()
            .getResultatBelopUnderholdskostnad())
            .isEqualTo(BigDecimal.valueOf(4380)),

        () -> assertThat(
            resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2019-03-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(3).getResultatGrunnlag().getSoknadBarnAlder())
            .isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning()
            .getResultatBelopUnderholdskostnad())
            .isEqualTo(BigDecimal.valueOf(4380)),

        () -> assertThat(
            resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(4).getResultatGrunnlag().getSoknadBarnAlder())
            .isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatBeregning()
            .getResultatBelopUnderholdskostnad())
            .isEqualTo(BigDecimal.valueOf(4491)),

        // Barnet har fyllt 11 år og skal ha høyere forbruksutgifter enn de første periodene
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil())
            .isNull(),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(5).getResultatGrunnlag().getSoknadBarnAlder())
            .isEqualTo(11),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatBeregning()
            .getResultatBelopUnderholdskostnad())
            .isEqualTo(BigDecimal.valueOf(5477))

    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test at barnetrygd ikke trekkes fra i barnets fødselsmåned, "
      + "tester også at barnetrygd trekkes fra i påfølgende periode som normalt")
  void testAtBarnetrygdIkkeTrekkesFraIFodselsmaaned() {

    var beregnDatoFra = LocalDate.parse("2019-07-01");
    var beregnDatoTil = LocalDate.parse("2020-07-01");
    var soknadsbarnFodselsdato = LocalDate.parse("2019-07-29");

//    var barnetilsynMedStonadPeriodeListe = new ArrayList<BarnetilsynMedStonadPeriode>(Collections.emptyList());
    var barnetilsynMedStonadPeriodeListe = new ArrayList<BarnetilsynMedStonadPeriode>();
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
        new Periode(LocalDate.parse("2019-03-01"), null),
        "DU", "64"));

//    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriode>(Collections.emptyList());
    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriode>();
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2018-04-01"), null), BigDecimal.valueOf(2000)));

    var forpleiningUtgiftPeriodeListe = new ArrayList<ForpleiningUtgiftPeriode>();
    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2018-02-01"), LocalDate.parse("2020-10-01")), BigDecimal.valueOf(0)));

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), null),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1054))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2021-07-01"), null),
        new Sjablon(SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1354))))));

    grunnlag = new BeregnUnderholdskostnadGrunnlag(1, beregnDatoFra, beregnDatoTil,
        soknadsbarnFodselsdato, barnetilsynMedStonadPeriodeListe, nettoBarnetilsynPeriodeListe,
        forpleiningUtgiftPeriodeListe, sjablonPeriodeListe);

    var resultat = underholdskostnadPeriode.beregnPerioder(grunnlag);

    assertThat(resultat).isNotNull();

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(2),

        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBelopUnderholdskostnad().doubleValue())
            .isEqualTo(2000d),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil())
            .isNull(),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBelopUnderholdskostnad().doubleValue())
            .isEqualTo(2000d - 1054d)
    );

    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Test det trekkes fra forhøyet barnetrygd for barn under seks år etter 01.07.2021")
  void testForhoyetBarneTrygd() {

    var beregnDatoFra = LocalDate.parse("2021-03-01");
    var beregnDatoTil = LocalDate.parse("2022-07-01");
    var soknadsbarnFodselsdato = LocalDate.parse("2015-10-29");

    var barnetilsynMedStonadPeriodeListe = new ArrayList<BarnetilsynMedStonadPeriode>();
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
        new Periode(LocalDate.parse("2019-03-01"), null),
        "DU", "64"));

    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriode>();
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2018-04-01"), null), BigDecimal.valueOf(2000)));

    var forpleiningUtgiftPeriodeListe = new ArrayList<ForpleiningUtgiftPeriode>();
    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2018-02-01"), null), BigDecimal.valueOf(0)));

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), null),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1054))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2021-07-01"), null),
        new Sjablon(SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1354))))));

    grunnlag = new BeregnUnderholdskostnadGrunnlag(1, beregnDatoFra, beregnDatoTil,
        soknadsbarnFodselsdato, barnetilsynMedStonadPeriodeListe, nettoBarnetilsynPeriodeListe,
        forpleiningUtgiftPeriodeListe, sjablonPeriodeListe);

    var resultat = underholdskostnadPeriode.beregnPerioder(grunnlag);

    assertThat(resultat).isNotNull();

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(3),
        // Første periode er før innføring av forhøyet barnetrygd
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2021-03-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2021-07-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBelopUnderholdskostnad().doubleValue())
            .isEqualTo(2000d - 1054d),
        // Forhøyet barnetrygd er innført, barnet er under seks år -> forhøyet barnetrygd brukes
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2021-07-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2021-11-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBelopUnderholdskostnad().doubleValue())
            .isEqualTo(2000d - 1354d),
        // Forhøyet barnetrygd er innført, barnet har fyllt seks år -> ordinær barnetrygd brukes
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra())
            .isEqualTo(LocalDate.parse("2021-11-01")),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil())
            .isNull(),
        () -> assertThat(
            resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBelopUnderholdskostnad().doubleValue())
            .isEqualTo(2000d - 1054d)
    );

    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

    lagGrunnlag("2015-01-01", "2021-01-01");
    var avvikListe = underholdskostnadPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(4),
        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo(
                "Første dato i barnetilsynMedStonadPeriodeListe (2018-01-01) er etter beregnDatoFra (2015-01-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType())
            .isEqualTo(AvvikType.PERIODE_MANGLER_DATA),
        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo(
                "Siste dato i barnetilsynMedStonadPeriodeListe (2020-12-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType())
            .isEqualTo(AvvikType.PERIODE_MANGLER_DATA),
        () -> assertThat(avvikListe.get(2).getAvvikTekst())
            .isEqualTo(
                "Første dato i nettoBarnetilsynPeriodeListe (2016-01-01) er etter beregnDatoFra (2015-01-01)"),
        () -> assertThat(avvikListe.get(2).getAvvikType())
            .isEqualTo(AvvikType.PERIODE_MANGLER_DATA),
        () -> assertThat(avvikListe.get(3).getAvvikTekst())
            .isEqualTo(
                "Siste dato i forpleiningUtgiftPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(3).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
    );

    printAvvikListe(avvikListe);
  }

  private void lagGrunnlag(String beregnDatoFra, String beregnDatoTil) {
    var soknadsbarnFodselsdato = LocalDate.parse("2008-01-29");

    grunnlag = new BeregnUnderholdskostnadGrunnlag(1, LocalDate.parse(beregnDatoFra),
        LocalDate.parse(beregnDatoTil), soknadsbarnFodselsdato,
        lagBarnetilsynMedStonadGrunnlag(), lagNettoBarnetilsynGrunnlag(),
        lagForpleiningUtgiftGrunnlag(), lagSjablonGrunnlag());
  }

  private List<BarnetilsynMedStonadPeriode> lagBarnetilsynMedStonadGrunnlag() {
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

  private List<NettoBarnetilsynPeriode> lagNettoBarnetilsynGrunnlag() {
    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriode>();
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2016-01-01"), LocalDate.parse("2019-01-01")), BigDecimal.valueOf(555)));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-04-01")), BigDecimal.valueOf(1666)));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2019-04-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(1777)));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
        new Periode(LocalDate.parse("2020-01-01"), null), BigDecimal.valueOf(1)));

    return nettoBarnetilsynPeriodeListe;
  }

  private List<ForpleiningUtgiftPeriode> lagForpleiningUtgiftGrunnlag() {
    var forpleiningUtgiftPeriodeListe = new ArrayList<ForpleiningUtgiftPeriode>();
    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2001-01-01"), LocalDate.parse("2017-01-01")), BigDecimal.valueOf(123)));
    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-02-01")), BigDecimal.valueOf(123)));
    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
        new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(1345)));

    return forpleiningUtgiftPeriodeListe;
  }

  private List<SjablonPeriode> lagSjablonGrunnlag() {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1054))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), null),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1054))))));

    // Forbruksutgifter
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), Collections
            .singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
            Collections.singletonList(
                new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(6985))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), Collections
            .singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Collections.singletonList(
                new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(3661))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), Collections
            .singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
            Collections.singletonList(
                new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(6985))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), Collections
            .singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Collections.singletonList(
                new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(5113))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), Collections
            .singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
            Collections.singletonList(
                new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(6099))))));

    return sjablonPeriodeListe;
  }

  private void printGrunnlagResultat(
      BeregnUnderholdskostnadResultat beregnUnderholdskostnadResultat) {
    beregnUnderholdskostnadResultat.getResultatPeriodeListe().stream().sorted(
        Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
        .forEach(sortedPR -> System.out
            .println(
                "Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: "
                    + sortedPR.getResultatDatoFraTil().getDatoTil()
                    + "; " + "Beløp: " + sortedPR.getResultatBeregning()
                    .getResultatBelopUnderholdskostnad()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println(
        "Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}