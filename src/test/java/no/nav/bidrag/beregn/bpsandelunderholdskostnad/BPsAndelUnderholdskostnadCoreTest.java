package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekter;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.InntekterPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode;
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
            .isEqualTo(Double.valueOf(666)),

        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(Double.valueOf(667)),

        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnBPsAndelUnderholdskostnadResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatAndelProsent())
            .isEqualTo(Double.valueOf(668)),
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

    var inntekterPeriode = new InntekterPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), 111, 222, 333);
    var inntekterPeriodeListe = new ArrayList<InntekterPeriodeCore>();
    inntekterPeriodeListe.add(inntekterPeriode);

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 1600d)));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnBPsAndelUnderholdskostnadGrunnlagCore = new BeregnBPsAndelUnderholdskostnadGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"),
        inntekterPeriodeListe, sjablonPeriodeListe);
  }

  private void byggBPsAndelUnderholdskostnadPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(Double.valueOf(666)),
        new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(new Inntekter(111, 222, 333),
            Arrays.asList(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 1600d)))))));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(Double.valueOf(667)),
        new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(new Inntekter(111, 222, 333),
            Arrays.asList(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 1640d)))))));

    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(Double.valueOf(668)),
        new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(new Inntekter(111, 222, 333),
            Arrays.asList(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
                Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 1640d)))))));


    bPsAndelunderholdskostnadPeriodeResultat = new BeregnBPsAndelUnderholdskostnadResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
  }

}
