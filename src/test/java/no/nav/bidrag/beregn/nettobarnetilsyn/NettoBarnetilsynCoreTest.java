package no.nav.bidrag.beregn.nettobarnetilsyn;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynResultat;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynGrunnlagCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.FaktiskUtgiftPeriodeCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("NettoBarnetilsynCore (dto-test)")
public class NettoBarnetilsynCoreTest {

  private NettoBarnetilsynCore nettoBarnetilsynCore;

  private List<Sjablon> sjablonListe = new ArrayList<>();

  @Mock
  private NettoBarnetilsynPeriode nettoBarnetilsynPeriodeMock;

  private BeregnNettoBarnetilsynGrunnlagCore beregnNettoBarnetilsynGrunnlagCore;
  private BeregnNettoBarnetilsynResultat nettoBarnetilsynPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    nettoBarnetilsynCore = new NettoBarnetilsynCoreImpl(nettoBarnetilsynPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne NettoBarnetilsyn")
  void skalBeregneNettoBarnetilsyn() {
    byggNettoBarnetilsynPeriodeGrunnlagCore();
    byggNettoBarnetilsynPeriodeResultat();

    when(nettoBarnetilsynPeriodeMock.beregnPerioder(any())).thenReturn(
        nettoBarnetilsynPeriodeResultat);
    var beregnNettoBarnetilsynResultatCore = nettoBarnetilsynCore.beregnNettoBarnetilsyn(
        beregnNettoBarnetilsynGrunnlagCore);

    assertAll(
        () -> assertThat(beregnNettoBarnetilsynResultatCore).isNotNull(),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getResultatPeriodeListe().size()).isEqualTo(1),

        () -> assertThat(beregnNettoBarnetilsynResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),

        () -> assertThat(beregnNettoBarnetilsynResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSjablonListe().get(0)
            .getSjablonInnholdListe().get(0).getSjablonInnholdVerdi()).isEqualTo(22)

    );
  }

  @Test
  @DisplayName("Skal ikke beregne NettoBarnetilsyn ved avvik")
  void skalIkkeBeregneBidragsevneVedAvvik() {
    byggNettoBarnetilsynPeriodeGrunnlagCore();
    byggAvvik();

    when(nettoBarnetilsynPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnbidragsevneResultatCore = nettoBarnetilsynCore.beregnNettoBarnetilsyn(
        beregnNettoBarnetilsynGrunnlagCore);

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


  private void byggNettoBarnetilsynPeriodeGrunnlagCore() {

    var faktiskUtgiftPeriode = new FaktiskUtgiftPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), LocalDate.parse("2010-01-01"),
        1, 2);
    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriodeCore>();
    faktiskUtgiftPeriodeListe.add(faktiskUtgiftPeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 22d)));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnNettoBarnetilsynGrunnlagCore = new BeregnNettoBarnetilsynGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        faktiskUtgiftPeriodeListe, sjablonPeriodeListe);
  }

  private void byggNettoBarnetilsynPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        Arrays.asList(new ResultatBeregning(1, 1)),
        new GrunnlagBeregningPeriodisert(
            Arrays.asList(new FaktiskUtgift(1, 10,3)),
            Arrays.asList(new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 22d)))))));

    nettoBarnetilsynPeriodeResultat = new BeregnNettoBarnetilsynResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
  }

}