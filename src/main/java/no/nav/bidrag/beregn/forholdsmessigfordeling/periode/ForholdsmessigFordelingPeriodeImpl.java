package no.nav.bidrag.beregn.forholdsmessigfordeling.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.forholdsmessigfordeling.beregning.ForholdsmessigFordelingBeregning;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingGrunnlag;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingResultat;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSak;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSakPeriode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.Bidragsevne;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPeriode;


public class ForholdsmessigFordelingPeriodeImpl implements ForholdsmessigFordelingPeriode {
  public ForholdsmessigFordelingPeriodeImpl(
      ForholdsmessigFordelingBeregning forholdsmessigFordelingBeregning) {
    this.forholdsmessigFordelingBeregning = forholdsmessigFordelingBeregning;
  }

  private ForholdsmessigFordelingBeregning forholdsmessigFordelingBeregning;

  public BeregnForholdsmessigFordelingResultat beregnPerioder(
      BeregnForholdsmessigFordelingGrunnlag beregnForholdsmessigFordelingGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertBidragsevnePeriodeListe =
        beregnForholdsmessigFordelingGrunnlag.getBidragsevnePeriodeListe()
        .stream()
        .map(BidragsevnePeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBeregnedeBidragSakPeriodeListe =
        beregnForholdsmessigFordelingGrunnlag.getBeregnetBidragPeriodeListe()
        .stream()
        .map(BeregnetBidragSakPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnForholdsmessigFordelingGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertBidragsevnePeriodeListe)
        .addBruddpunkter(justertBeregnedeBidragSakPeriodeListe)
        .addBruddpunkt(beregnForholdsmessigFordelingGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnForholdsmessigFordelingGrunnlag.getBeregnDatoFra(),
            beregnForholdsmessigFordelingGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnForholdsmessigFordelingGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFom(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var bidragsevne = justertBidragsevnePeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(bidragsevnePeriode -> new Bidragsevne(bidragsevnePeriode.getBidragsevneBelop(),
              bidragsevnePeriode.getTjuefemProsentInntekt())).findFirst().orElse(null);

      var beregnetBidragSakListe = justertBeregnedeBidragSakPeriodeListe.stream()
            .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
            .map(beregnetBidragSakPeriode -> new BeregnetBidragSak(
                beregnetBidragSakPeriode.getSaksnr(),
                beregnetBidragSakPeriode.getGrunnlagPerBarnListe())).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode

      var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
          bidragsevne, beregnetBidragSakListe);

        resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
            forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisert), grunnlagBeregningPeriodisert));
    }

    return new BeregnForholdsmessigFordelingResultat(resultatPeriodeListe);
  }


  // Validerer at input-verdier til beregn-barnebidrag er gyldige
  public List<Avvik> validerInput(BeregnForholdsmessigFordelingGrunnlag grunnlag) {

    // Sjekk perioder for bidragsevne
    var bidragsevnePeriodeListe = new ArrayList<Periode>();
    for (BidragsevnePeriode bidragsevnePeriode : grunnlag.getBidragsevnePeriodeListe()) {
      bidragsevnePeriodeListe.add(bidragsevnePeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),"bidragsevnePeriodeListe",
        bidragsevnePeriodeListe, false, false, true, true));

    // Sjekk perioder for beregnede bidrag som skal forholdsmessig fordeles
    var beregnetBidragSakPeriodeListe = new ArrayList<Periode>();
    for (BeregnetBidragSakPeriode forholdsmessigFordelingPeriode : grunnlag.getBeregnetBidragPeriodeListe()) {
      beregnetBidragSakPeriodeListe.add(forholdsmessigFordelingPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),"beregnetBidragSakPeriodeListe",
        beregnetBidragSakPeriodeListe, false, false, true, true));


    return avvikListe;
  }
}
