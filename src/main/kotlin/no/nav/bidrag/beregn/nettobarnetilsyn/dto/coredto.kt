package no.nav.bidrag.beregn.nettobarnetilsyn.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag
data class BeregnNettoBarnetilsynGrunnlagCore(
  val beregnDatoFra: LocalDate,
  val beregnDatoTil: LocalDate,
  var faktiskUtgiftPeriodeListe: List<FaktiskUtgiftPeriodeCore>,
  var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class FaktiskUtgiftPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val soknadsbarnFodselsdato: LocalDate,
  val soknadsbarnPersonId: Int,
  val belop: BigDecimal
)

// Resultat
data class BeregnetNettoBarnetilsynResultatCore(
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
