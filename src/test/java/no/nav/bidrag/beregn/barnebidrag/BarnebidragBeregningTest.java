package no.nav.bidrag.beregn.barnebidrag;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidrag;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaret;
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne;
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBosted;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.Samvaersfradrag;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeBarnebidrag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av barnebidrag")
public class BarnebidragBeregningTest {

  private final List<SjablonPeriode> sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

  private final BarnebidragBeregning barnebidragBeregning = BarnebidragBeregning.Companion.getInstance();

  @DisplayName("Beregner ved full evne, ett barn, ingen barnetillegg")
  @Test
  void testBeregningEttBarnMedFullEvneIngenBarnetillegg() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE, false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(8000))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG)
    );
  }

  @DisplayName("Beregner ved full evne, ett barn, og barnetillegg for BP der barnetillegg er høyere enn beregnet bidrag"
      + "Endelig bidrag skal da settes likt barnetillegg for BP")
  @Test
  void testBeregning1BarnFullEvneBarnetilleggBP() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(80), BigDecimal.valueOf(1000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf((100))),
        new DeltBosted(DELT_BOSTED_REFERANSE, false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.valueOf(1700), BigDecimal.valueOf(10)),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(1430))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_BP)
    );
  }

  @DisplayName("Beregner ved full evne, to barn")
  @Test
  void testBeregning2BarnFullEvne() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.valueOf(0.80), BigDecimal.valueOf(7000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(20000), BigDecimal.valueOf(20000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(8000))).isZero(),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(7000))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG)
    );
  }

  @DisplayName("Beregner for tre barn med for lav bidragsevne")
  @Test
  void testBeregning3BarnBegrensetAvBidragsevne() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(5000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.valueOf(0.80), BigDecimal.valueOf(3000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        3,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3", BigDecimal.valueOf(0.80), BigDecimal.valueOf(2000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_3", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_3", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_3", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_3", BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(8000), BigDecimal.valueOf(12000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(4000))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(2400))).isZero(),
        () -> assertThat(resultat.get(1).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(2).getBelop().compareTo(BigDecimal.valueOf(1600))).isZero(),
        () -> assertThat(resultat.get(2).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE)
    );
  }

  @DisplayName("Beregner ved manglende evne, ett barn")
  @Test
  void testBeregning1BarnIkkeFullEvne() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE, false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(1000), BigDecimal.valueOf(2000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE)
    );
  }


  @DisplayName("Beregner ved manglende evne, to barn")
  @Test
  void testBeregning2BarnIkkeFullEvne() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.valueOf(0.80), BigDecimal.valueOf(7000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(20000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(5330))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(4670))).isZero(),
        () -> assertThat(resultat.get(1).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE)
    );
  }

  @DisplayName("Beregner for tre barn som begrenses av 25%-regel")
  @Test
  void testBeregning3BarnBegrensetAv25ProsentAvInntekt() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(5000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.valueOf(0.80), BigDecimal.valueOf(3000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        3,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3", BigDecimal.valueOf(0.80), BigDecimal.valueOf(2000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_3", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_3", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_3", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_3", BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(12000), BigDecimal.valueOf(8000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(4000))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(2400))).isZero(),
        () -> assertThat(resultat.get(1).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT),
        () -> assertThat(resultat.get(2).getBelop().compareTo(BigDecimal.valueOf(1600))).isZero(),
        () -> assertThat(resultat.get(2).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT),

        () -> assertThat(resultat.get(0).getBelop()
            .add(resultat.get(1).getBelop())
            .add(resultat.get(2).getBelop())
            .compareTo(BigDecimal.valueOf(8000))).isZero()
    );
  }

  @DisplayName("Beregner at bidrag settes likt underholdskostnad minus nettobarnetilleggBM. Dette skjer "
      + "når beregnet bidrag er høyere enn underholdskostnad minus netto barnetillegg for BM. "
      + "Det skal trekkes fra for samvær også her ")
  @Test
  void testBeregningBidragSettesLiktBarnetilleggBM() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.80), BigDecimal.valueOf(1000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf(50)),
        new DeltBosted(DELT_BOSTED_REFERANSE, false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.valueOf(1000), BigDecimal.valueOf(10))));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(8000), BigDecimal.valueOf(12000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(300))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM)
    );
  }

  @DisplayName("Beregner at bidrag settes likt barnetilleggBP der det også finnes barnetillegg BM."
      + "Barnetillegg for BP overstyrer barnetilleggBM")
  @Test
  void testBeregningBidragSettesLiktBarnetilleggBP() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.80), BigDecimal.valueOf(200), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE, false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.valueOf(500), BigDecimal.valueOf(10)),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.valueOf(1000), BigDecimal.valueOf(10))));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(8000), BigDecimal.valueOf(12000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(450))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_BP)
    );
  }

  @DisplayName("Beregner at bidrag settes likt BPs andel av underholdskostnad minus samværsfradrag")
  @Test
  void testBeregningFradragSamvaer() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.80), BigDecimal.valueOf(2000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf(200)),
        new DeltBosted(DELT_BOSTED_REFERANSE, false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(8000), BigDecimal.valueOf(12000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(1800))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG)
    );
  }

  @DisplayName("Beregner for tre barn der barna har barnetillegg BP eller BM. "
      + "Bidrag settes likt underholdskostnad minus netto barnetilleggBM når beregnet bidrag er høyere enn"
      + "underholdskostnad minus netto barnetillegg for BM")
  @Test
  void testBeregning3BarnBarnetilleggBPogBM() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(400), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.valueOf(500), BigDecimal.valueOf(10)),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.valueOf(400), BigDecimal.valueOf(10))));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.valueOf(0.80), BigDecimal.valueOf(300), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.valueOf(100), BigDecimal.valueOf(10))));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(12000), BigDecimal.valueOf(8000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(450))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_BP),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(290))).isZero(),
        () -> assertThat(resultat.get(1).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM)
    );
  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for ett barn. Sjablonverdier for barnetillegg skal da overstyre"
      + "alt i beregningen")
  @Test
  void testBeregning1BarnBarnetilleggForsvaret() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf(1000)),
        new DeltBosted(DELT_BOSTED_REFERANSE, true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, true),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(4667))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET)
    );
  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for tre barn. Sjablonverdier for barnetillegg skal da overstyre"
      + "alt i beregningen")
  @Test
  void testBeregning3BarnBarnetilleggForsvaret() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        3,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_3", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_3", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_3", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_3", BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, true),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(2667))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET)
    );
  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for tre barn. Test på at samværsfradrag trekkes fra endelig bidragsbeløp")
  @Test
  void testBeregningBarnetilleggForsvaretFratrekkSamvaersfradrag() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.valueOf(1000)),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        3,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_3", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_3", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_3", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_3", BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, true),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(2667))).isZero(),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(1667))).isZero(),
        () -> assertThat(resultat.get(2).getBelop().compareTo(BigDecimal.valueOf(2667))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET)
    );
  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for elleve barn. Sjekker avrunding")
  @Test
  void testBeregning11BarnBarnetilleggForsvaret() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        3,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_3", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_3", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_3", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_3", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        4,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_4", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_4", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_4", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_4", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_4", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        5,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_5", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_5", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_5", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_5", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_5", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        6,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_6", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_6", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_6", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_6", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_6", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        7,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_7", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_7", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_7", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_7", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_7", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        8,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_8", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_8", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_8", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_8", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_8", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        9,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_9", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_9", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_9", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_9", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_9", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        10,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_10", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_10", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_10", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_10", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_10", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        11,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_11", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_11", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_11", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_11", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_11", BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, true),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(727))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET)
    );
  }

  @DisplayName("Beregner der delt bosted og barnetilleggBP er angitt, barnetillegget skal da ikke taes hensyn til")
  @Test
  void testBeregningDeltBostedOgBarnetilleggBP() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.valueOf(5000), BigDecimal.valueOf(10)),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(1000), BigDecimal.valueOf(1200)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(500))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(500))).isZero(),
        () -> assertThat(resultat.get(1).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE)
    );
  }

  @DisplayName("Beregner med to barn der det ene er selvforsørget, dvs har inntekt over 100 * sjablon for forhøyet forskudd."
      + "BPs andel skal da være 0, bidrag skal beregnes til 0 og resultatkode BARNET_ER_SELVFORSORGET skal angis")
  @Test
  void testBeregningSelvforsorgetBarn() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO, true),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.valueOf(5000), BigDecimal.valueOf(10)),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(1000), BigDecimal.valueOf(1200)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.ZERO)).isZero(),
        () -> assertThat(resultat.get(1).getKode()).isEqualTo(ResultatKodeBarnebidrag.BARNET_ER_SELVFORSORGET)
    );
  }

  @DisplayName("Resultatkode skal settes til BARNEBIDRAG_IKKE_BEREGNET_DELT_BOSTED ved delt bosted og "
      + "andel av U under 50%")
  @Test
  void testAtRiktigResultatkodeSettesVedDeltBostedOgAndelUnder50Prosent() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO, false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE, true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(12000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_IKKE_BEREGNET_DELT_BOSTED)
    );
  }

  @DisplayName("Resultatkode skal settes til DELT_BOSTED ved delt bosted og andel av U over 50% der "
      + "ingen andre faktorer har redusert bidraget ")
  @Test
  void testAtRiktigResultatkodeSettesVedDeltBostedOgAndelOver50Prosent() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE, true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(12000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.DELT_BOSTED)
    );
  }

  @DisplayName("Ved delt bosted og beregnet bidrag redusert av bidragsevne skal resultatkode reflektere"
      + "at det er lav evne")
  @Test
  void testAtRiktigResultatkodeSettesVedDeltBostedOgAndelOver50ProsentVedBegrensetEvne() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE, true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(1000), BigDecimal.valueOf(1200)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE),
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(1000))).isZero()
    );
  }

  @DisplayName("Tester at resultatkode angir forholdsmessig fordeling når evne ikke dekker ny sak pluss løpende bidrag")
  @Test
  void testBegrensetEvneGirForholdsmessigFordeling() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE, false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(12000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        singletonList(new AndreLopendeBidrag(ANDRE_LOPENDE_BIDRAG_REFERANSE, 2, BigDecimal.valueOf(1900), BigDecimal.valueOf(500))),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(
            ResultatKodeBarnebidrag.BEGRENSET_EVNE_FLERE_SAKER_UTFOER_FORHOLDSMESSIG_FORDELING),
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(8000))).isZero()
    );
  }

  @DisplayName("Beregner med barnetillegg fra forsvaret for tre barn. Resultatkoden skal angi"
      + "at det må gjøres en forholdsmessig fordeling da bidragsevnen ikke er høy nok til "
      + "å dekke beregnet bidrag pluss løpende bidrag")
  @Test
  void testBegrensetEvneGirForholdsmessigFordelingBarnetilleggForsvaret() {

    var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();
    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_1", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_1", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_1", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_1", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        2,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_2", BigDecimal.valueOf(1000)),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_2", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_2", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_2", BigDecimal.ZERO, BigDecimal.ZERO)));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(
        3,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3", BigDecimal.valueOf(0.80), BigDecimal.valueOf(8000), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE + "_3", BigDecimal.ZERO),
        new DeltBosted(DELT_BOSTED_REFERANSE + "_3", true),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE + "_3", BigDecimal.valueOf(10000), BigDecimal.ZERO),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE + "_3", BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, true),
        singletonList(new AndreLopendeBidrag(ANDRE_LOPENDE_BIDRAG_REFERANSE, 2, BigDecimal.valueOf(3000), BigDecimal.valueOf(500))),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(2667))).isZero(),
        () -> assertThat(resultat.get(1).getBelop().compareTo(BigDecimal.valueOf(1667))).isZero(),
        () -> assertThat(resultat.get(2).getBelop().compareTo(BigDecimal.valueOf(2667))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BEGRENSET_EVNE_FLERE_SAKER_UTFOER_FORHOLDSMESSIG_FORDELING)
    );
  }

  @DisplayName("Tester fra John")
  @Test
  void testerFraJohn() {

    var grunnlagBeregningPerBarnListe = singletonList(new GrunnlagBeregningPerBarn(
        1,
        new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(0.429), BigDecimal.valueOf(3725), false),
        new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf(457)),
        new DeltBosted(DELT_BOSTED_REFERANSE, false),
        new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.valueOf(2000), BigDecimal.valueOf(20)),
        new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.ZERO, BigDecimal.ZERO)));

    var grunnlagBeregningPeriodisert = new GrunnlagBeregning(
        new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(136), BigDecimal.valueOf(8334)),
        grunnlagBeregningPerBarnListe,
        new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
        emptyList(),
        sjablonPeriodeListe);

    var resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);

    assertAll(
        () -> assertThat(resultat.get(0).getBelop().compareTo(BigDecimal.valueOf(1140))).isZero(),
        () -> assertThat(resultat.get(0).getKode()).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_BP)
    );
  }
}
