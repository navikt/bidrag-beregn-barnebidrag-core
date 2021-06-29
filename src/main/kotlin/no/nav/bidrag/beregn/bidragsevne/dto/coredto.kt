package no.nav.bidrag.beregn.bidragsevne.dto

import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag
data class BeregnBidragsevneGrunnlagCore(
  val beregnDatoFra: LocalDate,
  val beregnDatoTil: LocalDate,
  val inntektPeriodeListe: List<InntektPeriodeCore>,
  val skatteklassePeriodeListe: List<SkatteklassePeriodeCore>,
  val bostatusPeriodeListe: List<BostatusPeriodeCore>,
  val barnIHusstandPeriodeListe: List<BarnIHusstandPeriodeCore>,
  val saerfradragPeriodeListe: List<SaerfradragPeriodeCore>,
  var sjablonPeriodeListe: List<SjablonPeriodeCore>
)

data class InntektPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val type: String,
  val belop: BigDecimal
)

data class SkatteklassePeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val skatteklasse: Int
)

data class BostatusPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val kode: String
)

data class BarnIHusstandPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val antallBarn: Double
)

data class SaerfradragPeriodeCore(
  val referanse: String,
  val periode: PeriodeCore,
  val kode: String
)

// Resultat
data class BeregnetBidragsevneResultatCore(
  val resultatPeriodeListe: List<ResultatPeriodeCore>,
  val sjablonListe: List<SjablonResultatGrunnlagCore>,
  val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
  val periode: PeriodeCore,
  val resultat: ResultatBeregningCore,
  val grunnlagReferanseListe: List<String>
)

data class ResultatBeregningCore(
  val belop: BigDecimal,
  val inntekt25Prosent: BigDecimal
)
