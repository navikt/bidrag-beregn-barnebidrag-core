package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import java.time.LocalDate

// Grunnlag periode
data class BeregnNettoBarnetilsynGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val faktiskUtgiftPeriodeListe: List<FaktiskUtgiftPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnNettoBarnetilsynResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregningListe,
    val resultatGrunnlag: BeregnNettoBarnetilsynGrunnlagPeriodisert
)

data class ResultatBeregningListe(
    val resultatBeregningListe: List<ResultatBeregning>
)

data class ResultatBeregning(
    val resultatPersonIdSoknadsbard: Int,
    val resultatBelop: Double
)

// Grunnlag beregning
data class BeregnNettoBarnetilsynGrunnlagPeriodisert(
    val faktiskUtgiftBelopListe: List<FaktiskUtgift>,
    val sjablonListe: List<Sjablon>)

data class FaktiskUtgift(
    val soknadsbarnFodselsdato: LocalDate,
    val soknadsbarnPersonId: Int,
    val faktiskUtgiftBelop: Double)