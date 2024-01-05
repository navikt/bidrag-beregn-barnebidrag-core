package no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning.BPsAndelUnderholdskostnadBeregning
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnetBPsAndelUnderholdskostnadResultat
import no.nav.bidrag.beregn.felles.bo.Avvik

interface BPsAndelUnderholdskostnadPeriode {
    fun beregnPerioder(grunnlag: BeregnBPsAndelUnderholdskostnadGrunnlag): BeregnetBPsAndelUnderholdskostnadResultat

    fun validerInput(grunnlag: BeregnBPsAndelUnderholdskostnadGrunnlag): List<Avvik>

    companion object {
        fun getInstance(): BPsAndelUnderholdskostnadPeriode = BPsAndelUnderholdskostnadPeriodeImpl(
            BPsAndelUnderholdskostnadBeregning.getInstance(),
        )
    }
}
