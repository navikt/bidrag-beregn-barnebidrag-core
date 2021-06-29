package no.nav.bidrag.beregn.bidragsevne.periode;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bidragsevne.beregning.BidragsevneBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstand;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstandPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnetBidragsevneResultat;
import no.nav.bidrag.beregn.bidragsevne.bo.Bostatus;
import no.nav.bidrag.beregn.bidragsevne.bo.BostatusPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.bidragsevne.bo.InntektPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.Saerfradrag;
import no.nav.bidrag.beregn.bidragsevne.bo.SaerfradragPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.Skatteklasse;
import no.nav.bidrag.beregn.bidragsevne.bo.SkatteklassePeriode;
import no.nav.bidrag.beregn.felles.FellesPeriode;
import no.nav.bidrag.beregn.felles.InntektUtil;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.Rolle;
import no.nav.bidrag.beregn.felles.enums.SoknadType;
import no.nav.bidrag.beregn.felles.inntekt.InntektPeriodeGrunnlag;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;

public class BidragsevnePeriodeImpl extends FellesPeriode implements BidragsevnePeriode {

  private final BidragsevneBeregning bidragsevneberegning;

  public BidragsevnePeriodeImpl(BidragsevneBeregning bidragsevneberegning) {
    this.bidragsevneberegning = bidragsevneberegning;
  }

  public BeregnetBidragsevneResultat beregnPerioder(BeregnBidragsevneGrunnlag beregnBidragsevneGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
    var justertInntektPeriodeListe = justerInntekter(beregnBidragsevneGrunnlag.getInntektPeriodeListe())
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSkatteklassePeriodeListe = beregnBidragsevneGrunnlag.getSkatteklassePeriodeListe()
        .stream()
        .map(SkatteklassePeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBostatusPeriodeListe = beregnBidragsevneGrunnlag.getBostatusPeriodeListe()
        .stream()
        .map(BostatusPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBarnIHusstandPeriodeListe = beregnBidragsevneGrunnlag.getBarnIHusstandPeriodeListe()
        .stream()
        .map(BarnIHusstandPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSaerfradragPeriodeListe = beregnBidragsevneGrunnlag.getSaerfradragPeriodeListe()
        .stream()
        .map(SaerfradragPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnBidragsevneGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnBidragsevneGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(justertInntektPeriodeListe)
        .addBruddpunkter(justertSkatteklassePeriodeListe)
        .addBruddpunkter(justertBostatusPeriodeListe)
        .addBruddpunkter(justertBarnIHusstandPeriodeListe)
        .addBruddpunkter(justertSaerfradragPeriodeListe)
        .addBruddpunkt(beregnBidragsevneGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnBidragsevneGrunnlag.getBeregnDatoFra(), beregnBidragsevneGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    mergeSluttperiode(perioder, beregnBidragsevneGrunnlag.getBeregnDatoTil());

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var inntektListe = justertInntektPeriodeListe.stream()
          .filter(inntektPeriode -> inntektPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getReferanse(), inntektPeriode.getType(), inntektPeriode.getBelop()))
          .collect(toList());

      var skatteklasse = justertSkatteklassePeriodeListe.stream()
          .filter(skatteklassePeriode -> skatteklassePeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(skatteklassePeriode -> new Skatteklasse(skatteklassePeriode.getReferanse(), skatteklassePeriode.getSkatteklasse()))
          .findFirst()
          .orElse(null);

      var bostatus = justertBostatusPeriodeListe.stream()
          .filter(bostatusPeriode -> bostatusPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(bostatusPeriode -> new Bostatus(bostatusPeriode.getReferanse(), bostatusPeriode.getKode()))
          .findFirst()
          .orElse(null);

      var barnIHusstand = justertBarnIHusstandPeriodeListe.stream()
          .filter(barnIHusstandPeriode -> barnIHusstandPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(barnIHusstandPeriode -> new BarnIHusstand(barnIHusstandPeriode.getReferanse(), barnIHusstandPeriode.getAntallBarn()))
          .findFirst()
          .orElse(null);

      var saerfradrag = justertSaerfradragPeriodeListe.stream()
          .filter(saerfradragPeriode -> saerfradragPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(saerfradragPeriode -> new Saerfradrag(saerfradragPeriode.getReferanse(), saerfradragPeriode.getKode()))
          .findFirst()
          .orElse(null);

      var sjablonliste = justertSjablonPeriodeListe.stream()
          .filter(sjablonPeriode -> sjablonPeriode.getPeriode().overlapperMed(beregningsperiode))
          .collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregning = new GrunnlagBeregning(inntektListe, skatteklasse, bostatus, barnIHusstand, saerfradrag, sjablonliste);
      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode, bidragsevneberegning.beregn(grunnlagBeregning), grunnlagBeregning));
    }

    return new BeregnetBidragsevneResultat(resultatPeriodeListe);

  }

  // Justerer inntekter basert på regler definert i InntektUtil (bidrag-beregn-felles)
  private List<InntektPeriode> justerInntekter(List<InntektPeriode> inntektPeriodeListe) {

    if (inntektPeriodeListe.isEmpty()) {
      return inntektPeriodeListe;
    }

    var justertInntektPeriodeListe = InntektUtil.justerInntekter(inntektPeriodeListe.stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getInntektPeriode(), inntektPeriode.getType(),
            inntektPeriode.getBelop(), false, false))
        .collect(toList()));

    return justertInntektPeriodeListe.stream()
        .map(inntektGrunnlag -> new InntektPeriode(inntektGrunnlag.getReferanse(), inntektGrunnlag.getInntektPeriode(), inntektGrunnlag.getType(),
            inntektGrunnlag.getBelop()))
        .sorted(comparing(inntektPeriode -> inntektPeriode.getInntektPeriode().getDatoFom()))
        .collect(toList());
  }


  // Validerer at input-verdier til bidragsevneberegning er gyldige
  public List<Avvik> validerInput(BeregnBidragsevneGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getPeriode());
    }
    var avvikListe = new ArrayList<>(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe",
        sjablonPeriodeListe, false, false, false, false));

    // Sjekk perioder for inntekt
    var inntektPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektPeriode : grunnlag.getInntektPeriodeListe()) {
      inntektPeriodeListe.add(inntektPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektPeriodeListe",
        inntektPeriodeListe, false, true, false, true));

