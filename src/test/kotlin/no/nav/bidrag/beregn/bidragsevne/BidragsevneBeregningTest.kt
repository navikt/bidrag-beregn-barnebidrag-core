package no.nav.bidrag.beregn.bidragsevne

import no.nav.bidrag.beregn.TestUtil.BARN_I_HUSSTAND_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BOSTATUS_REFERANSE
import no.nav.bidrag.beregn.TestUtil.INNTEKT_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SAERFRADRAG_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SKATTEKLASSE_REFERANSE
import no.nav.bidrag.beregn.TestUtil.byggSjablonPeriodeListe
import no.nav.bidrag.beregn.TestUtil.byggSjabloner
import no.nav.bidrag.beregn.bidragsevne.beregning.BidragsevneBeregning.Companion.getInstance
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstand
import no.nav.bidrag.beregn.bidragsevne.bo.Bostatus
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt
import no.nav.bidrag.beregn.bidragsevne.bo.Saerfradrag
import no.nav.bidrag.beregn.bidragsevne.bo.Skatteklasse
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.util.SjablonUtil.hentSjablonverdi
import no.nav.bidrag.beregn.felles.util.SjablonUtil.hentTrinnvisSkattesats
import no.nav.bidrag.domene.enums.beregning.Særfradragskode
import no.nav.bidrag.domene.enums.person.Bostatuskode
import no.nav.bidrag.domene.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

@DisplayName("Test av beregning av bidragsevne")
internal class BidragsevneBeregningTest {
    private val sjablonListe = byggSjabloner()
    private var sjablonPeriodeListe = byggSjablonPeriodeListe().toMutableList()
    private val bidragsevneBeregning = getInstance()

