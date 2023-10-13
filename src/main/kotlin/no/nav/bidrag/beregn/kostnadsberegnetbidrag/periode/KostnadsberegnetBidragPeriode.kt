package no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregning
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnetKostnadsberegnetBidragResultat

interface KostnadsberegnetBidragPeriode {
    fun beregnPerioder(grunnlag: BeregnKostnadsberegnetBidragGrunnlag): BeregnetKostnadsberegnetBidragResultat
    fun validerInput(grunnlag: BeregnKostnadsberegnetBidragGrunnlag): List<Avvik>

    companion object {
        fun getInstance(): KostnadsberegnetBidragPeriode = KostnadsberegnetBidragPeriodeImpl(KostnadsberegnetBidragBeregning.getInstance())
    }
}
