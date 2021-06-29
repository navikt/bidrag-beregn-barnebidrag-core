package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.felles.FellesCore;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnetKostnadsberegnetBidragResultat;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BPsAndelUnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnKostnadsberegnetBidragGrunnlagCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.BeregnetKostnadsberegnetBidragResultatCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.SamvaersfradragPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.dto.UnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode;

public class KostnadsberegnetBidragCoreImpl extends FellesCore implements KostnadsberegnetBidragCore {

  private final KostnadsberegnetBidragPeriode kostnadsberegnetBidragPeriode;

  public KostnadsberegnetBidragCoreImpl(KostnadsberegnetBidragPeriode kostnadsberegnetBidragPeriode) {
    this.kostnadsberegnetBidragPeriode = kostnadsberegnetBidragPeriode;
  }

  public BeregnetKostnadsberegnetBidragResultatCore beregnKostnadsberegnetBidrag(
      BeregnKostnadsberegnetBidragGrunnlagCore beregnKostnadsberegnetBidragGrunnlagCore) {
    var beregnKostnadsberegnetBidragGrunnlag = mapTilBusinessObject(beregnKostnadsberegnetBidragGrunnlagCore);
    var beregnKostnadsberegnetBidragResultat = new BeregnetKostnadsberegnetBidragResultat(Collections.emptyList());
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
          underholdskostnadPeriodeCore.getReferanse(),
          new Periode(underholdskostnadPeriodeCore.getPeriode().getDatoFom(), underholdskostnadPeriodeCore.getPeriode().getDatoTil()),
          underholdskostnadPeriodeCore.getBelop()));
    }
    return underholdskostnadPeriodeListe;
  }

  private List<BPsAndelUnderholdskostnadPeriode> mapBPsAndelUnderholdskostnadPeriodeListe(
      List<BPsAndelUnderholdskostnadPeriodeCore> bPsAndelUnderholdskostnadPeriodeListeCore) {
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<BPsAndelUnderholdskostnadPeriode>();
    for (BPsAndelUnderholdskostnadPeriodeCore bPsAndelUnderholdskostnadPeriodeCore : bPsAndelUnderholdskostnadPeriodeListeCore) {
      bPsAndelUnderholdskostnadPeriodeListe.add(new BPsAndelUnderholdskostnadPeriode(
          bPsAndelUnderholdskostnadPeriodeCore.getReferanse(),
          new Periode(bPsAndelUnderholdskostnadPeriodeCore.getPeriode().getDatoFom(), bPsAndelUnderholdskostnadPeriodeCore.getPeriode().getDatoTil()),
          bPsAndelUnderholdskostnadPeriodeCore.getAndelProsent()));
    }
    return bPsAndelUnderholdskostnadPeriodeListe;
  }

  private List<SamvaersfradragPeriode> mapSamvaersfradragPeriodeListe(List<SamvaersfradragPeriodeCore> samvaersfradragPeriodeListeCore) {
    var samvaersfradragPeriodeListe = new ArrayList<SamvaersfradragPeriode>();
    for (SamvaersfradragPeriodeCore samvaersfradragPeriodeCore : samvaersfradragPeriodeListeCore) {
      samvaersfradragPeriodeListe.add(new SamvaersfradragPeriode(
          samvaersfradragPeriodeCore.getReferanse(),
          new Periode(samvaersfradragPeriodeCore.getPeriode().getDatoFom(), samvaersfradragPeriodeCore.getPeriode().getDatoTil()),
          samvaersfradragPeriodeCore.getBelop()));
    }
    return samvaersfradragPeriodeListe;
  }

  private BeregnetKostnadsberegnetBidragResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnetKostnadsberegnetBidragResultat resultat) {
    return new BeregnetKostnadsberegnetBidragResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> resultatPeriodeListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode resultatPeriode : resultatPeriodeListe) {
      var kostnadsberegnetBidragResultat = resultatPeriode.getResultat();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          resultatPeriode.getSoknadsbarnPersonId(),
          new PeriodeCore(resultatPeriode.getPeriode().getDatoFom(), resultatPeriode.getPeriode().getDatoTil()),
          new ResultatBeregningCore(kostnadsberegnetBidragResultat.getBelop()),
          mapReferanseListe(resultatPeriode)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getGrunnlag();

    var referanseListe = new ArrayList<String>();
    referanseListe.add(resultatGrunnlag.getUnderholdskostnad().getReferanse());
    referanseListe.add(resultatGrunnlag.getBPsAndelUnderholdskostnad().getReferanse());
    referanseListe.add(resultatGrunnlag.getSamvaersfradrag().getReferanse());
    return referanseListe;
  }
}
