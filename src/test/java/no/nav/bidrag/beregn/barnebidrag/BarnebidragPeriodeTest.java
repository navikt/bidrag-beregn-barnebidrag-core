package no.nav.bidrag.beregn.barnebidrag;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaretPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBostedPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BarnebidragPeriodeTest {

  private final BarnebidragPeriode barnebidragPeriode = BarnebidragPeriode.getInstance();

  public ArrayList<SjablonPeriode> sjablonPeriodeListe = new ArrayList<>();

  @Test
  @DisplayName("Test med ett barn og splitt på bidragsevne")
  void enkelTestEttBarnToBidragsevner() {

    LocalDate beregnDatoFra = LocalDate.parse("2019-08-01");
    LocalDate beregnDatoTil = LocalDate.parse("2020-01-01");

    lagSjablonliste();

    var bidragsevnePeriodeListe           = new ArrayList<BidragsevnePeriode>();
    var bPsAndelUnderholdskostnadListe    = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    var samvaersfradragPeriodeListe       = new ArrayList<SamvaersfradragPeriode>();
    var deltBostedPeriodeListe            = new ArrayList<DeltBostedPeriode>();
    var barnetilleggBPPeriodeListe        = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggBMPeriodeListe        = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")),
        BigDecimal.valueOf(15000), BigDecimal.valueOf(16000)));
    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2019-10-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(17000), BigDecimal.valueOf(16000)));
    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO));
    deltBostedPeriodeListe.add(new DeltBostedPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        false));
    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));
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
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-10-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-10-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(15000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatkode()).isEqualTo(
            ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(0).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(16000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(0).getResultatkode()).isEqualTo(
            ResultatKode.KOSTNADSBEREGNET_BIDRAG)
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test med tre barn i to perioder, to barn i periode 3, barnetilleggForsvaret i siste periode")
  void testVariabeltAntallBarn() {

    LocalDate beregnDatoFra = LocalDate.parse("2019-08-01");
    LocalDate beregnDatoTil = LocalDate.parse("2020-01-01");

    lagSjablonliste();

    var bidragsevnePeriodeListe           = new ArrayList<BidragsevnePeriode>();
    var bPsAndelUnderholdskostnadListe    = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    var samvaersfradragPeriodeListe       = new ArrayList<SamvaersfradragPeriode>();
    var deltBostedPeriodeListe            = new ArrayList<DeltBostedPeriode>();
    var barnetilleggBPPeriodeListe        = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggBMPeriodeListe        = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")),
        BigDecimal.valueOf(15000), BigDecimal.valueOf(16000)));
    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2019-10-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(17000), BigDecimal.valueOf(16000)));

    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(2,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(2,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-09-01")),
        BigDecimal.ZERO));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(2,
        new Periode(LocalDate.parse("2019-09-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(1000)));
    deltBostedPeriodeListe.add(new DeltBostedPeriode(2,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        false));
    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(2,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(2,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));

    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")),
        BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")),
        BigDecimal.ZERO));
    deltBostedPeriodeListe.add(new DeltBostedPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")),
        false));
    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));

    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(3,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(3,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO));
    deltBostedPeriodeListe.add(new DeltBostedPeriode(3,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        false));
    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(3,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(3,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));

    barnetilleggForsvaretPeriodeListe.add(new BarnetilleggForsvaretPeriode(
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-12-01")),
        false));
    barnetilleggForsvaretPeriodeListe.add(new BarnetilleggForsvaretPeriode(
        new Periode(LocalDate.parse("2019-12-01"), LocalDate.parse("2020-01-01")),
        true));
    BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag =
        new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
            bPsAndelUnderholdskostnadListe, samvaersfradragPeriodeListe, deltBostedPeriodeListe,
            barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe, barnetilleggForsvaretPeriodeListe,
            sjablonPeriodeListe);

    var resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag);

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(4),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().size()).isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().size()).isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregningListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregningListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-10-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-10-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(1).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(2).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatkode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(0).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(1).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(4000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(2).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(0).getResultatkode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregningListe().get(0).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(7000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregningListe().get(1).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(8000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregningListe().get(0).getResultatkode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregningListe().get(0).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(3001))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregningListe().get(1).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(4001))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregningListe().get(0).getResultatkode())
            .isEqualTo(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET)

    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test med to barn i to perioder, der det ene barnet har delt bosted. Ved delt bosted skal BPs andel av underholdskostnad"
      + "reduseres med 50 prosentpoeng. I periode 2 blir andelen regnet om til under 50% og bidrag skal ikke beregnes for "
      + "dette barnet og hele evnen skal gis til det andre barnet")
  void testDeltBosted() {

    LocalDate beregnDatoFra = LocalDate.parse("2019-08-01");
    LocalDate beregnDatoTil = LocalDate.parse("2020-01-01");

    lagSjablonliste();

    var bidragsevnePeriodeListe           = new ArrayList<BidragsevnePeriode>();
    var bPsAndelUnderholdskostnadListe    = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    var samvaersfradragPeriodeListe       = new ArrayList<SamvaersfradragPeriode>();
    var deltBostedPeriodeListe            = new ArrayList<DeltBostedPeriode>();
    var barnetilleggBPPeriodeListe        = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggBMPeriodeListe        = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2018-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(1000), BigDecimal.valueOf(1600)));

    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-11-01")),
        BigDecimal.valueOf(40), BigDecimal.valueOf(4000), false));
    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(1,
        new Periode(LocalDate.parse("2019-11-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(60), BigDecimal.valueOf(6000), false));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO));
    deltBostedPeriodeListe.add(new DeltBostedPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        false));
    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));

    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(2,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-11-01")),
        BigDecimal.valueOf(60), BigDecimal.valueOf(6000), false));
    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(2,
        new Periode(LocalDate.parse("2019-11-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(40), BigDecimal.valueOf(4000), false));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(2,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO));
    deltBostedPeriodeListe.add(new DeltBostedPeriode(2,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        true));
    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(2,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(2,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));

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
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-11-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-11-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(800))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(1).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(200))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatGrunnlag().getGrunnlagPerBarnListe().get(1)
            .getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadProsent().compareTo(BigDecimal.valueOf(10))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregningListe().get(0).getResultatkode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(0).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(1).getResultatBarnebidragBelop()
            .compareTo(BigDecimal.ZERO)).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(0).getResultatkode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatGrunnlag().getGrunnlagPerBarnListe().get(1)
            .getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadProsent().compareTo(BigDecimal.ZERO)).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregningListe().get(1).getResultatkode())
            .isEqualTo(ResultatKode.DELT_BOSTED)
    );

    printGrunnlagResultat(resultat);
  }

  private void lagSjablonliste(){
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2021-06-30")),
        new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(5667))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2021-06-30")),
        new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                BigDecimal.valueOf(2334))))));
  }


  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

