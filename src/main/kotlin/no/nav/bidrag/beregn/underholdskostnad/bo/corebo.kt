package no.nav.bidrag.beregn.underholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnUnderholdskostnadGrunnlag(
  val beregnDatoFra: LocalDate,
  val beregnDatoTil: LocalDate,
  val soknadsbarn: Soknadsbarn,
  val barnetilsynMedStonadPeriodeListe: List<BarnetilsynMedStonadPeriode>,
  val nettoBarnetilsynPeriodeListe: List<NettoBarnetilsynPeriode>,
  val forpleiningUtgiftPeriodeListe: List<ForpleiningUtgiftPeriode>,
  val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultat periode
data class BeregnetUnderholdskostnadResultat(
  val beregnetUnderholdskostnadPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
  val soknadsbarnPersonId: Int,
  val periode: Periode,
  val resultat: ResultatBeregning,
  val grunnlag: GrunnlagBeregning
)

data class ResultatBeregning(
  val belop: BigDecimal,
  val sjablonListe: List<SjablonPeriodeNavnVerdi>
)

// Grunnlag beregning
data class GrunnlagBeregning(
  val soknadsbarn: SoknadsbarnAlder,
  val barnetilsynMedStonad: BarnetilsynMedStonad,
  val nettoBarnetilsyn: NettoBarnetilsyn,
  val forpleiningUtgift: ForpleiningUtgift,
  val sjablonListe: List<SjablonPeriode>
)

data class Soknadsbarn(
  val referanse: String,
  val personId: Int,
  val fodselsdato: LocalDate
)

data class SoknadsbarnAlder(
  val referanse: String,
  val alder: Int
)

data class BarnetilsynMedStonad(
  val referanse: String,
  val tilsynType: String,
  val stonadType: String
)

data class NettoBarnetilsyn(
  val referanse: String,
  val belop: BigDecimal
)

data class ForpleiningUtgift(
  val referanse: String,
  val belop: BigDecimal
)
