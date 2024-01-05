package no.nav.bidrag.beregn.bpsandelunderholdskostnad

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnetBPsAndelUnderholdskostnadResultat
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntektPeriode
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.UnderholdskostnadPeriode
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadGrunnlagCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnetBPsAndelUnderholdskostnadResultatCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.InntektPeriodeCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.UnderholdskostnadPeriodeCore
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode
import no.nav.bidrag.beregn.felles.FellesCore
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore

class BPsAndelUnderholdskostnadCoreImpl(private val bPsAndelunderholdskostnadPeriode: BPsAndelUnderholdskostnadPeriode) :
    FellesCore(), BPsAndelUnderholdskostnadCore {
    override fun beregnBPsAndelUnderholdskostnad(
        grunnlag: BeregnBPsAndelUnderholdskostnadGrunnlagCore,
    ): BeregnetBPsAndelUnderholdskostnadResultatCore {
        val beregnBPsAndelUnderholdskostnadGrunnlag = mapTilBusinessObject(grunnlag)
        val avvikListe = bPsAndelunderholdskostnadPeriode.validerInput(beregnBPsAndelUnderholdskostnadGrunnlag)
        val beregnBPsAndelUnderholdskostnadResultat =
            if (avvikListe.isEmpty()) {
                bPsAndelunderholdskostnadPeriode.beregnPerioder(beregnBPsAndelUnderholdskostnadGrunnlag)
            } else {
                BeregnetBPsAndelUnderholdskostnadResultat(emptyList())
            }
        return mapFraBusinessObject(avvikListe, beregnBPsAndelUnderholdskostnadResultat)
    }

    private fun mapTilBusinessObject(grunnlag: BeregnBPsAndelUnderholdskostnadGrunnlagCore) = BeregnBPsAndelUnderholdskostnadGrunnlag(
        beregnDatoFra = grunnlag.beregnDatoFra,
        beregnDatoTil = grunnlag.beregnDatoTil,
        soknadsbarnPersonId = grunnlag.soknadsbarnPersonId,
        underholdskostnadPeriodeListe = mapUnderholdskostnadPeriodeListe(grunnlag.underholdskostnadPeriodeListe),
        inntektBPPeriodeListe = mapInntektPeriodeListe(grunnlag.inntektBPPeriodeListe),
        inntektBMPeriodeListe = mapInntektPeriodeListe(grunnlag.inntektBMPeriodeListe),
        inntektBBPeriodeListe = mapInntektPeriodeListe(grunnlag.inntektBBPeriodeListe),
        sjablonPeriodeListe = mapSjablonPeriodeListe(grunnlag.sjablonPeriodeListe),
    )

    private fun mapFraBusinessObject(avvikListe: List<Avvik>, resultat: BeregnetBPsAndelUnderholdskostnadResultat) =
        BeregnetBPsAndelUnderholdskostnadResultatCore(
            resultatPeriodeListe = mapResultatPeriode(resultat.resultatPeriodeListe),
            sjablonListe = mapSjablonGrunnlagListe(resultat.resultatPeriodeListe),
            avvikListe = mapAvvik(avvikListe),
        )

    private fun mapInntektPeriodeListe(inntektPeriodeListeCore: List<InntektPeriodeCore>): List<InntektPeriode> {
        val inntektPeriodeListe = mutableListOf<InntektPeriode>()
        inntektPeriodeListeCore.forEach {
            inntektPeriodeListe.add(
                InntektPeriode(
                    it.referanse,
                    Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    it.type,
                    it.belop,
                    it.deltFordel,
                    it.skatteklasse2,
                ),
            )
        }
        return inntektPeriodeListe
    }

    private fun mapUnderholdskostnadPeriodeListe(
        underholdskostnadPeriodeListeCore: List<UnderholdskostnadPeriodeCore>,
    ): List<UnderholdskostnadPeriode> {
        val underholdskostnadPeriodeListe = mutableListOf<UnderholdskostnadPeriode>()
        underholdskostnadPeriodeListeCore.forEach {
            underholdskostnadPeriodeListe.add(
                UnderholdskostnadPeriode(
                    it.referanse,
                    Periode(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    it.belop,
                ),
            )
        }
        return underholdskostnadPeriodeListe
    }

    private fun mapResultatPeriode(resultatPeriodeListe: List<ResultatPeriode>): List<ResultatPeriodeCore> {
        val resultatPeriodeCoreListe = mutableListOf<ResultatPeriodeCore>()
        resultatPeriodeListe.forEach {
            val (andelProsent, andelBelop, barnetErSelvforsorget) = it.resultat
            resultatPeriodeCoreListe.add(
                ResultatPeriodeCore(
                    soknadsbarnPersonId = it.soknadsbarnPersonId,
                    periode = PeriodeCore(datoFom = it.periode.datoFom, datoTil = it.periode.datoTil),
                    resultat =
                    ResultatBeregningCore(
                        andelProsent = andelProsent,
                        andelBelop = andelBelop,
                        barnetErSelvforsorget = barnetErSelvforsorget,
                    ),
                    grunnlagReferanseListe = mapReferanseListe(it),
                ),
            )
        }
        return resultatPeriodeCoreListe
    }

    private fun mapReferanseListe(resultatPeriode: ResultatPeriode): List<String> {
        val (underholdskostnad, inntektBPListe, inntektBMListe, inntektBBListe) = resultatPeriode.grunnlag
        val sjablonListe = resultatPeriode.resultat.sjablonListe
        val referanseListe = mutableListOf<String>()
        referanseListe.add(underholdskostnad.referanse)
        inntektBPListe.forEach {
            referanseListe.add(it.referanse)
        }
        inntektBMListe.forEach {
            referanseListe.add(it.referanse)
        }
        inntektBBListe.forEach {
            referanseListe.add(it.referanse)
        }
        referanseListe.addAll(sjablonListe.map { lagSjablonReferanse(it) }.distinct())
        return referanseListe.sorted()
    }

    private fun mapSjablonGrunnlagListe(resultatPeriodeListe: List<ResultatPeriode>) = resultatPeriodeListe
        .flatMap { mapSjablonListe(it.resultat.sjablonListe) }
        .distinct()
}
