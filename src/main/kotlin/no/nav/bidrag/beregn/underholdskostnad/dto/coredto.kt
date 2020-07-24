package no.nav.bidrag.beregn.underholdskostnad.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.time.LocalDate


// Grunnlag periode
data class BeregnUnderholdskostnadGrunnlagCore(
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
    val nettoBarnetilsynBelop: Double
)

data class ForpleiningUtgiftPeriodeCore(
    val forpleiningUtgiftPeriodeDatoFraTil: PeriodeCore,
    val forpleiningUtgiftBelop: Double
)


// Resultat
data class BeregnUnderholdskostnadResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatBelopUnderholdskostnad: Double
)

data class ResultatGrunnlagCore(
    val soknadBarnAlder: Int,
    val barnetilsynMedStonadTilsynType: String,
    val barnetilsynMedStonadStonadType: String,
    val nettoBarnetilsynBelop: Double,
    val forpleiningUtgiftBelop: Double,
    val sjablonListe: List<SjablonCore>
)