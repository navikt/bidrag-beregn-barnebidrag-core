package no.nav.bidrag.beregn.barnebidrag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidrag;
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
import no.nav.bidrag.beregn.barnebidrag.dto.AndreLopendeBidragCore;
import no.nav.bidrag.beregn.barnebidrag.dto.AndreLopendeBidragPeriodeCore;
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
    var andreLopendeBidragPeriodeListe = mapAndreLopendeBidragPeriodeListe(
        beregnBarnebidragGrunnlagCore.getAndreLopendeBidragPeriodeListe());

    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBarnebidragGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, bidragsevnePeriodeListe,
        bPsAndelUnderholdskostnadPeriodeListe, samvaersfradragPeriodeListe,
        deltBostedPeriodeListe, barnetilleggBPPeriodeListe, barnetilleggBMPeriodeListe,
        barnetilleggForsvaretPeriodeListe, andreLopendeBidragPeriodeListe, sjablonPeriodeListe);
  }

  private List<BidragsevnePeriode> mapBidragsevnePeriodeListe(
      List<BidragsevnePeriodeCore> bidragsevnePeriodeListeCore) {
    var bidragsevnePeriodeListe = new ArrayList<BidragsevnePeriode>();
    for (BidragsevnePeriodeCore bidragsevnePeriodeCore : bidragsevnePeriodeListeCore) {
      bidragsevnePeriodeListe.add(new BidragsevnePeriode(
          new Periode(bidragsevnePeriodeCore.getBidragsevneDatoFraTil().getDatoFom(),
              bidragsevnePeriodeCore.getBidragsevneDatoFraTil().getDatoTil()),
          bidragsevnePeriodeCore.getBidragsevneBelop(),
          bidragsevnePeriodeCore.getTjuefemProsentInntekt()));
    }
    return bidragsevnePeriodeListe.stream()
        .sorted(Comparator.comparing(bidragsevnePeriode -> bidragsevnePeriode
            .getBidragsevneDatoFraTil().getDatoFom())).collect(Collectors.toList());
  }

  private List<BPsAndelUnderholdskostnadPeriode> mapBPsAndelUnderholdskostnadPeriodeListe(
      List<BPsAndelUnderholdskostnadPeriodeCore> bPsAndelUnderholdskostnadPeriodeListeCore) {
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    for (BPsAndelUnderholdskostnadPeriodeCore bPsAndelUnderholdskostnadPeriodeCore : bPsAndelUnderholdskostnadPeriodeListeCore) {
      bPsAndelUnderholdskostnadPeriodeListe.add(new BPsAndelUnderholdskostnadPeriode(
          bPsAndelUnderholdskostnadPeriodeCore.getSoknadsbarnPersonId(),
          new Periode(bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadDatoFraTil().getDatoFom(),
              bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadDatoFraTil().getDatoTil()),
          bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadProsent(),
          bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadBelop(),
          bPsAndelUnderholdskostnadPeriodeCore.getBarnetErSelvforsorget()));
    }
    return bPsAndelUnderholdskostnadPeriodeListe.stream()
        .sorted(Comparator.comparing(bPsAndelUnderholdskostnadPeriode -> bPsAndelUnderholdskostnadPeriode
            .getDatoFraTil().getDatoFom())).collect(Collectors.toList());
  }

  private List<SamvaersfradragPeriode> mapSamvaersfradragPeriodeListe(
      List<SamvaersfradragPeriodeCore> samvaersfradragPeriodeListeCore) {
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriode>();
    for (SamvaersfradragPeriodeCore samvaersfradragPeriodeCore : samvaersfradragPeriodeListeCore) {
      samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(
          samvaersfradragPeriodeCore.getSoknadsbarnPersonId(),
          new Periode(samvaersfradragPeriodeCore.getSamvaersfradragDatoFraTil().getDatoFom(),
              samvaersfradragPeriodeCore.getSamvaersfradragDatoFraTil().getDatoTil()),
          samvaersfradragPeriodeCore.getSamvaersfradragBelop()));
    }
    return samvaersfradragPeriodeListe.stream()
        .sorted(Comparator.comparing(samvaersfradragPeriode -> samvaersfradragPeriode
            .getDatoFraTil().getDatoFom())).collect(Collectors.toList());
  }

  private List<DeltBostedPeriode> mapDeltBostedPeriodeListe(
      List<DeltBostedPeriodeCore> deltBostedPeriodeListeCore) {
    var deltBostedPeriodeListe = new ArrayList<DeltBostedPeriode>();
    for (DeltBostedPeriodeCore deltBostedPeriodeCore : deltBostedPeriodeListeCore) {
      deltBostedPeriodeListe.add(new DeltBostedPeriode(
          deltBostedPeriodeCore.getSoknadsbarnPersonId(),
          new Periode(deltBostedPeriodeCore.getDeltBostedDatoFraTil().getDatoFom(),
              deltBostedPeriodeCore.getDeltBostedDatoFraTil().getDatoTil()),
          deltBostedPeriodeCore.getDeltBostedIPeriode()));
    }
    return deltBostedPeriodeListe.stream()
        .sorted(Comparator.comparing(deltBostedPeriode -> deltBostedPeriode
            .getDatoFraTil().getDatoFom())).collect(Collectors.toList());
  }

  private List<BarnetilleggPeriode> mapBarnetilleggBPPeriodeListe(
      List<BarnetilleggPeriodeCore> barnetilleggBPPeriodeListeCore) {
    var barnetilleggBPPeriodeListe = new ArrayList<BarnetilleggPeriode>();
    for (BarnetilleggPeriodeCore barnetilleggBPPeriodeCore : barnetilleggBPPeriodeListeCore) {
      barnetilleggBPPeriodeListe.add(new BarnetilleggPeriode(
          barnetilleggBPPeriodeCore.getSoknadsbarnPersonId(),
          new Periode(barnetilleggBPPeriodeCore.getBarnetilleggDatoFraTil().getDatoFom(),
              barnetilleggBPPeriodeCore.getBarnetilleggDatoFraTil().getDatoTil()),
          barnetilleggBPPeriodeCore.getBarnetilleggBelop(),
          barnetilleggBPPeriodeCore.getBarnetilleggSkattProsent()));
    }
    return barnetilleggBPPeriodeListe.stream()
        .sorted(Comparator.comparing(barnetilleggBPPeriode -> barnetilleggBPPeriode
            .getDatoFraTil().getDatoFom())).collect(Collectors.toList());
  }

  private List<BarnetilleggPeriode> mapBarnetilleggBMPeriodeListe(
      List<BarnetilleggPeriodeCore> barnetilleggBMPeriodeListeCore) {
    var barnetilleggBMPeriodeListe = new ArrayList<BarnetilleggPeriode>();
    for (BarnetilleggPeriodeCore barnetilleggBMPeriodeCore : barnetilleggBMPeriodeListeCore) {
      barnetilleggBMPeriodeListe.add(new BarnetilleggPeriode(
          barnetilleggBMPeriodeCore.getSoknadsbarnPersonId(),
          new Periode(barnetilleggBMPeriodeCore.getBarnetilleggDatoFraTil().getDatoFom(),
              barnetilleggBMPeriodeCore.getBarnetilleggDatoFraTil().getDatoTil()),
          barnetilleggBMPeriodeCore.getBarnetilleggBelop(),
          barnetilleggBMPeriodeCore.getBarnetilleggSkattProsent()));
    }
    return barnetilleggBMPeriodeListe.stream()
        .sorted(Comparator.comparing(barnetilleggBMPeriode -> barnetilleggBMPeriode
            .getDatoFraTil().getDatoFom())).collect(Collectors.toList());
  }

  private List<BarnetilleggForsvaretPeriode> mapBarnetilleggForsvaretPeriodeListe(
      List<BarnetilleggForsvaretPeriodeCore> barnetilleggForsvaretPeriodeListeCore) {
    var barnetilleggForsvaretPeriodeListe = new ArrayList<BarnetilleggForsvaretPeriode>();
    for (BarnetilleggForsvaretPeriodeCore barnetilleggForsvaretPeriodeCore : barnetilleggForsvaretPeriodeListeCore) {
      barnetilleggForsvaretPeriodeListe.add(new BarnetilleggForsvaretPeriode(
          new Periode(barnetilleggForsvaretPeriodeCore.getBarnetilleggForsvaretDatoFraTil().getDatoFom(),
              barnetilleggForsvaretPeriodeCore.getBarnetilleggForsvaretDatoFraTil().getDatoTil()),
          barnetilleggForsvaretPeriodeCore.getBarnetilleggForsvaretIPeriode()));
    }
    return barnetilleggForsvaretPeriodeListe.stream()
        .sorted(Comparator.comparing(barnetilleggForsvaretPeriode -> barnetilleggForsvaretPeriode
            .getDatoFraTil().getDatoFom())).collect(Collectors.toList());
  }

  private List<AndreLopendeBidragPeriode> mapAndreLopendeBidragPeriodeListe(
      List<AndreLopendeBidragPeriodeCore> andreLopendeBidragPeriodeListeCore) {
    var andreLopendeBidragPeriodeListe = new ArrayList<AndreLopendeBidragPeriode>();
    for (AndreLopendeBidragPeriodeCore andreLopendeBidragPeriodeCore : andreLopendeBidragPeriodeListeCore) {
      andreLopendeBidragPeriodeListe.add(new AndreLopendeBidragPeriode(
          new Periode(andreLopendeBidragPeriodeCore.getPeriodeDatoFraTil().getDatoFom(),
              andreLopendeBidragPeriodeCore.getPeriodeDatoFraTil().getDatoTil()),
          andreLopendeBidragPeriodeCore.getBarnPersonId(),
          andreLopendeBidragPeriodeCore.getBidragBelop(),
          andreLopendeBidragPeriodeCore.getSamvaersfradragBelop()
      ));
    }
    return andreLopendeBidragPeriodeListe.stream()
        .sorted(Comparator.comparing(andreLopendeBidragPeriode -> andreLopendeBidragPeriode
            .getDatoFraTil().getDatoFom())).collect(Collectors.toList());
  }

  private List<SjablonPeriode> mapSjablonPeriodeListe(List<SjablonPeriodeCore> sjablonPeriodeListeCore) {
    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    for (SjablonPeriodeCore sjablonPeriodeCore : sjablonPeriodeListeCore) {
      var sjablonNokkelListe = new ArrayList<SjablonNokkel>();
      var sjablonInnholdListe = new ArrayList<SjablonInnhold>();
      for (SjablonNokkelCore sjablonNokkelCore : sjablonPeriodeCore.getNokkelListe()) {
        sjablonNokkelListe.add(new SjablonNokkel(sjablonNokkelCore.getNavn(), sjablonNokkelCore.getVerdi()));
      }
      for (SjablonInnholdCore sjablonInnholdCore : sjablonPeriodeCore.getInnholdListe()) {
        sjablonInnholdListe.add(new SjablonInnhold(sjablonInnholdCore.getNavn(), sjablonInnholdCore.getVerdi()));
      }
      sjablonPeriodeListe.add(new SjablonPeriode(
          new Periode(sjablonPeriodeCore.getPeriode().getDatoFom(),
              sjablonPeriodeCore.getPeriode().getDatoTil()),
          new Sjablon(sjablonPeriodeCore.getNavn(), sjablonNokkelListe, sjablonInnholdListe)));
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
          new PeriodeCore(resultatPeriode.getResultatDatoFraTil().getDatoFom(), resultatPeriode.getResultatDatoFraTil().getDatoTil()),
          mapResultatBeregning(resultatPeriode.getResultatBeregningListe()),
          new GrunnlagBeregningPeriodisertCore(
              new BidragsevneCore(barnebidragResultatGrunnlag.getBidragsevne().getBidragsevneBelop(),
              barnebidragResultatGrunnlag.getBidragsevne().getTjuefemProsentInntekt()),
              mapResultatGrunnlag(barnebidragResultatGrunnlag.getGrunnlagPerBarnListe()),
              barnebidragResultatGrunnlag.getBarnetilleggForsvaret(),
              mapResultatGrunnlagAndreLopendeBidrag(barnebidragResultatGrunnlag.getAndreLopendeBidragListe()),
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

  private List<AndreLopendeBidragCore> mapResultatGrunnlagAndreLopendeBidrag(
      List<AndreLopendeBidrag> resultatGrunnlagAndreLopendeBidragListe) {
    var resultatGrunnlagAndreLopendeBidragListeCore = new ArrayList<AndreLopendeBidragCore>();
    for (AndreLopendeBidrag resultatGrunnlagAndreLopendeBidrag : resultatGrunnlagAndreLopendeBidragListe) {
      resultatGrunnlagAndreLopendeBidragListeCore
          .add(new AndreLopendeBidragCore(resultatGrunnlagAndreLopendeBidrag.getBarnPersonId(),
              resultatGrunnlagAndreLopendeBidrag.getLopendeBidragBelop(),
              resultatGrunnlagAndreLopendeBidrag.getBeregnetSamvaersfradragBelop()
          ));
    }
    return resultatGrunnlagAndreLopendeBidragListeCore;
  }

  private List<SjablonNavnVerdiCore> mapResultatGrunnlagSjabloner(List<SjablonNavnVerdi> resultatGrunnlagSjablonListe) {
    var resultatGrunnlagSjablonListeCore = new ArrayList<SjablonNavnVerdiCore>();
    for (SjablonNavnVerdi resultatGrunnlagSjablon : resultatGrunnlagSjablonListe) {
      resultatGrunnlagSjablonListeCore
          .add(new SjablonNavnVerdiCore(resultatGrunnlagSjablon.getNavn(), resultatGrunnlagSjablon.getVerdi()));
    }
    return resultatGrunnlagSjablonListeCore;
  }
}
