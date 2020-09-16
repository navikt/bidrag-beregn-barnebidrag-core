package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadResultatCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.InntektCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.UnderholdskostnadPeriodeCore;
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
import no.nav.bidrag.beregn.felles.enums.InntektType;

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
    var soknadsbarnPersonId = beregnBPsAndelUnderholdskostnadGrunnlagCore.getSoknadsbarnPersonId();
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore.getSjablonPeriodeListe());
    var underholdskostnadPeriodeListe = mapUnderholdskostnadPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore
        .getUnderholdskostnadPeriodeListe());
    var inntektBPPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore.getInntektBPPeriodeListe());
    var inntektBMPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore.getInntektBMPeriodeListe());
    var inntektBBPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore.getInntektBBPeriodeListe());

    return new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId,
        underholdskostnadPeriodeListe, inntektBPPeriodeListe, inntektBMPeriodeListe,
        inntektBBPeriodeListe, sjablonPeriodeListe);
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

  private List<InntektPeriode> mapInntektPeriodeListe(List<InntektPeriodeCore> inntekterPeriodeListeCore) {
    var inntekterPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore inntektPeriodeCore : inntekterPeriodeListeCore) {
      inntekterPeriodeListe.add(new InntektPeriode(
          new Periode(inntektPeriodeCore.getInntektPeriodeDatoFraTil().getPeriodeDatoFra(),
              inntektPeriodeCore.getInntektPeriodeDatoFraTil().getPeriodeDatoTil()),
              InntektType.valueOf(inntektPeriodeCore.getInntektType()),
              inntektPeriodeCore.getInntektBelop()));
    }
    return inntekterPeriodeListe;
  }

  private List<UnderholdskostnadPeriode> mapUnderholdskostnadPeriodeListe(List<UnderholdskostnadPeriodeCore> underholdskostnadPeriodeListeCore) {
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    for (UnderholdskostnadPeriodeCore underholdskostnadPeriodeCore : underholdskostnadPeriodeListeCore) {
      underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
          new Periode(underholdskostnadPeriodeCore.getUnderholdskostnadDatoFraTil().getPeriodeDatoFra(),
              underholdskostnadPeriodeCore.getUnderholdskostnadDatoFraTil().getPeriodeDatoTil()),
          underholdskostnadPeriodeCore.getUnderholdskostnadBelop()));
    }
    return underholdskostnadPeriodeListe;
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
          new ResultatBeregningCore(bPsAndelunderholdskostnadResultat.getResultatAndelProsent(),
              bPsAndelunderholdskostnadResultat.getResultatAndelBelop()),
          new ResultatGrunnlagCore(bPsAndelunderholdskostnadResultatGrunnlag.getSoknadsbarnPersonId(),
              bPsAndelunderholdskostnadResultatGrunnlag.getUnderholdskostnadBelop(),
              mapResultatGrunnlagInntekt(bPsAndelunderholdskostnadResultatGrunnlag.getInntektBP()),
              mapResultatGrunnlagInntekt(bPsAndelunderholdskostnadResultatGrunnlag.getInntektBM()),
              mapResultatGrunnlagInntekt(bPsAndelunderholdskostnadResultatGrunnlag.getInntektBM()),
              mapResultatGrunnlagSjabloner(bPsAndelunderholdskostnadResultatGrunnlag.getSjablonListe()))));
    }
    return resultatPeriodeCoreListe;
  }


  private List<InntektCore> mapResultatGrunnlagInntekt(List<Inntekt> resultatGrunnlagInntektListe) {
    var resultatGrunnlagInntektListeCore = new ArrayList<InntektCore>();
    for (Inntekt resultatGrunnlagInntekt : resultatGrunnlagInntektListe) {
      resultatGrunnlagInntektListeCore
          .add(new InntektCore(resultatGrunnlagInntekt.getInntektType().toString(), resultatGrunnlagInntekt.getInntektBelop()));
    }
    return resultatGrunnlagInntektListeCore;
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
