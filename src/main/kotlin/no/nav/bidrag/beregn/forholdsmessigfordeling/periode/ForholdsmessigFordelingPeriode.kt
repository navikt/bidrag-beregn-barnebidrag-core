package no.nav.bidrag.beregn.forholdsmessigfordeling.periode

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.forholdsmessigfordeling.beregning.ForholdsmessigFordelingBeregning
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingGrunnlag
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingResultat

interface ForholdsmessigFordelingPeriode {
    fun beregnPerioder(grunnlag: BeregnForholdsmessigFordelingGrunnlag): BeregnForholdsmessigFordelingResultat

    fun validerInput(grunnlag: BeregnForholdsmessigFordelingGrunnlag): List<Avvik>

    companion object {
        fun getInstance(): ForholdsmessigFordelingPeriode = ForholdsmessigFordelingPeriodeImpl(
            ForholdsmessigFordelingBeregning.getInstance(),
        )
    }
}
