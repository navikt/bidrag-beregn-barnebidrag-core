package no.nav.bidrag.beregn.barnebidrag.beregning

import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning
import no.nav.bidrag.beregn.felles.FellesBeregning
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.util.SjablonUtil
import no.nav.bidrag.domene.enums.beregning.ResultatkodeBarnebidrag
import no.nav.bidrag.domene.enums.sjablon.SjablonTallNavn
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

open class BarnebidragBeregningImpl : FellesBeregning(), BarnebidragBeregning {
    override fun beregn(grunnlag: GrunnlagBeregning): List<ResultatBeregning> {
        return if (grunnlag.barnetilleggForsvaret.barnetilleggForsvaretIPeriode) {
            beregnMedBarnetilleggForsvaret(grunnlag)
        } else {
            beregnOrdinaer(grunnlag)
        }
    }

    private fun beregnOrdinaer(grunnlag: GrunnlagBeregning): List<ResultatBeregning> {
        val resultatBeregningListe = mutableListOf<ResultatBeregning>()

        val totaltBelopUnderholdskostnad =
            grunnlag.grunnlagPerBarnListe
                .fold(BigDecimal.ZERO) { total, it ->
                    total + it.bPsAndelUnderholdskostnad.andelBelop
                }

        val totaltBelopLopendeBidrag =
            grunnlag.andreLopendeBidragListe
                .fold(BigDecimal.ZERO) { total, it ->
                    total + it.bidragBelop + it.samvaersfradragBelop
                }

        val maksBidragsbelop = minOf(grunnlag.bidragsevne.bidragsevneBelop, grunnlag.bidragsevne.tjuefemProsentInntekt)
        val bidragRedusertAvBidragsevne = grunnlag.bidragsevne.bidragsevneBelop < grunnlag.bidragsevne.tjuefemProsentInntekt

        grunnlag.grunnlagPerBarnListe.forEach { grunnlagBeregningPerBarn: GrunnlagBeregningPerBarn ->
            var resultatkode = ResultatkodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG
            var tempBarnebidrag: BigDecimal

            // Beregner netto barnetillegg for BP og BM
            val nettoBarnetilleggBP = beregnNettoBarnetillegg(grunnlagBeregningPerBarn.barnetilleggBP)
            val nettoBarnetilleggBM = beregnNettoBarnetillegg(grunnlagBeregningPerBarn.barnetilleggBM)

            // Regner ut underholdskostnad ut fra andelsprosent og beløp. Skal ikke gjøres hvis disse er lik 0
            val underholdskostnad = beregnUnderholdskostnad(grunnlagBeregningPerBarn)

            // Sjekker om totalt bidragsbeløp er større enn bidragsevne eller 25% av månedsinntekt
            if (maksBidragsbelop < totaltBelopUnderholdskostnad) {
                // Bidraget skal begrenses forholdsmessig pga manglende evne/25%-regel
                val andelProsent =
                    grunnlagBeregningPerBarn.bPsAndelUnderholdskostnad.andelBelop
                        .divide(totaltBelopUnderholdskostnad, MathContext(10, RoundingMode.HALF_UP))
                tempBarnebidrag = maksBidragsbelop * andelProsent
                resultatkode =
                    if (bidragRedusertAvBidragsevne) {
                        ResultatkodeBarnebidrag.BIDRAG_REDUSERT_AV_EVNE
                    } else {
                        ResultatkodeBarnebidrag.BIDRAG_REDUSERT_TIL_25_PROSENT_AV_INNTEKT
                    }
            } else {
                tempBarnebidrag = grunnlagBeregningPerBarn.bPsAndelUnderholdskostnad.andelBelop
            }

            // Trekker fra samværsfradrag
            tempBarnebidrag -= grunnlagBeregningPerBarn.samvaersfradrag.belop

            // Sjekker mot særregler for barnetillegg BP/BM
            when {
                // Dersom beregnet bidrag etter samværsfradrag er lavere enn eventuelt barnetillegg for BP, så skal bidraget settes likt
                // barnetillegget minus samværsfradrag. BarnetilleggBP skal ikke taes hensyn til ved delt bosted
                !grunnlagBeregningPerBarn.deltBosted.deltBostedIPeriode &&
                    tempBarnebidrag < nettoBarnetilleggBP &&
                    nettoBarnetilleggBP > BigDecimal.ZERO -> {
                    tempBarnebidrag = nettoBarnetilleggBP - grunnlagBeregningPerBarn.samvaersfradrag.belop
                    resultatkode = ResultatkodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_BP
                }

                // Regel for barnetilleggBP har ikke slått til. Sjekk om eventuelt barnetillegg for BM skal benyttes.
                // Bidrag settes likt underholdskostnad minus netto barnetilleggBM når beregnet bidrag er høyere enn underholdskostnad minus
                // netto barnetillegg for BM
                tempBarnebidrag > (underholdskostnad - nettoBarnetilleggBM) -> {
                    tempBarnebidrag = underholdskostnad - nettoBarnetilleggBM - grunnlagBeregningPerBarn.samvaersfradrag.belop
                    resultatkode = ResultatkodeBarnebidrag.BIDRAG_SATT_TIL_UNDERHOLDSKOSTNAD_MINUS_BARNETILLEGG_BM
                }
            }

            // Beløp for bidrag settes til 0 hvis bidraget er utregnet til negativt beløp etter samværsfradrag
            if (tempBarnebidrag <= BigDecimal.ZERO) {
                tempBarnebidrag = BigDecimal.ZERO
            }

            if (grunnlag.bidragsevne.bidragsevneBelop == BigDecimal.ZERO) {
                resultatkode = ResultatkodeBarnebidrag.INGEN_EVNE
            }

            // Hvis barnet har delt bosted og bidrag ikke er redusert under beregningen skal resultatkode settes til DELT_BOSTED
            if (grunnlagBeregningPerBarn.deltBosted.deltBostedIPeriode) {
                if (grunnlagBeregningPerBarn.bPsAndelUnderholdskostnad.andelProsent > BigDecimal.ZERO) {
                    if ((resultatkode == ResultatkodeBarnebidrag.KOSTNADSBEREGNET_BIDRAG)) {
                        resultatkode = ResultatkodeBarnebidrag.DELT_BOSTED
                    }
                } else {
                    resultatkode = ResultatkodeBarnebidrag.BIDRAG_IKKE_BEREGNET_DELT_BOSTED
                }
            }

            if (grunnlagBeregningPerBarn.bPsAndelUnderholdskostnad.barnetErSelvforsorget) {
                tempBarnebidrag = BigDecimal.ZERO
                resultatkode = ResultatkodeBarnebidrag.BARNET_ER_SELVFORSØRGET
            }

            // Sjekker om bidragsevne dekker beregnet bidrag pluss løpende bidragsbeløp for andre eksisterende bidragssaker + samværsfradrag.
            // Hvis ikke så skal bidragssaken merkes for forholdsmessig fordeling.
            if (grunnlag.bidragsevne.bidragsevneBelop < (tempBarnebidrag + totaltBelopLopendeBidrag) &&
                resultatkode !in
                setOf(
                    ResultatkodeBarnebidrag.BIDRAG_IKKE_BEREGNET_DELT_BOSTED,
                    ResultatkodeBarnebidrag.BARNET_ER_SELVFORSØRGET,
                    ResultatkodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_BP,
                    ResultatkodeBarnebidrag.INGEN_EVNE,
                )
            ) {
                resultatkode = ResultatkodeBarnebidrag.BEGRENSET_EVNE_FLERE_SAKER_UTFØR_FORHOLDSMESSIG_FORDELING
            }

            // Bidrag skal avrundes til nærmeste tier
            tempBarnebidrag = tempBarnebidrag.setScale(-1, RoundingMode.HALF_UP)

            resultatBeregningListe.add(
                ResultatBeregning(
                    soknadsbarnPersonId = grunnlagBeregningPerBarn.soknadsbarnPersonId,
                    belop = tempBarnebidrag,
                    kode = resultatkode,
                    sjablonListe = emptyList(),
                ),
            )
        }

        return resultatBeregningListe
    }

