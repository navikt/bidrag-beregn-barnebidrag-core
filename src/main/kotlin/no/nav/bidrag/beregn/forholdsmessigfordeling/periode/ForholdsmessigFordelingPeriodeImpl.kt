package no.nav.bidrag.beregn.forholdsmessigfordeling.periode

import no.nav.bidrag.beregn.felles.FellesPeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerBeregnPeriodeInput
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerInputDatoer
import no.nav.bidrag.beregn.forholdsmessigfordeling.beregning.ForholdsmessigFordelingBeregning
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingGrunnlag
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingResultat
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSak
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSakPeriode
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.Bidragsevne
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BidragsevnePeriode
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPeriode

class ForholdsmessigFordelingPeriodeImpl(
    private val forholdsmessigFordelingBeregning: ForholdsmessigFordelingBeregning
) : FellesPeriode(), ForholdsmessigFordelingPeriode {

    override fun beregnPerioder(grunnlag: BeregnForholdsmessigFordelingGrunnlag): BeregnForholdsmessigFordelingResultat {
        val resultatPeriodeListe = mutableListOf<ResultatPeriode>()

        // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode(it))
        val justertBidragsevnePeriodeListe = grunnlag.bidragsevnePeriodeListe
            .map { BidragsevnePeriode(it) }

        val justertBeregnedeBidragSakPeriodeListe = grunnlag.beregnetBidragPeriodeListe
            .map { BeregnetBidragSakPeriode(it) }

        // Bygger opp liste over perioder
        val perioder = Periodiserer()
            .addBruddpunkt(grunnlag.beregnDatoFra) // For å sikre bruddpunkt på start-beregning-fra-dato
            .addBruddpunkt(grunnlag.beregnDatoTil) // For å sikre bruddpunkt på start-beregning-til-dato
            .addBruddpunkter(justertBidragsevnePeriodeListe)
            .addBruddpunkter(justertBeregnedeBidragSakPeriodeListe)
            .finnPerioder(beregnDatoFom = grunnlag.beregnDatoFra, beregnDatoTil = grunnlag.beregnDatoTil)
            .toMutableList()

        // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
        mergeSluttperiode(periodeListe = perioder, datoTil = grunnlag.beregnDatoTil)

        // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
        perioder.forEach { beregningsperiode ->

            val bidragsevne = justertBidragsevnePeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Bidragsevne(belop = it.bidragsevneBelop, tjuefemProsentInntekt = it.tjuefemProsentInntekt) }
                .firstOrNull()

            val beregnetBidragSakListe = justertBeregnedeBidragSakPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { BeregnetBidragSak(saksnr = it.saksnr, grunnlagPerBarnListe = it.grunnlagPerBarnListe) }

            // Kaller beregningsmodulen for hver beregningsperiode
            val grunnlagBeregningPeriodisert = GrunnlagBeregningPeriodisert(
                bidragsevne = bidragsevne!!,
                beregnetBidragSakListe = beregnetBidragSakListe
            )

            resultatPeriodeListe.add(
                ResultatPeriode(
                    periode = beregningsperiode,
                    resultatBeregningListe = forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert),
                    resultatGrunnlag = grunnlagBeregningPeriodisert
                )
            )
        }

        return BeregnForholdsmessigFordelingResultat(resultatPeriodeListe)
    }

    // Validerer at input-verdier er gyldige
    override fun validerInput(grunnlag: BeregnForholdsmessigFordelingGrunnlag): List<Avvik> {
        val avvikListe = validerBeregnPeriodeInput(beregnDatoFra = grunnlag.beregnDatoFra, beregnDatoTil = grunnlag.beregnDatoTil).toMutableList()

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "bidragsevnePeriodeListe",
                periodeListe = grunnlag.bidragsevnePeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = false,
                sjekkOpphold = false,
                sjekkNull = true,
                sjekkBeregnPeriode = true
            )
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "beregnetBidragSakPeriodeListe",
                periodeListe = grunnlag.beregnetBidragPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = false,
                sjekkOpphold = false,
                sjekkNull = true,
                sjekkBeregnPeriode = true
            )
        )

        return avvikListe
    }
}
