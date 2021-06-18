package no.nav.bidrag.beregn.nettobarnetilsyn;

import static java.util.stream.Collectors.toList;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnetNettoBarnetilsynResultat;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgiftPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.ResultatPeriode;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnNettoBarnetilsynGrunnlagCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.BeregnetNettoBarnetilsynResultatCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.FaktiskUtgiftPeriodeCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.nettobarnetilsyn.periode.NettoBarnetilsynPeriode;

public class NettoBarnetilsynCoreImpl implements NettoBarnetilsynCore {

  public NettoBarnetilsynCoreImpl(NettoBarnetilsynPeriode nettoBarnetilsynPeriode) {
    this.nettoBarnetilsynPeriode = nettoBarnetilsynPeriode;
  }

  private final NettoBarnetilsynPeriode nettoBarnetilsynPeriode;

  public BeregnetNettoBarnetilsynResultatCore beregnNettoBarnetilsyn(BeregnNettoBarnetilsynGrunnlagCore beregnNettoBarnetilsynGrunnlagCore) {
    var beregnNettoBarnetilsynGrunnlag = mapTilBusinessObject(beregnNettoBarnetilsynGrunnlagCore);
    var beregnNettoBarnetilsynResultat = new BeregnetNettoBarnetilsynResultat(Collections.emptyList());
    var avvikListe = nettoBarnetilsynPeriode.validerInput(beregnNettoBarnetilsynGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnNettoBarnetilsynResultat = nettoBarnetilsynPeriode.beregnPerioder(beregnNettoBarnetilsynGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnNettoBarnetilsynResultat);
  }

  private BeregnNettoBarnetilsynGrunnlag mapTilBusinessObject(BeregnNettoBarnetilsynGrunnlagCore beregnNettoBarnetilsynGrunnlagCore) {
    var beregnDatoFra = beregnNettoBarnetilsynGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnNettoBarnetilsynGrunnlagCore.getBeregnDatoTil();
    var nettoBarnetilsynPeriodeListe = mapFaktiskUtgiftPeriodeListe(beregnNettoBarnetilsynGrunnlagCore.getFaktiskUtgiftPeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnNettoBarnetilsynGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnNettoBarnetilsynGrunnlag(beregnDatoFra, beregnDatoTil, nettoBarnetilsynPeriodeListe, sjablonPeriodeListe);
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

  private List<FaktiskUtgiftPeriode> mapFaktiskUtgiftPeriodeListe(List<FaktiskUtgiftPeriodeCore> faktiskUtgiftPeriodeListeCore) {
    var faktiskUtgiftPeriodeListe = new ArrayList<FaktiskUtgiftPeriode>();
    for (FaktiskUtgiftPeriodeCore faktiskUtgiftPeriodeCore : faktiskUtgiftPeriodeListeCore) {
      faktiskUtgiftPeriodeListe.add(new FaktiskUtgiftPeriode(
          faktiskUtgiftPeriodeCore.getSoknadsbarnPersonId(),
          faktiskUtgiftPeriodeCore.getReferanse(),
          new Periode(faktiskUtgiftPeriodeCore.getPeriode().getDatoFom(), faktiskUtgiftPeriodeCore.getPeriode().getDatoTil()),
          faktiskUtgiftPeriodeCore.getSoknadsbarnFodselsdato(),
          faktiskUtgiftPeriodeCore.getBelop()));
    }
    return faktiskUtgiftPeriodeListe;
  }

  private BeregnetNettoBarnetilsynResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnetNettoBarnetilsynResultat resultat) {
    return new BeregnetNettoBarnetilsynResultatCore(mapResultatPeriode(resultat.getBeregnetNettoBarnetilsynPeriodeListe()),
        mapSjablonGrunnlagListe(resultat.getBeregnetNettoBarnetilsynPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> resultatPeriodeListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode resultatPeriode : resultatPeriodeListe) {
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(resultatPeriode.getPeriode().getDatoFom(), resultatPeriode.getPeriode().getDatoTil()),
          mapResultatBeregning(resultatPeriode.getResultatListe()),
          mapReferanseListe(resultatPeriode)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<ResultatBeregningCore> mapResultatBeregning(List<ResultatBeregning> resultatBeregningListe) {
    var resultatBeregningListeCore = new ArrayList<ResultatBeregningCore>();
    for (ResultatBeregning resultatBeregning : resultatBeregningListe) {
      resultatBeregningListeCore.add(new ResultatBeregningCore(resultatBeregning.getSoknadsbarnPersonId(), resultatBeregning.getBelop()));
    }
    return resultatBeregningListeCore;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getGrunnlag();
    var sjablonListe = resultatPeriode.getResultatListe().stream()
        .map(ResultatBeregning::getSjablonListe)
        .flatMap(Collection::stream)
        .collect(toList());

    var referanseListe = new ArrayList<String>();
    resultatGrunnlag.getFaktiskUtgiftListe().forEach(faktiskUtgift -> referanseListe.add(faktiskUtgift.getReferanse()));
    referanseListe.addAll(sjablonListe.stream().map(this::lagSjablonReferanse).distinct().collect(toList()));
    return referanseListe;
  }

  private List<SjablonResultatGrunnlagCore> mapSjablonGrunnlagListe(List<ResultatPeriode> periodeResultatListe) {
    return periodeResultatListe.stream()
        .map(ResultatPeriode::getResultatListe)
        .flatMap(Collection::stream)
        .map(resultat -> mapSjablonListe(resultat.getSjablonListe()))
        .flatMap(Collection::stream)
        .distinct()
        .collect(toList());
  }

  private List<SjablonResultatGrunnlagCore> mapSjablonListe(List<SjablonPeriodeNavnVerdi> sjablonListe) {
    return sjablonListe.stream()
        .map(sjablon -> new SjablonResultatGrunnlagCore(lagSjablonReferanse(sjablon),
            new PeriodeCore(sjablon.getPeriode().getDatoFom(), sjablon.getPeriode().getDatoTil()),
            sjablon.getNavn(), sjablon.getVerdi()))
        .collect(toList());
  }

  private String lagSjablonReferanse(SjablonPeriodeNavnVerdi sjablon) {
    return "Sjablon_" + sjablon.getNavn() + "_" + sjablon.getPeriode().getDatoFom().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  }


  private List<AvvikCore> mapAvvik(List<Avvik> avvikListe) {
    var avvikCoreListe = new ArrayList<AvvikCore>();
    for (Avvik avvik : avvikListe) {
      avvikCoreListe.add(new AvvikCore(avvik.getAvvikTekst(), avvik.getAvvikType().toString()));
    }
    return avvikCoreListe;
  }
}
