package no.nav.bidrag.beregn.samvaersfradrag.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlagPeriodisert;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersklassePeriode;

public class SamvaersfradragPeriodeImpl implements SamvaersfradragPeriode {
  public SamvaersfradragPeriodeImpl(
      SamvaersfradragBeregning samvaersfradragBeregning) {
    this.samvaersfradragBeregning = samvaersfradragBeregning;
  }

  private SamvaersfradragBeregning samvaersfradragBeregning;

  public BeregnSamvaersfradragResultat beregnPerioder(
      BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertSamvaersklassePeriodeListe = beregnSamvaersfradragGrunnlag.getSamvaersklassePeriodeListe()
        .stream()
        .map(SamvaersklassePeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnSamvaersfradragGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Lager liste for å sikre brudd ved på barnets fødselsdato. Brudd-dato blir den 1 i påfølgende måned.
    // Gjøres for hele beregningsperioden
    LocalDate bruddatoAlder;

    if(beregnSamvaersfradragGrunnlag.getSoknadsbarnFodselsdato().getDayOfMonth() == 01){
      bruddatoAlder = beregnSamvaersfradragGrunnlag.getSoknadsbarnFodselsdato()
          .withYear(beregnSamvaersfradragGrunnlag.getBeregnDatoFra().getYear());
    } else {
      bruddatoAlder = beregnSamvaersfradragGrunnlag.getSoknadsbarnFodselsdato().plusMonths(1)
          .withYear(beregnSamvaersfradragGrunnlag.getBeregnDatoFra().getYear())
          .withDayOfMonth(01);
    }

    if(bruddatoAlder.isBefore(beregnSamvaersfradragGrunnlag.getBeregnDatoFra())){
      bruddatoAlder = bruddatoAlder.plusYears(1);
    }

    System.out.println("BruddatoAlder: " + bruddatoAlder);

    var bruddlisteAlderBarn = new ArrayList<Periode>();
    bruddlisteAlderBarn.add(new Periode(bruddatoAlder, bruddatoAlder));
//    Integer tellerAar = beregnSamvaersfradragGrunnlag.getBeregnDatoFra().getYear();
    // Bygger opp liste med bruddpunker i perioden mellom beregnFraDato og beregnTilDato,
    // passer også på å ikke legge til bruddpunkt etter beregnTilDato

    while (bruddatoAlder.plusYears(1).isBefore(beregnSamvaersfradragGrunnlag.getBeregnDatoTil())){
      bruddatoAlder = bruddatoAlder.plusYears(1);
      bruddlisteAlderBarn.add(new Periode(bruddatoAlder, bruddatoAlder));
    }

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnSamvaersfradragGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSamvaersklassePeriodeListe)
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(bruddlisteAlderBarn)
        .addBruddpunkt(beregnSamvaersfradragGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnSamvaersfradragGrunnlag.getBeregnDatoFra(), beregnSamvaersfradragGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnSamvaersfradragGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var samvaersklasse = justertSamvaersklassePeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(SamvaersklassePeriode::getSamvaersklasse).findFirst().orElse(null);

      var alderBarn = beregnSoknadbarnAlder(beregnSamvaersfradragGrunnlag, beregningsperiode.getDatoFra());

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnSamvaersfradragGrunnlagPeriodisert = new BeregnSamvaersfradragGrunnlagPeriodisert(alderBarn,
          samvaersklasse, sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
           samvaersfradragBeregning.beregn(beregnSamvaersfradragGrunnlagPeriodisert),
            beregnSamvaersfradragGrunnlagPeriodisert));

    }

