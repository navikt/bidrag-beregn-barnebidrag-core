package no.nav.bidrag.beregn.samvaersfradrag.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import java.time.LocalDate

// Grunnlag periode
data class BeregnSamvaersfradragGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val soknadsbarnFodselsdato: LocalDate,
    val samvaersklassePeriodeListe: List<SamvaersklassePeriodeCore>,
    var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class SamvaersklassePeriodeCore(
    val samvaersklassePeriodeDatoFraTil: PeriodeCore,
    val samvaersklasse: String
)

// Resultatperiode
data class BeregnSamvaersfradragResultatCore(
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
    val resultatSamvaersfradragBelop: Double
)

// Grunnlag beregning
data class ResultatGrunnlagCore(
    val soknadBarnAlder: Int,
    val samvaersklasse: String,
    val sjablonListe: List<SjablonCore>)