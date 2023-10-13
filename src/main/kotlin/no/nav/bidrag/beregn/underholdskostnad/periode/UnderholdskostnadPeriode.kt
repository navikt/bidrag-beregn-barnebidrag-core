package no.nav.bidrag.beregn.underholdskostnad.periode

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.underholdskostnad.beregning.UnderholdskostnadBeregning
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnetUnderholdskostnadResultat

interface UnderholdskostnadPeriode {
    fun beregnPerioder(grunnlag: BeregnUnderholdskostnadGrunnlag): BeregnetUnderholdskostnadResultat
    fun validerInput(grunnlag: BeregnUnderholdskostnadGrunnlag): List<Avvik>

    companion object {
        fun getInstance(): UnderholdskostnadPeriode = UnderholdskostnadPeriodeImpl(UnderholdskostnadBeregning.getInstance())
    }
}
