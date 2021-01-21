package no.nav.bidrag.beregn.forholdsmessigfordeling;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingResultat;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSak;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.Bidragsevne;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagPerBarn;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPerBarn;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnForholdsmessigFordelingGrunnlagCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnetBidragSakPeriodeCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BidragsevnePeriodeCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.GrunnlagPerBarnCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.periode.ForholdsmessigFordelingPeriode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("ForholdsmessigFordelingCore (dto-test)")
public class ForholdsmessigFordelingCoreTest {

  private ForholdsmessigFordelingCore forholdsmessigFordelingCore;

  @Mock
  private ForholdsmessigFordelingPeriode forholdsmessigFordelingPeriode;

  private BeregnForholdsmessigFordelingGrunnlagCore beregnForholdsmessigFordelingGrunnlagCore;
  private BeregnForholdsmessigFordelingResultat beregnForholdsmessigFordelingResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    forholdsmessigFordelingCore = new ForholdsmessigFordelingCoreImpl(forholdsmessigFordelingPeriode);
  }

  @Test
  @DisplayName("Skal utføre forholdsmessig fordeling")
  void skalGjoreForholdsmessigFordeling() {
    byggForholdsmessigFordelingPeriodeGrunnlagCore();
    byggForholdsmessigFordelingPeriodeResultat();

    when(forholdsmessigFordelingPeriode.beregnPerioder(any())).thenReturn(
        beregnForholdsmessigFordelingResultat);
    var beregnForholdsmessigFordelingResultatCore = forholdsmessigFordelingCore.beregnForholdsmessigFordeling(
        beregnForholdsmessigFordelingGrunnlagCore);

    assertAll(
        () -> assertThat(beregnForholdsmessigFordelingResultatCore).isNotNull(),
        () -> assertThat(beregnForholdsmessigFordelingResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnForholdsmessigFordelingResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnForholdsmessigFordelingResultatCore.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(beregnForholdsmessigFordelingResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnForholdsmessigFordelingResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01"))
    );
  }

  @Test
  @DisplayName("Skal ikke beregne forholdsmessig fordeling ved avvik")
  void skalIkkeBeregneForholdsmessigFordelingVedAvvik() {
    byggForholdsmessigFordelingPeriodeGrunnlagCore();
    byggAvvik();

    when(forholdsmessigFordelingPeriode.validerInput(any())).thenReturn(avvikListe);
    var beregnForholdsmessigFordelingResultatCore = forholdsmessigFordelingCore.beregnForholdsmessigFordeling(
        beregnForholdsmessigFordelingGrunnlagCore);

    assertAll(
        () -> assertThat(beregnForholdsmessigFordelingResultatCore).isNotNull(),
        () -> assertThat(beregnForholdsmessigFordelingResultatCore.getAvvikListe()).isNotEmpty(),
        () -> assertThat(beregnForholdsmessigFordelingResultatCore.getAvvikListe()).hasSize(1),
        () -> assertThat(beregnForholdsmessigFordelingResultatCore.getAvvikListe().get(0).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFra"),
        () -> assertThat(beregnForholdsmessigFordelingResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(
            AvvikType.DATO_FRA_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnForholdsmessigFordelingResultatCore.getResultatPeriodeListe()).isEmpty()
    );
  }


  private void byggForholdsmessigFordelingPeriodeGrunnlagCore() {

    var bidragsevnePeriode = new BidragsevnePeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        BigDecimal.valueOf(100000), BigDecimal.valueOf(20000));
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriodeCore>();
    bidragsevnePeriodeListe.add(bidragsevnePeriode);

    var beregnetBidragSakPeriode = new BeregnetBidragSakPeriodeCore(1,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        singletonList(new GrunnlagPerBarnCore(1, BigDecimal.valueOf(20000))));
    var beregnetBidragSakPeriodeListe = new ArrayList<BeregnetBidragSakPeriodeCore>();
    beregnetBidragSakPeriodeListe.add(beregnetBidragSakPeriode);

    beregnForholdsmessigFordelingGrunnlagCore = new BeregnForholdsmessigFordelingGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        bidragsevnePeriodeListe, beregnetBidragSakPeriodeListe);
  }

  private void byggForholdsmessigFordelingPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        Arrays.asList(new ResultatBeregning(1, Arrays.asList(
            new ResultatPerBarn(1, BigDecimal.valueOf(1), ResultatKode.KOSTNADSBEREGNET_BIDRAG)))),
        new GrunnlagBeregningPeriodisert(new Bidragsevne(BigDecimal.valueOf(1000), BigDecimal.valueOf(12000)),
            singletonList(new BeregnetBidragSak(1, singletonList(new GrunnlagPerBarn(1, BigDecimal.valueOf(1000))))))));

    beregnForholdsmessigFordelingResultat = new BeregnForholdsmessigFordelingResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
  }

}
