package no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning.BPsAndelUnderholdskostnadBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnetBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Underholdskostnad;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.UnderholdskostnadPeriode;
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

public class BPsAndelUnderholdskostnadPeriodeImpl extends FellesPeriode implements BPsAndelUnderholdskostnadPeriode {

  private final LocalDate regelendringsdato = LocalDate.parse("2009-01-01");

  private final BPsAndelUnderholdskostnadBeregning bPsAndelUnderholdskostnadBeregning;

  public BPsAndelUnderholdskostnadPeriodeImpl(BPsAndelUnderholdskostnadBeregning bPsAndelUnderholdskostnadBeregning) {
    this.bPsAndelUnderholdskostnadBeregning = bPsAndelUnderholdskostnadBeregning;
  }

  public BeregnetBPsAndelUnderholdskostnadResultat beregnPerioder(BeregnBPsAndelUnderholdskostnadGrunnlag beregnBPsAndelUnderholdskostnadGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
    var justertUnderholdskostnadPeriodeListe = beregnBPsAndelUnderholdskostnadGrunnlag.getUnderholdskostnadPeriodeListe()
        .stream()
        .map(UnderholdskostnadPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnBPsAndelUnderholdskostnadGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBPPeriodeListe = justerInntekter(beregnBPsAndelUnderholdskostnadGrunnlag.getInntektBPPeriodeListe())
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBMPeriodeListe = behandlUtvidetBarnetrygd(justerInntekter(beregnBPsAndelUnderholdskostnadGrunnlag.getInntektBMPeriodeListe()),
        justertSjablonPeriodeListe)
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBBPeriodeListe = justerInntekter(beregnBPsAndelUnderholdskostnadGrunnlag.getInntektBBPeriodeListe())
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Regler for beregning av BPs andel ble endret fra 01.01.2009, alle perioder etter da skal beregnes på ny måte.
    // Det må derfor legges til brudd på denne datoen
    var datoRegelendringer = new ArrayList<Periode>();
    datoRegelendringer.add(new Periode(regelendringsdato, regelendringsdato));

    // Bygger opp liste over perioder
    var perioder = new Periodiserer()
        .addBruddpunkt(beregnBPsAndelUnderholdskostnadGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkter(datoRegelendringer)
        .addBruddpunkter(justertUnderholdskostnadPeriodeListe)
        .addBruddpunkter(justertInntektBPPeriodeListe)
        .addBruddpunkter(justertInntektBMPeriodeListe)
        .addBruddpunkter(justertInntektBBPeriodeListe)
        .addBruddpunkt(beregnBPsAndelUnderholdskostnadGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnBPsAndelUnderholdskostnadGrunnlag.getBeregnDatoFra(), beregnBPsAndelUnderholdskostnadGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    mergeSluttperiode(perioder, beregnBPsAndelUnderholdskostnadGrunnlag.getBeregnDatoTil());

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var underholdskostnad = justertUnderholdskostnadPeriodeListe.stream()
          .filter(underholdskostnadPeriode -> underholdskostnadPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(underholdskostnadPeriode -> new Underholdskostnad(underholdskostnadPeriode.getReferanse(), underholdskostnadPeriode.getBelop()))
          .findFirst()
          .orElse(null);

      var inntektBPListe = justertInntektBPPeriodeListe.stream()
          .filter(inntektPeriode -> inntektPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getReferanse(), inntektPeriode.getType(), inntektPeriode.getBelop(),
              inntektPeriode.getDeltFordel(), inntektPeriode.getSkatteklasse2()))
          .collect(toList());

      var inntektBMListe = justertInntektBMPeriodeListe.stream()
          .filter(inntektPeriode -> inntektPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getReferanse(), inntektPeriode.getType(), inntektPeriode.getBelop(),
              inntektPeriode.getDeltFordel(), inntektPeriode.getSkatteklasse2()))
          .collect(toList());

      var inntektBBListe = justertInntektBBPeriodeListe.stream()
          .filter(inntektPeriode -> inntektPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getReferanse(), inntektPeriode.getType(), inntektPeriode.getBelop(),
              inntektPeriode.getDeltFordel(), inntektPeriode.getSkatteklasse2()))
          .collect(toList());

      var sjablonliste = justertSjablonPeriodeListe.stream()
          .filter(sjablonPeriode -> sjablonPeriode.getPeriode().overlapperMed(beregningsperiode))
          .collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregning = new GrunnlagBeregning(underholdskostnad, inntektBPListe, inntektBMListe, inntektBBListe, sjablonliste);

      // Beregner med gamle regler hvis periodens beregntilogmeddato er 01.01.2009 eller tidligere
      var brukNyeRegler = (beregningsperiode.getPeriode().getDatoTil() == null) ||
          (beregningsperiode.getPeriode().getDatoTil().isAfter(regelendringsdato));

      resultatPeriodeListe.add(new ResultatPeriode(beregnBPsAndelUnderholdskostnadGrunnlag.getSoknadsbarnPersonId(),
            beregningsperiode, bPsAndelUnderholdskostnadBeregning.beregn(grunnlagBeregning, brukNyeRegler),
            grunnlagBeregning));
    }

    return new BeregnetBPsAndelUnderholdskostnadResultat(resultatPeriodeListe);
  }

