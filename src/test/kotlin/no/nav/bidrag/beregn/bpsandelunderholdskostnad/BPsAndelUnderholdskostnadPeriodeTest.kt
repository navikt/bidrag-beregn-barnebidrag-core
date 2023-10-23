package no.nav.bidrag.beregn.bpsandelunderholdskostnad

import no.nav.bidrag.beregn.TestUtil.INNTEKT_BB_REFERANSE
import no.nav.bidrag.beregn.TestUtil.INNTEKT_BM_REFERANSE
import no.nav.bidrag.beregn.TestUtil.INNTEKT_BP_REFERANSE
import no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntektPeriode
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.UnderholdskostnadPeriode
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode.Companion.getInstance
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.domain.enums.AvvikType
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

internal class BPsAndelUnderholdskostnadPeriodeTest {

    private val bPsAndelunderholdskostnadPeriode = getInstance()

    @Test
    @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
    fun testPeriodisering() {
        val resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(lagGrunnlag(beregnDatoFra = "2018-07-01", beregnDatoTil = "2020-08-01"))

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(3) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2018-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.352)) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2020-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2020-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoTil).isNull() }
        )
    }

    @Test
    @DisplayName(
        "Test av beregning med gamle og nye regler. Resultat for perioder før 2009 skal angis i nærmeste sjettedel." +
            "Det skal også lages brudd i periode ved overgang til nye regler 01.01.2009"
    )
    fun testBeregningMedGamleOgNyeRegler() {
        val underholdskostnadPeriodeListe = listOf(
            UnderholdskostnadPeriode(
                referanse = UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                underholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2008-01-01"), datoTil = LocalDate.parse("2009-06-01")),
                belop = BigDecimal.valueOf(1000)
            ),
            UnderholdskostnadPeriode(
                referanse = UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                underholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2009-06-01"), datoTil = LocalDate.parse("2020-08-01")),
                belop = BigDecimal.valueOf(1000)
            )
        )

        val inntektBPPeriodeListe = listOf(
            InntektPeriode(
                referanse = INNTEKT_BP_REFERANSE + "_1",
                inntektPeriode = Periode(datoFom = LocalDate.parse("2008-01-01"), datoTil = LocalDate.parse("2009-06-01")),
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(300000),
                deltFordel = false,
                skatteklasse2 = false
            ),
            InntektPeriode(
                referanse = INNTEKT_BP_REFERANSE + "_2",
                inntektPeriode = Periode(datoFom = LocalDate.parse("2009-06-01"), datoTil = LocalDate.parse("2020-08-01")),
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(3000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val inntektBMPeriodeListe = listOf(
            InntektPeriode(
                referanse = INNTEKT_BM_REFERANSE + "_1",
                inntektPeriode = Periode(datoFom = LocalDate.parse("2008-01-01"), datoTil = LocalDate.parse("2009-06-01")),
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(400000),
                deltFordel = false,
                skatteklasse2 = false
            ),
            InntektPeriode(
                referanse = INNTEKT_BM_REFERANSE + "_2",
                inntektPeriode = Periode(datoFom = LocalDate.parse("2009-06-01"), datoTil = LocalDate.parse("2020-08-01")),
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(400000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val inntektBBPeriodeListe = listOf(
            InntektPeriode(
                referanse = INNTEKT_BB_REFERANSE + "_1",
                inntektPeriode = Periode(datoFom = LocalDate.parse("2008-01-01"), datoTil = LocalDate.parse("2009-06-01")),
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(40000),
                deltFordel = false,
                skatteklasse2 = false
            ),
            InntektPeriode(
                referanse = INNTEKT_BB_REFERANSE + "_2",
                inntektPeriode = Periode(datoFom = LocalDate.parse("2009-06-01"), datoTil = LocalDate.parse("2020-08-01")),
                type = "LONN_SKE",
                belop = BigDecimal.valueOf(4000000),
                deltFordel = false,
                skatteklasse2 = false
            )
        )

        val sjablonPeriodeListe = listOf(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2008-01-01"), datoTil = LocalDate.parse("2008-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1600)))
                )
            ),
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2008-07-01"), datoTil = LocalDate.parse("2019-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1700)))
                )
            )
        )

        val grunnlag = BeregnBPsAndelUnderholdskostnadGrunnlag(
            beregnDatoFra = LocalDate.parse("2008-01-01"),
            beregnDatoTil = LocalDate.parse("2009-07-01"),
            soknadsbarnPersonId = 1,
            underholdskostnadPeriodeListe = underholdskostnadPeriodeListe,
            inntektBPPeriodeListe = inntektBPPeriodeListe,
            inntektBMPeriodeListe = inntektBMPeriodeListe,
            inntektBBPeriodeListe = inntektBBPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )

        val resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(grunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(4) }, // Gamle regler
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2008-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2008-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.333)) }, // Gamle regler
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2008-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2009-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.333)) }, // Nye regler
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2009-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2009-06-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultat.andelProsent).isEqualTo(BigDecimal.valueOf(0.429)) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoFom).isEqualTo(LocalDate.parse("2009-06-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoTil).isEqualTo(LocalDate.parse("2009-07-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultat.andelProsent).isEqualTo(BigDecimal.ZERO) }
        )
    }

    @Test
    @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
    fun testGrunnlagMedAvvik() {
        val avvikListe = bPsAndelunderholdskostnadPeriode.validerInput(lagGrunnlag("2016-01-01", "2021-01-01"))

        assertAll(
            Executable { assertThat(avvikListe).isNotEmpty() },
            Executable { assertThat(avvikListe).hasSize(8) },
            Executable {
                assertThat(avvikListe[2].avvikTekst)
                    .isEqualTo("Første dato i inntektBPPeriodeListe (2018-01-01) er etter beregnDatoFra (2016-01-01)")
            },
            Executable { assertThat(avvikListe[0].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },
            Executable {
                assertThat(avvikListe[3].avvikTekst)
                    .isEqualTo("Siste dato i inntektBPPeriodeListe (2020-08-01) er før beregnDatoTil (2021-01-01)")
            },
            Executable { assertThat(avvikListe[1].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) }
        )
    }

    @Test
    @DisplayName("Test utvidet barnetrygd BM")
    fun testUtvidetBarnetrygdBM() {
        val resultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(lagGrunnlagMedInntekterTilJustering())

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2019-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-04-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].grunnlag.inntektBMListe.size).isEqualTo(3) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].grunnlag.inntektBMListe[0].type).isEqualTo("PENSJON_KORRIGERT_BARNETILLEGG") },
            Executable { assertThat(resultat.resultatPeriodeListe[0].grunnlag.inntektBMListe[0].belop).isEqualTo(BigDecimal.valueOf(400000)) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].grunnlag.inntektBMListe[1].type).isEqualTo("UTVIDET_BARNETRYGD") },
            Executable { assertThat(resultat.resultatPeriodeListe[0].grunnlag.inntektBMListe[1].belop).isEqualTo(BigDecimal.valueOf(12000)) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].grunnlag.inntektBMListe[2].type).isEqualTo("FORDEL_SAERFRADRAG_ENSLIG_FORSORGER") },
            Executable { assertThat(resultat.resultatPeriodeListe[0].grunnlag.inntektBMListe[2].belop).isEqualTo(BigDecimal.valueOf(13000)) }
        )
    }

    private fun lagGrunnlag(beregnDatoFra: String, beregnDatoTil: String) =
        BeregnBPsAndelUnderholdskostnadGrunnlag(
            beregnDatoFra = LocalDate.parse(beregnDatoFra),
            beregnDatoTil = LocalDate.parse(beregnDatoTil),
            soknadsbarnPersonId = 1,
            underholdskostnadPeriodeListe = listOf(
                UnderholdskostnadPeriode(
                    referanse = UNDERHOLDSKOSTNAD_REFERANSE,
                    underholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                    belop = BigDecimal.valueOf(1000)
                )
            ),
            inntektBPPeriodeListe = listOf(
                InntektPeriode(
                    referanse = INNTEKT_BP_REFERANSE,
                    inntektPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                    type = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                    belop = BigDecimal.valueOf(217666),
                    deltFordel = false,
                    skatteklasse2 = false
                )
            ),
            inntektBMPeriodeListe = listOf(
                InntektPeriode(
                    referanse = INNTEKT_BM_REFERANSE,
                    inntektPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                    type = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                    belop = BigDecimal.valueOf(400000),
                    deltFordel = false,
                    skatteklasse2 = false
                )
            ),
            inntektBBPeriodeListe = listOf(
                InntektPeriode(
                    referanse = INNTEKT_BB_REFERANSE,
                    inntektPeriode = Periode(datoFom = LocalDate.parse("2018-01-01"), datoTil = LocalDate.parse("2020-08-01")),
                    type = "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                    belop = BigDecimal.valueOf(40000),
                    deltFordel = false,
                    skatteklasse2 = false
                )
            ),
            sjablonPeriodeListe = lagSjablonGrunnlag()
        )

    private fun lagGrunnlagMedInntekterTilJustering(): BeregnBPsAndelUnderholdskostnadGrunnlag {
        val beregnDatoFra = LocalDate.parse("2019-01-01")
        val beregnDatoTil = LocalDate.parse("2019-04-01")

        return BeregnBPsAndelUnderholdskostnadGrunnlag(
            beregnDatoFra = LocalDate.parse("2019-01-01"),
            beregnDatoTil = LocalDate.parse("2019-04-01"),
            soknadsbarnPersonId = 1,
            underholdskostnadPeriodeListe = listOf(
                UnderholdskostnadPeriode(
                    referanse = UNDERHOLDSKOSTNAD_REFERANSE,
                    underholdskostnadPeriode = Periode(datoFom = beregnDatoFra, datoTil = beregnDatoTil),
                    belop = BigDecimal.valueOf(1000)
                )
            ),
            inntektBPPeriodeListe = listOf(
                InntektPeriode(
                    referanse = INNTEKT_BP_REFERANSE,
                    inntektPeriode = Periode(datoFom = beregnDatoFra, datoTil = beregnDatoTil),
                    type = "SKATTEGRUNNLAG_KORRIGERT_BARNETILLEGG",
                    belop = BigDecimal.valueOf(666001),
                    deltFordel = false,
                    skatteklasse2 = false
                )
            ),
            inntektBMPeriodeListe = listOf(
                InntektPeriode(
                    referanse = INNTEKT_BM_REFERANSE + "_1",
                    inntektPeriode = Periode(datoFom = beregnDatoFra, datoTil = beregnDatoTil),
                    type = "PENSJON_KORRIGERT_BARNETILLEGG",
                    belop = BigDecimal.valueOf(400000),
                    deltFordel = false,
                    skatteklasse2 = false
                ),
                InntektPeriode(
                    referanse = INNTEKT_BM_REFERANSE + "_2",
                    inntektPeriode = Periode(datoFom = beregnDatoFra, datoTil = beregnDatoTil),
                    type = "UTVIDET_BARNETRYGD",
                    belop = BigDecimal.valueOf(12000),
                    deltFordel = false,
                    skatteklasse2 = false
                )
            ),
            inntektBBPeriodeListe = emptyList(),
            sjablonPeriodeListe = lagSjablonGrunnlagUtvidetBarnetrygd()
        )
    }

    private fun lagSjablonGrunnlag() = listOf(
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2018-07-01"), datoTil = LocalDate.parse("2019-06-30")),
            sjablon = Sjablon(
                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1600)))
            )
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2020-06-30")),
            sjablon = Sjablon(
                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1640)))
            )
        ),
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2020-07-01"), datoTil = null),
            sjablon = Sjablon(
                navn = SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(1670)))
            )
        )
    )

    private fun lagSjablonGrunnlagUtvidetBarnetrygd() = listOf(

        // Sjablon 0030
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-01-01")),
            sjablon = Sjablon(
                navn = SjablonTallNavn.OVRE_INNTEKTSGRENSE_IKKE_I_SKATTEPOSISJON_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(105000)))
            )
        ),

        // Sjablon 0031
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-01-01")),
            sjablon = Sjablon(
                navn = SjablonTallNavn.NEDRE_INNTEKTSGRENSE_FULL_SKATTEPOSISJON_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(105000)))
            )
        ),

        // Sjablon 0039
        SjablonPeriode(
            sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2020-01-01")),
            sjablon = Sjablon(
                navn = SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.navn,
                nokkelListe = emptyList(),
                innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(13000)))
            )
        )
    )
}
