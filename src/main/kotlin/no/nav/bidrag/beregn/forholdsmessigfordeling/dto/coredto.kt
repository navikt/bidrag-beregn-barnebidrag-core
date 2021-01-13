package no.nav.bidrag.beregn.forholdsmessigfordeling.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnForholdsmessigFordelingGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val beregnetBidragPeriodeListe: List<BeregnetBidragSakPeriodeCore>
)

data class BeregnetBidragSakPeriodeCore(
    val saksnr: Int,
    val periodeDatoFraTil: PeriodeCore,
    val barnPersonId: Int,
    val bidragBelop: BigDecimal,
    val resultatkode: ResultatKode
)

// Resultatperiode
data class BeregnForholdsmessigFordelingResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val saksnr: Int,
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregningListe: List<ResultatBeregningCore>,
    val resultatGrunnlagListe: List<GrunnlagBeregningPeriodisertCore>
)

data class ResultatBeregningCore(
    val barnPersonId: Int,
    val resultatBarnebidragBelop: BigDecimal,
    val resultatkode: String
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisertCore(
    val saksnr: Int,
    val barnPersonId: Int,
    val bidragBelop: BigDecimal
)

