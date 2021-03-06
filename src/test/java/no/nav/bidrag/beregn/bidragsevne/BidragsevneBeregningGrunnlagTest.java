package no.nav.bidrag.beregn.bidragsevne;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test hent av sjablonverdi")
class BidragsevneBeregningGrunnlagTest {

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @Test
  void hentSjablon() {

    var sjablonverdiInntekt = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT);
    var sortertTrinnvisSkattesatsListe = SjablonUtil.hentTrinnvisSkattesats(sjablonListe, SjablonNavn.TRINNVIS_SKATTESATS);

    assertAll(
        () -> assertThat(sjablonverdiInntekt).isEqualTo(BigDecimal.valueOf(22)),
        () -> assertThat(sortertTrinnvisSkattesatsListe.size()).isEqualTo(4),
        () -> assertThat(sortertTrinnvisSkattesatsListe.get(0).getInntektGrense()).isEqualTo(BigDecimal.valueOf(180800)),
        () -> assertThat(sortertTrinnvisSkattesatsListe.get(0).getSats()).isEqualTo(BigDecimal.valueOf(1.9))
    );
  }
}
