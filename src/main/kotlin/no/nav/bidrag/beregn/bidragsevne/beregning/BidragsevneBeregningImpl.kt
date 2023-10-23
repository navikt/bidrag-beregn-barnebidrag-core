package no.nav.bidrag.beregn.bidragsevne.beregning

import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning
import no.nav.bidrag.beregn.felles.FellesBeregning
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.util.SjablonUtil
import no.nav.bidrag.beregn.felles.util.SjablonUtil.hentSjablonverdi
import no.nav.bidrag.domain.enums.BostatusKode
import no.nav.bidrag.domain.enums.SaerfradragKode
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonNokkelNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

open class BidragsevneBeregningImpl : FellesBeregning(), BidragsevneBeregning {

    override fun beregn(grunnlag: GrunnlagBeregning): ResultatBeregning {
        // Henter sjablonverdier
        val sjablonNavnVerdiMap = hentSjablonVerdier(
            sjablonPeriodeListe = grunnlag.sjablonListe,
            bostatusKode = grunnlag.bostatus.kode,
            skatteklasse = grunnlag.skatteklasse.skatteklasse
        )

        // Beregner minstefradrag
        val minstefradrag = beregnMinstefradrag(
            grunnlag = grunnlag,
            minstefradragInntektSjablonBelop = sjablonNavnVerdiMap[SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.navn] ?: BigDecimal.ZERO,
            minstefradragInntektSjablonProsent = sjablonNavnVerdiMap[SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.navn] ?: BigDecimal.ZERO
        )

        // Legger sammen inntektene
        val inntekt = grunnlag.inntektListe.fold(BigDecimal.ZERO) { total, inntekt -> total + inntekt.belop }

        // finner 25% av inntekt og omregner til månedlig beløp
        val tjuefemProsentInntekt = inntekt
            .divide(BigDecimal.valueOf(4), MathContext(10, RoundingMode.HALF_UP))
            .divide(BigDecimal.valueOf(12), MathContext(10, RoundingMode.HALF_UP))
            .setScale(0, RoundingMode.HALF_UP)

        // finner personfradragklasse ut fra angitt skatteklasse
        val personfradrag = if (grunnlag.skatteklasse.skatteklasse == 1) {
            sjablonNavnVerdiMap[SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.navn]
        } else {
            sjablonNavnVerdiMap[SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.navn]
        }

        val inntektMinusFradrag = inntekt - minstefradrag - (personfradrag ?: BigDecimal.ZERO)

        // Trekker fra skatt
        var forelopigBidragsevne =
            inntekt - (
                inntektMinusFradrag * (sjablonNavnVerdiMap[SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn] ?: BigDecimal.ZERO)
                    .divide(BigDecimal.valueOf(100), MathContext(10, RoundingMode.HALF_UP))
                )

        // Trekker fra trygdeavgift
        forelopigBidragsevne -= inntekt * (sjablonNavnVerdiMap[SjablonTallNavn.TRYGDEAVGIFT_PROSENT.navn] ?: BigDecimal.ZERO)
            .divide(BigDecimal.valueOf(100), MathContext(10, RoundingMode.HALF_UP))

        // Trekker fra trinnvis skatt
        forelopigBidragsevne -= beregnSkattetrinnBelop(grunnlag)

        // Trekker fra boutgifter og midler til eget underhold
        forelopigBidragsevne -= (sjablonNavnVerdiMap[SjablonInnholdNavn.BOUTGIFT_BELOP.navn] ?: BigDecimal.ZERO) * BigDecimal.valueOf(12)
        forelopigBidragsevne -= (sjablonNavnVerdiMap[SjablonInnholdNavn.UNDERHOLD_BELOP.navn] ?: BigDecimal.ZERO) * BigDecimal.valueOf(12)

        // Trekker fra midler til underhold egne barn i egen husstand
        forelopigBidragsevne -= (
            sjablonNavnVerdiMap[SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.navn]
                ?: BigDecimal.ZERO
            ) * BigDecimal.valueOf(grunnlag.barnIHusstand.antallBarn) * BigDecimal.valueOf(12)

        // Sjekker om og kalkulerer eventuell fordel særfradrag
        forelopigBidragsevne = when (grunnlag.saerfradrag.kode) {
            SaerfradragKode.HELT -> forelopigBidragsevne + (sjablonNavnVerdiMap[SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.navn] ?: BigDecimal.ZERO)
            SaerfradragKode.HALVT -> forelopigBidragsevne + (sjablonNavnVerdiMap[SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.navn] ?: BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(2), MathContext(10, RoundingMode.HALF_UP))

            else -> forelopigBidragsevne
        }

        // Legger til fordel skatteklasse2
        if (grunnlag.skatteklasse.skatteklasse == 2) {
            forelopigBidragsevne += sjablonNavnVerdiMap[SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.navn] ?: BigDecimal.ZERO
        }

        // Finner månedlig beløp for bidragsevne
        val maanedligBidragsevne = forelopigBidragsevne
            .divide(BigDecimal.valueOf(12), MathContext(10, RoundingMode.HALF_UP))
            .max(BigDecimal.ZERO)
            .setScale(0, RoundingMode.HALF_UP)

        return ResultatBeregning(
            belop = maanedligBidragsevne,
            inntekt25Prosent = tjuefemProsentInntekt,
            sjablonListe = byggSjablonResultatListe(sjablonNavnVerdiMap, grunnlag.sjablonListe)
        )
    }

