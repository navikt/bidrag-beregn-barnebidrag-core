package no.nav.bidrag.beregn.barnebidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.enums.ResultatKode
import java.math.BigDecimal
import java.time.LocalDate

// Grunnlag periode
data class BeregnBarnebidragGrunnlag(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val bidragsevnePeriodeListe: List<BidragsevnePeriode>,
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriode>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriode>,
    val deltBostedPeriodeListe: List<DeltBostedPeriode>,
    val barnetilleggBPPeriodeListe: List<BarnetilleggPeriode>,
    val barnetilleggBMPeriodeListe: List<BarnetilleggPeriode>,
    val barnetilleggForsvaretPeriodeListe: List<BarnetilleggForsvaretPeriode>,
    val andreLopendeBidragPeriodeListe: List<AndreLopendeBidragPeriode>,
    val sjablonPeriodeListe: List<SjablonPeriode>
)

// Resultatperiode
data class BeregnBarnebidragResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val resultatDatoFraTil: Periode,
    val resultatBeregningListe: List<ResultatBeregning>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisert
)

data class ResultatBeregning(
    val soknadsbarnPersonId: Int,
    val resultatBarnebidragBelop: BigDecimal,
    val resultatkode: ResultatKode,
    val sjablonListe: List<SjablonNavnVerdi>
)


// Grunnlag beregning
data class GrunnlagBeregningPeriodisert(
    val bidragsevne: Bidragsevne,
    val grunnlagPerBarnListe: List<GrunnlagBeregningPerBarn>,
    val barnetilleggForsvaret: Boolean,
    val andreLopendeBidragListe: List<AndreLopendeBidrag>,
    val sjablonListe: List<Sjablon>
)

data class Bidragsevne(
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class GrunnlagBeregningPerBarn(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnad: BPsAndelUnderholdskostnad,
    val samvaersfradrag: BigDecimal,
    val deltBosted: Boolean,
    val barnetilleggBP: Barnetillegg,
    val barnetilleggBM: Barnetillegg
)

data class BPsAndelUnderholdskostnad(
    val bPsAndelUnderholdskostnadProsent: BigDecimal,
    val bPsAndelUnderholdskostnadBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)

data class Barnetillegg(
    val barnetilleggBelop: BigDecimal,
    val barnetilleggSkattProsent: BigDecimal
)

data class AndreLopendeBidrag(
    val barnPersonId: Int,
    val lopendeBidragBelop: BigDecimal,
    val beregnetSamvaersfradragBelop: BigDecimal
)