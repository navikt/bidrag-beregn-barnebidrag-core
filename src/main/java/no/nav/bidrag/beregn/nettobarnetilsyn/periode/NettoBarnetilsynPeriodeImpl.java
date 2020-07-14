package no.nav.bidrag.beregn.nettobarnetilsyn.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.nettobarnetilsyn.beregning.NettoBarnetilsynBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynResultat;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgiftPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatPeriode;

public class NettoBarnetilsynPeriodeImpl implements NettoBarnetilsynPeriode {

  public NettoBarnetilsynPeriodeImpl(NettoBarnetilsynBeregning nettoBarnetilsynBeregning) {
    this.nettoBarnetilsynBeregning = nettoBarnetilsynBeregning;
  }

  private NettoBarnetilsynBeregning nettoBarnetilsynBeregning;

  public BeregnNettoBarnetilsynResultat beregnPerioder(
      BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertFaktiskUtgiftPeriodeListe = beregnNettoBarnetilsynGrunnlag.getFaktiskUtgiftPeriodeListe()
        .stream()
        .map(FaktiskUtgiftPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnNettoBarnetilsynGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Metode beregnSoknadbarn12aarsdagSet returneres et Hashset<Periode> med alle 12årsdager i grunnlaget,
    // konverterer det til ArrayList under.
    // Lager liste for å sikre brudd  01.01 året etter hvert barn i beregningen fyller 12 år.
    // Netto barnetilsyn er kun gyldig ut det året barnet fyller 12 år
    ArrayList<Periode> bruddliste12Aar = new ArrayList<>(beregnSoknadbarn12aarsdagListe(beregnNettoBarnetilsynGrunnlag));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnNettoBarnetilsynGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(justertFaktiskUtgiftPeriodeListe)
        .addBruddpunkter((bruddliste12Aar))
        .finnPerioder(beregnNettoBarnetilsynGrunnlag.getBeregnDatoFra(), beregnNettoBarnetilsynGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnNettoBarnetilsynGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      // Filtrerer vekk forekomster for barn som har fyllt 12 år i tillegg til der innsendt beløp ikke er større enn 0
      var faktiskUtgiftListe = justertFaktiskUtgiftPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .filter(i -> Double.valueOf(i.getFaktiskUtgiftBelop()).compareTo(0.0) > 0)
          .filter(i -> beregnSoknadbarn12aarsdag(i.getFaktiskUtgiftSoknadsbarnFodselsdato())
              .compareTo(beregningsperiode.getDatoTil()) >= 0)
          .map(faktiskUtgiftPeriode -> new FaktiskUtgift(faktiskUtgiftPeriode.getFaktiskUtgiftSoknadsbarnFodselsdato(),
              faktiskUtgiftPeriode.getFaktiskUtgiftSoknadsbarnPersonId(),
              faktiskUtgiftPeriode.getFaktiskUtgiftBelop())).collect(toList());

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnNettoBarnetilsynGrunnlagPeriodisert = new BeregnNettoBarnetilsynGrunnlagPeriodisert(
          faktiskUtgiftListe, sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
              nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert),
          beregnNettoBarnetilsynGrunnlagPeriodisert));
    }

    //Slår sammen perioder med samme resultat
    return new BeregnNettoBarnetilsynResultat(resultatPeriodeListe);
  }

  @Override
  public HashSet<Periode> beregnSoknadbarn12aarsdagListe(BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag) {
    var tolvaarsdagListe = new HashSet<Periode>();
    LocalDate tolvaarsdag;

    for (FaktiskUtgiftPeriode grunnlag: beregnNettoBarnetilsynGrunnlag.getFaktiskUtgiftPeriodeListe()) {
      tolvaarsdag = grunnlag.getFaktiskUtgiftSoknadsbarnFodselsdato().plusYears(13).withMonth(01).withDayOfMonth(01);
//      System.out.println("Fødselsdato: " + grunnlag.getFaktiskUtgiftSoknadsbarnFodselsdato());
//      System.out.println("12-Årsdag: " + tolvaarsdag);
//      System.out.println(" ");
      tolvaarsdagListe.add(new Periode(tolvaarsdag, tolvaarsdag));
    }
    return tolvaarsdagListe;
  }

  @Override
  public LocalDate beregnSoknadbarn12aarsdag(LocalDate fodselsdato) {
    return fodselsdato.plusYears(13).withMonth(01).withDayOfMonth(01);
  }

  // Validerer at input-verdier til NettoBarnetilsynsberegning er gyldige
  public List<Avvik> validerInput(BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag) {
    var avvikListe = new ArrayList<Avvik>();

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : beregnNettoBarnetilsynGrunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("sjablonPeriodeListe", sjablonPeriodeListe, false, false, false));

    // Sjekk perioder for faktisk utgift
    var faktiskUtgiftPeriodeListe = new ArrayList<Periode>();
    for (FaktiskUtgiftPeriode faktiskUtgiftPeriode : beregnNettoBarnetilsynGrunnlag.getFaktiskUtgiftPeriodeListe()) {
      faktiskUtgiftPeriodeListe.add(faktiskUtgiftPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("faktiskUtgiftPeriodeListe", faktiskUtgiftPeriodeListe, true, true, true));

    // Sjekk beregn dato fra/til
    avvikListe.addAll(validerBeregnPeriodeInput(beregnNettoBarnetilsynGrunnlag.getBeregnDatoFra(), beregnNettoBarnetilsynGrunnlag.getBeregnDatoTil()));

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
