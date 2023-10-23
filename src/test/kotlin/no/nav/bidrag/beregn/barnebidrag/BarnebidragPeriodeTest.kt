package no.nav.bidrag.beregn.barnebidrag

import no.nav.bidrag.beregn.TestUtil.ANDRE_LOPENDE_BIDRAG_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_BM_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_BP_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_FORSVARET_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BIDRAGSEVNE_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.TestUtil.DELT_BOSTED_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidragPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnadPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaretPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag
import no.nav.bidrag.beregn.barnebidrag.bo.BidragsevnePeriode
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBostedPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.SamvaersfradragPeriode
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode.Companion.getInstance
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.domain.enums.AvvikType
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeBarnebidrag
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal
import java.time.LocalDate

internal class BarnebidragPeriodeTest {

    private var sjablonPeriodeListe = mutableListOf<SjablonPeriode>()
    private val barnebidragPeriode = getInstance()

    @Test
    @DisplayName("Test med ett barn og splitt på bidragsevne")
    fun enkelTestEttBarnToBidragsevner() {
        val beregnDatoFra = LocalDate.parse("2019-08-01")
        val beregnDatoTil = LocalDate.parse("2020-01-01")

        lagSjablonliste()

        val bidragsevnePeriodeListe = listOf(
            BidragsevnePeriode(
                referanse = BIDRAGSEVNE_REFERANSE + "_1",
                bidragsevnePeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-10-01")),
                belop = BigDecimal.valueOf(15000),
                tjuefemProsentInntekt = BigDecimal.valueOf(16000)
            ),
            BidragsevnePeriode(
                referanse = BIDRAGSEVNE_REFERANSE + "_2",
                bidragsevnePeriode = Periode(datoFom = LocalDate.parse("2019-10-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.valueOf(17000),
                tjuefemProsentInntekt = BigDecimal.valueOf(16000)
            )
        )

        val bPsAndelUnderholdskostnadListe = listOf(
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 1,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                andelProsent = BigDecimal.valueOf(0.8),
                andelBelop = BigDecimal.valueOf(16000),
                barnetErSelvforsorget = false
            )
        )

        val samvaersfradragPeriodeListe = listOf(
            SamvaersfradragPeriode(
                soknadsbarnPersonId = 1,
                referanse = SAMVAERSFRADRAG_REFERANSE,
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO
            )
        )

        val deltBostedPeriodeListe = listOf(
            DeltBostedPeriode(
                soknadsbarnPersonId = 1,
                referanse = DELT_BOSTED_REFERANSE,
                deltBostedPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                deltBostedIPeriode = false
            )
        )

        val barnetilleggBPPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BP_REFERANSE,
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggBMPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BM_REFERANSE,
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggForsvaretPeriodeListe = listOf(
            BarnetilleggForsvaretPeriode(
                referanse = BARNETILLEGG_FORSVARET_REFERANSE,
                barnetilleggForsvaretPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnetilleggForsvaretIPeriode = false
            )
        )

        val andreLopendeBidragPeriodeListe = listOf(
            AndreLopendeBidragPeriode(
                referanse = ANDRE_LOPENDE_BIDRAG_REFERANSE,
                andreLopendeBidragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnPersonId = 1,
                bidragBelop = BigDecimal.ZERO,
                samvaersfradragBelop = BigDecimal.ZERO
            )
        )

        val beregnBarnebidragGrunnlag = BeregnBarnebidragGrunnlag(
            beregnDatoFra = beregnDatoFra,
            beregnDatoTil = beregnDatoTil,
            bidragsevnePeriodeListe = bidragsevnePeriodeListe,
            bPsAndelUnderholdskostnadPeriodeListe = bPsAndelUnderholdskostnadListe,
            samvaersfradragPeriodeListe = samvaersfradragPeriodeListe,
            deltBostedPeriodeListe = deltBostedPeriodeListe,
            barnetilleggBPPeriodeListe = barnetilleggBPPeriodeListe,
            barnetilleggBMPeriodeListe = barnetilleggBMPeriodeListe,
            barnetilleggForsvaretPeriodeListe = barnetilleggForsvaretPeriodeListe,
            andreLopendeBidragPeriodeListe = andreLopendeBidragPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )

        val resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag)

        assertAll(
            Executable { assertThat(resultat).isNotNull() },
            Executable { assertThat(resultat.resultatPeriodeListe).isNotEmpty() },
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(2) },

            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe.size).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2019-08-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-10-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.valueOf(15000))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) },

            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe.size).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-10-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].belop.compareTo(BigDecimal.valueOf(16000))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].kode).isEqualTo(ResultatKodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG) }
        )
    }

    @Test
    @DisplayName("Test med tre barn i to perioder, to barn i periode 3, barnetilleggForsvaret i siste periode")
    fun testVariabeltAntallBarn() {
        val beregnDatoFra = LocalDate.parse("2019-08-01")
        val beregnDatoTil = LocalDate.parse("2020-01-01")

        lagSjablonliste()

        val bidragsevnePeriodeListe = listOf(
            BidragsevnePeriode(
                referanse = BIDRAGSEVNE_REFERANSE + "_1",
                bidragsevnePeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-10-01")),
                belop = BigDecimal.valueOf(15000),
                tjuefemProsentInntekt = BigDecimal.valueOf(16000)
            ),
            BidragsevnePeriode(
                referanse = BIDRAGSEVNE_REFERANSE + "_2",
                bidragsevnePeriode = Periode(datoFom = LocalDate.parse("2019-10-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.valueOf(17000),
                tjuefemProsentInntekt = BigDecimal.valueOf(16000)
            )
        )

        val bPsAndelUnderholdskostnadListe = listOf(
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 1,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-10-01")),
                andelProsent = BigDecimal.valueOf(0.80),
                andelBelop = BigDecimal.valueOf(16000),
                barnetErSelvforsorget = false
            ),
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 2,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                andelProsent = BigDecimal.valueOf(0.80),
                andelBelop = BigDecimal.valueOf(16000),
                barnetErSelvforsorget = false
            ),
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 3,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3",
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                andelProsent = BigDecimal.valueOf(0.80),
                andelBelop = BigDecimal.valueOf(16000),
                barnetErSelvforsorget = false
            )
        )

        val samvaersfradragPeriodeListe = listOf(
            SamvaersfradragPeriode(
                soknadsbarnPersonId = 1,
                referanse = SAMVAERSFRADRAG_REFERANSE + "_1",
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-10-01")),
                belop = BigDecimal.ZERO
            ),
            SamvaersfradragPeriode(
                soknadsbarnPersonId = 2,
                referanse = SAMVAERSFRADRAG_REFERANSE + "_2",
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-09-01")),
                belop = BigDecimal.ZERO
            ),
            SamvaersfradragPeriode(
                soknadsbarnPersonId = 2,
                referanse = SAMVAERSFRADRAG_REFERANSE + "_3",
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2019-09-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.valueOf(1000)
            ),
            SamvaersfradragPeriode(
                soknadsbarnPersonId = 3,
                referanse = SAMVAERSFRADRAG_REFERANSE + "_4",
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO
            )
        )

        val deltBostedPeriodeListe = listOf(
            DeltBostedPeriode(
                soknadsbarnPersonId = 1,
                referanse = DELT_BOSTED_REFERANSE + "_1",
                deltBostedPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-10-01")),
                deltBostedIPeriode = false
            ),
            DeltBostedPeriode(
                soknadsbarnPersonId = 2,
                referanse = DELT_BOSTED_REFERANSE + "_2",
                deltBostedPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                deltBostedIPeriode = false
            ),
            DeltBostedPeriode(
                soknadsbarnPersonId = 3,
                referanse = DELT_BOSTED_REFERANSE + "_3",
                deltBostedPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                deltBostedIPeriode = false
            )
        )

        val barnetilleggBPPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BP_REFERANSE + "_1",
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-10-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            ),
            BarnetilleggPeriode(
                soknadsbarnPersonId = 2,
                referanse = BARNETILLEGG_BP_REFERANSE + "_2",
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            ),
            BarnetilleggPeriode(
                soknadsbarnPersonId = 3,
                referanse = BARNETILLEGG_BP_REFERANSE + "_3",
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggBMPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BM_REFERANSE + "_1",
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-10-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            ),
            BarnetilleggPeriode(
                soknadsbarnPersonId = 2,
                referanse = BARNETILLEGG_BM_REFERANSE + "_2",
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            ),
            BarnetilleggPeriode(
                soknadsbarnPersonId = 3,
                referanse = BARNETILLEGG_BM_REFERANSE + "_3",
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggForsvaretPeriodeListe = listOf(
            BarnetilleggForsvaretPeriode(
                referanse = BARNETILLEGG_FORSVARET_REFERANSE + "_1",
                barnetilleggForsvaretPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-12-01")),
                barnetilleggForsvaretIPeriode = false
            ),
            BarnetilleggForsvaretPeriode(
                referanse = BARNETILLEGG_FORSVARET_REFERANSE + "_2",
                barnetilleggForsvaretPeriode = Periode(datoFom = LocalDate.parse("2019-12-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnetilleggForsvaretIPeriode = true
            )
        )

        val andreLopendeBidragPeriodeListe = listOf(
            AndreLopendeBidragPeriode(
                referanse = ANDRE_LOPENDE_BIDRAG_REFERANSE,
                andreLopendeBidragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnPersonId = 1,
                bidragBelop = BigDecimal.ZERO,
                samvaersfradragBelop = BigDecimal.ZERO
            )
        )

        val beregnBarnebidragGrunnlag = BeregnBarnebidragGrunnlag(
            beregnDatoFra = beregnDatoFra,
            beregnDatoTil = beregnDatoTil,
            bidragsevnePeriodeListe = bidragsevnePeriodeListe,
            bPsAndelUnderholdskostnadPeriodeListe = bPsAndelUnderholdskostnadListe,
            samvaersfradragPeriodeListe = samvaersfradragPeriodeListe,
            deltBostedPeriodeListe = deltBostedPeriodeListe,
            barnetilleggBPPeriodeListe = barnetilleggBPPeriodeListe,
            barnetilleggBMPeriodeListe = barnetilleggBMPeriodeListe,
            barnetilleggForsvaretPeriodeListe = barnetilleggForsvaretPeriodeListe,
            andreLopendeBidragPeriodeListe = andreLopendeBidragPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )

        val resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag)

        assertAll(
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(4) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe.size).isEqualTo(3) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe.size).isEqualTo(3) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatListe.size).isEqualTo(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultatListe.size).isEqualTo(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2019-08-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-09-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-09-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2019-10-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoFom).isEqualTo(LocalDate.parse("2019-10-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].periode.datoTil).isEqualTo(LocalDate.parse("2019-12-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoFom).isEqualTo(LocalDate.parse("2019-12-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },

            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.valueOf(5000))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[1].belop.compareTo(BigDecimal.valueOf(5000))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[2].belop.compareTo(BigDecimal.valueOf(5000))).isZero() },

            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].belop.compareTo(BigDecimal.valueOf(5000))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[1].belop.compareTo(BigDecimal.valueOf(4000))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[2].belop.compareTo(BigDecimal.valueOf(5000))).isZero() },

            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatListe[0].belop.compareTo(BigDecimal.valueOf(7000))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatListe[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT) },
            Executable { assertThat(resultat.resultatPeriodeListe[2].resultatListe[1].belop.compareTo(BigDecimal.valueOf(8000))).isZero() },

            Executable { assertThat(resultat.resultatPeriodeListe[3].resultatListe[0].belop.compareTo(BigDecimal.valueOf(3001))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultatListe[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET) },
            Executable { assertThat(resultat.resultatPeriodeListe[3].resultatListe[1].belop.compareTo(BigDecimal.valueOf(4001))).isZero() }
        )
    }

    @Test
    @DisplayName(
        "Test med to barn i to perioder, der det ene barnet har delt bosted. Ved delt bosted skal BPs andel av underholdskostnad" +
            "reduseres med 50 prosentpoeng. I periode 2 blir andelen regnet om til under 50% og bidrag skal ikke beregnes for " +
            "dette barnet og hele evnen skal gis til det andre barnet"
    )
    fun testDeltBosted() {
        val beregnDatoFra = LocalDate.parse("2019-08-01")
        val beregnDatoTil = LocalDate.parse("2020-01-01")

        lagSjablonliste()

        val bidragsevnePeriodeListe = listOf(
            BidragsevnePeriode(
                referanse = BIDRAGSEVNE_REFERANSE,
                bidragsevnePeriode = Periode(datoFom = LocalDate.parse("2018-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.valueOf(1000),
                tjuefemProsentInntekt = BigDecimal.valueOf(1600)
            )
        )

        val bPsAndelUnderholdskostnadListe = listOf(
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 1,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-11-01")),
                andelProsent = BigDecimal.valueOf(0.4),
                andelBelop = BigDecimal.valueOf(4000),
                barnetErSelvforsorget = false
            ),
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 1,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-11-01"), datoTil = LocalDate.parse("2020-01-01")),
                andelProsent = BigDecimal.valueOf(0.6),
                andelBelop = BigDecimal.valueOf(6000),
                barnetErSelvforsorget = false
            ),
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 2,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3",
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-11-01")),
                andelProsent = BigDecimal.valueOf(0.6),
                andelBelop = BigDecimal.valueOf(6000),
                barnetErSelvforsorget = false
            ),
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 2,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_4",
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-11-01"), datoTil = LocalDate.parse("2020-01-01")),
                andelProsent = BigDecimal.valueOf(0.4),
                andelBelop = BigDecimal.valueOf(4000),
                barnetErSelvforsorget = false
            )
        )

        val samvaersfradragPeriodeListe = listOf(
            SamvaersfradragPeriode(
                soknadsbarnPersonId = 1,
                referanse = SAMVAERSFRADRAG_REFERANSE + "_1",
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO
            ),
            SamvaersfradragPeriode(
                soknadsbarnPersonId = 2,
                referanse = SAMVAERSFRADRAG_REFERANSE + "_2",
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO
            )
        )

        val deltBostedPeriodeListe = listOf(
            DeltBostedPeriode(
                soknadsbarnPersonId = 1,
                referanse = DELT_BOSTED_REFERANSE + "_1",
                deltBostedPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                deltBostedIPeriode = false
            ),
            DeltBostedPeriode(
                soknadsbarnPersonId = 2,
                referanse = DELT_BOSTED_REFERANSE + "_2",
                deltBostedPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                deltBostedIPeriode = true
            )
        )

        val barnetilleggBPPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BP_REFERANSE + "_1",
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            ),
            BarnetilleggPeriode(
                soknadsbarnPersonId = 2,
                referanse = BARNETILLEGG_BP_REFERANSE + "_2",
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            ),
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BM_REFERANSE + "_1",
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggBMPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 2,
                referanse = BARNETILLEGG_BM_REFERANSE + "_2",
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggForsvaretPeriodeListe = listOf(
            BarnetilleggForsvaretPeriode(
                referanse = BARNETILLEGG_FORSVARET_REFERANSE,
                barnetilleggForsvaretPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnetilleggForsvaretIPeriode = false
            )
        )

        val andreLopendeBidragPeriodeListe = listOf(
            AndreLopendeBidragPeriode(
                referanse = ANDRE_LOPENDE_BIDRAG_REFERANSE,
                andreLopendeBidragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnPersonId = 1,
                bidragBelop = BigDecimal.ZERO,
                samvaersfradragBelop = BigDecimal.ZERO
            )
        )

        val beregnBarnebidragGrunnlag = BeregnBarnebidragGrunnlag(
            beregnDatoFra = beregnDatoFra,
            beregnDatoTil = beregnDatoTil,
            bidragsevnePeriodeListe = bidragsevnePeriodeListe,
            bPsAndelUnderholdskostnadPeriodeListe = bPsAndelUnderholdskostnadListe,
            samvaersfradragPeriodeListe = samvaersfradragPeriodeListe,
            deltBostedPeriodeListe = deltBostedPeriodeListe,
            barnetilleggBPPeriodeListe = barnetilleggBPPeriodeListe,
            barnetilleggBMPeriodeListe = barnetilleggBMPeriodeListe,
            barnetilleggForsvaretPeriodeListe = barnetilleggForsvaretPeriodeListe,
            andreLopendeBidragPeriodeListe = andreLopendeBidragPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )

        val resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag)

        assertAll(
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe.size).isEqualTo(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe.size).isEqualTo(2) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoFom).isEqualTo(LocalDate.parse("2019-08-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].periode.datoTil).isEqualTo(LocalDate.parse("2019-11-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoFom).isEqualTo(LocalDate.parse("2019-11-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].periode.datoTil).isEqualTo(LocalDate.parse("2020-01-01")) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.valueOf(800))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[1].belop.compareTo(BigDecimal.valueOf(200))).isZero() },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[0].grunnlag.grunnlagPerBarnListe[1].bPsAndelUnderholdskostnad.andelProsent.compareTo(
                        BigDecimal.valueOf(0.1)
                    )
                ).isZero()
            },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].belop.compareTo(BigDecimal.valueOf(1000))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[1].belop.compareTo(BigDecimal.ZERO)).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) },
            Executable {
                assertThat(
                    resultat.resultatPeriodeListe[1].grunnlag.grunnlagPerBarnListe[1].bPsAndelUnderholdskostnad.andelProsent.compareTo(
                        BigDecimal.ZERO
                    )
                ).isZero()
            },
            Executable { assertThat(resultat.resultatPeriodeListe[1].resultatListe[1].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_IKKE_BEREGNET_DELT_BOSTED) }
        )
    }

    @Test
    @DisplayName("Test med delt bosted med BPs andel av underholdskostnad < 50%")
    fun testDeltBostedEttBarnAndelUnderFemtiProsent() {
        val beregnDatoFra = LocalDate.parse("2019-08-01")
        val beregnDatoTil = LocalDate.parse("2020-01-01")

        lagSjablonliste()

        val bidragsevnePeriodeListe = listOf(
            BidragsevnePeriode(
                referanse = BIDRAGSEVNE_REFERANSE,
                bidragsevnePeriode = Periode(datoFom = LocalDate.parse("2018-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.valueOf(5603),
                tjuefemProsentInntekt = BigDecimal.valueOf(8334)
            )
        )

        val bPsAndelUnderholdskostnadListe = listOf(
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 1,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                andelProsent = BigDecimal.valueOf(0.484),
                andelBelop = BigDecimal.valueOf(4203),
                barnetErSelvforsorget = false
            )
        )

        val samvaersfradragPeriodeListe = listOf(
            SamvaersfradragPeriode(
                soknadsbarnPersonId = 1,
                referanse = SAMVAERSFRADRAG_REFERANSE,
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO
            )
        )

        val deltBostedPeriodeListe = listOf(
            DeltBostedPeriode(
                soknadsbarnPersonId = 1,
                referanse = DELT_BOSTED_REFERANSE,
                deltBostedPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                deltBostedIPeriode = true
            )
        )

        val barnetilleggBPPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BP_REFERANSE,
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggBMPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BM_REFERANSE,
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggForsvaretPeriodeListe = listOf(
            BarnetilleggForsvaretPeriode(
                referanse = BARNETILLEGG_FORSVARET_REFERANSE,
                barnetilleggForsvaretPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnetilleggForsvaretIPeriode = false
            )
        )

        val andreLopendeBidragPeriodeListe = listOf(
            AndreLopendeBidragPeriode(
                referanse = ANDRE_LOPENDE_BIDRAG_REFERANSE,
                andreLopendeBidragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnPersonId = 1,
                bidragBelop = BigDecimal.ZERO,
                samvaersfradragBelop = BigDecimal.ZERO
            )
        )

        val beregnBarnebidragGrunnlag = BeregnBarnebidragGrunnlag(
            beregnDatoFra = beregnDatoFra,
            beregnDatoTil = beregnDatoTil,
            bidragsevnePeriodeListe = bidragsevnePeriodeListe,
            bPsAndelUnderholdskostnadPeriodeListe = bPsAndelUnderholdskostnadListe,
            samvaersfradragPeriodeListe = samvaersfradragPeriodeListe,
            deltBostedPeriodeListe = deltBostedPeriodeListe,
            barnetilleggBPPeriodeListe = barnetilleggBPPeriodeListe,
            barnetilleggBMPeriodeListe = barnetilleggBMPeriodeListe,
            barnetilleggForsvaretPeriodeListe = barnetilleggForsvaretPeriodeListe,
            andreLopendeBidragPeriodeListe = andreLopendeBidragPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )

        val resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag)

        assertAll(
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.valueOf(0))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_IKKE_BEREGNET_DELT_BOSTED) }
        )
    }

    @Test
    @DisplayName("Test med delt bosted med BPs andel av underholdskostnad > 50%")
    fun testDeltBostedEttBarnAndelOverFemtiProsent() {
        val beregnDatoFra = LocalDate.parse("2019-08-01")
        val beregnDatoTil = LocalDate.parse("2020-01-01")

        lagSjablonliste()

        val bidragsevnePeriodeListe = listOf(
            BidragsevnePeriode(
                referanse = BIDRAGSEVNE_REFERANSE,
                bidragsevnePeriode = Periode(datoFom = LocalDate.parse("2018-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.valueOf(359),
                tjuefemProsentInntekt = BigDecimal.valueOf(11458)
            )
        )

        val bPsAndelUnderholdskostnadListe = listOf(
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 1,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                andelProsent = BigDecimal.valueOf(0.564),
                andelBelop = BigDecimal.valueOf(4898),
                barnetErSelvforsorget = false
            )
        )

        val samvaersfradragPeriodeListe = listOf(
            SamvaersfradragPeriode(
                soknadsbarnPersonId = 1,
                referanse = SAMVAERSFRADRAG_REFERANSE,
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO
            )
        )

        val deltBostedPeriodeListe = listOf(
            DeltBostedPeriode(
                soknadsbarnPersonId = 1,
                referanse = DELT_BOSTED_REFERANSE,
                deltBostedPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                deltBostedIPeriode = true
            )
        )

        val barnetilleggBPPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BP_REFERANSE,
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggBMPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BM_REFERANSE,
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggForsvaretPeriodeListe = listOf(
            BarnetilleggForsvaretPeriode(
                referanse = BARNETILLEGG_FORSVARET_REFERANSE,
                barnetilleggForsvaretPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnetilleggForsvaretIPeriode = false
            )
        )

        val andreLopendeBidragPeriodeListe = listOf(
            AndreLopendeBidragPeriode(
                referanse = ANDRE_LOPENDE_BIDRAG_REFERANSE,
                andreLopendeBidragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnPersonId = 1,
                bidragBelop = BigDecimal.ZERO,
                samvaersfradragBelop = BigDecimal.ZERO
            )
        )

        val beregnBarnebidragGrunnlag = BeregnBarnebidragGrunnlag(
            beregnDatoFra = beregnDatoFra,
            beregnDatoTil = beregnDatoTil,
            bidragsevnePeriodeListe = bidragsevnePeriodeListe,
            bPsAndelUnderholdskostnadPeriodeListe = bPsAndelUnderholdskostnadListe,
            samvaersfradragPeriodeListe = samvaersfradragPeriodeListe,
            deltBostedPeriodeListe = deltBostedPeriodeListe,
            barnetilleggBPPeriodeListe = barnetilleggBPPeriodeListe,
            barnetilleggBMPeriodeListe = barnetilleggBMPeriodeListe,
            barnetilleggForsvaretPeriodeListe = barnetilleggForsvaretPeriodeListe,
            andreLopendeBidragPeriodeListe = andreLopendeBidragPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )

        val resultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag)

        assertAll(
            Executable { assertThat(resultat.resultatPeriodeListe.size).isEqualTo(1) },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].belop.compareTo(BigDecimal.valueOf(360))).isZero() },
            Executable { assertThat(resultat.resultatPeriodeListe[0].resultatListe[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) }
        )
    }

    @Test
    @DisplayName("Test med feil i grunnlag som skal resultere i avvik")
    fun testGrunnlagMedAvvik() {
        val beregnDatoFra = LocalDate.parse("2016-08-01")
        val beregnDatoTil = LocalDate.parse("2022-01-01")

        lagSjablonliste()

        val bidragsevnePeriodeListe = listOf(
            BidragsevnePeriode(
                referanse = BIDRAGSEVNE_REFERANSE + "_1",
                bidragsevnePeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2019-10-01")),
                belop = BigDecimal.valueOf(15000),
                tjuefemProsentInntekt = BigDecimal.valueOf(16000)
            ),
            BidragsevnePeriode(
                referanse = BIDRAGSEVNE_REFERANSE + "_2",
                bidragsevnePeriode = Periode(datoFom = LocalDate.parse("2019-10-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.valueOf(17000),
                tjuefemProsentInntekt = BigDecimal.valueOf(16000)
            )
        )

        val bPsAndelUnderholdskostnadListe = listOf(
            BPsAndelUnderholdskostnadPeriode(
                soknadsbarnPersonId = 1,
                referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                bPsAndelUnderholdskostnadPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                andelProsent = BigDecimal.valueOf(0.8),
                andelBelop = BigDecimal.valueOf(16000),
                barnetErSelvforsorget = false
            )
        )

        val samvaersfradragPeriodeListe = listOf(
            SamvaersfradragPeriode(
                soknadsbarnPersonId = 1,
                referanse = SAMVAERSFRADRAG_REFERANSE,
                samvaersfradragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO
            )
        )

        val deltBostedPeriodeListe = listOf(
            DeltBostedPeriode(
                soknadsbarnPersonId = 1,
                referanse = DELT_BOSTED_REFERANSE,
                deltBostedPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                deltBostedIPeriode = false
            )
        )

        val barnetilleggBPPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BP_REFERANSE,
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggBMPeriodeListe = listOf(
            BarnetilleggPeriode(
                soknadsbarnPersonId = 1,
                referanse = BARNETILLEGG_BM_REFERANSE,
                barnetilleggPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                belop = BigDecimal.ZERO,
                skattProsent = BigDecimal.ZERO
            )
        )

        val barnetilleggForsvaretPeriodeListe = listOf(
            BarnetilleggForsvaretPeriode(
                referanse = BARNETILLEGG_FORSVARET_REFERANSE,
                barnetilleggForsvaretPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnetilleggForsvaretIPeriode = false
            )
        )

        val andreLopendeBidragPeriodeListe = listOf(
            AndreLopendeBidragPeriode(
                referanse = ANDRE_LOPENDE_BIDRAG_REFERANSE,
                andreLopendeBidragPeriode = Periode(datoFom = LocalDate.parse("2019-08-01"), datoTil = LocalDate.parse("2020-01-01")),
                barnPersonId = 1,
                bidragBelop = BigDecimal.ZERO,
                samvaersfradragBelop = BigDecimal.ZERO
            )
        )

        val beregnBarnebidragGrunnlag = BeregnBarnebidragGrunnlag(
            beregnDatoFra = beregnDatoFra,
            beregnDatoTil = beregnDatoTil,
            bidragsevnePeriodeListe = bidragsevnePeriodeListe,
            bPsAndelUnderholdskostnadPeriodeListe = bPsAndelUnderholdskostnadListe,
            samvaersfradragPeriodeListe = samvaersfradragPeriodeListe,
            deltBostedPeriodeListe = deltBostedPeriodeListe,
            barnetilleggBPPeriodeListe = barnetilleggBPPeriodeListe,
            barnetilleggBMPeriodeListe = barnetilleggBMPeriodeListe,
            barnetilleggForsvaretPeriodeListe = barnetilleggForsvaretPeriodeListe,
            andreLopendeBidragPeriodeListe = andreLopendeBidragPeriodeListe,
            sjablonPeriodeListe = sjablonPeriodeListe
        )

        val avvikListe = barnebidragPeriode.validerInput(beregnBarnebidragGrunnlag)

        assertAll(
            Executable { assertThat(avvikListe).isNotEmpty() },
            Executable { assertThat(avvikListe[0].avvikTekst).isEqualTo("Første dato i bidragsevnePeriodeListe (2019-08-01) er etter beregnDatoFra (2016-08-01)") },
            Executable { assertThat(avvikListe[0].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) },
            Executable { assertThat(avvikListe[1].avvikTekst).isEqualTo("Siste dato i bidragsevnePeriodeListe (2020-01-01) er før beregnDatoTil (2022-01-01)") },
            Executable { assertThat(avvikListe[1].avvikType).isEqualTo(AvvikType.PERIODE_MANGLER_DATA) }
        )
    }

    private fun lagSjablonliste() {
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-01-01"), datoTil = LocalDate.parse("2021-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(5667)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                sjablonPeriode = Periode(datoFom = LocalDate.parse("2019-07-01"), datoTil = LocalDate.parse("2021-06-30")),
                sjablon = Sjablon(
                    navn = SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnhold(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = BigDecimal.valueOf(2334)))
                )
            )
        )
    }
}
