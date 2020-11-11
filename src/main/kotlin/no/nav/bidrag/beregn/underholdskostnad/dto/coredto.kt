package no.nav.bidrag.beregn.underholdskostnad.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.math.BigDecimal
import java.time.LocalDate


// Grunnlag periode
data class BeregnUnderholdskostnadGrunnlagCore(
    val soknadsbarnPersonId: Int,
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadBarnFodselsdato: LocalDate,
    val barnetilsynMedStonadPeriodeListe: List<BarnetilsynMedStonadPeriodeCore>,
    var nettoBarnetilsynPeriodeListe: List<NettoBarnetilsynPeriodeCore>,
    val forpleiningUtgiftPeriodeListe: List<ForpleiningUtgiftPeriodeCore>,
    var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class BarnetilsynMedStonadPeriodeCore(
    val barnetilsynMedStonadPeriodeDatoFraTil: PeriodeCore,
    val barnetilsynMedStonadTilsynType: String,
    val barnetilsynStonadStonadType: String
)

data class NettoBarnetilsynPeriodeCore(
    val nettoBarnetilsynPeriodeDatoFraTil: PeriodeCore,
    val nettoBarnetilsynBelop: BigDecimal
)

data class ForpleiningUtgiftPeriodeCore(
    val forpleiningUtgiftPeriodeDatoFraTil: PeriodeCore,
    val forpleiningUtgiftBelop: BigDecimal
)


// Resultatperiode
data class BeregnUnderholdskostnadResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val soknadsbarnPersonId: Int,
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatBelopUnderholdskostnad: BigDecimal
)

// Grunnlag beregning
data class ResultatGrunnlagCore(
    val soknadBarnAlder: Int,
    val barnetilsynMedStonadTilsynType: String,
    val barnetilsynMedStonadStonadType: String,
    val nettoBarnetilsynBelop: BigDecimal,
    val forpleiningUtgiftBelop: BigDecimal,
    val sjablonListe: List<SjablonNavnVerdiCore>
)
