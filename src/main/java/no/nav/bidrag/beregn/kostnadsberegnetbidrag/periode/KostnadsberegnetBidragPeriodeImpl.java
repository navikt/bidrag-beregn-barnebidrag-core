package no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode;

import static java.util.stream.Collectors.toCollection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragResultat;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode;

public class KostnadsberegnetBidragPeriodeImpl implements KostnadsberegnetBidragPeriode {
  public KostnadsberegnetBidragPeriodeImpl(
      KostnadsberegnetBidragBeregning kostnadsberegnetBidragBeregning) {
    this.kostnadsberegnetBidragBeregning = kostnadsberegnetBidragBeregning;
  }

  private KostnadsberegnetBidragBeregning kostnadsberegnetBidragBeregning;

  public BeregnKostnadsberegnetBidragResultat beregnPerioder(
      BeregnKostnadsberegnetBidragGrunnlag beregnKostnadsberegnetBidragGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertUnderholdskostnadPeriodeListe = beregnKostnadsberegnetBidragGrunnlag.getUnderholdskostnadPeriodeListe()
        .stream()
        .map(UnderholdskostnadPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBPsAndelUnderholdskostnadPeriodeListe = beregnKostnadsberegnetBidragGrunnlag.getBPsAndelUnderholdskostnadPeriodeListe()
        .stream()
        .map(BPsAndelUnderholdskostnadPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSamvaersfradragPeriodeListe = beregnKostnadsberegnetBidragGrunnlag.getSamvaersfradragPeriodeListe()
        .stream()
        .map(SamvaersfradragPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertUnderholdskostnadPeriodeListe)
        .addBruddpunkter(justertBPsAndelUnderholdskostnadPeriodeListe)
        .addBruddpunkter(justertSamvaersfradragPeriodeListe)
        .addBruddpunkt(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoFra(),
            beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var underholdskostnadBelop = justertUnderholdskostnadPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(UnderholdskostnadPeriode::getUnderholdskostnadBelop).findFirst().orElse(null);

      var bPsAndelUnderholdskostnadProsent = justertBPsAndelUnderholdskostnadPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(BPsAndelUnderholdskostnadPeriode::getBPsAndelUnderholdskostnadProsent).findFirst().orElse(null);

      var samvaersfradragBelop = justertSamvaersfradragPeriodeListe.stream().filter(i ->
          i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(SamvaersfradragPeriode::getSamvaersfradrag).findFirst().orElse(null);

      System.out.println("Samværsfradrag: " + samvaersfradragBelop);

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
          underholdskostnadBelop, bPsAndelUnderholdskostnadProsent, samvaersfradragBelop);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
          kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert),
          grunnlagBeregningPeriodisert));
    }

    return new BeregnKostnadsberegnetBidragResultat(resultatPeriodeListe);
  }

  // Validerer at input-verdier til underholdskostnadsberegning er gyldige
  public List<Avvik> validerInput(
      BeregnKostnadsberegnetBidragGrunnlag beregnKostnadsberegnetBidragGrunnlag) {
    var avvikListe = new ArrayList<Avvik>();

    // Sjekk perioder for underholdskostnad
    var underholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (UnderholdskostnadPeriode underholdskostnadPeriode : beregnKostnadsberegnetBidragGrunnlag.getUnderholdskostnadPeriodeListe()) {
      underholdskostnadPeriodeListe.add(underholdskostnadPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("underholdskostnadListe", underholdskostnadPeriodeListe, true, true, true));

    // Sjekk perioder for BPs andel av underholdskostnad
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (BPsAndelUnderholdskostnadPeriode bPsAndelUnderholdskostnadPeriode : beregnKostnadsberegnetBidragGrunnlag
        .getBPsAndelUnderholdskostnadPeriodeListe()) {
      bPsAndelUnderholdskostnadPeriodeListe.add(bPsAndelUnderholdskostnadPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("bPsAndelUnderholdskostnadPeriodeListe", bPsAndelUnderholdskostnadPeriodeListe, true, true, true));

    // Sjekk perioder for samværsfradrag
    var samvaersfradragPeriodeListe = new ArrayList<Periode>();
    for (SamvaersfradragPeriode samvaersfradragPeriode : beregnKostnadsberegnetBidragGrunnlag.getSamvaersfradragPeriodeListe()) {
      samvaersfradragPeriodeListe.add(samvaersfradragPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("samvaersfradragPeriodeListe", samvaersfradragPeriodeListe, true, true, true));

    // Sjekk beregn dato fra/til
    avvikListe.addAll(validerBeregnPeriodeInput(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoFra(), beregnKostnadsberegnetBidragGrunnlag
        .getBeregnDatoTil()));

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


