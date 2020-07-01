package no.nav.bidrag.beregn.nettobarnetilsyn.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.time.LocalDate


// Grunnlag periode
data class BeregnNettoBarnetilsynGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val faktiskUtgiftBarnetilsynPeriodeListe: List<FaktiskUtgiftBarnetilsynPeriodeCore>,
    val sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class FaktiskUtgiftBarnetilsynPeriodeCore(
    val faktiskUtgiftBarnetilsynPeriodeDatoFraTil: PeriodeCore,
    val faktiskUtgiftBarnetilsynSoknadsbarnFodselsdato: LocalDate,
    val faktiskUtgiftBarnetilsynSoknadsbarnPersonId: Int,
    val faktiskUtgiftBarnetilsynBelop: Double
)


// Resultat
data class BeregnNettoBarnetilsynResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregningListe: List<ResultatBeregningCore>,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatPersonIdSoknadsbard: Int,
    val resultatNettoBarnetilsynBelop: Double
)

data class ResultatGrunnlagCore(
    val grunnlagCoreListe: List<GrunnlagCore>,
    val sjablonListe: List<SjablonCore>
)

data class GrunnlagCore(
    val soknadBarnFodselsdato: LocalDate,
    val faktiskUtgiftBelop: Double
)