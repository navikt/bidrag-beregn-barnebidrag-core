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
    val nettoBarnetilsynPeriodeListe: List<NettoBarnetilsynPeriodeCore>,
    val sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class NettoBarnetilsynPeriodeCore(
    val nettoBarnetilsynPeriodeDatoFraTil: PeriodeCore,
    val nettoBarnetilsynSoknadsbarnFodselsdato: LocalDate,
    val nettoBarnetilsynBelop: Double
)


// Resultat
data class BeregnNettoBarnetilsynResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatNettoBarnetilsynBelop: Double
)

data class ResultatGrunnlagCore(
    val soknadBarnAlder: Int,
    val barnetilsynFaktiskUtgiftBruttoBelop: Double,
    val sjablonListe: List<SjablonCore>
)