package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_BB_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_BM_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_BP_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnetBPsAndelUnderholdskostnadResultat;
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

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getAndelProsent())
            .isEqualTo(BigDecimal.valueOf(35.2)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-07-01")),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2020-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isNull()
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test av beregning med gamle og nye regler. Resultat for perioder før 2009 skal angis i nærmeste sjettedel."
      + "Det skal også lages brudd i periode ved overgang til nye regler 01.01.2009")
  void testBeregningMedGamleOgNyeRegler() {

    var beregnDatoFra = LocalDate.parse("2008-01-01");
    var beregnDatoTil = LocalDate.parse("2009-07-01");
    var soknadsbarnPersonId = 1;

    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    var inntektBPPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBMPeriodeListe = new ArrayList<InntektPeriode>();
    var inntektBBPeriodeListe = new ArrayList<InntektPeriode>();

    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(UNDERHOLDSKOSTNAD_REFERANSE + "_1",
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")), BigDecimal.valueOf(1000)));
    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(UNDERHOLDSKOSTNAD_REFERANSE + "_2",
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")), BigDecimal.valueOf(1000)));

    inntektBPPeriodeListe.add(new InntektPeriode(INNTEKT_BP_REFERANSE + "_1",
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")), InntektType.LONN_SKE, BigDecimal.valueOf(300000), false, false));
    inntektBPPeriodeListe.add(new InntektPeriode(INNTEKT_BP_REFERANSE + "_2",
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")), InntektType.LONN_SKE, BigDecimal.valueOf(3000), false, false));

    inntektBMPeriodeListe.add(new InntektPeriode(INNTEKT_BM_REFERANSE + "_1",
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")), InntektType.LONN_SKE, BigDecimal.valueOf(400000), false, false));
    inntektBMPeriodeListe.add(new InntektPeriode(INNTEKT_BM_REFERANSE + "_2",
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")), InntektType.LONN_SKE, BigDecimal.valueOf(400000), false, false));

    inntektBBPeriodeListe.add(new InntektPeriode(INNTEKT_BB_REFERANSE + "_1",
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2009-06-01")), InntektType.LONN_SKE, BigDecimal.valueOf(40000), false, false));
    inntektBBPeriodeListe.add(new InntektPeriode(INNTEKT_BB_REFERANSE + "_2",
        new Periode(LocalDate.parse("2009-06-01"), LocalDate.parse("2020-08-01")), InntektType.LONN_SKE, BigDecimal.valueOf(4000000), false, false));

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2008-01-01"), LocalDate.parse("2008-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1600))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2008-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1700))))));

    BeregnBPsAndelUnderholdskostnadGrunnlag grunnlag = new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId,
        underholdskostnadPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe, sjablonPeriodeListe);

    var resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(4),

        // Gamle regler
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2008-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2008-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getAndelProsent()).isEqualTo(BigDecimal.valueOf(33.3)),

        // Gamle regler
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2008-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2009-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getAndelProsent()).isEqualTo(BigDecimal.valueOf(33.3)),

        // Nye regler
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2009-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2009-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultat().getAndelProsent()).isEqualTo(BigDecimal.valueOf(42.9)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2009-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2009-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultat().getAndelProsent()).isEqualTo(BigDecimal.ZERO)
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
        () -> assertThat(avvikListe.get(0).getAvvikType())
            .isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i inntektBPPeriodeListe (2020-08-01) er før beregnDatoTil (2021-01-01)"),
        () -> assertThat(avvikListe.get(1).getAvvikType())
            .isEqualTo(AvvikType.PERIODE_MANGLER_DATA)
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

        () -> assertThat(avvikListe.get(0).getAvvikTekst()).isEqualTo("inntektType " + InntektType.SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG + " og rolle " + Rolle.BIDRAGSPLIKTIG),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE),

        () -> assertThat(avvikListe.get(1).getAvvikTekst()).isEqualTo("inntektType " + InntektType.PENSJON_KORRIGERT_BARNETILLEGG +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG + " og rolle " + Rolle.BIDRAGSMOTTAKER),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_TYPE),

        () -> assertThat(avvikListe.get(2).getAvvikTekst()).isEqualTo("inntektType " + InntektType.PENSJON_KORRIGERT_BARNETILLEGG +
            " er kun gyldig fom. 2015-01-01 tom. 2016-01-01"),
        () -> assertThat(avvikListe.get(2).getAvvikType()).isEqualTo(AvvikType.UGYLDIG_INNTEKT_PERIODE),

        () -> assertThat(avvikListe.get(3).getAvvikTekst()).isEqualTo("inntektType " + InntektType.BARNETRYGD_MANUELL_VURDERING +
            " er ugyldig for søknadstype " + SoknadType.BIDRAG + " og rolle " + Rolle.SOKNADSBARN),
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

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBPListe().size())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBPListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBPListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(200000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektBPListe().size())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektBPListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektBPListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektBPListe().size())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektBPListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektBPListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBPListe().size())
            .isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBPListe().get(0).getType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBPListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBPListe().get(1).getType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBPListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBPListe().size())
            .isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBPListe().get(0).getType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBPListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBPListe().get(1).getType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBPListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoTil())
            .isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBPListe().size())
            .isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBPListe().get(0).getType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBPListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBPListe().get(1).getType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBPListe().get(1).getBelop())
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

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBMListe().size())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBMListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBMListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(200000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektBMListe().size())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektBMListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektBMListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektBMListe().size())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektBMListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektBMListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBMListe().size())
            .isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBMListe().get(0).getType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBMListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBMListe().get(1).getType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBMListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBMListe().size())
            .isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBMListe().get(0).getType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBMListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBMListe().get(1).getType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBMListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBMListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBMListe().get(0).getType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBMListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBMListe().get(1).getType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBMListe().get(1).getBelop())
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

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBBListe().size())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBBListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBBListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(200000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-06-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektBBListe().size())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektBBListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getInntektBBListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektBBListe().size())
            .isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektBBListe().get(0).getType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getGrunnlag().getInntektBBListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(150000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBBListe().size())
            .isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBBListe().get(0).getType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBBListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBBListe().get(1).getType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getGrunnlag().getInntektBBListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBBListe().size())
            .isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBBListe().get(0).getType())
            .isEqualTo(InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBBListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(300000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBBListe().get(1).getType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getGrunnlag().getInntektBBListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getPeriode().getDatoTil())
            .isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBBListe().size())
            .isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBBListe().get(0).getType())
            .isEqualTo(InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBBListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(100000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBBListe().get(1).getType())
            .isEqualTo(InntektType.ATTFORING_AAP),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getGrunnlag().getInntektBBListe().get(1).getBelop())
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

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBMListe().size())
            .isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBMListe().get(0).getType())
            .isEqualTo(InntektType.PENSJON_KORRIGERT_BARNETILLEGG),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBMListe().get(0).getBelop())
            .isEqualTo(BigDecimal.valueOf(400000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBMListe().get(1).getType())
            .isEqualTo(InntektType.UTVIDET_BARNETRYGD),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBMListe().get(1).getBelop())
            .isEqualTo(BigDecimal.valueOf(12000)),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBMListe().get(2).getType())
            .isEqualTo(InntektType.FORDEL_SAERFRADRAG_ENSLIG_FORSORGER),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getInntektBMListe().get(2).getBelop())
            .isEqualTo(BigDecimal.valueOf(13000))
    );
  }

  private void lagGrunnlag(String beregnDatoFra, String beregnDatoTil) {

    var soknadsbarnPersonId = 1;

    var underholdskostnadPeriodeListe = singletonList(new UnderholdskostnadPeriode(UNDERHOLDSKOSTNAD_REFERANSE,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")), BigDecimal.valueOf(1000)));

    var inntektBPPeriodeListe = singletonList(new InntektPeriode(INNTEKT_BP_REFERANSE,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")), InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        BigDecimal.valueOf(217666), false, false));

    var inntektBMPeriodeListe = singletonList(new InntektPeriode(INNTEKT_BM_REFERANSE,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")), InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        BigDecimal.valueOf(400000), false, false));

    var inntektBBPeriodeListe = singletonList(new InntektPeriode(INNTEKT_BB_REFERANSE,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")), InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        BigDecimal.valueOf(40000), false, false));

    grunnlag = new BeregnBPsAndelUnderholdskostnadGrunnlag(LocalDate.parse(beregnDatoFra), LocalDate.parse(beregnDatoTil),
        soknadsbarnPersonId, underholdskostnadPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe,
        lagSjablonGrunnlag());
  }

  private void lagGrunnlagMedAvvikUgyldigInntekt() {

    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");
    var soknadsbarnPersonId = 1;

    var underholdskostnadPeriodeListe = singletonList(new UnderholdskostnadPeriode(UNDERHOLDSKOSTNAD_REFERANSE,
        new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(1000)));

    var inntektBPPeriodeListe = singletonList(new InntektPeriode(INNTEKT_BP_REFERANSE,
        new Periode(beregnDatoFra, beregnDatoTil), InntektType.SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG, BigDecimal.valueOf(666001), false, false));

    var inntektBMPeriodeListe = singletonList(new InntektPeriode(INNTEKT_BM_REFERANSE,
        new Periode(beregnDatoFra, beregnDatoTil), InntektType.PENSJON_KORRIGERT_BARNETILLEGG, BigDecimal.valueOf(400000), false, false));

    var inntektBBPeriodeListe = singletonList(new InntektPeriode(INNTEKT_BB_REFERANSE,
        new Periode(beregnDatoFra, beregnDatoTil), InntektType.BARNETRYGD_MANUELL_VURDERING, BigDecimal.valueOf(40000), false, false));

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

    var underholdskostnadPeriodeListe = singletonList(new UnderholdskostnadPeriode(UNDERHOLDSKOSTNAD_REFERANSE,
        new Periode(beregnDatoFra, beregnDatoTil), BigDecimal.valueOf(1000)));

    var sjablonPeriodeListe = lagSjablonGrunnlag();

    var inntektBPPeriodeListe = rolle.equals("BP") ? lagJustertInntektGrunnlag() :
        singletonList(new InntektPeriode(INNTEKT_BP_REFERANSE, new Periode(beregnDatoFra, beregnDatoTil),
            InntektType.SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG, BigDecimal.valueOf(666001), false, false));

    List<InntektPeriode> inntektBMPeriodeListe;
    if (rolle.equals("BM")) {
      inntektBMPeriodeListe = lagJustertInntektGrunnlag();
    } else if (rolle.equals("BMUTV")) {
      inntektBMPeriodeListe = asList(
          new InntektPeriode(INNTEKT_BM_REFERANSE + "_1", new Periode(beregnDatoFra, beregnDatoTil),
              InntektType.PENSJON_KORRIGERT_BARNETILLEGG, BigDecimal.valueOf(400000), false, false),
          new InntektPeriode(INNTEKT_BM_REFERANSE + "_2", new Periode(beregnDatoFra, beregnDatoTil),
              InntektType.UTVIDET_BARNETRYGD, BigDecimal.valueOf(12000), false, false));
      sjablonPeriodeListe = lagSjablonGrunnlagUtvidetBarnetrygd();
    } else {
      inntektBMPeriodeListe = singletonList(new InntektPeriode(INNTEKT_BM_REFERANSE, new Periode(beregnDatoFra, beregnDatoTil),
          InntektType.PENSJON_KORRIGERT_BARNETILLEGG, BigDecimal.valueOf(400000), false, false));
    }

    List<InntektPeriode> inntektBBPeriodeListe;
    inntektBBPeriodeListe = rolle.equals("BB") ? lagJustertInntektGrunnlag() : emptyList();

    grunnlag = new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId, underholdskostnadPeriodeListe,
        inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe, sjablonPeriodeListe);
  }


  private List<SjablonPeriode> lagSjablonGrunnlag() {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    // Sjablontall
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1600))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1640))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2020-07-01"), null),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1670))))));

    return sjablonPeriodeListe;
  }

  private List<SjablonPeriode> lagSjablonGrunnlagUtvidetBarnetrygd() {
    var sjablontallPeriodeListe = new ArrayList<SjablonPeriode>();

    // Sjablon 0030
    sjablontallPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_IKKE_I_SKATTEPOSISJON_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(105000))))));

    // Sjablon 0031
    sjablontallPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new Sjablon(SjablonTallNavn.NEDRE_INNTEKTSGRENSE_FULL_SKATTEPOSISJON_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(105000))))));

    // Sjablon 0039
    sjablontallPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new Sjablon(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(13000))))));

    return sjablontallPeriodeListe;
  }

  private List<InntektPeriode> lagJustertInntektGrunnlag() {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();

    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_1",
        new Periode(LocalDate.parse("2018-01-01"), null), InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        BigDecimal.valueOf(200000), false, false));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_2",
        new Periode(LocalDate.parse("2018-06-01"), LocalDate.parse("2019-01-01")), InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER,
        BigDecimal.valueOf(150000), false, false));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_3",
        new Periode(LocalDate.parse("2019-01-01"), null), InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT,
        BigDecimal.valueOf(300000), false, false));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_4",
        new Periode(LocalDate.parse("2019-01-01"), null), InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER,
        BigDecimal.valueOf(100000), false, false));
    inntektPeriodeListe.add(new InntektPeriode(INNTEKT_REFERANSE + "_5",
        new Periode(LocalDate.parse("2020-01-01"), null), InntektType.ATTFORING_AAP,
        BigDecimal.valueOf(250000), false, false));

    return inntektPeriodeListe;
  }

  private void printGrunnlagResultat(BeregnetBPsAndelUnderholdskostnadResultat beregnetBPsAndelUnderholdskostnadResultat) {
    beregnetBPsAndelUnderholdskostnadResultat.getResultatPeriodeListe().stream()
        .sorted(comparing(pR -> pR.getPeriode().getDatoFom()))
        .forEach(sortedPR -> System.out.println("Dato fra: " + sortedPR.getPeriode().getDatoFom() + "; " +
            "Dato til: " + sortedPR.getPeriode().getDatoTil() + "; " + "Prosentandel: " + sortedPR.getResultat().getAndelProsent()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
