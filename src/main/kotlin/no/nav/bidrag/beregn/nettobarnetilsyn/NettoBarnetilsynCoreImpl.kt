package no.nav.bidrag.beregn.nettobarnetilsyn

import no.nav.bidrag.beregn.felles.FellesCore
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnetNettoBarnetilsynResultat
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgiftPeriode
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatPeriode
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynGrunnlagCore
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnetNettoBarnetilsynResultatCore
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.FaktiskUtgiftPeriodeCore
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode

class NettoBarnetilsynCoreImpl(private val nettoBarnetilsynPeriode: NettoBarnetilsynPeriode) : FellesCore(), NettoBarnetilsynCore {
    override fun beregnNettoBarnetilsyn(grunnlag: BeregnNettoBarnetilsynGrunnlagCore): BeregnetNettoBarnetilsynResultatCore {
        val beregnNettoBarnetilsynGrunnlag = mapTilBusinessObject(grunnlag)
        val avvikListe = nettoBarnetilsynPeriode.validerInput(beregnNettoBarnetilsynGrunnlag)
        val beregnNettoBarnetilsynResultat =
            if (avvikListe.isEmpty()) {
                nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag)
            } else {
                BeregnetNettoBarnetilsynResultat(emptyList())
            }
        return mapFraBusinessObject(avvikListe = avvikListe, resultat = beregnNettoBarnetilsynResultat)
    }

    private fun mapTilBusinessObject(grunnlag: BeregnNettoBarnetilsynGrunnlagCore) = BeregnNettoBarnetilsynGrunnlag(
        beregnDatoFra = grunnlag.beregnDatoFra,
        beregnDatoTil = grunnlag.beregnDatoTil,
        faktiskUtgiftPeriodeListe = mapFaktiskUtgiftPeriodeListe(grunnlag.faktiskUtgiftPeriodeListe),
        sjablonPeriodeListe = mapSjablonPeriodeListe(grunnlag.sjablonPeriodeListe),
    )

    private fun mapFraBusinessObject(avvikListe: List<Avvik>, resultat: BeregnetNettoBarnetilsynResultat) = BeregnetNettoBarnetilsynResultatCore(
        resultatPeriodeListe = mapResultatPeriode(resultat.resultatPeriodeListe),
        sjablonListe = mapSjablonGrunnlagListe(resultat.resultatPeriodeListe),
        avvikListe = mapAvvik(avvikListe),
    )

    private fun mapFaktiskUtgiftPeriodeListe(faktiskUtgiftPeriodeListeCore: List<FaktiskUtgiftPeriodeCore>): List<FaktiskUtgiftPeriode> {
        val faktiskUtgiftPeriodeListe = mutableListOf<FaktiskUtgiftPeriode>()
        faktiskUtgiftPeriodeListeCore.forEach {
            faktiskUtgiftPeriodeListe.add(
                FaktiskUtgiftPeriode(
                    soknadsbarnPersonId = it.soknadsbarnPersonId,
                    referanse = it.referanse,
                    faktiskUtgiftPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    soknadsbarnFodselsdato = it.soknadsbarnFodselsdato,
                    belop = it.belop,
                ),
            )
        }
        return faktiskUtgiftPeriodeListe
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
                        resultat = ResultatBeregningCore(it.belop),
                        grunnlagReferanseListe = mapReferanseListe(resultatPeriode),
                    ),
                )
            }
        }
        return resultatPeriodeCoreListe
    }

    private fun mapReferanseListe(resultatPeriode: ResultatPeriode): List<String> {
        val sjablonListe = resultatPeriode.resultatListe.flatMap { it.sjablonListe }
        val referanseListe = mutableListOf<String>()
        resultatPeriode.grunnlag.faktiskUtgiftListe.forEach {
            referanseListe.add(it.referanse)
        }
        referanseListe.addAll(sjablonListe.map { lagSjablonReferanse(it) }.distinct())
        return referanseListe.sorted()
    }

    private fun mapSjablonGrunnlagListe(resultatPeriodeListe: List<ResultatPeriode>) = resultatPeriodeListe
        .flatMap { it.resultatListe }
        .flatMap { mapSjablonListe(it.sjablonListe) }
        .distinct()
}
