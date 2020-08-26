package no.nav.bidrag.beregn.barnebidrag.dto

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatGrunnlagCore
import java.time.LocalDate

// Grunnlag periode
data class BeregnBarnebidragGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnFodselsdato: LocalDate,
    val underholdskostnadPeriodeListe: List<UnderholdskostnadPeriodeCore>,
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriodeCore>,
    val samvaersklassePeriodeListe: List<SamvaersklassePeriodeCore>,
    val sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class UnderholdskostnadPeriodeCore(
    val underholdskostnadDatoFraTil: PeriodeCore,
    val underholdskostnadBelop: Double
)

data class BPsAndelUnderholdskostnadPeriodeCore(
    val bPsAndelUnderholdskostnadDatoFraTil: PeriodeCore,
    val bPsAndelUnderholdskostnadProsent: Double
)

data class SamvaersklassePeriodeCore(
    val samvaersklasseDatoFraTil: PeriodeCore,
    val samvaersklasse: String
)

// Resultatperiode
data class BeregnBarnebidragResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatBarnebidragBelop: Double
)

// Grunnlag beregning
data class ResultatGrunnlagCore(
    val soknadBarnAlder: Int,
    val underholdskostnadBelop: Double,
    val bPsAndelUnderholdskostnadProsent: Double,
    val samvaersklasse: String,
    val sjablonListe: List<Sjablon>)