package no.nav.bidrag.beregn.barnebidrag;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidragPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaretPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBostedPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.barnebidrag.dto.AndreLopendeBidragPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BPsAndelUnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggForsvaretPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnBarnebidragGrunnlagCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnetBarnebidragResultatCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BidragsevnePeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.DeltBostedPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.barnebidrag.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.SamvaersfradragPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode;
import no.nav.bidrag.beregn.felles.FellesCore;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore;


public class BarnebidragCoreImpl extends FellesCore implements BarnebidragCore {

  private final BarnebidragPeriode barnebidragPeriode;

  public BarnebidragCoreImpl(BarnebidragPeriode barnebidragPeriode) {
    this.barnebidragPeriode = barnebidragPeriode;
  }

  public BeregnetBarnebidragResultatCore beregnBarnebidrag(BeregnBarnebidragGrunnlagCore beregnBarnebidragGrunnlagCore) {
    var beregnBarnebidragGrunnlag = mapTilBusinessObject(beregnBarnebidragGrunnlagCore);
    var beregnBarnebidragResultat = new BeregnBarnebidragResultat(Collections.emptyList());
    var avvikListe = barnebidragPeriode.validerInput(beregnBarnebidragGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnBarnebidragResultat = barnebidragPeriode.beregnPerioder(beregnBarnebidragGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnBarnebidragResultat);
  }

  private BeregnBarnebidragGrunnlag mapTilBusinessObject(BeregnBarnebidragGrunnlagCore beregnBarnebidragGrunnlagCore) {
    var beregnDatoFra = beregnBarnebidragGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnBarnebidragGrunnlagCore.getBeregnDatoTil();

    var bidragsevnePeriodeListe = mapBidragsevnePeriodeListe(beregnBarnebidragGrunnlagCore.getBidragsevnePeriodeListe());
    var bPsAndelUnderholdskostnadPeriodeListe = mapBPsAndelUnderholdskostnadPeriodeListe(
        beregnBarnebidragGrunnlagCore.getBPsAndelUnderholdskostnadPeriodeListe());
    var samvaersfradragPeriodeListe = mapSamvaersfradragPeriodeListe(beregnBarnebidragGrunnlagCore.getSamvaersfradragPeriodeListe());
    var deltBostedPeriodeListe = mapDeltBostedPeriodeListe(beregnBarnebidragGrunnlagCore.getDeltBostedPeriodeListe());
    var barnetilleggBPPeriodeListe = mapBarnetilleggBPPeriodeListe(beregnBarnebidragGrunnlagCore.getBarnetilleggBPPeriodeListe());
    var barnetilleggBMPeriodeListe = mapBarnetilleggBMPeriodeListe(beregnBarnebidragGrunnlagCore.getBarnetilleggBMPeriodeListe());
    var barnetilleggForsvaretPeriodeListe = mapBarnetilleggForsvaretPeriodeListe(
        beregnBarnebidragGrunnlagCore.getBarnetilleggForsvaretPeriodeListe());
    var andreLopendeBidragPeriodeListe = mapAndreLopendeBidragPeriodeListe(beregnBarnebidragGrunnlagCore.getAndreLopendeBidragPeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBarnebidragGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe, bPsAndelUnderholdskostnadPeriodeListe,
        samvaersfradragPeriodeListe, deltBostedPeriodeListe, barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe,
        barnetilleggForsvaretPeriodeListe, andreLopendeBidragPeriodeListe, sjablonPeriodeListe);
  }

  private List<BidragsevnePeriode> mapBidragsevnePeriodeListe(List<BidragsevnePeriodeCore> bidragsevnePeriodeListeCore) {
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    for (BidragsevnePeriodeCore bidragsevnePeriodeCore : bidragsevnePeriodeListeCore) {
      bidragsevnePeriodeListe.add(new BidragsevnePeriode(
          bidragsevnePeriodeCore.getReferanse(),
          new Periode(bidragsevnePeriodeCore.getPeriode().getDatoFom(), bidragsevnePeriodeCore.getPeriode().getDatoTil()),
          bidragsevnePeriodeCore.getBelop(),
          bidragsevnePeriodeCore.getTjuefemProsentInntekt()));
    }
    return bidragsevnePeriodeListe.stream()
        .sorted(comparing(bidragsevnePeriode -> bidragsevnePeriode.getBidragsevnePeriode().getDatoFom()))
        .collect(Collectors.toList());
  }

  private List<BPsAndelUnderholdskostnadPeriode> mapBPsAndelUnderholdskostnadPeriodeListe(
      List<BPsAndelUnderholdskostnadPeriodeCore> bPsAndelUnderholdskostnadPeriodeListeCore) {
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    for (BPsAndelUnderholdskostnadPeriodeCore bPsAndelUnderholdskostnadPeriodeCore : bPsAndelUnderholdskostnadPeriodeListeCore) {
      bPsAndelUnderholdskostnadPeriodeListe.add(new BPsAndelUnderholdskostnadPeriode(
          bPsAndelUnderholdskostnadPeriodeCore.getSoknadsbarnPersonId(),
          bPsAndelUnderholdskostnadPeriodeCore.getReferanse(),
          new Periode(bPsAndelUnderholdskostnadPeriodeCore.getPeriode().getDatoFom(), bPsAndelUnderholdskostnadPeriodeCore.getPeriode().getDatoTil()),
          bPsAndelUnderholdskostnadPeriodeCore.getAndelProsent(),
          bPsAndelUnderholdskostnadPeriodeCore.getAndelBelop(),
          bPsAndelUnderholdskostnadPeriodeCore.getBarnetErSelvforsorget()));
    }
    return bPsAndelUnderholdskostnadPeriodeListe.stream()
        .sorted(comparing(bPsAndelUnderholdskostnadPeriode -> bPsAndelUnderholdskostnadPeriode.getPeriode().getDatoFom()))
        .collect(Collectors.toList());
  }

  private List<SamvaersfradragPeriode> mapSamvaersfradragPeriodeListe(List<SamvaersfradragPeriodeCore> samvaersfradragPeriodeListeCore) {
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriode>();
    for (SamvaersfradragPeriodeCore samvaersfradragPeriodeCore : samvaersfradragPeriodeListeCore) {
      samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(
          samvaersfradragPeriodeCore.getSoknadsbarnPersonId(),
          samvaersfradragPeriodeCore.getReferanse(),
          new Periode(samvaersfradragPeriodeCore.getPeriode().getDatoFom(), samvaersfradragPeriodeCore.getPeriode().getDatoTil()),
          samvaersfradragPeriodeCore.getBelop()));
    }
    return samvaersfradragPeriodeListe.stream()
        .sorted(comparing(samvaersfradragPeriode -> samvaersfradragPeriode.getPeriode().getDatoFom()))
        .collect(Collectors.toList());
  }

