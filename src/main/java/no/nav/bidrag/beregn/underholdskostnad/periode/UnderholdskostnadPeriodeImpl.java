package no.nav.bidrag.beregn.underholdskostnad.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.bidragsevne.bo.BeregnBidragsevneResultat;
import no.nav.bidrag.beregn.felles.bidragsevne.bo.ResultatPeriode;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.underholdskostnad.beregning.Underholdskostnadberegning;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode;

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
      bruddlisteBarnAlder.add(LocalDate.of(tellerAar, 07, 01), LocalDate.of(tellerAar, 07, 01);
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

      var BarnetilsynMedStonadListe = justertBarnetilsynMedStonadPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(barnetilsynMedStonadPeriode -> new BarnetilsynMedStonad(barnetilsynMedStonadPeriode.getBarnetilsynMedStonadTilsynType(),
              barnetilsynMedStonadPeriode.getBarnetilsynStonadType())).collect(toList());

      var nettoBarnetilsynBelop = justertNettoBarnetilsynPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(NettoBarnetilsynPeriode::getNettoBarnetilsynBelop).findFirst().orElse(null);

      var forpleiningUtgiftBelop = justertForpleiningUtgiftPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(ForpleiningUtgiftPeriode::getForpleiningUtgiftBelop).findFirst().orElse(null);

      var alderBarn = bruddlisteBarnAlder.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(beregnSoknadbarnAlder(beregnUnderholdskostnadGrunnlag, beregningsperiode.getDatoTil()));

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      System.out.println("Beregner underholdskostnad for periode: " + beregningsperiode.getDatoFra() + " " + beregningsperiode.getDatoTil());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnUnderholdskostnadGrunnlagPeriodisert = new BeregnUnderholdskostnadGrunnlagPeriodisert(
          alderBarn, BarnetilsynMedStonadListe, nettoBarnetilsynBelop, forpleiningUtgiftBelop, sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode, underholdskostnadberegning.beregn(beregnUnderholdskostnadGrunnlagPeriodisert),
          beregnUnderholdskostnadGrunnlagPeriodisert));
    }

    //Slår sammen perioder med samme resultat
    return new BeregnBidragsevneResultat(resultatPeriodeListe);

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





}
