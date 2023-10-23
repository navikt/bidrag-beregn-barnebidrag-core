package no.nav.bidrag.beregn.samvaersfradrag.beregning

import no.nav.bidrag.beregn.felles.FellesBeregning
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.util.SjablonUtil.hentSjablonverdi
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatBeregning
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonNokkelNavn
import java.math.BigDecimal

class SamvaersfradragBeregningImpl : FellesBeregning(), SamvaersfradragBeregning {
    override fun beregn(grunnlag: GrunnlagBeregning): ResultatBeregning {
        // Henter sjablonverdier
        val sjablonNavnVerdiMap = hentSjablonVerdier(
            sjablonPeriodeListe = grunnlag.sjablonListe,
            samvaersklasse = grunnlag.samvaersklasse.samvaersklasse,
            soknadBarnAlder = grunnlag.soknadsbarn.alder
        )

        val belopFradrag = sjablonNavnVerdiMap[SjablonNavn.SAMVAERSFRADRAG.navn] ?: BigDecimal.ZERO

        return ResultatBeregning(
            belop = belopFradrag,
            sjablonListe = byggSjablonResultatListe(sjablonNavnVerdiMap = sjablonNavnVerdiMap, sjablonPeriodeListe = grunnlag.sjablonListe)
        )
    }

    // Henter sjablonverdier
    private fun hentSjablonVerdier(sjablonPeriodeListe: List<SjablonPeriode>, samvaersklasse: String, soknadBarnAlder: Int): Map<String, BigDecimal> {
        val sjablonNavnVerdiMap = HashMap<String, BigDecimal>()
        val sjablonListe = sjablonPeriodeListe.map { it.sjablon }

        // Samværsfradrag
        sjablonNavnVerdiMap[SjablonNavn.SAMVAERSFRADRAG.navn] = hentSjablonverdi(
            sjablonListe = sjablonListe,
            sjablonNavn = SjablonNavn.SAMVAERSFRADRAG,
            sjablonNokkelListe = listOf(SjablonNokkel(navn = SjablonNokkelNavn.SAMVAERSKLASSE.navn, verdi = samvaersklasse)),
            sjablonNokkelNavn = SjablonNokkelNavn.ALDER_TOM,
            sjablonNokkelVerdi = soknadBarnAlder,
            sjablonInnholdNavn = SjablonInnholdNavn.FRADRAG_BELOP
        )

        return sjablonNavnVerdiMap
    }
}
