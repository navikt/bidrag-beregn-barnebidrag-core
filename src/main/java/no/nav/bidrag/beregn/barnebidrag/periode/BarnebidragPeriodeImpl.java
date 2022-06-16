package no.nav.bidrag.beregn.barnebidrag.periode;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidrag;
import no.nav.bidrag.beregn.barnebidrag.bo.AndreLopendeBidragPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaret;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaretPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne;
import no.nav.bidrag.beregn.barnebidrag.bo.BidragsevnePeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBosted;
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBostedPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.barnebidrag.bo.Samvaersfradrag;
import no.nav.bidrag.beregn.barnebidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.felles.FellesPeriode;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;

public class BarnebidragPeriodeImpl extends FellesPeriode implements BarnebidragPeriode {

  private final BarnebidragBeregning barnebidragBeregning;

  public BarnebidragPeriodeImpl(BarnebidragBeregning barnebidragBeregning) {
    this.barnebidragBeregning = barnebidragBeregning;
  }

  public BeregnBarnebidragResultat beregnPerioder(BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
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

    var justertAndreLopendeBidragPeriodeListe = beregnBarnebidragGrunnlag.getAndreLopendeBidragPeriodeListe()
        .stream()
        .map(AndreLopendeBidragPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSjablonPeriodeListe = beregnBarnebidragGrunnlag.getSjablonPeriodeListe()
        .stream()
        .map(SjablonPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    var perioder = new Periodiserer()
        .addBruddpunkt(beregnBarnebidragGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertBidragsevnePeriodeListe)
        .addBruddpunkter(justertBPsAndelUnderholdskostnadPeriodeListe)
        .addBruddpunkter(justertSamvaersfradragPeriodeListe)
        .addBruddpunkter(justertDeltBostedPeriodeListe)
        .addBruddpunkter(justertBarnetilleggBPPeriodeListe)
        .addBruddpunkter(justertBarnetilleggBMPeriodeListe)
        .addBruddpunkter(justertBarnetilleggForsvaretPeriodeListe)
        .addBruddpunkter(justertAndreLopendeBidragPeriodeListe)
        .addBruddpunkter(justertSjablonPeriodeListe)
        .addBruddpunkt(beregnBarnebidragGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnBarnebidragGrunnlag.getBeregnDatoFra(), beregnBarnebidragGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    mergeSluttperiode(perioder, beregnBarnebidragGrunnlag.getBeregnDatoTil());

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      HashSet<Integer> soknadsbarnPersonIdListe = lagSoknadsbarnPersonIdListe(beregnBarnebidragGrunnlag, beregningsperiode);

      var bidragsevne = justertBidragsevnePeriodeListe.stream()
          .filter(bidragsevnePeriode -> bidragsevnePeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(bidragsevnePeriode -> new Bidragsevne(bidragsevnePeriode.getReferanse(), bidragsevnePeriode.getBelop(),
              bidragsevnePeriode.getTjuefemProsentInntekt()))
          .findFirst()
          .orElse(null);

      var barnetilleggForsvaret = justertBarnetilleggForsvaretPeriodeListe.stream()
          .filter(barnetilleggForsvaretPeriode -> barnetilleggForsvaretPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(barnetilleggForsvaretPeriode -> new BarnetilleggForsvaret(barnetilleggForsvaretPeriode.getReferanse(),
              barnetilleggForsvaretPeriode.getBarnetilleggForsvaretIPeriode()))
          .findFirst()
          .orElse(null);

      var andreLopendeBidragListe = justertAndreLopendeBidragPeriodeListe.stream()
          .filter(andreLopendeBidragPeriode -> andreLopendeBidragPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(andreLopendeBidragPeriode -> new AndreLopendeBidrag(andreLopendeBidragPeriode.getReferanse(),
              andreLopendeBidragPeriode.getBarnPersonId(), andreLopendeBidragPeriode.getBidragBelop(),
              andreLopendeBidragPeriode.getSamvaersfradragBelop()))
          .collect(toList());

      var grunnlagBeregningPerBarnListe = new ArrayList<GrunnlagBeregningPerBarn>();

      for (Integer soknadsbarnPersonId : soknadsbarnPersonIdListe) {

        var bPsAndelUnderholdskostnad = justertBPsAndelUnderholdskostnadPeriodeListe.stream()
            .filter(bPsAndelUnderholdskostnadPeriode -> bPsAndelUnderholdskostnadPeriode.getPeriode().overlapperMed(beregningsperiode))
            .filter(bPsAndelUnderholdskostnadPeriode -> bPsAndelUnderholdskostnadPeriode.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(bPsAndelUnderholdskostnadPeriode -> new BPsAndelUnderholdskostnad(bPsAndelUnderholdskostnadPeriode.getReferanse(),
                bPsAndelUnderholdskostnadPeriode.getAndelProsent(), bPsAndelUnderholdskostnadPeriode.getAndelBelop(),
                bPsAndelUnderholdskostnadPeriode.getBarnetErSelvforsorget()))
            .findFirst()
            .orElse(null);

        var samvaersfradrag = justertSamvaersfradragPeriodeListe.stream()
            .filter(samvaersfradragPeriode -> samvaersfradragPeriode.getPeriode().overlapperMed(beregningsperiode))
            .filter(samvaersfradragPeriode -> samvaersfradragPeriode.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(samvaersfradragPeriode -> new Samvaersfradrag(samvaersfradragPeriode.getReferanse(), samvaersfradragPeriode.getBelop()))
            .findFirst()
            .orElse(null);

        var deltBosted = justertDeltBostedPeriodeListe.stream()
            .filter(deltBostedPeriode -> deltBostedPeriode.getPeriode().overlapperMed(beregningsperiode))
            .filter(deltBostedPeriode -> deltBostedPeriode.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(deltBostedPeriode -> new DeltBosted(deltBostedPeriode.getReferanse(), deltBostedPeriode.getDeltBostedIPeriode()))
            .findFirst()
            .orElse(null);

        var barnetilleggBP = justertBarnetilleggBPPeriodeListe.stream()
            .filter(barnetilleggBPPeriode -> barnetilleggBPPeriode.getPeriode().overlapperMed(beregningsperiode))
            .filter(barnetilleggBPPeriode -> barnetilleggBPPeriode.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(barnetilleggBPPeriode -> new Barnetillegg(barnetilleggBPPeriode.getReferanse(), barnetilleggBPPeriode.getBelop(),
                barnetilleggBPPeriode.getSkattProsent()))
            .findFirst()
            .orElse(null);

        var barnetilleggBM = justertBarnetilleggBMPeriodeListe.stream()
            .filter(barnetilleggBMPeriode -> barnetilleggBMPeriode.getPeriode().overlapperMed(beregningsperiode))
            .filter(barnetilleggBMPeriode -> barnetilleggBMPeriode.getSoknadsbarnPersonId() == soknadsbarnPersonId)
            .map(barnetilleggBMPeriode -> new Barnetillegg(barnetilleggBMPeriode.getReferanse(), barnetilleggBMPeriode.getBelop(),
                barnetilleggBMPeriode.getSkattProsent()))
            .findFirst()
            .orElse(null);

        // Ved delt bosted skal andel av underholdskostnad reduseres med 50 prosentpoeng. Blir andelen under 50% skal ikke bidrag beregnes
        if (deltBosted.getDeltBostedIPeriode()) {
          bPsAndelUnderholdskostnad = justerForDeltBosted(bPsAndelUnderholdskostnad);
        }

        grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(soknadsbarnPersonId, bPsAndelUnderholdskostnad, samvaersfradrag, deltBosted,
            barnetilleggBP, barnetilleggBM));
      }

      var sjablonliste = justertSjablonPeriodeListe.stream()
          .filter(sjablonPeriode -> sjablonPeriode.getPeriode().overlapperMed(beregningsperiode))
          .collect(toList());

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregning = new GrunnlagBeregning(bidragsevne, grunnlagBeregningPerBarnListe, barnetilleggForsvaret, andreLopendeBidragListe,
          sjablonliste);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode, barnebidragBeregning.beregn(grunnlagBeregning), grunnlagBeregning));
    }

    return new BeregnBarnebidragResultat(resultatPeriodeListe);
  }

  private HashSet<Integer> lagSoknadsbarnPersonIdListe(BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag, Periode periode) {
    var soknadsbarnPersonIdListe = new HashSet<Integer>();

    for (BPsAndelUnderholdskostnadPeriode grunnlag : beregnBarnebidragGrunnlag.getBPsAndelUnderholdskostnadPeriodeListe()) {
      if (grunnlag.getPeriode().overlapperMed(periode)) {
        soknadsbarnPersonIdListe.add(grunnlag.getSoknadsbarnPersonId());
      }
    }
    for (SamvaersfradragPeriode grunnlag : beregnBarnebidragGrunnlag.getSamvaersfradragPeriodeListe()) {
      if (grunnlag.getPeriode().overlapperMed(periode)) {
        soknadsbarnPersonIdListe.add(grunnlag.getSoknadsbarnPersonId());
      }
    }
    for (DeltBostedPeriode grunnlag : beregnBarnebidragGrunnlag.getDeltBostedPeriodeListe()) {
      if (grunnlag.getPeriode().overlapperMed(periode)) {
        soknadsbarnPersonIdListe.add(grunnlag.getSoknadsbarnPersonId());
      }
    }
    for (BarnetilleggPeriode grunnlag : beregnBarnebidragGrunnlag.getBarnetilleggBPPeriodeListe()) {
      if (grunnlag.getPeriode().overlapperMed(periode)) {
        soknadsbarnPersonIdListe.add(grunnlag.getSoknadsbarnPersonId());
      }
    }
    for (BarnetilleggPeriode grunnlag : beregnBarnebidragGrunnlag.getBarnetilleggBMPeriodeListe()) {
      if (grunnlag.getPeriode().overlapperMed(periode)) {
        soknadsbarnPersonIdListe.add(grunnlag.getSoknadsbarnPersonId());
      }
    }

    return soknadsbarnPersonIdListe;
  }

  // Ved delt bosted skal andel av underholdskostnad reduseres med 50 prosentpoeng. Blir andelen under 50% skal ikke bidrag beregnes
  private BPsAndelUnderholdskostnad justerForDeltBosted(BPsAndelUnderholdskostnad bPsAndelUnderholdskostnad) {
    var andelProsentJustert = bPsAndelUnderholdskostnad.getAndelProsent();
    var andelBelop = bPsAndelUnderholdskostnad.getAndelBelop();

    if (bPsAndelUnderholdskostnad.getAndelProsent().compareTo(BigDecimal.valueOf(0.5)) > 0) {
      andelProsentJustert = bPsAndelUnderholdskostnad.getAndelProsent()
          .subtract(BigDecimal.valueOf(0.5));

      andelBelop = bPsAndelUnderholdskostnad.getAndelBelop()
          .divide(bPsAndelUnderholdskostnad.getAndelProsent(), new MathContext(10, RoundingMode.HALF_UP))
          .multiply(andelProsentJustert)
      ;
    } else {
      andelProsentJustert = BigDecimal.ZERO;
      andelBelop = BigDecimal.ZERO;
    }

    return new BPsAndelUnderholdskostnad(bPsAndelUnderholdskostnad.getReferanse(), andelProsentJustert, andelBelop,
        bPsAndelUnderholdskostnad.getBarnetErSelvforsorget());
  }


  // Validerer at input-verdier til beregn-barnebidrag er gyldige
  public List<Avvik> validerInput(BeregnBarnebidragGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : grunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getPeriode());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "sjablonPeriodeListe", sjablonPeriodeListe,
            false, false, false, false));

    // Sjekk perioder for bidragsevne
    var bidragsevnePeriodeListe = new ArrayList<Periode>();
    for (BidragsevnePeriode bidragsevnePeriode : grunnlag.getBidragsevnePeriodeListe()) {
      bidragsevnePeriodeListe.add(bidragsevnePeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "bidragsevnePeriodeListe",
        bidragsevnePeriodeListe, false, false, true, true));

    // Sjekk perioder for BPs andel av underholdskostnad
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (BPsAndelUnderholdskostnadPeriode bPsAndelUnderholdskostnadPeriode : grunnlag.getBPsAndelUnderholdskostnadPeriodeListe()) {
      bPsAndelUnderholdskostnadPeriodeListe.add(bPsAndelUnderholdskostnadPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "bPsAndelUnderholdskostnadPeriodeListe", bPsAndelUnderholdskostnadPeriodeListe, false, false, true, true));

    // Sjekk perioder for samværsfradrag
    var samvaersfradragPeriodeListe = new ArrayList<Periode>();
    for (SamvaersfradragPeriode samvaersfradragPeriode : grunnlag.getSamvaersfradragPeriodeListe()) {
      samvaersfradragPeriodeListe.add(samvaersfradragPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "samvaersfradragPeriodeListe",
        samvaersfradragPeriodeListe, false, false, true, true));

    // Sjekk perioder for delt bosted
    var deltBostedPeriodeListe = new ArrayList<Periode>();
    for (DeltBostedPeriode deltBostedPeriode : grunnlag.getDeltBostedPeriodeListe()) {
      deltBostedPeriodeListe.add(deltBostedPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "deltBostedPeriodeListe",
        deltBostedPeriodeListe, false, false, true, true));

    // Sjekk perioder for barnetillegg BP
    var barnetilleggBPPeriodeListe = new ArrayList<Periode>();
    for (BarnetilleggPeriode barnetilleggBPPeriode : grunnlag.getBarnetilleggBPPeriodeListe()) {
      barnetilleggBPPeriodeListe.add(barnetilleggBPPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "barnetilleggBPPeriodeListe",
        barnetilleggBPPeriodeListe, false, false, true, true));

    // Sjekk perioder for barnetillegg BM
    var barnetilleggBMPeriodeListe = new ArrayList<Periode>();
    for (BarnetilleggPeriode barnetilleggBMPeriode : grunnlag.getBarnetilleggBMPeriodeListe()) {
      barnetilleggBMPeriodeListe.add(barnetilleggBMPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "barnetilleggBMPeriodeListe",
        barnetilleggBMPeriodeListe, false, false, true, true));

    // Sjekk perioder for barnetillegg fra forsvaret
    var barnetilleggForsvaretPeriodeListe = new ArrayList<Periode>();
    for (BarnetilleggForsvaretPeriode barnetilleggForsvaretPeriode : grunnlag.getBarnetilleggForsvaretPeriodeListe()) {
      barnetilleggForsvaretPeriodeListe.add(barnetilleggForsvaretPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "barnetilleggForsvaretPeriodeListe", barnetilleggForsvaretPeriodeListe, false, false, true, true));

    return avvikListe;
  }
}
