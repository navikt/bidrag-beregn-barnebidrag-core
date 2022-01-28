package no.nav.bidrag.beregn.underholdskostnad.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.FellesPeriode;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.underholdskostnad.beregning.UnderholdskostnadBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonad;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnetUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgift;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsyn;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.SoknadsbarnAlder;

public class UnderholdskostnadPeriodeImpl extends FellesPeriode implements UnderholdskostnadPeriode {

  protected static final String UTEN_BARNETRYGD = " ";
  protected static final String ORDINAER_BARNETRYGD = "O";
  protected static final String FORHOYET_BARNETRYGD = "F";

  private final UnderholdskostnadBeregning underholdskostnadBeregning;

  public UnderholdskostnadPeriodeImpl(UnderholdskostnadBeregning underholdskostnadBeregning) {
    this.underholdskostnadBeregning = underholdskostnadBeregning;
  }

  public BeregnetUnderholdskostnadResultat beregnPerioder(BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
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

    // Barnetrygd skal ikke trekkes fra i barnets fødselsmåned, må derfor lage denne måneden som egen periode
    var soknadsbarnFodselsmaaned = new Periode(
        beregnUnderholdskostnadGrunnlag.getSoknadsbarn().getFodselsdato().withDayOfMonth(1),
        beregnUnderholdskostnadGrunnlag.getSoknadsbarn().getFodselsdato().withDayOfMonth(1).plusMonths(1));

    // Ny sjablon forhøyet barnetrygd for barn til og med fem år inntrer fra 01.07.2021
    // Det må derfor legges til brudd på denne datoen
    var datoRegelendringer = new Periode(LocalDate.parse("2021-07-01"), LocalDate.parse("2021-07-01"));

    // For å beregne 6-års bruddato brukes 01.07. som fødselsdato.
    // Datoen brukes til å skape brudd på 6-årsdag, og til å sjekke om ordinær eller forhøyet barnetrygd skal brukes.
    var seksAarBruddDato = beregnUnderholdskostnadGrunnlag.getSoknadsbarn().getFodselsdato().plusYears(6).withMonth(7).withDayOfMonth(1);
    var seksAarBruddPeriode = new Periode(seksAarBruddDato, seksAarBruddDato);

    // Barnets fødselsdag og måned skal overstyres til 01.07.
    // Lager liste for å sikre brudd ved ny alder fra 01.07 hvert år i beregningsperioden (ifht. henting av riktige sjablonverdier).
    var bruddlisteBarnAlder = new ArrayList<Periode>();
    var tellerAar = beregnUnderholdskostnadGrunnlag.getBeregnDatoFra().getYear();

    // Bygger opp liste med bruddpunker i perioden mellom beregnFraDato og beregnTilDato.
    // Passer også på å ikke legge til bruddpunkt etter beregnTilDato.
    while (tellerAar <= beregnUnderholdskostnadGrunnlag.getBeregnDatoTil().getYear()
        && beregnUnderholdskostnadGrunnlag.getBeregnDatoTil().isAfter(LocalDate.of(tellerAar, 7, 1))) {
      bruddlisteBarnAlder.add(new Periode(LocalDate.of(tellerAar, 7, 1), LocalDate.of(tellerAar, 7, 1)));
      tellerAar++;
    }

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnUnderholdskostnadGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(justertBarnetilsynMedStonadPeriodeListe)
        .addBruddpunkter(justertNettoBarnetilsynPeriodeListe)
        .addBruddpunkter(justertForpleiningUtgiftPeriodeListe)
        .addBruddpunkter(soknadsbarnFodselsmaaned)
        .addBruddpunkter(seksAarBruddPeriode)
        .addBruddpunkter(datoRegelendringer)
        .addBruddpunkter(bruddlisteBarnAlder)
        .addBruddpunkt(beregnUnderholdskostnadGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnUnderholdskostnadGrunnlag.getBeregnDatoFra(), beregnUnderholdskostnadGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    mergeSluttperiode(perioder, beregnUnderholdskostnadGrunnlag.getBeregnDatoTil());

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var barnetilsynMedStonad = justertBarnetilsynMedStonadPeriodeListe.stream()
          .filter(barnetilsynMedStonadPeriode -> barnetilsynMedStonadPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(barnetilsynMedStonadPeriode -> new BarnetilsynMedStonad(barnetilsynMedStonadPeriode.getReferanse(),
              barnetilsynMedStonadPeriode.getTilsynType(), barnetilsynMedStonadPeriode.getStonadType()))
          .findFirst()
          .orElse(null);

      var nettoBarnetilsyn = justertNettoBarnetilsynPeriodeListe.stream()
          .filter(nettoBarnetilsynPeriode -> nettoBarnetilsynPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(nettoBarnetilsynPeriode -> new NettoBarnetilsyn(nettoBarnetilsynPeriode.getReferanse(), nettoBarnetilsynPeriode.getBelop()))
          .findFirst()
          .orElse(null);

      var forpleiningUtgift = justertForpleiningUtgiftPeriodeListe.stream()
          .filter(forpleiningUtgiftPeriode -> forpleiningUtgiftPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(forpleiningUtgiftPeriode -> new ForpleiningUtgift(forpleiningUtgiftPeriode.getReferanse(), forpleiningUtgiftPeriode.getBelop()))
          .findFirst()
          .orElse(null);

      var soknadsbarnAlder = new SoknadsbarnAlder(beregnUnderholdskostnadGrunnlag.getSoknadsbarn().getReferanse(),
          beregnSoknadsbarnAlderOverstyrt(beregnUnderholdskostnadGrunnlag, beregningsperiode.getDatoFom()));

      var sjablonListe = justertSjablonPeriodeListe.stream()
          .filter(sjablonPeriode -> sjablonPeriode.getPeriode().overlapperMed(beregningsperiode))
          .collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregning = new GrunnlagBeregning(soknadsbarnAlder, barnetilsynMedStonad, nettoBarnetilsyn, forpleiningUtgift, sjablonListe);

      resultatPeriodeListe.add(new ResultatPeriode(beregnUnderholdskostnadGrunnlag.getSoknadsbarn().getPersonId(), beregningsperiode,
          beregnUnderholdskostnad(grunnlagBeregning, beregningsperiode, soknadsbarnFodselsmaaned, datoRegelendringer, seksAarBruddDato),
          grunnlagBeregning));
    }

    return new BeregnetUnderholdskostnadResultat(resultatPeriodeListe);
  }

  private Integer beregnSoknadsbarnAlderOverstyrt(BeregnUnderholdskostnadGrunnlag beregnUnderholdskostnadGrunnlag, LocalDate beregnDatoFra) {
    var tempSoknadbarnFodselsdato = beregnUnderholdskostnadGrunnlag.getSoknadsbarn().getFodselsdato().withDayOfMonth(1).withMonth(7);
    return Period.between(tempSoknadbarnFodselsdato, beregnDatoFra).getYears();
  }

  // Velger mellom 3 metoder for å beregne:
  // 1. beregnUtenBarnetrygd - Barnetrygd skal ikke trekkes fra i barnets fødselsmåned
  // 2. beregnOrdinaerBarnetrygd - Beregning med ordinær barnetrygd
  // 3. beregnForhoyetBarnetrygd - Beregner med forhøyet barnetrygd
  private ResultatBeregning beregnUnderholdskostnad(GrunnlagBeregning grunnlagBeregning, Periode beregningsperiode, Periode soknadsbarnFodselsmaaned,
      Periode datoRegelendringer, LocalDate seksaarsbruddato) {
    if (beregningsperiode.getDatoFom().equals(soknadsbarnFodselsmaaned.getDatoFom())) {
      return underholdskostnadBeregning.beregn(grunnlagBeregning, UTEN_BARNETRYGD);
    } else if (beregningsperiode.getDatoFom().isBefore(datoRegelendringer.getDatoFom())) {
      return underholdskostnadBeregning.beregn(grunnlagBeregning, ORDINAER_BARNETRYGD);
    } else if (beregningsperiode.getDatoFom().isBefore(seksaarsbruddato)) {
      return underholdskostnadBeregning.beregn(grunnlagBeregning, FORHOYET_BARNETRYGD);
    } else {
      return underholdskostnadBeregning.beregn(grunnlagBeregning, ORDINAER_BARNETRYGD);
    }
  }

  // Validerer at input-verdier til underholdskostnadsberegning er gyldige
  public List<Avvik> validerInput(BeregnUnderholdskostnadGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getPeriode());
    }
    var avvikListe = new ArrayList<>(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe",
        sjablonPeriodeListe, false, false, false, false));

    // Sjekk perioder for barnetilsynMedStonad
    var barnetilsynMedStonadPeriodeListe = new ArrayList<Periode>();
    for (BarnetilsynMedStonadPeriode barnetilsynMedStonadPeriode : grunnlag.getBarnetilsynMedStonadPeriodeListe()) {
      barnetilsynMedStonadPeriodeListe.add(barnetilsynMedStonadPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "barnetilsynMedStonadPeriodeListe",
        barnetilsynMedStonadPeriodeListe, true, true, true, true));

    // Sjekk perioder for netto barnetilsyn
    var nettoBarnetilsynPeriodeListe = new ArrayList<Periode>();
    for (NettoBarnetilsynPeriode nettoBarnetilsynPeriode : grunnlag.getNettoBarnetilsynPeriodeListe()) {
      nettoBarnetilsynPeriodeListe.add(nettoBarnetilsynPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "nettoBarnetilsynPeriodeListe",
        nettoBarnetilsynPeriodeListe, true, true, true, true));

    // Sjekk perioder for forpleiningsutgifter
    var forpleiningUtgiftPeriodeListe = new ArrayList<Periode>();
    for (ForpleiningUtgiftPeriode forpleiningUtgiftPeriode : grunnlag.getForpleiningUtgiftPeriodeListe()) {
      forpleiningUtgiftPeriodeListe.add(forpleiningUtgiftPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "forpleiningUtgiftPeriodeListe",
        forpleiningUtgiftPeriodeListe, true, true, true, true));

    return avvikListe;
  }
}
