package no.nav.bidrag.beregn.forholdsmessigfordeling

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingGrunnlag
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingResultat
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSak
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSakPeriode
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BidragsevnePeriode
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagPerBarn
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPerBarn
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPeriode
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnForholdsmessigFordelingGrunnlagCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnForholdsmessigFordelingResultatCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnetBidragSakCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnetBidragSakPeriodeCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BidragsevneCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BidragsevnePeriodeCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.GrunnlagBeregningPeriodisertCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.GrunnlagPerBarnCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.ResultatPerBarnCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.forholdsmessigfordeling.periode.ForholdsmessigFordelingPeriode

class ForholdsmessigFordelingCoreImpl(private val forholdsmessigFordelingPeriode: ForholdsmessigFordelingPeriode) : ForholdsmessigFordelingCore {
    override fun beregnForholdsmessigFordeling(grunnlag: BeregnForholdsmessigFordelingGrunnlagCore): BeregnForholdsmessigFordelingResultatCore {
        val beregnForholdsmessigFordelingGrunnlag = mapTilBusinessObject(grunnlag)
        val avvikListe = forholdsmessigFordelingPeriode.validerInput(beregnForholdsmessigFordelingGrunnlag)
        val beregnForholdsmessigFordelingResultat =
            if (avvikListe.isEmpty()) {
                forholdsmessigFordelingPeriode.beregnPerioder(beregnForholdsmessigFordelingGrunnlag)
            } else {
                BeregnForholdsmessigFordelingResultat(emptyList())
            }
        return mapFraBusinessObject(avvikListe, beregnForholdsmessigFordelingResultat)
    }

    private fun mapTilBusinessObject(grunnlag: BeregnForholdsmessigFordelingGrunnlagCore) = BeregnForholdsmessigFordelingGrunnlag(
        beregnDatoFra = grunnlag.beregnDatoFra,
        beregnDatoTil = grunnlag.beregnDatoTil,
        bidragsevnePeriodeListe = mapBidragsevnePeriodeListe(grunnlag.bidragsevnePeriodeListe),
        beregnetBidragPeriodeListe = mapBeregnetBidragSakPeriodeListe(grunnlag.beregnetBidragPeriodeListe),
    )

    private fun mapFraBusinessObject(avvikListe: List<Avvik>, resultat: BeregnForholdsmessigFordelingResultat) =
        BeregnForholdsmessigFordelingResultatCore(
            resultatPeriodeListe = mapResultatPeriode(resultat.resultatPeriodeListe),
            avvikListe = mapAvvik(avvikListe),
        )

    private fun mapBidragsevnePeriodeListe(bidragsevnePeriodeListeCore: List<BidragsevnePeriodeCore>): List<BidragsevnePeriode> {
        val bidragsevnePeriodeListe = mutableListOf<BidragsevnePeriode>()
        bidragsevnePeriodeListeCore.forEach {
            bidragsevnePeriodeListe.add(
                BidragsevnePeriode(
                    bidragsevneDatoFraTil = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    bidragsevneBelop = it.belop,
                    tjuefemProsentInntekt = it.tjuefemProsentInntekt,
                ),
            )
        }
        return bidragsevnePeriodeListe.sortedBy { it.bidragsevneDatoFraTil.datoFom }
    }

    private fun mapBeregnetBidragSakPeriodeListe(
        beregnetBidragSakPeriodeListeCore: List<BeregnetBidragSakPeriodeCore>,
    ): List<BeregnetBidragSakPeriode> {
        val beregnetBidragSakPeriodeListe = mutableListOf<BeregnetBidragSakPeriode>()
        beregnetBidragSakPeriodeListeCore.forEach {
            beregnetBidragSakPeriodeListe.add(
                BeregnetBidragSakPeriode(
                    saksnr = it.saksnr,
                    periodeDatoFraTil = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    grunnlagPerBarnListe = mapGrunnlagPerBarnListe(it.grunnlagPerBarnListe),
                ),
            )
        }
        return beregnetBidragSakPeriodeListe.sortedBy { it.periodeDatoFraTil.datoFom }
    }

