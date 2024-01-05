package no.nav.bidrag.beregn.bidragsevne.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.domene.enums.beregning.Særfradragskode
import no.nav.bidrag.domene.enums.person.Bostatuskode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnBidragsevneGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val inntektPeriodeListe: List<InntektPeriode>,
    val skatteklassePeriodeListe: List<SkatteklassePeriode>,
    val bostatusPeriodeListe: List<BostatusPeriode>,
    val barnIHusstandPeriodeListe: List<BarnIHusstandPeriode>,
    val saerfradragPeriodeListe: List<SaerfradragPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>,
)

// Resultat periode
data class BeregnetBidragsevneResultat(
    val resultatPeriodeListe: List<ResultatPeriode>,
)

data class ResultatPeriode(
    val periode: Periode,
    val resultat: ResultatBeregning,
    val grunnlag: GrunnlagBeregning,
)

data class ResultatBeregning(
    val belop: BigDecimal,
    val inntekt25Prosent: BigDecimal,
    val sjablonListe: List<SjablonPeriodeNavnVerdi>,
)

// Grunnlag beregning
data class GrunnlagBeregning(
    val inntektListe: List<Inntekt>,
    val skatteklasse: Skatteklasse,
    val bostatus: Bostatus,
    val barnIHusstand: BarnIHusstand,
    val saerfradrag: Saerfradrag,
    val sjablonListe: List<SjablonPeriode>,
)

data class Inntekt(
    val referanse: String,
    val type: String,
    val belop: BigDecimal,
)

data class Skatteklasse(
    val referanse: String,
    val skatteklasse: Int,
)

data class Bostatus(
    val referanse: String,
    val kode: Bostatuskode,
)

data class BarnIHusstand(
    val referanse: String,
    val antallBarn: Double,
)

data class Saerfradrag(
    val referanse: String,
    val kode: Særfradragskode,
)

// Hjelpeklasser
data class BeregnBidragsevneListeGrunnlag(
    val periodeResultatListe: MutableList<ResultatPeriode> = mutableListOf(),
    var justertInntektPeriodeListe: List<InntektPeriode> = listOf(),
    var justertSkatteklassePeriodeListe: List<SkatteklassePeriode> = listOf(),
    var justertBostatusPeriodeListe: List<BostatusPeriode> = listOf(),
    var justertBarnIHusstandPeriodeListe: List<BarnIHusstandPeriode> = listOf(),
    var justertSaerfradragPeriodeListe: List<SaerfradragPeriode> = listOf(),
    var justertSjablonPeriodeListe: List<SjablonPeriode> = listOf(),
    var bruddPeriodeListe: MutableList<Periode> = mutableListOf(),
)
