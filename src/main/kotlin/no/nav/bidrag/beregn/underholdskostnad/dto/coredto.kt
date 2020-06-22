package no.nav.bidrag.beregn.underholdskostnad.dto

import java.time.LocalDate


// Grunnlag periode
data class UnderholdskostnadGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadBarnFodselsdato: LocalDate,
    val barnetilsynMedStonadPeriodeListe: List<BarnetilsynMedStonadPeriodeCore>,
    val barnetilsynFaktiskUtgiftBruttoPeriodeListe: List<BarnetilsynFaktiskUtgiftBruttoPeriodeCore>,
    val forpleiningUtgiftPeriodeListe: List<ForpleiningUtgiftPeriodeCore>,
    val sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class BarnetilsynMedStonadPeriodeCore(
    val barnetilsynMedStonadDatoFra: LocalDate,
    val barnetilsynMedStonadDatoTil: LocalDate,
    val barnetilsynMedStonadType: String,
    val barnetilsynStonadType: String
)

data class BarnetilsynFaktiskUtgiftBruttoPeriodeCore(
    val barnetilsynFaktiskUtgiftBruttoDatoFra: LocalDate,
    val barnetilsynFaktiskUtgiftBruttoDatoTil: LocalDate,
    val barnetilsynFaktiskUtgiftBruttoBelop: Double
)

data class ForpleiningUtgiftPeriodeCore(
    val forpleiningUtgiftDatoFra: LocalDate,
    val forpleiningUtgiftDatoTil: LocalDate,
    val forpleiningUtgiftBelop: Double
)

data class SjablonPeriodeCore(
    val sjablonPeriodeDatoFraTil: PeriodeCore,
    val sjablonNavn: String,
    val sjablonNokkelListe: List<SjablonNokkelCore>? = emptyList(),
    val sjablonInnholdListe: List<SjablonInnholdCore>
)


// Resultat
data class UnderholdskostnadResultatCore(
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
    val soknadBarnFodselsdato: LocalDate,
    val barnetilsynMedStonadTilsynType: String,
    val barnetilsynMedStonadStonadType: String,
    val barnetilsynFaktiskUtgiftBruttoBelop: Double,
    val forpleiningUtgiftBelop: Double,
    val sjablonListe: List<SjablonCore>
)

// Felles
data class PeriodeCore(
    val periodeDatoFra: LocalDate,
    val periodeDatoTil: LocalDate?
)

data class SjablonCore(
    val sjablonNavn: String,
    val sjablonNokkelListe: List<SjablonNokkelCore>? = emptyList(),
    val sjablonInnholdListe: List<SjablonInnholdCore>
)

data class SjablonNokkelCore(
    val sjablonNokkelNavn: String,
    val sjablonNokkelVerdi: String
)

data class SjablonInnholdCore(
    val sjablonInnholdNavn: String,
    val sjablonInnholdVerdi: Double
)

data class AvvikCore(
    val avvikTekst: String,
    val avvikType: String
)
