package no.nav.bidrag.beregn.barnebidrag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
import no.nav.bidrag.beregn.barnebidrag.dto.BPsAndelUnderholdskostnadCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BPsAndelUnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggForsvaretPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BarnetilleggPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnBarnebidragGrunnlagCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnBarnebidragResultatCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BidragsevneCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BidragsevnePeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.DeltBostedPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.GrunnlagBeregningPerBarnCore;
import no.nav.bidrag.beregn.barnebidrag.dto.GrunnlagBeregningPeriodisertCore;
import no.nav.bidrag.beregn.barnebidrag.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.barnebidrag.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.dto.SamvaersfradragPeriodeCore;
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;


public class BarnebidragCoreImpl implements BarnebidragCore {

  public BarnebidragCoreImpl(BarnebidragPeriode barnebidragPeriode) {
    this.barnebidragPeriode = barnebidragPeriode;
  }

  private final BarnebidragPeriode barnebidragPeriode;

  public BeregnBarnebidragResultatCore beregnBarnebidrag(
      BeregnBarnebidragGrunnlagCore beregnBarnebidragGrunnlagCore) {
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

    var bidragsevnePeriodeListe                =
        mapBidragsevnePeriodeListe(beregnBarnebidragGrunnlagCore.getBidragsevnePeriodeListe());
    var bPsAndelUnderholdskostnadPeriodeListe  =
        mapBPsAndelUnderholdskostnadPeriodeListe(beregnBarnebidragGrunnlagCore.getBPsAndelUnderholdskostnadPeriodeListe());
    var samvaersfradragPeriodeListe            =
        mapSamvaersfradragPeriodeListe(beregnBarnebidragGrunnlagCore.getSamvaersfradragPeriodeListe());
    var deltBostedPeriodeListe                 =
        mapDeltBostedPeriodeListe(beregnBarnebidragGrunnlagCore.getDeltBostedPeriodeListe());
    var barnetilleggBPPeriodeListe             =
        mapBarnetilleggBPPeriodeListe(beregnBarnebidragGrunnlagCore.getBarnetilleggBPPeriodeListe());
    var barnetilleggBMPeriodeListe             =
        mapBarnetilleggBMPeriodeListe(beregnBarnebidragGrunnlagCore.getBarnetilleggBMPeriodeListe());
    var barnetilleggForsvaretPeriodeListe      =
        mapBarnetilleggForsvaretPeriodeListe(beregnBarnebidragGrunnlagCore.getBarnetilleggForsvaretPeriodeListe());

    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBarnebidragGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
        bPsAndelUnderholdskostnadPeriodeListe, samvaersfradragPeriodeListe,
        deltBostedPeriodeListe, barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe,
        barnetilleggForsvaretPeriodeListe, sjablonPeriodeListe);
  }

  private List<BidragsevnePeriode> mapBidragsevnePeriodeListe(
      List<BidragsevnePeriodeCore> bidragsevnePeriodeListeCore) {
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    for (BidragsevnePeriodeCore bidragsevnePeriodeCore : bidragsevnePeriodeListeCore) {
      bidragsevnePeriodeListe.add(new BidragsevnePeriode(
          new Periode(bidragsevnePeriodeCore.getBidragsevneDatoFraTil().getPeriodeDatoFra(),
              bidragsevnePeriodeCore.getBidragsevneDatoFraTil().getPeriodeDatoTil()),
          bidragsevnePeriodeCore.getBidragsevneBelop(),
          bidragsevnePeriodeCore.getTjuefemProsentInntekt()));
    }
    return bidragsevnePeriodeListe.stream()
        .sorted(Comparator.comparing(bidragsevnePeriode -> bidragsevnePeriode
            .getBidragsevneDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }

  private List<BPsAndelUnderholdskostnadPeriode> mapBPsAndelUnderholdskostnadPeriodeListe(
      List<BPsAndelUnderholdskostnadPeriodeCore> bPsAndelUnderholdskostnadPeriodeListeCore) {
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    for (BPsAndelUnderholdskostnadPeriodeCore bPsAndelUnderholdskostnadPeriodeCore : bPsAndelUnderholdskostnadPeriodeListeCore) {
      bPsAndelUnderholdskostnadPeriodeListe.add(new BPsAndelUnderholdskostnadPeriode(
          bPsAndelUnderholdskostnadPeriodeCore.getSoknadsbarnPersonId(),
          new Periode(bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadDatoFraTil().getPeriodeDatoFra(),
              bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadDatoFraTil().getPeriodeDatoTil()),
          bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadProsent(),
          bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadBelop(),
          bPsAndelUnderholdskostnadPeriodeCore.getBarnetErSelvforsorget()));
    }
    return bPsAndelUnderholdskostnadPeriodeListe.stream()
        .sorted(Comparator.comparing(bPsAndelUnderholdskostnadPeriode -> bPsAndelUnderholdskostnadPeriode
            .getDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }

  private List<SamvaersfradragPeriode> mapSamvaersfradragPeriodeListe(
      List<SamvaersfradragPeriodeCore> samvaersfradragPeriodeListeCore) {
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriode>();
    for (SamvaersfradragPeriodeCore samvaersfradragPeriodeCore : samvaersfradragPeriodeListeCore) {
      samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(
          samvaersfradragPeriodeCore.getSoknadsbarnPersonId(),
          new Periode(samvaersfradragPeriodeCore.getSamvaersfradragDatoFraTil().getPeriodeDatoFra(),
              samvaersfradragPeriodeCore.getSamvaersfradragDatoFraTil().getPeriodeDatoTil()),
          samvaersfradragPeriodeCore.getSamvaersfradragBelop()));
    }
    return samvaersfradragPeriodeListe.stream()
        .sorted(Comparator.comparing(samvaersfradragPeriode -> samvaersfradragPeriode
            .getDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }

  private List<DeltBostedPeriode> mapDeltBostedPeriodeListe(
      List<DeltBostedPeriodeCore> deltBostedPeriodeListeCore) {
    var deltBostedPeriodeListe = new ArrayList<DeltBostedPeriode>();
    for (DeltBostedPeriodeCore deltBostedPeriodeCore : deltBostedPeriodeListeCore) {
      deltBostedPeriodeListe.add(new DeltBostedPeriode(
          deltBostedPeriodeCore.getSoknadsbarnPersonId(),
          new Periode(deltBostedPeriodeCore.getDeltBostedDatoFraTil().getPeriodeDatoFra(),
              deltBostedPeriodeCore.getDeltBostedDatoFraTil().getPeriodeDatoTil()),
          deltBostedPeriodeCore.getDeltBostedIPeriode()));
    }
    return deltBostedPeriodeListe.stream()
        .sorted(Comparator.comparing(deltBostedPeriode -> deltBostedPeriode
            .getDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }

  private List<BarnetilleggPeriode> mapBarnetilleggBPPeriodeListe(
      List<BarnetilleggPeriodeCore> barnetilleggBPPeriodeListeCore) {
    var barnetilleggBPPeriodeListe = new ArrayList<BarnetilleggPeriode>();
    for (BarnetilleggPeriodeCore barnetilleggBPPeriodeCore : barnetilleggBPPeriodeListeCore) {
      barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(
          barnetilleggBPPeriodeCore.getSoknadsbarnPersonId(),
          new Periode(barnetilleggBPPeriodeCore.getBarnetilleggDatoFraTil().getPeriodeDatoFra(),
              barnetilleggBPPeriodeCore.getBarnetilleggDatoFraTil().getPeriodeDatoTil()),
          barnetilleggBPPeriodeCore.getBarnetilleggBelop(),
          barnetilleggBPPeriodeCore.getBarnetilleggSkattProsent()));
    }
    return barnetilleggBPPeriodeListe.stream()
        .sorted(Comparator.comparing(barnetilleggBPPeriode -> barnetilleggBPPeriode
            .getDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }

  private List<BarnetilleggPeriode> mapBarnetilleggBMPeriodeListe(
      List<BarnetilleggPeriodeCore> barnetilleggBMPeriodeListeCore) {
    var barnetilleggBMPeriodeListe = new ArrayList<BarnetilleggPeriode>();
    for (BarnetilleggPeriodeCore barnetilleggBMPeriodeCore : barnetilleggBMPeriodeListeCore) {
      barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(
          barnetilleggBMPeriodeCore.getSoknadsbarnPersonId(),
          new Periode(barnetilleggBMPeriodeCore.getBarnetilleggDatoFraTil().getPeriodeDatoFra(),
              barnetilleggBMPeriodeCore.getBarnetilleggDatoFraTil().getPeriodeDatoTil()),
          barnetilleggBMPeriodeCore.getBarnetilleggBelop(),
          barnetilleggBMPeriodeCore.getBarnetilleggSkattProsent()));
    }
    return barnetilleggBMPeriodeListe.stream()
        .sorted(Comparator.comparing(barnetilleggBMPeriode -> barnetilleggBMPeriode
            .getDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }

  private List<BarnetilleggForsvaretPeriode> mapBarnetilleggForsvaretPeriodeListe(
      List<BarnetilleggForsvaretPeriodeCore> barnetilleggForsvaretPeriodeListeCore) {
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriode>();
    for (BarnetilleggForsvaretPeriodeCore barnetilleggForsvaretPeriodeCore : barnetilleggForsvaretPeriodeListeCore) {
      barnetilleggForsvaretPeriodeListe.add(new BarnetilleggForsvaretPeriode(
          new Periode(barnetilleggForsvaretPeriodeCore.getBarnetilleggForsvaretDatoFraTil().getPeriodeDatoFra(),
              barnetilleggForsvaretPeriodeCore.getBarnetilleggForsvaretDatoFraTil().getPeriodeDatoTil()),
          barnetilleggForsvaretPeriodeCore.getBarnetilleggForsvaretIPeriode()));
    }
    return barnetilleggForsvaretPeriodeListe.stream()
        .sorted(Comparator.comparing(barnetilleggForsvaretPeriode -> barnetilleggForsvaretPeriode
            .getDatoFraTil().getDatoFra())).collect(Collectors.toList());
  }

  private List<SjablonPeriode> mapSjablonPeriodeListe(List<SjablonPeriodeCore> sjablonPeriodeListeCore) {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    for (SjablonPeriodeCore sjablonPeriodeCore : sjablonPeriodeListeCore) {
      var sjablonNokkelListe = new ArrayList<SjablonNokkel>();
      var sjablonInnholdListe = new ArrayList<SjablonInnhold>();
      for (SjablonNokkelCore sjablonNokkelCore : sjablonPeriodeCore.getSjablonNokkelListe()) {
        sjablonNokkelListe.add(new SjablonNokkel(sjablonNokkelCore.getSjablonNokkelNavn(), sjablonNokkelCore.getSjablonNokkelVerdi()));
      }
      for (SjablonInnholdCore sjablonInnholdCore : sjablonPeriodeCore.getSjablonInnholdListe()) {
        sjablonInnholdListe.add(new SjablonInnhold(sjablonInnholdCore.getSjablonInnholdNavn(), sjablonInnholdCore.getSjablonInnholdVerdi()));
      }
      sjablonPeriodeListe.add(new SjablonPeriode(
          new Periode(sjablonPeriodeCore.getSjablonPeriodeDatoFraTil().getPeriodeDatoFra(),
              sjablonPeriodeCore.getSjablonPeriodeDatoFraTil().getPeriodeDatoTil()),
          new Sjablon(sjablonPeriodeCore.getSjablonNavn(), sjablonNokkelListe, sjablonInnholdListe)));
    }
    return sjablonPeriodeListe;
  }

  private BeregnBarnebidragResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnBarnebidragResultat resultat) {
    return new BeregnBarnebidragResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      var barnebidragResultatGrunnlag = resultatPeriode.getResultatGrunnlag();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(resultatPeriode.getResultatDatoFraTil().getDatoFra(), resultatPeriode.getResultatDatoFraTil().getDatoTil()),
          mapResultatBeregning(resultatPeriode.getResultatBeregningListe()),
          new GrunnlagBeregningPeriodisertCore(
              new BidragsevneCore(barnebidragResultatGrunnlag.getBidragsevne().getBidragsevneBelop(),
              barnebidragResultatGrunnlag.getBidragsevne().getTjuefemProsentInntekt()),
              mapResultatGrunnlag(barnebidragResultatGrunnlag.getGrunnlagPerBarnListe()),
              barnebidragResultatGrunnlag.getBarnetilleggForsvaret(),
              mapResultatGrunnlagSjabloner(resultatPeriode.getResultatBeregningListe().get(0).getSjablonListe()))));
    }
    return resultatPeriodeCoreListe;
  }

  private List<ResultatBeregningCore>mapResultatBeregning(List<ResultatBeregning> resultatBeregningListe) {
    var resultatBeregningListeCore = new ArrayList<ResultatBeregningCore>();
    for (ResultatBeregning resultatBeregning : resultatBeregningListe) {
      resultatBeregningListeCore
          .add(new ResultatBeregningCore(resultatBeregning.getSoknadsbarnPersonId(),
              resultatBeregning.getResultatBarnebidragBelop(), resultatBeregning.getResultatkode().toString()));
    }
    return resultatBeregningListeCore;
  }

  private List<GrunnlagBeregningPerBarnCore> mapResultatGrunnlag(List<GrunnlagBeregningPerBarn> grunnlagBeregningPerBarnListe) {
    var grunnlagPerBarnListeCore = new ArrayList<GrunnlagBeregningPerBarnCore>();
    for (GrunnlagBeregningPerBarn grunnlagBeregningPerBarn : grunnlagBeregningPerBarnListe) {
      grunnlagPerBarnListeCore.add(new GrunnlagBeregningPerBarnCore(
          grunnlagBeregningPerBarn.getSoknadsbarnPersonId(),
          new BPsAndelUnderholdskostnadCore(
              grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadProsent(),
          grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBPsAndelUnderholdskostnadBelop(),
              grunnlagBeregningPerBarn.getBPsAndelUnderholdskostnad().getBarnetErSelvforsorget()),
          grunnlagBeregningPerBarn.getSamvaersfradrag(),
          grunnlagBeregningPerBarn.getDeltBosted(),
          new BarnetilleggCore(grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggBelop(),
          grunnlagBeregningPerBarn.getBarnetilleggBP().getBarnetilleggSkattProsent()),
          new BarnetilleggCore(grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggBelop(),
              grunnlagBeregningPerBarn.getBarnetilleggBM().getBarnetilleggSkattProsent())));
    }
    return grunnlagPerBarnListeCore;
  }

  private List<SjablonNavnVerdiCore> mapResultatGrunnlagSjabloner(List<SjablonNavnVerdi> resultatGrunnlagSjablonListe) {
    var resultatGrunnlagSjablonListeCore = new ArrayList<SjablonNavnVerdiCore>();
    for (SjablonNavnVerdi resultatGrunnlagSjablon : resultatGrunnlagSjablonListe) {
      resultatGrunnlagSjablonListeCore
          .add(new SjablonNavnVerdiCore(resultatGrunnlagSjablon.getSjablonNavn(), resultatGrunnlagSjablon.getSjablonVerdi()));
    }
    return resultatGrunnlagSjablonListeCore;
  }
}
