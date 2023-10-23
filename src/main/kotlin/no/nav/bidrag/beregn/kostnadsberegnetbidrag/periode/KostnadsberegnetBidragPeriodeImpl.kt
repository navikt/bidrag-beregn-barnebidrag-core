package no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode

import no.nav.bidrag.beregn.felles.FellesPeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.felles.util.PeriodeUtil
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerInputDatoer
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregning
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnad
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragListeGrunnlag
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnetKostnadsberegnetBidragResultat
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Samvaersfradrag
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersfradragPeriode
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Underholdskostnad
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode

class KostnadsberegnetBidragPeriodeImpl(private val kostnadsberegnetBidragBeregning: KostnadsberegnetBidragBeregning) :
    FellesPeriode(),
    KostnadsberegnetBidragPeriode {

    override fun beregnPerioder(grunnlag: BeregnKostnadsberegnetBidragGrunnlag): BeregnetKostnadsberegnetBidragResultat {
        val beregnKostnadsberegnetBidragListeGrunnlag = BeregnKostnadsberegnetBidragListeGrunnlag()

        // Juster datoer
        justerDatoerGrunnlagslister(periodeGrunnlag = grunnlag, beregnKostnadsberegnetBidragListeGrunnlag = beregnKostnadsberegnetBidragListeGrunnlag)

        // Lag bruddperioder
        lagBruddperioder(periodeGrunnlag = grunnlag, beregnKostnadsberegnetBidragListeGrunnlag = beregnKostnadsberegnetBidragListeGrunnlag)

        // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
        mergeSluttperiode(periodeListe = beregnKostnadsberegnetBidragListeGrunnlag.bruddPeriodeListe, datoTil = grunnlag.beregnDatoTil)

        // Foreta beregning
        beregnKostnadsberegnetBidragPerPeriode(beregnKostnadsberegnetBidragListeGrunnlag, grunnlag.soknadsbarnPersonId)

        return BeregnetKostnadsberegnetBidragResultat(beregnKostnadsberegnetBidragListeGrunnlag.periodeResultatListe)
    }

    private fun justerDatoerGrunnlagslister(
        periodeGrunnlag: BeregnKostnadsberegnetBidragGrunnlag,
        beregnKostnadsberegnetBidragListeGrunnlag: BeregnKostnadsberegnetBidragListeGrunnlag
    ) {
        // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode(it))
        beregnKostnadsberegnetBidragListeGrunnlag.justertUnderholdskostnadPeriodeListe = periodeGrunnlag.underholdskostnadPeriodeListe
            .map { UnderholdskostnadPeriode(it) }

        beregnKostnadsberegnetBidragListeGrunnlag.justertBPsAndelUnderholdskostnadPeriodeListe = periodeGrunnlag.bPsAndelUnderholdskostnadPeriodeListe
            .map { BPsAndelUnderholdskostnadPeriode(it) }

        beregnKostnadsberegnetBidragListeGrunnlag.justertSamvaersfradragPeriodeListe = periodeGrunnlag.samvaersfradragPeriodeListe!!
            .map { SamvaersfradragPeriode(it) }
    }

    // Lager bruddperioder ved å løpe gjennom alle periodelistene
    private fun lagBruddperioder(
        periodeGrunnlag: BeregnKostnadsberegnetBidragGrunnlag,
        beregnKostnadsberegnetBidragListeGrunnlag: BeregnKostnadsberegnetBidragListeGrunnlag
    ) {
        // Bygger opp liste over perioder
        beregnKostnadsberegnetBidragListeGrunnlag.bruddPeriodeListe = Periodiserer()
            .addBruddpunkt(periodeGrunnlag.beregnDatoFra) // For å sikre bruddpunkt på start-beregning-fra-dato
            .addBruddpunkt(periodeGrunnlag.beregnDatoTil) // For å sikre bruddpunkt på start-beregning-til-dato
            .addBruddpunkter(beregnKostnadsberegnetBidragListeGrunnlag.justertUnderholdskostnadPeriodeListe)
            .addBruddpunkter(beregnKostnadsberegnetBidragListeGrunnlag.justertBPsAndelUnderholdskostnadPeriodeListe)
            .addBruddpunkter(beregnKostnadsberegnetBidragListeGrunnlag.justertSamvaersfradragPeriodeListe)
            .finnPerioder(beregnDatoFom = periodeGrunnlag.beregnDatoFra, beregnDatoTil = periodeGrunnlag.beregnDatoTil)
            .toMutableList()
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    private fun beregnKostnadsberegnetBidragPerPeriode(grunnlag: BeregnKostnadsberegnetBidragListeGrunnlag, soknadsbarnPersonId: Int) {
        grunnlag.bruddPeriodeListe.forEach { beregningsperiode ->

            val underholdskostnad = grunnlag.justertUnderholdskostnadPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Underholdskostnad(referanse = it.referanse, belop = it.belop) }
                .firstOrNull()

            val bPsAndelUnderholdskostnad = grunnlag.justertBPsAndelUnderholdskostnadPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { BPsAndelUnderholdskostnad(referanse = it.referanse, andelProsent = it.andelProsent) }
                .firstOrNull()

            val samvaersfradrag = grunnlag.justertSamvaersfradragPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Samvaersfradrag(referanse = it.referanse, belop = it.belop) }
                .firstOrNull()

            // Kaller beregningsmodulen for hver beregningsperiode
            val grunnlagBeregning = GrunnlagBeregning(
                underholdskostnad = underholdskostnad!!,
                bPsAndelUnderholdskostnad = bPsAndelUnderholdskostnad!!,
                samvaersfradrag = samvaersfradrag!!
            )

            grunnlag.periodeResultatListe.add(
                ResultatPeriode(
                    soknadsbarnPersonId = soknadsbarnPersonId,
                    periode = beregningsperiode,
                    resultat = kostnadsberegnetBidragBeregning.beregn(grunnlagBeregning),
                    grunnlag = grunnlagBeregning
                )
            )
        }
    }

    // Validerer at input-verdier er gyldige
    override fun validerInput(grunnlag: BeregnKostnadsberegnetBidragGrunnlag): List<Avvik> {
        val avvikListe = PeriodeUtil.validerBeregnPeriodeInput(beregnDatoFra = grunnlag.beregnDatoFra, beregnDatoTil = grunnlag.beregnDatoTil).toMutableList()

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "underholdskostnadPeriodeListe",
                periodeListe = grunnlag.underholdskostnadPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = true,
                sjekkOpphold = true,
                sjekkNull = false,
                sjekkBeregnPeriode = true
            )
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "bPsAndelUnderholdskostnadPeriodeListe",
                periodeListe = grunnlag.bPsAndelUnderholdskostnadPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = true,
                sjekkOpphold = true,
                sjekkNull = false,
                sjekkBeregnPeriode = true
            )
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "samværsfradragPeriodeListe",
                periodeListe = grunnlag.samvaersfradragPeriodeListe!!.map { it.getPeriode() },
                sjekkOverlapp = false,
                sjekkOpphold = false,
                sjekkNull = false,
                sjekkBeregnPeriode = false
            )
        )

        return avvikListe
    }
}
