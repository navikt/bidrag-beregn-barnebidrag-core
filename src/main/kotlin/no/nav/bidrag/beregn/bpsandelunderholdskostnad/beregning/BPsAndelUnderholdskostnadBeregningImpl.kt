package no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning
import no.nav.bidrag.beregn.felles.FellesBeregning
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.util.SjablonUtil.hentSjablonverdi
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

open class BPsAndelUnderholdskostnadBeregningImpl : FellesBeregning(), BPsAndelUnderholdskostnadBeregning {
    override fun beregn(grunnlag: GrunnlagBeregning, beregnMedNyeRegler: Boolean): ResultatBeregning {
        var andelProsent = BigDecimal.ZERO
        var andelBelop = BigDecimal.ZERO
        var barnetErSelvforsorget = false

        // Henter sjablonverdier
        val sjablonNavnVerdiMap = hentSjablonVerdier(grunnlag.sjablonListe)

        // Legger sammen inntektene
        val inntektBP = grunnlag.inntektBPListe.fold(BigDecimal.ZERO) { total, inntektBP -> total + inntektBP.belop }
        val inntektBM = grunnlag.inntektBMListe.fold(BigDecimal.ZERO) { total, inntektBM -> total + inntektBM.belop }
        val inntektBB = grunnlag.inntektBBListe.fold(BigDecimal.ZERO) { total, inntektBB -> total + inntektBB.belop }

        val forskuddssatsBelop = sjablonNavnVerdiMap[SjablonTallNavn.FORSKUDDSSATS_BELOP.navn] ?: BigDecimal.ZERO

        // Hvis barnets inntekt er høyere enn 100 ganger sats for forhøyet forskudd skal ikke BPs andel regnes ut.
        if ((forskuddssatsBelop > BigDecimal.ZERO) && inntektBB > (forskuddssatsBelop * BigDecimal.valueOf(100))) {
            barnetErSelvforsorget = true
        } else {
            andelProsent = if (beregnMedNyeRegler) {
                beregnMedNyeRegler(inntektBP, inntektBM, inntektBB, forskuddssatsBelop)
            } else {
                beregnMedGamleRegler(inntektBP, inntektBM, inntektBB)
            }
            andelBelop = (grunnlag.underholdskostnad.belop * andelProsent).setScale(0, RoundingMode.HALF_UP)
        }
        return ResultatBeregning(
            andelProsent = andelProsent,
            andelBelop = andelBelop,
            barnetErSelvforsorget = barnetErSelvforsorget,
            sjablonListe = byggSjablonResultatListe(sjablonNavnVerdiMap = sjablonNavnVerdiMap, sjablonPeriodeListe = grunnlag.sjablonListe)
        )
    }

    private fun beregnMedNyeRegler(inntektBP: BigDecimal, inntektBM: BigDecimal, inntektBB: BigDecimal, forskuddsatsBelop: BigDecimal): BigDecimal {
        val justertInntektBB = maxOf(inntektBB - (forskuddsatsBelop * BigDecimal.valueOf(30)), BigDecimal.ZERO)

        val andelProsent = inntektBP
            .divide((inntektBP + inntektBM + justertInntektBB), MathContext(10))
            .setScale(12, RoundingMode.HALF_UP)

        // Hvis beregnet andelProsent blir større enn 5/6 returneres 12 desimaltall, ellers 3
        return if (andelProsent >= BigDecimal.valueOf(0.833333333333)) {
            BigDecimal.valueOf(0.833333333333)
        } else {
            andelProsent.setScale(3, RoundingMode.HALF_UP)
        }
    }

    private fun beregnMedGamleRegler(inntektBP: BigDecimal, inntektBM: BigDecimal, inntektBB: BigDecimal): BigDecimal {
        // Med gamle regler skal beregnet fordelingsnøkkel rundes av til nærmeste sjettedel, men ikke over 5/6
        var andelProsent = inntektBP
            .divide((inntektBP + inntektBM + inntektBB), MathContext(10, RoundingMode.HALF_UP))

        // Lager en liste med sjettedeler fra 1/6 til 5/6
        val sjettedeler = (1..5).map { BigDecimal(it).divide(BigDecimal(6), MathContext(12, RoundingMode.HALF_UP)) }

        // Finner den sjettedelen som er nærmest beregnet andel
        // (Løper gjennom sjettedel-lista, trekker sjettedel-verdi fra andelProsent og finner absoluttverdi. Til slutt blir laveste verdi valgt)
        andelProsent = sjettedeler.minByOrNull { andelProsent.subtract(it).abs() } ?: BigDecimal.ZERO

        // Hvis beregnet andelProsent blir større enn 5/6 returneres 12 desimaltall, ellers 3
        return if (andelProsent >= BigDecimal.valueOf(0.833333333333)) {
            BigDecimal.valueOf(0.833333333333)
        } else {
            andelProsent.setScale(3, RoundingMode.HALF_UP)
        }
    }

    // Henter sjablonverdier
    private fun hentSjablonVerdier(sjablonPeriodeListe: List<SjablonPeriode>): Map<String, BigDecimal> {
        val sjablonNavnVerdiMap = HashMap<String, BigDecimal>()
        val sjablonListe = sjablonPeriodeListe.map { it.sjablon }

        // Sjablontall
        sjablonNavnVerdiMap[SjablonTallNavn.FORSKUDDSSATS_BELOP.navn] = hentSjablonverdi(
            sjablonListe = sjablonListe,
            sjablonTallNavn = SjablonTallNavn.FORSKUDDSSATS_BELOP
        )

        return sjablonNavnVerdiMap
    }
}