    // Sjekk perioder for skatteklasse
    var skatteklassePeriodeListe = new ArrayList<Periode>();
    for (SkatteklassePeriode skatteklassePeriode : grunnlag.getSkatteklassePeriodeListe()) {
      skatteklassePeriodeListe.add(skatteklassePeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "skatteklassePeriodeListe", skatteklassePeriodeListe, true, true, true, true));

    // Sjekk perioder for bostatus
    var bostatusPeriodeListe = new ArrayList<Periode>();
    for (BostatusPeriode bostatusPeriode : grunnlag.getBostatusPeriodeListe()) {
      bostatusPeriodeListe.add(bostatusPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "bostatusPeriodeListe", bostatusPeriodeListe, true, true, true, true));

    // Sjekk perioder for barn i husstand
    var antallBarnIEgetHusholdPeriodeListe = new ArrayList<Periode>();
    for (BarnIHusstandPeriode antallBarnIEgetHusholdPeriode : grunnlag.getBarnIHusstandPeriodeListe()) {
      antallBarnIEgetHusholdPeriodeListe.add(antallBarnIEgetHusholdPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "barnIHusstandPeriodeListe", antallBarnIEgetHusholdPeriodeListe, false, false, false, true));

    // Sjekk perioder for særfradrag
    var saerfradragPeriodeListe = new ArrayList<Periode>();
    for (SaerfradragPeriode saerfradragPeriode : grunnlag.getSaerfradragPeriodeListe()) {
      saerfradragPeriodeListe.add(saerfradragPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "saerfradragPeriodeListe", saerfradragPeriodeListe, true, true, true, true));

    // Valider inntekter
    var inntektGrunnlagListe = grunnlag.getInntektPeriodeListe().stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getPeriode(), inntektPeriode.getType(),
            inntektPeriode.getBelop(), false, false))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.BIDRAGSPLIKTIG));

    return avvikListe;
  }
}
