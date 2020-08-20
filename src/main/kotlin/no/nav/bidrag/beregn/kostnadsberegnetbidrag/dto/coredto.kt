package no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlagPeriodisert
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatBeregning
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersklassePeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode
import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatGrunnlagCore
import java.time.LocalDate

// Grunnlag periode
data class BeregnKostnadsberegnetBidragGrunnlagCore(
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
data class BeregnKostnadsberegnetBidragResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregningCore,
    val resultatGrunnlag: ResultatGrunnlagCore
)

data class ResultatBeregningCore(
    val resultatKostnadsberegnetBidragBelop: Double
)

// Grunnlag beregning
data class ResultatGrunnlagCore(
    val soknadBarnAlder: Int,
    val underholdskostnadBelop: Double,
    val bPsAndelUnderholdskostnadProsent: Double,
    val samvaersklasse: String,
    val sjablonListe: List<Sjablon>)