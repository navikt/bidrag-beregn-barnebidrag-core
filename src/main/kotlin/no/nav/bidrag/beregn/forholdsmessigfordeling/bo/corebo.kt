package no.nav.bidrag.beregn.forholdsmessigfordeling.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnForholdsmessigFordelingGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val beregnetBidragPeriodeListe: List<BeregnetBidragSakPeriode>
)

// Resultatperiode
data class BeregnForholdsmessigFordelingResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val saksnr: Int,
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregningListe: List<ResultatBeregning>,
    val resultatGrunnlagListe: List<GrunnlagBeregningPeriodisert>
)

data class ResultatBeregning(
    val barnPersonId: Int,
    val resultatBarnebidragBelop: BigDecimal,
    val resultatkode: String
)


// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val saksnr: Int,
    val barnPersonId: Int,
    val bidragBelop: BigDecimal
)