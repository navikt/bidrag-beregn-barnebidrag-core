package no.nav.bidrag.beregn.nettobarnetilsyn.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnNettoBarnetilsynGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    var faktiskUtgiftPeriodeListe: List<FaktiskUtgiftPeriodeCore>,
    var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class FaktiskUtgiftPeriodeCore(
    val faktiskUtgiftPeriodeDatoFraTil: PeriodeCore,
    val faktiskUtgiftSoknadsbarnFodselsdato: LocalDate,
    val faktiskUtgiftSoknadsbarnPersonId: Int,
    val faktiskUtgiftBelop: BigDecimal
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
    val resultatSoknadsbarnPersonId: Int,
    val resultatBelop: BigDecimal
)

data class ResultatGrunnlagCore(
    val faktiskUtgiftListe: List<FaktiskUtgiftCore>,
    val sjablonListe: List<SjablonCore>
)

data class FaktiskUtgiftCore(
    val faktiskUtgiftSoknadsbarnPersonId: Int,
    val soknadsbarnAlder: Int,
    val faktiskUtgiftBelop: BigDecimal
)