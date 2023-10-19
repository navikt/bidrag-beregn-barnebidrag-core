package no.nav.bidrag.beregn.kostnadsberegnetbidrag

import no.nav.bidrag.beregn.felles.FellesCore
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnetKostnadsberegnetBidragResultat
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersfradragPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BPsAndelUnderholdskostnadPeriodeCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnKostnadsberegnetBidragGrunnlagCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnetKostnadsberegnetBidragResultatCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.SamvaersfradragPeriodeCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.UnderholdskostnadPeriodeCore
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode

class KostnadsberegnetBidragCoreImpl(private val kostnadsberegnetBidragPeriode: KostnadsberegnetBidragPeriode) : FellesCore(),
    KostnadsberegnetBidragCore {
    override fun beregnKostnadsberegnetBidrag(grunnlag: BeregnKostnadsberegnetBidragGrunnlagCore): BeregnetKostnadsberegnetBidragResultatCore {
        val beregnKostnadsberegnetBidragGrunnlag = mapTilBusinessObject(grunnlag)
        val avvikListe = kostnadsberegnetBidragPeriode.validerInput(beregnKostnadsberegnetBidragGrunnlag)
        val beregnKostnadsberegnetBidragResultat =
            if (avvikListe.isEmpty()) {
                kostnadsberegnetBidragPeriode.beregnPerioder(beregnKostnadsberegnetBidragGrunnlag)
            } else {
                BeregnetKostnadsberegnetBidragResultat(emptyList())
            }
        return mapFraBusinessObject(avvikListe, beregnKostnadsberegnetBidragResultat)
    }

    private fun mapTilBusinessObject(grunnlag: BeregnKostnadsberegnetBidragGrunnlagCore) =
        BeregnKostnadsberegnetBidragGrunnlag(
            beregnDatoFra = grunnlag.beregnDatoFra,
            beregnDatoTil = grunnlag.beregnDatoTil,
            soknadsbarnPersonId = grunnlag.soknadsbarnPersonId,
            underholdskostnadPeriodeListe = mapUnderholdskostnadPeriodeListe(grunnlag.underholdskostnadPeriodeListe),
            bPsAndelUnderholdskostnadPeriodeListe = mapBPsAndelUnderholdskostnadPeriodeListe(grunnlag.bPsAndelUnderholdskostnadPeriodeListe),
            samvaersfradragPeriodeListe = mapSamvaersfradragPeriodeListe(grunnlag.samvaersfradragPeriodeListe)
        )

    private fun mapFraBusinessObject(avvikListe: List<Avvik>, resultat: BeregnetKostnadsberegnetBidragResultat) =
        BeregnetKostnadsberegnetBidragResultatCore(
            resultatPeriodeListe = mapResultatPeriode(resultat.resultatPeriodeListe),
            avvikListe = mapAvvik(avvikListe)
        )

    private fun mapUnderholdskostnadPeriodeListe(underholdskostnadPeriodeListeCore: List<UnderholdskostnadPeriodeCore>): List<UnderholdskostnadPeriode> {
        val underholdskostnadPeriodeListe = mutableListOf<UnderholdskostnadPeriode>()
        underholdskostnadPeriodeListeCore.forEach {
            underholdskostnadPeriodeListe.add(
                UnderholdskostnadPeriode(
                    referanse = it.referanse,
                    underholdskostnadPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    belop = it.belop
                )
            )
        }
        return underholdskostnadPeriodeListe
    }

    private fun mapBPsAndelUnderholdskostnadPeriodeListe(
        bPsAndelUnderholdskostnadPeriodeListeCore: List<BPsAndelUnderholdskostnadPeriodeCore>
    ): List<BPsAndelUnderholdskostnadPeriode> {
        val bPsAndelUnderholdskostnadPeriodeListe = mutableListOf<BPsAndelUnderholdskostnadPeriode>()
        bPsAndelUnderholdskostnadPeriodeListeCore.forEach {
            bPsAndelUnderholdskostnadPeriodeListe.add(
                BPsAndelUnderholdskostnadPeriode(
                    referanse = it.referanse,
                    bPsAndelUnderholdskostnadPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    andelProsent = it.andelProsent
                )
            )
        }
        return bPsAndelUnderholdskostnadPeriodeListe
    }

    private fun mapSamvaersfradragPeriodeListe(samvaersfradragPeriodeListeCore: List<SamvaersfradragPeriodeCore>): List<SamvaersfradragPeriode> {
        val samvaersfradragPeriodeListe = mutableListOf<SamvaersfradragPeriode>()
        samvaersfradragPeriodeListeCore.forEach {
            SamvaersfradragPeriode(
                referanse = it.referanse,
                samvaersfradragPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                belop = it.belop
            )
        }
        return samvaersfradragPeriodeListe
    }

    private fun mapResultatPeriode(resultatPeriodeListe: List<ResultatPeriode>): List<ResultatPeriodeCore> {
        val resultatPeriodeCoreListe = mutableListOf<ResultatPeriodeCore>()
        resultatPeriodeListe.forEach {
            resultatPeriodeCoreListe.add(
                ResultatPeriodeCore(
                    soknadsbarnPersonId = it.soknadsbarnPersonId,
                    periode = PeriodeCore(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    resultat = ResultatBeregningCore(it.resultat.belop),
                    grunnlagReferanseListe = mapReferanseListe(it)
                )
            )
        }
        return resultatPeriodeCoreListe
    }

    private fun mapReferanseListe(resultatPeriode: ResultatPeriode): List<String> {
        val (underholdskostnad, bPsAndelUnderholdskostnad, samvaersfradrag) = resultatPeriode.grunnlag
        val referanseListe = mutableListOf<String>()
        referanseListe.add(underholdskostnad.referanse)
        referanseListe.add(bPsAndelUnderholdskostnad.referanse)
        referanseListe.add(samvaersfradrag.referanse)
        return referanseListe.sorted()
    }
}
