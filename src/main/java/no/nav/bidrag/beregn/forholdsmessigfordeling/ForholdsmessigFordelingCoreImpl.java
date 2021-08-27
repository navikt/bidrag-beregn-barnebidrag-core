package no.nav.bidrag.beregn.forholdsmessigfordeling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingGrunnlag;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingResultat;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSak;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnetBidragSakPeriode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.GrunnlagPerBarn;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatBeregning;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPerBarn;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnForholdsmessigFordelingGrunnlagCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnForholdsmessigFordelingResultatCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnetBidragSakCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BeregnetBidragSakPeriodeCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BidragsevneCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.BidragsevnePeriodeCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.GrunnlagBeregningPeriodisertCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.GrunnlagPerBarnCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.ResultatPerBarnCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.forholdsmessigfordeling.periode.ForholdsmessigFordelingPeriode;


public class ForholdsmessigFordelingCoreImpl implements ForholdsmessigFordelingCore {

  public ForholdsmessigFordelingCoreImpl(
      ForholdsmessigFordelingPeriode forholdsmessigFordelingPeriode) {
    this.forholdsmessigFordelingPeriode = forholdsmessigFordelingPeriode;
  }

  private final ForholdsmessigFordelingPeriode forholdsmessigFordelingPeriode;

  public BeregnForholdsmessigFordelingResultatCore beregnForholdsmessigFordeling(
      BeregnForholdsmessigFordelingGrunnlagCore beregnForholdsmessigFordelingGrunnlagCore) {
    var beregnForholdsmessigFordelingGrunnlag = mapTilBusinessObject(beregnForholdsmessigFordelingGrunnlagCore);
    var beregnForholdsmessigFordelingResultat = new BeregnForholdsmessigFordelingResultat(Collections.emptyList());
    var avvikListe = forholdsmessigFordelingPeriode.validerInput(beregnForholdsmessigFordelingGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnForholdsmessigFordelingResultat = forholdsmessigFordelingPeriode.beregnPerioder(beregnForholdsmessigFordelingGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnForholdsmessigFordelingResultat);
  }

  private BeregnForholdsmessigFordelingGrunnlag mapTilBusinessObject(BeregnForholdsmessigFordelingGrunnlagCore beregnForholdsmessigFordelingGrunnlagCore) {
    var beregnDatoFra = beregnForholdsmessigFordelingGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnForholdsmessigFordelingGrunnlagCore.getBeregnDatoTil();

    var bidragsevnePeriodeListe                =
        mapBidragsevnePeriodeListe(beregnForholdsmessigFordelingGrunnlagCore.getBidragsevnePeriodeListe());

    var beregnetBidragSakPeriodeListe =
        mapBeregnetBidragSakPeriodeListe(beregnForholdsmessigFordelingGrunnlagCore.getBeregnetBidragPeriodeListe());

    return new BeregnForholdsmessigFordelingGrunnlag(beregnDatoFra, beregnDatoTil,
        bidragsevnePeriodeListe, beregnetBidragSakPeriodeListe);
  }

  private List<BidragsevnePeriode> mapBidragsevnePeriodeListe(
      List<BidragsevnePeriodeCore> bidragsevnePeriodeListeCore) {
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    for (BidragsevnePeriodeCore bidragsevnePeriodeCore : bidragsevnePeriodeListeCore) {
      bidragsevnePeriodeListe.add(new BidragsevnePeriode(
          new Periode(bidragsevnePeriodeCore.getPeriode().getDatoFom(),
              bidragsevnePeriodeCore.getPeriode().getDatoTil()),
          bidragsevnePeriodeCore.getBelop(),
          bidragsevnePeriodeCore.getTjuefemProsentInntekt()));
    }
    return bidragsevnePeriodeListe.stream()
        .sorted(Comparator.comparing(bidragsevnePeriode -> bidragsevnePeriode
            .getBidragsevneDatoFraTil().getDatoFom())).collect(Collectors.toList());
  }

  private List<BeregnetBidragSakPeriode> mapBeregnetBidragSakPeriodeListe(
      List<BeregnetBidragSakPeriodeCore> beregnetBidragSakPeriodeListeCore) {
    var beregnetBidragSakPeriodeListe = new ArrayList<BeregnetBidragSakPeriode>();
    for (BeregnetBidragSakPeriodeCore beregnetBidragSakPeriodeCore : beregnetBidragSakPeriodeListeCore) {
      beregnetBidragSakPeriodeListe.add(new BeregnetBidragSakPeriode(
          beregnetBidragSakPeriodeCore.getSaksnr(),
          new Periode(beregnetBidragSakPeriodeCore.getPeriode().getDatoFom(),
              beregnetBidragSakPeriodeCore.getPeriode().getDatoTil()),
          mapGrunnlagPerBarnListe(beregnetBidragSakPeriodeCore.getGrunnlagPerBarnListe())));
    }
    return beregnetBidragSakPeriodeListe.stream()
        .sorted(Comparator.comparing(bidragsevnePeriode -> bidragsevnePeriode
            .getDatoFraTil().getDatoFom())).collect(Collectors.toList());
  }

  private List<GrunnlagPerBarn> mapGrunnlagPerBarnListe(List<GrunnlagPerBarnCore> grunnlagPerBarnListeCore) {
    var grunnlagPerBarnListe = new ArrayList<GrunnlagPerBarn>();
    for (GrunnlagPerBarnCore grunnlagPerBarnCore : grunnlagPerBarnListeCore) {
      grunnlagPerBarnListe.add(new GrunnlagPerBarn(
          grunnlagPerBarnCore.getBarnPersonId(),
          grunnlagPerBarnCore.getBidragBelop()));
    }
    return grunnlagPerBarnListe;
  }


  private BeregnForholdsmessigFordelingResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnForholdsmessigFordelingResultat resultat) {
    return new BeregnForholdsmessigFordelingResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<AvvikCore> mapAvvik(List<Avvik> avvikListe) {
    var avvikCoreListe = new ArrayList<AvvikCore>();
    for (Avvik avvik : avvikListe) {
      avvikCoreListe.add(new AvvikCore(avvik.getAvvikTekst(), avvik.getAvvikType().toString()));
    }
    return avvikCoreListe;
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> resultatPeriodeListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode resultatPeriode : resultatPeriodeListe) {
//      var forholdsmessigFordelingResultatGrunnlagListe = resultatPeriode.getResultatGrunnlagListe();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(resultatPeriode.getPeriode().getDatoFom(), resultatPeriode.getPeriode().getDatoTil()),
          mapResultatBeregning(resultatPeriode.getResultatBeregningListe()),
          new GrunnlagBeregningPeriodisertCore(
              new BidragsevneCore(resultatPeriode.getResultatGrunnlag().getBidragsevne().getBelop(),
              resultatPeriode.getResultatGrunnlag().getBidragsevne().getTjuefemProsentInntekt()),
          mapBeregnetBidragSak(resultatPeriode.getResultatGrunnlag().getBeregnetBidragSakListe()))));
    }
    return resultatPeriodeCoreListe;
  }

  private List<ResultatBeregningCore>mapResultatBeregning(List<ResultatBeregning> resultatBeregningListe) {
    var resultatBeregningListeCore = new ArrayList<ResultatBeregningCore>();
    for (ResultatBeregning resultatBeregning : resultatBeregningListe) {
      resultatBeregningListeCore
          .add(new ResultatBeregningCore(resultatBeregning.getSaksnr(),
              mapResultatPerBarn(resultatBeregning.getResultatPerBarnListe())));
    }
    return resultatBeregningListeCore;
  }

  private List<ResultatPerBarnCore> mapResultatPerBarn(List<ResultatPerBarn> resultatPerBarnListe) {
    var resultatPerBarnListeCore = new ArrayList<ResultatPerBarnCore>();
    for (ResultatPerBarn resultatPerBarn : resultatPerBarnListe) {
      resultatPerBarnListeCore.add(new ResultatPerBarnCore(
          resultatPerBarn.getBarnPersonId(),
          resultatPerBarn.getBelop(),
          resultatPerBarn.getKode().toString()));
    }
    return resultatPerBarnListeCore;
  }

  private List<BeregnetBidragSakCore> mapBeregnetBidragSak(List<BeregnetBidragSak> beregnetBidragSakListe) {
    var beregnetBidragSakListeCore = new ArrayList<BeregnetBidragSakCore>();
    for (BeregnetBidragSak beregnetBidragSak : beregnetBidragSakListe) {
      beregnetBidragSakListeCore.add(new BeregnetBidragSakCore(
          beregnetBidragSak.getSaksnr(),
          mapGrunnlagPerBarn(beregnetBidragSak.getGrunnlagPerBarnListe())));
    }
    return beregnetBidragSakListeCore;
  }

  private List<GrunnlagPerBarnCore> mapGrunnlagPerBarn(List<GrunnlagPerBarn> grunnlagPerBarnListe) {
    var grunnlagPerBarnListeCore = new ArrayList<GrunnlagPerBarnCore>();
    for (GrunnlagPerBarn grunnlagPerBarn : grunnlagPerBarnListe) {
      grunnlagPerBarnListeCore.add(new GrunnlagPerBarnCore(
          grunnlagPerBarn.getBarnPersonId(), grunnlagPerBarn.getBidragBelop()));
    }
    return grunnlagPerBarnListeCore;
  }

}