//    lagGrunnlag("2016-01-01", "2021-01-01");
    LocalDate beregnDatoFra = LocalDate.parse("2016-08-01");
    LocalDate beregnDatoTil = LocalDate.parse("2022-01-01");

    lagSjablonliste();

    var bidragsevnePeriodeListe           = new ArrayList<BidragsevnePeriode>();
    var bPsAndelUnderholdskostnadListe    = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    var samvaersfradragPeriodeListe       = new ArrayList<SamvaersfradragPeriode>();
    var deltBostedPeriodeListe            = new ArrayList<DeltBostedPeriode>();
    var barnetilleggBPPeriodeListe        = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggBMPeriodeListe        = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")),
        BigDecimal.valueOf(15000), BigDecimal.valueOf(16000)));
    bidragsevnePeriodeListe.add(new BidragsevnePeriode(
        new Periode(LocalDate.parse("2019-10-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(17000), BigDecimal.valueOf(16000)));
    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO));
    deltBostedPeriodeListe.add(new DeltBostedPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        false));
    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(1,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggForsvaretPeriodeListe.add(new BarnetilleggForsvaretPeriode(
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")),
        false));
    BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag =
        new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
            bPsAndelUnderholdskostnadListe, samvaersfradragPeriodeListe, deltBostedPeriodeListe,
            barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe, barnetilleggForsvaretPeriodeListe,
            sjablonPeriodeListe);

    var avvikListe = barnebidragPeriode.validerInput(beregnBarnebidragGrunnlag);

    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Første dato i bidragsevnePeriodeListe (2019-08-01) er etter beregnDatoFra (2016-08-01)"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODE_MANGLER_DATA),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("Siste dato i bidragsevnePeriodeListe (2020-01-01) er før beregnDatoTil (2022-01-01)"),
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
                + "; " + "Resultat: " + sortedPR.getResultatBeregningListe().get(0)));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
