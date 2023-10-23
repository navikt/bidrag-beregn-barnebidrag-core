package no.nav.bidrag.beregn.barnebidrag.bo

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeBarnebidrag
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

// Resultat periode
data class BeregnBarnebidragResultat(
    val resultatPeriodeListe: List<ResultatPeriode>
)

data class ResultatPeriode(
    val periode: Periode,
    val resultatListe: List<ResultatBeregning>,
    val grunnlag: GrunnlagBeregning
)

data class ResultatBeregning(
    val soknadsbarnPersonId: Int,
    val belop: BigDecimal,
    val kode: ResultatKodeBarnebidrag,
    val sjablonListe: List<SjablonPeriodeNavnVerdi>
)

// Grunnlag beregning
data class GrunnlagBeregning(
    val bidragsevne: Bidragsevne,
    val grunnlagPerBarnListe: List<GrunnlagBeregningPerBarn>,
    val barnetilleggForsvaret: BarnetilleggForsvaret,
    val andreLopendeBidragListe: List<AndreLopendeBidrag>,
    val sjablonListe: List<SjablonPeriode>
)

data class Bidragsevne(
    val referanse: String,
    val bidragsevneBelop: BigDecimal,
    val tjuefemProsentInntekt: BigDecimal
)

data class GrunnlagBeregningPerBarn(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnad: BPsAndelUnderholdskostnad,
    val samvaersfradrag: Samvaersfradrag,
    val deltBosted: DeltBosted,
    val barnetilleggBP: Barnetillegg?,
    val barnetilleggBM: Barnetillegg?
)

data class BPsAndelUnderholdskostnad(
    val referanse: String,
    val andelProsent: BigDecimal,
    val andelBelop: BigDecimal,
    val barnetErSelvforsorget: Boolean
)

data class Samvaersfradrag(
    val referanse: String,
    val belop: BigDecimal
)

data class DeltBosted(
    val referanse: String,
    val deltBostedIPeriode: Boolean
)

data class Barnetillegg(
    val referanse: String,
    val belop: BigDecimal,
    val skattProsent: BigDecimal
)

data class BarnetilleggForsvaret(
    val referanse: String,
    val barnetilleggForsvaretIPeriode: Boolean
)

data class AndreLopendeBidrag(
    val referanse: String,
    val barnPersonId: Int,
    val bidragBelop: BigDecimal,
    val samvaersfradragBelop: BigDecimal
)

// Hjelpeklasser
data class BeregnBarnebidragListeGrunnlag(
    val periodeResultatListe: MutableList<ResultatPeriode> = mutableListOf(),
    var justertBidragsevnePeriodeListe: List<BidragsevnePeriode> = listOf(),
    var justertBPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriode> = listOf(),
    var justertDeltBostedPeriodeListe: List<DeltBostedPeriode> = listOf(),
    var justertSamvaersfradragPeriodeListe: List<SamvaersfradragPeriode> = listOf(),
    var justertBarnetilleggBPPeriodeListe: List<BarnetilleggPeriode> = listOf(),
    var justertBarnetilleggBMPeriodeListe: List<BarnetilleggPeriode> = listOf(),
    var justertBarnetilleggForsvaretPeriodeListe: List<BarnetilleggForsvaretPeriode> = listOf(),
    var justertAndreLopendeBidragPeriodeListe: List<AndreLopendeBidragPeriode> = listOf(),
    var justertSjablonPeriodeListe: List<SjablonPeriode> = listOf(),
    var bruddPeriodeListe: MutableList<Periode> = mutableListOf()
)
