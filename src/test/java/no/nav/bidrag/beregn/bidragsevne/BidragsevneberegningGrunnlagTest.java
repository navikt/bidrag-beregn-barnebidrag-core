package no.nav.bidrag.beregn.bidragsevne;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test hent av sjablonverdi")
class BidragsevneberegningGrunnlagTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @Test
  void hentSjablon() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();
    inntekter.add(new Inntekt(InntektType.LONN_TREKK, BigDecimal.valueOf(1000000)));

    var sjablonVerdi =
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT);
    assertThat(sjablonVerdi).isEqualTo(BigDecimal.valueOf(22));

    var sortertTrinnvisSkattesatsListe = SjablonUtil
        .hentTrinnvisSkattesats(sjablonListe, SjablonNavn.TRINNVIS_SKATTESATS);

    assertThat(sortertTrinnvisSkattesatsListe.size()).isEqualTo(4);
    assertThat(sortertTrinnvisSkattesatsListe.get(0).getInntektGrense()).isEqualTo(BigDecimal.valueOf(180800));
    assertThat(sortertTrinnvisSkattesatsListe.get(0).getSats()).isEqualTo(BigDecimal.valueOf(1.9));

  }
}