package no.nav.bidrag.beregn.underholdskostnad

import no.nav.bidrag.beregn.felles.FellesCore
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnetUnderholdskostnadResultat
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.Soknadsbarn
import no.nav.bidrag.beregn.underholdskostnad.dto.BarnetilsynMedStonadPeriodeCore
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadGrunnlagCore
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnetUnderholdskostnadResultatCore
import no.nav.bidrag.beregn.underholdskostnad.dto.ForpleiningUtgiftPeriodeCore
import no.nav.bidrag.beregn.underholdskostnad.dto.NettoBarnetilsynPeriodeCore
import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.underholdskostnad.dto.SoknadsbarnCore
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode

class UnderholdskostnadCoreImpl(private val underholdskostnadPeriode: UnderholdskostnadPeriode) : FellesCore(), UnderholdskostnadCore {
    override fun beregnUnderholdskostnad(grunnlag: BeregnUnderholdskostnadGrunnlagCore): BeregnetUnderholdskostnadResultatCore {
        val beregnUnderholdskostnadGrunnlag = mapTilBusinessObject(grunnlag)
        val avvikListe = underholdskostnadPeriode.validerInput(beregnUnderholdskostnadGrunnlag)
        val beregnUnderholdskostnadResultat =
            if (avvikListe.isEmpty()) {
                underholdskostnadPeriode.beregnPerioder(beregnUnderholdskostnadGrunnlag)
            } else {
                BeregnetUnderholdskostnadResultat(emptyList())
            }
        return mapFraBusinessObject(avvikListe, beregnUnderholdskostnadResultat)
    }

    private fun mapTilBusinessObject(grunnlag: BeregnUnderholdskostnadGrunnlagCore) = BeregnUnderholdskostnadGrunnlag(
        beregnDatoFra = grunnlag.beregnDatoFra,
        beregnDatoTil = grunnlag.beregnDatoTil,
        soknadsbarn = mapSoknadsbarn(grunnlag.soknadsbarn),
        barnetilsynMedStonadPeriodeListe = mapBarnetilsynMedStonadPeriodeListe(grunnlag.barnetilsynMedStonadPeriodeListe),
        nettoBarnetilsynPeriodeListe = mapNettoBarnetilsynPeriodeListe(grunnlag.nettoBarnetilsynPeriodeListe),
        forpleiningUtgiftPeriodeListe = mapForpleiningUtgiftPeriodeListe(grunnlag.forpleiningUtgiftPeriodeListe),
        sjablonPeriodeListe = mapSjablonPeriodeListe(grunnlag.sjablonPeriodeListe),
    )

    private fun mapFraBusinessObject(avvikListe: List<Avvik>, resultat: BeregnetUnderholdskostnadResultat) = BeregnetUnderholdskostnadResultatCore(
        resultatPeriodeListe = mapResultatPeriode(resultat.resultatPeriodeListe),
        sjablonListe = mapSjablonGrunnlagListe(resultat.resultatPeriodeListe),
        avvikListe = mapAvvik(avvikListe),
    )

    private fun mapSoknadsbarn(soknadsbarnCore: SoknadsbarnCore) =
        Soknadsbarn(soknadsbarnCore.referanse, soknadsbarnCore.personId, soknadsbarnCore.fodselsdato)

    private fun mapBarnetilsynMedStonadPeriodeListe(
        barnetilsynMedStonadPeriodeListeCore: List<BarnetilsynMedStonadPeriodeCore>,
    ): List<BarnetilsynMedStonadPeriode> {
        val barnetilsynMedStonadPeriodeListe = mutableListOf<BarnetilsynMedStonadPeriode>()
        barnetilsynMedStonadPeriodeListeCore.forEach {
            barnetilsynMedStonadPeriodeListe.add(
                BarnetilsynMedStonadPeriode(
                    referanse = it.referanse,
                    barnetilsynMedStonadPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    tilsynType = it.tilsynType,
                    stonadType = it.stonadType,
                ),
            )
        }
        return barnetilsynMedStonadPeriodeListe
    }

    private fun mapNettoBarnetilsynPeriodeListe(nettoBarnetilsynPeriodeListeCore: List<NettoBarnetilsynPeriodeCore>): List<NettoBarnetilsynPeriode> {
        val nettoBarnetilsynPeriodeListe = mutableListOf<NettoBarnetilsynPeriode>()
        nettoBarnetilsynPeriodeListeCore.forEach {
            nettoBarnetilsynPeriodeListe.add(
                NettoBarnetilsynPeriode(
                    referanse = it.referanse,
                    nettoBarnetilsynPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    belop = it.belop,
                ),
            )
        }
        return nettoBarnetilsynPeriodeListe
    }

    private fun mapForpleiningUtgiftPeriodeListe(
        forpleiningUtgiftPeriodeListeCore: List<ForpleiningUtgiftPeriodeCore>,
    ): List<ForpleiningUtgiftPeriode> {
        val forpleiningUtgiftPeriodeListe = mutableListOf<ForpleiningUtgiftPeriode>()
        forpleiningUtgiftPeriodeListeCore.forEach {
            forpleiningUtgiftPeriodeListe.add(
                ForpleiningUtgiftPeriode(
                    referanse = it.referanse,
                    forpleiningUtgiftPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    belop = it.belop,
                ),
            )
        }
        return forpleiningUtgiftPeriodeListe
    }

    private fun mapResultatPeriode(resultatPeriodeListe: List<ResultatPeriode>): List<ResultatPeriodeCore> {
        val resultatPeriodeCoreListe = mutableListOf<ResultatPeriodeCore>()
        resultatPeriodeListe.forEach {
            resultatPeriodeCoreListe.add(
                ResultatPeriodeCore(
                    soknadsbarnPersonId = it.soknadsbarnPersonId,
                    periode = PeriodeCore(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    resultat = ResultatBeregningCore(it.resultat.belop),
                    grunnlagReferanseListe = mapReferanseListe(it),
                ),
            )
        }
        return resultatPeriodeCoreListe
    }

    private fun mapReferanseListe(resultatPeriode: ResultatPeriode): List<String> {
        val (soknadsbarn, barnetilsynMedStonad, nettoBarnetilsyn, forpleiningUtgift) = resultatPeriode.grunnlag
        val sjablonListe = resultatPeriode.resultat.sjablonListe
        val referanseListe = mutableListOf<String>()
        referanseListe.add(soknadsbarn.referanse)
        referanseListe.add(barnetilsynMedStonad.referanse)
        referanseListe.add(nettoBarnetilsyn.referanse)
        referanseListe.add(forpleiningUtgift.referanse)
        referanseListe.addAll(sjablonListe.map { lagSjablonReferanse(it) }.distinct())
        return referanseListe.sorted()
    }

    private fun mapSjablonGrunnlagListe(resultatPeriodeListe: List<ResultatPeriode>) = resultatPeriodeListe
        .flatMap { mapSjablonListe(it.resultat.sjablonListe) }
        .distinct()
}
