package no.nav.bidrag.beregn.underholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.math.BigDecimal

data class BarnetilsynMedStonadPeriode(
    val referanse: String,
    val barnetilsynMedStonadPeriode: Periode,
    val tilsynType: String,
    val stonadType: String,
) : PeriodisertGrunnlag {
    constructor(barnetilsynMedStonadPeriode: BarnetilsynMedStonadPeriode) : this(
        barnetilsynMedStonadPeriode.referanse,
        barnetilsynMedStonadPeriode.barnetilsynMedStonadPeriode.justerDatoer(),
        barnetilsynMedStonadPeriode.tilsynType,
        barnetilsynMedStonadPeriode.stonadType,
    )

    override fun getPeriode(): Periode {
        return barnetilsynMedStonadPeriode
    }
}

data class NettoBarnetilsynPeriode(
    val referanse: String,
    val nettoBarnetilsynPeriode: Periode,
    val belop: BigDecimal,
) : PeriodisertGrunnlag {
    constructor(nettoBarnetilsynPeriode: NettoBarnetilsynPeriode) : this(
        nettoBarnetilsynPeriode.referanse,
        nettoBarnetilsynPeriode.nettoBarnetilsynPeriode.justerDatoer(),
        nettoBarnetilsynPeriode.belop,
    )

    override fun getPeriode(): Periode {
        return nettoBarnetilsynPeriode
    }
}

data class ForpleiningUtgiftPeriode(
    val referanse: String,
    val forpleiningUtgiftPeriode: Periode,
    val belop: BigDecimal,
) : PeriodisertGrunnlag {
    constructor(forpleiningUtgiftPeriode: ForpleiningUtgiftPeriode) : this(
        forpleiningUtgiftPeriode.referanse,
        forpleiningUtgiftPeriode.forpleiningUtgiftPeriode.justerDatoer(),
        forpleiningUtgiftPeriode.belop,
    )

    override fun getPeriode(): Periode {
        return forpleiningUtgiftPeriode
    }
}
