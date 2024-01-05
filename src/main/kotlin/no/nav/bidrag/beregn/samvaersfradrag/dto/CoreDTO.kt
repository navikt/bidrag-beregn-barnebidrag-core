package no.nav.bidrag.beregn.samvaersfradrag.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag
data class BeregnSamvaersfradragGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarn: SoknadsbarnCore,
    val samvaersklassePeriodeListe: List<SamvaersklassePeriodeCore>,
    var sjablonPeriodeListe: List<SjablonPeriodeCore>,
)

data class SoknadsbarnCore(
    val referanse: String,
    val personId: Int,
    val fodselsdato: LocalDate,
)

data class SamvaersklassePeriodeCore(
    val referanse: String,
    val periode: PeriodeCore,
    val samvaersklasse: String,
)

// Resultat
data class BeregnetSamvaersfradragResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val sjablonListe: List<SjablonResultatGrunnlagCore>,
    val avvikListe: List<AvvikCore>,
)

data class ResultatPeriodeCore(
    val soknadsbarnPersonId: Int,
    val periode: PeriodeCore,
    val resultat: ResultatBeregningCore,
    val grunnlagReferanseListe: List<String>,
)

data class ResultatBeregningCore(
    val belop: BigDecimal,
)