    private fun beregnNettoBarnetillegg(barnetillegg: Barnetillegg?) = barnetillegg?.run {
        belop - (belop * skattProsent).divide(BigDecimal.valueOf(100), MathContext(10, RoundingMode.HALF_UP))
    } ?: BigDecimal.ZERO

    private fun beregnUnderholdskostnad(grunnlagBeregningPerBarn: GrunnlagBeregningPerBarn) =
        if (grunnlagBeregningPerBarn.bPsAndelUnderholdskostnad.andelProsent > BigDecimal.ZERO &&
            grunnlagBeregningPerBarn.bPsAndelUnderholdskostnad.andelBelop > BigDecimal.ZERO
        ) {
            grunnlagBeregningPerBarn.bPsAndelUnderholdskostnad.andelBelop
                .divide(grunnlagBeregningPerBarn.bPsAndelUnderholdskostnad.andelProsent, MathContext(10, RoundingMode.HALF_UP))
                .setScale(0, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

    private fun beregnMedBarnetilleggForsvaret(grunnlag: GrunnlagBeregning): List<ResultatBeregning> {
        // Henter sjablonverdier
        val sjablonNavnVerdiMap = hentSjablonVerdier(grunnlag.sjablonListe)
        val barnetilleggForsvaretForsteBarn = sjablonNavnVerdiMap[SjablonTallNavn.BARNETILLEGG_FORSVARET_FØRSTE_BARN_BELØP.navn] ?: BigDecimal.ZERO
        val barnetilleggForsvaretOvrigeBarn = sjablonNavnVerdiMap[SjablonTallNavn.BARNETILLEGG_FORSVARET_ØVRIGE_BARN_BELØP.navn] ?: BigDecimal.ZERO

        val resultatBeregningListe = mutableListOf<ResultatBeregning>()

        val totaltBelopLopendeBidrag =
            grunnlag.andreLopendeBidragListe.fold(BigDecimal.ZERO) { total, it ->
                total + it.bidragBelop + it.samvaersfradragBelop
            }

        val barnetilleggForsvaretPerBarn =
            if (grunnlag.grunnlagPerBarnListe.size == 1) {
                barnetilleggForsvaretForsteBarn
            } else {
                (barnetilleggForsvaretForsteBarn + barnetilleggForsvaretOvrigeBarn)
                    .divide(BigDecimal.valueOf(grunnlag.grunnlagPerBarnListe.size.toLong()), MathContext(10, RoundingMode.HALF_UP))
                    .setScale(0, RoundingMode.HALF_UP)
            }

        // Sjekker om bidragsevne dekker beregnet bidrag + løpende bidragsbeløp + samværsfradrag.
        // Hvis ikke så skal bidragssaken merkes for forholdsmessig fordeling.
        val resultatkode =
            if (grunnlag.bidragsevne.bidragsevneBelop < (
                    barnetilleggForsvaretPerBarn
                        * BigDecimal.valueOf(grunnlag.grunnlagPerBarnListe.size.toLong()) + totaltBelopLopendeBidrag
                    )
            ) {
                ResultatkodeBarnebidrag.BEGRENSET_EVNE_FLERE_SAKER_UTFØR_FORHOLDSMESSIG_FORDELING
            } else {
                ResultatkodeBarnebidrag.BIDRAG_SATT_TIL_BARNETILLEGG_FORSVARET
            }

        grunnlag.grunnlagPerBarnListe.forEach {
            val barnebidragEtterSamvaersfradrag = barnetilleggForsvaretPerBarn - it.samvaersfradrag.belop
            resultatBeregningListe.add(
                ResultatBeregning(
                    soknadsbarnPersonId = it.soknadsbarnPersonId,
                    belop = barnebidragEtterSamvaersfradrag,
                    kode = resultatkode,
                    sjablonListe =
                    byggSjablonResultatListe(
                        sjablonNavnVerdiMap = sjablonNavnVerdiMap,
                        sjablonPeriodeListe = grunnlag.sjablonListe,
                    ),
                ),
            )
        }

        return resultatBeregningListe
    }

    // Henter sjablonverdier
    private fun hentSjablonVerdier(sjablonPeriodeListe: List<SjablonPeriode>): Map<String, BigDecimal> {
        val sjablonListe = sjablonPeriodeListe.map { it.sjablon }

        val sjablonNavnVerdiMap = HashMap<String, BigDecimal>()

        // Sjablontall
        sjablonNavnVerdiMap[SjablonTallNavn.BARNETILLEGG_FORSVARET_FØRSTE_BARN_BELØP.navn] =
            SjablonUtil.hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.BARNETILLEGG_FORSVARET_FØRSTE_BARN_BELØP)
        sjablonNavnVerdiMap[SjablonTallNavn.BARNETILLEGG_FORSVARET_ØVRIGE_BARN_BELØP.navn] =
            SjablonUtil.hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.BARNETILLEGG_FORSVARET_ØVRIGE_BARN_BELØP)

        return sjablonNavnVerdiMap
    }
}
