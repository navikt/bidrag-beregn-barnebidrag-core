/*
package no.nav.bidrag.beregn.barnebidrag.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning;

import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaretPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.KostnadsberegnetBidragPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;


public class BarnebidragPeriodeImpl implements BarnebidragPeriode {
  public BarnebidragPeriodeImpl(
      BarnebidragBeregning kostnadsberegnetBidragberegning) {
    this.barnebidragBeregning = barnebidragBeregning;
  }

  private BarnebidragBeregning barnebidragBeregning;

  public BeregnBarnebidragResultat beregnPerioder(
      BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertBidragsevnePeriodeListe = beregnBarnebidragGrunnlag.getBidragsevnePeriodeListe()
        .stream()
        .map(BidragsevnePeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertKostnadsberegnetBidragPeriodeListe = beregnBarnebidragGrunnlag.getGrunnlagPerBarnPeriodeListe()
         .getKostnadsberegnetBidragPeriodeListe()
        .stream()
        .map(KostnadsberegnetBidragPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSamvaersfradragPeriodeListe = beregnBarnebidragGrunnlag.getSamvaersfradragPeriodeListe()
        .stream()
        .map(SamvaersfradragPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBarnetilleggBPPeriodeListe = beregnBarnebidragGrunnlag.getBarnetilleggBPPeriodeListe()
        .stream()
        .map(BarnetilleggBPPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBarnetilleggBMPeriodeListe = beregnBarnebidragGrunnlag.getBarnetilleggBMPeriodeListe()
        .stream()
        .map(BarnetilleggBMPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBarnetilleggForsvaretPeriodeListe = beregnBarnebidragGrunnlag.getBarnetilleggForsvaretPeriodeListe()
        .stream()
        .map(BarnetilleggForsvaretPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnBarnebidragGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnBarnebidragGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertBidragsevnePeriodeListe)
        .addBruddpunkter(justertKostnadsberegnetBidragPeriodeListe)
        .addBruddpunkter(justertSamvaersfradragPeriodeListe)
        .addBruddpunkter(justertBarnetilleggBPPeriodeListe)
        .addBruddpunkter(justertBarnetilleggBMPeriodeListe)
        .addBruddpunkter(justertBarnetilleggForsvaretPeriodeListe)
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkt(beregnBarnebidragGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnBarnebidragGrunnlag.getBeregnDatoFra(),
            beregnBarnebidragGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnBarnebidragGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var bidragsevneBelop = justertBidragsevnePeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(BidragsevnePeriode::getBidragsevneBelop).findFirst().orElse(null);

      var kostnadsberegnetBidragBelop = justertKostnadsberegnetBidragPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(KostnadsberegnetBidragPeriode::getKostnadsberegnetBidragBelop).findFirst().orElse(null);

      var samvaersfradragBelop = justertSamvaersfradragPeriodeListe.stream().filter(i ->
          i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(SamvaersfradragPeriode::getSamvaersfradrag).findFirst().orElse(null);

      var barnetilleggBP = justertBarnetilleggBPPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(barnetilleggBPPeriode -> new BarnetilleggBP(barnetilleggBPPeriode.getBarnetilleggBPBelop(),
          barnetilleggBPPeriode.getBarnetilleggBPSkattProsent())).findFirst().orElse(null);

      var barnetilleggBM = justertBarnetilleggBMPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(barnetilleggBMPeriode -> new BarnetilleggBM(barnetilleggBMPeriode.getBarnetilleggBMBelop(),
              barnetilleggBMPeriode.getBarnetilleggBMSkattProsent())).findFirst().orElse(null);

      var barnetilleggForsvaret = justertBarnetilleggForsvaretPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(barnetilleggForsvaretPeriode -> new BarnetilleggForsvaret(barnetilleggForsvaretPeriode.getBarnetilleggForsvaretIPeriode(),
              barnetilleggForsvaretPeriode.getBarnetilleggForsvaretAntallBarn())).findFirst().orElse(null);

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      System.out.println("Samværsfradrag: " + samvaersfradragBelop);

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
          bidragsevneBelop, kostnadsberegnetBidragBelop, samvaersfradragBelop, barnetilleggBP,
          barnetilleggBM, barnetilleggForsvaret, sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
          barnebidragBeregning.beregn(grunnlagBeregningPeriodisert),
          grunnlagBeregningPeriodisert));
    }

    return new BeregnBarnebidragResultat(resultatPeriodeListe);
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
      barnetilleggForsvaretPeriodeListe.add(barnetilleggForsvaretPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "barnetilleggForsvaretPeriodeListe",
        bidragsevnePeriodeListe, true, true, true, true));

    return avvikListe;
  }
}
*/
