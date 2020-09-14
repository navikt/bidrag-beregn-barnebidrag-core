package no.nav.bidrag.beregn.nettobarnetilsyn.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
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
    val resultatGrunnlag: BeregnNettoBarnetilsynGrunnlagPeriodisert
)

data class ResultatBeregning(
    val resultatSoknadsbarnPersonId: Int,
    val resultatBelop: Double
)

// Grunnlag beregning
data class BeregnNettoBarnetilsynGrunnlagPeriodisert(
    val faktiskUtgiftListe: List<FaktiskUtgift>,
    val sjablonListe: List<Sjablon>)

data class FaktiskUtgift(
    val soknadsbarnFodselsdato: LocalDate,
    val soknadsbarnPersonId: Int,
    val faktiskUtgiftBelop: Double)