    private fun beregnMinstefradrag(
        grunnlag: GrunnlagBeregning,
        minstefradragInntektSjablonBelop: BigDecimal,
        minstefradragInntektSjablonProsent: BigDecimal
    ): BigDecimal {
        // Legger sammen inntektene
        val inntekt = grunnlag.inntektListe.fold(BigDecimal.ZERO) { total, inntekt -> total + inntekt.belop }

        return (inntekt * minstefradragInntektSjablonProsent.divide(BigDecimal.valueOf(100), MathContext(2, RoundingMode.HALF_UP)))
            .min(minstefradragInntektSjablonBelop)
            .setScale(0, RoundingMode.HALF_UP)
    }

    private fun beregnSkattetrinnBelop(grunnlag: GrunnlagBeregning): BigDecimal {
        // Legger sammen inntektene
        val inntekt = grunnlag.inntektListe.fold(BigDecimal.ZERO) { total, inntekt -> total + inntekt.belop }

        val sortertTrinnvisSkattesatsListe = SjablonUtil.hentTrinnvisSkattesats(
            sjablonListe = grunnlag.sjablonListe.map { it.sjablon },
            sjablonNavn = SjablonNavn.TRINNVIS_SKATTESATS
        )

        var samletSkattetrinnBelop = BigDecimal.ZERO
        var indeks = 1

        // Beregn skattetrinnbeløp
        while (indeks < sortertTrinnvisSkattesatsListe.size) {
            val denneGrense = sortertTrinnvisSkattesatsListe[indeks - 1].inntektGrense
            val nesteGrense = sortertTrinnvisSkattesatsListe[indeks].inntektGrense

            if (inntekt > denneGrense) {
                val taxableIncome = minOf(inntekt, nesteGrense) - denneGrense
                val taxRate = sortertTrinnvisSkattesatsListe[indeks - 1].sats

                samletSkattetrinnBelop += (taxableIncome * taxRate).divide(BigDecimal.valueOf(100), MathContext(10, RoundingMode.HALF_UP))
            }
            indeks++
        }

        if (inntekt > sortertTrinnvisSkattesatsListe[indeks - 1].inntektGrense) {
            val taxableIncome = inntekt - sortertTrinnvisSkattesatsListe[indeks - 1].inntektGrense
            val taxRate = sortertTrinnvisSkattesatsListe[indeks - 1].sats

            samletSkattetrinnBelop += (taxableIncome * taxRate / BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
        }

        return samletSkattetrinnBelop.setScale(0, RoundingMode.HALF_UP)
    }

    // Henter sjablonverdier
    private fun hentSjablonVerdier(
        sjablonPeriodeListe: List<SjablonPeriode>,
        bostatusKode: BostatusKode,
        skatteklasse: Int
    ): Map<String, BigDecimal> {
        val sjablonNavnVerdiMap = HashMap<String, BigDecimal>()
        val sjablonListe = sjablonPeriodeListe.map { it.sjablon }

        // Sjablontall
        if (skatteklasse == 1) {
            sjablonNavnVerdiMap[SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.navn] =
                hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP)
        } else {
            sjablonNavnVerdiMap[SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.navn] =
                hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP)
            sjablonNavnVerdiMap[SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.navn] =
                hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP)
        }

        sjablonNavnVerdiMap[SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn] =
            hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT)
        sjablonNavnVerdiMap[SjablonTallNavn.TRYGDEAVGIFT_PROSENT.navn] =
            hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.TRYGDEAVGIFT_PROSENT)
        sjablonNavnVerdiMap[SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.navn] =
            hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP)
        sjablonNavnVerdiMap[SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.navn] =
            hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP)
        sjablonNavnVerdiMap[SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.navn] =
            hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT)
        sjablonNavnVerdiMap[SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.navn] =
            hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP)

        // Bidragsevne
        val sjablonNokkelVerdi = if (bostatusKode == BostatusKode.ALENE) "EN" else "GS"
        sjablonNavnVerdiMap[SjablonInnholdNavn.BOUTGIFT_BELOP.navn] = hentSjablonverdi(
            sjablonListe = sjablonListe,
            sjablonNavn = SjablonNavn.BIDRAGSEVNE,
            sjablonNokkelListe = listOf(SjablonNokkel(navn = SjablonNokkelNavn.BOSTATUS.navn, verdi = sjablonNokkelVerdi)),
            sjablonInnholdNavn = SjablonInnholdNavn.BOUTGIFT_BELOP
        )
        sjablonNavnVerdiMap[SjablonInnholdNavn.UNDERHOLD_BELOP.navn] = hentSjablonverdi(
            sjablonListe = sjablonListe,
            sjablonNavn = SjablonNavn.BIDRAGSEVNE,
            sjablonNokkelListe = listOf(SjablonNokkel(SjablonNokkelNavn.BOSTATUS.navn, sjablonNokkelVerdi)),
            sjablonInnholdNavn = SjablonInnholdNavn.UNDERHOLD_BELOP
        )

        // TrinnvisSkattesats
        val trinnvisSkattesatsListe = SjablonUtil.hentTrinnvisSkattesats(sjablonListe = sjablonListe, sjablonNavn = SjablonNavn.TRINNVIS_SKATTESATS)
        var indeks = 1
        trinnvisSkattesatsListe.forEach {
            sjablonNavnVerdiMap[SjablonNavn.TRINNVIS_SKATTESATS.navn + "InntektGrense" + indeks] = it.inntektGrense
            sjablonNavnVerdiMap[SjablonNavn.TRINNVIS_SKATTESATS.navn + "Sats" + indeks] = it.sats
            indeks++
        }

        return sjablonNavnVerdiMap
    }
}