    private fun mapGrunnlagPerBarnListe(grunnlagPerBarnListeCore: List<GrunnlagPerBarnCore>): List<GrunnlagPerBarn> {
        val grunnlagPerBarnListe = mutableListOf<GrunnlagPerBarn>()
        grunnlagPerBarnListeCore.forEach {
            grunnlagPerBarnListe.add(
                GrunnlagPerBarn(barnPersonId = it.barnPersonId, bidragBelop = it.bidragBelop),
            )
        }
        return grunnlagPerBarnListe
    }

    private fun mapAvvik(avvikListe: List<Avvik>): List<AvvikCore> {
        val avvikCoreListe = mutableListOf<AvvikCore>()
        avvikListe.forEach {
            avvikCoreListe.add(AvvikCore(avvikTekst = it.avvikTekst, avvikType = it.avvikType.toString()))
        }
        return avvikCoreListe
    }

    private fun mapResultatPeriode(resultatPeriodeListe: List<ResultatPeriode>): List<ResultatPeriodeCore> {
        val resultatPeriodeCoreListe = mutableListOf<ResultatPeriodeCore>()
        resultatPeriodeListe.forEach {
            resultatPeriodeCoreListe.add(
                ResultatPeriodeCore(
                    periode = PeriodeCore(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    resultatBeregningListe = mapResultatBeregning(it.resultatBeregningListe),
                    resultatGrunnlag =
                    GrunnlagBeregningPeriodisertCore(
                        bidragsevne =
                        BidragsevneCore(
                            belop = it.resultatGrunnlag.bidragsevne.belop,
                            tjuefemProsentInntekt = it.resultatGrunnlag.bidragsevne.tjuefemProsentInntekt,
                        ),
                        beregnetBidragSakListe = mapBeregnetBidragSak(it.resultatGrunnlag.beregnetBidragSakListe),
                    ),
                ),
            )
        }
        return resultatPeriodeCoreListe
    }

    private fun mapResultatBeregning(resultatBeregningListe: List<ResultatBeregning>): List<ResultatBeregningCore> {
        val resultatBeregningListeCore = mutableListOf<ResultatBeregningCore>()
        resultatBeregningListe.forEach {
            resultatBeregningListeCore.add(
                ResultatBeregningCore(saksnr = it.saksnr, resultatPerBarnListe = mapResultatPerBarn(it.resultatPerBarnListe)),
            )
        }
        return resultatBeregningListeCore
    }

    private fun mapResultatPerBarn(resultatPerBarnListe: List<ResultatPerBarn>): List<ResultatPerBarnCore> {
        val resultatPerBarnListeCore = mutableListOf<ResultatPerBarnCore>()
        resultatPerBarnListe.forEach {
            resultatPerBarnListeCore.add(
                ResultatPerBarnCore(
                    barnPersonId = it.barnPersonId,
                    belop = it.belop,
                    kode = it.kode.toString(),
                ),
            )
        }
        return resultatPerBarnListeCore
    }

    private fun mapBeregnetBidragSak(beregnetBidragSakListe: List<BeregnetBidragSak>): List<BeregnetBidragSakCore> {
        val beregnetBidragSakListeCore = mutableListOf<BeregnetBidragSakCore>()
        beregnetBidragSakListe.forEach {
            beregnetBidragSakListeCore.add(
                BeregnetBidragSakCore(
                    saksnr = it.saksnr,
                    grunnlagPerBarnListe = mapGrunnlagPerBarn(it.grunnlagPerBarnListe),
                ),
            )
        }
        return beregnetBidragSakListeCore
    }

    private fun mapGrunnlagPerBarn(grunnlagPerBarnListe: List<GrunnlagPerBarn>): List<GrunnlagPerBarnCore> {
        val grunnlagPerBarnListeCore = mutableListOf<GrunnlagPerBarnCore>()
        grunnlagPerBarnListe.forEach {
            grunnlagPerBarnListeCore.add(
                GrunnlagPerBarnCore(barnPersonId = it.barnPersonId, bidragBelop = it.bidragBelop),
            )
        }
        return grunnlagPerBarnListeCore
    }
}
