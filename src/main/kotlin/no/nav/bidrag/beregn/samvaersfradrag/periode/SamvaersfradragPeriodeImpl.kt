package no.nav.bidrag.beregn.samvaersfradrag.periode

import no.nav.bidrag.beregn.felles.FellesPeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerBeregnPeriodeInput
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerInputDatoer
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregning
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnetSamvaersfradragResultat
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode
import no.nav.bidrag.beregn.samvaersfradrag.bo.Samvaersklasse
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersklassePeriode
import no.nav.bidrag.beregn.samvaersfradrag.bo.SoknadsbarnAlder
import java.time.Period

class SamvaersfradragPeriodeImpl(private val samvaersfradragBeregning: SamvaersfradragBeregning) : FellesPeriode(), SamvaersfradragPeriode {
    override fun beregnPerioder(grunnlag: BeregnSamvaersfradragGrunnlag): BeregnetSamvaersfradragResultat {
        val resultatPeriodeListe = mutableListOf<ResultatPeriode>()

        // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::it)
        val justertSamvaersklassePeriodeListe =
            grunnlag.samvaersklassePeriodeListe
                .map { SamvaersklassePeriode(it) }

        val justertSjablonPeriodeListe =
            grunnlag.sjablonPeriodeListe
                .map { SjablonPeriode(it) }

        // Lager liste for å sikre brudd på barnets fødselsdato. Brudd-dato blir den 1. i påfølgende måned. Gjøres for hele beregningsperioden
        val bruddlisteBarnAlder = settBruddListeBarnAlder(grunnlag)

        // Bygger opp liste over perioder
        val perioder =
            Periodiserer()
                .addBruddpunkt(grunnlag.beregnDatoFra) // For å sikre bruddpunkt på start-beregning-fra-dato
                .addBruddpunkt(grunnlag.beregnDatoTil) // For å sikre bruddpunkt på start-beregning-til-dato
                .addBruddpunkter(bruddlisteBarnAlder)
                .addBruddpunkter(justertSamvaersklassePeriodeListe)
                .addBruddpunkter(justertSjablonPeriodeListe)
                .finnPerioder(grunnlag.beregnDatoFra, grunnlag.beregnDatoTil)
                .toMutableList()

        // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
        mergeSluttperiode(periodeListe = perioder, datoTil = grunnlag.beregnDatoTil)

        // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
        perioder.forEach { beregningsperiode ->

            val soknadsbarnAlder =
                SoknadsbarnAlder(
                    referanse = grunnlag.soknadsbarn.referanse,
                    alder = Period.between(grunnlag.soknadsbarn.fodselsdato, beregningsperiode.datoFom).years,
                )

            val samvaersklasse =
                justertSamvaersklassePeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map { Samvaersklasse(referanse = it.referanse, samvaersklasse = it.samvaersklasse) }
                    .firstOrNull()

            val sjablonliste =
                justertSjablonPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }

            // Kaller beregningsmodulen for hver beregningsperiode
            val grunnlagBeregning =
                GrunnlagBeregning(
                    soknadsbarn = soknadsbarnAlder,
                    samvaersklasse = samvaersklasse!!,
                    sjablonListe = sjablonliste,
                )

            resultatPeriodeListe.add(
                ResultatPeriode(
                    soknadsbarnPersonId = grunnlag.soknadsbarn.personId,
                    periode = beregningsperiode,
                    resultat = samvaersfradragBeregning.beregn(grunnlagBeregning),
                    grunnlag = grunnlagBeregning,
                ),
            )
        }

        return BeregnetSamvaersfradragResultat(resultatPeriodeListe)
    }

    // Lager liste for å sikre brudd på barnets fødselsdato. Brudd-dato blir den 1. i påfølgende måned. Gjøres for hele beregningsperioden
    private fun settBruddListeBarnAlder(grunnlag: BeregnSamvaersfradragGrunnlag): List<Periode> {
        var bruddDatoAlder =
            if (grunnlag.soknadsbarn.fodselsdato.dayOfMonth == 1) {
                grunnlag.soknadsbarn.fodselsdato.withYear(grunnlag.beregnDatoFra.year)
            } else {
                grunnlag.soknadsbarn.fodselsdato.plusMonths(1).withYear(grunnlag.beregnDatoFra.year).withDayOfMonth(1)
            }

        if (bruddDatoAlder.isBefore(grunnlag.beregnDatoFra)) {
            bruddDatoAlder = bruddDatoAlder.plusYears(1)
        }

        // Bygger opp liste med bruddpunkter i perioden mellom beregnFraDato og beregnTilDato.
        // Passer også på å ikke legge til bruddpunkt etter beregnTilDato.
        val bruddlisteBarnAlder = mutableListOf<Periode>()
        bruddlisteBarnAlder.add(Periode(bruddDatoAlder, bruddDatoAlder))

        while (bruddDatoAlder.plusYears(1).isBefore(grunnlag.beregnDatoTil)) {
            bruddDatoAlder = bruddDatoAlder.plusYears(1)
            bruddlisteBarnAlder.add(Periode(bruddDatoAlder, bruddDatoAlder))
        }

        return bruddlisteBarnAlder
    }

    // Validerer at input-verdier er gyldige
    override fun validerInput(grunnlag: BeregnSamvaersfradragGrunnlag): List<Avvik> {
        val avvikListe =
            validerBeregnPeriodeInput(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
            ).toMutableList()

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "samvaersklassePeriodeListe",
                periodeListe = grunnlag.samvaersklassePeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = true,
                sjekkOppholdMellomPerioder = true,
                sjekkDatoTilNull = true,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "sjablonPeriodeListe",
                periodeListe = grunnlag.sjablonPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = true,
                sjekkDatoTilNull = false,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        return avvikListe
    }
}
