package no.nav.bidrag.beregn.underholdskostnad.beregning

import no.nav.bidrag.beregn.felles.FellesBeregning
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.util.SjablonUtil.hentSjablonverdi
import no.nav.bidrag.beregn.underholdskostnad.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning
import no.nav.bidrag.domene.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonNøkkelNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonTallNavn
import java.math.BigDecimal

open class UnderholdskostnadBeregningImpl : FellesBeregning(), UnderholdskostnadBeregning {
    // Uten barnetrygd: Blir kun kalt for å beregne for barnets fødselsmåned
    // Ordinær barnetrygd: Beregner for perioder frem til 01.07.2021
    // Forhøyet barnetrygd: Beregner for perioder fra 01.07.2021 og fremover, inkluderer både ordinær og forhøyet barnetrygd
    override fun beregn(grunnlag: GrunnlagBeregning, barnetrygdIndikator: String): ResultatBeregning {
        // Henter sjablonverdier
        val sjablonNavnVerdiMap =
            hentSjablonVerdier(
                sjablonPeriodeListe = grunnlag.sjablonListe,
                soknadBarnAlder = grunnlag.soknadsbarn.alder,
                tilsynType = grunnlag.barnetilsynMedStonad.tilsynType,
                stonadType = grunnlag.barnetilsynMedStonad.stonadType,
                barnetrygdIndikator = barnetrygdIndikator,
            )
        val beregnetUnderholdskostnad =
            (sjablonNavnVerdiMap[SjablonNavn.FORBRUKSUTGIFTER.navn] ?: BigDecimal.ZERO) // Forbruksutgifter
                .add(sjablonNavnVerdiMap[SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELØP.navn] ?: BigDecimal.ZERO) // Boutgifter
                .add(sjablonNavnVerdiMap[SjablonNavn.BARNETILSYN.navn] ?: BigDecimal.ZERO) // Barnetilsyn
                .add(grunnlag.nettoBarnetilsyn.belop) // Netto barnetilsyn
                .subtract(hentBarnetrygdBelop(sjablonNavnVerdiMap, barnetrygdIndikator)) // Barnetrygd
                .subtract(grunnlag.forpleiningUtgift.belop) // Forpleiningsutgifter

        return ResultatBeregning(
            belop = maxOf(beregnetUnderholdskostnad, BigDecimal.ZERO),
            sjablonListe = byggSjablonResultatListe(sjablonNavnVerdiMap = sjablonNavnVerdiMap, sjablonPeriodeListe = grunnlag.sjablonListe),
        )
    }

    private fun hentBarnetrygdBelop(sjablonNavnVerdiMap: Map<String, BigDecimal>, barnetrygdIndikator: String) = when (barnetrygdIndikator) {
        ORDINAER_BARNETRYGD -> sjablonNavnVerdiMap[SjablonTallNavn.ORDINÆR_BARNETRYGD_BELØP.navn] ?: BigDecimal.ZERO
        FORHOYET_BARNETRYGD -> sjablonNavnVerdiMap[SjablonTallNavn.FORHØYET_BARNETRYGD_BELØP.navn] ?: BigDecimal.ZERO
        else -> BigDecimal.ZERO
    }

    // Henter sjablonverdier
    private fun hentSjablonVerdier(
        sjablonPeriodeListe: List<SjablonPeriode>,
        soknadBarnAlder: Int,
        tilsynType: String,
        stonadType: String,
        barnetrygdIndikator: String,
    ): Map<String, BigDecimal> {
        val sjablonNavnVerdiMap = HashMap<String, BigDecimal>()
        val sjablonListe = sjablonPeriodeListe.map { it.sjablon }

        // Sjablontall
        sjablonNavnVerdiMap[SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELØP.navn] =
            hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELØP)
        if (barnetrygdIndikator == ORDINAER_BARNETRYGD) {
            sjablonNavnVerdiMap[SjablonTallNavn.ORDINÆR_BARNETRYGD_BELØP.navn] =
                hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.ORDINÆR_BARNETRYGD_BELØP)
        } else if (barnetrygdIndikator == FORHOYET_BARNETRYGD) {
            sjablonNavnVerdiMap[SjablonTallNavn.FORHØYET_BARNETRYGD_BELØP.navn] =
                hentSjablonverdi(sjablonListe = sjablonListe, sjablonTallNavn = SjablonTallNavn.FORHØYET_BARNETRYGD_BELØP)
        }

        // Forbruksutgifter
        sjablonNavnVerdiMap[SjablonNavn.FORBRUKSUTGIFTER.navn] =
            hentSjablonverdi(
                sjablonListe = sjablonListe,
                sjablonNavn = SjablonNavn.FORBRUKSUTGIFTER,
                sjablonNokkelVerdi = soknadBarnAlder,
            )

        // Barnetilsyn
        val sjablonNokkelListe = mutableListOf<SjablonNokkel>()
        sjablonNokkelListe.add(SjablonNokkel(navn = SjablonNøkkelNavn.TILSYN_TYPE.navn, verdi = tilsynType))
        sjablonNokkelListe.add(SjablonNokkel(navn = SjablonNøkkelNavn.STØNAD_TYPE.navn, verdi = stonadType))

        sjablonNavnVerdiMap[SjablonNavn.BARNETILSYN.navn] =
            hentSjablonverdi(
                sjablonListe = sjablonListe,
                sjablonNavn = SjablonNavn.BARNETILSYN,
                sjablonNokkelListe = sjablonNokkelListe,
                sjablonInnholdNavn = SjablonInnholdNavn.BARNETILSYN_BELØP,
            )

        return sjablonNavnVerdiMap
    }

    companion object {
        protected const val ORDINAER_BARNETRYGD = "O"
        protected const val FORHOYET_BARNETRYGD = "F"
    }
}
