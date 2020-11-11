package no.nav.bidrag.beregn.underholdskostnad;

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
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.underholdskostnad.dto.BarnetilsynMedStonadPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.ForpleiningUtgiftPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.NettoBarnetilsynPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("UnderholdskostnadCore (dto-test)")
public class UnderholdskostnadCoreTest {

  private UnderholdskostnadCore underholdskostnadCore;

  @Mock
  private UnderholdskostnadPeriode underholdskostnadPeriodeMock;

  private BeregnUnderholdskostnadGrunnlagCore beregnUnderholdskostnadGrunnlagCore;
  private BeregnUnderholdskostnadResultat underholdskostnadPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    underholdskostnadCore = new UnderholdskostnadCoreImpl(underholdskostnadPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne underholdskostnad")
  void skalBeregneunderholdskostnad() {
    byggUnderholdskostnadPeriodeGrunnlagCore();
    byggUnderholdskostnadPeriodeResultat();

    when(underholdskostnadPeriodeMock.beregnPerioder(any())).thenReturn(
        underholdskostnadPeriodeResultat);
    var beregnUnderholdskostnadResultatCore = underholdskostnadCore.beregnUnderholdskostnad(
        beregnUnderholdskostnadGrunnlagCore);

    assertAll(
        () -> assertThat(beregnUnderholdskostnadResultatCore).isNotNull(),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBelopUnderholdskostnad())
            .isEqualTo(BigDecimal.valueOf(666)),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getNettoBarnetilsynBelop())
            .isEqualTo(BigDecimal.valueOf(666)),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getForpleiningUtgiftBelop())
            .isEqualTo(BigDecimal.valueOf(777)),
        () -> assertThat(
            beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBarnetilsynMedStonadTilsynType())
            .isEqualTo("DU"),

        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBelopUnderholdskostnad())
            .isEqualTo(BigDecimal.valueOf(667)),

        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBelopUnderholdskostnad())
            .isEqualTo(BigDecimal.valueOf(668)),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSjablonListe().get(0)
            .getSjablonVerdi()).isEqualTo(BigDecimal.valueOf(22))

    );
  }

  @Test
  @DisplayName("Skal ikke beregne underholdskostnad ved avvik")
  void skalIkkeBeregneBidragsevneVedAvvik() {
    byggUnderholdskostnadPeriodeGrunnlagCore();
    byggAvvik();

    when(underholdskostnadPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnbidragsevneResultatCore = underholdskostnadCore.beregnUnderholdskostnad(
        beregnUnderholdskostnadGrunnlagCore);

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


  private void byggUnderholdskostnadPeriodeGrunnlagCore() {

    var barnetilsynMedStonadPeriode = new BarnetilsynMedStonadPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), "DU", "64");
    var barnetilsynMedStonadPeriodeListe = new ArrayList<BarnetilsynMedStonadPeriodeCore>();
    barnetilsynMedStonadPeriodeListe.add(barnetilsynMedStonadPeriode);

    var nettoBarnetilsynPeriode = new NettoBarnetilsynPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), BigDecimal.valueOf(666));
    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriodeCore>();
    nettoBarnetilsynPeriodeListe.add(nettoBarnetilsynPeriode);

    var forpleiningUtgiftPeriode = new ForpleiningUtgiftPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), BigDecimal.valueOf(666));
    var forpleiningUtgiftPeriodeListe = new ArrayList<ForpleiningUtgiftPeriodeCore>();
    forpleiningUtgiftPeriodeListe.add(forpleiningUtgiftPeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnUnderholdskostnadGrunnlagCore = new BeregnUnderholdskostnadGrunnlagCore(1,
        LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        LocalDate.parse("2017-01-01"), barnetilsynMedStonadPeriodeListe, nettoBarnetilsynPeriodeListe, forpleiningUtgiftPeriodeListe,
        sjablonPeriodeListe);
  }

  private void byggUnderholdskostnadPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(666),
            singletonList(new SjablonNavnVerdi(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new BeregnUnderholdskostnadGrunnlagPeriodisert(7, new BarnetilsynMedStonad("DU", "64"),
            BigDecimal.valueOf(666),
            BigDecimal.valueOf(777),
            singletonList(new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))))))));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(667),
            singletonList(new SjablonNavnVerdi(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new BeregnUnderholdskostnadGrunnlagPeriodisert(7, new BarnetilsynMedStonad("DU", "64"),
            BigDecimal.valueOf(667),
            BigDecimal.valueOf(778),
            singletonList(new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))))))));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(668),
            singletonList(new SjablonNavnVerdi(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new BeregnUnderholdskostnadGrunnlagPeriodisert(7, new BarnetilsynMedStonad("DU", "64"),
            BigDecimal.valueOf(668),
                BigDecimal.valueOf(778),
            singletonList(new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))))))));

    underholdskostnadPeriodeResultat = new BeregnUnderholdskostnadResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
  }

}
