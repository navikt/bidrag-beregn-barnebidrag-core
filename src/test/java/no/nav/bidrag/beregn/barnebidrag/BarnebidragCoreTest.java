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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidrag;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaret;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne;
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBosted;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.Samvaersfradrag;
import no.nav.bidrag.beregn.barnebidrag.dto.AndreLopendeBidragPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BPsAndelUnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggForsvaretPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnBarnebidragGrunnlagCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BidragsevnePeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.DeltBostedPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.SamvaersfradragPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.domain.enums.AvvikType;
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeBarnebidrag;
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn;
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BarnebidragCore (dto-test)")
public class BarnebidragCoreTest {

  private BarnebidragCore barnebidragCore;

  @Mock
  private BarnebidragPeriode barnebidragPeriodeMock;

  private BeregnBarnebidragGrunnlagCore beregnBarnebidragGrunnlagCore;
  private BeregnBarnebidragResultat beregnBarnebidragPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    barnebidragCore = new BarnebidragCoreImpl(barnebidragPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne barnebidrag")
  void skalBeregneBarnebidrag() {
    byggBarnebidragPeriodeGrunnlagCore();
    byggBarnebidragPeriodeResultat();

    when(barnebidragPeriodeMock.beregnPerioder(any())).thenReturn(beregnBarnebidragPeriodeResultat);
    var beregnBarnebidragResultatCore = barnebidragCore.beregnBarnebidrag(beregnBarnebidragGrunnlagCore);

    assertAll(
        () -> assertThat(beregnBarnebidragResultatCore).isNotNull(),
        () -> assertThat(beregnBarnebidragResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().size()).isEqualTo(1),
        () -> assertThat(beregnBarnebidragResultatCore.getSjablonListe()).isNotEmpty(),
        () -> assertThat(beregnBarnebidragResultatCore.getSjablonListe().size()).isEqualTo(1),

        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),

        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(0))
            .isEqualTo(ANDRE_LOPENDE_BIDRAG_REFERANSE),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(1))
            .isEqualTo(BARNETILLEGG_BM_REFERANSE),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(2))
            .isEqualTo(BARNETILLEGG_BP_REFERANSE),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(3))
            .isEqualTo(BARNETILLEGG_FORSVARET_REFERANSE),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(4))
            .isEqualTo(BIDRAGSEVNE_REFERANSE),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(5))
            .isEqualTo(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(6))
            .isEqualTo(DELT_BOSTED_REFERANSE),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(7))
            .isEqualTo(SAMVAERSFRADRAG_REFERANSE)
    );
  }

  @Test
  @DisplayName("Skal ikke beregne barnebidrag ved avvik")
  void skalIkkeBeregneBarnebidragVedAvvik() {
    byggBarnebidragPeriodeGrunnlagCore();
    byggAvvik();

    when(barnebidragPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnbidragsevneResultatCore = barnebidragCore.beregnBarnebidrag(
        beregnBarnebidragGrunnlagCore);

    assertAll(
        () -> assertThat(beregnbidragsevneResultatCore).isNotNull(),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe()).isNotEmpty(),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe()).hasSize(1),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe().get(0).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFra"),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(
            AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe()).isEmpty()
    );
  }


  private void byggBarnebidragPeriodeGrunnlagCore() {

    var bidragsevnePeriodeListe = singletonList(new BidragsevnePeriodeCore(BIDRAGSEVNE_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), BigDecimal.valueOf(100000), BigDecimal.valueOf(20000)));

    var bPsAndelUnderholdskostnadPeriodeListe = singletonList(new BPsAndelUnderholdskostnadPeriodeCore(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
        1, new PeriodeCore(LocalDate.parse("2017-01-01"), null), BigDecimal.valueOf(100000), BigDecimal.valueOf(20000), false));

    var samvaersfradragPeriodeListe = singletonList(new SamvaersfradragPeriodeCore(SAMVAERSFRADRAG_REFERANSE,
        1, new PeriodeCore(LocalDate.parse("2017-01-01"), null), BigDecimal.valueOf(1000)));

    var deltBostedPeriodeListe = singletonList(new DeltBostedPeriodeCore(DELT_BOSTED_REFERANSE,
        1, new PeriodeCore(LocalDate.parse("2017-01-01"), null), false));

    var barnetilleggBPPeriodeListe = singletonList(new BarnetilleggPeriodeCore(BARNETILLEGG_BP_REFERANSE,
        1, new PeriodeCore(LocalDate.parse("2017-01-01"), null), BigDecimal.valueOf(100), BigDecimal.valueOf(10)));

    var barnetilleggBMPeriodeListe = singletonList(new BarnetilleggPeriodeCore(BARNETILLEGG_BM_REFERANSE,
        1, new PeriodeCore(LocalDate.parse("2017-01-01"), null), BigDecimal.valueOf(100), BigDecimal.valueOf(10)));

    var barnetilleggForsvaretPeriodeListe = singletonList(new BarnetilleggForsvaretPeriodeCore(BARNETILLEGG_FORSVARET_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), false));

    var andreLopendeBidragPeriodeListe = singletonList(new AndreLopendeBidragPeriodeCore(ANDRE_LOPENDE_BIDRAG_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), 1, BigDecimal.ZERO, BigDecimal.ZERO));

    var sjablonPeriodeListe = singletonList(new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))));

    beregnBarnebidragGrunnlagCore = new BeregnBarnebidragGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        bidragsevnePeriodeListe, bPsAndelUnderholdskostnadPeriodeListe, samvaersfradragPeriodeListe, deltBostedPeriodeListe,
        barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe, barnetilleggForsvaretPeriodeListe, andreLopendeBidragPeriodeListe,
        sjablonPeriodeListe);
  }

  private void byggBarnebidragPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        singletonList(new ResultatBeregning(
            1,
            BigDecimal.valueOf(1),
            ResultatKodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG,
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22))))),
        new GrunnlagBeregning(
            new Bidragsevne(BIDRAGSEVNE_REFERANSE, BigDecimal.valueOf(1000), BigDecimal.valueOf(12000)),
            singletonList(new GrunnlagBeregningPerBarn(
                1,
                new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(60), BigDecimal.valueOf(8000), false),
                new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf(100)),
                new DeltBosted(DELT_BOSTED_REFERANSE, false),
                new Barnetillegg(BARNETILLEGG_BP_REFERANSE, BigDecimal.valueOf(100), BigDecimal.valueOf(10)),
                new Barnetillegg(BARNETILLEGG_BM_REFERANSE, BigDecimal.valueOf(1000), BigDecimal.valueOf(10)))),
            new BarnetilleggForsvaret(BARNETILLEGG_FORSVARET_REFERANSE, false),
            singletonList(new AndreLopendeBidrag(ANDRE_LOPENDE_BIDRAG_REFERANSE, 1, BigDecimal.ZERO, BigDecimal.ZERO)),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    beregnBarnebidragPeriodeResultat = new BeregnBarnebidragResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }

}
