package no.nav.bidrag.beregn.underholdskostnad.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag
data class BeregnUnderholdskostnadGrunnlagCore(
  val beregnDatoFra: LocalDate,
  val beregnDatoTil: LocalDate,
  val soknadsbarn: SoknadsbarnCore,
  val barnetilsynMedStonadPeriodeListe: List<BarnetilsynMedStonadPeriodeCore>,
  var nettoBarnetilsynPeriodeListe: List<NettoBarnetilsynPeriodeCore>,
  val forpleiningUtgiftPeriodeListe: List<ForpleiningUtgiftPeriodeCore>,
  var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class SoknadsbarnCore(
  val referanse: String,
  val personId: Int,
  val fodselsdato: LocalDate
)

data class BarnetilsynMedStonadPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val tilsynType: String,
  val stonadType: String
)

data class NettoBarnetilsynPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val belop: BigDecimal
)

data class ForpleiningUtgiftPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val belop: BigDecimal
)

// Resultat
data class BeregnetUnderholdskostnadResultatCore(
  val resultatPeriodeListe: List<ResultatPeriodeCore>,
  val sjablonListe: List<SjablonResultatGrunnlagCore>,
  val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
  val soknadsbarnPersonId: Int,
  val periode: PeriodeCore,
  val resultat: ResultatBeregningCore,
  val grunnlagReferanseListe: List<String>
)

data class ResultatBeregningCore(
  val belop: BigDecimal
)
