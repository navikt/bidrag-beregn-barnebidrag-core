package no.nav.bidrag.beregn.underholdskostnad.periode;

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
import no.nav.bidrag.beregn.underholdskostnad.beregning.Underholdskostnadberegning;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatPeriode;

public class UnderholdskostnadPeriodeImpl implements UnderholdskostnadPeriode{

  public UnderholdskostnadPeriodeImpl(Underholdskostnadberegning underholdskostnadberegning) {
    this.underholdskostnadberegning = underholdskostnadberegning;
  }

  private Underholdskostnadberegning underholdskostnadberegning;

  public BeregnUnderholdskostnadResultat beregnPerioder(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertBarnetilsynMedStonadPeriodeListe = beregnUnderholdskostnadGrunnlag.getBarnetilsynMedStonadPeriodeListe()
        .stream()
        .map(BarnetilsynMedStonadPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertNettoBarnetilsynPeriodeListe = beregnUnderholdskostnadGrunnlag.getNettoBarnetilsynPeriodeListe()
        .stream()
        .map(NettoBarnetilsynPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertForpleiningUtgiftPeriodeListe = beregnUnderholdskostnadGrunnlag.getForpleiningUtgiftPeriodeListe()
        .stream()
        .map(ForpleiningUtgiftPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnUnderholdskostnadGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Barnets fødselsdag og måned blir overstyrt til 01.07. Lager liste for å sikre brudd ved ny
    // alder fra 01.07 hvert år i beregningsperioden
    var bruddlisteBarnAlder = new ArrayList<Periode>();
    Integer tellerAar = beregnUnderholdskostnadGrunnlag.getBeregnDatoFra().getYear();


    // Bygger opp liste med bruddpunker i perioden mellom beregnFraDato og beregnTilDato,
    // passer også på å ikke legge til bruddpunkt etter beregnTilDato
    while (tellerAar <= beregnUnderholdskostnadGrunnlag.getBeregnDatoTil().getYear()
    && beregnUnderholdskostnadGrunnlag.getBeregnDatoTil()
        .isAfter(LocalDate.of(tellerAar, 07, 01))) {
      bruddlisteBarnAlder.add(new Periode (LocalDate.of(tellerAar, 07, 01), LocalDate.of(tellerAar, 07, 01)));
      tellerAar ++;
    }


    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnUnderholdskostnadGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(justertBarnetilsynMedStonadPeriodeListe)
        .addBruddpunkter(justertNettoBarnetilsynPeriodeListe)
        .addBruddpunkter(justertForpleiningUtgiftPeriodeListe)
        .addBruddpunkter(bruddlisteBarnAlder)
        .finnPerioder(beregnUnderholdskostnadGrunnlag.getBeregnDatoFra(), beregnUnderholdskostnadGrunnlag.getBeregnDatoTil());


    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnUnderholdskostnadGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    for (Periode beregningsperiode : perioder) {

      var BarnetilsynMedStonad = justertBarnetilsynMedStonadPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(barnetilsynMedStonadPeriode -> new BarnetilsynMedStonad(barnetilsynMedStonadPeriode.getBarnetilsynMedStonadTilsynType(),
              barnetilsynMedStonadPeriode.getBarnetilsynStonadType())).findFirst().orElse(null);

      var nettoBarnetilsynBelop = justertNettoBarnetilsynPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(NettoBarnetilsynPeriode::getNettoBarnetilsynBelop).findFirst().orElse(null);

      var forpleiningUtgiftBelop = justertForpleiningUtgiftPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(ForpleiningUtgiftPeriode::getForpleiningUtgiftBelop).findFirst().orElse(null);

/*
      var alderBarn = bruddlisteBarnAlder.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(beregnSoknadbarnAlder(beregnUnderholdskostnadGrunnlag, beregningsperiode.getDatoTil()));
*/

      var alderBarn = beregnSoknadbarnAlder(beregnUnderholdskostnadGrunnlag, beregningsperiode.getDatoFra());

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      System.out.println("Beregner underholdskostnad for periode: " + beregningsperiode.getDatoFra() + " " + beregningsperiode.getDatoTil());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnUnderholdskostnadGrunnlagPeriodisert = new BeregnUnderholdskostnadGrunnlagPeriodisert(
          alderBarn, BarnetilsynMedStonad, nettoBarnetilsynBelop, forpleiningUtgiftBelop, sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode, underholdskostnadberegning.beregn(beregnUnderholdskostnadGrunnlagPeriodisert),
          beregnUnderholdskostnadGrunnlagPeriodisert));
    }

    //Slår sammen perioder med samme resultat
    return new BeregnUnderholdskostnadResultat(resultatPeriodeListe);

  }



  @Override
  public Integer beregnSoknadbarnAlder(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag,
      LocalDate beregnDatoFra) {

    LocalDate overstyrtFodselsdato = beregnUnderholdskostnadGrunnlag.getSoknadsbarnFodselsdato()
        .withDayOfMonth(01)
        .withMonth(07)
        .withYear(beregnUnderholdskostnadGrunnlag.getBeregnDatoFra().getYear());

    LocalDate tempSoknadbarnFodselsdato = beregnUnderholdskostnadGrunnlag.getSoknadsbarnFodselsdato()
        .withDayOfMonth(01)
        .withMonth(07);

    System.out.println("tempSoknadbarnFodselsdato: " + tempSoknadbarnFodselsdato);

    Integer beregnetAlder = Period.between(tempSoknadbarnFodselsdato, LocalDate.now()).getYears();

    System.out.println("Beregnet alder: " + beregnetAlder);

    return beregnetAlder;

  }


  // Validerer at input-verdier til bidragsevneberegning er gyldige
  public List<Avvik> validerInput(BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag) {
    var avvikListe = new ArrayList<Avvik>();

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : beregnUnderholdskostnadGrunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("sjablonPeriodeListe", sjablonPeriodeListe, false, false, false));

    // Sjekk perioder for barnetilsynMedStonad
    var barnetilsynMedStonadPeriodeListe = new ArrayList<Periode>();
    for (BarnetilsynMedStonadPeriode barnetilsynMedStonadPeriode : beregnUnderholdskostnadGrunnlag.getBarnetilsynMedStonadPeriodeListe()) {
      barnetilsynMedStonadPeriodeListe.add(barnetilsynMedStonadPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("inntektPeriodeListe", barnetilsynMedStonadPeriodeListe, true, true, true));

    // Sjekk perioder for netto barnetilsyn
    var nettoBarnetilsynPeriodeListe = new ArrayList<Periode>();
    for (NettoBarnetilsynPeriode nettoBarnetilsynPeriode : beregnUnderholdskostnadGrunnlag.getNettoBarnetilsynPeriodeListe()) {
      nettoBarnetilsynPeriodeListe.add(nettoBarnetilsynPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("skatteklassePeriodeListe", nettoBarnetilsynPeriodeListe, true, true, true));

    // Sjekk perioder for forpleiningsutgifter
    var forpleiningUtgiftPeriodeListe = new ArrayList<Periode>();
    for (ForpleiningUtgiftPeriode forpleiningUtgiftPeriode : beregnUnderholdskostnadGrunnlag.getForpleiningUtgiftPeriodeListe()) {
      forpleiningUtgiftPeriodeListe.add(forpleiningUtgiftPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("bostatusPeriodeListe", forpleiningUtgiftPeriodeListe, true, true, true));

    // Sjekk beregn dato fra/til
    avvikListe.addAll(validerBeregnPeriodeInput(beregnUnderholdskostnadGrunnlag.getBeregnDatoFra(), beregnUnderholdskostnadGrunnlag.getBeregnDatoTil()));

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
