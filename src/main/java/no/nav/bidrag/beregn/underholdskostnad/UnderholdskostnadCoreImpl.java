package no.nav.bidrag.beregn.underholdskostnad;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.felles.FellesCore;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore;
import no.nav.bidrag.beregn.underholdskostnad.bo.BarnetilsynMedStonadPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.underholdskostnad.bo.BeregnetUnderholdskostnadResultat;
import no.nav.bidrag.beregn.underholdskostnad.bo.ForpleiningUtgiftPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.NettoBarnetilsynPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.underholdskostnad.bo.Soknadsbarn;
import no.nav.bidrag.beregn.underholdskostnad.dto.BarnetilsynMedStonadPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.BeregnetUnderholdskostnadResultatCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.ForpleiningUtgiftPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.NettoBarnetilsynPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.underholdskostnad.dto.SoknadsbarnCore;
import no.nav.bidrag.beregn.underholdskostnad.periode.UnderholdskostnadPeriode;

public class UnderholdskostnadCoreImpl extends FellesCore implements UnderholdskostnadCore {

  public UnderholdskostnadCoreImpl(UnderholdskostnadPeriode underholdskostnadPeriode) {
    this.underholdskostnadPeriode = underholdskostnadPeriode;
  }

  private final UnderholdskostnadPeriode underholdskostnadPeriode;

  public BeregnetUnderholdskostnadResultatCore beregnUnderholdskostnad(BeregnUnderholdskostnadGrunnlagCore beregnUnderholdskostnadGrunnlagCore) {
    var beregnUnderholdskostnadGrunnlag = mapTilBusinessObject(beregnUnderholdskostnadGrunnlagCore);
    var beregnUnderholdskostnadResultat = new BeregnetUnderholdskostnadResultat(Collections.emptyList());
    var avvikListe = underholdskostnadPeriode.validerInput(beregnUnderholdskostnadGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnUnderholdskostnadResultat = underholdskostnadPeriode.beregnPerioder(beregnUnderholdskostnadGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnUnderholdskostnadResultat);
  }

  private BeregnUnderholdskostnadGrunnlag mapTilBusinessObject(BeregnUnderholdskostnadGrunnlagCore beregnUnderholdskostnadGrunnlagCore) {
    var beregnDatoFra = beregnUnderholdskostnadGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnUnderholdskostnadGrunnlagCore.getBeregnDatoTil();
    var soknadsbarn = mapSoknadsbarn(beregnUnderholdskostnadGrunnlagCore.getSoknadsbarn());
    var barnetilsynMedStonadPeriodeListe = mapBarnetilsynMedStonadPeriodeListe(
        beregnUnderholdskostnadGrunnlagCore.getBarnetilsynMedStonadPeriodeListe());
    var nettoBarnetilsynPeriodeListe = mapNettoBarnetilsynPeriodeListe(beregnUnderholdskostnadGrunnlagCore.getNettoBarnetilsynPeriodeListe());
    var forpleiningUtgiftPeriodeListe = mapForpleiningUtgiftPeriodeListe(beregnUnderholdskostnadGrunnlagCore.getForpleiningUtgiftPeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnUnderholdskostnadGrunnlagCore.getSjablonPeriodeListe());
    return new BeregnUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarn, barnetilsynMedStonadPeriodeListe,
        nettoBarnetilsynPeriodeListe, forpleiningUtgiftPeriodeListe, sjablonPeriodeListe);
  }

  private Soknadsbarn mapSoknadsbarn(SoknadsbarnCore soknadsbarnCore) {
    return new Soknadsbarn(soknadsbarnCore.getReferanse(), soknadsbarnCore.getPersonId(), soknadsbarnCore.getFodselsdato());
  }

  private List<BarnetilsynMedStonadPeriode> mapBarnetilsynMedStonadPeriodeListe(
      List<BarnetilsynMedStonadPeriodeCore> barnetilsynMedStonadPeriodeListeCore) {
    var barnetilsynMedStonadPeriodeListe = new ArrayList<BarnetilsynMedStonadPeriode>();
    for (BarnetilsynMedStonadPeriodeCore barnetilsynMedStonadPeriodeCore : barnetilsynMedStonadPeriodeListeCore) {
      barnetilsynMedStonadPeriodeListe.add(new BarnetilsynMedStonadPeriode(
          barnetilsynMedStonadPeriodeCore.getReferanse(),
          new Periode(barnetilsynMedStonadPeriodeCore.getPeriode().getDatoFom(), barnetilsynMedStonadPeriodeCore.getPeriode().getDatoTil()),
          barnetilsynMedStonadPeriodeCore.getTilsynType(),
          barnetilsynMedStonadPeriodeCore.getStonadType()));
    }
    return barnetilsynMedStonadPeriodeListe;
  }

