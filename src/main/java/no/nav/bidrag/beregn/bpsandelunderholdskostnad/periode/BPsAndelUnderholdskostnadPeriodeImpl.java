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
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;

public class BPsAndelUnderholdskostnadPeriodeImpl implements BPsAndelUnderholdskostnadPeriode{
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

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()

      //  Her må det lages brudd for regelendringer !

        .addBruddpunkt(beregnBPsAndelUnderholdskostnadGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertInntekterPeriodeListe)
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

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
          bPsAndelUnderholdskostnadBeregning.beregn(beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert),
          beregnBPsAndelUnderholdskostnadGrunnlagPeriodisert));
    }

    //Slår sammen perioder med samme resultat
    return new BeregnBPsAndelUnderholdskostnadResultat(resultatPeriodeListe);


  }


  // Validerer at input-verdier til underholdskostnadsberegning er gyldige
  public List<Avvik> validerInput(BeregnBPsAndelUnderholdskostnadGrunnlag beregnBPsAndelUnderholdskostnadGrunnlag) {
    var avvikListe = new ArrayList<Avvik>();

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : beregnBPsAndelUnderholdskostnadGrunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("sjablonPeriodeListe", sjablonPeriodeListe, false, false, false));

    // Sjekk perioder for barnetilsynMedStonad
    var inntekterPeriodeListe = new ArrayList<Periode>();
    for (InntekterPeriode inntekterPeriode : beregnBPsAndelUnderholdskostnadGrunnlag.getInntekterPeriodeListe()) {
      inntekterPeriodeListe.add(inntekterPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("inntekterListe", inntekterPeriodeListe, true, true, true));


    // Sjekk beregn dato fra/til
    avvikListe.addAll(validerBeregnPeriodeInput(beregnBPsAndelUnderholdskostnadGrunnlag.getBeregnDatoFra(), beregnBPsAndelUnderholdskostnadGrunnlag.getBeregnDatoTil()));

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