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
    val faktiskUtgiftPeriodeListe: List<FaktiskUtgiftPeriodeCore>,
    val sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class FaktiskUtgiftPeriodeCore(
    val faktiskUtgiftPeriodeDatoFraTil: PeriodeCore,
    val faktiskUtgiftSoknadsbarnFodselsdato: LocalDate,
    val faktiskUtgiftSoknadsbarnPersonId: Int,
    val faktiskUtgiftBelop: Double
)


// Resultat
data class BeregnNettoBarnetilsynResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: PeriodeCore,
    val resultatBeregning: ResultatBeregningListeCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningListeCore(
    val resultatBeregningListeCore: List<ResultatBeregningCore>
)

data class ResultatBeregningCore(
    val resultatSoknadsbardPersonId: Int,
    val resultatBelop: Double
)

data class ResultatGrunnlagCore(
    val faktiskUtgiftCoreListe: List<FaktiskUtgiftCore>,
    val sjablonListe: List<SjablonCore>
)

data class FaktiskUtgiftCore(
    val soknadBarnFodselsdato: LocalDate,
    val soknadsbardPersonId: Int,
    val faktiskUtgiftBelop: Double
)