package no.nav.bidrag.beregn.samvaersfradrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag

data class SamvaersklassePeriode(
    val referanse: String,
    val samvaersklassePeriode: Periode,
    val samvaersklasse: String,
) : PeriodisertGrunnlag {
    constructor(samvaersklassePeriode: SamvaersklassePeriode) : this(
        samvaersklassePeriode.referanse,
        samvaersklassePeriode.samvaersklassePeriode.justerDatoer(),
        samvaersklassePeriode.samvaersklasse,
    )

    override fun getPeriode(): Periode {
        return samvaersklassePeriode
    }
}
