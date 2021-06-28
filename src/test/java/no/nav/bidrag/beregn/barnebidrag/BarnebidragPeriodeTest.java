package no.nav.bidrag.beregn.barnebidrag;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static no.nav.bidrag.beregn.TestUtil.ANDRE_LOPENDE_BIDRAG_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_BM_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_BP_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_FORSVARET_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.BIDRAGSEVNE_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.DELT_BOSTED_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidragPeriode;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BarnebidragPeriodeTest {

  public ArrayList<SjablonPeriode> sjablonPeriodeListe = new ArrayList<>();

  private final BarnebidragPeriode barnebidragPeriode = BarnebidragPeriode.getInstance();

  @Test
  @Disabled
  @DisplayName("Test med ett barn og splitt på bidragsevne")
  void enkelTestEttBarnToBidragsevner() {

    var beregnDatoFra = LocalDate.parse("2019-08-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    lagSjablonliste();

    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    bidragsevnePeriodeListe.add(new BidragsevnePeriode(BIDRAGSEVNE_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")), BigDecimal.valueOf(15000), BigDecimal.valueOf(16000)));
    bidragsevnePeriodeListe.add(new BidragsevnePeriode(BIDRAGSEVNE_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-10-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(17000), BigDecimal.valueOf(16000)));

    var bPsAndelUnderholdskostnadListe = singletonList(new BPsAndelUnderholdskostnadPeriode(1, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false));

    var samvaersfradragPeriodeListe = singletonList(new SamvaersfradragPeriode(1, SAMVAERSFRADRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO));

    var deltBostedPeriodeListe = singletonList(new DeltBostedPeriode(1, DELT_BOSTED_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), false));

    var barnetilleggBPPeriodeListe = singletonList(new BarnetilleggPeriode(1, BARNETILLEGG_BP_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    var barnetilleggBMPeriodeListe = singletonList(new BarnetilleggPeriode(1, BARNETILLEGG_BM_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    var barnetilleggForsvaretPeriodeListe = singletonList(new BarnetilleggForsvaretPeriode(BARNETILLEGG_FORSVARET_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), false));

    var andreLopendeBidragPeriodeListe = singletonList(new AndreLopendeBidragPeriode(ANDRE_LOPENDE_BIDRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), 1, BigDecimal.ZERO, BigDecimal.ZERO));

    var beregnBarnebidragGrunnlag = new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
        bPsAndelUnderholdskostnadListe, samvaersfradragPeriodeListe, deltBostedPeriodeListe, barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe,
        barnetilleggForsvaretPeriodeListe, andreLopendeBidragPeriodeListe, sjablonPeriodeListe);

    var resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(2),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-10-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop().compareTo(BigDecimal.valueOf(15000)))
            .isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getKode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().size()).isEqualTo(1),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-10-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getBelop().compareTo(BigDecimal.valueOf(16000)))
            .isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getKode())
            .isEqualTo(ResultatKode.KOSTNADSBEREGNET_BIDRAG)
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @Disabled
  @DisplayName("Test med tre barn i to perioder, to barn i periode 3, barnetilleggForsvaret i siste periode")
  void testVariabeltAntallBarn() {

    var beregnDatoFra = LocalDate.parse("2019-08-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    lagSjablonliste();

    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    var bPsAndelUnderholdskostnadListe = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriode>();
    var deltBostedPeriodeListe = new ArrayList<DeltBostedPeriode>();
    var barnetilleggBPPeriodeListe = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggBMPeriodeListe = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriode>();
    var andreLopendeBidragPeriodeListe = new ArrayList<AndreLopendeBidragPeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(BIDRAGSEVNE_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")), BigDecimal.valueOf(15000), BigDecimal.valueOf(16000)));
    bidragsevnePeriodeListe.add(new BidragsevnePeriode(BIDRAGSEVNE_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-10-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(17000), BigDecimal.valueOf(16000)));

    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(1, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")), BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false));
    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(2, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false));
    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(3, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false));

    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(1, SAMVAERSFRADRAG_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")), BigDecimal.ZERO));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(2, SAMVAERSFRADRAG_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-09-01")), BigDecimal.ZERO));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(2, SAMVAERSFRADRAG_REFERANSE + "_3",
        new Periode(LocalDate.parse("2019-09-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(1000)));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(3, SAMVAERSFRADRAG_REFERANSE + "_4",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO));

    deltBostedPeriodeListe.add(new DeltBostedPeriode(1, DELT_BOSTED_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")), false));
    deltBostedPeriodeListe.add(new DeltBostedPeriode(2, DELT_BOSTED_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), false));
    deltBostedPeriodeListe.add(new DeltBostedPeriode(3, DELT_BOSTED_REFERANSE + "_3",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), false));

    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(1, BARNETILLEGG_BP_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")), BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(2, BARNETILLEGG_BP_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(3, BARNETILLEGG_BP_REFERANSE + "_3",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(1, BARNETILLEGG_BM_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")), BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(2, BARNETILLEGG_BM_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(3, BARNETILLEGG_BM_REFERANSE + "_3",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    barnetilleggForsvaretPeriodeListe.add(new BarnetilleggForsvaretPeriode(BARNETILLEGG_FORSVARET_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-12-01")), false));
    barnetilleggForsvaretPeriodeListe.add(new BarnetilleggForsvaretPeriode(BARNETILLEGG_FORSVARET_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-12-01"), LocalDate.parse("2020-01-01")), true));

    andreLopendeBidragPeriodeListe.add(new AndreLopendeBidragPeriode(ANDRE_LOPENDE_BIDRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), 1, BigDecimal.ZERO, BigDecimal.ZERO));

    var beregnBarnebidragGrunnlag = new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
        bPsAndelUnderholdskostnadListe, samvaersfradragPeriodeListe, deltBostedPeriodeListe, barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe,
        barnetilleggForsvaretPeriodeListe, andreLopendeBidragPeriodeListe, sjablonPeriodeListe);

    var resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag);

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(4),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().size()).isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().size()).isEqualTo(3),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-10-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-10-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getKode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(1).getBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(2).getBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getKode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(1).getBelop()
            .compareTo(BigDecimal.valueOf(4000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(2).getBelop()
            .compareTo(BigDecimal.valueOf(5000))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(7000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatListe().get(0).getKode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatListe().get(1).getBelop()
            .compareTo(BigDecimal.valueOf(8000))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(3001))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatListe().get(0).getKode())
            .isEqualTo(ResultatKode.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatListe().get(1).getBelop()
            .compareTo(BigDecimal.valueOf(4001))).isZero()
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @Disabled
  @DisplayName("Test med to barn i to perioder, der det ene barnet har delt bosted. Ved delt bosted skal BPs andel av underholdskostnad"
      + "reduseres med 50 prosentpoeng. I periode 2 blir andelen regnet om til under 50% og bidrag skal ikke beregnes for "
      + "dette barnet og hele evnen skal gis til det andre barnet")
  void testDeltBosted() {

    var beregnDatoFra = LocalDate.parse("2019-08-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    lagSjablonliste();

    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    var bPsAndelUnderholdskostnadListe = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriode>();
    var deltBostedPeriodeListe = new ArrayList<DeltBostedPeriode>();
    var barnetilleggBPPeriodeListe = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggBMPeriodeListe = new ArrayList<BarnetilleggPeriode>();
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriode>();
    var andreLopendeBidragPeriodeListe = new ArrayList<AndreLopendeBidragPeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(BIDRAGSEVNE_REFERANSE,
        new Periode(LocalDate.parse("2018-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(1000), BigDecimal.valueOf(1600)));

    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(1, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-11-01")), BigDecimal.valueOf(40), BigDecimal.valueOf(4000), false));
    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(1, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-11-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(60), BigDecimal.valueOf(6000), false));
    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(2, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-11-01")), BigDecimal.valueOf(60), BigDecimal.valueOf(6000), false));
    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnadPeriode(2, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_4",
        new Periode(LocalDate.parse("2019-11-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(40), BigDecimal.valueOf(4000), false));

    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(1, SAMVAERSFRADRAG_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO));
    samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(2, SAMVAERSFRADRAG_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO));

    deltBostedPeriodeListe.add(new DeltBostedPeriode(1, DELT_BOSTED_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), false));
    deltBostedPeriodeListe.add(new DeltBostedPeriode(2, DELT_BOSTED_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), true));

    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(1, BARNETILLEGG_BP_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(2, BARNETILLEGG_BP_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(1, BARNETILLEGG_BM_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));
    barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(2, BARNETILLEGG_BM_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    barnetilleggForsvaretPeriodeListe.add(new BarnetilleggForsvaretPeriode(BARNETILLEGG_FORSVARET_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), false));

    andreLopendeBidragPeriodeListe.add(new AndreLopendeBidragPeriode(ANDRE_LOPENDE_BIDRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), 1, BigDecimal.ZERO, BigDecimal.ZERO));

    var beregnBarnebidragGrunnlag = new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
        bPsAndelUnderholdskostnadListe, samvaersfradragPeriodeListe, deltBostedPeriodeListe, barnetilleggBPPeriodeListe,
        barnetilleggBMPeriodeListe, barnetilleggForsvaretPeriodeListe, andreLopendeBidragPeriodeListe, sjablonPeriodeListe);

    var resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag);

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().size()).isEqualTo(2),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-11-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-11-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2020-01-01")),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(800))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(1).getBelop()
            .compareTo(BigDecimal.valueOf(200))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getGrunnlag().getGrunnlagPerBarnListe().get(1)
            .getBPsAndelUnderholdskostnad().getAndelProsent().compareTo(BigDecimal.valueOf(10))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getKode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(1).getBelop()
            .compareTo(BigDecimal.ZERO)).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(0).getKode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getGrunnlag().getGrunnlagPerBarnListe().get(1)
            .getBPsAndelUnderholdskostnad().getAndelProsent().compareTo(BigDecimal.ZERO)).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatListe().get(1).getKode())
            .isEqualTo(ResultatKode.BARNEBIDRAG_IKKE_BEREGNET_DELT_BOSTED)
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test med delt bosted med BPs andel av underholdskostnad < 50%")
  void testDeltBostedEttBarnAndelUnderFemtiProsent() {

    var beregnDatoFra = LocalDate.parse("2019-08-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    lagSjablonliste();

    var bidragsevnePeriodeListe = singletonList(new BidragsevnePeriode(BIDRAGSEVNE_REFERANSE,
        new Periode(LocalDate.parse("2018-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(5603), BigDecimal.valueOf(8334)));

    var bPsAndelUnderholdskostnadListe = singletonList(new BPsAndelUnderholdskostnadPeriode(1, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(48.4), BigDecimal.valueOf(4203), false));

    var samvaersfradragPeriodeListe = singletonList(new SamvaersfradragPeriode(1, SAMVAERSFRADRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO));

    var deltBostedPeriodeListe = singletonList(new DeltBostedPeriode(1, DELT_BOSTED_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), true));

    var barnetilleggBPPeriodeListe = singletonList(new BarnetilleggPeriode(1, BARNETILLEGG_BP_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    var barnetilleggBMPeriodeListe = singletonList(new BarnetilleggPeriode(1, BARNETILLEGG_BM_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    var barnetilleggForsvaretPeriodeListe = singletonList(new BarnetilleggForsvaretPeriode(BARNETILLEGG_FORSVARET_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), false));

    var andreLopendeBidragPeriodeListe = singletonList(new AndreLopendeBidragPeriode(ANDRE_LOPENDE_BIDRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), 1, BigDecimal.ZERO, BigDecimal.ZERO));

    var beregnBarnebidragGrunnlag = new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
        bPsAndelUnderholdskostnadListe, samvaersfradragPeriodeListe, deltBostedPeriodeListe, barnetilleggBPPeriodeListe,
        barnetilleggBMPeriodeListe, barnetilleggForsvaretPeriodeListe, andreLopendeBidragPeriodeListe, sjablonPeriodeListe);

    var resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag);

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(0))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getKode())
            .isEqualTo(ResultatKode.BARNEBIDRAG_IKKE_BEREGNET_DELT_BOSTED)
    );

    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Test med delt bosted med BPs andel av underholdskostnad > 50%")
  void testDeltBostedEttBarnAndelOverFemtiProsent() {

    var beregnDatoFra = LocalDate.parse("2019-08-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    lagSjablonliste();

    var bidragsevnePeriodeListe = singletonList(new BidragsevnePeriode(BIDRAGSEVNE_REFERANSE,
        new Periode(LocalDate.parse("2018-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(359), BigDecimal.valueOf(11458)));

    var bPsAndelUnderholdskostnadListe = singletonList(new BPsAndelUnderholdskostnadPeriode(1, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(56.4), BigDecimal.valueOf(4898), false));

    var samvaersfradragPeriodeListe = singletonList(new SamvaersfradragPeriode(1, SAMVAERSFRADRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO));

    var deltBostedPeriodeListe = singletonList(new DeltBostedPeriode(1, DELT_BOSTED_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), true));

    var barnetilleggBPPeriodeListe = singletonList(new BarnetilleggPeriode(1, BARNETILLEGG_BP_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    var barnetilleggBMPeriodeListe = singletonList(new BarnetilleggPeriode(1, BARNETILLEGG_BM_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    var barnetilleggForsvaretPeriodeListe = singletonList(new BarnetilleggForsvaretPeriode(BARNETILLEGG_FORSVARET_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), false));

    var andreLopendeBidragPeriodeListe = singletonList(new AndreLopendeBidragPeriode(ANDRE_LOPENDE_BIDRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), 1, BigDecimal.ZERO, BigDecimal.ZERO));

    var beregnBarnebidragGrunnlag = new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
        bPsAndelUnderholdskostnadListe, samvaersfradragPeriodeListe, deltBostedPeriodeListe, barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe,
        barnetilleggForsvaretPeriodeListe, andreLopendeBidragPeriodeListe, sjablonPeriodeListe);

    var resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag);

    assertAll(
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getBelop()
            .compareTo(BigDecimal.valueOf(360))).isZero(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatListe().get(0).getKode())
            .isEqualTo(ResultatKode.BIDRAG_REDUSERT_AV_EVNE)
    );

    printGrunnlagResultat(resultat);
  }

  @Test
  @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
  void testGrunnlagMedAvvik() {

    var beregnDatoFra = LocalDate.parse("2016-08-01");
    var beregnDatoTil = LocalDate.parse("2022-01-01");

    lagSjablonliste();

    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();

    bidragsevnePeriodeListe.add(new BidragsevnePeriode(BIDRAGSEVNE_REFERANSE + "_1",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2019-10-01")), BigDecimal.valueOf(15000), BigDecimal.valueOf(16000)));
    bidragsevnePeriodeListe.add(new BidragsevnePeriode(BIDRAGSEVNE_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-10-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(17000), BigDecimal.valueOf(16000)));

    var bPsAndelUnderholdskostnadListe = singletonList(new BPsAndelUnderholdskostnadPeriode(1, BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(80), BigDecimal.valueOf(16000), false));

    var samvaersfradragPeriodeListe = singletonList(new SamvaersfradragPeriode(1, SAMVAERSFRADRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO));

    var deltBostedPeriodeListe = singletonList(new DeltBostedPeriode(1, DELT_BOSTED_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), false));

    var barnetilleggBPPeriodeListe = singletonList(new BarnetilleggPeriode(1, BARNETILLEGG_BP_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    var barnetilleggBMPeriodeListe = singletonList(new BarnetilleggPeriode(1, BARNETILLEGG_BM_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), BigDecimal.ZERO, BigDecimal.ZERO));

    var barnetilleggForsvaretPeriodeListe = singletonList(new BarnetilleggForsvaretPeriode(BARNETILLEGG_FORSVARET_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), false));

    var andreLopendeBidragPeriodeListe = singletonList(new AndreLopendeBidragPeriode(ANDRE_LOPENDE_BIDRAG_REFERANSE,
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-01-01")), 1, BigDecimal.ZERO, BigDecimal.ZERO));

    var beregnBarnebidragGrunnlag = new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
        bPsAndelUnderholdskostnadListe, samvaersfradragPeriodeListe, deltBostedPeriodeListe, barnetilleggBPPeriodeListe,
        barnetilleggBMPeriodeListe, barnetilleggForsvaretPeriodeListe, andreLopendeBidragPeriodeListe, sjablonPeriodeListe);

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


  private void lagSjablonliste() {
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2021-06-30")),
        new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(5667))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2019-07-01"), LocalDate.parse("2021-06-30")),
        new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn(), emptyList(),
            Collections.singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(2334))))));
  }

  private void printGrunnlagResultat(BeregnBarnebidragResultat beregnBarnebidragResultat) {
    beregnBarnebidragResultat.getResultatPeriodeListe().stream()
        .sorted(comparing(pR -> pR.getPeriode().getDatoFom()))
        .forEach(sortedPR -> System.out.println("Dato fra: " + sortedPR.getPeriode().getDatoFom() + "; " +
            "Dato til: " + sortedPR.getPeriode().getDatoTil() + "; " + "Resultat: " + sortedPR.getResultatListe().get(0)));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
