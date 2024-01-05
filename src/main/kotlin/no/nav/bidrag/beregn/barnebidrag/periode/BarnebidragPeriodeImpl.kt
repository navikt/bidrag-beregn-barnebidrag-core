package no.nav.bidrag.beregn.barnebidrag.periode

import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidrag
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidragPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnadPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaret
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaretPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragListeGrunnlag
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne
import no.nav.bidrag.beregn.barnebidrag.bo.BidragsevnePeriode
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBosted
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBostedPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatPeriode
import no.nav.bidrag.beregn.barnebidrag.bo.Samvaersfradrag
import no.nav.bidrag.beregn.barnebidrag.bo.SamvaersfradragPeriode
import no.nav.bidrag.beregn.felles.FellesPeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerBeregnPeriodeInput
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerInputDatoer
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class BarnebidragPeriodeImpl(private val barnebidragBeregning: BarnebidragBeregning) : FellesPeriode(), BarnebidragPeriode {
    override fun beregnPerioder(grunnlag: BeregnBarnebidragGrunnlag): BeregnBarnebidragResultat {
        val beregnBarnebidragListeGrunnlag = BeregnBarnebidragListeGrunnlag()

        // Juster datoer
        justerDatoerGrunnlagslister(periodeGrunnlag = grunnlag, beregnBarnebidragListeGrunnlag = beregnBarnebidragListeGrunnlag)

        // Lag bruddperioder
        lagBruddperioder(periodeGrunnlag = grunnlag, beregnBarnebidragListeGrunnlag = beregnBarnebidragListeGrunnlag)

        // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
        mergeSluttperiode(periodeListe = beregnBarnebidragListeGrunnlag.bruddPeriodeListe, datoTil = grunnlag.beregnDatoTil)

        // Foreta beregning
        beregnBarnebidragPerPeriode(beregnBarnebidragListeGrunnlag)

        return BeregnBarnebidragResultat(beregnBarnebidragListeGrunnlag.periodeResultatListe)
    }

    private fun justerDatoerGrunnlagslister(
        periodeGrunnlag: BeregnBarnebidragGrunnlag,
        beregnBarnebidragListeGrunnlag: BeregnBarnebidragListeGrunnlag,
    ) {
        // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode(it))
        beregnBarnebidragListeGrunnlag.justertBidragsevnePeriodeListe =
            periodeGrunnlag.bidragsevnePeriodeListe
                .map { BidragsevnePeriode(it) }

        beregnBarnebidragListeGrunnlag.justertBPsAndelUnderholdskostnadPeriodeListe =
            periodeGrunnlag.bPsAndelUnderholdskostnadPeriodeListe
                .map { BPsAndelUnderholdskostnadPeriode(it) }

        beregnBarnebidragListeGrunnlag.justertDeltBostedPeriodeListe =
            periodeGrunnlag.deltBostedPeriodeListe
                .map { DeltBostedPeriode(it) }

        beregnBarnebidragListeGrunnlag.justertSamvaersfradragPeriodeListe =
            periodeGrunnlag.samvaersfradragPeriodeListe
                .map { SamvaersfradragPeriode(it) }

        beregnBarnebidragListeGrunnlag.justertBarnetilleggBPPeriodeListe =
            periodeGrunnlag.barnetilleggBPPeriodeListe
                .map { BarnetilleggPeriode(it) }

        beregnBarnebidragListeGrunnlag.justertBarnetilleggBMPeriodeListe =
            periodeGrunnlag.barnetilleggBMPeriodeListe
                .map { BarnetilleggPeriode(it) }

        beregnBarnebidragListeGrunnlag.justertBarnetilleggForsvaretPeriodeListe =
            periodeGrunnlag.barnetilleggForsvaretPeriodeListe
                .map { BarnetilleggForsvaretPeriode(it) }

        beregnBarnebidragListeGrunnlag.justertAndreLopendeBidragPeriodeListe =
            periodeGrunnlag.andreLopendeBidragPeriodeListe
                .map { AndreLopendeBidragPeriode(it) }

        beregnBarnebidragListeGrunnlag.justertSjablonPeriodeListe =
            periodeGrunnlag.sjablonPeriodeListe
                .map { SjablonPeriode(it) }
    }

    // Lager bruddperioder ved å løpe gjennom alle periodelistene
    private fun lagBruddperioder(periodeGrunnlag: BeregnBarnebidragGrunnlag, beregnBarnebidragListeGrunnlag: BeregnBarnebidragListeGrunnlag) {
        // Bygger opp liste over perioder
        beregnBarnebidragListeGrunnlag.bruddPeriodeListe =
            Periodiserer()
                .addBruddpunkt(periodeGrunnlag.beregnDatoFra) // For å sikre bruddpunkt på start-beregning-fra-dato
                .addBruddpunkt(periodeGrunnlag.beregnDatoTil) // For å sikre bruddpunkt på start-beregning-til-dato
                .addBruddpunkter(beregnBarnebidragListeGrunnlag.justertBidragsevnePeriodeListe)
                .addBruddpunkter(beregnBarnebidragListeGrunnlag.justertBPsAndelUnderholdskostnadPeriodeListe)
                .addBruddpunkter(beregnBarnebidragListeGrunnlag.justertDeltBostedPeriodeListe)
                .addBruddpunkter(beregnBarnebidragListeGrunnlag.justertSamvaersfradragPeriodeListe)
                .addBruddpunkter(beregnBarnebidragListeGrunnlag.justertBarnetilleggBPPeriodeListe)
                .addBruddpunkter(beregnBarnebidragListeGrunnlag.justertBarnetilleggBMPeriodeListe)
                .addBruddpunkter(beregnBarnebidragListeGrunnlag.justertBarnetilleggForsvaretPeriodeListe)
                .addBruddpunkter(beregnBarnebidragListeGrunnlag.justertAndreLopendeBidragPeriodeListe)
                .addBruddpunkter(beregnBarnebidragListeGrunnlag.justertSjablonPeriodeListe)
                .finnPerioder(beregnDatoFom = periodeGrunnlag.beregnDatoFra, beregnDatoTil = periodeGrunnlag.beregnDatoTil)
                .toMutableList()
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    private fun beregnBarnebidragPerPeriode(grunnlag: BeregnBarnebidragListeGrunnlag) {
        grunnlag.bruddPeriodeListe.forEach { beregningsperiode ->

            val soknadsbarnPersonIdListe = lagSoknadsbarnPersonIdListe(grunnlag = grunnlag, periode = beregningsperiode)

            val bidragsevne =
                grunnlag.justertBidragsevnePeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map {
                        Bidragsevne(
                            referanse = it.referanse,
                            bidragsevneBelop = it.belop,
                            tjuefemProsentInntekt = it.tjuefemProsentInntekt,
                        )
                    }
                    .firstOrNull()

            val barnetilleggForsvaret =
                grunnlag.justertBarnetilleggForsvaretPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map {
                        BarnetilleggForsvaret(
                            referanse = it.referanse,
                            barnetilleggForsvaretIPeriode = it.barnetilleggForsvaretIPeriode,
                        )
                    }
                    .firstOrNull()

            val andreLopendeBidragListe =
                grunnlag.justertAndreLopendeBidragPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                    .map {
                        AndreLopendeBidrag(
                            referanse = it.referanse,
                            barnPersonId = it.barnPersonId,
                            bidragBelop = it.bidragBelop,
                            samvaersfradragBelop = it.samvaersfradragBelop,
                        )
                    }

            val grunnlagBeregningPerBarnListe = mutableListOf<GrunnlagBeregningPerBarn>()

            soknadsbarnPersonIdListe.forEach { soknadsbarnPersonId ->

                var bPsAndelUnderholdskostnad =
                    grunnlag.justertBPsAndelUnderholdskostnadPeriodeListe
                        .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                        .filter { it.soknadsbarnPersonId == soknadsbarnPersonId }
                        .map {
                            BPsAndelUnderholdskostnad(
                                referanse = it.referanse,
                                andelProsent = it.andelProsent,
                                andelBelop = it.andelBelop,
                                barnetErSelvforsorget = it.barnetErSelvforsorget,
                            )
                        }
                        .firstOrNull()

                val samvaersfradrag =
                    grunnlag.justertSamvaersfradragPeriodeListe
                        .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                        .filter { it.soknadsbarnPersonId == soknadsbarnPersonId }
                        .map { Samvaersfradrag(referanse = it.referanse, belop = it.belop) }
                        .firstOrNull()

                val deltBosted =
                    grunnlag.justertDeltBostedPeriodeListe
                        .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                        .filter { it.soknadsbarnPersonId == soknadsbarnPersonId }
                        .map { DeltBosted(referanse = it.referanse, deltBostedIPeriode = it.deltBostedIPeriode) }
                        .firstOrNull()

                val barnetilleggBP =
                    grunnlag.justertBarnetilleggBPPeriodeListe
                        .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                        .filter { it.soknadsbarnPersonId == soknadsbarnPersonId }
                        .map { Barnetillegg(referanse = it.referanse, belop = it.belop, skattProsent = it.skattProsent) }
                        .firstOrNull()

                val barnetilleggBM =
                    grunnlag.justertBarnetilleggBMPeriodeListe
                        .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                        .filter { it.soknadsbarnPersonId == soknadsbarnPersonId }
                        .map { Barnetillegg(referanse = it.referanse, belop = it.belop, skattProsent = it.skattProsent) }
                        .firstOrNull()

                // Ved delt bosted skal andel av underholdskostnad reduseres med 50 prosentpoeng. Blir andelen under 50% skal ikke bidrag beregnes
                if (deltBosted?.deltBostedIPeriode == true && bPsAndelUnderholdskostnad != null) {
                    bPsAndelUnderholdskostnad = justerForDeltBosted(bPsAndelUnderholdskostnad)
                }

                grunnlagBeregningPerBarnListe.add(
                    GrunnlagBeregningPerBarn(
                        soknadsbarnPersonId = soknadsbarnPersonId,
                        bPsAndelUnderholdskostnad = bPsAndelUnderholdskostnad!!,
                        samvaersfradrag = samvaersfradrag!!,
                        deltBosted = deltBosted!!,
                        barnetilleggBP = barnetilleggBP,
                        barnetilleggBM = barnetilleggBM,
                    ),
                )
            }

            val sjablonliste =
                grunnlag.justertSjablonPeriodeListe
                    .filter { it.getPeriode().overlapperMed(beregningsperiode) }

            // Kaller beregningsmodulen for hver beregningsperiode
            val grunnlagBeregning =
                GrunnlagBeregning(
                    bidragsevne = bidragsevne!!,
                    grunnlagPerBarnListe = grunnlagBeregningPerBarnListe,
                    barnetilleggForsvaret = barnetilleggForsvaret!!,
                    andreLopendeBidragListe = andreLopendeBidragListe,
                    sjablonListe = sjablonliste,
                )

            grunnlag.periodeResultatListe.add(
                ResultatPeriode(
                    periode = beregningsperiode,
                    resultatListe = barnebidragBeregning.beregn(grunnlagBeregning),
                    grunnlag = grunnlagBeregning,
                ),
            )
        }
    }

    private fun lagSoknadsbarnPersonIdListe(grunnlag: BeregnBarnebidragListeGrunnlag, periode: Periode): HashSet<Int> {
        val soknadsbarnPersonIdListe = HashSet<Int>()

        grunnlag.justertBPsAndelUnderholdskostnadPeriodeListe.forEach {
            if (it.getPeriode().overlapperMed(periode)) {
                soknadsbarnPersonIdListe.add(it.soknadsbarnPersonId)
            }
        }

        grunnlag.justertSamvaersfradragPeriodeListe.forEach {
            if (it.getPeriode().overlapperMed(periode)) {
                soknadsbarnPersonIdListe.add(it.soknadsbarnPersonId)
            }
        }

        grunnlag.justertDeltBostedPeriodeListe.forEach {
            if (it.getPeriode().overlapperMed(periode)) {
                soknadsbarnPersonIdListe.add(it.soknadsbarnPersonId)
            }
        }

        grunnlag.justertBarnetilleggBPPeriodeListe.forEach {
            if (it.getPeriode().overlapperMed(periode)) {
                soknadsbarnPersonIdListe.add(it.soknadsbarnPersonId)
            }
        }

        grunnlag.justertBarnetilleggBMPeriodeListe.forEach {
            if (it.getPeriode().overlapperMed(periode)) {
                soknadsbarnPersonIdListe.add(it.soknadsbarnPersonId)
            }
        }

        return soknadsbarnPersonIdListe
    }

    // Ved delt bosted skal andel av underholdskostnad reduseres med 50 prosentpoeng. Blir andelen under 50% skal ikke bidrag beregnes
    private fun justerForDeltBosted(bPsAndelUnderholdskostnad: BPsAndelUnderholdskostnad): BPsAndelUnderholdskostnad {
        var andelProsentJustert = BigDecimal.ZERO
        var andelBelop = BigDecimal.ZERO

        if (bPsAndelUnderholdskostnad.andelProsent > BigDecimal.valueOf(0.5)) {
            andelProsentJustert = bPsAndelUnderholdskostnad.andelProsent - BigDecimal.valueOf(0.5)
            andelBelop =
                bPsAndelUnderholdskostnad.andelBelop
                    .divide(bPsAndelUnderholdskostnad.andelProsent, MathContext(10, RoundingMode.HALF_UP))
                    .multiply(andelProsentJustert)
        }

        return BPsAndelUnderholdskostnad(
            referanse = bPsAndelUnderholdskostnad.referanse,
            andelProsent = andelProsentJustert,
            andelBelop = andelBelop,
            barnetErSelvforsorget = bPsAndelUnderholdskostnad.barnetErSelvforsorget,
        )
    }

    // Validerer at input-verdier er gyldige
    override fun validerInput(grunnlag: BeregnBarnebidragGrunnlag): List<Avvik> {
        val avvikListe =
            validerBeregnPeriodeInput(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
            ).toMutableList()

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "bidragsevnePeriodeListe",
                periodeListe = grunnlag.bidragsevnePeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = false,
                sjekkDatoTilNull = true,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "bPsAndelUnderholdskostnadPeriodeListe",
                periodeListe = grunnlag.bPsAndelUnderholdskostnadPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = false,
                sjekkDatoTilNull = true,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "deltBostedPeriodeListe",
                periodeListe = grunnlag.deltBostedPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = false,
                sjekkDatoTilNull = true,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "samvaersfradragPeriodeListe",
                periodeListe = grunnlag.samvaersfradragPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = false,
                sjekkDatoTilNull = true,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "barnetilleggBPPeriodeListe",
                periodeListe = grunnlag.barnetilleggBPPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = false,
                sjekkDatoTilNull = true,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "barnetilleggBMPeriodeListe",
                periodeListe = grunnlag.barnetilleggBMPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = false,
                sjekkDatoTilNull = true,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "barnetilleggForsvaretPeriodeListe",
                periodeListe = grunnlag.barnetilleggForsvaretPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = false,
                sjekkDatoTilNull = true,
                sjekkDatoStartSluttAvPerioden = true,
                sjekkBeregnPeriode = true,
            ),
        )

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "andreLopendeBidragPeriodeListe",
                periodeListe = grunnlag.andreLopendeBidragPeriodeListe.map { it.getPeriode() },
                sjekkOverlappendePerioder = false,
                sjekkOppholdMellomPerioder = false,
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
