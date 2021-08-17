package no.nav.bidrag.beregn.bidragsevne;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstandPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnetBidragsevneResultat;
import no.nav.bidrag.beregn.bidragsevne.bo.BostatusPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.InntektPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.SaerfradragPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.SkatteklassePeriode;
import no.nav.bidrag.beregn.bidragsevne.dto.BarnIHusstandPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneGrunnlagCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnetBidragsevneResultatCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.bidragsevne.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.SaerfradragPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.SkatteklassePeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode;
import no.nav.bidrag.beregn.felles.FellesCore;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;

public class BidragsevneCoreImpl extends FellesCore implements BidragsevneCore {

  private final BidragsevnePeriode bidragsevnePeriode;

  public BidragsevneCoreImpl(BidragsevnePeriode bidragsevnePeriode) {
    this.bidragsevnePeriode = bidragsevnePeriode;
  }

  public BeregnetBidragsevneResultatCore beregnBidragsevne(BeregnBidragsevneGrunnlagCore beregnBidragsevneGrunnlagCore) {
    var beregnBidragsevneGrunnlag = mapTilBusinessObject(beregnBidragsevneGrunnlagCore);
    var beregnBidragsevneResultat = new BeregnetBidragsevneResultat(Collections.emptyList());
    var avvikListe = bidragsevnePeriode.validerInput(beregnBidragsevneGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnBidragsevneResultat = bidragsevnePeriode.beregnPerioder(beregnBidragsevneGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnBidragsevneResultat);
  }

  private BeregnBidragsevneGrunnlag mapTilBusinessObject(BeregnBidragsevneGrunnlagCore beregnBidragsevneGrunnlagCore) {
    var beregnDatoFra = beregnBidragsevneGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnBidragsevneGrunnlagCore.getBeregnDatoTil();
    var inntektPeriodeListe = mapInntektPeriodeListe(beregnBidragsevneGrunnlagCore.getInntektPeriodeListe());
    var skatteklassePeriodeListe = mapSkatteklassePeriodeListe(beregnBidragsevneGrunnlagCore.getSkatteklassePeriodeListe());
    var bostatusPeriodeListe = mapBostatusPeriodeListe(beregnBidragsevneGrunnlagCore.getBostatusPeriodeListe());
    var barnIHusstandPeriodeListe = mapBarnIHusstandPeriodeListe(beregnBidragsevneGrunnlagCore.getBarnIHusstandPeriodeListe());
    var saerfradragPeriodeListe = mapSaerfradragPeriodeListe(beregnBidragsevneGrunnlagCore.getSaerfradragPeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBidragsevneGrunnlagCore.getSjablonPeriodeListe());
    return new BeregnBidragsevneGrunnlag(beregnDatoFra, beregnDatoTil, inntektPeriodeListe, skatteklassePeriodeListe, bostatusPeriodeListe,
        barnIHusstandPeriodeListe, saerfradragPeriodeListe, sjablonPeriodeListe);
  }

  private List<InntektPeriode> mapInntektPeriodeListe(List<InntektPeriodeCore> inntektPeriodeListeCore) {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore inntektPeriodeCore : inntektPeriodeListeCore) {
      inntektPeriodeListe.add(new InntektPeriode(
          inntektPeriodeCore.getReferanse(),
          new Periode(inntektPeriodeCore.getPeriode().getDatoFom(), inntektPeriodeCore.getPeriode().getDatoTil()),
          InntektType.valueOf(inntektPeriodeCore.getType()),
          inntektPeriodeCore.getBelop()));
    }
    return inntektPeriodeListe;
  }

  private List<SkatteklassePeriode> mapSkatteklassePeriodeListe(List<SkatteklassePeriodeCore> skatteklassePeriodeListeCore) {
    var skatteklassePeriodeListe = new ArrayList<SkatteklassePeriode>();
    for (SkatteklassePeriodeCore skatteklassePeriodeCore : skatteklassePeriodeListeCore) {
      skatteklassePeriodeListe.add(new SkatteklassePeriode(
          skatteklassePeriodeCore.getReferanse(),
          new Periode(skatteklassePeriodeCore.getPeriode().getDatoFom(), skatteklassePeriodeCore.getPeriode().getDatoTil()),
          skatteklassePeriodeCore.getSkatteklasse()));
    }
    return skatteklassePeriodeListe;
  }

