package no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning.BPsAndelUnderholdskostnadBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekter;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntekterPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
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

    var justertInntekterPeriodeListe = beregnBPsAndelUnderholdskostnadGrunnlag.getInntekterPeriodeListe()
        .stream()
        .map(InntekterPeriode::new)
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
        .addBruddpunkter(justertInntekterPeriodeListe)
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

      var inntekter = justertInntekterPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(inntekterPeriode -> new Inntekter(inntekterPeriode.getInntektBP(), inntekterPeriode.getInntektBM(),
              inntekterPeriode.getInntektBB())).findFirst().orElse(null);

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert = new BeregnBPsAndelUnderholdskostnadGrunnlagPeriodisert(
          inntekter, sjablonliste);

      // Beregner med gamle regler hvis periodens beregntilogmeddato er 01.01.2009 eller tidligere
      if (beregningsperiode.getDatoTil() == null ||
          beregningsperiode.getDatoFraTil().getDatoTil().isAfter(LocalDate.parse("2009-01-01"))) {
        System.out.println("Beregner med nye regler, tomdato: " + beregningsperiode.getDatoFraTil().getDatoTil());
        resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
            bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert),
            beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert));
      } else {
        System.out.println("Beregner med gamle regler, tomdato: " + beregningsperiode.getDatoFraTil().getDatoTil());
        resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
            bPsAndelUnderholdskostnadBeregning.beregnMedGamleRegler(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert),
            beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert));
      }
    }

    //Slår sammen perioder med samme resultat
    return new BeregnBPsAndelUnderholdskostnadResultat(resultatPeriodeListe);
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

    // Sjekk perioder for barnetilsynMedStonad
    var inntekterPeriodeListe = new ArrayList<Periode>();
    for (InntekterPeriode inntekterPeriode : grunnlag.getInntekterPeriodeListe()) {
      inntekterPeriodeListe.add(inntekterPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "inntektPeriodeListe",
        inntekterPeriodeListe, false, true, false, true));

    return avvikListe;
  }
}
