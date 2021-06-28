package no.nav.bidrag.beregn.bidragsevne;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.BARN_I_HUSSTAND_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.BOSTATUS_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SAERFRADRAG_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SKATTEKLASSE_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstand;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnetBidragsevneResultat;
import no.nav.bidrag.beregn.bidragsevne.bo.Bostatus;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.Saerfradrag;
import no.nav.bidrag.beregn.bidragsevne.bo.Skatteklasse;
import no.nav.bidrag.beregn.bidragsevne.dto.BarnIHusstandPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneGrunnlagCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.SaerfradragPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.SkatteklassePeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode;
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
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BidragsevneCore (dto-test)")
public class BidragsevneCoreTest {

  private BidragsevneCore bidragsevneCore;

  @Mock
  private BidragsevnePeriode bidragsevnePeriodeMock;

  private BeregnBidragsevneGrunnlagCore beregnBidragsevneGrunnlagCore;
  private BeregnetBidragsevneResultat bidragsevnePeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    bidragsevneCore = new BidragsevneCoreImpl(bidragsevnePeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne bidragsevne")
  void skalBeregneBidragsevne() {
    byggBidragsevnePeriodeGrunnlagCore();
    byggBidragsevnePeriodeResultat();

    when(bidragsevnePeriodeMock.beregnPerioder(any())).thenReturn(bidragsevnePeriodeResultat);
    var beregnBidragsevneResultatCore = bidragsevneCore.beregnBidragsevne(beregnBidragsevneGrunnlagCore);

    assertAll(
        () -> assertThat(beregnBidragsevneResultatCore).isNotNull(),
        () -> assertThat(beregnBidragsevneResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),
        () -> assertThat(beregnBidragsevneResultatCore.getSjablonListe()).isNotEmpty(),
        () -> assertThat(beregnBidragsevneResultatCore.getSjablonListe().size()).isEqualTo(1),

        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(0).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(666)),

        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(0))
            .isEqualTo(INNTEKT_REFERANSE),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(1))
            .isEqualTo(SKATTEKLASSE_REFERANSE),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(2))
            .isEqualTo(BOSTATUS_REFERANSE),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(3))
            .isEqualTo(BARN_I_HUSSTAND_REFERANSE),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(4))
            .isEqualTo(SAERFRADRAG_REFERANSE),

        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(1).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(667)),

        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnBidragsevneResultatCore.getResultatPeriodeListe().get(2).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(668))
        );
  }

  @Test
  @DisplayName("Skal ikke beregne bidragsevne ved avvik")
  void skalIkkeBeregneBidragsevneVedAvvik() {
    byggBidragsevnePeriodeGrunnlagCore();
    byggAvvik();

    when(bidragsevnePeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnbidragsevneResultatCore = bidragsevneCore.beregnBidragsevne(beregnBidragsevneGrunnlagCore);

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


  private void byggBidragsevnePeriodeGrunnlagCore() {

    var inntektPeriodeListe = singletonList(new InntektPeriodeCore(INNTEKT_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), InntektType.LONN_SKE.toString(), BigDecimal.valueOf(666000)));

    var skatteklassePeriodeListe = singletonList(new SkatteklassePeriodeCore(SKATTEKLASSE_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), 1));

    var bostatusPeriodeListe = singletonList(new BostatusPeriodeCore(BOSTATUS_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), BostatusKode.MED_ANDRE.toString()));

    var barnIHusstandPeriodeListe = singletonList(new BarnIHusstandPeriodeCore(BARN_I_HUSSTAND_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), 1));

    var saerfradragPeriodeListe = singletonList(new SaerfradragPeriodeCore(SAERFRADRAG_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), SaerfradragKode.HELT.toString()));

    var sjablonPeriodeListe = singletonList(new SjablonPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))));

    beregnBidragsevneGrunnlagCore = new BeregnBidragsevneGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        inntektPeriodeListe, skatteklassePeriodeListe, bostatusPeriodeListe, barnIHusstandPeriodeListe, saerfradragPeriodeListe, sjablonPeriodeListe);
  }

  private void byggBidragsevnePeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(666), BigDecimal.valueOf(166500),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new GrunnlagBeregning(
            singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
            new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
            new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE),
            new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
            new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(667), BigDecimal.valueOf(166500),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new GrunnlagBeregning(
            singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(500000))),
            new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
            new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE),
            new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
            new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(668), BigDecimal.valueOf(166500),
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), BigDecimal.valueOf(22)))),
        new GrunnlagBeregning(
            singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(500000))),
            new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
            new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE),
            new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
            new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT),
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))))))));

    bidragsevnePeriodeResultat = new BeregnetBidragsevneResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }
}