  // Justerer inntekter basert på regler definert i InntektUtil (bidrag-beregn-felles)
  private List<InntektPeriode> justerInntekter(List<InntektPeriode> inntektPeriodeListe) {

    if (inntektPeriodeListe.isEmpty()) {
      return inntektPeriodeListe;
    }

    var justertInntektPeriodeListe = InntektUtil.justerInntekter(inntektPeriodeListe.stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getInntektPeriode(), inntektPeriode.getType(),
            inntektPeriode.getBelop(), inntektPeriode.getDeltFordel(), inntektPeriode.getSkatteklasse2()))
        .collect(toList()));

    return justertInntektPeriodeListe.stream()
        .map(inntektGrunnlag -> new InntektPeriode(inntektGrunnlag.getReferanse(), inntektGrunnlag.getInntektPeriode(), inntektGrunnlag.getType(),
            inntektGrunnlag.getBelop(), inntektGrunnlag.getDeltFordel(), inntektGrunnlag.getSkatteklasse2()))
        .sorted(comparing(inntektPeriode -> inntektPeriode.getInntektPeriode().getDatoFom()))
        .collect(toList());
  }

  // Sjekker om det skal legges til inntekt for fordel særfradrag enslig forsørger og skatteklasse 2 (kun BM)
  private List<InntektPeriode> behandlUtvidetBarnetrygd(List<InntektPeriode> inntektPeriodeListe, List<SjablonPeriode> sjablonPeriodeListe) {

    if (inntektPeriodeListe.isEmpty()) {
      return inntektPeriodeListe;
    }

    var justertInntektPeriodeListe = InntektUtil.behandlUtvidetBarnetrygd(inntektPeriodeListe.stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getPeriode(), inntektPeriode.getType(),
            inntektPeriode.getBelop(), inntektPeriode.getDeltFordel(), inntektPeriode.getSkatteklasse2()))
        .collect(toList()),
        sjablonPeriodeListe);

    return justertInntektPeriodeListe.stream()
        .map(inntektGrunnlag -> new InntektPeriode(inntektGrunnlag.getReferanse(), inntektGrunnlag.getInntektPeriode(), inntektGrunnlag.getType(),
            inntektGrunnlag.getBelop(), inntektGrunnlag.getDeltFordel(), inntektGrunnlag.getSkatteklasse2()))
        .sorted(comparing(inntektPeriode -> inntektPeriode.getInntektPeriode().getDatoFom()))
        .collect(toList());
  }

  // Validerer at input-verdier til underholdskostnadsberegning er gyldige
  public List<Avvik> validerInput(BeregnBPsAndelUnderholdskostnadGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getPeriode());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe",
            sjablonPeriodeListe, false, false, false, false));

    // Sjekk perioder for inntektBP
    var inntektBPPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBPPeriode : grunnlag.getInntektBPPeriodeListe()) {
      inntektBPPeriodeListe.add(inntektBPPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBPPeriodeListe",
        inntektBPPeriodeListe, false, true, false, true));

    // Sjekk perioder for inntektBM
    var inntektBMPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBMPeriode : grunnlag.getInntektBMPeriodeListe()) {
      inntektBMPeriodeListe.add(inntektBMPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBMPeriodeListe",
        inntektBMPeriodeListe, false, true, false, true));

    // Sjekk perioder for inntektBB
    var inntektBBPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBBPeriode : grunnlag.getInntektBBPeriodeListe()) {
      inntektBBPeriodeListe.add(inntektBBPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBBPeriodeListe",
        inntektBBPeriodeListe, false, true, false, true));

    // Valider inntekter BP
    var inntektGrunnlagListe = grunnlag.getInntektBPPeriodeListe().stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getInntektPeriode(), inntektPeriode.getType(),
            inntektPeriode.getBelop(), inntektPeriode.getDeltFordel(), inntektPeriode.getSkatteklasse2()))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.BIDRAGSPLIKTIG));

    // Valider inntekter BM
    inntektGrunnlagListe = grunnlag.getInntektBMPeriodeListe().stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getInntektPeriode(), inntektPeriode.getType(),
            inntektPeriode.getBelop(), inntektPeriode.getDeltFordel(), inntektPeriode.getSkatteklasse2()))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.BIDRAGSMOTTAKER));

    // Valider inntekter BB
    inntektGrunnlagListe = grunnlag.getInntektBBPeriodeListe().stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getInntektPeriode(), inntektPeriode.getType(),
            inntektPeriode.getBelop(), inntektPeriode.getDeltFordel(), inntektPeriode.getSkatteklasse2()))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.SOKNADSBARN));

    return avvikListe;
  }
}
