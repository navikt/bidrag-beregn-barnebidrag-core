package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_BB_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_BM_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_BP_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnetBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Underholdskostnad;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.UnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode;
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
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BPsAndelUnderholdskostnadCore (dto-test)")
public class BPsAndelUnderholdskostnadCoreTest {

  private BPsAndelUnderholdskostnadCore bPsAndelunderholdskostnadCore;

  @Mock
  private BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriodeMock;

  private BeregnBPsAndelUnderholdskostnadGrunnlagCore beregnBPsAndelUnderholdskostnadGrunnlagCore;
  private BeregnetBPsAndelUnderholdskostnadResultat bPsAndelunderholdskostnadPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    bPsAndelunderholdskostnadCore = new BPsAndelUnderholdskostnadCoreImpl(bPsAndelunderholdskostnadPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne BPsAndel av underholdskostnad")
  void skalBeregneBPsAndelunderholdskostnad() {
    byggBPsAndelUnderholdskostnadPeriodeGrunnlagCore();
    byggBPsAndelUnderholdskostnadPeriodeResultat();

    when(bPsAndelunderholdskostnadPeriodeMock.beregnPerioder(any())).thenReturn(bPsAndelunderholdskostnadPeriodeResultat);
    var beregnBPsAndelUnderholdskostnadResultatCore = bPsAndelunderholdskostnadCore.beregnBPsAndelUnderholdskostnad(
        beregnBPsAndelUnderholdskostnadGrunnlagCore);

    assertAll(
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore).isNotNull(),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultat().getAndelProsent())
            .isEqualTo(BigDecimal.valueOf(0.10)),

        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(0))
            .isEqualTo(INNTEKT_BB_REFERANSE),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(1))
            .isEqualTo(INNTEKT_BM_REFERANSE),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(2))
            .isEqualTo(INNTEKT_BP_REFERANSE),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getGrunnlagReferanseListe().get(4))
            .isEqualTo(UNDERHOLDSKOSTNAD_REFERANSE),

        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getResultat().getAndelProsent())
            .isEqualTo(BigDecimal.valueOf(0.20)),

        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getResultat().getAndelProsent())
            .isEqualTo(BigDecimal.valueOf(0.30))
    );
  }

  @Test
  @DisplayName("Skal ikke beregne BPs andel av underholdskostnad ved avvik")
  void skalIkkeBeregneAndelVedAvvik() {
    byggBPsAndelUnderholdskostnadPeriodeGrunnlagCore();
    byggAvvik();

    when(bPsAndelunderholdskostnadPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnbidragsevneResultatCore = bPsAndelunderholdskostnadCore.beregnBPsAndelUnderholdskostnad(
        beregnBPsAndelUnderholdskostnadGrunnlagCore);

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


  private void byggBPsAndelUnderholdskostnadPeriodeGrunnlagCore() {

    var underholdskostnadPeriodeListe = singletonList(new UnderholdskostnadPeriodeCore(UNDERHOLDSKOSTNAD_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), BigDecimal.valueOf(1000)));

    var inntektBPPeriodeListe = singletonList(new InntektPeriodeCore(INNTEKT_BP_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), InntektType.LONN_SKE.toString(), BigDecimal.valueOf(111),
        false, false));

    var inntektBMPeriodeListe = singletonList(new InntektPeriodeCore(INNTEKT_BM_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), InntektType.LONN_SKE.toString(), BigDecimal.valueOf(222),
        false, false));

    var inntektBBPeriodeListe = singletonList(new InntektPeriodeCore(INNTEKT_BB_REFERANSE,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), InntektType.LONN_SKE.toString(), BigDecimal.valueOf(333),
        false, false));

    var sjablonPeriodeListe = singletonList(new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1600)))));

    beregnBPsAndelUnderholdskostnadGrunnlagCore = new BeregnBPsAndelUnderholdskostnadGrunnlagCore(LocalDate.parse("2017-01-01"),
        LocalDate.parse("2020-01-01"), 1, underholdskostnadPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe,
        inntektBBPeriodeListe, sjablonPeriodeListe);
  }

  private void byggBPsAndelUnderholdskostnadPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    var inntektBPListe = singletonList(new Inntekt(INNTEKT_BP_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(111), false, false));
    var inntektBMListe = singletonList(new Inntekt(INNTEKT_BM_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(222), false, false));
    var inntektBBListe = singletonList(new Inntekt(INNTEKT_BB_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(333), false, false));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(0.10), BigDecimal.valueOf(100), false,
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), BigDecimal.valueOf(1600)))),
        new GrunnlagBeregning(
            new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
            inntektBPListe,
            inntektBMListe,
            inntektBBListe,
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1600)))))))));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(0.20), BigDecimal.valueOf(200), false,
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), BigDecimal.valueOf(1600)))),
        new GrunnlagBeregning(
            new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
            inntektBPListe,
            inntektBMListe,
            inntektBBListe,
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1640)))))))));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(0.30), BigDecimal.valueOf(300), false,
            singletonList(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), BigDecimal.valueOf(1600)))),
        new GrunnlagBeregning(
            new Underholdskostnad(UNDERHOLDSKOSTNAD_REFERANSE, BigDecimal.valueOf(1000)),
            inntektBPListe,
            inntektBMListe,
            inntektBBListe,
            singletonList(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                    singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1680)))))))));

    bPsAndelunderholdskostnadPeriodeResultat = new BeregnetBPsAndelUnderholdskostnadResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }

}
