package no.nav.bidrag.beregn.samvaersfradrag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersklassePeriode;
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragGrunnlagCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragResultatCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.SamvaersklassePeriodeCore;
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode;

public class SamvaersfradragCoreImpl implements SamvaersfradragCore {

  public SamvaersfradragCoreImpl(SamvaersfradragPeriode samvaersfradragPeriode) {
    this.samvaersfradragPeriode = samvaersfradragPeriode;
  }

  private final SamvaersfradragPeriode samvaersfradragPeriode;

  public BeregnSamvaersfradragResultatCore beregnSamvaersfradrag(
      BeregnSamvaersfradragGrunnlagCore beregnSamvaersfradragGrunnlagCore) {
    var beregnSamvaersfradragGrunnlag = mapTilBusinessObject(beregnSamvaersfradragGrunnlagCore);
    var beregnSamvaersfradragResultat = new BeregnSamvaersfradragResultat(Collections.emptyList());
    var avvikListe = samvaersfradragPeriode.validerInput(beregnSamvaersfradragGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnSamvaersfradragResultat = samvaersfradragPeriode.beregnPerioder(beregnSamvaersfradragGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnSamvaersfradragResultat);
  }

  private BeregnSamvaersfradragGrunnlag mapTilBusinessObject(BeregnSamvaersfradragGrunnlagCore beregnSamvaersfradragGrunnlagCore) {
    var soknadsbarnPersonId = beregnSamvaersfradragGrunnlagCore.getSoknadsbarnPersonId();
    var beregnDatoFra = beregnSamvaersfradragGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnSamvaersfradragGrunnlagCore.getBeregnDatoTil();
    var soknadsbarnFodselsdato = beregnSamvaersfradragGrunnlagCore.getSoknadsbarnFodselsdato();
    var samvaersklassePeriodeListe = mapSamvaersklassePeriodeListe(beregnSamvaersfradragGrunnlagCore.getSamvaersklassePeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnSamvaersfradragGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnSamvaersfradragGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId, soknadsbarnFodselsdato,
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
              mapResultatGrunnlagSjabloner(samvaersfradragResultat.getSjablonListe()))));
    }
    return resultatPeriodeCoreListe;
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
