package no.nav.bidrag.beregn.nettobarnetilsyn.periode;

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
import no.nav.bidrag.beregn.nettobarnetilsyn.beregning.NettoBarnetilsynBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynResultat;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgiftBarnetilsynPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatPeriode;

public class NettoBarnetilsynPeriodeImpl implements FaktiskUtgiftBarnetilsynPeriode {

  public NettoBarnetilsynPeriodeImpl(NettoBarnetilsynBeregning nettoBarnetilsynBeregning) {
    this.nettoBarnetilsynBeregning = nettoBarnetilsynBeregning;
  }

  private NettoBarnetilsynBeregning nettoBarnetilsynBeregning;

  public BeregnNettoBarnetilsynResultat beregnPerioder(
      BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertNettoBarnetilsynPeriodeListe = beregnNettoBarnetilsynGrunnlag.getFaktiskUtgiftBarnetilsynPeriodeListe()
        .stream()
        .map(FaktiskUtgiftBarnetilsynPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnNettoBarnetilsynGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Barnets fødselsdag og måned skal overstyres til 01.07. Lager liste for å sikre brudd ved ny
    // alder fra 01.07 hvert år i beregningsperioden
    var bruddlisteBarnAlder = new ArrayList<Periode>();
    Integer tellerAar = beregnNettoBarnetilsynGrunnlag.getBeregnDatoFra().getYear();

    // Bygger opp liste med bruddpunker i perioden mellom beregnFraDato og beregnTilDato,
    // passer også på å ikke legge til bruddpunkt etter beregnTilDato
    while (tellerAar <= beregnNettoBarnetilsynGrunnlag.getBeregnDatoTil().getYear()
    && beregnNettoBarnetilsynGrunnlag.getBeregnDatoTil()
        .isAfter(LocalDate.of(tellerAar, 07, 01))) {
      bruddlisteBarnAlder.add(new Periode (LocalDate.of(tellerAar, 07, 01), LocalDate.of(tellerAar, 07, 01)));
      tellerAar ++;
    }

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnNettoBarnetilsynGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(justertNettoBarnetilsynPeriodeListe)
        .addBruddpunkter(bruddlisteBarnAlder)
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

      var nettoBarnetilsynBelop = justertNettoBarnetilsynPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(FaktiskUtgiftBarnetilsynPeriode::getFaktiskUtgiftBarnetilsynBelop).findFirst().orElse(null);

      var alderBarn = beregnSoknadbarnAlder(beregnNettoBarnetilsynGrunnlag, beregningsperiode.getDatoFra());

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnNettoBarnetilsynGrunnlagPeriodisert = new BeregnNettoBarnetilsynGrunnlagPeriodisert(
          alderBarn, nettoBarnetilsynBelop, sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode, nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert),
          beregnNettoBarnetilsynGrunnlagPeriodisert));
    }

    //Slår sammen perioder med samme resultat
    return new BeregnNettoBarnetilsynResultat(resultatPeriodeListe);
  }

  @Override
  public Integer beregnSoknadbarnAlder(
      BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag,
      LocalDate beregnDatoFra) {

    LocalDate tempSoknadbarnFodselsdato = beregnNettoBarnetilsynGrunnlag.getFaktiskUtgiftBarnetilsynPeriodeListe().

        .withDayOfMonth(01)
        .withMonth(07);

    Integer beregnetAlder = Period.between(tempSoknadbarnFodselsdato, beregnDatoFra).getYears();

    return beregnetAlder;
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

    // Sjekk perioder for netto barnetilsyn
    var nettoBarnetilsynPeriodeListe = new ArrayList<Periode>();
    for (FaktiskUtgiftBarnetilsynPeriode faktiskUtgiftBarnetilsynPeriode : beregnNettoBarnetilsynGrunnlag.getFaktiskUtgiftBarnetilsynPeriodeListe()) {
      nettoBarnetilsynPeriodeListe.add(faktiskUtgiftBarnetilsynPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("nettoBarnetilsynPeriodeListe", nettoBarnetilsynPeriodeListe, true, true, true));


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
