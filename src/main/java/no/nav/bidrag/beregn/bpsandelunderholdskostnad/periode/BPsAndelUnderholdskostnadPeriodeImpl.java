package no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning.BPsAndelUnderholdskostnadBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.felles.InntektUtil;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.Rolle;
import no.nav.bidrag.beregn.felles.enums.SoknadType;
import no.nav.bidrag.beregn.felles.inntekt.InntektGrunnlag;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;

public class BPsAndelUnderholdskostnadPeriodeImpl implements BPsAndelUnderholdskostnadPeriode {

  public BPsAndelUnderholdskostnadPeriodeImpl(
      BPsAndelUnderholdskostnadBeregning bPsAndelUnderholdskostnadBeregning) {
    this.bPsAndelUnderholdskostnadBeregning = bPsAndelUnderholdskostnadBeregning;
  }

  private BPsAndelUnderholdskostnadBeregning bPsAndelUnderholdskostnadBeregning;

  public BeregnBPsAndelUnderholdskostnadResultat beregnPerioder(
      BeregnBPsAndelUnderholdskostnadGrunnlag beregnBPsAndelUnderholdskostnadGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
    var justertUnderholdskostnadPeriodeListe = beregnBPsAndelUnderholdskostnadGrunnlag.getUnderholdskostnadListe()
        .stream()
        .map(UnderholdskostnadPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBPPeriodeListe = justerInntekter(beregnBPsAndelUnderholdskostnadGrunnlag.getInntektBPPeriodeListe())
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBMPeriodeListe = justerInntekter(beregnBPsAndelUnderholdskostnadGrunnlag.getInntektBMPeriodeListe())
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertInntektBBPeriodeListe = justerInntekter(beregnBPsAndelUnderholdskostnadGrunnlag.getInntektBBPeriodeListe())
        .stream()
        .map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnBPsAndelUnderholdskostnadGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Regler for beregning av BPs andel ble endret fra 01.01.2009, alle perioder etter da skal beregnes på ny måte.
    // Det må derfor legges til brudd på denne datoen
    var datoRegelendringer = new ArrayList<Periode>();
    datoRegelendringer.add(new Periode(LocalDate.parse("2009-01-01"), LocalDate.parse("2009-01-01")));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
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
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnBPsAndelUnderholdskostnadGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var underholdskostnad = justertUnderholdskostnadPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(UnderholdskostnadPeriode::getUnderholdskostnadBelop).findFirst().orElse(null);

      var inntektBP = justertInntektBPPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getInntektType(),
              inntektPeriode.getInntektBelop())).collect(toList());

      var inntektBM = justertInntektBMPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getInntektType(),
              inntektPeriode.getInntektBelop())).collect(toList());

      var inntektBB = justertInntektBBPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getInntektType(),
              inntektPeriode.getInntektBelop())).collect(toList());

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert = new GrunnlagBeregningPeriodisert(
          underholdskostnad, inntektBP, inntektBM, inntektBB, sjablonliste);

      // Beregner med gamle regler hvis periodens beregntilogmeddato er 01.01.2009 eller tidligere
      if (beregningsperiode.getDatoTil() == null ||
          beregningsperiode.getDatoFraTil().getDatoTil().isAfter(LocalDate.parse("2009-01-01"))) {
        System.out.println("Beregner med nye regler, tomdato: " + beregningsperiode.getDatoFraTil().getDatoTil());
        resultatPeriodeListe.add(new ResultatPeriode(
            beregnBPsAndelUnderholdskostnadGrunnlag.getSoknadsbarnPersonId(),
            beregningsperiode, bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert),
            beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert));
      } else {
        System.out.println("Beregner med gamle regler, tomdato: " + beregningsperiode.getDatoFraTil().getDatoTil());
        resultatPeriodeListe.add(new ResultatPeriode(
            beregnBPsAndelUnderholdskostnadGrunnlag.getSoknadsbarnPersonId(),
            beregningsperiode,
            bPsAndelUnderholdskostnadBeregning.beregnMedGamleRegler(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert),
            beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert));
      }
    }

    //Slår sammen perioder med samme resultat
    return new BeregnBPsAndelUnderholdskostnadResultat(resultatPeriodeListe);
  }

  // Justerer inntekter basert på regler definert i InntektUtil (bidrag-beregn-felles)
  private List<InntektPeriode> justerInntekter(List<InntektPeriode> inntektPeriodeListe) {

    if (inntektPeriodeListe.isEmpty()) {
      return inntektPeriodeListe;
    }

    var justertInntektPeriodeListe = InntektUtil.justerInntekter(inntektPeriodeListe.stream()
        .map(inntektPeriode -> new InntektGrunnlag(inntektPeriode.getInntektDatoFraTil(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop()))
        .collect(toList()));

    return justertInntektPeriodeListe.stream()
        .map(inntektGrunnlag -> new InntektPeriode(inntektGrunnlag.getInntektDatoFraTil(), inntektGrunnlag.getInntektType(),
            inntektGrunnlag.getInntektBelop()))
        .sorted(comparing(inntektPeriode -> inntektPeriode.getInntektDatoFraTil().getDatoFra()))
        .collect(toList());
  }


  // Validerer at input-verdier til underholdskostnadsberegning er gyldige
  public List<Avvik> validerInput(BeregnBPsAndelUnderholdskostnadGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe",
            sjablonPeriodeListe, false, false, false, false));

    // Sjekk perioder for inntektBP
    var inntektBPPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBPPeriode : grunnlag.getInntektBPPeriodeListe()) {
      inntektBPPeriodeListe.add(inntektBPPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBPPeriodeListe",
        inntektBPPeriodeListe, false, true, false, true));

    // Sjekk perioder for inntektBM
    var inntektBMPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBMPeriode : grunnlag.getInntektBMPeriodeListe()) {
      inntektBMPeriodeListe.add(inntektBMPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBMPeriodeListe",
        inntektBMPeriodeListe, false, true, false, true));

    // Sjekk perioder for inntektBB
    var inntektBBPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode inntektBBPeriode : grunnlag.getInntektBBPeriodeListe()) {
      inntektBBPeriodeListe.add(inntektBBPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektBBPeriodeListe",
        inntektBPPeriodeListe, false, true, false, true));

    // Valider inntekter BP
    var inntektGrunnlagListe = grunnlag.getInntektBPPeriodeListe().stream()
        .map(inntektPeriode -> new InntektGrunnlag(inntektPeriode.getInntektDatoFraTil(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop()))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.BIDRAGSPLIKTIG));

    // Valider inntekter BM
    inntektGrunnlagListe = grunnlag.getInntektBMPeriodeListe().stream()
        .map(inntektPeriode -> new InntektGrunnlag(inntektPeriode.getInntektDatoFraTil(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop()))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.BIDRAGSMOTTAKER));

    // Valider inntekter BB
    inntektGrunnlagListe = grunnlag.getInntektBBPeriodeListe().stream()
        .map(inntektPeriode -> new InntektGrunnlag(inntektPeriode.getInntektDatoFraTil(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop()))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.BIDRAG, Rolle.SOKNADSBARN));

    return avvikListe;
  }
}
