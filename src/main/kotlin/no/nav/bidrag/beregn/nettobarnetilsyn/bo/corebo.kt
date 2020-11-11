package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnNettoBarnetilsynGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    var faktiskUtgiftPeriodeListe: List<FaktiskUtgiftPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnNettoBarnetilsynResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregningListe: List<ResultatBeregning>,
    val resultatGrunnlagBeregning: GrunnlagBeregningPeriodisert
)

data class ResultatBeregning(
    val resultatSoknadsbarnPersonId: Int,
    val resultatBelop: BigDecimal,
    val sjablonListe: List<SjablonNavnVerdi>
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val faktiskUtgiftListe: List<FaktiskUtgift>,
    val sjablonListe: List<Sjablon>
)

data class FaktiskUtgift(
    val faktiskUtgiftSoknadsbarnPersonId: Int,
    val soknadsbarnAlder: Int,
    val faktiskUtgiftBelop: BigDecimal
)