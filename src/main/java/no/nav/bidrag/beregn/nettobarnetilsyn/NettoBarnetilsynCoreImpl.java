package no.nav.bidrag.beregn.nettobarnetilsyn;

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
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynResultat;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.NettoBarnetilsynPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynGrunnlagCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynResultatCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.NettoBarnetilsynPeriodeCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.ResultatPeriodeCore;

public class NettoBarnetilsynCoreImpl implements NettoBarnetilsynCore {

  public NettoBarnetilsynCoreImpl(NettoBarnetilsynPeriode NettoBarnetilsynPeriode) {
    this.NettoBarnetilsynPeriode = NettoBarnetilsynPeriode;
  }

  private NettoBarnetilsynPeriode NettoBarnetilsynPeriode;

  public BeregnNettoBarnetilsynResultatCore beregnNettoBarnetilsyn(BeregnNettoBarnetilsynGrunnlagCore beregnNettoBarnetilsynGrunnlagCore) {
    var beregnNettoBarnetilsynGrunnlag = mapTilBusinessObject(beregnNettoBarnetilsynGrunnlagCore);
    var beregnNettoBarnetilsynResultat = new BeregnNettoBarnetilsynResultat(Collections.emptyList());
    var avvikListe = NettoBarnetilsynPeriode.validerInput(beregnNettoBarnetilsynGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnNettoBarnetilsynResultat = NettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnNettoBarnetilsynResultat);
  }

  private BeregnNettoBarnetilsynGrunnlag mapTilBusinessObject(BeregnNettoBarnetilsynGrunnlagCore beregnNettoBarnetilsynGrunnlagCore) {
    var beregnDatoFra = beregnNettoBarnetilsynGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnNettoBarnetilsynGrunnlagCore.getBeregnDatoTil();
    var nettoBarnetilsynPeriodeListe = mapNettoBarnetilsynPeriodeListe(beregnNettoBarnetilsynGrunnlagCore.getNettoBarnetilsynPeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnNettoBarnetilsynGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, nettoBarnetilsynPeriodeListe, sjablonPeriodeListe);
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

  private List<NettoBarnetilsynPeriode> mapNettoBarnetilsynPeriodeListe(List<NettoBarnetilsynPeriodeCore> nettoBarnetilsynPeriodeListeCore) {
    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriode>();
    for (NettoBarnetilsynPeriodeCore nettoBarnetilsynPeriodeCore : nettoBarnetilsynPeriodeListeCore) {
      nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
          new Periode(nettoBarnetilsynPeriodeCore.getNettoBarnetilsynPeriodeDatoFraTil().getPeriodeDatoFra(),
              nettoBarnetilsynPeriodeCore.getNettoBarnetilsynPeriodeDatoFraTil().getPeriodeDatoTil()),
          nettoBarnetilsynPeriodeCore.getNettoBarnetilsynSoknadsbarnFodselsdato(),
          nettoBarnetilsynPeriodeCore.getNettoBarnetilsynBelop()));
    }
    return nettoBarnetilsynPeriodeListe;
  }


  private BeregnNettoBarnetilsynResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnNettoBarnetilsynResultat resultat) {
    return new BeregnNettoBarnetilsynResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      var NettoBarnetilsynResultat = periodeResultat.getResultatBeregning();
      var NettoBarnetilsynResultatGrunnlag = periodeResultat.getResultatGrunnlag();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFra(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(NettoBarnetilsynResultat.getResultatNettoBarnetilsynBelop()),
          new ResultatGrunnlagCore(NettoBarnetilsynResultatGrunnlag.getSoknadBarnAlder(),
              NettoBarnetilsynResultatGrunnlag.getNettoBarnetilsynBelop(),
              mapResultatGrunnlagSjabloner(NettoBarnetilsynResultatGrunnlag.getSjablonListe()))));
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
