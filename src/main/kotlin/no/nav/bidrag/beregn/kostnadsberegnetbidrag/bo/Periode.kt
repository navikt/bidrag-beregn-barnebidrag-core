package no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import java.math.BigDecimal

data class UnderholdskostnadPeriode(
    val referanse: String,
    val underholdskostnadPeriode: Periode,
    val belop: BigDecimal,
) : PeriodisertGrunnlag {
    constructor(underholdskostnadPeriode: UnderholdskostnadPeriode) : this(
        underholdskostnadPeriode.referanse,
        underholdskostnadPeriode.underholdskostnadPeriode.justerDatoer(),
        underholdskostnadPeriode.belop,
    )

    override fun getPeriode(): Periode {
        return underholdskostnadPeriode
    }
}

data class BPsAndelUnderholdskostnadPeriode(
    val referanse: String,
    val bPsAndelUnderholdskostnadPeriode: Periode,
    val andelProsent: BigDecimal,
) : PeriodisertGrunnlag {
    constructor(bPsAndelunderholdskostnadPeriode: BPsAndelUnderholdskostnadPeriode) : this(
        bPsAndelunderholdskostnadPeriode.referanse,
        bPsAndelunderholdskostnadPeriode.bPsAndelUnderholdskostnadPeriode.justerDatoer(),
        bPsAndelunderholdskostnadPeriode.andelProsent,
    )

    override fun getPeriode(): Periode {
        return bPsAndelUnderholdskostnadPeriode
    }
}

data class SamvaersfradragPeriode(
    val referanse: String,
    val samvaersfradragPeriode: Periode,
    val belop: BigDecimal,
) : PeriodisertGrunnlag {
    constructor(samvaersfradragPeriode: SamvaersfradragPeriode) : this(
        samvaersfradragPeriode.referanse,
        samvaersfradragPeriode.samvaersfradragPeriode.justerDatoer(),
        samvaersfradragPeriode.belop,
    )

    override fun getPeriode(): Periode {
        return samvaersfradragPeriode
    }
}
