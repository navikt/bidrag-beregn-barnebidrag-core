package no.nav.bidrag.beregn.forholdsmessigfordeling

import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnForholdsmessigFordelingGrunnlagCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnForholdsmessigFordelingResultatCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.periode.ForholdsmessigFordelingPeriode

fun interface ForholdsmessigFordelingCore {
    fun beregnForholdsmessigFordeling(grunnlag: BeregnForholdsmessigFordelingGrunnlagCore): BeregnForholdsmessigFordelingResultatCore

    companion object {
        fun getInstance(): ForholdsmessigFordelingCore = ForholdsmessigFordelingCoreImpl(ForholdsmessigFordelingPeriode.getInstance())
    }
}
