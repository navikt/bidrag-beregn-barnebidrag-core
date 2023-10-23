package no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.math.BigDecimal

data class InntektPeriode(
    val referanse: String,
    val inntektPeriode: Periode,
    val type: String,
    val belop: BigDecimal,
    val deltFordel: Boolean,
    val skatteklasse2: Boolean
) : PeriodisertGrunnlag {

    constructor(inntektPeriode: InntektPeriode) : this(
        inntektPeriode.referanse,
        inntektPeriode.inntektPeriode.justerDatoer(),
        inntektPeriode.type,
        inntektPeriode.belop,
        inntektPeriode.deltFordel,
        inntektPeriode.skatteklasse2
    )

    override fun getPeriode(): Periode {
        return inntektPeriode
    }
}

data class UnderholdskostnadPeriode(
    val referanse: String,
    val underholdskostnadPeriode: Periode,
    val belop: BigDecimal
) : PeriodisertGrunnlag {

    constructor(underholdskostnadPeriode: UnderholdskostnadPeriode) : this(
        underholdskostnadPeriode.referanse,
        underholdskostnadPeriode.underholdskostnadPeriode.justerDatoer(),
        underholdskostnadPeriode.belop
    )

    override fun getPeriode(): Periode {
        return underholdskostnadPeriode
    }
}
