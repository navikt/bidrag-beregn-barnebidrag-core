package no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnKostnadsberegnetBidragGrunnlag(
  val beregnDatoFra: LocalDate,
  val beregnDatoTil: LocalDate,
  val soknadsbarnPersonId: Int,
  val underholdskostnadPeriodeListe: List<UnderholdskostnadPeriode>,
  val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriode>,
  val samvaersfradragPeriodeListe: List<SamvaersfradragPeriode>?
)

// Resultat periode
data class BeregnetKostnadsberegnetBidragResultat(
  val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
  val soknadsbarnPersonId: Int,
  val periode: Periode,
  val resultat: ResultatBeregning,
  val grunnlag: GrunnlagBeregning
)

data class ResultatBeregning(
  val belop: BigDecimal
)

// Grunnlag beregning
data class GrunnlagBeregning(
  val underholdskostnad: Underholdskostnad,
  val bPsAndelUnderholdskostnad: BPsAndelUnderholdskostnad,
  val samvaersfradrag: Samvaersfradrag
)

data class Underholdskostnad(
  val referanse: String,
  val belop: BigDecimal
)

data class BPsAndelUnderholdskostnad(
  val referanse: String,
  val andelProsent: BigDecimal
)

data class Samvaersfradrag(
  val referanse: String,
  val belop: BigDecimal
)


// Hjelpeklasser
data class BeregnKostnadsberegnetBidragListeGrunnlag(
  val periodeResultatListe: MutableList<ResultatPeriode> = mutableListOf(),
  var justertUnderholdskostnadPeriodeListe: List<UnderholdskostnadPeriode> = listOf(),
  var justertBPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriode> = listOf(),
  var justertSamvaersfradragPeriodeListe: List<SamvaersfradragPeriode> = listOf(),
  var bruddPeriodeListe: MutableList<Periode> = mutableListOf()
)
