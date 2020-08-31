package no.nav.bidrag.beregn.bidragsevne.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.enums.BostatusKode
import no.nav.bidrag.beregn.felles.enums.InntektType
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import java.time.LocalDate


// Grunnlag periode
data class BeregnBidragsevneGrunnlagAlt(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val inntektPeriodeListe: List<InntektPeriode>,
    val skatteklassePeriodeListe: List<SkatteklassePeriode>,
    val bostatusPeriodeListe: List<BostatusPeriode>,
    val antallBarnIEgetHusholdPeriodeListe: List<AntallBarnIEgetHusholdPeriode>,
    val saerfradragPeriodeListe: List<SaerfradragPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnBidragsevneResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlag: BeregnBidragsevneGrunnlagPeriodisert
)


// Grunnlag beregning
data class BeregnBidragsevneGrunnlagPeriodisert(
    val inntektListe: List<Inntekt>,
    val skatteklasse: Int,
    val bostatusKode: BostatusKode,
    val antallEgneBarnIHusstand: Int,
    val saerfradragkode: SaerfradragKode,
    val sjablonListe: List<Sjablon>)


data class Inntekt(
    val inntektType: InntektType,
    val inntektBelop: Double
)

data class ResultatBeregning(
    val resultatEvneBelop: Double,
    val resultat25ProsentInntekt: Double
)
