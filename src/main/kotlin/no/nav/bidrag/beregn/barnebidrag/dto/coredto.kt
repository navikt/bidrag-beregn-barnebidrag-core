package no.nav.bidrag.beregn.barnebidrag.dto

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.PeriodisertGrunnlag
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.felles.enums.ResultatKode
//import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatGrunnlagCore
import java.time.LocalDate

// Grunnlag periode
data class BeregnBarnebidragGrunnlagCore(
    val beregnDatoFra: LocalDate,
    val beregnDatoTil: LocalDate,
    val bidragsevnePeriodeListe: List<BidragsevnePeriodeCore>,
    val barnetilleggBPPeriodeListe: List<BarnetilleggBPPeriodeCore>,
    val barnetilleggBMPeriodeListe: List<BarnetilleggBMPeriodeCore>,
    val barnetilleggForsvaretPeriodeListe: List<BarnetilleggForsvaretPeriodeCore>,
    val grunnlagPerBarnPeriodeListe: List<GrunnlagPerBarnCore>,
    val sjablonPeriodeListe: List<SjablonPeriodeCore>
)

// Del av grunnlaget som angis per barn i søknaden
data class GrunnlagPerBarnCore(
    val bPsAndelUnderholdskostnadPeriodeListe: List<BPsAndelUnderholdskostnadPeriodeCore>,
    val kostnadsberegnetBidragPeriodeListe: List<KostnadsberegnetBidragPeriodeCore>,
    val samvaersfradragPeriodeListe: List<SamvaersfradragPeriodeCore>,
    val deltBostedPeriodeListe: List<DeltBostedPeriodeCore>
)

data class BidragsevnePeriodeCore(
    val bidragsevneDatoFraTil: Periode,
    val bidragsevneBelop: Double,
    val tjuefemProsentInntekt: Double
)

data class BarnetilleggBPPeriodeCore(
    val barnetilleggBPDatoFraTil: Periode,
    val barnetilleggBPBelop: Double,
    val barnetilleggBPSkattProsent: Double
)

data class BarnetilleggBMPeriodeCore(
    val barnetilleggBMDatoFraTil: Periode,
    val barnetilleggBMBelop: Double,
    val barnetilleggBMSkattProsent: Double
)

data class BarnetilleggForsvaretPeriodeCore(
    val barnetilleggForsvaretDatoFraTil: Periode,
    val barnetilleggForsvaretAntallBarn: Int,
    val barnetilleggForsvaretIPeriode: Boolean
)

data class BPsAndelUnderholdskostnadPeriodeCore(
    val kostnadsberegnetBidragDatoFraTil: Periode,
    val soknadsbarnPersonId: Int,
    val kostnadsberegnetBidragBelop: Double
)

data class KostnadsberegnetBidragPeriodeCore(
    val kostnadsberegnetBidragDatoFraTil: Periode,
    val soknadsbarnPersonId: Int,
    val kostnadsberegnetBidragBelop: Double
)

data class SamvaersfradragPeriodeCore(
    val samvaersfradragDatoFraTil: Periode,
    val soknadsbarnPersonId: Int,
    val samvaersfradrag: Double?
)

data class DeltBostedPeriodeCore(
    val deltBostedDatoFraTil: Periode,
    val soknadsbarnPersonId: Int,
    val deltBostedPeriodeCore: Boolean
)



// Resultatperiode
data class BeregnBarnebidragResultatCore(
    val resultatPeriodeListe: List<ResultatPeriodeCore>,
    val avvikListe: List<AvvikCore>
)

data class ResultatPeriodeCore(
    val resultatDatoFraTil: Periode,
    val resultatBeregning: List<ResultatBeregningCore>,
    val resultatGrunnlag: GrunnlagBeregningPeriodisertCore
)

data class ResultatBeregningCore(
    val soknadsbarnPersonId: Int,
    val resultatBarnebidragBelop: Double,
    val resultatkode: ResultatKode
)

// Grunnlag beregning
data class GrunnlagBeregningPeriodisertCore(
    val bidragsevneBelop: Double,
    val barnetilleggBP: BarnetilleggBPCore,
    val barnetilleggBM: BarnetilleggBMCore,
    val barnetilleggForsvaret: BarnetilleggForsvaretCore,
    val bPsAndelUnderholdskostnad: List<BPsAndelUnderholdskostnadCore>,
    val kostnadsberegnetBidrag: List<KostnadsberegnetBidragCore>,
    val samvaersfradrag: List<SamvaersfradragCore>,
    val deltBosted: List<DeltBostedCore>,
    val sjablonListe: List<Sjablon>
)

data class BPsAndelUnderholdskostnadCore(
    val soknadsbarnPersonId: Int,
    val bPsAndelUnderholdskostnadProsent: Double,
    val bPsAndelUnderholdskostnadBelop: Double
)

data class KostnadsberegnetBidragCore(
    val soknadsbarnPersonId: Int,
    val kostnadsberegnetBidragBelop: Double
)

data class SamvaersfradragCore(
    val soknadsbarnPersonId: Int,
    val samvaersfradragBelop: Double
)

data class DeltBostedCore(
    val soknadsbarnPersonId: Int,
    val deltBosted: Boolean
)

data class BarnetilleggBPCore(
    val barnetilleggBPBelop: Double,
    val barnetilleggBPSkattProsent: Double
)

data class BarnetilleggBMCore(
    val barnetilleggBMBelop: Double,
    val barnetilleggBMSkattProsent: Double
)

data class BarnetilleggForsvaretCore(
    val barnetilleggForsvaretJaNei: Boolean,
    val antallBarn: Int
)