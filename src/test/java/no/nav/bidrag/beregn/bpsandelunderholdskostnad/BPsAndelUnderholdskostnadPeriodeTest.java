package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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

  private final BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriode = BPsAndelUnderholdskostnadPeriode.getInstance();

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
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(35.2)),

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
        BigDecimal.valueOf(1000)));

    inntektBPPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")),
        InntektType.LONN_SKE, BigDecimal.valueOf(300000), false, false));

    inntektBMPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")),
        InntektType.LONN_SKE, BigDecimal.valueOf(400000), false, false));

    inntektBBPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")),
        InntektType.LONN_SKE, BigDecimal.valueOf(40000), false, false));

    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        BigDecimal.valueOf(1000)));

    inntektBPPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        InntektType.LONN_SKE, BigDecimal.valueOf(3000), false, false));

    inntektBMPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        InntektType.LONN_SKE, BigDecimal.valueOf(400000), false, false));

    inntektBBPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")),
        InntektType.LONN_SKE, BigDecimal.valueOf(4000000), false, false));

    // Lag sjabloner
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2008-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1600))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2008-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1700))))));

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
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(33.3)),

        // Gamle regler
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2008-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2009-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(33.3)),

        // Nye regler
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2009-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2009-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(42.9)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2009-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2009-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.ZERO)
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
        () -> assertThat(avvikListe).hasSize(4),

        () -> assertThat(avvikListe.get(0).getAvvikTekst()).isEqualTo("inntektType " + InntektType.SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG.toString() +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG.toString() + " og rolle " + Rolle.BIDRAGSPLIKTIG.toString()),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE),

        () -> assertThat(avvikListe.get(1).getAvvikTekst()).isEqualTo("inntektType " + InntektType.PENSJON_KORRIGERT_BARNETILLEGG.toString() +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG.toString() + " og rolle " + Rolle.BIDRAGSMOTTAKER.toString()),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE),

        () -> assertThat(avvikListe.get(2).getAvvikTekst()).isEqualTo("inntektType " + InntektType.PENSJON_KORRIGERT_BARNETILLEGG.toString() +
            " er kun gyldig fom. 2015-01-01 tom. 2016-01-01"),
        () -> assertThat(avvikListe.get(2).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_PERIODE),

        () -> assertThat(avvikListe.get(3).getAvvikTekst()).isEqualTo("inntektType " + InntektType.BARNETRYGD_MANUELL_VURDERING.toString() +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG.toString() + " og rolle " + Rolle.SOKNADSBARN.toString()),
        () -> assertThat(avvikListe.get(3).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE)
    );

    printAvvikListe(avvikListe);
  }

  @Test
  @DisplayName("Test justering av inntekter BP")
  void testJusteringAvInntekterBP() {

    lagGrunnlagMedInntekterTilJustering("BP", null, null);
    var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(200000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBPListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(250000))
    );
  }

  @Test
  @DisplayName("Test justering av inntekter BM")
  void testJusteringAvInntekterBM() {

    lagGrunnlagMedInntekterTilJustering("BM", null, null);
    var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(200000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(250000))
    );
  }

  @Test
  @DisplayName("Test justering av inntekter BB")
  void testJusteringAvInntekterBB() {

    lagGrunnlagMedInntekterTilJustering("BB", null, null);
    var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(6),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(200000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatGrunnlagBeregning().getInntektBBListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(250000))
    );
  }

  @Test
  @DisplayName("Test utvidet barnetrygd BM")
  void testUtvidetBarnetrygdBM() {

    lagGrunnlagMedInntekterTilJustering("BMUTV", LocalDate.parse("2019-01-01"), LocalDate.parse("2019-04-01"));
    var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().size()).isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektType())
            .isEqualTo(InntektType.PENSJON_KORRIGERT_BARNETILLEGG),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(0).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(1).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(12000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(2).getInntektType())
            .isEqualTo(InntektType.FORDEL_SAERFRADRAG_ENSLIG_FORSORGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlagBeregning().getInntektBMListe().get(2).getInntektBelop())
            .isEqualTo(BigDecimal.valueOf(13000))
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
        BigDecimal.valueOf(1000)));

    inntektBPPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(217666), false, false));

    inntektBMPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000), false, false));

    inntektBBPeriodeListe.add(new InntektPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(40000), false, false));

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
        BigDecimal.valueOf(1000)));

    inntektBPPeriodeListe
        .add(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG,
            BigDecimal.valueOf(666001), false, false));

    inntektBMPeriodeListe.add(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.PENSJON_KORRIGERT_BARNETILLEGG,
        BigDecimal.valueOf(400000), false, false));

    inntektBBPeriodeListe.add(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil), InntektType.BARNETRYGD_MANUELL_VURDERING,
        BigDecimal.valueOf(40000), false, false));

    grunnlag = new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId, underholdskostnadPeriodeListe,
        inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe, lagSjablonGrunnlag());
  }

  private void lagGrunnlagMedInntekterTilJustering(String rolle, LocalDate beregnDatoFra, LocalDate beregnDatoTil) {

    if (beregnDatoFra == null) {
      beregnDatoFra = LocalDate.parse("2018-01-01");
    }
    if (beregnDatoTil == null) {
      beregnDatoTil = LocalDate.parse("2020-07-01");
    }

    var soknadsbarnPersonId = 1;

    List<InntektPeriode> inntektBPPeriodeListe;
    List<InntektPeriode> inntektBMPeriodeListe;
    List<InntektPeriode> inntektBBPeriodeListe;

    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(new Periode(beregnDatoFra, beregnDatoTil), BigDecimal.valueOf(1000)));

    var sjablonPeriodeListe = lagSjablonGrunnlag();

    if (rolle.equals("BP")) {
      inntektBPPeriodeListe = lagJustertInntektGrunnlag();
    } else {
      inntektBPPeriodeListe = singletonList(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil),
          InntektType.SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG, BigDecimal.valueOf(666001), false, false));
    }

    if (rolle.equals("BM")) {
      inntektBMPeriodeListe = lagJustertInntektGrunnlag();
    } else if (rolle.equals("BMUTV")) {
      inntektBMPeriodeListe = Arrays.asList(
          new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil),
              InntektType.PENSJON_KORRIGERT_BARNETILLEGG, BigDecimal.valueOf(400000), false, false),
          new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil),
              InntektType.UTVIDET_BARNETRYGD, BigDecimal.valueOf(12000), false, false));
      sjablonPeriodeListe = lagSjablonGrunnlagUtvidetBarnetrygd();
    } else {
      inntektBMPeriodeListe = singletonList(new InntektPeriode(new Periode(beregnDatoFra, beregnDatoTil),
          InntektType.PENSJON_KORRIGERT_BARNETILLEGG, BigDecimal.valueOf(400000), false, false));
    }

    if (rolle.equals("BB")) {
      inntektBBPeriodeListe = lagJustertInntektGrunnlag();
    } else {
      inntektBBPeriodeListe = emptyList();
    }

    grunnlag = new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId, underholdskostnadPeriodeListe,
        inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe, sjablonPeriodeListe);
  }


  private List<SjablonPeriode> lagSjablonGrunnlag() {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1600))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1640))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2020-07-01"), null),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(1670))))));

    return sjablonPeriodeListe;
  }

  private List<SjablonPeriode> lagSjablonGrunnlagUtvidetBarnetrygd() {
    var sjablontallPeriodeListe = new ArrayList<SjablonPeriode>();

    // Sjablon 0030
    sjablontallPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_IKKE_I_SKATTEPOSISJON_BELOP.getNavn(), emptyList(), Collections.singletonList(
            new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(105000))))));

    // Sjablon 0031
    sjablontallPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new Sjablon(SjablonTallNavn.NEDRE_INNTEKTSGRENSE_FULL_SKATTEPOSISJON_BELOP.getNavn(), emptyList(), Collections.singletonList(
            new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(105000))))));

    // Sjablon 0039
    sjablontallPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new Sjablon(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn(), emptyList(), Collections.singletonList(
            new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(13000))))));

    return sjablontallPeriodeListe;
  }

  private List<InntektPeriode> lagJustertInntektGrunnlag() {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();

    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2018-01-01"), null),
            InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(200000), false, false));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2018-06-01"), LocalDate.parse("2019-01-01")),
            InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(150000), false, false));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null),
            InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT, BigDecimal.valueOf(300000), false, false));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null),
            InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER, BigDecimal.valueOf(100000), false, false));
    inntektPeriodeListe.add(
        new InntektPeriode(new Periode(LocalDate.parse("2020-01-01"), null),
            InntektType.ATTFORING_AAP, BigDecimal.valueOf(250000), false, false));

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
