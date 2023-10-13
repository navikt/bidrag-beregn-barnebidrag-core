package no.nav.bidrag.beregn.barnebidrag.periode

import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat
import no.nav.bidrag.beregn.felles.bo.Avvik

interface BarnebidragPeriode {
    fun beregnPerioder(grunnlag: BeregnBarnebidragGrunnlag): BeregnBarnebidragResultat
    fun validerInput(grunnlag: BeregnBarnebidragGrunnlag): List<Avvik>

    companion object {
        fun getInstance(): BarnebidragPeriode = BarnebidragPeriodeImpl(BarnebidragBeregning.getInstance())
    }
}
