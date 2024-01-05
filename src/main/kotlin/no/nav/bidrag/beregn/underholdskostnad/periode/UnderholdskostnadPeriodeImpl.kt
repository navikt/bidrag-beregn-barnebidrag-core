package no.nav.bidrag.beregn.underholdskostnad.periode

import no.nav.bidrag.beregn.felles.FellesPeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerBeregnPeriodeInput
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerInputDatoer
import no.nav.bidrag.beregn.underholdskostnad.beregning.UnderholdskostnadBeregning
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadListeGrunnlag
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnetUnderholdskostnadResultat
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgift
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsyn
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatPeriode
import no.nav.bidrag.beregn.underholdskostnad.bo.SoknadsbarnAlder
import java.time.LocalDate
import java.time.Period

open class UnderholdskostnadPeriodeImpl(
    private val underholdskostnadBeregning: UnderholdskostnadBeregning,
) : FellesPeriode(), UnderholdskostnadPeriode {

    override fun beregnPerioder(grunnlag: BeregnUnderholdskostnadGrunnlag): BeregnetUnderholdskostnadResultat {
        val beregnUnderholdskostnadListeGrunnlag = BeregnUnderholdskostnadListeGrunnlag()

        // Juster datoer
        justerDatoerGrunnlagslister(periodeGrunnlag = grunnlag, beregnUnderholdskostnadListeGrunnlag = beregnUnderholdskostnadListeGrunnlag)

        // Barnetrygd skal ikke trekkes fra i barnets fødselsmåned, må derfor lage denne måneden som egen periode
        val soknadsbarnFodselsmaaned =
            Periode(
                datoFom = grunnlag.soknadsbarn.fodselsdato.withDayOfMonth(1),
                datoTil = grunnlag.soknadsbarn.fodselsdato.withDayOfMonth(1).plusMonths(1),
            )

        // Ny sjablon forhøyet barnetrygd for barn til og med fem år inntrer fra 01.07.2021. Det må derfor legges til brudd på denne datoen
        val datoRegelendringer = Periode(datoFom = LocalDate.parse("2021-07-01"), datoTil = LocalDate.parse("2021-07-01"))

        // For å beregne 6-års bruddato brukes 01.07. som fødselsdato.
        // Datoen brukes til å skape brudd på 6-årsdag, og til å sjekke om ordinær eller forhøyet barnetrygd skal brukes.
        val seksAarBruddDato = grunnlag.soknadsbarn.fodselsdato.plusYears(6).withMonth(7).withDayOfMonth(1)
        val seksAarBruddPeriode = Periode(datoFom = seksAarBruddDato, datoTil = seksAarBruddDato)

        // Lag bruddperioder
        lagBruddperioder(
            periodeGrunnlag = grunnlag,
            beregnUnderholdskostnadListeGrunnlag = beregnUnderholdskostnadListeGrunnlag,
            soknadsbarnFodselsmaaned = soknadsbarnFodselsmaaned,
            seksAarBruddPeriode = seksAarBruddPeriode,
            datoRegelendringer = datoRegelendringer,
            bruddlisteBarnAlder = bruddVedAlder(grunnlag),
        )

        // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
        mergeSluttperiode(periodeListe = beregnUnderholdskostnadListeGrunnlag.bruddPeriodeListe, datoTil = grunnlag.beregnDatoTil)

        // Foreta beregning
        beregnUnderholdskostnadPerPeriode(
            beregnUnderholdskostnadListeGrunnlag = beregnUnderholdskostnadListeGrunnlag,
            grunnlag = grunnlag,
            soknadsbarnFodselsmaaned = soknadsbarnFodselsmaaned,
            datoRegelendringer = datoRegelendringer,
            seksAarBruddDato = seksAarBruddDato,
        )

        return BeregnetUnderholdskostnadResultat(beregnUnderholdskostnadListeGrunnlag.periodeResultatListe)
    }

    private fun justerDatoerGrunnlagslister(
        periodeGrunnlag: BeregnUnderholdskostnadGrunnlag,
        beregnUnderholdskostnadListeGrunnlag: BeregnUnderholdskostnadListeGrunnlag,
    ) {
        // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode(it))
        beregnUnderholdskostnadListeGrunnlag.justertBarnetilsynMedStonadPeriodeListe =
            periodeGrunnlag.barnetilsynMedStonadPeriodeListe
                .map { BarnetilsynMedStonadPeriode(it) }

        beregnUnderholdskostnadListeGrunnlag.justertNettoBarnetilsynPeriodeListe =
            periodeGrunnlag.nettoBarnetilsynPeriodeListe
                .map { NettoBarnetilsynPeriode(it) }

        beregnUnderholdskostnadListeGrunnlag.justertForpleiningUtgiftPeriodeListe =
            periodeGrunnlag.forpleiningUtgiftPeriodeListe
                .map { ForpleiningUtgiftPeriode(it) }

        beregnUnderholdskostnadListeGrunnlag.justertSjablonPeriodeListe =
            periodeGrunnlag.sjablonPeriodeListe
                .map { SjablonPeriode(it) }
    }

    // Barnets fødselsdag og måned skal overstyres til 01.07.
    // Lager liste for å sikre brudd ved ny alder fra 01.07 hvert år i beregningsperioden (ifht. henting av riktige sjablonverdier).
    private fun bruddVedAlder(grunnlag: BeregnUnderholdskostnadGrunnlag): List<Periode> {
        val bruddlisteBarnAlder = mutableListOf<Periode>()
        var tellerAar = grunnlag.beregnDatoFra.year

        // Lager liste med bruddpunker i perioden mellom beregnFraDato og beregnTilDato. Det skal ikke legges til bruddpunkt etter beregnTilDato.
        while (tellerAar <= grunnlag.beregnDatoTil.year && grunnlag.beregnDatoTil.isAfter(LocalDate.of(tellerAar, 7, 1))) {
            bruddlisteBarnAlder.add(Periode(LocalDate.of(tellerAar, 7, 1), LocalDate.of(tellerAar, 7, 1)))
            tellerAar++
        }

        return bruddlisteBarnAlder
    }

    // Lager bruddperioder ved å løpe gjennom alle periodelistene
    private fun lagBruddperioder(
        periodeGrunnlag: BeregnUnderholdskostnadGrunnlag,
        beregnUnderholdskostnadListeGrunnlag: BeregnUnderholdskostnadListeGrunnlag,
        soknadsbarnFodselsmaaned: Periode,
        seksAarBruddPeriode: Periode,
        datoRegelendringer: Periode,
        bruddlisteBarnAlder: List<Periode>,
    ) {
        // Bygger opp liste over perioder
        beregnUnderholdskostnadListeGrunnlag.bruddPeriodeListe =
            Periodiserer()
                .addBruddpunkt(periodeGrunnlag.beregnDatoFra) // For å sikre bruddpunkt på start-beregning-fra-dato
                .addBruddpunkt(periodeGrunnlag.beregnDatoTil) // For å sikre bruddpunkt på start-beregning-til-dato
                .addBruddpunkter(soknadsbarnFodselsmaaned)
                .addBruddpunkter(seksAarBruddPeriode)
                .addBruddpunkter(datoRegelendringer)
                .addBruddpunkter(bruddlisteBarnAlder)
                .addBruddpunkter(beregnUnderholdskostnadListeGrunnlag.justertBarnetilsynMedStonadPeriodeListe)
                .addBruddpunkter(beregnUnderholdskostnadListeGrunnlag.justertNettoBarnetilsynPeriodeListe)
                .addBruddpunkter(beregnUnderholdskostnadListeGrunnlag.justertForpleiningUtgiftPeriodeListe)
                .addBruddpunkter(beregnUnderholdskostnadListeGrunnlag.justertSjablonPeriodeListe)
                .finnPerioder(beregnDatoFom = periodeGrunnlag.beregnDatoFra, beregnDatoTil = periodeGrunnlag.beregnDatoTil)
                .toMutableList()
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    private fun beregnUnderholdskostnadPerPeriode(
        beregnUnderholdskostnadListeGrunnlag: BeregnUnderholdskostnadListeGrunnlag,
        grunnlag: BeregnUnderholdskostnadGrunnlag,
        soknadsbarnFodselsmaaned: Periode,
        datoRegelendringer: Periode,
        seksAarBruddDato: LocalDate,
    ) {
        beregnUnderholdskostnadListeGrunnlag.bruddPeriodeListe.forEach { beregningsperiode ->

            val barnetilsynMedStonad =
                beregnUnderholdskostnadListeGrunnlag.justertBarnetilsynMedStonadPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map { BarnetilsynMedStonad(referanse = it.referanse, tilsynType = it.tilsynType, stonadType = it.stonadType) }
                    .firstOrNull()

            val nettoBarnetilsyn =
                beregnUnderholdskostnadListeGrunnlag.justertNettoBarnetilsynPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map { NettoBarnetilsyn(referanse = it.referanse, belop = it.belop) }
                    .firstOrNull()

            val forpleiningUtgift =
                beregnUnderholdskostnadListeGrunnlag.justertForpleiningUtgiftPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map { ForpleiningUtgift(referanse = it.referanse, belop = it.belop) }
                    .firstOrNull()

            val soknadsbarnAlder =
                SoknadsbarnAlder(
                    referanse = grunnlag.soknadsbarn.referanse,
                    alder =
                    Period.between(
                        grunnlag.soknadsbarn.fodselsdato.withDayOfMonth(1).withMonth(7),
                        beregningsperiode.datoFom,
                    ).years,
                )

            val sjablonListe =
                beregnUnderholdskostnadListeGrunnlag.justertSjablonPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }

            // Kaller beregningsmodulen for hver beregningsperiode
            val grunnlagBeregning =
                GrunnlagBeregning(
                    soknadsbarn = soknadsbarnAlder,
                    barnetilsynMedStonad = barnetilsynMedStonad!!,
                    nettoBarnetilsyn = nettoBarnetilsyn!!,
                    forpleiningUtgift = forpleiningUtgift!!,
                    sjablonListe = sjablonListe,
                )

            beregnUnderholdskostnadListeGrunnlag.periodeResultatListe.add(
                ResultatPeriode(
                    soknadsbarnPersonId = grunnlag.soknadsbarn.personId,
                    periode = beregningsperiode,
                    resultat =
                    beregnUnderholdskostnad(
                        grunnlag = grunnlagBeregning,
                        beregningsperiode = beregningsperiode,
                        soknadsbarnFodselsmaaned = soknadsbarnFodselsmaaned,
                        datoRegelendringer = datoRegelendringer,
                        seksaarsbruddato = seksAarBruddDato,
                    ),
                    grunnlag = grunnlagBeregning,
                ),
            )
        }
    }

    // Velger mellom 3 metoder for å beregne:
    // 1. Uten barnetrygd - Barnetrygd skal ikke trekkes fra i barnets fødselsmåned
    // 2. Ordinær barnetrygd - Beregner med ordinær barnetrygd
    // 3. Forhøyet barnetrygd - Beregner med forhøyet barnetrygd
    private fun beregnUnderholdskostnad(
        grunnlag: GrunnlagBeregning,
        beregningsperiode: Periode,
        soknadsbarnFodselsmaaned: Periode,
        datoRegelendringer: Periode,
        seksaarsbruddato: LocalDate,
    ) = when {
        beregningsperiode.datoFom == soknadsbarnFodselsmaaned.datoFom -> {
            underholdskostnadBeregning.beregn(grunnlag = grunnlag, barnetrygdIndikator = UTEN_BARNETRYGD)
        }

        beregningsperiode.datoFom.isBefore(datoRegelendringer.datoFom) -> {
            underholdskostnadBeregning.beregn(grunnlag = grunnlag, barnetrygdIndikator = ORDINAER_BARNETRYGD)
        }

        beregningsperiode.datoFom.isBefore(seksaarsbruddato) -> {
            underholdskostnadBeregning.beregn(grunnlag = grunnlag, barnetrygdIndikator = FORHOYET_BARNETRYGD)
        }

        else -> {
            underholdskostnadBeregning.beregn(grunnlag = grunnlag, barnetrygdIndikator = ORDINAER_BARNETRYGD)
        }
    }

    // Validerer at input-verdier er gyldige
    override fun validerInput(grunnlag: BeregnUnderholdskostnadGrunnlag): List<Avvik> {
        val avvikListe =
            validerBeregnPeriodeInput(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
            ).toMutableList()

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "barnetilsynMedStonadPeriodeListe",
                periodeListe = grunnlag.barnetilsynMedStonadPeriodeListe.map { it.getPeriode() },
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
                dataElement = "nettoBarnetilsynPeriodeListe",
                periodeListe = grunnlag.nettoBarnetilsynPeriodeListe.map { it.getPeriode() },
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
                dataElement = "forpleiningUtgiftPeriodeListe",
                periodeListe = grunnlag.forpleiningUtgiftPeriodeListe.map { it.getPeriode() },
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

    companion object {
        protected const val UTEN_BARNETRYGD = " "
        protected const val ORDINAER_BARNETRYGD = "O"
        protected const val FORHOYET_BARNETRYGD = "F"
    }
}
