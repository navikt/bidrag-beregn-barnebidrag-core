package no.nav.bidrag.beregn.nettobarnetilsyn.periode

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.nettobarnetilsyn.beregning.NettoBarnetilsynBeregning
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnetNettoBarnetilsynResultat

interface NettoBarnetilsynPeriode {
    fun beregnPerioder(grunnlag: BeregnNettoBarnetilsynGrunnlag): BeregnetNettoBarnetilsynResultat

    fun validerInput(grunnlag: BeregnNettoBarnetilsynGrunnlag): List<Avvik>

    companion object {
        fun getInstance(): NettoBarnetilsynPeriode = NettoBarnetilsynPeriodeImpl(NettoBarnetilsynBeregning.getInstance())
    }
}
