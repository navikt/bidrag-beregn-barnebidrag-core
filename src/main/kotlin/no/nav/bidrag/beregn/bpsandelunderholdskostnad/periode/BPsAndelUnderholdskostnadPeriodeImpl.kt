package no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode

import no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning.BPsAndelUnderholdskostnadBeregning
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadListeGrunnlag
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnetBPsAndelUnderholdskostnadResultat
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntektPeriode
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Underholdskostnad
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.UnderholdskostnadPeriode
import no.nav.bidrag.beregn.felles.FellesPeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.inntekt.InntektPeriodeGrunnlagUtenInntektType
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.felles.util.InntektUtil.behandlUtvidetBarnetrygd
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerBeregnPeriodeInput
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerInputDatoer
import java.time.LocalDate

class BPsAndelUnderholdskostnadPeriodeImpl(private val bPsAndelUnderholdskostnadBeregning: BPsAndelUnderholdskostnadBeregning) :
    FellesPeriode(),
    BPsAndelUnderholdskostnadPeriode {
    private val regelendringsdato = LocalDate.parse("2009-01-01")

    override fun beregnPerioder(grunnlag: BeregnBPsAndelUnderholdskostnadGrunnlag): BeregnetBPsAndelUnderholdskostnadResultat {
        val beregnBPsAndelUnderholdskostnadListeGrunnlag = BeregnBPsAndelUnderholdskostnadListeGrunnlag()

        // Juster datoer
        justerDatoerGrunnlagslister(
            periodeGrunnlag = grunnlag,
            beregnBPsAndelUnderholdskostnadListeGrunnlag = beregnBPsAndelUnderholdskostnadListeGrunnlag,
        )

        // Lag bruddperioder
        lagBruddperioder(
            periodeGrunnlag = grunnlag,
            beregnBPsAndelUnderholdskostnadListeGrunnlag = beregnBPsAndelUnderholdskostnadListeGrunnlag,
        )

        // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
        mergeSluttperiode(periodeListe = beregnBPsAndelUnderholdskostnadListeGrunnlag.bruddPeriodeListe, datoTil = grunnlag.beregnDatoTil)

        // Foreta beregning
        beregnBPsAndelUnderholdskostnadPerPeriode(
            grunnlag = beregnBPsAndelUnderholdskostnadListeGrunnlag,
            soknadsbarnPersonId = grunnlag.soknadsbarnPersonId,
        )

        return BeregnetBPsAndelUnderholdskostnadResultat(beregnBPsAndelUnderholdskostnadListeGrunnlag.periodeResultatListe)
    }

    private fun justerDatoerGrunnlagslister(
        periodeGrunnlag: BeregnBPsAndelUnderholdskostnadGrunnlag,
        beregnBPsAndelUnderholdskostnadListeGrunnlag: BeregnBPsAndelUnderholdskostnadListeGrunnlag,
    ) {
        // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode(it))
        beregnBPsAndelUnderholdskostnadListeGrunnlag.justertSjablonPeriodeListe =
            periodeGrunnlag.sjablonPeriodeListe
                .map { SjablonPeriode(it) }

        beregnBPsAndelUnderholdskostnadListeGrunnlag.justertUnderholdskostnadPeriodeListe =
            periodeGrunnlag.underholdskostnadPeriodeListe
                .map { UnderholdskostnadPeriode(it) }

        beregnBPsAndelUnderholdskostnadListeGrunnlag.justertInntektBPPeriodeListe =
            periodeGrunnlag.inntektBPPeriodeListe
                .map { InntektPeriode(it) }

        beregnBPsAndelUnderholdskostnadListeGrunnlag.justertInntektBMPeriodeListe =
            behandlUtvidetBarnetrygd(periodeGrunnlag.inntektBMPeriodeListe, beregnBPsAndelUnderholdskostnadListeGrunnlag.justertSjablonPeriodeListe)
                .map { InntektPeriode(it) }

        beregnBPsAndelUnderholdskostnadListeGrunnlag.justertInntektBBPeriodeListe =
            periodeGrunnlag.inntektBBPeriodeListe
                .map { InntektPeriode(it) }
    }

    // Lager bruddperioder ved å løpe gjennom alle periodelistene
    private fun lagBruddperioder(
        periodeGrunnlag: BeregnBPsAndelUnderholdskostnadGrunnlag,
        beregnBPsAndelUnderholdskostnadListeGrunnlag: BeregnBPsAndelUnderholdskostnadListeGrunnlag,
    ) {
        // Bygger opp liste over perioder
        beregnBPsAndelUnderholdskostnadListeGrunnlag.bruddPeriodeListe =
            Periodiserer()
                .addBruddpunkt(periodeGrunnlag.beregnDatoFra) // For å sikre bruddpunkt på start-beregning-fra-dato
                .addBruddpunkt(periodeGrunnlag.beregnDatoTil) // For å sikre bruddpunkt på start-beregning-til-dato
                .addBruddpunkter(
                    Periode(
                        datoFom = regelendringsdato,
                        datoTil = regelendringsdato,
                    ),
                ) // Regler for beregning av BPs andel av underholdskostnad ble endret fra 01.01.2009. Det må derfor legges til brudd på denne datoen.
                .addBruddpunkter(beregnBPsAndelUnderholdskostnadListeGrunnlag.justertUnderholdskostnadPeriodeListe)
                .addBruddpunkter(beregnBPsAndelUnderholdskostnadListeGrunnlag.justertInntektBPPeriodeListe)
                .addBruddpunkter(beregnBPsAndelUnderholdskostnadListeGrunnlag.justertInntektBMPeriodeListe)
                .addBruddpunkter(beregnBPsAndelUnderholdskostnadListeGrunnlag.justertInntektBBPeriodeListe)
                .addBruddpunkter(beregnBPsAndelUnderholdskostnadListeGrunnlag.justertSjablonPeriodeListe)
                .finnPerioder(beregnDatoFom = periodeGrunnlag.beregnDatoFra, beregnDatoTil = periodeGrunnlag.beregnDatoTil)
                .toMutableList()
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    private fun beregnBPsAndelUnderholdskostnadPerPeriode(grunnlag: BeregnBPsAndelUnderholdskostnadListeGrunnlag, soknadsbarnPersonId: Int) {
        grunnlag.bruddPeriodeListe.forEach { beregningsperiode ->

            val underholdskostnad =
                grunnlag.justertUnderholdskostnadPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map { Underholdskostnad(referanse = it.referanse, belop = it.belop) }
                    .firstOrNull()

            val inntektBPListe =
                grunnlag.justertInntektBPPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map {
                        Inntekt(
                            referanse = it.referanse,
                            type = it.type,
                            belop = it.belop,
                            deltFordel = it.deltFordel,
                            skatteklasse2 = it.skatteklasse2,
                        )
                    }

            val inntektBMListe =
                grunnlag.justertInntektBMPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map {
                        Inntekt(
                            referanse = it.referanse,
                            type = it.type,
                            belop = it.belop,
                            deltFordel = it.deltFordel,
                            skatteklasse2 = it.skatteklasse2,
                        )
                    }

            val inntektBBListe =
                grunnlag.justertInntektBBPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map {
                        Inntekt(
                            referanse = it.referanse,
                            type = it.type,
                            belop = it.belop,
                            deltFordel = it.deltFordel,
                            skatteklasse2 = it.skatteklasse2,
                        )
                    }

            val sjablonListe =
                grunnlag.justertSjablonPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }

            // Kaller beregningsmodulen for hver beregningsperiode
            val grunnlagBeregning =
                GrunnlagBeregning(
                    underholdskostnad = underholdskostnad!!,
                    inntektBPListe = inntektBPListe,
                    inntektBMListe = inntektBMListe,
                    inntektBBListe = inntektBBListe,
                    sjablonListe = sjablonListe,
                )

            // Beregner med gamle regler hvis periodens beregntilogmeddato er 01.01.2009 eller tidligere
            // Med gamle regler skal beregnet andelProsent rundes av til nærmeste sjettedel, men ikke over 5/6
            val brukNyeRegler =
                beregningsperiode.getPeriode().datoTil == null ||
                    beregningsperiode.getPeriode().datoTil!!.isAfter(regelendringsdato)

            grunnlag.periodeResultatListe.add(
                ResultatPeriode(
                    soknadsbarnPersonId = soknadsbarnPersonId,
                    periode = beregningsperiode,
                    resultat = bPsAndelUnderholdskostnadBeregning.beregn(grunnlag = grunnlagBeregning, beregnMedNyeRegler = brukNyeRegler),
                    grunnlag = grunnlagBeregning,
                ),
            )
        }
    }

    // Sjekker om det skal legges til inntekt for fordel særfradrag enslig forsørger og skatteklasse 2 (kun BM)
    private fun behandlUtvidetBarnetrygd(
        inntektPeriodeListe: List<InntektPeriode>,
        sjablonPeriodeListe: List<SjablonPeriode>,
    ): List<InntektPeriode> {
        if (inntektPeriodeListe.isEmpty()) {
            return inntektPeriodeListe
        }

        val justertInntektPeriodeListe =
            behandlUtvidetBarnetrygd(
                inntektPeriodeGrunnlagListe =
                inntektPeriodeListe
                    .map {
                        InntektPeriodeGrunnlagUtenInntektType(
                            referanse = it.referanse,
                            inntektPeriode = it.getPeriode(),
                            type = it.type,
                            belop = it.belop,
                            deltFordel = it.deltFordel,
                            skatteklasse2 = it.skatteklasse2,
                        )
                    },
                sjablonPeriodeListe = sjablonPeriodeListe,
            )

        return justertInntektPeriodeListe
            .map {
                InntektPeriode(
                    referanse = it.referanse,
                    inntektPeriode = it.getPeriode(),
                    type = it.type,
                    belop = it.belop,
                    deltFordel = it.deltFordel,
                    skatteklasse2 = it.skatteklasse2,
                )
            }
            .sortedBy { it.inntektPeriode.datoFom }
    }

    // Validerer at input-verdier er gyldige
    override fun validerInput(grunnlag: BeregnBPsAndelUnderholdskostnadGrunnlag): List<Avvik> {
        val avvikListe =
            validerBeregnPeriodeInput(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
            ).toMutableList()

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "underholdskostnadPeriodeListe",
                periodeListe = grunnlag.underholdskostnadPeriodeListe.map { it.getPeriode() },
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
                dataElement = "inntektBPPeriodeListe",
                periodeListe = grunnlag.inntektBPPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = true,
                sjekkDatoTilNull = false,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "inntektBMPeriodeListe",
                periodeListe = grunnlag.inntektBMPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = true,
                sjekkDatoTilNull = false,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "inntektBBPeriodeListe",
                periodeListe = grunnlag.inntektBBPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = true,
                sjekkDatoTilNull = false,
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
