package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE;
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
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnetKostnadsberegnetBidragResultat;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Samvaersfradrag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Underholdskostnad;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BPsAndelUnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnKostnadsberegnetBidragGrunnlagCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.SamvaersfradragPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.UnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("KostnadsberegnetBidragCore (dto-test)")
public class KostnadsberegnetBidragCoreTest {

  private KostnadsberegnetBidragCore kostnadsberegnetBidragCore;

  @Mock
  private KostnadsberegnetBidragPeriode kostnadsberegnetBidragPeriodeMock;

  private BeregnKostnadsberegnetBidragGrunnlagCore beregnKostnadsberegnetBidragGrunnlagCore;
  private BeregnetKostnadsberegnetBidragResultat kostnadsberegnetBidragPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    kostnadsberegnetBidragCore = new KostnadsberegnetBidragCoreImpl(kostnadsberegnetBidragPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne kostnadsberegnet bidrag")
  void skalBeregneKostnadsberegnetBidrag() {
    byggKostnadsberegnetBidragPeriodeGrunnlagCore();
    byggKostnadsberegnetBidragPeriodeResultat();

    when(kostnadsberegnetBidragPeriodeMock.beregnPerioder(any())).thenReturn(kostnadsberegnetBidragPeriodeResultat);
    var beregnKostnadsberegnetBidragResultatCore = kostnadsberegnetBidragCore.beregnKostnadsberegnetBidrag(
        beregnKostnadsberegnetBidragGrunnlagCore);

    assertAll(
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore).isNotNull(),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(0).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(666)),

        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(0))
            .isEqualTo(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(1))
            .isEqualTo(SAMVAERSFRADRAG_REFERANSE),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(2))
            .isEqualTo(UNDERHOLDSKOSTNAD_REFERANSE),

        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(1).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(667)),

        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnKostnadsberegnetBidragResultatCore.getResultatPeriodeListe().get(2).getResultat().getBelop())
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

    var underholdskostnadPeriodeListe = singletonList(new UnderholdskostnadPeriodeCore(UNDERHOLDSKOSTNAD_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(10000)));

    var bPsAndelUnderholdskostnadPeriodeListe = singletonList(new BPsAndelUnderholdskostnadPeriodeCore(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(20)));

    var samvaersfradragPeriodeListe = singletonList(new SamvaersfradragPeriodeCore(SAMVAERSFRADRAG_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(100)));

    beregnKostnadsberegnetBidragGrunnlagCore = new BeregnKostnadsberegnetBidragGrunnlagCore(
        LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"), 1, underholdskostnadPeriodeListe,
        bPsAndelUnderholdskostnadPeriodeListe, samvaersfradragPeriodeListe);
  }

  private void byggKostnadsberegnetBidragPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(666)),
        new GrunnlagBeregning(
            new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(10000)),
            new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(20)),
            new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf(100)))));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(667)),
        new GrunnlagBeregning(
            new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(10000)),
            new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(20)),
            new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf(100)))));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(668)),
        new GrunnlagBeregning(
            new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(10000)),
            new BPsAndelUnderholdskostnad(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(20)),
            new Samvaersfradrag(SAMVAERSFRADRAG_REFERANSE, BigDecimal.valueOf(100)))));

    kostnadsberegnetBidragPeriodeResultat = new BeregnetKostnadsberegnetBidragResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }

}
