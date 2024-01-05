package no.nav.bidrag.beregn.samvaersfradrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnSamvaersfradragGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarn: Soknadsbarn,
    val samvaersklassePeriodeListe: List<SamvaersklassePeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>,
)

data class Soknadsbarn(
    val referanse: String,
    val personId: Int,
    val fodselsdato: LocalDate,
)

// Resultat periode
data class BeregnetSamvaersfradragResultat(
    val resultatPeriodeListe: List<ResultatPeriode>,
)

data class ResultatPeriode(
    val soknadsbarnPersonId: Int,
    val periode: Periode,
    val resultat: ResultatBeregning,
    val grunnlag: GrunnlagBeregning,
)

data class ResultatBeregning(
    val belop: BigDecimal,
    val sjablonListe: List<SjablonPeriodeNavnVerdi>,
)

// Grunnlag beregning
data class GrunnlagBeregning(
    val soknadsbarn: SoknadsbarnAlder,
    val samvaersklasse: Samvaersklasse,
    val sjablonListe: List<SjablonPeriode>,
)

data class SoknadsbarnAlder(
    val referanse: String,
    val alder: Int,
)

data class Samvaersklasse(
    val referanse: String,
    val samvaersklasse: String,
)
