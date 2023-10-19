package no.nav.bidrag.beregn.samvaersfradrag

import no.nav.bidrag.beregn.felles.FellesCore
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnetSamvaersfradragResultat
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersklassePeriode
import no.nav.bidrag.beregn.samvaersfradrag.bo.Soknadsbarn
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragGrunnlagCore
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnetSamvaersfradragResultatCore
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.samvaersfradrag.dto.SamvaersklassePeriodeCore
import no.nav.bidrag.beregn.samvaersfradrag.dto.SoknadsbarnCore
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode

class SamvaersfradragCoreImpl(private val samvaersfradragPeriode: SamvaersfradragPeriode) : FellesCore(), SamvaersfradragCore {
    override fun beregnSamvaersfradrag(grunnlag: BeregnSamvaersfradragGrunnlagCore): BeregnetSamvaersfradragResultatCore {
        val beregnSamvaersfradragGrunnlag = mapTilBusinessObject(grunnlag)
        val avvikListe = samvaersfradragPeriode.validerInput(beregnSamvaersfradragGrunnlag)
        val beregnSamvaersfradragResultat =
            if (avvikListe.isEmpty()) {
                samvaersfradragPeriode.beregnPerioder(beregnSamvaersfradragGrunnlag)
            } else {
                BeregnetSamvaersfradragResultat(emptyList())
            }
        return mapFraBusinessObject(avvikListe = avvikListe, resultat = beregnSamvaersfradragResultat)
    }

    private fun mapTilBusinessObject(grunnlag: BeregnSamvaersfradragGrunnlagCore) =
        BeregnSamvaersfradragGrunnlag(
            beregnDatoFra = grunnlag.beregnDatoFra,
            beregnDatoTil = grunnlag.beregnDatoTil,
            soknadsbarn = mapSoknadsbarn(grunnlag.soknadsbarn),
            samvaersklassePeriodeListe = mapSamvaersklassePeriodeListe(grunnlag.samvaersklassePeriodeListe),
            sjablonPeriodeListe = mapSjablonPeriodeListe(grunnlag.sjablonPeriodeListe)
        )

    private fun mapFraBusinessObject(avvikListe: List<Avvik>, resultat: BeregnetSamvaersfradragResultat) =
        BeregnetSamvaersfradragResultatCore(
            resultatPeriodeListe = mapResultatPeriode(resultat.resultatPeriodeListe),
            sjablonListe = mapSjablonGrunnlagListe(resultat.resultatPeriodeListe),
            avvikListe = mapAvvik(avvikListe)
        )

    private fun mapSoknadsbarn(soknadsbarnCore: SoknadsbarnCore) =
        Soknadsbarn(referanse = soknadsbarnCore.referanse, personId = soknadsbarnCore.personId, fodselsdato = soknadsbarnCore.fodselsdato)

    private fun mapSamvaersklassePeriodeListe(samvaersklassePeriodeListeCore: List<SamvaersklassePeriodeCore>): List<SamvaersklassePeriode> {
        val samvaersklassePeriodeListe = mutableListOf<SamvaersklassePeriode>()
        samvaersklassePeriodeListeCore.forEach {
            samvaersklassePeriodeListe.add(
                SamvaersklassePeriode(
                    referanse = it.referanse,
                    samvaersklassePeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    samvaersklasse = it.samvaersklasse
                )
            )
        }
        return samvaersklassePeriodeListe
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
        val (soknadsbarn, samvaersklasse) = resultatPeriode.grunnlag
        val sjablonListe = resultatPeriode.resultat.sjablonListe
        val referanseListe = mutableListOf<String>()
        referanseListe.add(soknadsbarn.referanse)
        referanseListe.add(samvaersklasse.referanse)
        referanseListe.addAll(sjablonListe.map { lagSjablonReferanse(it) }.distinct())
        return referanseListe.sorted()
    }

    private fun mapSjablonGrunnlagListe(resultatPeriodeListe: List<ResultatPeriode>) =
        resultatPeriodeListe
            .flatMap { mapSjablonListe(it.resultat.sjablonListe) }
            .distinct()
}
