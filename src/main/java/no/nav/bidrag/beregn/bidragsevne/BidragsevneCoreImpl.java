package no.nav.bidrag.beregn.bidragsevne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.bidragsevne.bo.AntallBarnIEgetHusholdPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneResultat;
import no.nav.bidrag.beregn.bidragsevne.bo.BostatusPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.bidragsevne.bo.InntektPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.SaerfradragPeriode;
import no.nav.bidrag.beregn.bidragsevne.bo.SkatteklassePeriode;
import no.nav.bidrag.beregn.bidragsevne.dto.AntallBarnIEgetHusholdPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneGrunnlagCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BeregnBidragsevneResultatCore;
import no.nav.bidrag.beregn.bidragsevne.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.InntektCore;
import no.nav.bidrag.beregn.bidragsevne.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.bidragsevne.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.bidragsevne.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.SaerfradragPeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.dto.SkatteklassePeriodeCore;
import no.nav.bidrag.beregn.bidragsevne.periode.BidragsevnePeriode;
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
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;

public class BidragsevneCoreImpl implements BidragsevneCore {

  public BidragsevneCoreImpl(BidragsevnePeriode bidragsevnePeriode) {
    this.bidragsevnePeriode = bidragsevnePeriode;
  }

  private final BidragsevnePeriode bidragsevnePeriode;

  public BeregnBidragsevneResultatCore beregnBidragsevne(
      BeregnBidragsevneGrunnlagCore beregnBidragsevneGrunnlagCore) {
    var beregnBidragsevneGrunnlag = mapTilBusinessObject(beregnBidragsevneGrunnlagCore);
    var beregnBidragsevneResultat = new BeregnBidragsevneResultat(Collections.emptyList());
    var avvikListe = bidragsevnePeriode.validerInput(beregnBidragsevneGrunnlag);
    if (avvikListe.isEmpty()) {
      beregnBidragsevneResultat = bidragsevnePeriode.beregnPerioder(beregnBidragsevneGrunnlag);
    }
    return mapFraBusinessObject(avvikListe, beregnBidragsevneResultat);
  }

