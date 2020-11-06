package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.UnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("BPsAndelUnderholdskostnadCore (dto-test)")
public class BPsAndelUnderholdskostnadCoreTest {

  private BPsAndelUnderholdskostnadCore bPsAndelunderholdskostnadCore;

  private List<Sjablon> sjablonListe = new ArrayList<>();

  @Mock
  private BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriodeMock;

  private BeregnBPsAndelUnderholdskostnadGrunnlagCore beregnBPsAndelUnderholdskostnadGrunnlagCore;
  private BeregnBPsAndelUnderholdskostnadResultat bPsAndelunderholdskostnadPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    bPsAndelunderholdskostnadCore = new BPsAndelUnderholdskostnadCoreImpl(
        bPsAndelunderholdskostnadPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne BPsAndel av underholdskostnad")
  void skalBeregneBPsAndelunderholdskostnad() {
    byggBPsAndelUnderholdskostnadPeriodeGrunnlagCore();
    byggBPsAndelUnderholdskostnadPeriodeResultat();

    when(bPsAndelunderholdskostnadPeriodeMock.beregnPerioder(any())).thenReturn(
        bPsAndelunderholdskostnadPeriodeResultat);
    var beregnBPsAndelUnderholdskostnadResultatCore = bPsAndelunderholdskostnadCore.beregnBPsAndelUnderholdskostnad(
        beregnBPsAndelUnderholdskostnadGrunnlagCore);

    assertAll(
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore).isNotNull(),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(10)),

        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(20)),

        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(BigDecimal.valueOf(30)),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSjablonListe().get(0)
            .getSjablonInnholdListe().get(0).getSjablonInnholdVerdi()).isEqualTo(1600)

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
            AvvikType.DATO_FRA_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnbidragsevneResultatCore.getResultatPeriodeListe()).isEmpty()
    );
  }


  private void byggBPsAndelUnderholdskostnadPeriodeGrunnlagCore() {

    var underholdskostnadPeriode = new UnderholdskostnadPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BigDecimal.valueOf(1000));

    var inntektBPPeriode = new InntektPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), InntektType.LONN_SKE.toString(),
        BigDecimal.valueOf(111));

    var inntektBMPeriode = new InntektPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), InntektType.LONN_SKE.toString(),
        BigDecimal.valueOf(222));

    var inntektBBPeriode = new InntektPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), InntektType.LONN_SKE.toString(),
        BigDecimal.valueOf(333));

    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriodeCore>();
    var inntektBPPeriodeListe = new ArrayList<InntektPeriodeCore>();
    var inntektBMPeriodeListe = new ArrayList<InntektPeriodeCore>();
    var inntektBBPeriodeListe = new ArrayList<InntektPeriodeCore>();

    underholdskostnadPeriodeListe.add(underholdskostnadPeriode);
    inntektBPPeriodeListe.add(inntektBPPeriode);
    inntektBMPeriodeListe.add(inntektBMPeriode);
    inntektBBPeriodeListe.add(inntektBBPeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1600))));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnBPsAndelUnderholdskostnadGrunnlagCore = new BeregnBPsAndelUnderholdskostnadGrunnlagCore(LocalDate.parse("2017-01-01"),
        LocalDate.parse("2020-01-01"), 1,
        underholdskostnadPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe,
        inntektBBPeriodeListe, sjablonPeriodeListe);
  }

  private void byggBPsAndelUnderholdskostnadPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    var inntektBPListe = new ArrayList<Inntekt>();
    var inntektBMListe = new ArrayList<Inntekt>();
    var inntektBBListe = new ArrayList<Inntekt>();

    inntektBPListe.add(new Inntekt(InntektType.LONN_SKE,BigDecimal.valueOf(111d)));
    inntektBMListe.add(new Inntekt(InntektType.LONN_SKE,BigDecimal.valueOf(222d)));
    inntektBBListe.add(new Inntekt(InntektType.LONN_SKE,BigDecimal.valueOf(333d)));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(10), BigDecimal.valueOf(100), false),
        new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(1000), inntektBPListe, inntektBMListe, inntektBBListe,
            Arrays.asList(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                    BigDecimal.valueOf(1600))))))));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(20), BigDecimal.valueOf(200), false),
        new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(1000), inntektBPListe, inntektBMListe, inntektBBListe,
            Arrays.asList(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                    BigDecimal.valueOf(1640))))))));

    periodeResultatListe.add(new ResultatPeriode(1,
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(30), BigDecimal.valueOf(300), false),
        new GrunnlagBeregningPeriodisert(BigDecimal.valueOf(1000), inntektBPListe, inntektBMListe, inntektBBListe,
            Arrays.asList(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(),
                    BigDecimal.valueOf(1680))))))));


    bPsAndelunderholdskostnadPeriodeResultat = new BeregnBPsAndelUnderholdskostnadResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
  }

}
