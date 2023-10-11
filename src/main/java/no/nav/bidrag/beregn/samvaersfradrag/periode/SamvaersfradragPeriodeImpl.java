package no.nav.bidrag.beregn.samvaersfradrag.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.FellesPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.felles.util.PeriodeUtil;
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnetSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.Samvaersklasse;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersklassePeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SoknadsbarnAlder;

public class SamvaersfradragPeriodeImpl extends FellesPeriode implements SamvaersfradragPeriode {

  private final SamvaersfradragBeregning samvaersfradragBeregning;

  public SamvaersfradragPeriodeImpl(SamvaersfradragBeregning samvaersfradragBeregning) {
    this.samvaersfradragBeregning = samvaersfradragBeregning;
  }

  public BeregnetSamvaersfradragResultat beregnPerioder(BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
    var justertSamvaersklassePeriodeListe = beregnSamvaersfradragGrunnlag.getSamvaersklassePeriodeListe()
        .stream()
        .map(SamvaersklassePeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnSamvaersfradragGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Lager liste for å sikre brudd på barnets fødselsdato. Brudd-dato blir den 1. i påfølgende måned. Gjøres for hele beregningsperioden
    var bruddlisteBarnAlder = settBruddListeBarnAlder(beregnSamvaersfradragGrunnlag);

    // Bygger opp liste over perioder
    var perioder = new Periodiserer()
        .addBruddpunkt(beregnSamvaersfradragGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSamvaersklassePeriodeListe)
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(bruddlisteBarnAlder)
        .addBruddpunkt(beregnSamvaersfradragGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnSamvaersfradragGrunnlag.getBeregnDatoFra(), beregnSamvaersfradragGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    mergeSluttperiode(perioder, beregnSamvaersfradragGrunnlag.getBeregnDatoTil());

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var samvaersklasse = justertSamvaersklassePeriodeListe.stream()
          .filter(samvaersklassePeriode -> samvaersklassePeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(samvaersklassePeriode -> new Samvaersklasse(samvaersklassePeriode.getReferanse(), samvaersklassePeriode.getSamvaersklasse()))
          .findFirst()
          .orElse(null);

      var soknadsbarnAlder = new SoknadsbarnAlder(beregnSamvaersfradragGrunnlag.getSoknadsbarn().getReferanse(),
          beregnSoknadsbarnAlder(beregnSamvaersfradragGrunnlag, beregningsperiode.getDatoFom()));

      var sjablonliste = justertSjablonPeriodeListe.stream()
          .filter(sjablonPeriode -> sjablonPeriode.getPeriode().overlapperMed(beregningsperiode))
          .collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregning = new GrunnlagBeregning(soknadsbarnAlder, samvaersklasse, sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(
          beregnSamvaersfradragGrunnlag.getSoknadsbarn().getPersonId(), beregningsperiode, samvaersfradragBeregning.beregn(grunnlagBeregning),
          grunnlagBeregning));
    }

    return new BeregnetSamvaersfradragResultat(resultatPeriodeListe);
  }

  // Lager liste for å sikre brudd på barnets fødselsdato. Brudd-dato blir den 1. i påfølgende måned. Gjøres for hele beregningsperioden
  private List<Periode> settBruddListeBarnAlder(BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag) {
    LocalDate bruddDatoAlder;

    if (beregnSamvaersfradragGrunnlag.getSoknadsbarn().getFodselsdato().getDayOfMonth() == 1) {
      bruddDatoAlder = beregnSamvaersfradragGrunnlag.getSoknadsbarn().getFodselsdato()
          .withYear(beregnSamvaersfradragGrunnlag.getBeregnDatoFra().getYear());
    } else {
      bruddDatoAlder = beregnSamvaersfradragGrunnlag.getSoknadsbarn().getFodselsdato().plusMonths(1)
          .withYear(beregnSamvaersfradragGrunnlag.getBeregnDatoFra().getYear())
          .withDayOfMonth(1);
    }

    if (bruddDatoAlder.isBefore(beregnSamvaersfradragGrunnlag.getBeregnDatoFra())) {
      bruddDatoAlder = bruddDatoAlder.plusYears(1);
    }

    // Bygger opp liste med bruddpunkter i perioden mellom beregnFraDato og beregnTilDato,
    // passer også på å ikke legge til bruddpunkt etter beregnTilDato
    var bruddlisteBarnAlder = new ArrayList<Periode>();
    bruddlisteBarnAlder.add(new Periode(bruddDatoAlder, bruddDatoAlder));

    while (bruddDatoAlder.plusYears(1).isBefore(beregnSamvaersfradragGrunnlag.getBeregnDatoTil())) {
      bruddDatoAlder = bruddDatoAlder.plusYears(1);
      bruddlisteBarnAlder.add(new Periode(bruddDatoAlder, bruddDatoAlder));
    }
    return bruddlisteBarnAlder;
  }

  private Integer beregnSoknadsbarnAlder(BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag, LocalDate beregnDatoFra) {
    var tempSoknadbarnFodselsdato = beregnSamvaersfradragGrunnlag.getSoknadsbarn().getFodselsdato();
    return Period.between(tempSoknadbarnFodselsdato, beregnDatoFra).getYears();
  }


  // Validerer at input-verdier til underholdskostnadsberegning er gyldige
  public List<Avvik> validerInput(BeregnSamvaersfradragGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getPeriode());
    }
    var avvikListe = new ArrayList<>(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe",
        sjablonPeriodeListe, false, false, false, false));

    // Sjekk perioder for samværsklasse
    var samvaersklassePeriodeListe = new ArrayList<Periode>();
    for (SamvaersklassePeriode samvaersklassePeriode : grunnlag.getSamvaersklassePeriodeListe()) {
      samvaersklassePeriodeListe.add(samvaersklassePeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "samvaersklassePeriodeListe",
        samvaersklassePeriodeListe, true, true, true, true));

    return avvikListe;
  }
}
