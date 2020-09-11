package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadResultatCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.InntekterPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;

public class BPsAndelUnderholdskostnadCoreImpl implements BPsAndelUnderholdskostnadCore{

  public BPsAndelUnderholdskostnadCoreImpl(
      BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriode) {
    this.bPsAndelunderholdskostnadPeriode = bPsAndelunderholdskostnadPeriode;
  }

  private BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriode;


  @Override
  public BeregnBPsAndelUnderholdskostnadResultatCore beregnBPsAndelUnderholdskostnad(
      BeregnBPsAndelUnderholdskostnadGrunnlagCore beregnBPsAndelUnderholdskostnadGrunnlagCore) {


    var beregnBPsAndelUnderholdskostnadGrunnlag = mapTilBusinessObject(beregnBPsAndelUnderholdskostnadGrunnlagCore);
    var beregnBPsAndelUnderholdskostnadResultat = new BeregnBPsAndelUnderholdskostnadResultat(Collections.emptyList());
    var avvikListe = bPsAndelunderholdskostnadPeriode.validerInput(beregnBPsAndelUnderholdskostnadGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnBPsAndelUnderholdskostnadResultat = bPsAndelunderholdskostnadPeriode.beregnPerioder(beregnBPsAndelUnderholdskostnadGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnBPsAndelUnderholdskostnadResultat);
  }

  private BeregnBPsAndelUnderholdskostnadGrunnlag mapTilBusinessObject(
      BeregnBPsAndelUnderholdskostnadGrunnlagCore beregnBPsAndelUnderholdskostnadGrunnlagCore) {
    var beregnDatoFra = beregnBPsAndelUnderholdskostnadGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnBPsAndelUnderholdskostnadGrunnlagCore.getBeregnDatoTil();
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore.getSjablonPeriodeListe());
    var inntekterPeriodeListe = mapInntekterPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore.getInntekterPeriodeListe());

    return new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, inntekterPeriodeListe, sjablonPeriodeListe);
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

  private List<InntektPeriode> mapInntekterPeriodeListe(List<InntekterPeriodeCore> inntekterPeriodeListeCore) {
    var inntekterPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntekterPeriodeCore inntekterPeriodeCore : inntekterPeriodeListeCore) {
      inntekterPeriodeListe.add(new InntektPeriode(
          new Periode(inntekterPeriodeCore.getInntekterPeriodeDatoFraTil().getPeriodeDatoFra(),
              inntekterPeriodeCore.getInntekterPeriodeDatoFraTil().getPeriodeDatoTil()),
          inntekterPeriodeCore.getInntektBP(),
          inntekterPeriodeCore.getInntektBM(),
          inntekterPeriodeCore.getInntektBB()));
    }
    return inntekterPeriodeListe;
  }



  private BeregnBPsAndelUnderholdskostnadResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnBPsAndelUnderholdskostnadResultat resultat) {
    return new BeregnBPsAndelUnderholdskostnadResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      var bPsAndelunderholdskostnadResultat = periodeResultat.getResultatBeregning();
      var bPsAndelunderholdskostnadResultatGrunnlag = periodeResultat.getResultatGrunnlag();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFra(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(bPsAndelunderholdskostnadResultat.getResultatAndelProsent()),
          new ResultatGrunnlagCore(bPsAndelunderholdskostnadResultatGrunnlag.getInntekter().getInntektBP(),
              bPsAndelunderholdskostnadResultatGrunnlag.getInntekter().getInntektBM(),
              bPsAndelunderholdskostnadResultatGrunnlag.getInntekter().getInntektBB(),
              mapResultatGrunnlagSjabloner(bPsAndelunderholdskostnadResultatGrunnlag.getSjablonListe()))));
    }
    return resultatPeriodeCoreListe;
  }

  private List<SjablonCore> mapResultatGrunnlagSjabloner(List<Sjablon> resultatGrunnlagSjablonListe) {
    var resultatGrunnlagSjablonListeCore = new ArrayList<SjablonCore>();
    for (Sjablon resultatGrunnlagSjablon : resultatGrunnlagSjablonListe) {
      var sjablonNokkelListeCore = new ArrayList<SjablonNokkelCore>();
      var sjablonInnholdListeCore = new ArrayList<SjablonInnholdCore>();
      for (SjablonNokkel sjablonNokkel : resultatGrunnlagSjablon.getSjablonNokkelListe()) {
        sjablonNokkelListeCore.add(new SjablonNokkelCore(sjablonNokkel.getSjablonNokkelNavn(), sjablonNokkel.getSjablonNokkelVerdi()));
      }
      for (SjablonInnhold sjablonInnhold : resultatGrunnlagSjablon.getSjablonInnholdListe()) {
        sjablonInnholdListeCore.add(new SjablonInnholdCore(sjablonInnhold.getSjablonInnholdNavn(), sjablonInnhold.getSjablonInnholdVerdi()));
      }
      resultatGrunnlagSjablonListeCore
          .add(new SjablonCore(resultatGrunnlagSjablon.getSjablonNavn(), sjablonNokkelListeCore, sjablonInnholdListeCore));
    }

    return resultatGrunnlagSjablonListeCore;
  }

}
