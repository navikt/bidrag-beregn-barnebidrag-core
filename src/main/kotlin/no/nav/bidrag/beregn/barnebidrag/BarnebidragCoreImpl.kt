package no.nav.bidrag.beregn.barnebidrag

import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidragPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnadPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaretPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat
import no.nav.bidrag.beregn.barnebidrag.bo.BidragsevnePeriode
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBostedPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.SamvaersfradragPeriode
import no.nav.bidrag.beregn.barnebidrag.dto.AndreLopendeBidragPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.BPsAndelUnderholdskostnadPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggForsvaretPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnBarnebidragGrunnlagCore
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnetBarnebidragResultatCore
import no.nav.bidrag.beregn.barnebidrag.dto.BidragsevnePeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.DeltBostedPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.barnebidrag.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.dto.SamvaersfradragPeriodeCore
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode
import no.nav.bidrag.beregn.felles.FellesCore
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore

class BarnebidragCoreImpl(private val barnebidragPeriode: BarnebidragPeriode) : FellesCore(), BarnebidragCore {
    override fun beregnBarnebidrag(grunnlag: BeregnBarnebidragGrunnlagCore): BeregnetBarnebidragResultatCore {
        val beregnBarnebidragGrunnlag = mapTilBusinessObject(grunnlag)
        val avvikListe = barnebidragPeriode.validerInput(beregnBarnebidragGrunnlag)
        val beregnBarnebidragResultat =
            if (avvikListe.isEmpty()) {
                barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag)
            } else {
                BeregnBarnebidragResultat(emptyList())
            }
        return mapFraBusinessObject(avvikListe = avvikListe, resultat = beregnBarnebidragResultat)
    }

    private fun mapTilBusinessObject(grunnlag: BeregnBarnebidragGrunnlagCore) = BeregnBarnebidragGrunnlag(
        beregnDatoFra = grunnlag.beregnDatoFra,
        beregnDatoTil = grunnlag.beregnDatoTil,
        bidragsevnePeriodeListe = mapBidragsevnePeriodeListe(grunnlag.bidragsevnePeriodeListe),
        bPsAndelUnderholdskostnadPeriodeListe =
        mapBPsAndelUnderholdskostnadPeriodeListe(
            grunnlag.bPsAndelUnderholdskostnadPeriodeListe,
        ),
        samvaersfradragPeriodeListe = mapSamvaersfradragPeriodeListe(grunnlag.samvaersfradragPeriodeListe),
        deltBostedPeriodeListe = mapDeltBostedPeriodeListe(grunnlag.deltBostedPeriodeListe),
        barnetilleggBPPeriodeListe = mapBarnetilleggPeriodeListe(grunnlag.barnetilleggBPPeriodeListe),
        barnetilleggBMPeriodeListe = mapBarnetilleggPeriodeListe(grunnlag.barnetilleggBMPeriodeListe),
        barnetilleggForsvaretPeriodeListe = mapBarnetilleggForsvaretPeriodeListe(grunnlag.barnetilleggForsvaretPeriodeListe),
        andreLopendeBidragPeriodeListe = mapAndreLopendeBidragPeriodeListe(grunnlag.andreLopendeBidragPeriodeListe),
        sjablonPeriodeListe = mapSjablonPeriodeListe(grunnlag.sjablonPeriodeListe),
    )

    private fun mapFraBusinessObject(avvikListe: List<Avvik>, resultat: BeregnBarnebidragResultat) = BeregnetBarnebidragResultatCore(
        resultatPeriodeListe = mapResultatPeriode(resultat.resultatPeriodeListe),
        sjablonListe = mapSjablonGrunnlagListe(resultat.resultatPeriodeListe),
        avvikListe = mapAvvik(avvikListe),
    )

    private fun mapBidragsevnePeriodeListe(bidragsevnePeriodeListeCore: List<BidragsevnePeriodeCore>): List<BidragsevnePeriode> {
        val bidragsevnePeriodeListe = mutableListOf<BidragsevnePeriode>()
        bidragsevnePeriodeListeCore.forEach {
            bidragsevnePeriodeListe.add(
                BidragsevnePeriode(
                    referanse = it.referanse,
                    bidragsevnePeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    belop = it.belop,
                    tjuefemProsentInntekt = it.tjuefemProsentInntekt,
                ),
            )
        }
        return bidragsevnePeriodeListe.sortedBy { it.bidragsevnePeriode.datoFom }
    }

    private fun mapBPsAndelUnderholdskostnadPeriodeListe(
        bPsAndelUnderholdskostnadPeriodeListeCore: List<BPsAndelUnderholdskostnadPeriodeCore>,
    ): List<BPsAndelUnderholdskostnadPeriode> {
        val bPsAndelUnderholdskostnadPeriodeListe = mutableListOf<BPsAndelUnderholdskostnadPeriode>()
        bPsAndelUnderholdskostnadPeriodeListeCore.forEach {
            bPsAndelUnderholdskostnadPeriodeListe.add(
                BPsAndelUnderholdskostnadPeriode(
                    soknadsbarnPersonId = it.soknadsbarnPersonId,
                    referanse = it.referanse,
                    bPsAndelUnderholdskostnadPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    andelProsent = it.andelProsent,
                    andelBelop = it.andelBelop,
                    barnetErSelvforsorget = it.barnetErSelvforsorget,
                ),
            )
        }
        return bPsAndelUnderholdskostnadPeriodeListe.sortedBy { it.bPsAndelUnderholdskostnadPeriode.datoFom }
    }

    private fun mapSamvaersfradragPeriodeListe(samvaersfradragPeriodeListeCore: List<SamvaersfradragPeriodeCore>): List<SamvaersfradragPeriode> {
        val samvaersfradragPeriodeListe = mutableListOf<SamvaersfradragPeriode>()
        samvaersfradragPeriodeListeCore.forEach {
            samvaersfradragPeriodeListe.add(
                SamvaersfradragPeriode(
                    soknadsbarnPersonId = it.soknadsbarnPersonId,
                    referanse = it.referanse,
                    samvaersfradragPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    belop = it.belop,
                ),
            )
        }
        return samvaersfradragPeriodeListe.sortedBy { it.samvaersfradragPeriode.datoFom }
    }

    private fun mapDeltBostedPeriodeListe(deltBostedPeriodeListeCore: List<DeltBostedPeriodeCore>): List<DeltBostedPeriode> {
        val deltBostedPeriodeListe = mutableListOf<DeltBostedPeriode>()
        deltBostedPeriodeListeCore.forEach {
            deltBostedPeriodeListe.add(
                DeltBostedPeriode(
                    soknadsbarnPersonId = it.soknadsbarnPersonId,
                    referanse = it.referanse,
                    deltBostedPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    deltBostedIPeriode = it.deltBostedIPeriode,
                ),
            )
        }
        return deltBostedPeriodeListe.sortedBy { it.deltBostedPeriode.datoFom }
    }

    private fun mapBarnetilleggPeriodeListe(barnetilleggPeriodeListeCore: List<BarnetilleggPeriodeCore>): List<BarnetilleggPeriode> {
        val barnetilleggPeriodeListe = mutableListOf<BarnetilleggPeriode>()
        barnetilleggPeriodeListeCore.forEach {
            barnetilleggPeriodeListe.add(
                BarnetilleggPeriode(
                    soknadsbarnPersonId = it.soknadsbarnPersonId,
                    referanse = it.referanse,
                    barnetilleggPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    belop = it.belop,
                    skattProsent = it.skattProsent,
                ),
            )
        }
        return barnetilleggPeriodeListe.sortedBy { it.barnetilleggPeriode.datoFom }
    }

    private fun mapBarnetilleggForsvaretPeriodeListe(
        barnetilleggForsvaretPeriodeListeCore: List<BarnetilleggForsvaretPeriodeCore>,
    ): List<BarnetilleggForsvaretPeriode> {
        val barnetilleggForsvaretPeriodeListe = mutableListOf<BarnetilleggForsvaretPeriode>()
        barnetilleggForsvaretPeriodeListeCore.forEach {
            barnetilleggForsvaretPeriodeListe.add(
                BarnetilleggForsvaretPeriode(
                    referanse = it.referanse,
                    barnetilleggForsvaretPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    barnetilleggForsvaretIPeriode = it.barnetilleggForsvaretIPeriode,
                ),
            )
        }
        return barnetilleggForsvaretPeriodeListe.sortedBy { it.barnetilleggForsvaretPeriode.datoFom }
    }

    private fun mapAndreLopendeBidragPeriodeListe(
        andreLopendeBidragPeriodeListeCore: List<AndreLopendeBidragPeriodeCore>,
    ): List<AndreLopendeBidragPeriode> {
        val andreLopendeBidragPeriodeListe = mutableListOf<AndreLopendeBidragPeriode>()
        andreLopendeBidragPeriodeListeCore.forEach {
            andreLopendeBidragPeriodeListe.add(
                AndreLopendeBidragPeriode(
                    referanse = it.referanse,
                    andreLopendeBidragPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    barnPersonId = it.barnPersonId,
                    bidragBelop = it.bidragBelop,
                    samvaersfradragBelop = it.samvaersfradragBelop,
                ),
            )
        }
        return andreLopendeBidragPeriodeListe.sortedBy { it.andreLopendeBidragPeriode.datoFom }
    }

    private fun mapResultatPeriode(resultatPeriodeListe: List<ResultatPeriode>): List<ResultatPeriodeCore> {
        val resultatPeriodeCoreListe = mutableListOf<ResultatPeriodeCore>()
        resultatPeriodeListe.forEach { resultatPeriode ->
            val resultatBeregningListe = resultatPeriode.resultatListe
            resultatBeregningListe.forEach {
                resultatPeriodeCoreListe.add(
                    ResultatPeriodeCore(
                        soknadsbarnPersonId = it.soknadsbarnPersonId,
                        periode = PeriodeCore(datoFom = resultatPeriode.periode.datoFom, datoTil = resultatPeriode.periode.datoTil),
                        resultat = ResultatBeregningCore(belop = it.belop, kode = it.kode.toString()),
                        grunnlagReferanseListe =
                        mapReferanseListe(
                            resultatPeriode = resultatPeriode,
                            soknadsbarnPersonId = it.soknadsbarnPersonId,
                        ),
                    ),
                )
            }
        }
        return resultatPeriodeCoreListe
    }

    // Mapper ut felles grunnlag (som gjelder uavhengig av barn)
    private fun mapReferanseListe(resultatPeriode: ResultatPeriode, soknadsbarnPersonId: Int): List<String> {
        val (bidragsevne, grunnlagPerBarnListe, barnetilleggForsvaret, andreLopendeBidragListe) = resultatPeriode.grunnlag
        val sjablonListe = resultatPeriode.resultatListe.flatMap { it.sjablonListe }
        val referanseListe = mutableListOf<String>()
        referanseListe.add(bidragsevne.referanse)
        referanseListe.add(barnetilleggForsvaret.referanse)
        andreLopendeBidragListe.forEach {
            referanseListe.add(it.referanse)
        }
        referanseListe.addAll(mapGrunnlagPerBarn(grunnlagPerBarnListe, soknadsbarnPersonId))
        referanseListe.addAll(sjablonListe.map { lagSjablonReferanse(it) }.distinct())
        return referanseListe.sorted()
    }

    // Mapper ut grunnlag for det barnet som behandles
    private fun mapGrunnlagPerBarn(grunnlagBeregningPerBarnListe: List<GrunnlagBeregningPerBarn>, soknadsbarnPersonId: Int): List<String> {
        val referanseListe = mutableListOf<String>()
        grunnlagBeregningPerBarnListe.forEach {
            if (it.soknadsbarnPersonId == soknadsbarnPersonId) {
                referanseListe.add(it.bPsAndelUnderholdskostnad.referanse)
                referanseListe.add(it.samvaersfradrag.referanse)
                referanseListe.add(it.deltBosted.referanse)
                it.barnetilleggBP?.referanse?.let { referanse -> referanseListe.add(referanse) }
                it.barnetilleggBM?.referanse?.let { referanse -> referanseListe.add(referanse) }
            }
        }
        return referanseListe
    }

    private fun mapSjablonGrunnlagListe(resultatPeriodeListe: List<ResultatPeriode>) = resultatPeriodeListe
        .flatMap { it.resultatListe }
        .flatMap { mapSjablonListe(it.sjablonListe) }
        .distinct()
}
