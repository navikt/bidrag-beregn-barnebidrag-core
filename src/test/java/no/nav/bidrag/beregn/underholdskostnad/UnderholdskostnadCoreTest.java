package no.nav.bidrag.beregn.underholdskostnad;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.BARNETILSYN_MED_STONAD_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.FORPLEINING_UTGIFT_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.NETTO_BARNETILSYN_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SOKNADSBARN_REFERANSE;
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
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnetUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgift;
import no.nav.bidrag.beregn.underholdskostnad.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsyn;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.SoknadsbarnAlder;
import no.nav.bidrag.beregn.underholdskostnad.dto.BarnetilsynMedStonadPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.ForpleiningUtgiftPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.NettoBarnetilsynPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.SoknadsbarnCore;
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnderholdskostnadCore (dto-test)")
public class UnderholdskostnadCoreTest {

  private UnderholdskostnadCore underholdskostnadCore;

  @Mock
  private UnderholdskostnadPeriode underholdskostnadPeriodeMock;

  private BeregnUnderholdskostnadGrunnlagCore beregnUnderholdskostnadGrunnlagCore;
  private BeregnetUnderholdskostnadResultat underholdskostnadPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    underholdskostnadCore = new UnderholdskostnadCoreImpl(underholdskostnadPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne underholdskostnad")
  void skalBeregneunderholdskostnad() {
    byggUnderholdskostnadPeriodeGrunnlagCore();
    byggUnderholdskostnadPeriodeResultat();

    when(underholdskostnadPeriodeMock.beregnPerioder(any())).thenReturn(underholdskostnadPeriodeResultat);
    var beregnUnderholdskostnadResultatCore = underholdskostnadCore.beregnUnderholdskostnad(
        beregnUnderholdskostnadGrunnlagCore);

    assertAll(
        () -> assertThat(beregnUnderholdskostnadResultatCore).isNotNull(),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().size()).isEqualTo(3),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getSjablonListe()).isNotEmpty(),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getSjablonListe().size()).isEqualTo(1),

        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(0).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(666)),

        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(0).getGrunnlagReferanseListe().get(0))
            .isEqualTo(SOKNADSBARN_REFERANSE),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(0).getGrunnlagReferanseListe().get(1))
            .isEqualTo(BARNETILSYN_MED_STONAD_REFERANSE),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(0).getGrunnlagReferanseListe().get(2))
            .isEqualTo(NETTO_BARNETILSYN_REFERANSE),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(0).getGrunnlagReferanseListe().get(3))
            .isEqualTo(FORPLEINING_UTGIFT_REFERANSE),

        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(1).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(667)),

        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnUnderholdskostnadResultatCore.getBeregnetUnderholdskostnadPeriodeListe().get(2).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(668))
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
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe().get(0).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFom"),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(
            AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnbidragsevneResultatCore.getBeregnetUnderholdskostnadPeriodeListe()).isEmpty()
    );
  }


  private void byggUnderholdskostnadPeriodeGrunnlagCore() {

    var soknadsbarn = new SoknadsbarnCore(SOKNADSBARN_REFERANSE, 1, LocalDate.parse("2017-01-01"));

    var barnetilsynMedStonadPeriode = new BarnetilsynMedStonadPeriodeCore(BARNETILSYN_MED_STONAD_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), "DU", "64");
    var barnetilsynMedStonadPeriodeListe = new ArrayList<BarnetilsynMedStonadPeriodeCore>();
    barnetilsynMedStonadPeriodeListe.add(barnetilsynMedStonadPeriode);

    var nettoBarnetilsynPeriode = new NettoBarnetilsynPeriodeCore(NETTO_BARNETILSYN_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), BigDecimal.valueOf(666));
    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriodeCore>();
    nettoBarnetilsynPeriodeListe.add(nettoBarnetilsynPeriode);

    var forpleiningUtgiftPeriode = new ForpleiningUtgiftPeriodeCore(FORPLEINING_UTGIFT_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), BigDecimal.valueOf(666));
    var forpleiningUtgiftPeriodeListe = new ArrayList<ForpleiningUtgiftPeriodeCore>();
    forpleiningUtgiftPeriodeListe.add(forpleiningUtgiftPeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnUnderholdskostnadGrunnlagCore = new BeregnUnderholdskostnadGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        soknadsbarn, barnetilsynMedStonadPeriodeListe, nettoBarnetilsynPeriodeListe, forpleiningUtgiftPeriodeListe, sjablonPeriodeListe);
  }

  private void byggUnderholdskostnadPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(666),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new GrunnlagBeregning(
            new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 7),
            new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "DU", "64"),
            new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.valueOf(666)),
            new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.valueOf(777)),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(667),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new GrunnlagBeregning(
            new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 7),
            new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "DU", "64"),
            new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.valueOf(667)),
            new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.valueOf(778)),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(668),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new GrunnlagBeregning(
            new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 7),
            new BarnetilsynMedStonad(BARNETILSYN_MED_STONAD_REFERANSE, "DU", "64"),
            new NettoBarnetilsyn(NETTO_BARNETILSYN_REFERANSE, BigDecimal.valueOf(668)),
            new ForpleiningUtgift(FORPLEINING_UTGIFT_REFERANSE, BigDecimal.valueOf(778)),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    underholdskostnadPeriodeResultat = new BeregnetUnderholdskostnadResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFom", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }

}
