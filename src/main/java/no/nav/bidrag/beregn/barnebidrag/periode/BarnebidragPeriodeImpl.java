package no.nav.bidrag.beregn.barnebidrag.periode;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggBMPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggBPPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaretPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.KostnadsberegnetBidragPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.SamvaersfradragPeriode;
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

  // Validerer at input-verdier til beregn-barnebidrag er gyldige
  public List<Avvik> validerInput(BeregnBarnebidragGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe", sjablonPeriodeListe,
            false, false, false, false));

    // Sjekk perioder for bidragsevne
    var bidragsevnePeriodeListe = new ArrayList<Periode>();
    for (BidragsevnePeriode bidragsevnePeriode : grunnlag.getBidragsevnePeriodeListe()) {
      bidragsevnePeriodeListe.add(bidragsevnePeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),"bidragsevnePeriodeListe",
        bidragsevnePeriodeListe, true, true, true, true));

    // Sjekk perioder for kostnadsberegnet bidrag
    var kostnadsberegnetBidragPeriodeListe = new ArrayList<Periode>();
    for (KostnadsberegnetBidragPeriode kostnadsberegnetBidragPeriode : grunnlag.getKostnadsberegnetBidragPeriodeListe()) {
      kostnadsberegnetBidragPeriodeListe.add(kostnadsberegnetBidragPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "kostnadsberegnetBidragPeriodeListe",
        bidragsevnePeriodeListe, true, true, true, true));

    // Sjekk perioder for samværsfradrag
    var samvaersfradragPeriodeListe = new ArrayList<Periode>();
    for (SamvaersfradragPeriode samvaersfradragPeriode : grunnlag.getSamvaersfradragPeriodeListe()) {
      samvaersfradragPeriodeListe.add(samvaersfradragPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "samvaersfradragPeriodeListe",
        bidragsevnePeriodeListe, true, true, true, true));

    // Sjekk perioder for barnetillegg BP
    var barnetilleggBPPeriodeListe = new ArrayList<Periode>();
    for (BarnetilleggBPPeriode barnetilleggBPPeriode : grunnlag.getBarnetilleggBPPeriodeListe()) {
      barnetilleggBPPeriodeListe.add(barnetilleggBPPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "barnetilleggBPPeriodeListe",
        bidragsevnePeriodeListe, true, true, true, true));

    // Sjekk perioder for barnetillegg BM
    var barnetilleggBMPeriodeListe = new ArrayList<Periode>();
    for (BarnetilleggBMPeriode barnetilleggBMPeriode : grunnlag.getBarnetilleggBMPeriodeListe()) {
      barnetilleggBMPeriodeListe.add(barnetilleggBMPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "barnetilleggBMPeriodeListe",
        bidragsevnePeriodeListe, true, true, true, true));

    // Sjekk perioder for barnetillegg fra forsvaret
    var barnetilleggForsvaretPeriodeListe = new ArrayList<Periode>();
    for (BarnetilleggForsvaretPeriode barnetilleggForsvaretPeriode : grunnlag.getBarnetilleggForsvaretPeriodeListe()) {
      barnetilleggBMPeriodeListe.add(barnetilleggForsvaretPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "barnetilleggForsvaretPeriodeListe",
        bidragsevnePeriodeListe, true, true, true, true));

    return avvikListe;
  }
}