    //Slår sammen perioder med samme resultat
    return new BeregnSamvaersfradragResultat(resultatPeriodeListe);


  }



  @Override
  public Integer beregnSoknadbarnAlder(
      BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag,
      LocalDate beregnDatoFra) {

    LocalDate tempSoknadbarnFodselsdato = beregnSamvaersfradragGrunnlag.getSoknadsbarnFodselsdato();
    Integer beregnetAlder = Period.between(tempSoknadbarnFodselsdato, beregnDatoFra).getYears();

    System.out.println("Beregnet alder: " + beregnetAlder);

    return beregnetAlder;
  }



  // Validerer at input-verdier til underholdskostnadsberegning er gyldige
  public List<Avvik> validerInput(BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag) {
    var avvikListe = new ArrayList<Avvik>();

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : beregnSamvaersfradragGrunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("sjablonPeriodeListe", sjablonPeriodeListe, false, false, false));

    // Sjekk perioder for samværsklasse
    var samvaersklassePeriodeListe = new ArrayList<Periode>();
    for (SamvaersklassePeriode samvaersklassePeriode : beregnSamvaersfradragGrunnlag.getSamvaersklassePeriodeListe()) {
      samvaersklassePeriodeListe.add(samvaersklassePeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("samvaersklassePeriodeListe", samvaersklassePeriodeListe, true, true, true));

    // Sjekk beregn dato fra/til
    avvikListe.addAll(validerBeregnPeriodeInput(beregnSamvaersfradragGrunnlag.getBeregnDatoFra(), beregnSamvaersfradragGrunnlag
        .getBeregnDatoTil()));

    return avvikListe;
  }

  // Validerer at datoer er gyldige
  private List<Avvik> validerInput(String dataElement, List<Periode> periodeListe, boolean sjekkOverlapp, boolean sjekkOpphold, boolean sjekkNull) {
    var avvikListe = new ArrayList<Avvik>();
    int indeks = 0;
    Periode forrigePeriode = null;

    for (Periode dennePeriode : periodeListe) {
      indeks++;

      //Sjekk om perioder overlapper
      if (sjekkOverlapp) {
        if (dennePeriode.overlapper(forrigePeriode)) {
          var feilmelding = "Overlappende perioder i " + dataElement + ": periodeDatoTil=" + forrigePeriode.getDatoTil() + ", periodeDatoFra=" +
              dennePeriode.getDatoFra();
          avvikListe.add(new Avvik(feilmelding, AvvikType.PERIODER_OVERLAPPER));
        }
      }

      //Sjekk om det er opphold mellom perioder
      if (sjekkOpphold) {
        if (dennePeriode.harOpphold(forrigePeriode)) {
          var feilmelding = "Opphold mellom perioder i " + dataElement + ": periodeDatoTil=" + forrigePeriode.getDatoTil() + ", periodeDatoFra=" +
              dennePeriode.getDatoFra();
          avvikListe.add(new Avvik(feilmelding, AvvikType.PERIODER_HAR_OPPHOLD));
        }
      }

      //Sjekk om dato er null
      if (sjekkNull) {
        if ((indeks != periodeListe.size()) && (dennePeriode.getDatoTil() == null)) {
          var feilmelding = "periodeDatoTil kan ikke være null i " + dataElement + ": periodeDatoFra=" + dennePeriode.getDatoFra() +
              ", periodeDatoTil=" + dennePeriode.getDatoTil();
          avvikListe.add(new Avvik(feilmelding, AvvikType.NULL_VERDI_I_DATO));
        }
        if ((indeks != 1) && (dennePeriode.getDatoFra() == null)) {
          var feilmelding = "periodeDatoFra kan ikke være null i " + dataElement + ": periodeDatoFra=" + dennePeriode.getDatoFra() +
              ", periodeDatoTil=" + dennePeriode.getDatoTil();
          avvikListe.add(new Avvik(feilmelding, AvvikType.NULL_VERDI_I_DATO));
        }
      }

      //Sjekk om dato fra er etter dato til
      if (!(dennePeriode.datoTilErEtterDatoFra())) {
        var feilmelding = "periodeDatoTil må være etter periodeDatoFra i " + dataElement + ": periodeDatoFra=" + dennePeriode.getDatoFra() +
            ", periodeDatoTil=" + dennePeriode.getDatoTil();
        avvikListe.add(new Avvik(feilmelding, AvvikType.DATO_FRA_ETTER_DATO_TIL));
      }

      forrigePeriode = new Periode(dennePeriode.getDatoFra(), dennePeriode.getDatoTil());
    }

    return avvikListe;
  }

  // Validerer at beregningsperiode fra/til er gyldig
  private List<Avvik> validerBeregnPeriodeInput(LocalDate beregnDatoFra, LocalDate beregnDatoTil) {
    var avvikListe = new ArrayList<Avvik>();

    if (beregnDatoFra == null) {
      avvikListe.add(new Avvik("beregnDatoFra kan ikke være null", AvvikType.NULL_VERDI_I_DATO));
    }
    if (beregnDatoTil == null) {
      avvikListe.add(new Avvik("beregnDatoTil kan ikke være null", AvvikType.NULL_VERDI_I_DATO));
    }
    if (!new Periode(beregnDatoFra, beregnDatoTil).datoTilErEtterDatoFra()) {
      avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
    }

    return avvikListe;
  }
}


