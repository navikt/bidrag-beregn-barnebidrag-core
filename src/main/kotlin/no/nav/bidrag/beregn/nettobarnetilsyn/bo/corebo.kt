package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode
import java.time.LocalDate

// Grunnlag periode
data class BeregnNettoBarnetilsynGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val faktiskUtgiftBarnetilsynPeriodeListe: List<FaktiskUtgiftBarnetilsynPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnNettoBarnetilsynResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlag: BeregnNettoBarnetilsynGrunnlagPeriodisert
)

data class ResultatBeregning(
    val resultatNettoBarnetilsynBelop: Double
)

// Grunnlag beregning
data class BeregnNettoBarnetilsynGrunnlagPeriodisert(
    val faktiskUtgiftBarnetilsynBelopListe: List<FaktiskUtgiftBarnetilsyn>,
    val sjablonListe: List<Sjablon>)

data class FaktiskUtgiftBarnetilsyn(
    val soknadBarnAlder: Int,
    val faktiskUtgiftBarnetilsynBelop: Double)