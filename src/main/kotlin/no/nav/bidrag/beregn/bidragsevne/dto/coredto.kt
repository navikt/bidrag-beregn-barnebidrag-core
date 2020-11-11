package no.nav.bidrag.beregn.bidragsevne.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnBidragsevneGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val inntektPeriodeListe: List<InntektPeriodeCore>,
    val skatteklassePeriodeListe: List<SkatteklassePeriodeCore>,
    val bostatusPeriodeListe: List<BostatusPeriodeCore>,
    val antallBarnIEgetHusholdPeriodeListe: List<AntallBarnIEgetHusholdPeriodeCore>,
    val saerfradragPeriodeListe: List<SaerfradragPeriodeCore>,
    var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class InntektPeriodeCore(
    val inntektPeriodeDatoFraTil: PeriodeCore,
    val inntektType: String,
    val inntektBelop: BigDecimal
)

data class SkatteklassePeriodeCore(
    val skatteklassePeriodeDatoFraTil: PeriodeCore,
    val skatteklasse: Int
)

data class BostatusPeriodeCore(
    val bostatusPeriodeDatoFraTil: PeriodeCore,
    val bostatusKode: String
)

data class AntallBarnIEgetHusholdPeriodeCore(
    val antallBarnIEgetHusholdPeriodeDatoFraTil: PeriodeCore,
    val antallBarn: Int
)

data class SaerfradragPeriodeCore(
    val saerfradragPeriodeDatoFraTil: PeriodeCore,
    val saerfradragKode: String
)


// Resultatperiode
data class BeregnBidragsevneResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatEvneBelop: BigDecimal,
    val resultat25ProsentInntekt: BigDecimal
)

// Grunnlag beregning
data class ResultatGrunnlagCore(
    val inntektListe: List<InntektCore>,
    val skatteklasse: Int,
    val bostatusKode: String,
    val antallEgneBarnIHusstand: Int,
    val saerfradragkode: String,
    val sjablonListe: List<SjablonNavnVerdiCore>
)

data class InntektCore(
    val inntektType: String,
    val inntektBelop: BigDecimal
)
