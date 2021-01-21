package no.nav.bidrag.beregn.barnebidrag;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidrag;
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidragPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatPeriode;
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
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    MockitoAnnotations.initMocks(this);
    barnebidragCore = new BarnebidragCoreImpl(barnebidragPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne barnebidrag")
  void skalBeregneBarnebidrag() {
    byggBarnebidragPeriodeGrunnlagCore();
    byggBarnebidragPeriodeResultat();

    when(barnebidragPeriodeMock.beregnPerioder(any())).thenReturn(
        beregnBarnebidragPeriodeResultat);
    var beregnBarnebidragResultatCore = barnebidragCore.beregnBarnebidrag(
        beregnBarnebidragGrunnlagCore);

    assertAll(
        () -> assertThat(beregnBarnebidragResultatCore).isNotNull(),
        () -> assertThat(beregnBarnebidragResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),

        () -> assertThat(beregnBarnebidragResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSjablonListe().get(0)
            .getSjablonVerdi()).isEqualTo(BigDecimal.valueOf(22))

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
            AvvikType.DATO_FRA_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe()).isEmpty()
    );
  }


  private void byggBarnebidragPeriodeGrunnlagCore() {

    var bidragsevnePeriode = new BidragsevnePeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        BigDecimal.valueOf(100000), BigDecimal.valueOf(20000));
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriodeCore>();
    bidragsevnePeriodeListe.add(bidragsevnePeriode);

    var bPsAndelUnderholdskostnadPeriode = new BPsAndelUnderholdskostnadPeriodeCore(
        1, new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        BigDecimal.valueOf(100000), BigDecimal.valueOf(20000), false);
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<BPsAndelUnderholdskostnadPeriodeCore>();
    bPsAndelUnderholdskostnadPeriodeListe.add(bPsAndelUnderholdskostnadPeriode);

    var samvaersfradragPeriode = new SamvaersfradragPeriodeCore(
        1, new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        BigDecimal.valueOf(1000));
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriodeCore>();
    samvaersfradragPeriodeListe.add(samvaersfradragPeriode);

    var deltBostedPeriode = new DeltBostedPeriodeCore(
        1, new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        false);
    var deltBostedPeriodeListe = new ArrayList<DeltBostedPeriodeCore>();
    deltBostedPeriodeListe.add(deltBostedPeriode);

    var barnetilleggBPPeriode = new BarnetilleggPeriodeCore(
        1, new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        BigDecimal.valueOf(100), BigDecimal.valueOf(10));
    var barnetilleggBPPeriodeListe = new ArrayList<BarnetilleggPeriodeCore>();
    barnetilleggBPPeriodeListe.add(barnetilleggBPPeriode);

    var barnetilleggBMPeriode = new BarnetilleggPeriodeCore(
        1, new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        BigDecimal.valueOf(100), BigDecimal.valueOf(10));
    var barnetilleggBMPeriodeListe = new ArrayList<BarnetilleggPeriodeCore>();
    barnetilleggBMPeriodeListe.add(barnetilleggBMPeriode);

    var barnetilleggForsvaretPeriode = new BarnetilleggForsvaretPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        false);
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriodeCore>();
    barnetilleggForsvaretPeriodeListe.add(barnetilleggForsvaretPeriode);

    var andreLopendeBidragPeriode = new AndreLopendeBidragPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        1, BigDecimal.ZERO, BigDecimal.ZERO);
    var andreLopendeBidragPeriodeListe = new ArrayList<AndreLopendeBidragPeriodeCore>();
    andreLopendeBidragPeriodeListe.add(andreLopendeBidragPeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnBarnebidragGrunnlagCore = new BeregnBarnebidragGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        bidragsevnePeriodeListe, bPsAndelUnderholdskostnadPeriodeListe, samvaersfradragPeriodeListe,
        deltBostedPeriodeListe, barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe,
        barnetilleggForsvaretPeriodeListe, andreLopendeBidragPeriodeListe, sjablonPeriodeListe);
  }

  private void byggBarnebidragPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        singletonList(new ResultatBeregning(1, BigDecimal.valueOf(1), ResultatKode.KOSTNADSBEREGNET_BIDRAG,
            singletonList(new SjablonNavnVerdi(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22))))),
        new GrunnlagBeregningPeriodisert(new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(12000)),
            singletonList(new GrunnlagBeregningPerBarn(1, new BPsAndelUnderholdskostnad(
                BigDecimal.valueOf(60), BigDecimal.valueOf(8000), false), BigDecimal.valueOf(100),
                false,
                new Barnetillegg(BigDecimal.valueOf(100), BigDecimal.valueOf(10)),
                new Barnetillegg(BigDecimal.valueOf(1000), BigDecimal.valueOf(10)))),
            false, singletonList(new AndreLopendeBidrag(1, BigDecimal.ZERO, BigDecimal.ZERO)),
            singletonList(new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))))))));

    beregnBarnebidragPeriodeResultat = new BeregnBarnebidragResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
  }

}
