package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragResultat;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BPsAndelUnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnKostnadsberegnetBidragGrunnlagCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnKostnadsberegnetBidragResultatCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.SamvaersfradragPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.UnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode;

public class KostnadsberegnetBidragCoreImpl implements KostnadsberegnetBidragCore {

  public KostnadsberegnetBidragCoreImpl(KostnadsberegnetBidragPeriode kostnadsberegnetBidragPeriode) {
    this.kostnadsberegnetBidragPeriode = kostnadsberegnetBidragPeriode;
  }

  private final KostnadsberegnetBidragPeriode kostnadsberegnetBidragPeriode;

  public BeregnKostnadsberegnetBidragResultatCore beregnKostnadsberegnetBidrag(
      BeregnKostnadsberegnetBidragGrunnlagCore beregnKostnadsberegnetBidragGrunnlagCore) {
    var beregnKostnadsberegnetBidragGrunnlag = mapTilBusinessObject(beregnKostnadsberegnetBidragGrunnlagCore);
    var beregnKostnadsberegnetBidragResultat = new BeregnKostnadsberegnetBidragResultat(Collections.emptyList());
    var avvikListe = kostnadsberegnetBidragPeriode.validerInput(beregnKostnadsberegnetBidragGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnKostnadsberegnetBidragResultat = kostnadsberegnetBidragPeriode.beregnPerioder(beregnKostnadsberegnetBidragGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnKostnadsberegnetBidragResultat);
  }

  private BeregnKostnadsberegnetBidragGrunnlag mapTilBusinessObject(BeregnKostnadsberegnetBidragGrunnlagCore beregnKostnadsberegnetBidragGrunnlagCore) {
    var beregnDatoFra = beregnKostnadsberegnetBidragGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnKostnadsberegnetBidragGrunnlagCore.getBeregnDatoTil();
    var soknadsbarnPersonId = beregnKostnadsberegnetBidragGrunnlagCore.getSoknadsbarnPersonId();
    var underholdskostnadPeriodeListe = mapUnderholdskostnadPeriodeListe(
        beregnKostnadsberegnetBidragGrunnlagCore.getUnderholdskostnadPeriodeListe());
    var bPsAndelUnderholdskostnadPeriodeListe = mapBPsAndelUnderholdskostnadPeriodeListe(
        beregnKostnadsberegnetBidragGrunnlagCore.getBPsAndelUnderholdskostnadPeriodeListe());
    var samvaersfradragPeriodeListe = mapSamvaersfradragPeriodeListe(
        beregnKostnadsberegnetBidragGrunnlagCore.getSamvaersfradragPeriodeListe());

    return new BeregnKostnadsberegnetBidragGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId, underholdskostnadPeriodeListe,
        bPsAndelUnderholdskostnadPeriodeListe, samvaersfradragPeriodeListe);
  }

  private List<UnderholdskostnadPeriode> mapUnderholdskostnadPeriodeListe(List<UnderholdskostnadPeriodeCore> underholdskostnadPeriodeListeCore) {
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    for (UnderholdskostnadPeriodeCore underholdskostnadPeriodeCore : underholdskostnadPeriodeListeCore) {
      underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
          new Periode(underholdskostnadPeriodeCore.getUnderholdskostnadPeriodeDatoFraTil().getDatoFom(),
              underholdskostnadPeriodeCore.getUnderholdskostnadPeriodeDatoFraTil().getDatoTil()),
          underholdskostnadPeriodeCore.getUnderholdskostnadBelop()));
    }
    return underholdskostnadPeriodeListe;
  }

  private List<BPsAndelUnderholdskostnadPeriode> mapBPsAndelUnderholdskostnadPeriodeListe(
      List<BPsAndelUnderholdskostnadPeriodeCore> bPsAndelUnderholdskostnadPeriodeListeCore) {
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    for (BPsAndelUnderholdskostnadPeriodeCore bPsAndelUnderholdskostnadPeriodeCore : bPsAndelUnderholdskostnadPeriodeListeCore) {
      bPsAndelUnderholdskostnadPeriodeListe.add(new BPsAndelUnderholdskostnadPeriode(
          new Periode(bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadPeriodeDatoFraTil().getDatoFom(),
              bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadPeriodeDatoFraTil().getDatoTil()),
          bPsAndelUnderholdskostnadPeriodeCore.getBPsAndelUnderholdskostnadProsent()));
    }
    return bPsAndelUnderholdskostnadPeriodeListe;
  }

  private List<SamvaersfradragPeriode> mapSamvaersfradragPeriodeListe(List<SamvaersfradragPeriodeCore> samvaersfradragPeriodeListeCore) {
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriode>();
    for (SamvaersfradragPeriodeCore samvaersfradragPeriodeCore : samvaersfradragPeriodeListeCore) {
      samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(
          new Periode(samvaersfradragPeriodeCore.getSamvaersfradragDatoPeriodeFraTil().getDatoFom(),
              samvaersfradragPeriodeCore.getSamvaersfradragDatoPeriodeFraTil().getDatoTil()),
          samvaersfradragPeriodeCore.getSamvaersfradrag()));
    }
    return samvaersfradragPeriodeListe;
  }

  private BeregnKostnadsberegnetBidragResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnKostnadsberegnetBidragResultat resultat) {
    return new BeregnKostnadsberegnetBidragResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<AvvikCore> mapAvvik(List<Avvik> avvikListe) {
    var avvikCoreListe = new ArrayList<AvvikCore>();
    for (Avvik avvik : avvikListe) {
      avvikCoreListe.add(new AvvikCore(avvik.getAvvikTekst(), avvik.getAvvikType().toString()));
    }
    return avvikCoreListe;
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> periodeResultatListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode periodeResultat : periodeResultatListe) {
      var kostnadsberegnetBidragResultat = periodeResultat.getResultatBeregning();
      var kostnadsberegnetBidragResultatGrunnlag = periodeResultat.getResultatGrunnlag();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          periodeResultat.getSoknadsbarnPersonId(),
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFom(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(kostnadsberegnetBidragResultat.getResultatkostnadsberegnetbidragBelop()),
          new ResultatGrunnlagCore(kostnadsberegnetBidragResultatGrunnlag.getUnderholdskostnadBelop(),
              kostnadsberegnetBidragResultatGrunnlag.getBPsAndelUnderholdskostnadProsent(),
              kostnadsberegnetBidragResultatGrunnlag.getSamvaersfradragBelop()
          )));
    }
    return resultatPeriodeCoreListe;
  }
}