  private List<BostatusPeriode> mapBostatusPeriodeListe(List<BostatusPeriodeCore> bostatusPeriodeListeCore) {
    var bostatusPeriodeListe = new ArrayList<BostatusPeriode>();
    for (BostatusPeriodeCore bostatusPeriodeCore : bostatusPeriodeListeCore) {
      bostatusPeriodeListe.add(new BostatusPeriode(
          bostatusPeriodeCore.getReferanse(),
          new Periode(bostatusPeriodeCore.getPeriode().getDatoFom(), bostatusPeriodeCore.getPeriode().getDatoTil()),
          BostatusKode.valueOf(bostatusPeriodeCore.getKode())));
    }
    return bostatusPeriodeListe;
  }

  private List<BarnIHusstandPeriode> mapBarnIHusstandPeriodeListe(
      List<BarnIHusstandPeriodeCore> barnIHusstandPeriodeListeCore) {
    var barnIHusstandPeriodeListe = new ArrayList<BarnIHusstandPeriode>();
    for (BarnIHusstandPeriodeCore barnIHusstandPeriodeCore : barnIHusstandPeriodeListeCore) {
      barnIHusstandPeriodeListe.add(new BarnIHusstandPeriode(
          barnIHusstandPeriodeCore.getReferanse(),
          new Periode(barnIHusstandPeriodeCore.getPeriode().getDatoFom(), barnIHusstandPeriodeCore.getPeriode().getDatoTil()),
          barnIHusstandPeriodeCore.getAntallBarn()));
    }
    return barnIHusstandPeriodeListe;
  }

  private List<SaerfradragPeriode> mapSaerfradragPeriodeListe(List<SaerfradragPeriodeCore> saerfradragPeriodeListeCore) {
    var saerfradragPeriodeListe = new ArrayList<SaerfradragPeriode>();
    for (SaerfradragPeriodeCore saerfradragPeriodeCore : saerfradragPeriodeListeCore) {
      saerfradragPeriodeListe.add(new SaerfradragPeriode(
          saerfradragPeriodeCore.getReferanse(),
          new Periode(saerfradragPeriodeCore.getPeriode().getDatoFom(), saerfradragPeriodeCore.getPeriode().getDatoTil()),
          SaerfradragKode.valueOf(saerfradragPeriodeCore.getKode())));
    }
    return saerfradragPeriodeListe;
  }

  private BeregnetBidragsevneResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnetBidragsevneResultat resultat) {
    return new BeregnetBidragsevneResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()),
        mapSjablonGrunnlagListe(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> resultatPeriodeListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode resultatPeriode : resultatPeriodeListe) {
      var bidragsevneBeregningResultat = resultatPeriode.getResultat();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(resultatPeriode.getPeriode().getDatoFom(), resultatPeriode.getPeriode().getDatoTil()),
          new ResultatBeregningCore(bidragsevneBeregningResultat.getBelop(), bidragsevneBeregningResultat.getInntekt25Prosent()),
          mapReferanseListe(resultatPeriode)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getGrunnlag();
    var sjablonListe = resultatPeriode.getResultat().getSjablonListe();

    var referanseListe = new ArrayList<String>();
    resultatGrunnlag.getInntektListe().forEach(inntekt -> referanseListe.add(inntekt.getReferanse()));
    referanseListe.add(resultatGrunnlag.getSkatteklasse().getReferanse());
    referanseListe.add(resultatGrunnlag.getBostatus().getReferanse());
    referanseListe.add(resultatGrunnlag.getBarnIHusstand().getReferanse());
    referanseListe.add(resultatGrunnlag.getSaerfradrag().getReferanse());
    referanseListe.addAll(sjablonListe.stream().map(this::lagSjablonReferanse).distinct().collect(toList()));
    return referanseListe.stream().sorted().toList();
  }

  private List<SjablonResultatGrunnlagCore> mapSjablonGrunnlagListe(List<ResultatPeriode> resultatPeriodeListe) {
    return resultatPeriodeListe.stream()
        .map(resultatPeriode -> mapSjablonListe(resultatPeriode.getResultat().getSjablonListe()))
        .flatMap(Collection::stream)
        .distinct()
        .collect(toList());
  }
}
