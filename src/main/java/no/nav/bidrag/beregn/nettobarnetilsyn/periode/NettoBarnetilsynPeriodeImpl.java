package no.nav.bidrag.beregn.nettobarnetilsyn.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.nettobarnetilsyn.beregning.NettoBarnetilsynBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynResultat;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgiftPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.GrunnlagBeregningPeriodisert;
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

    // Metode beregnSoknadbarn12aarsdagSet returnerer et Hashset<Periode> med alle 12årsdager i grunnlaget,
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
        .addBruddpunkt(beregnNettoBarnetilsynGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
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
//          .filter(i -> Double.compare(i.getFaktiskUtgiftBelop(), 0.0) > 0)
          .filter(i -> beregnSoknadbarn12aarsdag(i.getFaktiskUtgiftSoknadsbarnFodselsdato())
              .compareTo(beregningsperiode.getDatoTil()) >= 0)
          .map(faktiskUtgiftPeriode -> new FaktiskUtgift(faktiskUtgiftPeriode.getFaktiskUtgiftSoknadsbarnPersonId(),
              faktiskUtgiftPeriode.getFaktiskUtgiftSoknadsbarnFodselsdato(),
              faktiskUtgiftPeriode.getFaktiskUtgiftBelop())).collect(toList());

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnNettoBarnetilsynGrunnlagPeriodisert = new GrunnlagBeregningPeriodisert(
          faktiskUtgiftListe, sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
          nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert),
          beregnNettoBarnetilsynGrunnlagPeriodisert));
    }

    return new BeregnNettoBarnetilsynResultat(resultatPeriodeListe);
  }

  @Override
  public HashSet<Periode> beregnSoknadbarn12aarsdagListe(BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag) {
    var tolvaarsdagListe = new HashSet<Periode>();
    LocalDate tolvaarsdag;

    for (FaktiskUtgiftPeriode grunnlag: beregnNettoBarnetilsynGrunnlag.getFaktiskUtgiftPeriodeListe()) {
      tolvaarsdag = grunnlag.getFaktiskUtgiftSoknadsbarnFodselsdato().plusYears(13).withMonth(1).withDayOfMonth(1);
      tolvaarsdagListe.add(new Periode(tolvaarsdag, tolvaarsdag));
    }
    return tolvaarsdagListe;
  }

  @Override
  public LocalDate beregnSoknadbarn12aarsdag(LocalDate fodselsdato) {
    return fodselsdato.plusYears(13).withMonth(1).withDayOfMonth(1);
  }

  // Validerer at input-verdier til NettoBarnetilsynsberegning er gyldige
  public List<Avvik> validerInput(BeregnNettoBarnetilsynGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe",
            sjablonPeriodeListe, false, false, false, false));

    // Sjekk perioder for faktisk utgift, skrur av kontroll av overlapp og opphold pga potensielt flere barn i input
    var faktiskUtgiftPeriodeListe = new ArrayList<Periode>();
    for (FaktiskUtgiftPeriode faktiskUtgiftPeriode : grunnlag.getFaktiskUtgiftPeriodeListe()) {
      faktiskUtgiftPeriodeListe.add(faktiskUtgiftPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),"faktiskUtgiftPeriodeListe",
        faktiskUtgiftPeriodeListe, false, false, true, true));

    return avvikListe;
  }
}