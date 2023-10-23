package no.nav.bidrag.beregn.nettobarnetilsyn.periode

import no.nav.bidrag.beregn.felles.FellesPeriode
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerBeregnPeriodeInput
import no.nav.bidrag.beregn.felles.util.PeriodeUtil.validerInputDatoer
import no.nav.bidrag.beregn.nettobarnetilsyn.beregning.NettoBarnetilsynBeregning
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnetNettoBarnetilsynResultat
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgiftPeriode
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatPeriode
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

class NettoBarnetilsynPeriodeImpl(private val nettoBarnetilsynBeregning: NettoBarnetilsynBeregning) : FellesPeriode(), NettoBarnetilsynPeriode {
    override fun beregnPerioder(grunnlag: BeregnNettoBarnetilsynGrunnlag): BeregnetNettoBarnetilsynResultat {
        val resultatPeriodeListe = mutableListOf<ResultatPeriode>()

        // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::it)
        val justertFaktiskUtgiftPeriodeListe = grunnlag.faktiskUtgiftPeriodeListe
            .map { FaktiskUtgiftPeriode(it) }

        val justertSjablonPeriodeListe = grunnlag.sjablonPeriodeListe
            .map { SjablonPeriode(it) }

        // Netto barnetilsyn er kun gyldig ut det året barnet fyller 12 år.
        // Lager liste for å sikre brudd  01.01 året etter hvert barn i beregningen fyller 12 år.
        val bruddliste13Aar = beregnSoknadbarn13aarsdagListe(grunnlag)

        // Bygger opp liste over perioder
        val perioder = Periodiserer()
            .addBruddpunkt(grunnlag.beregnDatoFra) // For å sikre bruddpunkt på start-beregning-fra-dato
            .addBruddpunkt(grunnlag.beregnDatoTil) // For å sikre bruddpunkt på start-beregning-til-dato
            .addBruddpunkter(bruddliste13Aar)
            .addBruddpunkter(justertFaktiskUtgiftPeriodeListe)
            .addBruddpunkter(justertSjablonPeriodeListe)
            .finnPerioder(grunnlag.beregnDatoFra, grunnlag.beregnDatoTil)
            .toMutableList()

        // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
        mergeSluttperiode(periodeListe = perioder, datoTil = grunnlag.beregnDatoTil)

        // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
        perioder.forEach { beregningsperiode ->

            // Filtrerer vekk forekomster for barn som har fylt 12 år i tillegg til der innsendt beløp ikke er større enn 0
            val faktiskUtgiftListe = justertFaktiskUtgiftPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map {
                    FaktiskUtgift(
                        soknadsbarnPersonId = it.soknadsbarnPersonId,
                        referanse = it.referanse,
                        soknadsbarnAlder = beregnSoknadbarnAlder(fodselsdato = it.soknadsbarnFodselsdato, beregnTil = beregningsperiode.datoTil),
                        belop = finnEndeligFaktiskUtgiftBelop(
                            alder = beregnSoknadbarnAlder(fodselsdato = it.soknadsbarnFodselsdato, beregnTil = beregningsperiode.datoTil),
                            faktiskUtgift = it.belop
                        )
                    )
                }

            val sjablonliste = justertSjablonPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }

            // Kaller beregningsmodulen for hver beregningsperiode
            val grunnlagBeregning = GrunnlagBeregning(faktiskUtgiftListe = faktiskUtgiftListe, sjablonListe = sjablonliste)

            resultatPeriodeListe.add(
                ResultatPeriode(
                    periode = beregningsperiode,
                    resultatListe = nettoBarnetilsynBeregning.beregn(grunnlagBeregning),
                    grunnlag = grunnlagBeregning
                )
            )
        }

        return BeregnetNettoBarnetilsynResultat(resultatPeriodeListe)
    }

    private fun beregnSoknadbarn13aarsdagListe(grunnlag: BeregnNettoBarnetilsynGrunnlag): List<Periode> {
        val trettenaarsdagListe = HashSet<Periode>()
        grunnlag.faktiskUtgiftPeriodeListe.forEach {
            val trettenaarsdag = it.soknadsbarnFodselsdato.plusYears(13).withMonth(1).withDayOfMonth(1)
            trettenaarsdagListe.add(Periode(trettenaarsdag, trettenaarsdag))
        }
        return trettenaarsdagListe.toList()
    }

    private fun beregnSoknadbarnAlder(fodselsdato: LocalDate, beregnTil: LocalDate?) =
        Period.between(fodselsdato.withDayOfMonth(1).withMonth(1), beregnTil!!.minusDays(1)).years

    // Setter beløp for faktisk utgift til 0 hvis barnet er over 12 år
    private fun finnEndeligFaktiskUtgiftBelop(alder: Int, faktiskUtgift: BigDecimal) =
        if (alder > 12) {
            BigDecimal.ZERO
        } else {
            faktiskUtgift
        }

    // Validerer at input-verdier til NettoBarnetilsynsberegning er gyldige
    override fun validerInput(grunnlag: BeregnNettoBarnetilsynGrunnlag): List<Avvik> {
        val avvikListe = validerBeregnPeriodeInput(beregnDatoFra = grunnlag.beregnDatoFra, beregnDatoTil = grunnlag.beregnDatoTil).toMutableList()

        avvikListe.addAll(
            validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "faktiskUtgiftPeriodeListe",
                periodeListe = grunnlag.faktiskUtgiftPeriodeListe.map { it.getPeriode() },
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
