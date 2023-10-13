package no.nav.bidrag.beregn.bidragsevne.periode

import no.nav.bidrag.beregn.bidragsevne.beregning.BidragsevneBeregning
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstand
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstandPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneListeGrunnlag
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnetBidragsevneResultat
import no.nav.bidrag.beregn.bidragsevne.bo.Bostatus
import no.nav.bidrag.beregn.bidragsevne.bo.BostatusPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt
import no.nav.bidrag.beregn.bidragsevne.bo.InntektPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.Saerfradrag
import no.nav.bidrag.beregn.bidragsevne.bo.SaerfradragPeriode
import no.nav.bidrag.beregn.bidragsevne.bo.Skatteklasse
import no.nav.bidrag.beregn.bidragsevne.bo.SkatteklassePeriode
import no.nav.bidrag.beregn.felles.FellesPeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerBeregnPeriodeInput
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerInputDatoer

class BidragsevnePeriodeImpl(private val bidragsevneberegning: BidragsevneBeregning) : FellesPeriode(), BidragsevnePeriode {
    override fun beregnPerioder(grunnlag: BeregnBidragsevneGrunnlag): BeregnetBidragsevneResultat {
        val beregnBidragsevneListeGrunnlag = BeregnBidragsevneListeGrunnlag()

        // Juster datoer
        justerDatoerGrunnlagslister(periodeGrunnlag = grunnlag, beregnBidragsevneListeGrunnlag = beregnBidragsevneListeGrunnlag)

        // Lag bruddperioder
        lagBruddperioder(periodeGrunnlag = grunnlag, beregnBidragsevneListeGrunnlag = beregnBidragsevneListeGrunnlag)

        // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
        mergeSluttperiode(periodeListe = beregnBidragsevneListeGrunnlag.bruddPeriodeListe, datoTil = grunnlag.beregnDatoTil)

        // Foreta beregning
        beregnBidragsevnePerPeriode(beregnBidragsevneListeGrunnlag)

        return BeregnetBidragsevneResultat(beregnBidragsevneListeGrunnlag.periodeResultatListe)
    }

    private fun justerDatoerGrunnlagslister(
        periodeGrunnlag: BeregnBidragsevneGrunnlag,
        beregnBidragsevneListeGrunnlag: BeregnBidragsevneListeGrunnlag
    ) {
        // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode(it))
        beregnBidragsevneListeGrunnlag.justertInntektPeriodeListe = periodeGrunnlag.inntektPeriodeListe
            .map { InntektPeriode(it) }

        beregnBidragsevneListeGrunnlag.justertSkatteklassePeriodeListe = periodeGrunnlag.skatteklassePeriodeListe
            .map { SkatteklassePeriode(it) }

        beregnBidragsevneListeGrunnlag.justertBostatusPeriodeListe = periodeGrunnlag.bostatusPeriodeListe
            .map { BostatusPeriode(it) }

        beregnBidragsevneListeGrunnlag.justertBarnIHusstandPeriodeListe = periodeGrunnlag.barnIHusstandPeriodeListe
            .map { BarnIHusstandPeriode(it) }

        beregnBidragsevneListeGrunnlag.justertSaerfradragPeriodeListe = periodeGrunnlag.saerfradragPeriodeListe
            .map { SaerfradragPeriode(it) }

        beregnBidragsevneListeGrunnlag.justertSjablonPeriodeListe = periodeGrunnlag.sjablonPeriodeListe
            .map { SjablonPeriode(it) }
    }

