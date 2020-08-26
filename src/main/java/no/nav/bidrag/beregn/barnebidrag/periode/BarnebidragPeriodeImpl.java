package no.nav.bidrag.beregn.barnebidrag.periode;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.UnderholdskostnadPeriode;

public class BarnebidragPeriodeImpl implements BarnebidragPeriode {
  public BarnebidragPeriodeImpl(
      BarnebidragBeregning kostnadsberegnetBidragberegning) {
    this.barnebidragBeregning = barnebidragBeregning;
  }

  private BarnebidragBeregning barnebidragBeregning;

  public BeregnBarnebidragResultat beregnPerioder(
      BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag) {

    return null;

  }

  @Override
  public Integer beregnSoknadbarnAlder(
      BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag,
      LocalDate beregnDatoFra) {

    LocalDate tempSoknadbarnFodselsdato = beregnBarnebidragGrunnlag.getSoknadsbarnFodselsdato();
    Integer beregnetAlder = Period.between(tempSoknadbarnFodselsdato, beregnDatoFra).getYears();

    return beregnetAlder;
  }



  // Validerer at input-verdier til underholdskostnadsberegning er gyldige
  public List<Avvik> validerInput(BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag) {
    var avvikListe = new ArrayList<Avvik>();

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : beregnBarnebidragGrunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("sjablonPeriodeListe", sjablonPeriodeListe, false, false, false));

    // Sjekk perioder for underholdskostnad
    var underholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (UnderholdskostnadPeriode underholdskostnadPeriode : beregnBarnebidragGrunnlag.getUnderholdskostnadPeriodeListe()) {
      underholdskostnadPeriodeListe.add(underholdskostnadPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("underholdskostnadListe", underholdskostnadPeriodeListe, true, true, true));

    // Sjekk perioder for BPs andel av underholdskostnad
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (BPsAndelUnderholdskostnadPeriode bPsAndelUnderholdskostnadPeriode : beregnBarnebidragGrunnlag
        .getBPsAndelUnderholdskostnadPeriodeListe()) {
      bPsAndelUnderholdskostnadPeriodeListe.add(bPsAndelUnderholdskostnadPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("nettoBarnetilsynPeriodeListe", bPsAndelUnderholdskostnadPeriodeListe, true, true, true));

  /*  // Sjekk perioder for samværsklasse
    var samvaersklassePeriodeListe = new ArrayList<Periode>();
    for (SamvaersklassePeriode samvaersklassePeriode : beregnBarnebidragGrunnlag.getSamvaersklassePeriodeListe()) {
      samvaersklassePeriodeListe.add(samvaersklassePeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("samværsklassePeriodeListe", samvaersklassePeriodeListe, true, true, true));
*/
    // Sjekk beregn dato fra/til
    avvikListe.addAll(validerBeregnPeriodeInput(beregnBarnebidragGrunnlag.getBeregnDatoFra(), beregnBarnebidragGrunnlag
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


