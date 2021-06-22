package no.nav.bidrag.beregn.nettobarnetilsyn;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.FAKTISK_UTGIFT_REFERANSE;
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
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnetNettoBarnetilsynResultat;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynGrunnlagCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.FaktiskUtgiftPeriodeCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("NettoBarnetilsynCore (dto-test)")
public class NettoBarnetilsynCoreTest {

  private NettoBarnetilsynCore nettoBarnetilsynCore;

  @Mock
  private NettoBarnetilsynPeriode nettoBarnetilsynPeriodeMock;

  private BeregnNettoBarnetilsynGrunnlagCore beregnNettoBarnetilsynGrunnlagCore;
  private BeregnetNettoBarnetilsynResultat nettoBarnetilsynPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    nettoBarnetilsynCore = new NettoBarnetilsynCoreImpl(nettoBarnetilsynPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne netto barnetilsyn")
  void skalBeregneNettoBarnetilsyn() {
    byggNettoBarnetilsynPeriodeGrunnlagCore();
    byggNettoBarnetilsynPeriodeResultat();

    when(nettoBarnetilsynPeriodeMock.beregnPerioder(any())).thenReturn(nettoBarnetilsynPeriodeResultat);
    var beregnNettoBarnetilsynResultatCore = nettoBarnetilsynCore.beregnNettoBarnetilsyn(
        beregnNettoBarnetilsynGrunnlagCore);

    assertAll(
        () -> assertThat(beregnNettoBarnetilsynResultatCore).isNotNull(),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getBeregnetNettoBarnetilsynPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getBeregnetNettoBarnetilsynPeriodeListe().size()).isEqualTo(1),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getSjablonListe()).isNotEmpty(),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getSjablonListe().size()).isEqualTo(1),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getSjablonListe().get(0).getVerdi()).isEqualTo(BigDecimal.valueOf(22)),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getBeregnetNettoBarnetilsynPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnNettoBarnetilsynResultatCore.getBeregnetNettoBarnetilsynPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01"))
    );
  }

  @Test
  @DisplayName("Skal ikke beregne NettoBarnetilsyn ved avvik")
  void skalIkkeBeregneBidragsevneVedAvvik() {
    byggNettoBarnetilsynPeriodeGrunnlagCore();
    byggAvvik();

    when(nettoBarnetilsynPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnbidragsevneResultatCore = nettoBarnetilsynCore.beregnNettoBarnetilsyn(beregnNettoBarnetilsynGrunnlagCore);

    assertAll(
        () -> assertThat(beregnbidragsevneResultatCore).isNotNull(),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe()).isNotEmpty(),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe()).hasSize(1),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe().get(0).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFom"),
        () -> assertThat(beregnbidragsevneResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(
            AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnbidragsevneResultatCore.getBeregnetNettoBarnetilsynPeriodeListe()).isEmpty()
    );
  }


  private void byggNettoBarnetilsynPeriodeGrunnlagCore() {

    var faktiskUtgiftPeriode = new FaktiskUtgiftPeriodeCore(FAKTISK_UTGIFT_REFERANSE, new PeriodeCore(LocalDate.parse("2017-01-01"), null),
        LocalDate.parse("2010-01-01"), 1, BigDecimal.valueOf(2));
    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriodeCore>();
    faktiskUtgiftPeriodeListe.add(faktiskUtgiftPeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnNettoBarnetilsynGrunnlagCore = new BeregnNettoBarnetilsynGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        faktiskUtgiftPeriodeListe, sjablonPeriodeListe);
  }

  private void byggNettoBarnetilsynPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        singletonList(new ResultatBeregning(1, BigDecimal.valueOf(1),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22))))),
        new GrunnlagBeregning(singletonList(
            new FaktiskUtgift(1, FAKTISK_UTGIFT_REFERANSE, 10, BigDecimal.valueOf(3))),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    nettoBarnetilsynPeriodeResultat = new BeregnetNettoBarnetilsynResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFom", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }

}