    @Test
    @DisplayName("Skal beregne bidragsevne med inntekt 1000000")
    fun skalBeregneBidragsevneMedInntekt1000000() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(1000000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.ALENE),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.INGEN),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertThat(BigDecimal.valueOf(31859)).isEqualTo(belop)
    }

    @Test
    @DisplayName("Skal beregne bidragsevne med inntekt 520000")
    fun skalBeregneBidragsevneMedInntekt520000() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(520000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.ALENE),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.INGEN),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop, inntekt25Prosent) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertAll(
            Executable { assertThat(BigDecimal.valueOf(8322)).isEqualTo(belop) },
            Executable { assertThat(BigDecimal.valueOf(10833)).isEqualTo(inntekt25Prosent) },
        )
    }

    @Test
    @DisplayName("Skal beregne bidragsevne med inntekt 600000")
    fun skalBeregneBidragsevneMedInntekt600000() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(600000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.ALENE),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 0.0),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.HELT),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertThat(BigDecimal.valueOf(17617)).isEqualTo(belop)
    }

    @Test
    @DisplayName("Skal beregne bidragsevne med inntekt 666000")
    fun skalBeregneBidragsevneMedInntekt666000() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(666000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.ALENE),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 3.0),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.INGEN),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertThat(BigDecimal.valueOf(8424)).isEqualTo(belop)
    }

    @Test
    @DisplayName("Skal beregne bidragsevne lik 0 når evne er negativ")
    fun skalBeregneBidragsevneLik0NaarEvneErNegativ() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(100000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.IKKE_MED_FORELDER),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 1.0),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.HELT),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertThat(BigDecimal.ZERO).isEqualTo(belop)
    }

    @Test
    @DisplayName("Skal ikke legge fordel skatteklasse 2 til beregnet evne for skatteklasse lik 1")
    fun skalIkkeLeggeFordelSkatteklasse2TilBeregnetEvneForSkatteklasseLik1() {
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                sjablon =
                Sjablon(
                    navn = SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe =
                    listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(12000)),
                    ),
                ),
            ),
        )
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(666000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.ALENE),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 3.0),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.INGEN),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertThat(BigDecimal.valueOf(8424)).isEqualTo(belop)
    }

    @Test
    @DisplayName("Skal legge fordel skatteklasse 2 til beregnet evne for skatteklasse lik 2")
    fun skalLeggeFordelSkatteklasse2TilBeregnetEvneForSkatteklasseLik2() {
        sjablonPeriodeListe.add(
            0,
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                sjablon =
                Sjablon(
                    navn = SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe =
                    listOf(
                        element = SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(12000)),
                    ),
                ),
            ),
        )
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(666000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 2),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.ALENE),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 3.0),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.INGEN),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertThat(BigDecimal.valueOf(9424)).isEqualTo(belop)
    }

    @Test
    @DisplayName("Skal bruke halvt barn pga delt bosted")
    fun skalBrukeHalvtBarnPgaDeltBosted() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(666000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 2),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.IKKE_MED_FORELDER),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 0.5),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.INGEN),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertThat(BigDecimal.valueOf(23314)).isEqualTo(belop)
    }

    @Test
    @DisplayName("Skal bruke personfradrag skatteklasse 2 hvis skatteklasse 2 er angitt")
    fun skalBrukePersonfradragSkatteklasse2HvisSkatteklasse2ErAngitt() {
        sjablonPeriodeListe.add(
            0,
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("9999-12-31")),
                sjablon =
                Sjablon(
                    navn = SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELØP.navn,
                    nokkelListe = emptyList(),
                    innholdListe =
                    listOf(
                        SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(24000)),
                    ),
                ),
            ),
        )
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(666000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 2),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.ALENE),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 3.0),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.INGEN),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertThat(BigDecimal.valueOf(7923)).isEqualTo(belop)
    }

    @Test
    @DisplayName("Skal bruke halvt særfradrag")
    fun skalBrukeHalvtSaerfradrag() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(666000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.ALENE),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 3.0),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.HALVT),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertThat(BigDecimal.valueOf(8965)).isEqualTo(belop)
    }

    @Test
    @DisplayName("Skal bruke bostatus med andre")
    fun skalBrukeBostatusMedAndre() {
        val grunnlagBeregning =
            GrunnlagBeregning(
                inntektListe = listOf(Inntekt(referanse = INNTEKT_REFERANSE, type = "LONN_SKE", belop = BigDecimal.valueOf(666000))),
                skatteklasse = Skatteklasse(referanse = SKATTEKLASSE_REFERANSE, skatteklasse = 1),
                bostatus = Bostatus(referanse = BOSTATUS_REFERANSE, kode = Bostatuskode.IKKE_MED_FORELDER),
                barnIHusstand = BarnIHusstand(referanse = BARN_I_HUSSTAND_REFERANSE, antallBarn = 3.0),
                saerfradrag = Saerfradrag(referanse = SAERFRADRAG_REFERANSE, kode = Særfradragskode.HALVT),
                sjablonListe = sjablonPeriodeListe,
            )

        val (belop) = bidragsevneBeregning.beregn(grunnlagBeregning)

        assertThat(BigDecimal.valueOf(14253)).isEqualTo(belop)
    }

    @Test
    @DisplayName("Skal hente sjablonverdier for trinnvis skattesats")
    fun skalhenteSjablonverdierForTrinnvisSkattesats() {
        val sjablonVerdiSkattesats =
            hentSjablonverdi(
                sjablonListe = sjablonListe,
                sjablonTallNavn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT,
            )
        val sortertTrinnvisSkattesatsListe =
            hentTrinnvisSkattesats(sjablonListe = sjablonListe, sjablonNavn = SjablonNavn.TRINNVIS_SKATTESATS)

        assertAll(
            Executable { assertThat(sjablonVerdiSkattesats).isEqualTo(BigDecimal.valueOf(22)) },
            Executable { assertThat(sortertTrinnvisSkattesatsListe.size).isEqualTo(4) },
            Executable { assertThat(sortertTrinnvisSkattesatsListe[0].inntektGrense).isEqualTo(BigDecimal.valueOf(180800)) },
            Executable { assertThat(sortertTrinnvisSkattesatsListe[0].sats).isEqualTo(BigDecimal.valueOf(1.9)) },
        )
    }
}
