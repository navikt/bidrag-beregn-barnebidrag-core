package no.nav.bidrag.beregn.bpsandelunderholdskostnad;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnetBPsAndelUnderholdskostnadResultat;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.InntektPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnBPsAndelUnderholdskostnadGrunnlagCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.BeregnetBPsAndelUnderholdskostnadResultatCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.dto.UnderholdskostnadPeriodeCore;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.periode.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.felles.FellesCore;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonResultatGrunnlagCore;
import no.nav.bidrag.beregn.felles.enums.InntektType;

public class BPsAndelUnderholdskostnadCoreImpl extends FellesCore implements BPsAndelUnderholdskostnadCore {

  private final BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriode;

  public BPsAndelUnderholdskostnadCoreImpl(BPsAndelUnderholdskostnadPeriode bPsAndelunderholdskostnadPeriode) {
    this.bPsAndelunderholdskostnadPeriode = bPsAndelunderholdskostnadPeriode;
  }

  public BeregnetBPsAndelUnderholdskostnadResultatCore beregnBPsAndelUnderholdskostnad(
      BeregnBPsAndelUnderholdskostnadGrunnlagCore beregnBPsAndelUnderholdskostnadGrunnlagCore) {

    var beregnBPsAndelUnderholdskostnadGrunnlag = mapTilBusinessObject(beregnBPsAndelUnderholdskostnadGrunnlagCore);
    var beregnBPsAndelUnderholdskostnadResultat = new BeregnetBPsAndelUnderholdskostnadResultat(Collections.emptyList());
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
    var underholdskostnadPeriodeListe = mapUnderholdskostnadPeriodeListe(
        beregnBPsAndelUnderholdskostnadGrunnlagCore.getUnderholdskostnadPeriodeListe());
    var inntektBPPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore.getInntektBPPeriodeListe());
    var inntektBMPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore.getInntektBMPeriodeListe());
    var inntektBBPeriodeListe = mapInntektPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore.getInntektBBPeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBPsAndelUnderholdskostnadGrunnlagCore.getSjablonPeriodeListe());

    return new BeregnBPsAndelUnderholdskostnadGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId, underholdskostnadPeriodeListe,
        inntektBPPeriodeListe, inntektBMPeriodeListe, inntektBBPeriodeListe, sjablonPeriodeListe);
  }

  private List<InntektPeriode> mapInntektPeriodeListe(List<InntektPeriodeCore> inntektPeriodeListeCore) {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore inntektPeriodeCore : inntektPeriodeListeCore) {
      inntektPeriodeListe.add(new InntektPeriode(
          inntektPeriodeCore.getReferanse(),
          new Periode(inntektPeriodeCore.getPeriode().getDatoFom(), inntektPeriodeCore.getPeriode().getDatoTil()),
          InntektType.valueOf(inntektPeriodeCore.getType()),
          inntektPeriodeCore.getBelop(),
          inntektPeriodeCore.getDeltFordel(),
          inntektPeriodeCore.getSkatteklasse2()));
    }
    return inntektPeriodeListe;
  }

  private List<UnderholdskostnadPeriode> mapUnderholdskostnadPeriodeListe(List<UnderholdskostnadPeriodeCore> underholdskostnadPeriodeListeCore) {
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    for (UnderholdskostnadPeriodeCore underholdskostnadPeriodeCore : underholdskostnadPeriodeListeCore) {
      underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(
          underholdskostnadPeriodeCore.getReferanse(),
          new Periode(underholdskostnadPeriodeCore.getPeriode().getDatoFom(), underholdskostnadPeriodeCore.getPeriode().getDatoTil()),
          underholdskostnadPeriodeCore.getBelop()));
    }
    return underholdskostnadPeriodeListe;
  }

  private BeregnetBPsAndelUnderholdskostnadResultatCore mapFraBusinessObject(
      List<Avvik> avvikListe, BeregnetBPsAndelUnderholdskostnadResultat resultat) {
    return new BeregnetBPsAndelUnderholdskostnadResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()),
        mapSjablonGrunnlagListe(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
  }

  private List<ResultatPeriodeCore> mapResultatPeriode(List<ResultatPeriode> resultatPeriodeListe) {
    var resultatPeriodeCoreListe = new ArrayList<ResultatPeriodeCore>();
    for (ResultatPeriode resultatPeriode : resultatPeriodeListe) {
      var bPsAndelunderholdskostnadResultat = resultatPeriode.getResultat();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          resultatPeriode.getSoknadsbarnPersonId(),
          new PeriodeCore(resultatPeriode.getPeriode().getDatoFom(), resultatPeriode.getPeriode().getDatoTil()),
          new ResultatBeregningCore(bPsAndelunderholdskostnadResultat.getAndelProsent(),
              bPsAndelunderholdskostnadResultat.getAndelBelop(),
              bPsAndelunderholdskostnadResultat.getBarnetErSelvforsorget()),
          mapReferanseListe(resultatPeriode)));
    }
    return resultatPeriodeCoreListe;
  }

  private List<String> mapReferanseListe(ResultatPeriode resultatPeriode) {
    var resultatGrunnlag = resultatPeriode.getGrunnlag();
    var sjablonListe = resultatPeriode.getResultat().getSjablonListe();

    var referanseListe = new ArrayList<String>();
    referanseListe.add(resultatGrunnlag.getUnderholdskostnad().getReferanse());
    resultatGrunnlag.getInntektBPListe().forEach(inntekt -> referanseListe.add(inntekt.getReferanse()));
    resultatGrunnlag.getInntektBMListe().forEach(inntekt -> referanseListe.add(inntekt.getReferanse()));
    resultatGrunnlag.getInntektBBListe().forEach(inntekt -> referanseListe.add(inntekt.getReferanse()));
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
