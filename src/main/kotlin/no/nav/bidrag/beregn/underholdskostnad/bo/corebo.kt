package no.nav.bidrag.beregn.underholdskostnad.bo

import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnUnderholdskostnadGrunnlag(
    val soknadsbarnPersonId: Int,
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val soknadsbarnFodselsdato: LocalDate,
    val barnetilsynMedStonadPeriodeListe: List<BarnetilsynMedStonadPeriode>,
    val nettoBarnetilsynPeriodeListe: List<NettoBarnetilsynPeriode>,
    val forpleiningUtgiftPeriodeListe: List<ForpleiningUtgiftPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

data class BarnetilsynMedStonad(
    val barnetilsynMedStonadTilsynType: String,
    val barnetilsynMedStonadStonadType: String
)

// Resultatperiode
data class BeregnUnderholdskostnadResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val soknadsbarnPersonId: Int,
    val resultatDatoFraTil: Periode,
    val resultatBeregning: ResultatBeregning,
    val resultatGrunnlag: BeregnUnderholdskostnadGrunnlagPeriodisert
)

data class ResultatBeregning(
    val resultatBelopUnderholdskostnad: BigDecimal
)

// Grunnlag beregning
data class BeregnUnderholdskostnadGrunnlagPeriodisert(
    val soknadBarnAlder: Int,
    val barnetilsynMedStonad: BarnetilsynMedStonad?,
    val nettoBarnetilsynBelop: BigDecimal,
    val forpleiningUtgiftBelop: BigDecimal,
    val sjablonListe: List<Sjablon>)