package no.nav.bidrag.beregn.bidragsevne

import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstandPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnetBidragsevneResultat
import no.nav.bidrag.beregn.bidragsevne.bo.BostatusPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.InntektPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.SaerfradragPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.SkatteklassePeriode
import no.nav.bidrag.beregn.bidragsevne.dto.BarnIHusstandPeriodeCore
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneGrunnlagCore
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnetBidragsevneResultatCore
import no.nav.bidrag.beregn.bidragsevne.dto.BostatusPeriodeCore
import no.nav.bidrag.beregn.bidragsevne.dto.InntektPeriodeCore
import no.nav.bidrag.beregn.bidragsevne.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.bidragsevne.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.bidragsevne.dto.SaerfradragPeriodeCore
import no.nav.bidrag.beregn.bidragsevne.dto.SkatteklassePeriodeCore
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode
import no.nav.bidrag.beregn.felles.FellesCore
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.domain.enums.BostatusKode
import no.nav.bidrag.domain.enums.SaerfradragKode

class BidragsevneCoreImpl(private val bidragsevnePeriode: BidragsevnePeriode) : FellesCore(), BidragsevneCore {
    override fun beregnBidragsevne(grunnlag: BeregnBidragsevneGrunnlagCore): BeregnetBidragsevneResultatCore {
        val beregnBidragsevneGrunnlag = mapTilBusinessObject(grunnlag)
        val avvikListe = bidragsevnePeriode.validerInput(beregnBidragsevneGrunnlag)
        val beregnBidragsevneResultat =
            if (avvikListe.isEmpty()) {
                bidragsevnePeriode.beregnPerioder(beregnBidragsevneGrunnlag)
            } else {
                BeregnetBidragsevneResultat(emptyList())
            }
        return mapFraBusinessObject(avvikListe = avvikListe, resultat = beregnBidragsevneResultat)
    }

    private fun mapTilBusinessObject(grunnlag: BeregnBidragsevneGrunnlagCore) =
        BeregnBidragsevneGrunnlag(
            beregnDatoFra = grunnlag.beregnDatoFra,
            beregnDatoTil = grunnlag.beregnDatoTil,
            inntektPeriodeListe = mapInntektPeriodeListe(grunnlag.inntektPeriodeListe),
            skatteklassePeriodeListe = mapSkatteklassePeriodeListe(grunnlag.skatteklassePeriodeListe),
            bostatusPeriodeListe = mapBostatusPeriodeListe(grunnlag.bostatusPeriodeListe),
            barnIHusstandPeriodeListe = mapBarnIHusstandPeriodeListe(grunnlag.barnIHusstandPeriodeListe),
            saerfradragPeriodeListe = mapSaerfradragPeriodeListe(grunnlag.saerfradragPeriodeListe),
            sjablonPeriodeListe = mapSjablonPeriodeListe(grunnlag.sjablonPeriodeListe)
        )

    private fun mapFraBusinessObject(avvikListe: List<Avvik>, resultat: BeregnetBidragsevneResultat) =
        BeregnetBidragsevneResultatCore(
            resultatPeriodeListe = mapResultatPeriode(resultat.resultatPeriodeListe),
            sjablonListe = mapSjablonGrunnlagListe(resultat.resultatPeriodeListe),
            avvikListe = mapAvvik(avvikListe)
        )

    private fun mapInntektPeriodeListe(inntektPeriodeListeCore: List<InntektPeriodeCore>): List<InntektPeriode> {
        val inntektPeriodeListe = mutableListOf<InntektPeriode>()
        inntektPeriodeListeCore.forEach {
            inntektPeriodeListe.add(
                InntektPeriode(
                    referanse = it.referanse,
                    inntektPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    type = it.type,
                    belop = it.belop
                )
            )
        }
        return inntektPeriodeListe
    }