  private BeregnBidragsevneGrunnlag mapTilBusinessObject(
      BeregnBidragsevneGrunnlagCore beregnBidragsevneGrunnlagCore) {
    var beregnDatoFra = beregnBidragsevneGrunnlagCore.getBeregnDatoFra();
    var beregnDatoTil = beregnBidragsevneGrunnlagCore.getBeregnDatoTil();
    var inntektPeriodeListe = mapInntektPeriodeListe(beregnBidragsevneGrunnlagCore.getInntektPeriodeListe());
    var skatteklassePeriodeListe = mapSkatteklassePeriodeListe(beregnBidragsevneGrunnlagCore.getSkatteklassePeriodeListe());
    var bostatusPeriodeListe = mapBostatusPeriodeListe(beregnBidragsevneGrunnlagCore.getBostatusPeriodeListe());
    var antallBarnIEgetHusholdPeriodeListe = mapAntallBarnIEgetHusholdPeriodeListe(
        beregnBidragsevneGrunnlagCore.getAntallBarnIEgetHusholdPeriodeListe());
    var saerfradragPeriodeListe = mapSaerfradragPeriodeListe(beregnBidragsevneGrunnlagCore.getSaerfradragPeriodeListe());
    var sjablonPeriodeListe = mapSjablonPeriodeListe(beregnBidragsevneGrunnlagCore.getSjablonPeriodeListe());
    return new BeregnBidragsevneGrunnlag(beregnDatoFra, beregnDatoTil, inntektPeriodeListe, skatteklassePeriodeListe,
        bostatusPeriodeListe, antallBarnIEgetHusholdPeriodeListe, saerfradragPeriodeListe, sjablonPeriodeListe);
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

  private List<InntektPeriode> mapInntektPeriodeListe(List<InntektPeriodeCore> inntektPeriodeListeCore) {
    var inntektPeriodeListe = new ArrayList<InntektPeriode>();
    for (InntektPeriodeCore inntektPeriodeCore : inntektPeriodeListeCore) {
      inntektPeriodeListe.add(new InntektPeriode(
          new Periode(inntektPeriodeCore.getInntektPeriodeDatoFraTil().getPeriodeDatoFra(),
              inntektPeriodeCore.getInntektPeriodeDatoFraTil().getPeriodeDatoTil()),
          InntektType.valueOf(inntektPeriodeCore.getInntektType()),
          inntektPeriodeCore.getInntektBelop()));
    }
    return inntektPeriodeListe;
  }

  private List<SkatteklassePeriode> mapSkatteklassePeriodeListe(List<SkatteklassePeriodeCore> skatteklassePeriodeListeCore) {
    var skatteklassePeriodeListe = new ArrayList<SkatteklassePeriode>();
    for (SkatteklassePeriodeCore skatteklassePeriodeCore : skatteklassePeriodeListeCore) {
      skatteklassePeriodeListe.add(new SkatteklassePeriode(
          new Periode(skatteklassePeriodeCore.getSkatteklassePeriodeDatoFraTil().getPeriodeDatoFra(),
              skatteklassePeriodeCore.getSkatteklassePeriodeDatoFraTil().getPeriodeDatoTil()),
          skatteklassePeriodeCore.getSkatteklasse()));
    }
    return skatteklassePeriodeListe;
  }

  private List<BostatusPeriode> mapBostatusPeriodeListe(List<BostatusPeriodeCore> bostatusPeriodeListeCore) {
    var bostatusPeriodeListe = new ArrayList<BostatusPeriode>();
    for (BostatusPeriodeCore bostatusPeriodeCore : bostatusPeriodeListeCore) {
      bostatusPeriodeListe.add(new BostatusPeriode(
          new Periode(bostatusPeriodeCore.getBostatusPeriodeDatoFraTil().getPeriodeDatoFra(),
              bostatusPeriodeCore.getBostatusPeriodeDatoFraTil().getPeriodeDatoTil()),
          BostatusKode.valueOf(bostatusPeriodeCore.getBostatusKode())));
    }
    return bostatusPeriodeListe;
  }

  private List<AntallBarnIEgetHusholdPeriode> mapAntallBarnIEgetHusholdPeriodeListe(
      List<AntallBarnIEgetHusholdPeriodeCore> antallBarnIEgetHusholdPeriodeListeCore) {
    var antallBarnIEgetHusholdPeriodeListe = new ArrayList<AntallBarnIEgetHusholdPeriode>();
    for (AntallBarnIEgetHusholdPeriodeCore antallBarnIEgetHusholdPeriodeCore : antallBarnIEgetHusholdPeriodeListeCore) {
      antallBarnIEgetHusholdPeriodeListe.add(new AntallBarnIEgetHusholdPeriode(
          new Periode(antallBarnIEgetHusholdPeriodeCore.getAntallBarnIEgetHusholdPeriodeDatoFraTil()
              .getPeriodeDatoFra(),
              antallBarnIEgetHusholdPeriodeCore.getAntallBarnIEgetHusholdPeriodeDatoFraTil()
                  .getPeriodeDatoTil()),
          antallBarnIEgetHusholdPeriodeCore.getAntallBarn()));
    }
    return antallBarnIEgetHusholdPeriodeListe;
  }

  private List<SaerfradragPeriode> mapSaerfradragPeriodeListe(List<SaerfradragPeriodeCore> saerfradragPeriodeListeCore) {
    var saerfradragPeriodeListe = new ArrayList<SaerfradragPeriode>();
    for (SaerfradragPeriodeCore saerfradragPeriodeCore : saerfradragPeriodeListeCore) {
      saerfradragPeriodeListe.add(new SaerfradragPeriode(
          new Periode(saerfradragPeriodeCore.getSaerfradragPeriodeDatoFraTil().getPeriodeDatoFra(),
              saerfradragPeriodeCore.getSaerfradragPeriodeDatoFraTil().getPeriodeDatoTil()),
          SaerfradragKode.valueOf(saerfradragPeriodeCore.getSaerfradragKode())));
    }
    return saerfradragPeriodeListe;
  }

  private BeregnBidragsevneResultatCore mapFraBusinessObject(List<Avvik> avvikListe, BeregnBidragsevneResultat resultat) {
    return new BeregnBidragsevneResultatCore(mapResultatPeriode(resultat.getResultatPeriodeListe()), mapAvvik(avvikListe));
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
      var bidragsevneResultat = periodeResultat.getResultatBeregning();
      var bidragsevneResultatGrunnlag = periodeResultat.getResultatGrunnlagBeregning();
      resultatPeriodeCoreListe.add(new ResultatPeriodeCore(
          new PeriodeCore(periodeResultat.getResultatDatoFraTil().getDatoFra(), periodeResultat.getResultatDatoFraTil().getDatoTil()),
          new ResultatBeregningCore(bidragsevneResultat.getResultatEvneBelop(), bidragsevneResultat.getResultat25ProsentInntekt()),
          new ResultatGrunnlagCore(mapResultatGrunnlagInntekt(bidragsevneResultatGrunnlag.getInntektListe()),
              bidragsevneResultatGrunnlag.getSkatteklasse(),
              bidragsevneResultatGrunnlag.getBostatusKode().toString(),
              bidragsevneResultatGrunnlag.getAntallEgneBarnIHusstand(),
              bidragsevneResultatGrunnlag.getSaerfradragkode().toString(),
              mapResultatGrunnlagSjabloner(bidragsevneResultat.getSjablonListe()))));
    }
    return resultatPeriodeCoreListe;
  }

  private List<InntektCore> mapResultatGrunnlagInntekt(List<Inntekt> resultatGrunnlagInntektListe) {
    var resultatGrunnlagInntektListeCore = new ArrayList<InntektCore>();
    for (Inntekt resultatGrunnlagInntekt : resultatGrunnlagInntektListe) {
      resultatGrunnlagInntektListeCore
          .add(new InntektCore(resultatGrunnlagInntekt.getInntektType().toString(), resultatGrunnlagInntekt.getInntektBelop()));
    }
    return resultatGrunnlagInntektListeCore;
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
