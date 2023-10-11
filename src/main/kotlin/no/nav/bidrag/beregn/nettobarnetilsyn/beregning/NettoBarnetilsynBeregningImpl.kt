package no.nav.bidrag.beregn.nettobarnetilsyn.beregning

import no.nav.bidrag.beregn.felles.FellesBeregning
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.util.SjablonUtil.hentSjablonverdi
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning
import no.nav.bidrag.domain.enums.sjablon.SjablonNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class NettoBarnetilsynBeregningImpl : FellesBeregning(), NettoBarnetilsynBeregning {
    override fun beregn(grunnlag: GrunnlagBeregning): List<ResultatBeregning> {

        val resultatBeregningListe = mutableListOf<ResultatBeregning>()

        // Summerer faktisk utgift pr barn
        val faktiskUtgiftListeSummertPerBarn = grunnlag.faktiskUtgiftListe
            .groupBy { it.soknadsbarnPersonId }
            .mapValues { (_, faktiskUtgift) ->
                faktiskUtgift.map { it.belop }.fold(BigDecimal.ZERO, BigDecimal::add)
            }

        // Barn som er 13 år eller eldre skal ikke telles med ved henting av sjablon for maks tilsyn og fradrag
        val listeMedBarnUnder13Aar = grunnlag.faktiskUtgiftListe
            .filter { it.soknadsbarnAlder < 13 }
            .groupBy { it.soknadsbarnPersonId }
            .mapValues { (_, faktiskUtgift) ->
                faktiskUtgift.map { it.belop }.fold(BigDecimal.ZERO, BigDecimal::add)
            }

        val antallBarnIPerioden = listeMedBarnUnder13Aar.size

        // Henter sjablonverdier
        val sjablonNavnVerdiMap = hentSjablonVerdier(sjablonPeriodeListe = grunnlag.sjablonListe, antallBarnIPerioden = antallBarnIPerioden)

        var antallBarnMedTilsynsutgift = 0
        var samletFaktiskUtgiftBelop = BigDecimal.ZERO

        faktiskUtgiftListeSummertPerBarn.forEach { (_, belop) ->
            if (belop > BigDecimal.ZERO) {
                antallBarnMedTilsynsutgift++
                samletFaktiskUtgiftBelop = samletFaktiskUtgiftBelop.add(belop)
            }
        }

        val maksTilsynsbelop = sjablonNavnVerdiMap[SjablonNavn.MAKS_TILSYN.navn] ?: BigDecimal.ZERO

        val fradragsbelopPerBarn = beregnFradragsbelopPerBarn(
            antallBarnMedTilsynsutgift = antallBarnMedTilsynsutgift,
            tilsynsbelop = minOf(samletFaktiskUtgiftBelop, maksTilsynsbelop),
            sjablonNavnVerdiMap = sjablonNavnVerdiMap
        )

        // Finner prosentandel av totalbeløp og beregner så andel av maks tilsynsbeløp
        faktiskUtgiftListeSummertPerBarn.forEach { (key, value) ->
            var resultatBelop = if (samletFaktiskUtgiftBelop > maksTilsynsbelop) {
                value.divide(samletFaktiskUtgiftBelop, MathContext(2, RoundingMode.HALF_UP)) * maksTilsynsbelop
            } else {
                value
            }

            // Trekker fra beregnet fradragsbeløp
            resultatBelop -= fradragsbelopPerBarn

            // Setter beregnet netto barnetilsynsbeløp til 0 hvis beregnet beløp er under 0
            if (resultatBelop < BigDecimal.ZERO) {
                resultatBelop = BigDecimal.ZERO
            }

            resultatBeregningListe.add(
                ResultatBeregning(
                    soknadsbarnPersonId = key,
                    belop = resultatBelop.setScale(0, RoundingMode.HALF_UP),
                    sjablonListe = byggSjablonResultatListe(sjablonNavnVerdiMap = sjablonNavnVerdiMap, sjablonPeriodeListe = grunnlag.sjablonListe)
                )
            )
        }

        return resultatBeregningListe.sortedBy { it.soknadsbarnPersonId }
    }

    private fun beregnFradragsbelopPerBarn(
        antallBarnMedTilsynsutgift: Int,
        tilsynsbelop: BigDecimal,
        sjablonNavnVerdiMap: Map<String, BigDecimal>
    ): BigDecimal {

        val maksFradrag = sjablonNavnVerdiMap[SjablonNavn.MAKS_FRADRAG.navn] ?: BigDecimal.ZERO
        val skattAlminneligInntektProsent = (sjablonNavnVerdiMap[SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.navn] ?: BigDecimal.ZERO)
            .divide(BigDecimal.valueOf(100), MathContext(10, RoundingMode.HALF_UP))

        val maksFradragsbelop = maksFradrag * skattAlminneligInntektProsent
        val fradragsbelop = minOf((tilsynsbelop * skattAlminneligInntektProsent), maksFradragsbelop)

        var fradragsbelopPerBarn = BigDecimal.ZERO
        if (antallBarnMedTilsynsutgift > 0) {
            fradragsbelopPerBarn =
                fradragsbelop.divide(BigDecimal.valueOf(antallBarnMedTilsynsutgift.toLong()), MathContext(10, RoundingMode.HALF_UP))
        }

        return fradragsbelopPerBarn
    }

    // Henter sjablonverdier
    private fun hentSjablonVerdier(sjablonPeriodeListe: List<SjablonPeriode>, antallBarnIPerioden: Int): Map<String, BigDecimal> {

        val sjablonNavnVerdiMap = HashMap<String, BigDecimal>()
        val sjablonListe = sjablonPeriodeListe.map { it.sjablon }

        // Sjablontall
        sjablonNavnVerdiMap[SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.navn] =
            hentSjablonverdi(sjablonListe, SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT)

        // MaksTilsyn
        sjablonNavnVerdiMap[SjablonNavn.MAKS_TILSYN.navn] = hentSjablonverdi(sjablonListe, SjablonNavn.MAKS_TILSYN, antallBarnIPerioden)

        // MaksFradrag
        sjablonNavnVerdiMap[SjablonNavn.MAKS_FRADRAG.navn] = hentSjablonverdi(sjablonListe, SjablonNavn.MAKS_FRADRAG, antallBarnIPerioden)

        return sjablonNavnVerdiMap
    }
}