    private fun mapSkatteklassePeriodeListe(skatteklassePeriodeListeCore: List<SkatteklassePeriodeCore>): List<SkatteklassePeriode> {
        val skatteklassePeriodeListe = mutableListOf<SkatteklassePeriode>()
        skatteklassePeriodeListeCore.forEach {
            skatteklassePeriodeListe.add(
                SkatteklassePeriode(
                    referanse = it.referanse,
                    skatteklassePeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    skatteklasse = it.skatteklasse
                )
            )
        }
        return skatteklassePeriodeListe
    }

    private fun mapBostatusPeriodeListe(bostatusPeriodeListeCore: List<BostatusPeriodeCore>): List<BostatusPeriode> {
        val bostatusPeriodeListe = mutableListOf<BostatusPeriode>()
        bostatusPeriodeListeCore.forEach {
            bostatusPeriodeListe.add(
                BostatusPeriode(
                    referanse = it.referanse,
                    bostatusPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    kode = BostatusKode.valueOf(it.kode)
                )
            )
        }
        return bostatusPeriodeListe
    }

    private fun mapBarnIHusstandPeriodeListe(barnIHusstandPeriodeListeCore: List<BarnIHusstandPeriodeCore>): List<BarnIHusstandPeriode> {
        val barnIHusstandPeriodeListe = mutableListOf<BarnIHusstandPeriode>()
        barnIHusstandPeriodeListeCore.forEach {
            barnIHusstandPeriodeListe.add(
                BarnIHusstandPeriode(
                    referanse = it.referanse,
                    barnIHusstandPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    antallBarn = it.antallBarn
                )
            )
        }
        return barnIHusstandPeriodeListe
    }

    private fun mapSaerfradragPeriodeListe(saerfradragPeriodeListeCore: List<SaerfradragPeriodeCore>): List<SaerfradragPeriode> {
        val saerfradragPeriodeListe = mutableListOf<SaerfradragPeriode>()
        saerfradragPeriodeListeCore.forEach {
            saerfradragPeriodeListe.add(
                SaerfradragPeriode(
                    referanse = it.referanse,
                    saerfradragPeriode = Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    kode = SaerfradragKode.valueOf(it.kode)
                )
            )
        }
        return saerfradragPeriodeListe
    }

    private fun mapResultatPeriode(resultatPeriodeListe: List<ResultatPeriode>): List<ResultatPeriodeCore> {
        val resultatPeriodeCoreListe = mutableListOf<ResultatPeriodeCore>()
        resultatPeriodeListe.forEach {
            val (belop, inntekt25Prosent) = it.resultat
            resultatPeriodeCoreListe.add(
                ResultatPeriodeCore(
                    periode = PeriodeCore(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    resultat = ResultatBeregningCore(belop = belop, inntekt25Prosent = inntekt25Prosent),
                    grunnlagReferanseListe = mapReferanseListe(it)
                )
            )
        }
        return resultatPeriodeCoreListe
    }

    private fun mapReferanseListe(resultatPeriode: ResultatPeriode): List<String> {
        val (inntektListe, skatteklasse, bostatus, barnIHusstand, saerfradrag) = resultatPeriode.grunnlag
        val sjablonListe = resultatPeriode.resultat.sjablonListe
        val referanseListe = mutableListOf<String>()
        inntektListe.forEach {
            referanseListe.add(it.referanse)
        }
        referanseListe.add(skatteklasse.referanse)
        referanseListe.add(bostatus.referanse)
        referanseListe.add(barnIHusstand.referanse)
        referanseListe.add(saerfradrag.referanse)
        referanseListe.addAll(sjablonListe.map { lagSjablonReferanse(it) }.distinct())
        return referanseListe.sorted()
    }

    private fun mapSjablonGrunnlagListe(resultatPeriodeListe: List<ResultatPeriode>) =
        resultatPeriodeListe
            .flatMap { mapSjablonListe(it.resultat.sjablonListe) }
            .distinct()
}