    // Lager bruddperioder ved å løpe gjennom alle periodelistene
    private fun lagBruddperioder(periodeGrunnlag: BeregnBidragsevneGrunnlag, beregnBidragsevneListeGrunnlag: BeregnBidragsevneListeGrunnlag) {
        // Bygger opp liste over perioder
        beregnBidragsevneListeGrunnlag.bruddPeriodeListe = Periodiserer()
            .addBruddpunkt(periodeGrunnlag.beregnDatoFra) // For å sikre bruddpunkt på start-beregning-fra-dato
            .addBruddpunkt(periodeGrunnlag.beregnDatoTil) // For å sikre bruddpunkt på start-beregning-til-dato
            .addBruddpunkter(beregnBidragsevneListeGrunnlag.justertInntektPeriodeListe)
            .addBruddpunkter(beregnBidragsevneListeGrunnlag.justertSkatteklassePeriodeListe)
            .addBruddpunkter(beregnBidragsevneListeGrunnlag.justertBostatusPeriodeListe)
            .addBruddpunkter(beregnBidragsevneListeGrunnlag.justertBarnIHusstandPeriodeListe)
            .addBruddpunkter(beregnBidragsevneListeGrunnlag.justertSaerfradragPeriodeListe)
            .addBruddpunkter(beregnBidragsevneListeGrunnlag.justertSjablonPeriodeListe)
            .finnPerioder(beregnDatoFom = periodeGrunnlag.beregnDatoFra, beregnDatoTil = periodeGrunnlag.beregnDatoTil)
            .toMutableList()
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    private fun beregnBidragsevnePerPeriode(grunnlag: BeregnBidragsevneListeGrunnlag) {

        grunnlag.bruddPeriodeListe.forEach { beregningsperiode ->

            val inntektListe = grunnlag.justertInntektPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Inntekt(referanse = it.referanse, type = it.type, belop = it.belop) }

            val skatteklasse = grunnlag.justertSkatteklassePeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Skatteklasse(referanse = it.referanse, skatteklasse = it.skatteklasse) }
                .firstOrNull()

            val bostatus = grunnlag.justertBostatusPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Bostatus(referanse = it.referanse, kode = it.kode) }
                .firstOrNull()

            val barnIHusstand = grunnlag.justertBarnIHusstandPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { BarnIHusstand(referanse = it.referanse, antallBarn = it.antallBarn) }
                .firstOrNull()

            val saerfradrag = grunnlag.justertSaerfradragPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Saerfradrag(referanse = it.referanse, kode = it.kode) }
                .firstOrNull()

            val sjablonliste = grunnlag.justertSjablonPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }

            // Kaller beregningsmodulen for hver beregningsperiode
            val grunnlagBeregning = GrunnlagBeregning(
                inntektListe = inntektListe,
                skatteklasse = skatteklasse!!,
                bostatus = bostatus!!,
                barnIHusstand = barnIHusstand!!,
                saerfradrag = saerfradrag!!,
                sjablonListe = sjablonliste
            )

            grunnlag.periodeResultatListe.add(
                ResultatPeriode(
                    periode = beregningsperiode,
                    resultat = bidragsevneberegning.beregn(grunnlagBeregning),
                    grunnlag = grunnlagBeregning
                )
            )
        }
    }

    // Validerer at input-verdier er gyldige
    override fun validerInput(grunnlag: BeregnBidragsevneGrunnlag): List<Avvik> {
        val avvikListe = validerBeregnPeriodeInput(beregnDatoFra = grunnlag.beregnDatoFra, beregnDatoTil = grunnlag.beregnDatoTil).toMutableList()

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "inntektPeriodeListe",
                periodeListe = grunnlag.inntektPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = false,
                sjekkOpphold = true,
                sjekkNull = false,
                sjekkBeregnPeriode = true
            )
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "skatteklassePeriodeListe",
                periodeListe = grunnlag.skatteklassePeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = true,
                sjekkOpphold = true,
                sjekkNull = true,
                sjekkBeregnPeriode = true
            )
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "bostatusPeriodeListe",
                periodeListe = grunnlag.bostatusPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = true,
                sjekkOpphold = true,
                sjekkNull = true,
                sjekkBeregnPeriode = true
            )
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "barnIHusstandPeriodeListe",
                periodeListe = grunnlag.barnIHusstandPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = false,
                sjekkOpphold = false,
                sjekkNull = false,
                sjekkBeregnPeriode = true
            )
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "saerfradragPeriodeListe",
                periodeListe = grunnlag.saerfradragPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = true,
                sjekkOpphold = true,
                sjekkNull = true,
                sjekkBeregnPeriode = true
            )
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "sjablonPeriodeListe",
                periodeListe = grunnlag.sjablonPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = false,
                sjekkOpphold = false,
                sjekkNull = false,
                sjekkBeregnPeriode = false
            )
        )

        return avvikListe
    }
}
