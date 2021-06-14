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
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.InntektType;

public class BPsAndelUnderholdskostnadCoreImpl implements BPsAndelUnderholdskostnadCore{

  public BPsAndelUnderholdskostnadCoreImpl(
      BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriode) {
    this.bPsAndelunderholdskostnadPeriode = bPsAndelunderholdskostnadPeriode;
  }

  private final BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriode;

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

  private List<InntektPeriode> mapInntektPeriodeListe(List<InntektPeriodeCore> inntektPeriodeListeCore) {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore inntektPeriodeCore : inntektPeriodeListeCore) {
      inntektPeriodeListe.add(new InntektPeriode(
          new Periode(inntektPeriodeCore.getInntektPeriodeDatoFraTil().getDatoFom(),
              inntektPeriodeCore.getInntektPeriodeDatoFraTil().getDatoTil()),
          InntektType.valueOf(inntektPeriodeCore.getInntektType()),
          inntektPeriodeCore.getInntektBelop(),
          inntektPeriodeCore.getDeltFordel(),
          inntektPeriodeCore.getSkatteklasse2()));
    }
    return inntektPeriodeListe;
  }

  private List<UnderholdskostnadPeriode> mapUnderholdskostnadPeriodeListe(List<UnderholdskostnadPeriodeCore> underholdskostnadPeriodeListeCore) {
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    for (UnderholdskostnadPeriodeCore underholdskostnadPeriodeCore : underholdskostnadPeriodeListeCore) {
      underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
          new Periode(underholdskostnadPeriodeCore.getUnderholdskostnadDatoFraTil().getDatoFom(),
              underholdskostnadPeriodeCore.getUnderholdskostnadDatoFraTil().getDatoTil()),
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
      var bPsAndelunderholdskostnadResultatGrunnlag = periodeResultat.getResultatGrunnlagBeregning();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          periodeResultat.getSoknadsbarnPersonId(),
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFom(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(bPsAndelunderholdskostnadResultat.getResultatAndelProsent(),
              bPsAndelunderholdskostnadResultat.getResultatAndelBelop(),
              bPsAndelunderholdskostnadResultat.getBarnetErSelvforsorget()),
          new ResultatGrunnlagCore(bPsAndelunderholdskostnadResultatGrunnlag.getUnderholdskostnadBelop(),
              mapResultatGrunnlagInntekt(bPsAndelunderholdskostnadResultatGrunnlag.getInntektBPListe()),
              mapResultatGrunnlagInntekt(bPsAndelunderholdskostnadResultatGrunnlag.getInntektBMListe()),
              mapResultatGrunnlagInntekt(bPsAndelunderholdskostnadResultatGrunnlag.getInntektBBListe()),
              mapResultatGrunnlagSjabloner(bPsAndelunderholdskostnadResultat.getSjablonListe()))));
    }
    return resultatPeriodeCoreListe;
  }

  private List<InntektCore> mapResultatGrunnlagInntekt(List<Inntekt> resultatGrunnlagInntektListe) {
    var resultatGrunnlagInntektListeCore = new ArrayList<InntektCore>();
    for (Inntekt resultatGrunnlagInntekt : resultatGrunnlagInntektListe) {
      resultatGrunnlagInntektListeCore
          .add(new InntektCore(resultatGrunnlagInntekt.getInntektType().toString(), resultatGrunnlagInntekt.getInntektBelop(),
          resultatGrunnlagInntekt.getDeltFordel(), resultatGrunnlagInntekt.getSkatteklasse2()));
    }
    return resultatGrunnlagInntektListeCore;
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
