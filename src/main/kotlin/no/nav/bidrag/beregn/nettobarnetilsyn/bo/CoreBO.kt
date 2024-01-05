package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnNettoBarnetilsynGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    var faktiskUtgiftPeriodeListe: List<FaktiskUtgiftPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>,
)

// Resultat periode
data class BeregnetNettoBarnetilsynResultat(
    val resultatPeriodeListe: List<ResultatPeriode>,
)

data class ResultatPeriode(
    val periode: Periode,
    val resultatListe: List<ResultatBeregning>,
    val grunnlag: GrunnlagBeregning,
)

data class ResultatBeregning(
    val soknadsbarnPersonId: Int,
    val belop: BigDecimal,
    val sjablonListe: List<SjablonPeriodeNavnVerdi>,
)

// Grunnlag beregning
data class GrunnlagBeregning(
    val faktiskUtgiftListe: List<FaktiskUtgift>,
    val sjablonListe: List<SjablonPeriode>,
)

data class FaktiskUtgift(
    val soknadsbarnPersonId: Int,
    val referanse: String,
    val soknadsbarnAlder: Int,
    val belop: BigDecimal,
)
