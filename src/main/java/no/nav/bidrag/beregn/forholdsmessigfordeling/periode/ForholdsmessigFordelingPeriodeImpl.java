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
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSakPeriode;
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

    var justertBeregnedeBidragSakPeriodeListe =
        beregnForholdsmessigFordelingGrunnlag.getBeregnetBidragPeriodeListe()
        .stream()
        .map(BeregnetBidragSakPeriode::new)
        .collect(toCollection(ArrayList::new));


    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnForholdsmessigFordelingGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertBeregnedeBidragSakPeriodeListe)
        .addBruddpunkt(beregnForholdsmessigFordelingGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnForholdsmessigFordelingGrunnlag.getBeregnDatoFra(),
            beregnForholdsmessigFordelingGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnForholdsmessigFordelingGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

//      var beregnetBidragSakListe = new ArrayList<GrunnlagBeregningPeriodisert>();

        var grunnlagBeregningPeriodisertListe = justertBeregnedeBidragSakPeriodeListe.stream()
            .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
            .map(beregnetBidragSakPeriode -> new GrunnlagBeregningPeriodisert(
                beregnetBidragSakPeriode.getSaksnr(),
                beregnetBidragSakPeriode.getBarnPersonId(),
                beregnetBidragSakPeriode.getBidragBelop())).collect(toList());

/*        var samvaersfradrag = justertSamvaersfradragPeriodeListe.stream().filter(i ->
            i.getDatoFraTil().overlapperMed(beregningsperiode))
            .filter(i -> i.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(SamvaersfradragPeriode::getSamvaersfradragBelop).findFirst().orElse(null);

        var deltBosted = justertDeltBostedPeriodeListe.stream().filter(i ->
            i.getDatoFraTil().overlapperMed(beregningsperiode))
            .filter(i -> i.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(DeltBostedPeriode::getDeltBostedIPeriode).findFirst().orElse(null);

        var barnetilleggBP = justertBarnetilleggBPPeriodeListe.stream()
            .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
            .filter(i -> i.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(barnetilleggBPPeriode -> new Barnetillegg(barnetilleggBPPeriode.getBarnetilleggBelop(),
                barnetilleggBPPeriode.getBarnetilleggSkattProsent())).findFirst().orElse(null);

        var barnetilleggBM = justertBarnetilleggBMPeriodeListe.stream()
            .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
            .filter(i -> i.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(barnetilleggBMPeriode -> new Barnetillegg(barnetilleggBMPeriode.getBarnetilleggBelop(),
                barnetilleggBMPeriode.getBarnetilleggSkattProsent())).findFirst().orElse(null);*/
/*
        // Ved delt bosted skal andel av underholdskostnad reduseres med 50 prosentpoeng. Blir andelen under 50%
        // så skal ikke bidrag beregnes
        var andelProsent = bPsAndelUnderholdskostnad.getBPsAndelUnderholdskostnadProsent();
        var andelBelop = bPsAndelUnderholdskostnad.getBPsAndelUnderholdskostnadBelop() ;
        var barnetErSelvforsorget = bPsAndelUnderholdskostnad.getBarnetErSelvforsorget() ;


        beregnetBidragSakListe.add(new GrunnlagBeregningPerBarn(soknadsbarnPersonId,
            new BPsAndelUnderholdskostnad(andelProsent, andelBelop, barnetErSelvforsorget),
            samvaersfradrag, deltBosted, barnetilleggBP, barnetilleggBM));
      }

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());*/

      // Kaller beregningsmodulen for hver beregningsperiode
/*
      var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
          bidragsevne, grunnlagBeregningPerBarnListe, barnetilleggForsvaret, andreLopendeBidragListe, sjablonliste);

*/

        resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
            forholdsmessigFordelingBeregning.beregn(grunnlagBeregningPeriodisertListe), grunnlagBeregningPeriodisertListe));



    }

    return new BeregnForholdsmessigFordelingResultat(resultatPeriodeListe);
  }


  // Validerer at input-verdier til beregn-barnebidrag er gyldige
  public List<Avvik> validerInput(BeregnForholdsmessigFordelingGrunnlag grunnlag) {


    // Sjekk perioder for bidragsevne
    var beregnetBidragSakPeriodeListe = new ArrayList<Periode>();
    for (BeregnetBidragSakPeriode forholdsmessigFordelingPeriode : grunnlag.getBeregnetBidragPeriodeListe()) {
      beregnetBidragSakPeriodeListe.add(forholdsmessigFordelingPeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),"beregnetBidragSakPeriodeListe",
        beregnetBidragSakPeriodeListe, false, false, true, true));


    return avvikListe;
  }
}
