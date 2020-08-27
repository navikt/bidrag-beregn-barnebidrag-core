package no.nav.bidrag.beregn.underholdskostnad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.underholdskostnad.dto.BarnetilsynMedStonadPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadResultatCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.ForpleiningUtgiftPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.NettoBarnetilsynPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode;

public class UnderholdskostnadCoreImpl implements UnderholdskostnadCore{

  public UnderholdskostnadCoreImpl(UnderholdskostnadPeriode underholdskostnadPeriode) {
    this.underholdskostnadPeriode = underholdskostnadPeriode;
  }

  private UnderholdskostnadPeriode underholdskostnadPeriode;

  public BeregnUnderholdskostnadResultatCore beregnUnderholdskostnad(BeregnUnderholdskostnadGrunnlagCore beregnUnderholdskostnadGrunnlagCore) {
    var beregnUnderholdskostnadGrunnlag = mapTilBusinessObject(beregnUnderholdskostnadGrunnlagCore);
    var beregnUnderholdskostnadResultat = new BeregnUnderholdskostnadResultat(Collections.emptyList());
    var avvikListe = underholdskostnadPeriode.validerInput(beregnUnderholdskostnadGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnUnderholdskostnadResultat = underholdskostnadPeriode.beregnPerioder(beregnUnderholdskostnadGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnUnderholdskostnadResultat);
  }

  private BeregnUnderholdskostnadGrunnlag mapTilBusinessObject(BeregnUnderholdskostnadGrunnlagCore beregnUnderholdskostnadGrunnlagCore) {
    var beregnDatoFra = beregnUnderholdskostnadGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnUnderholdskostnadGrunnlagCore.getBeregnDatoTil();
    var soknadsbarnFodselsdato = beregnUnderholdskostnadGrunnlagCore.getSoknadBarnFodselsdato();
    var barnetilsynMedStonadPeriodeListe = mapBarnetilsynMedStonadPeriodeListe(beregnUnderholdskostnadGrunnlagCore.getBarnetilsynMedStonadPeriodeListe());
    var nettoBarnetilsynPeriodeListe = mapNettoBarnetilsynPeriodeListe(beregnUnderholdskostnadGrunnlagCore.getNettoBarnetilsynPeriodeListe());
    var forpleiningUtgiftPeriodeListe = mapForpleiningUtgiftPeriodeListe(beregnUnderholdskostnadGrunnlagCore.getForpleiningUtgiftPeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnUnderholdskostnadGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnFodselsdato, barnetilsynMedStonadPeriodeListe, nettoBarnetilsynPeriodeListe,
        forpleiningUtgiftPeriodeListe, sjablonPeriodeListe);
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

  private List<BarnetilsynMedStonadPeriode> mapBarnetilsynMedStonadPeriodeListe(List<BarnetilsynMedStonadPeriodeCore> barnetilsynMedStonadPeriodeListeCore) {
    var barnetilsynMedStonadPeriodeListe = new ArrayList<BarnetilsynMedStonadPeriode>();
    for (BarnetilsynMedStonadPeriodeCore barnetilsynMedStonadPeriodeCore : barnetilsynMedStonadPeriodeListeCore) {
      barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
          new Periode(barnetilsynMedStonadPeriodeCore.getBarnetilsynMedStonadPeriodeDatoFraTil().getPeriodeDatoFra(),
              barnetilsynMedStonadPeriodeCore.getBarnetilsynMedStonadPeriodeDatoFraTil().getPeriodeDatoTil()),
          barnetilsynMedStonadPeriodeCore.getBarnetilsynMedStonadTilsynType(),
          barnetilsynMedStonadPeriodeCore.getBarnetilsynStonadStonadType()));
    }
    return barnetilsynMedStonadPeriodeListe;
  }

  private List<NettoBarnetilsynPeriode> mapNettoBarnetilsynPeriodeListe(List<NettoBarnetilsynPeriodeCore> nettoBarnetilsynPeriodeListeCore) {
    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriode>();
    for (NettoBarnetilsynPeriodeCore nettoBarnetilsynPeriodeCore : nettoBarnetilsynPeriodeListeCore) {
      nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
          new Periode(nettoBarnetilsynPeriodeCore.getNettoBarnetilsynPeriodeDatoFraTil().getPeriodeDatoFra(),
              nettoBarnetilsynPeriodeCore.getNettoBarnetilsynPeriodeDatoFraTil().getPeriodeDatoTil()),
          nettoBarnetilsynPeriodeCore.getNettoBarnetilsynBelop()));
    }
    return nettoBarnetilsynPeriodeListe;
  }

  private List<ForpleiningUtgiftPeriode> mapForpleiningUtgiftPeriodeListe(List<ForpleiningUtgiftPeriodeCore> forpleiningUtgiftPeriodeListeCore) {
    var forpleiningUtgiftPeriodeListe = new ArrayList<ForpleiningUtgiftPeriode>();
    for (ForpleiningUtgiftPeriodeCore forpleiningUtgiftPeriodeCore : forpleiningUtgiftPeriodeListeCore) {
      forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
          new Periode(forpleiningUtgiftPeriodeCore.getForpleiningUtgiftPeriodeDatoFraTil().getPeriodeDatoFra(),
              forpleiningUtgiftPeriodeCore.getForpleiningUtgiftPeriodeDatoFraTil().getPeriodeDatoTil()),
          forpleiningUtgiftPeriodeCore.getForpleiningUtgiftBelop()));
    }
    return forpleiningUtgiftPeriodeListe;
  }

  private BeregnUnderholdskostnadResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnUnderholdskostnadResultat resultat) {
    return new BeregnUnderholdskostnadResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      var underholdskostnadResultat = periodeResultat.getResultatBeregning();
      var underholdskostnadResultatGrunnlag = periodeResultat.getResultatGrunnlag();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFra(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(underholdskostnadResultat.getResultatBelopUnderholdskostnad()),
          new ResultatGrunnlagCore(underholdskostnadResultatGrunnlag.getSoknadBarnAlder(),
              underholdskostnadResultatGrunnlag.getBarnetilsynMedStonad().getBarnetilsynMedStonadTilsynType(),
              underholdskostnadResultatGrunnlag.getBarnetilsynMedStonad().getBarnetilsynMedStonadStonadType(),
              underholdskostnadResultatGrunnlag.getNettoBarnetilsynBelop(),
              underholdskostnadResultatGrunnlag.getForpleiningUtgiftBelop(),
              mapResultatGrunnlagSjabloner(underholdskostnadResultatGrunnlag.getSjablonListe()))));
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
