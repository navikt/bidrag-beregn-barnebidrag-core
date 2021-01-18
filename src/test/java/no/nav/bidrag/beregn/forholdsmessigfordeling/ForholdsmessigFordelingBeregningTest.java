package no.nav.bidrag.beregn.forholdsmessigfordeling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.enums.ResultatKode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.beregning.ForholdsmessigFordelingBeregningImpl;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av forholdsmessig fordeling")
public class ForholdsmessigFordelingBeregningTest {

  private final List<GrunnlagBeregningPeriodisert> grunnlagBeregningPeriodisertListe  = new ArrayList<>();

  @DisplayName("Basic test")
  @Test
  void testEnSakEttBarn() {
    ForholdsmessigFordelingBeregningImpl forholdsmessigFordelingBeregning = new ForholdsmessigFordelingBeregningImpl();

    grunnlagBeregningPeriodisertListe.add(new GrunnlagBeregningPeriodisert(
        1234567, 1, BigDecimal.valueOf(1000)));

    List<ResultatBeregning> resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisertListe);

    assertAll(
        () -> assertThat(resultat.get(0).getSaksnr()).isEqualTo(1234567),
        () -> assertThat(resultat.size()).isEqualTo(1),
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.KOSTNADSBEREGNET_BIDRAG)
    );
  }

  @DisplayName("test")
  @Test
  void testEnSakTreBarn() {
    ForholdsmessigFordelingBeregningImpl forholdsmessigFordelingBeregning = new ForholdsmessigFordelingBeregningImpl();

    grunnlagBeregningPeriodisertListe.add(new GrunnlagBeregningPeriodisert(
        1234567, 1, BigDecimal.valueOf(1000)));
    grunnlagBeregningPeriodisertListe.add(new GrunnlagBeregningPeriodisert(
        1234567, 2, BigDecimal.valueOf(2000)));
    grunnlagBeregningPeriodisertListe.add(new GrunnlagBeregningPeriodisert(
        1234567, 3, BigDecimal.valueOf(2000)));

    List<ResultatBeregning> resultat = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisertListe);

    assertAll(
        () -> assertThat(resultat.get(0).getSaksnr()).isEqualTo(1234567),
        () -> assertThat(resultat.get(1).getSaksnr()).isEqualTo(1234567),
        () -> assertThat(resultat.get(2).getSaksnr()).isEqualTo(1234567),
        () -> assertThat(resultat.size()).isEqualTo(3),
        () -> assertThat(resultat.get(0).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(1000))).isZero(),
        () -> assertThat(resultat.get(1).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(2000))).isZero(),
        () -> assertThat(resultat.get(2).getResultatBarnebidragBelop().compareTo(BigDecimal.valueOf(2000))).isZero(),
        () -> assertThat(resultat.get(0).getResultatkode()).isEqualTo(ResultatKode.KOSTNADSBEREGNET_BIDRAG)
    );
  }

}
