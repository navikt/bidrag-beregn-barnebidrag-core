package no.nav.bidrag.beregn.barnebidrag

import no.nav.bidrag.beregn.TestUtil.ANDRE_LOPENDE_BIDRAG_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_BM_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_BP_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BARNETILLEGG_FORSVARET_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BIDRAGSEVNE_REFERANSE
import no.nav.bidrag.beregn.TestUtil.BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE
import no.nav.bidrag.beregn.TestUtil.DELT_BOSTED_REFERANSE
import no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE
import no.nav.bidrag.beregn.TestUtil.byggSjablonPeriodeListe
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning.Companion.getInstance
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidrag
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaret
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBosted
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn
import no.nav.bidrag.beregn.barnebidrag.bo.Samvaersfradrag
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeBarnebidrag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal

@DisplayName("Test av beregning av barnebidrag")
internal class BarnebidragBeregningTest {

    private val sjablonPeriodeListe = byggSjablonPeriodeListe()
    private val barnebidragBeregning = getInstance()

    @DisplayName("Beregner ved full evne, ett barn, ingen barnetillegg")
    @Test
    fun testBeregningEttBarnMedFullEvneIngenBarnetillegg() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(10000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(10000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(8000))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG) }
        )
    }

    @DisplayName(
        "Beregner ved full evne, ett barn, og barnetillegg for BP der barnetillegg er høyere enn beregnet bidrag" +
            "Endelig bidrag skal da settes likt barnetillegg for BP"
    )
    @Test
    fun testBeregningEttBarnMedFullEvneBarnetilleggBP() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(80),
                    andelBelop = BigDecimal.valueOf(1000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.valueOf((100).toLong())),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE,
                    belop = BigDecimal.valueOf(1700),
                    skattProsent = BigDecimal.valueOf(10)
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(
                referanse = BIDRAGSEVNE_REFERANSE,
                bidragsevneBelop = BigDecimal.valueOf(10000),
                tjuefemProsentInntekt = BigDecimal.valueOf(10000)
            ),
            grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
            barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
            andreLopendeBidragListe = emptyList(),
            sjablonListe = sjablonPeriodeListe
        )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(1430))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_BP) }
        )
    }

    @DisplayName("Beregner ved full evne, to barn")
    @Test
    fun testBeregningToBarnMedFullEvne() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(7000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(20000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(20000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(8000))).isZero() },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(7000))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG) }
        )
    }

    @DisplayName("Beregner for tre barn med for lav bidragsevne")
    @Test
    fun testBeregningTreBarnBegrensetAvBidragsevne() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(5000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(3000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 3,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(2000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_3", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_3", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_3", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_3", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(
                referanse = BIDRAGSEVNE_REFERANSE,
                bidragsevneBelop = BigDecimal.valueOf(8000),
                tjuefemProsentInntekt = BigDecimal.valueOf(12000)
            ),
            grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
            barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
            andreLopendeBidragListe = emptyList(),
            sjablonListe = sjablonPeriodeListe
        )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(4000))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(2400))).isZero() },
            Executable { assertThat(resultat[1].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) },
            Executable { assertThat(resultat[2].belop.compareTo(BigDecimal.valueOf(1600))).isZero() },
            Executable { assertThat(resultat[2].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) }
        )
    }

    @DisplayName("Beregner ved manglende evne, ett barn")
    @Test
    fun testBeregningEttBarnIkkeFullEvne() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(
                referanse = BIDRAGSEVNE_REFERANSE,
                bidragsevneBelop = BigDecimal.valueOf(1000),
                tjuefemProsentInntekt = BigDecimal.valueOf(2000)
            ),
            grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
            barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
            andreLopendeBidragListe = emptyList(),
            sjablonListe = sjablonPeriodeListe
        )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(1000))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) }
        )
    }

    @DisplayName("Beregner ved manglende evne, to barn")
    @Test
    fun testBeregningToBarnIkkeFullEvne() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(7000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert = GrunnlagBeregning(
            bidragsevne = Bidragsevne(
                referanse = BIDRAGSEVNE_REFERANSE,
                bidragsevneBelop = BigDecimal.valueOf(10000),
                tjuefemProsentInntekt = BigDecimal.valueOf(20000)
            ),
            grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
            barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
            andreLopendeBidragListe = emptyList(),
            sjablonListe = sjablonPeriodeListe
        )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(5330))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(4670))).isZero() },
            Executable { assertThat(resultat[1].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) }
        )
    }

    @DisplayName("Beregner for tre barn som begrenses av 25%-regel")
    @Test
    fun testBeregningTreBarnBegrensetAv25ProsentAvInntekt() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(5000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(3000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 3,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(2000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_3", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_3", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_3", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_3", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(12000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(8000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(4000))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT) },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(2400))).isZero() },
            Executable { assertThat(resultat[1].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT) },
            Executable { assertThat(resultat[2].belop.compareTo(BigDecimal.valueOf(1600))).isZero() },
            Executable { assertThat(resultat[2].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT) },
            Executable { assertThat(resultat[0].belop.add(resultat[1].belop).add(resultat[2].belop).compareTo(BigDecimal.valueOf(8000))).isZero() }
        )
    }

    @DisplayName(
        (
            "Beregner at bidrag settes likt underholdskostnad minus nettobarnetilleggBM. Dette skjer " +
                "når beregnet bidrag er høyere enn underholdskostnad minus netto barnetillegg for BM. " +
                "Det skal trekkes fra for samvær også her "
            )
    )
    @Test
    fun testBeregningBidragSettesLiktBarnetilleggBM() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(1000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.valueOf(50)),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(
                    referanse = BARNETILLEGG_BM_REFERANSE,
                    belop = BigDecimal.valueOf(1000),
                    skattProsent = BigDecimal.valueOf(10)
                )
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(8000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(12000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(300))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM) }
        )
    }

    @DisplayName("Beregner at bidrag settes likt barnetilleggBP der det også finnes barnetillegg BM. Barnetillegg for BP overstyrer barnetilleggBM")
    @Test
    fun testBeregningBidragSettesLiktBarnetilleggBP() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(200),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE,
                    belop = BigDecimal.valueOf(500),
                    skattProsent = BigDecimal.valueOf(10)
                ),
                barnetilleggBM = Barnetillegg(
                    referanse = BARNETILLEGG_BM_REFERANSE,
                    belop = BigDecimal.valueOf(1000),
                    skattProsent = BigDecimal.valueOf(10)
                )
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(8000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(12000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(450))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_BP) }
        )
    }

    @DisplayName("Beregner at bidrag settes likt BPs andel av underholdskostnad minus samværsfradrag")
    @Test
    fun testBeregningFradragSamvaer() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(2000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.valueOf(200)),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(8000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(12000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(1800))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG) }
        )
    }

    @DisplayName(
        (
            "Beregner for tre barn der barna har barnetillegg BP eller BM. " +
                "Bidrag settes likt underholdskostnad minus netto barnetilleggBM når beregnet bidrag er høyere enn" +
                "underholdskostnad minus netto barnetillegg for BM"
            )
    )
    @Test
    fun testBeregningTreBarnBarnetilleggBPogBM() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(400),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_1",
                    belop = BigDecimal.valueOf(500),
                    skattProsent = BigDecimal.valueOf(10)
                ),
                barnetilleggBM = Barnetillegg(
                    referanse = BARNETILLEGG_BM_REFERANSE + "_1",
                    belop = BigDecimal.valueOf(400),
                    skattProsent = BigDecimal.valueOf(10)
                )
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(300),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(
                    referanse = BARNETILLEGG_BM_REFERANSE + "_2",
                    belop = BigDecimal.valueOf(100),
                    skattProsent = BigDecimal.valueOf(10)
                )
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(12000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(8000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(450))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_BP) },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(290))).isZero() },
            Executable { assertThat(resultat[1].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM) }
        )
    }

    @DisplayName("Beregner med barnetillegg fra forsvaret for ett barn. Sjablonverdier for barnetillegg skal da overstyre alt i beregningen")
    @Test
    fun testBeregningEttBarnBarnetilleggForsvaret() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.valueOf(1000)),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE,
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(10000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(10000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = true),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(4667))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET) }
        )
    }

    @DisplayName(
        (
            "Beregner med barnetillegg fra forsvaret for tre barn. Sjablonverdier for barnetillegg skal da overstyre" +
                "alt i beregningen"
            )
    )
    @Test
    fun testBeregningTreBarnBarnetilleggForsvaret() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_1",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_2",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 3,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_3", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_3", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_3",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_3", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(10000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(10000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = true),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(2667))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET) }
        )
    }

    @DisplayName("Beregner med barnetillegg fra forsvaret for tre barn. Test på at samværsfradrag trekkes fra endelig bidragsbeløp")
    @Test
    fun testBeregningBarnetilleggForsvaretFratrekkSamvaersfradrag() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_1",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.valueOf(1000)),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_2",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 3,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_3", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_3", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_3",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_3", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(10000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(10000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = true),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(2667))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET) },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(1667))).isZero() },
            Executable { assertThat(resultat[2].belop.compareTo(BigDecimal.valueOf(2667))).isZero() }
        )
    }

    @DisplayName("Beregner med barnetillegg fra forsvaret for elleve barn. Sjekker avrunding")
    @Test
    fun testBeregningElleveBarnBarnetilleggForsvaret() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_1",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_2",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 3,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_3", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_3", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_3",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_3", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 4,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_4",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_4", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_4", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_4",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_4", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 5,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_5",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_5", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_5", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_5",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_5", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 6,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_6",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_6", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_6", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_6",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_6", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 7,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_7",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_7", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_7", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_7",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_7", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 8,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_8",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_8", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_8", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_8",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_8", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 9,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_9",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_9", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_9", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_9",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_9", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 10,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_10",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_10", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_10", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_10",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_10", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 11,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_11",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_11", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_11", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_11",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_11", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(10000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(10000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = true),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(727))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET) }
        )
    }

    @DisplayName("Beregner der delt bosted og barnetilleggBP er angitt, barnetillegget skal da ikke taes hensyn til")
    @Test
    fun testBeregningDeltBostedOgBarnetilleggBP() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_2",
                    belop = BigDecimal.valueOf(5000),
                    skattProsent = BigDecimal.valueOf(10)
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(1000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(1200)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(500))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(500))).isZero() },
            Executable { assertThat(resultat[1].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) }
        )
    }

    @DisplayName(
        (
            "Beregner med to barn der det ene er selvforsørget, dvs har inntekt over 100 * sjablon for forhøyet forskudd." +
                "BPs andel skal da være 0, bidrag skal beregnes til 0 og resultatkode BARNET_ER_SELVFORSORGET skal angis"
            )
    )
    @Test
    fun testBeregningSelvforsorgetBarn() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.ZERO,
                    andelBelop = BigDecimal.ZERO,
                    barnetErSelvforsorget = true
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_2",
                    belop = BigDecimal.valueOf(5000),
                    skattProsent = BigDecimal.valueOf(10)
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(1000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(1200)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(1000))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.ZERO)).isZero() },
            Executable { assertThat(resultat[1].kode).isEqualTo(ResultatKodeBarnebidrag.BARNET_ER_SELVFORSORGET) }
        )
    }

    @DisplayName("Resultatkode skal settes til BARNEBIDRAG_IKKE_BEREGNET_DELT_BOSTED ved delt bosted og andel av U under 50%")
    @Test
    fun testAtRiktigResultatkodeSettesVedDeltBostedOgAndelUnder50Prosent() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.ZERO,
                    andelBelop = BigDecimal.ZERO,
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(10000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(12000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_IKKE_BEREGNET_DELT_BOSTED) }
        )
    }

    @DisplayName("Resultatkode skal settes til DELT_BOSTED ved delt bosted og andel av U over 50% der ingen andre faktorer har redusert bidraget")
    @Test
    fun testAtRiktigResultatkodeSettesVedDeltBostedOgAndelOver50Prosent() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(10000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(12000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.DELT_BOSTED) }
        )
    }

    @DisplayName("Ved delt bosted og beregnet bidrag redusert av bidragsevne skal resultatkode reflektere at det er lav evne")
    @Test
    fun testAtRiktigResultatkodeSettesVedDeltBostedOgAndelOver50ProsentVedBegrensetEvne() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(1000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(1200)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(1000))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE) }
        )
    }

    @DisplayName("Tester at resultatkode angir forholdsmessig fordeling når evne ikke dekker ny sak pluss løpende bidrag")
    @Test
    fun testBegrensetEvneGirForholdsmessigFordeling() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(referanse = BARNETILLEGG_BP_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(10000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(12000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = listOf(
                    AndreLopendeBidrag(
                        referanse = ANDRE_LOPENDE_BIDRAG_REFERANSE,
                        barnPersonId = 2,
                        bidragBelop = BigDecimal.valueOf(1900),
                        samvaersfradragBelop = BigDecimal.valueOf(500)
                    )
                ),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(8000))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BEGRENSET_EVNE_FLERE_SAKER_UTFOER_FORHOLDSMESSIG_FORDELING) }
        )
    }

    @DisplayName(
        (
            "Beregner med barnetillegg fra forsvaret for tre barn. Resultatkoden skal angi at det må gjøres en forholdsmessig fordeling da bidragsevnen " +
                "ikke er høy nok til å dekke beregnet bidrag pluss løpende bidrag"
            )
    )
    @Test
    fun testBegrensetEvneGirForholdsmessigFordelingBarnetilleggForsvaret() {
        val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_1",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_1", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_1", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_1",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_1", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 2,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_2",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_2", belop = BigDecimal.valueOf(1000)),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_2", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_2",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_2", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        grunnlagBeregningPerBarnListe.add(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 3,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE + "_3",
                    andelProsent = BigDecimal.valueOf(0.80),
                    andelBelop = BigDecimal.valueOf(8000),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE + "_3", belop = BigDecimal.ZERO),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE + "_3", deltBostedIPeriode = true),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE + "_3",
                    belop = BigDecimal.valueOf(10000),
                    skattProsent = BigDecimal.ZERO
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE + "_3", belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(10000),
                    tjuefemProsentInntekt = BigDecimal.valueOf(10000)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = true),
                andreLopendeBidragListe = listOf(AndreLopendeBidrag(ANDRE_LOPENDE_BIDRAG_REFERANSE, 2, BigDecimal.valueOf(3000), BigDecimal.valueOf(500))),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(2667))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BEGRENSET_EVNE_FLERE_SAKER_UTFOER_FORHOLDSMESSIG_FORDELING) },
            Executable { assertThat(resultat[1].belop.compareTo(BigDecimal.valueOf(1667))).isZero() },
            Executable { assertThat(resultat[2].belop.compareTo(BigDecimal.valueOf(2667))).isZero() }
        )
    }

    @DisplayName("Test av barnetillegg BP")
    @Test
    fun testBarnetilleggBP() {
        val grunnlagBeregningPerBarnListe = listOf(
            GrunnlagBeregningPerBarn(
                soknadsbarnPersonId = 1,
                bPsAndelUnderholdskostnad = BPsAndelUnderholdskostnad(
                    referanse = BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
                    andelProsent = BigDecimal.valueOf(0.429),
                    andelBelop = BigDecimal.valueOf(3725),
                    barnetErSelvforsorget = false
                ),
                samvaersfradrag = Samvaersfradrag(referanse = SAMVAERSFRADRAG_REFERANSE, belop = BigDecimal.valueOf(457)),
                deltBosted = DeltBosted(referanse = DELT_BOSTED_REFERANSE, deltBostedIPeriode = false),
                barnetilleggBP = Barnetillegg(
                    referanse = BARNETILLEGG_BP_REFERANSE,
                    belop = BigDecimal.valueOf(2000),
                    skattProsent = BigDecimal.valueOf(20)
                ),
                barnetilleggBM = Barnetillegg(referanse = BARNETILLEGG_BM_REFERANSE, belop = BigDecimal.ZERO, skattProsent = BigDecimal.ZERO)
            )
        )
        val grunnlagBeregningPeriodisert =
            GrunnlagBeregning(
                bidragsevne = Bidragsevne(
                    referanse = BIDRAGSEVNE_REFERANSE,
                    bidragsevneBelop = BigDecimal.valueOf(136),
                    tjuefemProsentInntekt = BigDecimal.valueOf(8334)
                ),
                grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                barnetilleggForsvaret = BarnetilleggForsvaret(referanse = BARNETILLEGG_FORSVARET_REFERANSE, barnetilleggForsvaretIPeriode = false),
                andreLopendeBidragListe = emptyList(),
                sjablonListe = sjablonPeriodeListe
            )

        val resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert)

        assertAll(
            Executable { assertThat(resultat[0].belop.compareTo(BigDecimal.valueOf(1140))).isZero() },
            Executable { assertThat(resultat[0].kode).isEqualTo(ResultatKodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_BP) }
        )
    }
}
