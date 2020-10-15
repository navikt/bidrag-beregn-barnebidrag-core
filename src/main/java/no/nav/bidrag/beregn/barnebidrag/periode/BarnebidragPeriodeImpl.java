package no.nav.bidrag.beregn.barnebidrag.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaretPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne;
import no.nav.bidrag.beregn.barnebidrag.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBostedPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;


public class BarnebidragPeriodeImpl implements BarnebidragPeriode {
  public BarnebidragPeriodeImpl(
      BarnebidragBeregning barnebidragBeregning) {
    this.barnebidragBeregning = barnebidragBeregning;
  }

  private BarnebidragBeregning barnebidragBeregning;

  public BeregnBarnebidragResultat beregnPerioder(
      BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    var justertBidragsevnePeriodeListe = beregnBarnebidragGrunnlag.getBidragsevnePeriodeListe()
        .stream()
        .map(BidragsevnePeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBPsAndelUnderholdskostnadPeriodeListe = beregnBarnebidragGrunnlag.getBPsAndelUnderholdskostnadPeriodeListe()
        .stream()
        .map(BPsAndelUnderholdskostnadPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertDeltBostedPeriodeListe = beregnBarnebidragGrunnlag.getDeltBostedPeriodeListe()
        .stream()
        .map(DeltBostedPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSamvaersfradragPeriodeListe = beregnBarnebidragGrunnlag.getSamvaersfradragPeriodeListe()
        .stream()
        .map(SamvaersfradragPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBarnetilleggBPPeriodeListe = beregnBarnebidragGrunnlag.getBarnetilleggBPPeriodeListe()
        .stream()
        .map(BarnetilleggPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBarnetilleggBMPeriodeListe = beregnBarnebidragGrunnlag.getBarnetilleggBMPeriodeListe()
        .stream()
        .map(BarnetilleggPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBarnetilleggForsvaretPeriodeListe = beregnBarnebidragGrunnlag.getBarnetilleggForsvaretPeriodeListe()
        .stream()
        .map(BarnetilleggForsvaretPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnBarnebidragGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnBarnebidragGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertBidragsevnePeriodeListe)
        .addBruddpunkter(justertBPsAndelUnderholdskostnadPeriodeListe)
        .addBruddpunkter(justertSamvaersfradragPeriodeListe)
        .addBruddpunkter(justertDeltBostedPeriodeListe)
        .addBruddpunkter(justertBarnetilleggBPPeriodeListe)
        .addBruddpunkter(justertBarnetilleggBMPeriodeListe)
        .addBruddpunkter(justertBarnetilleggForsvaretPeriodeListe)
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkt(beregnBarnebidragGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnBarnebidragGrunnlag.getBeregnDatoFra(),
            beregnBarnebidragGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnBarnebidragGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      HashSet<Integer> soknadsbarnPersonIdListe = lagSoknadsbarnPersonIdListe(beregnBarnebidragGrunnlag, beregningsperiode);

      var bidragsevne = justertBidragsevnePeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(bidragsevnePeriode -> new Bidragsevne(bidragsevnePeriode.getBidragsevneBelop(),
              bidragsevnePeriode.getTjuefemProsentInntekt())).findFirst().orElse(null);

      var barnetilleggForsvaret = justertBarnetilleggForsvaretPeriodeListe.stream().filter(i ->
          i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(BarnetilleggForsvaretPeriode::getBarnetilleggForsvaretIPeriode).findFirst().orElse(null);

      var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();

      for (Integer soknadsbarnPersonId : soknadsbarnPersonIdListe) {

        var bPsAndelUnderholdskostnad = justertBPsAndelUnderholdskostnadPeriodeListe.stream()
            .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
            .filter(i -> i.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(bPsAndelUnderholdskostnadPeriode -> new BPsAndelUnderholdskostnad(
                bPsAndelUnderholdskostnadPeriode.getBPsAndelUnderholdskostnadProsent(),
                bPsAndelUnderholdskostnadPeriode.getBPsAndelUnderholdskostnadBelop()))
            .findFirst().orElse(null);

        var samvaersfradrag = justertSamvaersfradragPeriodeListe.stream().filter(i ->
            i.getDatoFraTil().overlapperMed(beregningsperiode))
            .filter(i -> i.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(SamvaersfradragPeriode::getSamvaersfradragBelop).findFirst().orElse(null);

        var deltBosted = justertDeltBostedPeriodeListe.stream().filter(i ->
            i.getDatoFraTil().overlapperMed(beregningsperiode))
            .filter(i -> i.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(DeltBostedPeriode::getDeltBostedIPeriode).findFirst().orElse(null);

        var barnetilleggBP = justertBarnetilleggBPPeriodeListe.stream()
            .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
            .filter(i -> i.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(barnetilleggBPPeriode -> new Barnetillegg(barnetilleggBPPeriode.getBarnetilleggBelop(),
                barnetilleggBPPeriode.getBarnetilleggSkattProsent())).findFirst().orElse(null);

        var barnetilleggBM = justertBarnetilleggBMPeriodeListe.stream()
            .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
            .filter(i -> i.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(barnetilleggBMPeriode -> new Barnetillegg(barnetilleggBMPeriode.getBarnetilleggBelop(),
                barnetilleggBMPeriode.getBarnetilleggSkattProsent())).findFirst().orElse(null);

        // Ved delt bosted skal andel av underholdskostnad reduseres med 50 prosentpoeng. Blir andelen under 50%
        // så skal ikke bidrag beregnes
        var andelProsent = bPsAndelUnderholdskostnad.getBPsAndelUnderholdskostnadProsent();
        var andelBelop = bPsAndelUnderholdskostnad.getBPsAndelUnderholdskostnadBelop() ;

        if (deltBosted) {
          if (bPsAndelUnderholdskostnad.getBPsAndelUnderholdskostnadProsent() > 50d) {
            andelProsent = bPsAndelUnderholdskostnad.getBPsAndelUnderholdskostnadProsent() - 50d;

            BigDecimal omregnetAndelBelop =
                BigDecimal.valueOf(bPsAndelUnderholdskostnad.getBPsAndelUnderholdskostnadBelop() /
                    bPsAndelUnderholdskostnad.getBPsAndelUnderholdskostnadProsent())
                    .multiply(BigDecimal.valueOf(100));

            omregnetAndelBelop = omregnetAndelBelop.multiply(BigDecimal.valueOf(andelProsent)
                .divide(BigDecimal.valueOf(100), new MathContext(10, RoundingMode.HALF_UP)));
            andelBelop = omregnetAndelBelop.doubleValue();
          } else {
            andelProsent = 0d;
            andelBelop = 0d;
          }
        }

        grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(soknadsbarnPersonId,
            new BPsAndelUnderholdskostnad(andelProsent, andelBelop), samvaersfradrag, deltBosted,
            barnetilleggBP, barnetilleggBM));
      }

      var sjablonliste = justertSjablonPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
          bidragsevne, grunnlagBeregningPerBarnListe, barnetilleggForsvaret, sjablonliste);

      if (barnetilleggForsvaret) {
        resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
            barnebidragBeregning.beregnVedBarnetilleggForsvaret(grunnlagBeregningPeriodisert), grunnlagBeregningPeriodisert));
      } else {
        resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
            barnebidragBeregning.beregn(grunnlagBeregningPeriodisert), grunnlagBeregningPeriodisert));

      }

    }

    return new BeregnBarnebidragResultat(resultatPeriodeListe);
  }

  @Override
  public HashSet<Integer> lagSoknadsbarnPersonIdListe(BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag,
      Periode periode) {
    var soknadsbarnPersonIdListe = new HashSet<Integer>();
    LocalDate tolvaarsdag;

    for (BPsAndelUnderholdskostnadPeriode grunnlag: beregnBarnebidragGrunnlag.getBPsAndelUnderholdskostnadPeriodeListe()) {
      if (grunnlag.getDatoFraTil().overlapperMed(periode)) {
        soknadsbarnPersonIdListe.add(grunnlag.getSoknadsbarnPersonId());
      }
    }
    for (SamvaersfradragPeriode grunnlag: beregnBarnebidragGrunnlag.getSamvaersfradragPeriodeListe()) {
      if (grunnlag.getDatoFraTil().overlapperMed(periode)) {
        soknadsbarnPersonIdListe.add(grunnlag.getSoknadsbarnPersonId());
      }
    }
    for (DeltBostedPeriode grunnlag: beregnBarnebidragGrunnlag.getDeltBostedPeriodeListe()) {
      if (grunnlag.getDatoFraTil().overlapperMed(periode)) {
        soknadsbarnPersonIdListe.add(grunnlag.getSoknadsbarnPersonId());
      }
    }
    for (BarnetilleggPeriode grunnlag: beregnBarnebidragGrunnlag.getBarnetilleggBPPeriodeListe()) {
      if (grunnlag.getDatoFraTil().overlapperMed(periode)) {
        soknadsbarnPersonIdListe.add(grunnlag.getSoknadsbarnPersonId());
      }
    }
    for (BarnetilleggPeriode grunnlag: beregnBarnebidragGrunnlag.getBarnetilleggBMPeriodeListe()) {
      if (grunnlag.getDatoFraTil().overlapperMed(periode)) {
        soknadsbarnPersonIdListe.add(grunnlag.getSoknadsbarnPersonId());
      }
    }

    return soknadsbarnPersonIdListe;
  }
  

  // Validerer at input-verdier til beregn-barnebidrag er gyldige
  public List<Avvik> validerInput(BeregnBarnebidragGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe", sjablonPeriodeListe,
            false, false, false, false));

    // Sjekk perioder for bidragsevne
    var bidragsevnePeriodeListe = new ArrayList<Periode>();
    for (BidragsevnePeriode bidragsevnePeriode : grunnlag.getBidragsevnePeriodeListe()) {
      bidragsevnePeriodeListe.add(bidragsevnePeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),"bidragsevnePeriodeListe",
        bidragsevnePeriodeListe, false, false, true, true));

    // Sjekk perioder for BPs andel av underholdskostnad
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (BPsAndelUnderholdskostnadPeriode bPsAndelUnderholdskostnadPeriode : grunnlag.getBPsAndelUnderholdskostnadPeriodeListe()) {
      bPsAndelUnderholdskostnadPeriodeListe.add(bPsAndelUnderholdskostnadPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "bPsAndelUnderholdskostnadPeriodeListe",
        bidragsevnePeriodeListe, false, false, true, true));

    // Sjekk perioder for samværsfradrag
    var samvaersfradragPeriodeListe = new ArrayList<Periode>();
    for (SamvaersfradragPeriode samvaersfradragPeriode : grunnlag.getSamvaersfradragPeriodeListe()) {
      samvaersfradragPeriodeListe.add(samvaersfradragPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "samvaersfradragPeriodeListe",
        bidragsevnePeriodeListe, false, false, true, true));

    // Sjekk perioder for delt bosted
    var deltBostedPeriodeListe = new ArrayList<Periode>();
    for (DeltBostedPeriode deltBostedPeriode : grunnlag.getDeltBostedPeriodeListe()) {
      deltBostedPeriodeListe.add(deltBostedPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "deltBostedPeriodeListe",
        bidragsevnePeriodeListe, false, false, true, true));

    // Sjekk perioder for barnetillegg BP
    var barnetilleggBPPeriodeListe = new ArrayList<Periode>();
    for (BarnetilleggPeriode barnetilleggBPPeriode : grunnlag.getBarnetilleggBPPeriodeListe()) {
      barnetilleggBPPeriodeListe.add(barnetilleggBPPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "barnetilleggBPPeriodeListe",
        bidragsevnePeriodeListe, false, false, true, true));

    // Sjekk perioder for barnetillegg BM
    var barnetilleggBMPeriodeListe = new ArrayList<Periode>();
    for (BarnetilleggPeriode barnetilleggBMPeriode : grunnlag.getBarnetilleggBMPeriodeListe()) {
      barnetilleggBMPeriodeListe.add(barnetilleggBMPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "barnetilleggBMPeriodeListe",
        bidragsevnePeriodeListe, false, false, true, true));

    // Sjekk perioder for barnetillegg fra forsvaret
    var barnetilleggForsvaretPeriodeListe = new ArrayList<Periode>();
    for (BarnetilleggForsvaretPeriode barnetilleggForsvaretPeriode : grunnlag.getBarnetilleggForsvaretPeriodeListe()) {
      barnetilleggForsvaretPeriodeListe.add(barnetilleggForsvaretPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "barnetilleggForsvaretPeriodeListe",
        bidragsevnePeriodeListe, false, false, true, true));

    return avvikListe;
  }
}