  private List<NettoBarnetilsynPeriode> mapNettoBarnetilsynPeriodeListe(List<NettoBarnetilsynPeriodeCore> nettoBarnetilsynPeriodeListeCore) {
    var nettoBarnetilsynPeriodeListe = new ArrayList<NettoBarnetilsynPeriode>();
    for (NettoBarnetilsynPeriodeCore nettoBarnetilsynPeriodeCore : nettoBarnetilsynPeriodeListeCore) {
      nettoBarnetilsynPeriodeListe.add(new NettoBarnetilsynPeriode(
          nettoBarnetilsynPeriodeCore.getReferanse(),
          new Periode(nettoBarnetilsynPeriodeCore.getPeriode().getDatoFom(), nettoBarnetilsynPeriodeCore.getPeriode().getDatoTil()),
          nettoBarnetilsynPeriodeCore.getBelop()));
    }
    return nettoBarnetilsynPeriodeListe;
  }

  private List<ForpleiningUtgiftPeriode> mapForpleiningUtgiftPeriodeListe(List<ForpleiningUtgiftPeriodeCore> forpleiningUtgiftPeriodeListeCore) {
    var forpleiningUtgiftPeriodeListe = new ArrayList<ForpleiningUtgiftPeriode>();
    for (ForpleiningUtgiftPeriodeCore forpleiningUtgiftPeriodeCore : forpleiningUtgiftPeriodeListeCore) {
      forpleiningUtgiftPeriodeListe.add(new ForpleiningUtgiftPeriode(
          forpleiningUtgiftPeriodeCore.getReferanse(),
          new Periode(forpleiningUtgiftPeriodeCore.getPeriode().getDatoFom(), forpleiningUtgiftPeriodeCore.getPeriode().getDatoTil()),
          forpleiningUtgiftPeriodeCore.getBelop()));
    }
    return forpleiningUtgiftPeriodeListe;
  }

  private BeregnetUnderholdskostnadResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnetUnderholdskostnadResultat resultat) {
    return new BeregnetUnderholdskostnadResultatCore(mapResultatPeriode(resultat.getBeregnetUnderholdskostnadPeriodeListe()),
        mapSjablonGrunnlagListe(resultat.getBeregnetUnderholdskostnadPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> resultatPeriodeListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode resultatPeriode : resultatPeriodeListe) {
      var underholdskostnadResultat = resultatPeriode.getResultat();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          resultatPeriode.getSoknadsbarnPersonId(),
          new PeriodeCore(resultatPeriode.getPeriode().getDatoFom(), resultatPeriode.getPeriode().getDatoTil()),
          new ResultatBeregningCore(underholdskostnadResultat.getBelop()),
          mapReferanseListe(resultatPeriode)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getGrunnlag();
    var sjablonListe = resultatPeriode.getResultat().getSjablonListe();

    var referanseListe = new ArrayList<String>();
    referanseListe.add(resultatGrunnlag.getSoknadsbarn().getReferanse());
    referanseListe.add(resultatGrunnlag.getBarnetilsynMedStonad().getReferanse());
    referanseListe.add(resultatGrunnlag.getNettoBarnetilsyn().getReferanse());
    referanseListe.add(resultatGrunnlag.getForpleiningUtgift().getReferanse());
    referanseListe.addAll(sjablonListe.stream().map(this::lagSjablonReferanse).distinct().collect(toList()));
    return referanseListe;
  }

  private List<SjablonResultatGrunnlagCore> mapSjablonGrunnlagListe(List<ResultatPeriode> periodeResultatListe) {
    return periodeResultatListe.stream()
        .map(resultatPeriode -> mapSjablonListe(resultatPeriode.getResultat().getSjablonListe()))
        .flatMap(Collection::stream)
        .distinct()
        .collect(toList());
  }
}
