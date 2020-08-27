package no.nav.bidrag.beregn.barnebidrag.periode;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;

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
  public List<Avvik> validerInput(BeregnBarnebidragGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),"sjablonPeriodeListe", sjablonPeriodeListe,
            false, false, false, false));

    // Sjekk perioder for underholdskostnad
    var underholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (UnderholdskostnadPeriode underholdskostnadPeriode : grunnlag.getUnderholdskostnadPeriodeListe()) {
      underholdskostnadPeriodeListe.add(underholdskostnadPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),"underholdskostnadListe",
        underholdskostnadPeriodeListe, true, true, true, true));

    // Sjekk perioder for BPs andel av underholdskostnad
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (BPsAndelUnderholdskostnadPeriode bPsAndelUnderholdskostnadPeriode : grunnlag
        .getBPsAndelUnderholdskostnadPeriodeListe()) {
      bPsAndelUnderholdskostnadPeriodeListe.add(bPsAndelUnderholdskostnadPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),"nettoBarnetilsynPeriodeListe",
        bPsAndelUnderholdskostnadPeriodeListe, true, true, true, true));

  /*  // Sjekk perioder for samværsklasse
    var samvaersklassePeriodeListe = new ArrayList<Periode>();
    for (SamvaersklassePeriode samvaersklassePeriode : beregnBarnebidragGrunnlag.getSamvaersklassePeriodeListe()) {
      samvaersklassePeriodeListe.add(samvaersklassePeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("samværsklassePeriodeListe", samvaersklassePeriodeListe, true, true, true));
*/

    return avvikListe;
  }
}


