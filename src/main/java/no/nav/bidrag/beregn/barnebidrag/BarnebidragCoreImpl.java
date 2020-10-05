/*
package no.nav.bidrag.beregn.barnebidrag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnBarnebidragGrunnlagCore;
import no.nav.bidrag.beregn.barnebidrag.dto.BeregnBarnebidragResultatCore;
import no.nav.bidrag.beregn.barnebidrag.periode.BarnebidragPeriode;
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


public class BarnebidragCoreImpl implements BarnebidragCore {

  public BarnebidragCoreImpl(BarnebidragPeriode barnebidragPeriode) {
    this.barnebidragPeriode = barnebidragPeriode;
  }

  private BarnebidragPeriode barnebidragPeriode;

  public BeregnBarnebidragResultatCore beregnBarnebidragfradrag(
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
//    var soknadsbarnPersonId = beregnBarnebidragGrunnlagCore.getSoknadsbarnPersonId();
    var beregnDatoFra = beregnBarnebidragGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnBarnebidragGrunnlagCore.getBeregnDatoTil();
//    var soknadsbarnFodselsdato = beregnBarnebidragGrunnlagCore.getSoknadsbarnFodselsdato();
    var samvaersklassePeriodeListe = mapSamvaersklassePeriodeListe(beregnBarnebidragGrunnlagCore.getSamvaersklassePeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBarnebidragGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnBarnebidragGrunnlag(beregnDatoFra, beregnDatoTil, //soknadsbarnPersonId,
        soknadsbarnFodselsdato,
        samvaersklassePeriodeListe, sjablonPeriodeListe);
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

  private List<SamvaersklassePeriode> mapSamvaersklassePeriodeListe(
      List<SamvaersklassePeriodeCore> samvaersklassePeriodeListeCore) {
    var samvaersklassePeriodeListe = new ArrayList<SamvaersklassePeriode>();
    for (SamvaersklassePeriodeCore samvaersklassePeriodeCore : samvaersklassePeriodeListeCore) {
      samvaersklassePeriodeListe.add(new SamvaersklassePeriode(
          new Periode(samvaersklassePeriodeCore.getSamvaersklassePeriodeDatoFraTil().getPeriodeDatoFra(),
              samvaersklassePeriodeCore.getSamvaersklassePeriodeDatoFraTil().getPeriodeDatoTil()),
          samvaersklassePeriodeCore.getSamvaersklasse()));
    }
    return samvaersklassePeriodeListe;
  }

  private BeregnSamvaersfradragResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnSamvaersfradragResultat resultat) {
    return new BeregnSamvaersfradragResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      var samvaersfradragResultat = resultatPeriode.getResultatBeregning();
      var samvaersfradragResultatGrunnlag = resultatPeriode.getResultatGrunnlagBeregning();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(resultatPeriode.getSoknadsbarnPersonId(),
          new PeriodeCore(resultatPeriode.getResultatDatoFraTil().getDatoFra(), resultatPeriode.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(samvaersfradragResultat.getResultatSamvaersfradragBelop()),
          new ResultatGrunnlagCore(samvaersfradragResultatGrunnlag.getSoknadBarnAlder(),
              samvaersfradragResultatGrunnlag.getSamvaersklasse(),
              mapResultatGrunnlagSjabloner(samvaersfradragResultatGrunnlag.getSjablonListe()))));
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
*/