  private List<DeltBostedPeriode> mapDeltBostedPeriodeListe(List<DeltBostedPeriodeCore> deltBostedPeriodeListeCore) {
    var deltBostedPeriodeListe = new ArrayList<DeltBostedPeriode>();
    for (DeltBostedPeriodeCore deltBostedPeriodeCore : deltBostedPeriodeListeCore) {
      deltBostedPeriodeListe.add(new DeltBostedPeriode(
          deltBostedPeriodeCore.getSoknadsbarnPersonId(),
          deltBostedPeriodeCore.getReferanse(),
          new Periode(deltBostedPeriodeCore.getPeriode().getDatoFom(), deltBostedPeriodeCore.getPeriode().getDatoTil()),
          deltBostedPeriodeCore.getDeltBostedIPeriode()));
    }
    return deltBostedPeriodeListe.stream()
        .sorted(comparing(deltBostedPeriode -> deltBostedPeriode.getPeriode().getDatoFom()))
        .collect(Collectors.toList());
  }

  private List<BarnetilleggPeriode> mapBarnetilleggBPPeriodeListe(List<BarnetilleggPeriodeCore> barnetilleggBPPeriodeListeCore) {
    var barnetilleggBPPeriodeListe = new ArrayList<BarnetilleggPeriode>();
    for (BarnetilleggPeriodeCore barnetilleggBPPeriodeCore : barnetilleggBPPeriodeListeCore) {
      barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(
          barnetilleggBPPeriodeCore.getSoknadsbarnPersonId(),
          barnetilleggBPPeriodeCore.getReferanse(),
          new Periode(barnetilleggBPPeriodeCore.getPeriode().getDatoFom(), barnetilleggBPPeriodeCore.getPeriode().getDatoTil()),
          barnetilleggBPPeriodeCore.getBelop(),
          barnetilleggBPPeriodeCore.getSkattProsent()));
    }
    return barnetilleggBPPeriodeListe.stream()
        .sorted(comparing(barnetilleggBPPeriode -> barnetilleggBPPeriode.getPeriode().getDatoFom()))
        .collect(Collectors.toList());
  }

  private List<BarnetilleggPeriode> mapBarnetilleggBMPeriodeListe(List<BarnetilleggPeriodeCore> barnetilleggBMPeriodeListeCore) {
    var barnetilleggBMPeriodeListe = new ArrayList<BarnetilleggPeriode>();
    for (BarnetilleggPeriodeCore barnetilleggBMPeriodeCore : barnetilleggBMPeriodeListeCore) {
      barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(
          barnetilleggBMPeriodeCore.getSoknadsbarnPersonId(),
          barnetilleggBMPeriodeCore.getReferanse(),
          new Periode(barnetilleggBMPeriodeCore.getPeriode().getDatoFom(), barnetilleggBMPeriodeCore.getPeriode().getDatoTil()),
          barnetilleggBMPeriodeCore.getBelop(),
          barnetilleggBMPeriodeCore.getSkattProsent()));
    }
    return barnetilleggBMPeriodeListe.stream()
        .sorted(comparing(barnetilleggBMPeriode -> barnetilleggBMPeriode.getPeriode().getDatoFom()))
        .collect(Collectors.toList());
  }

  private List<BarnetilleggForsvaretPeriode> mapBarnetilleggForsvaretPeriodeListe(
      List<BarnetilleggForsvaretPeriodeCore> barnetilleggForsvaretPeriodeListeCore) {
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriode>();
    for (BarnetilleggForsvaretPeriodeCore barnetilleggForsvaretPeriodeCore : barnetilleggForsvaretPeriodeListeCore) {
      barnetilleggForsvaretPeriodeListe.add(new BarnetilleggForsvaretPeriode(
          barnetilleggForsvaretPeriodeCore.getReferanse(),
          new Periode(barnetilleggForsvaretPeriodeCore.getPeriode().getDatoFom(), barnetilleggForsvaretPeriodeCore.getPeriode().getDatoTil()),
          barnetilleggForsvaretPeriodeCore.getBarnetilleggForsvaretIPeriode()));
    }
    return barnetilleggForsvaretPeriodeListe.stream()
        .sorted(comparing(barnetilleggForsvaretPeriode -> barnetilleggForsvaretPeriode.getPeriode().getDatoFom()))
        .collect(Collectors.toList());
  }

  private List<AndreLopendeBidragPeriode> mapAndreLopendeBidragPeriodeListe(List<AndreLopendeBidragPeriodeCore> andreLopendeBidragPeriodeListeCore) {
    var andreLopendeBidragPeriodeListe = new ArrayList<AndreLopendeBidragPeriode>();
    for (AndreLopendeBidragPeriodeCore andreLopendeBidragPeriodeCore : andreLopendeBidragPeriodeListeCore) {
      andreLopendeBidragPeriodeListe.add(new AndreLopendeBidragPeriode(
          andreLopendeBidragPeriodeCore.getReferanse(),
          new Periode(andreLopendeBidragPeriodeCore.getPeriode().getDatoFom(), andreLopendeBidragPeriodeCore.getPeriode().getDatoTil()),
          andreLopendeBidragPeriodeCore.getBarnPersonId(),
          andreLopendeBidragPeriodeCore.getBidragBelop(),
          andreLopendeBidragPeriodeCore.getSamvaersfradragBelop()
      ));
    }
    return andreLopendeBidragPeriodeListe.stream()
        .sorted(comparing(andreLopendeBidragPeriode -> andreLopendeBidragPeriode.getPeriode().getDatoFom()))
        .collect(Collectors.toList());
  }

  private BeregnetBarnebidragResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnBarnebidragResultat resultat) {
    return new BeregnetBarnebidragResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()),
        mapSjablonGrunnlagListe(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> resultatPeriodeListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode resultatPeriode : resultatPeriodeListe) {
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(resultatPeriode.getPeriode().getDatoFom(), resultatPeriode.getPeriode().getDatoTil()),
          mapResultatBeregning(resultatPeriode.getResultatListe()),
          mapReferanseListe(resultatPeriode)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<ResultatBeregningCore> mapResultatBeregning(List<ResultatBeregning> resultatBeregningListe) {
    var resultatBeregningListeCore = new ArrayList<ResultatBeregningCore>();
    for (ResultatBeregning resultatBeregning : resultatBeregningListe) {
      resultatBeregningListeCore.add(new ResultatBeregningCore(resultatBeregning.getSoknadsbarnPersonId(), resultatBeregning.getBelop(),
          resultatBeregning.getKode().toString()));
    }
    return resultatBeregningListeCore;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getGrunnlag();
    var sjablonListe = resultatPeriode.getResultatListe().stream()
        .map(ResultatBeregning::getSjablonListe)
        .flatMap(Collection::stream)
        .collect(toList());

    var referanseListe = new ArrayList<String>();
    referanseListe.add(resultatGrunnlag.getBidragsevne().getReferanse());
    referanseListe.add(resultatGrunnlag.getBarnetilleggForsvaret().getReferanse());
    resultatGrunnlag.getAndreLopendeBidragListe().forEach(andreLopendeBidrag -> referanseListe.add(andreLopendeBidrag.getReferanse()));
    referanseListe.addAll(mapGrunnlagPerBarn(resultatGrunnlag.getGrunnlagPerBarnListe()));
    referanseListe.addAll(sjablonListe.stream().map(this::lagSjablonReferanse).distinct().collect(toList()));
    return referanseListe;
  }

  private List<String> mapGrunnlagPerBarn(List<GrunnlagBeregningPerBarn> grunnlagBeregningPerBarnListe) {
    var referanseListe = new ArrayList<String>();
    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn : grunnlagBeregningPerBarnListe) {
      referanseListe.add(grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getReferanse());
      referanseListe.add(grunnlagBeregningPerBarn.getSamvaersfradrag().getReferanse());
      referanseListe.add(grunnlagBeregningPerBarn.getDeltBosted().getReferanse());
      referanseListe.add(grunnlagBeregningPerBarn.getBarnetilleggBP().getReferanse());
      referanseListe.add(grunnlagBeregningPerBarn.getBarnetilleggBM().getReferanse());
    }
    return referanseListe;
  }

  private List<SjablonResultatGrunnlagCore> mapSjablonGrunnlagListe(List<ResultatPeriode> resultatPeriodeListe) {
    return resultatPeriodeListe.stream()
        .map(ResultatPeriode::getResultatListe)
        .flatMap(Collection::stream)
        .map(resultat -> mapSjablonListe(resultat.getSjablonListe()))
        .flatMap(Collection::stream)
        .distinct()
        .collect(toList());
  }
}
