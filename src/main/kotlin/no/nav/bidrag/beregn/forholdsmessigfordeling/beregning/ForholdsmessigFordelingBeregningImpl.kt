package no.nav.bidrag.beregn.forholdsmessigfordeling.beregning

import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPerBarn
import no.nav.bidrag.domene.enums.beregning.ResultatkodeBarnebidrag
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class ForholdsmessigFordelingBeregningImpl : ForholdsmessigFordelingBeregning {
    override fun beregn(grunnlag: GrunnlagBeregningPeriodisert): List<ResultatBeregning> {
        val resultatBeregningListe = mutableListOf<ResultatBeregning>()

        val endeligBidragsevne = minOf(grunnlag.bidragsevne.belop, grunnlag.bidragsevne.tjuefemProsentInntekt)

        // Hvis summert beløp for alle saker i perioden er høyere enn bidragsevnen skal det gjøres en forholdsmessig fordeling
        val samletBidragsbelopAlleSaker =
            grunnlag.beregnetBidragSakListe
                .flatMap { it.grunnlagPerBarnListe }
                .map { it.bidragBelop }
                .fold(BigDecimal.ZERO, BigDecimal::add)

        // Gjør forholdsmessig fordeling
        if (samletBidragsbelopAlleSaker > endeligBidragsevne) {
            grunnlag.beregnetBidragSakListe.forEach { beregnetBidragSak ->
                val samletBidragsbelopSak =
                    grunnlag.beregnetBidragSakListe
                        .filter { it.saksnr == beregnetBidragSak.saksnr }
                        .flatMap { it.grunnlagPerBarnListe }
                        .map { it.bidragBelop }
                        .fold(BigDecimal.ZERO, BigDecimal::add)

                // Hvor stor prosentdel av det totale beløpet utgjør denne sakens samlede bidragsbeløp
                val sakensAndelAvTotaltBelopAlleSakerProsent =
                    samletBidragsbelopSak.divide(
                        samletBidragsbelopAlleSaker,
                        MathContext(10, RoundingMode.HALF_UP),
                    )

                // Regner ut hvor mye prosentsatsen utgjør av bidragsevnen
                val sakensAndelAvEndeligBidragsevneBelop =
                    sakensAndelAvTotaltBelopAlleSakerProsent.multiply(
                        endeligBidragsevne,
                        MathContext(10, RoundingMode.HALF_UP),
                    )

                val resultatPerBarnListe = mutableListOf<ResultatPerBarn>()

                // Leser hvert barn i saken og regner ut nytt, justert bidragsbeløp
                beregnetBidragSak.grunnlagPerBarnListe.forEach { grunnlagPerBarn ->
                    val barnetsAndelAvTotaltBelopForSakProsent =
                        grunnlagPerBarn.bidragBelop.divide(
                            samletBidragsbelopSak,
                            MathContext(10, RoundingMode.HALF_UP),
                        )
                    val beregnetBidragsbelop =
                        barnetsAndelAvTotaltBelopForSakProsent.multiply(
                            sakensAndelAvEndeligBidragsevneBelop,
                            MathContext(10, RoundingMode.HALF_UP),
                        ).setScale(-1, RoundingMode.HALF_UP)

                    resultatPerBarnListe.add(
                        ResultatPerBarn(
                            barnPersonId = grunnlagPerBarn.barnPersonId,
                            belop = beregnetBidragsbelop,
                            kode = ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_BIDRAGSBELØP_ENDRET,
                        ),
                    )
                }

                resultatBeregningListe.add(
                    ResultatBeregning(saksnr = beregnetBidragSak.saksnr, resultatPerBarnListe = resultatPerBarnListe),
                )
            }

            // Ingen forholdsmessig fordeling gjøres og originalt bidragsbeløp returneres
        } else {
            grunnlag.beregnetBidragSakListe.forEach {
                val resultatPerBarnListe = mutableListOf<ResultatPerBarn>()

                it.grunnlagPerBarnListe.forEach { grunnlagPerBarn ->
                    resultatPerBarnListe.add(
                        ResultatPerBarn(
                            barnPersonId = grunnlagPerBarn.barnPersonId,
                            belop = grunnlagPerBarn.bidragBelop,
                            kode = ResultatkodeBarnebidrag.FORHOLDSMESSIG_FORDELING_INGEN_ENDRING,
                        ),
                    )
                }

                resultatBeregningListe.add(ResultatBeregning(saksnr = it.saksnr, resultatPerBarnListe = resultatPerBarnListe))
            }
        }

        return resultatBeregningListe
    }
}
