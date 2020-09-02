package no.nav.bidrag.beregn.underholdskostnad.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.underholdskostnad.beregning.UnderholdskostnadBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatPeriode;

public class UnderholdskostnadPeriodeImpl implements UnderholdskostnadPeriode {

  public UnderholdskostnadPeriodeImpl(UnderholdskostnadBeregning underholdskostnadBeregning) {
    this.underholdskostnadBeregning = underholdskostnadBeregning;
  }

  private UnderholdskostnadBeregning underholdskostnadBeregning;

  public BeregnUnderholdskostnadResultat beregnPerioder(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertBarnetilsynMedStonadPeriodeListe = beregnUnderholdskostnadGrunnlag
        .getBarnetilsynMedStonadPeriodeListe()
        .stream()
        .map(BarnetilsynMedStonadPeriode::new)
        .collect(toCollection(ArrayList::new));
    var justertNettoBarnetilsynPeriodeListe = beregnUnderholdskostnadGrunnlag
        .getNettoBarnetilsynPeriodeListe()
        .stream()
        .map(NettoBarnetilsynPeriode::new)
        .collect(toCollection(ArrayList::new));
    var justertForpleiningUtgiftPeriodeListe = beregnUnderholdskostnadGrunnlag
        .getForpleiningUtgiftPeriodeListe()
        .stream()
        .map(ForpleiningUtgiftPeriode::new)
        .collect(toCollection(ArrayList::new));
    var justertSjablonPeriodeListe = beregnUnderholdskostnadGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Barnetrygd skal ikke trekkes fra i barnets fødselsmåned, må derfor lage denne måneden som egen periode
    Periode soknadsbarnFodselsmaaned = new Periode(
        beregnUnderholdskostnadGrunnlag.getSoknadsbarnFodselsdato().withDayOfMonth(01),
        beregnUnderholdskostnadGrunnlag.getSoknadsbarnFodselsdato().withDayOfMonth(01)
            .plusMonths(1));

    // Ny sjablon forhøyet barnetrygd for barn til og med fem år inntrer fra 01.07.2021
    // Det må derfor legges til brudd på denne datoen
    Periode datoRegelendringer = new Periode(LocalDate.parse("2021-07-01"),
        LocalDate.parse("2021-07-01"));

    // Lager bruddperiode i påfølgende måned etter at barnet fyller seks år,
    // forhøyet barnetrygd skal da erstattes med ordinær barnetrygd (fra 01.07.2021)
    LocalDate seksaarsbruddato = beregnUnderholdskostnadGrunnlag
        .getSoknadsbarnFodselsdato().plusYears(6).plusMonths(01).withDayOfMonth(01);
    Periode maanedEtterSeksaarsdag = new Periode(seksaarsbruddato, seksaarsbruddato);

    System.out.println("Seksårsdagperiode" + maanedEtterSeksaarsdag.getDatoFra() + maanedEtterSeksaarsdag.getDatoTil());

    // Barnets fødselsdag og måned skal overstyres til 01.07. Lager liste for å sikre brudd ved ny
    // alder fra 01.07 hvert år i beregningsperioden
    var bruddlisteBarnAlder = new ArrayList<Periode>();
    var tellerAar = beregnUnderholdskostnadGrunnlag.getBeregnDatoFra().getYear();

    // Bygger opp liste med bruddpunker i perioden mellom beregnFraDato og beregnTilDato,
    // passer også på å ikke legge til bruddpunkt etter beregnTilDato
    while (tellerAar <= beregnUnderholdskostnadGrunnlag.getBeregnDatoTil().getYear()
        && beregnUnderholdskostnadGrunnlag.getBeregnDatoTil()
        .isAfter(LocalDate.of(tellerAar, 7, 1))) {
      bruddlisteBarnAlder
          .add(new Periode(LocalDate.of(tellerAar, 7, 1), LocalDate.of(tellerAar, 7, 1)));
      tellerAar++;
    }

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnUnderholdskostnadGrunnlag
            .getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(justertBarnetilsynMedStonadPeriodeListe)
        .addBruddpunkter(justertNettoBarnetilsynPeriodeListe)
        .addBruddpunkter(justertForpleiningUtgiftPeriodeListe)
        .addBruddpunkter(soknadsbarnFodselsmaaned)
        .addBruddpunkter(maanedEtterSeksaarsdag)
        .addBruddpunkter(datoRegelendringer)
        .addBruddpunkter(bruddlisteBarnAlder)
        .addBruddpunkt(beregnUnderholdskostnadGrunnlag
            .getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnUnderholdskostnadGrunnlag.getBeregnDatoFra(),
            beregnUnderholdskostnadGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil()
          .equals(beregnUnderholdskostnadGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var BarnetilsynMedStonad = justertBarnetilsynMedStonadPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(barnetilsynMedStonadPeriode -> new BarnetilsynMedStonad(
              barnetilsynMedStonadPeriode.getBarnetilsynMedStonadTilsynType(),
              barnetilsynMedStonadPeriode.getBarnetilsynStonadType())).findFirst().orElse(null);

      var nettoBarnetilsynBelop = justertNettoBarnetilsynPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(NettoBarnetilsynPeriode::getNettoBarnetilsynBelop).findFirst().orElse(null);

      var forpleiningUtgiftBelop = justertForpleiningUtgiftPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(ForpleiningUtgiftPeriode::getForpleiningUtgiftBelop).findFirst().orElse(null);

      var alderBarn = beregnSoknadbarnAlderOverstyrt(beregnUnderholdskostnadGrunnlag,
          beregningsperiode.getDatoFra());

      var sjablonliste = justertSjablonPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnUnderholdskostnadGrunnlagPeriodisert = new BeregnUnderholdskostnadGrunnlagPeriodisert(
          alderBarn, BarnetilsynMedStonad, nettoBarnetilsynBelop, forpleiningUtgiftBelop,
          sjablonliste);

      // Velger mellom 3 metoder for å beregne:
      // 1. beregnUtenBarnetrygd - Barnetrygd skal ikke trekkes fra i barnets fødselsmåned
      // 2. beregnOrdinaerBarnetrygd - Beregning med ordinær barnetrygd
      // 3. beregnForhoyetBarnetrygd - Beregner med forhøyet barnetrygd

      if (beregningsperiode.getDatoFra().equals(soknadsbarnFodselsmaaned.getDatoFra())) {
        System.out.println(
            "Barnets fødselsmåned, beregner uten barnetrygd: "
                + beregningsperiode.getDatoFra() + " " + beregningsperiode.getDatoTil());
        resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode, underholdskostnadBeregning
            .beregnUtenBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert),
            beregnUnderholdskostnadGrunnlagPeriodisert));
      } else {
        if (beregnUnderholdskostnadGrunnlag.getBeregnDatoTil()
            .isBefore(datoRegelendringer.getDatoFra().plusDays(1))) {
          System.out.println(
              "Periode er før innføring av forhøyet barnetrygd, beregner kun med ordinær barnetrygd "
                  + beregningsperiode.getDatoFra() + " " + beregningsperiode.getDatoTil());

          resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode, underholdskostnadBeregning
              .beregnOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert),
              beregnUnderholdskostnadGrunnlagPeriodisert));
        } else {
          // BeregnDatoTil er etter dato for innføring av forhøyet barnetrygd, 01.07.2021
          if (beregningsperiode.getDatoFraTil() == null) {
            // Siste periode som skal beregnes, vil her alltid være > dato for innføring av forhøyet barnetrygd
            System.out.println("Beregner med ordinær og forhøyet barnetrygd1 "
                + beregningsperiode.getDatoFra() + " " + beregningsperiode.getDatoTil());
            resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
                underholdskostnadBeregning.beregnForhoyetBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert),
                beregnUnderholdskostnadGrunnlagPeriodisert));
          } else if (beregningsperiode.getDatoFra()
              .isAfter(datoRegelendringer.getDatoFra().minusDays(01))) {
            // Perioden er etter dato for innføring av forhøyet barnetrygd
            System.out.println("Beregner med ordinær og forhøyet barnetrygd2 "
                + beregningsperiode.getDatoFra() + " " + beregningsperiode.getDatoTil());

            resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
                underholdskostnadBeregning.beregnForhoyetBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert),
                beregnUnderholdskostnadGrunnlagPeriodisert));
          } else {
            // Perioden er før dato for innføring av forhøyet barnetrygd
            System.out.println(
                "Periode er før innføring av forhøyet barnetrygd, beregner kun med ordinær barnetrygd "
                    + beregningsperiode.getDatoFra() + " " + beregningsperiode.getDatoTil());

            resultatPeriodeListe
                .add(new ResultatPeriode(beregningsperiode, underholdskostnadBeregning
                    .beregnOrdinaerBarnetrygd(beregnUnderholdskostnadGrunnlagPeriodisert),
                    beregnUnderholdskostnadGrunnlagPeriodisert));
          }
        }
      }
    }
    return new BeregnUnderholdskostnadResultat(resultatPeriodeListe);
  }

  @Override
  public Integer beregnSoknadbarnAlderOverstyrt(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag,
      LocalDate beregnDatoFra) {

    LocalDate tempSoknadbarnFodselsdato = beregnUnderholdskostnadGrunnlag
        .getSoknadsbarnFodselsdato()
        .withDayOfMonth(1)
        .withMonth(7);

    return Period.between(tempSoknadbarnFodselsdato, beregnDatoFra).getYears();
  }

  @Override
  public Integer beregnSoknadbarnAlderReell(
      BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag,
      LocalDate beregnDatoFra) {

    return Period.between(beregnUnderholdskostnadGrunnlag.getSoknadsbarnFodselsdato(),
        beregnDatoFra).getYears();
  }


  // Validerer at input-verdier til underholdskostnadsberegning er gyldige
  public List<Avvik> validerInput(BeregnUnderholdskostnadGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
            "sjablonPeriodeListe",
            sjablonPeriodeListe, false, false, false, false));

    // Sjekk perioder for barnetilsynMedStonad
    var barnetilsynMedStonadPeriodeListe = new ArrayList<Periode>();
    for (BarnetilsynMedStonadPeriode barnetilsynMedStonadPeriode : grunnlag
        .getBarnetilsynMedStonadPeriodeListe()) {
      barnetilsynMedStonadPeriodeListe.add(barnetilsynMedStonadPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil
        .validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
            "barnetilsynMedStonadPeriodeListe",
            barnetilsynMedStonadPeriodeListe, true, true, true, true));

    // Sjekk perioder for netto barnetilsyn
    var nettoBarnetilsynPeriodeListe = new ArrayList<Periode>();
    for (NettoBarnetilsynPeriode nettoBarnetilsynPeriode : grunnlag
        .getNettoBarnetilsynPeriodeListe()) {
      nettoBarnetilsynPeriodeListe.add(nettoBarnetilsynPeriode.getDatoFraTil());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
            "nettoBarnetilsynPeriodeListe", nettoBarnetilsynPeriodeListe, true, true, true, true));

    // Sjekk perioder for forpleiningsutgifter
    var forpleiningUtgiftPeriodeListe = new ArrayList<Periode>();
    for (ForpleiningUtgiftPeriode forpleiningUtgiftPeriode : grunnlag
        .getForpleiningUtgiftPeriodeListe()) {
      forpleiningUtgiftPeriodeListe.add(forpleiningUtgiftPeriode.getDatoFraTil());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
            "forpleiningUtgiftPeriodeListe", forpleiningUtgiftPeriodeListe, true, true, true,
            true));

    return avvikListe;
  }
}
