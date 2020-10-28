package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadResultat;
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
import no.nav.bidrag.beregn.felles.enums.Rolle;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.felles.enums.SoknadType;
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
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent()).isEqualTo(35.2d),

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
    var soknadsbarnPersonId = 1;

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
        InntektType.LONN_SKE, 300000d));

    inntektBMPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")),
        InntektType.LONN_SKE, 400000d));

    inntektBBPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")),
        InntektType.LONN_SKE, 40000d));

    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        1000d));

    inntektBPPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        InntektType.LONN_SKE, 3000d));

    inntektBMPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        InntektType.LONN_SKE, 400000d));

    inntektBBPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        InntektType.LONN_SKE, 4000000d));

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
        new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId,
            underholdskostnadPeriodeListe, inntektBPPeriodeListe,
            inntektBMPeriodeListe, inntektBBPeriodeListe, sjablonPeriodeListe);

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
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatAndelProsent()).isEqualTo(42.9d),

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

  @Test
  @DisplayName("Test med ugyldig inntekt som skal resultere i avvik")
  void testGrunnlagMedAvvikUgyldigInntekt() {

    lagGrunnlagMedAvvikUgyldigInntekt();
    var avvikListe = bPsAndelunderholdskostnadPeriode.validerInput(grunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(5),

        () -> assertThat(avvikListe.get(0).getAvvikTekst()).isEqualTo("inntektType " + InntektType.LIGNING_KORRIGERT_BARNETILLEGG.toString() +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG.toString() + " og rolle " + Rolle.BIDRAGSPLIKTIG.toString()),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE),

        () -> assertThat(avvikListe.get(1).getAvvikTekst()).isEqualTo("inntektType " + InntektType.LIGNING_KORRIGERT_BARNETILLEGG.toString() +
            " er kun gyldig fom. 2013-01-01 tom. 2018-12-31"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_PERIODE),

        () -> assertThat(avvikListe.get(2).getAvvikTekst()).isEqualTo("inntektType " + InntektType.PENSJON_KORR_BARNETILLEGG.toString() +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG.toString() + " og rolle " + Rolle.BIDRAGSMOTTAKER.toString()),
        () -> assertThat(avvikListe.get(2).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE),

        () -> assertThat(avvikListe.get(3).getAvvikTekst()).isEqualTo("inntektType " + InntektType.PENSJON_KORR_BARNETILLEGG.toString() +
            " er kun gyldig fom. 2015-01-01 tom. 2015-12-31"),
        () -> assertThat(avvikListe.get(3).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_PERIODE),

        () -> assertThat(avvikListe.get(4).getAvvikTekst()).isEqualTo("inntektType " + InntektType.BARNETRYGD_MAN_VURDERING.toString() +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG.toString() + " og rolle " + Rolle.SOKNADSBARN.toString()),
        () -> assertThat(avvikListe.get(4).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE)
    );
    printAvvikListe(avvikListe);
  }

  @Test
  @DisplayName("Test justering av inntekter BP")
  void testJusteringAvInntekterBP() {

    lagGrunnlagMedInntekterTilJustering("BP");
    var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(200000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(150000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(150000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(300000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektBelop())
            .isEqualTo(100000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(300000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektBelop())
            .isEqualTo(100000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(100000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektBelop())
            .isEqualTo(250000d)
    );
  }

  @Test
  @DisplayName("Test justering av inntekter BM")
  void testJusteringAvInntekterBM() {

    lagGrunnlagMedInntekterTilJustering("BM");
    var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(200000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(150000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(150000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(300000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektBelop())
            .isEqualTo(100000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(300000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektBelop())
            .isEqualTo(100000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(100000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektBelop())
            .isEqualTo(250000d)
    );
  }

  @Test
  @DisplayName("Test justering av inntekter BB")
  void testJusteringAvInntekterBB() {

    lagGrunnlagMedInntekterTilJustering("BB");
    var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(200000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(150000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPL_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(150000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(300000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektBelop())
            .isEqualTo(100000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(300000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektBelop())
            .isEqualTo(100000d),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPL),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(100000d),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektBelop())
            .isEqualTo(250000d)
    );
  }

  private void lagGrunnlag(String beregnDatoFra, String beregnDatoTil) {

    var soknadsbarnPersonId = 1;
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    var inntektBPPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBMPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBBPeriodeListe = new ArrayList<InntektPeriode>();

    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        1000d));

    inntektBPPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPL_ARBEIDSGIVER, 217666d));

    inntektBMPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPL_ARBEIDSGIVER, 400000d));

    inntektBBPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPL_ARBEIDSGIVER, 40000d));

    grunnlag = new BeregnBPsAndelUnderholdskostnadGrunnlag(LocalDate.parse(beregnDatoFra), LocalDate.parse(beregnDatoTil),
        soknadsbarnPersonId, underholdskostnadPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe,
        inntektBBPeriodeListe, lagSjablonGrunnlag());
  }

  private void lagGrunnlagMedAvvikUgyldigInntekt() {

    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");
    var soknadsbarnPersonId = 1;
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    var inntektBPPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBMPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBBPeriodeListe = new ArrayList<InntektPeriode>();

    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-01-01")),
        1000d));

    inntektBPPeriodeListe
        .add(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.LIGNING_KORRIGERT_BARNETILLEGG, 666001d));

    inntektBMPeriodeListe.add(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.PENSJON_KORR_BARNETILLEGG, 400000d));

    inntektBBPeriodeListe.add(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.BARNETRYGD_MAN_VURDERING, 40000d));

    grunnlag = new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId, underholdskostnadPeriodeListe,
        inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe, lagSjablonGrunnlag());
  }

  private void lagGrunnlagMedInntekterTilJustering(String rolle) {

    var beregnDatoFra = LocalDate.parse("2018-01-01");
    var beregnDatoTil = LocalDate.parse("2020-07-01");
    var soknadsbarnPersonId = 1;
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    List<InntektPeriode> inntektBPPeriodeListe;
    List<InntektPeriode> inntektBMPeriodeListe;
    List<InntektPeriode> inntektBBPeriodeListe;

    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(new Periode(beregnDatoFra, beregnDatoTil),1000d));

    if (rolle.equals("BP")) {
      inntektBPPeriodeListe = lagJustertInntektGrunnlag();
    } else {
      inntektBPPeriodeListe = singletonList(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.LIGNING_KORRIGERT_BARNETILLEGG, 666001d));
    }

    if (rolle.equals("BM")) {
      inntektBMPeriodeListe = lagJustertInntektGrunnlag();
    } else {
      inntektBMPeriodeListe = singletonList(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.PENSJON_KORR_BARNETILLEGG, 400000d));
    }

    if (rolle.equals("BB")) {
      inntektBBPeriodeListe = lagJustertInntektGrunnlag();
    } else {
      inntektBBPeriodeListe = singletonList(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.BARNETRYGD_MAN_VURDERING, 40000d));
    }

    grunnlag = new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId, underholdskostnadPeriodeListe,
        inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe, lagSjablonGrunnlag());
  }


  private List<SjablonPeriode> lagSjablonGrunnlag() {
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

    return sjablonPeriodeListe;
  }

  private List<InntektPeriode> lagJustertInntektGrunnlag() {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();

    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2018-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, 200000d));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2018-06-01"), LocalDate.parse("2018-12-31")), InntektType.INNTEKTSOPPL_ARBEIDSGIVER,
            150000d));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null), InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT, 300000d));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null), InntektType.KAPITALINNTEKT_EGNE_OPPL, 100000d));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2020-01-01"), null), InntektType.ATTFORING_AAP, 250000d));

    return inntektPeriodeListe;
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
