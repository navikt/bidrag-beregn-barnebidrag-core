package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.math.BigDecimal
import java.time.LocalDate

data class FaktiskUtgiftPeriode(
    val soknadsbarnPersonId: Int,
    val referanse: String,
    val faktiskUtgiftPeriode: Periode,
    val soknadsbarnFodselsdato: LocalDate,
    val belop: BigDecimal
) : PeriodisertGrunnlag {

    constructor(faktiskUtgiftPeriode: FaktiskUtgiftPeriode) : this(
        faktiskUtgiftPeriode.soknadsbarnPersonId,
        faktiskUtgiftPeriode.referanse,
        faktiskUtgiftPeriode.faktiskUtgiftPeriode.justerDatoer(),
        faktiskUtgiftPeriode.soknadsbarnFodselsdato,
        faktiskUtgiftPeriode.belop
    )

    override fun getPeriode(): Periode {
        return faktiskUtgiftPeriode
    }
}
