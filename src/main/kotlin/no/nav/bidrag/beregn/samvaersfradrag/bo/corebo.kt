package no.nav.bidrag.beregn.samvaersfradrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import java.time.LocalDate

// Grunnlag periode
data class BeregnSamvaersfradragGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnPersonId: Int,
    val soknadsbarnFodselsdato: LocalDate,
    val samvaersklassePeriodeListe: List<SamvaersklassePeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnSamvaersfradragResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val soknadsbarnPersonId: Int,
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlag: BeregnSamvaersfradragGrunnlagPeriodisert
)

data class ResultatBeregning(
    val resultatSamvaersfradragBelop: Double
)

// Grunnlag beregning
data class BeregnSamvaersfradragGrunnlagPeriodisert(
    val soknadBarnAlder: Int,
    val samvaersklasse: String,
    val sjablonListe: List<Sjablon>)