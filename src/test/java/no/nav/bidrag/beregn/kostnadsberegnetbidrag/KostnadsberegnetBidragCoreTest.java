package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragResultat;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BPsAndelUnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnKostnadsberegnetBidragGrunnlagCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.SamvaersfradragPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.UnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("KostnadsberegnetBidragCore (dto-test)")
public class KostnadsberegnetBidragCoreTest {

  private KostnadsberegnetBidragCore kostnadsberegnetBidragCore;

  @Mock
  private KostnadsberegnetBidragPeriode kostnadsberegnetBidragPeriodeMock;

  private BeregnKostnadsberegnetBidragGrunnlagCore beregnKostnadsberegnetBidragGrunnlagCore;
  private BeregnKostnadsberegnetBidragResultat kostnadsberegnetBidragPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    kostnadsberegnetBidragCore = new KostnadsberegnetBidragCoreImpl(
        kostnadsberegnetBidragPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne kostnadsberegnet bidrag")
  void skalBeregneKostnadsberegnetBidrag() {
    byggKostnadsberegnetBidragPeriodeGrunnlagCore();
    byggKostnadsberegnetBidragPeriodeResultat();

    when(kostnadsberegnetBidragPeriodeMock.beregnPerioder(any())).thenReturn(
        kostnadsberegnetBidragPeriodeResultat);
    var beregnKostnadsberegnetBidragResultatCore = kostnadsberegnetBidragCore.beregnKostnadsberegnetBidrag(
        beregnKostnadsberegnetBidragGrunnlagCore);

    assertAll(
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore).isNotNull(),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKostnadsberegnetBidragBelop())
            .isEqualTo(BigDecimal.valueOf(666)),

        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatKostnadsberegnetBidragBelop())
            .isEqualTo(BigDecimal.valueOf(667)),

        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatKostnadsberegnetBidragBelop())
            .isEqualTo(BigDecimal.valueOf(668))

    );
  }

  @Test
  @DisplayName("Skal ikke beregne Kostnadsberegnet bidrag ved avvik")
  void skalIkkeBeregneAndelVedAvvik() {
    byggKostnadsberegnetBidragPeriodeGrunnlagCore();
    byggAvvik();

    when(kostnadsberegnetBidragPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnKostnadsberegnetBidragResultatCore = kostnadsberegnetBidragCore.beregnKostnadsberegnetBidrag(
        beregnKostnadsberegnetBidragGrunnlagCore);

    assertAll(
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore).isNotNull(),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getAvvikListe()).isNotEmpty(),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getAvvikListe()).hasSize(1),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getAvvikListe().get(0).getAvvikTekst())
            .isEqualTo("beregnDatoTil må være etter beregnDatoFra"),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(
            AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe()).isEmpty()
    );
  }


  private void byggKostnadsberegnetBidragPeriodeGrunnlagCore() {

    var underholdskostnadPeriode = new UnderholdskostnadPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(10000));
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriodeCore>();
    underholdskostnadPeriodeListe.add(underholdskostnadPeriode);

    var bPsAndelUnderholdskostnadPeriode = new BPsAndelUnderholdskostnadPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(20));
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<BPsAndelUnderholdskostnadPeriodeCore>();
    bPsAndelUnderholdskostnadPeriodeListe.add(bPsAndelUnderholdskostnadPeriode);

    var samvaersfradragPeriode = new SamvaersfradragPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(100));
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriodeCore>();
    samvaersfradragPeriodeListe.add(samvaersfradragPeriode);

    beregnKostnadsberegnetBidragGrunnlagCore = new BeregnKostnadsberegnetBidragGrunnlagCore(LocalDate.parse("2017-01-01"),
        LocalDate.parse("2020-01-01"),
        1, underholdskostnadPeriodeListe, bPsAndelUnderholdskostnadPeriodeListe, samvaersfradragPeriodeListe);
  }

  private void byggKostnadsberegnetBidragPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(666)),
        new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(10000),
            BigDecimal.valueOf(20),
            BigDecimal.valueOf(100)
        )));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(667)),
        new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(10000),
            BigDecimal.valueOf(20),
            BigDecimal.valueOf(100)
        )));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(668)),
        new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(10000),
            BigDecimal.valueOf(20),
            BigDecimal.valueOf(100)
        )));

    kostnadsberegnetBidragPeriodeResultat = new BeregnKostnadsberegnetBidragResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }

}
