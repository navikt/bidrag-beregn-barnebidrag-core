package no.nav.bidrag.beregn.bidragsevne.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.domene.enums.beregning.Særfradragskode
import no.nav.bidrag.domene.enums.person.Bostatuskode
import java.math.BigDecimal

data class InntektPeriode(
    val referanse: String,
    val inntektPeriode: Periode,
    val type: String,
    val belop: BigDecimal,
) : PeriodisertGrunnlag {
    constructor(inntektPeriode: InntektPeriode) : this(
        inntektPeriode.referanse,
        inntektPeriode.inntektPeriode.justerDatoer(),
        inntektPeriode.type,
        inntektPeriode.belop,
    )

    override fun getPeriode(): Periode {
        return inntektPeriode
    }
}

data class SkatteklassePeriode(
    val referanse: String,
    val skatteklassePeriode: Periode,
    val skatteklasse: Int,
) : PeriodisertGrunnlag {
    constructor(skatteklassePeriode: SkatteklassePeriode) : this(
        skatteklassePeriode.referanse,
        skatteklassePeriode.skatteklassePeriode.justerDatoer(),
        skatteklassePeriode.skatteklasse,
    )

    override fun getPeriode(): Periode {
        return skatteklassePeriode
    }
}

data class BostatusPeriode(
    val referanse: String,
    val bostatusPeriode: Periode,
    val kode: Bostatuskode,
) : PeriodisertGrunnlag {
    constructor(bostatusPeriode: BostatusPeriode) : this(
        bostatusPeriode.referanse,
        bostatusPeriode.bostatusPeriode.justerDatoer(),
        bostatusPeriode.kode,
    )

    override fun getPeriode(): Periode {
        return bostatusPeriode
    }
}

data class BarnIHusstandPeriode(
    val referanse: String,
    val barnIHusstandPeriode: Periode,
    val antallBarn: Double,
) : PeriodisertGrunnlag {
    constructor(barnIHusstandPeriode: BarnIHusstandPeriode) : this(
        barnIHusstandPeriode.referanse,
        barnIHusstandPeriode.barnIHusstandPeriode.justerDatoer(),
        barnIHusstandPeriode.antallBarn,
    )

    override fun getPeriode(): Periode {
        return barnIHusstandPeriode
    }
}

data class SaerfradragPeriode(
    val referanse: String,
    val saerfradragPeriode: Periode,
    val kode: Særfradragskode,
) : PeriodisertGrunnlag {
    constructor(saerfradragPeriode: SaerfradragPeriode) : this(
        saerfradragPeriode.referanse,
        saerfradragPeriode.saerfradragPeriode.justerDatoer(),
        saerfradragPeriode.kode,
    )

    override fun getPeriode(): Periode {
        return saerfradragPeriode
    }
}
