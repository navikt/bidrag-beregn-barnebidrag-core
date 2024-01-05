package no.nav.bidrag.beregn.bidragsevne

import no.nav.bidrag.beregn.TestUtil.byggSjabloner
import no.nav.bidrag.beregn.felles.util.SjablonUtil.hentSjablonverdi
import no.nav.bidrag.beregn.felles.util.SjablonUtil.hentTrinnvisSkattesats
import no.nav.bidrag.domene.enums.sjablon.SjablonNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal

internal class BidragsevneBeregningGrunnlagTest {
    private val sjablonListe = byggSjabloner()

    @Test
    fun hentSjablon() {
        val sjablonverdiInntekt =
            hentSjablonverdi(
                sjablonListe = sjablonListe,
                sjablonTallNavn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT,
            )
        val sortertTrinnvisSkattesatsListe =
            hentTrinnvisSkattesats(sjablonListe = sjablonListe, sjablonNavn = SjablonNavn.TRINNVIS_SKATTESATS)

        assertAll(
            Executable { assertThat(sjablonverdiInntekt).isEqualTo(BigDecimal.valueOf(22)) },
            Executable { assertThat(sortertTrinnvisSkattesatsListe).hasSize(4) },
            Executable { assertThat(sortertTrinnvisSkattesatsListe[0].inntektGrense).isEqualTo(BigDecimal.valueOf(180800)) },
            Executable { assertThat(sortertTrinnvisSkattesatsListe[0].sats).isEqualTo(BigDecimal.valueOf(1.9)) },
        )
    }
}
