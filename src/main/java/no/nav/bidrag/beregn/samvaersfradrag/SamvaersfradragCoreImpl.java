package no.nav.bidrag.beregn.samvaersfradrag;

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
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnetSamvaersfradragResultat;
import no.nav.bidrag.beregn.samvaersfradrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.SamvaersklassePeriode;
import no.nav.bidrag.beregn.samvaersfradrag.bo.Soknadsbarn;
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnSamvaersfradragGrunnlagCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.BeregnetSamvaersfradragResultatCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.SamvaersklassePeriodeCore;
import no.nav.bidrag.beregn.samvaersfradrag.dto.SoknadsbarnCore;
import no.nav.bidrag.beregn.samvaersfradrag.periode.SamvaersfradragPeriode;

public class SamvaersfradragCoreImpl extends FellesCore implements SamvaersfradragCore {

  private final SamvaersfradragPeriode samvaersfradragPeriode;

  public SamvaersfradragCoreImpl(SamvaersfradragPeriode samvaersfradragPeriode) {
    this.samvaersfradragPeriode = samvaersfradragPeriode;
  }

  public BeregnetSamvaersfradragResultatCore beregnSamvaersfradrag(BeregnSamvaersfradragGrunnlagCore beregnSamvaersfradragGrunnlagCore) {
    var beregnSamvaersfradragGrunnlag = mapTilBusinessObject(beregnSamvaersfradragGrunnlagCore);
    var beregnSamvaersfradragResultat = new BeregnetSamvaersfradragResultat(Collections.emptyList());
    var avvikListe = samvaersfradragPeriode.validerInput(beregnSamvaersfradragGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnSamvaersfradragResultat = samvaersfradragPeriode.beregnPerioder(beregnSamvaersfradragGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnSamvaersfradragResultat);
  }

  private BeregnSamvaersfradragGrunnlag mapTilBusinessObject(BeregnSamvaersfradragGrunnlagCore beregnSamvaersfradragGrunnlagCore) {
    var beregnDatoFra = beregnSamvaersfradragGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnSamvaersfradragGrunnlagCore.getBeregnDatoTil();
    var soknadsbarn = mapSoknadsbarn(beregnSamvaersfradragGrunnlagCore.getSoknadsbarn());
    var samvaersklassePeriodeListe = mapSamvaersklassePeriodeListe(beregnSamvaersfradragGrunnlagCore.getSamvaersklassePeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnSamvaersfradragGrunnlagCore.getSjablonPeriodeListe());
    return new BeregnSamvaersfradragGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarn, samvaersklassePeriodeListe, sjablonPeriodeListe);
  }

  private Soknadsbarn mapSoknadsbarn(SoknadsbarnCore soknadsbarnCore) {
    return new Soknadsbarn(soknadsbarnCore.getReferanse(), soknadsbarnCore.getPersonId(), soknadsbarnCore.getFodselsdato());
  }

  private List<SamvaersklassePeriode> mapSamvaersklassePeriodeListe(List<SamvaersklassePeriodeCore> samvaersklassePeriodeListeCore) {
    var samvaersklassePeriodeListe = new ArrayList<SamvaersklassePeriode>();
    for (SamvaersklassePeriodeCore samvaersklassePeriodeCore : samvaersklassePeriodeListeCore) {
      samvaersklassePeriodeListe.add(new SamvaersklassePeriode(
          samvaersklassePeriodeCore.getReferanse(),
          new Periode(samvaersklassePeriodeCore.getPeriode().getDatoFom(), samvaersklassePeriodeCore.getPeriode().getDatoTil()),
          samvaersklassePeriodeCore.getSamvaersklasse()));
    }
    return samvaersklassePeriodeListe;
  }

  private BeregnetSamvaersfradragResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnetSamvaersfradragResultat resultat) {
    return new BeregnetSamvaersfradragResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()),
        mapSjablonGrunnlagListe(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> resultatPeriodeListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode resultatPeriode : resultatPeriodeListe) {
      var samvaersfradragResultat = resultatPeriode.getResultat();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          resultatPeriode.getSoknadsbarnPersonId(),
          new PeriodeCore(resultatPeriode.getPeriode().getDatoFom(), resultatPeriode.getPeriode().getDatoTil()),
          new ResultatBeregningCore(samvaersfradragResultat.getBelop()),
          mapReferanseListe(resultatPeriode)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getGrunnlag();
    var sjablonListe = resultatPeriode.getResultat().getSjablonListe();

    var referanseListe = new ArrayList<String>();
    referanseListe.add(resultatGrunnlag.getSoknadsbarn().getReferanse());
    referanseListe.add(resultatGrunnlag.getSamvaersklasse().getReferanse());
    referanseListe.addAll(sjablonListe.stream().map(this::lagSjablonReferanse).distinct().collect(toList()));
    return referanseListe;
  }

  private List<SjablonResultatGrunnlagCore> mapSjablonGrunnlagListe(List<ResultatPeriode> resultatPeriodeListe) {
    return resultatPeriodeListe.stream()
        .map(resultatPeriode -> mapSjablonListe(resultatPeriode.getResultat().getSjablonListe()))
        .flatMap(Collection::stream)
        .distinct()
        .collect(toList());
  }
}
