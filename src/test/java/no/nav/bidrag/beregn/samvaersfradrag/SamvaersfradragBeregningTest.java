package no.nav.bidrag.beregn.samvaersfradrag;

import static no.nav.bidrag.beregn.TestUtil.SAMVAERSKLASSE_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SOKNADSBARN_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.Samvaersklasse;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SoknadsbarnAlder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av samværsfradrag")
public class SamvaersfradragBeregningTest {

  private final List<SjablonPeriode> sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

  private final SamvaersfradragBeregning samvaersfradragberegning = SamvaersfradragBeregning.getInstance();

  @DisplayName("Test av beregning av samvaersfradrag for fireåring")
  @Test
  void testFireAar() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 4),
        new Samvaersklasse(SAMVAERSKLASSE_REFERANSE, "03"),
        sjablonPeriodeListe
    );

    var resultat = samvaersfradragberegning.beregn(grunnlagBeregning);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(2272))).isZero();
  }

  @DisplayName("Test av beregning av samvaersfradrag for seksåring")
  @Test
  void testSeksAar() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 6),
        new Samvaersklasse(SAMVAERSKLASSE_REFERANSE, "03"),
        sjablonPeriodeListe
    );

    var resultat = samvaersfradragberegning.beregn(grunnlagBeregning);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(2716))).isZero();
  }

  @DisplayName("Test av beregning av samvaersfradrag for fjortenåring")
  @Test
  void testFraJohn() {
    var grunnlagBeregning = new GrunnlagBeregning(
        new SoknadsbarnAlder(SOKNADSBARN_REFERANSE, 14),
        new Samvaersklasse(SAMVAERSKLASSE_REFERANSE, "01"),
        sjablonPeriodeListe
    );

    var resultat = samvaersfradragberegning.beregn(grunnlagBeregning);
    assertThat(resultat.getBelop().compareTo(BigDecimal.valueOf(457))).isZero();
  }
}
