package no.nav.bidrag.beregn.barnebidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import java.time.LocalDate

// Grunnlag periode
data class BeregnBarnebidragGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnFodselsdato: LocalDate,
    val underholdskostnadPeriodeListe: List<UnderholdskostnadPeriode>,
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriode>,
//    val samvaersklassePeriodeListe: List<SamvaersklassePeriode>?,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnBarnebidragResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlag: BeregnBarnebidragGrunnlagPeriodisert
)

data class ResultatBeregning(
    val resultatBarnebidragBelop: Double
)

// Grunnlag beregning
data class BeregnBarnebidragGrunnlagPeriodisert(
    val soknadBarnAlder: Int,
    val underholdskostnadBelop: Double,
    val bPsAndelUnderholdskostnadProsent: Double,
//    val samvaersklasse: String?,
    val sjablonListe: List<Sjablon>)