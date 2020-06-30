package no.nav.bidrag.beregn.nettobarnetilsyn;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test hent av sjablonverdier for trinnvis skattesats")
public class NettoBarnetilsynBeregningGrunnlagTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @Test
  void hentSjablon() {

    var sortertTrinnvisSkattesatsListe = SjablonUtil
        .hentTrinnvisSkattesats(sjablonListe, SjablonNavn.TRINNVIS_SKATTESATS);

    assertThat(sortertTrinnvisSkattesatsListe.size()).isEqualTo(4);
    assertThat(sortertTrinnvisSkattesatsListe.get(0).getInntektGrense()).isEqualTo(174500d);
    assertThat(sortertTrinnvisSkattesatsListe.get(0).getSats()).isEqualTo(1.9d);

  }
}
