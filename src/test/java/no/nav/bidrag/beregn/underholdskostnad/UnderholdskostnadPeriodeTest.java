package no.nav.bidrag.beregn.underholdskostnad;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static no.nav.bidrag.beregn.TestUtil.BARNETILSYN_MED_STONAD_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.FORPLEINING_UTGIFT_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.NETTO_BARNETILSYN_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SOKNADSBARN_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnetUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.Soknadsbarn;
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UnderholdskostnadPeriodeTest {

  private BeregnUnderholdskostnadGrunnlag grunnlag;

  private final UnderholdskostnadPeriode underholdskostnadPeriode = UnderholdskostnadPeriode.getInstance();

  @Test
  @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  void testPeriodisering() {

    lagGrunnlag("2018-07-01", "2020-01-01");

    var resultat = underholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getSoknadsbarn().getAlder())
            .isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(4491)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getSoknadsbarn().getAlder())
            .isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(5602)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getForpleiningUtgift().getBelop())
            .isEqualTo(BigDecimal.valueOf(123)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getSoknadsbarn().getAlder())
            .isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(4380)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getSoknadsbarn().getAlder())
            .isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(4380)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getSoknadsbarn().getAlder())
            .isEqualTo(10),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(4491)),

        // Barnet har fylt 11 år og skal ha høyere forbruksutgifter enn de første periodene
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoTil())
            .isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getSoknadsbarn().getAlder())
            .isEqualTo(11),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(5477))
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test at barnetrygd ikke trekkes fra i barnets fødselsmåned, og at barnetrygd trekkes fra i påfølgende periode som normalt")
  void testAtBarnetrygdIkkeTrekkesFraIFodselsmaaned() {

    var beregnDatoFra = LocalDate.parse("2019-07-01");
    var beregnDatoTil = LocalDate.parse("2020-07-01");
    var soknadsbarnFodselsdato = LocalDate.parse("2019-07-29");

    var soknadsbarn = new Soknadsbarn(SOKNADSBARN_REFERANSE, 1, soknadsbarnFodselsdato);

    var barnetilsynMedStonadPeriodeListe = singletonList(new BarnetilsynMedStonadPeriode(BARNETILSYN_MED_STONAD_REFERANSE,
        new Periode(LocalDate.parse("2019-03-01"), null), "DU", "64"));

    var nettoBarnetilsynPeriodeListe = singletonList(new NettoBarnetilsynPeriode(NETTO_BARNETILSYN_REFERANSE,
        new Periode(LocalDate.parse("2018-04-01"), null), BigDecimal.valueOf(2000)));

    var forpleiningUtgiftPeriodeListe = singletonList(new ForpleiningUtgiftPeriode(FORPLEINING_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2018-02-01"), LocalDate.parse("2020-10-01")), BigDecimal.ZERO));

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), null),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1054))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2021-07-01"), null),
        new Sjablon(SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1354))))));

    grunnlag = new BeregnUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarn, barnetilsynMedStonadPeriodeListe,
        nettoBarnetilsynPeriodeListe, forpleiningUtgiftPeriodeListe, sjablonPeriodeListe);

    var resultat = underholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(2),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getBelop()
            .compareTo(BigDecimal.valueOf(2000))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultat().getBelop()
            .compareTo(BigDecimal.valueOf(2000 - 1054))).isZero()
    );

    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Test det trekkes fra forhøyet barnetrygd for barn under seks år etter 01.07.2021")
  void testForhoyetBarneTrygd() {

    var beregnDatoFra = LocalDate.parse("2021-03-01");
    var beregnDatoTil = LocalDate.parse("2022-07-01");
    var soknadsbarnFodselsdato = LocalDate.parse("2015-10-29");

    var soknadsbarn = new Soknadsbarn(SOKNADSBARN_REFERANSE, 1, soknadsbarnFodselsdato);

    var barnetilsynMedStonadPeriodeListe = singletonList(new BarnetilsynMedStonadPeriode(BARNETILSYN_MED_STONAD_REFERANSE,
        new Periode(LocalDate.parse("2019-03-01"), null), "DU", "64"));

    var nettoBarnetilsynPeriodeListe = singletonList(new NettoBarnetilsynPeriode(NETTO_BARNETILSYN_REFERANSE,
        new Periode(LocalDate.parse("2018-04-01"), null), BigDecimal.valueOf(2000)));

    var forpleiningUtgiftPeriodeListe = singletonList(new ForpleiningUtgiftPeriode(FORPLEINING_UTGIFT_REFERANSE,
        new Periode(LocalDate.parse("2018-02-01"), null), BigDecimal.ZERO));

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-01-01"), null),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1054))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2021-07-01"), null),
        new Sjablon(SjablonTallNavn.FORHOYET_BARNETRYGD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1354))))));

    grunnlag = new BeregnUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarn, barnetilsynMedStonadPeriodeListe,
        nettoBarnetilsynPeriodeListe, forpleiningUtgiftPeriodeListe, sjablonPeriodeListe);

    var resultat = underholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(3),

        // Første periode er før innføring av forhøyet barnetrygd
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2021-03-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2021-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getBelop()
            .compareTo(BigDecimal.valueOf(2000 - 1054))).isZero(),

        // Forhøyet barnetrygd er innført, barnet er under seks år -> forhøyet barnetrygd brukes
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2021-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2021-11-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultat().getBelop()
            .compareTo(BigDecimal.valueOf(2000 - 1354))).isZero(),

        // Forhøyet barnetrygd er innført, barnet har fyllt seks år -> ordinær barnetrygd brukes
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2021-11-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultat().getBelop()
            .compareTo(BigDecimal.valueOf(2000 - 1054))).isZero()
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
            .isEqualTo("Første dato i barnetilsynMedStonadPeriodeListe (2018-01-01) er etter beregnDatoFra (2015-01-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType())
            .isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i barnetilsynMedStonadPeriodeListe (2020-12-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType())
            .isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(2).getAvvikTekst())
            .isEqualTo("Første dato i nettoBarnetilsynPeriodeListe (2016-01-01) er etter beregnDatoFra (2015-01-01)"),
        () -> assertThat(avvikListe.get(2).getAvvikType())
            .isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(3).getAvvikTekst())
            .isEqualTo("Siste dato i forpleiningUtgiftPeriodeListe (2020-01-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(3).getAvvikType())
            .isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
    );

    printAvvikListe(avvikListe);
  }

  private void lagGrunnlag(String beregnDatoFra, String beregnDatoTil) {

    var soknadsbarnFodselsdato = LocalDate.parse("2008-01-29");

    grunnlag = new BeregnUnderholdskostnadGrunnlag(LocalDate.parse(beregnDatoFra), LocalDate.parse(beregnDatoTil),
        new Soknadsbarn(SOKNADSBARN_REFERANSE, 1, soknadsbarnFodselsdato), lagBarnetilsynMedStonadGrunnlag(), lagNettoBarnetilsynGrunnlag(),
        lagForpleiningUtgiftGrunnlag(), lagSjablonGrunnlag());
  }

  private List<BarnetilsynMedStonadPeriode> lagBarnetilsynMedStonadGrunnlag() {
    var barnetilsynMedStonadPeriodeListe = new ArrayList<BarnetilsynMedStonadPeriode>();

    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(BARNETILSYN_MED_STONAD_REFERANSE + "_1",
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-07-01")), "DU", "64"));
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(BARNETILSYN_MED_STONAD_REFERANSE + "_2",
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-01-01")), "DU", "64"));
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(BARNETILSYN_MED_STONAD_REFERANSE + "_3",
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-03-01")), "DU", "64"));
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(BARNETILSYN_MED_STONAD_REFERANSE + "_4",
        new Periode(LocalDate.parse("2019-03-01"), LocalDate.parse("2019-07-01")), "DU", "64"));
    barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(BARNETILSYN_MED_STONAD_REFERANSE + "_5",
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-12-01")), "DU", "64"));

    return barnetilsynMedStonadPeriodeListe;
  }

  private List<NettoBarnetilsynPeriode> lagNettoBarnetilsynGrunnlag() {
    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriode>();

    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(NETTO_BARNETILSYN_REFERANSE + "_1",
        new Periode(LocalDate.parse("2016-01-01"), LocalDate.parse("2019-01-01")), BigDecimal.valueOf(555)));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(NETTO_BARNETILSYN_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-04-01")), BigDecimal.valueOf(1666)));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(NETTO_BARNETILSYN_REFERANSE + "_3",
        new Periode(LocalDate.parse("2019-04-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(1777)));
    nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(NETTO_BARNETILSYN_REFERANSE + "_4",
        new Periode(LocalDate.parse("2020-01-01"), null), BigDecimal.valueOf(1)));

    return nettoBarnetilsynPeriodeListe;
  }

  private List<ForpleiningUtgiftPeriode> lagForpleiningUtgiftGrunnlag() {
    var forpleiningUtgiftPeriodeListe = new ArrayList<ForpleiningUtgiftPeriode>();

    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(FORPLEINING_UTGIFT_REFERANSE + "_1",
        new Periode(LocalDate.parse("2001-01-01"), LocalDate.parse("2017-01-01")), BigDecimal.valueOf(123)));
    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(FORPLEINING_UTGIFT_REFERANSE + "_2",
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-02-01")), BigDecimal.valueOf(123)));
    forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(FORPLEINING_UTGIFT_REFERANSE + "_3",
        new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(1345)));

    return forpleiningUtgiftPeriodeListe;
  }

  private List<SjablonPeriode> lagSjablonGrunnlag() {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    // Sjablontall
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-12-31")),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1054))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2019-01-01"), null),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1054))))));

    // Forbruksutgifter
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(6985))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(3661))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(6985))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(5113))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(6099))))));

    return sjablonPeriodeListe;
  }

  private void printGrunnlagResultat(BeregnetUnderholdskostnadResultat beregnetUnderholdskostnadResultat) {
    beregnetUnderholdskostnadResultat.getResultatPeriodeListe().stream()
        .sorted(comparing(pR -> pR.getPeriode().getDatoFom()))
        .forEach(sortedPR -> System.out.println("Dato fra: " + sortedPR.getPeriode().getDatoFom() + "; " +
            "Dato til: " + sortedPR.getPeriode().getDatoTil() + "; " + "Beløp: " + sortedPR.getResultat().getBelop()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
