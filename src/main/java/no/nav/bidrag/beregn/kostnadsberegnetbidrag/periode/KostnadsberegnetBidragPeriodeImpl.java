package no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.FellesPeriode;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.felles.util.PeriodeUtil;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnetKostnadsberegnetBidragResultat;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Samvaersfradrag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.Underholdskostnad;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode;

public class KostnadsberegnetBidragPeriodeImpl extends FellesPeriode implements KostnadsberegnetBidragPeriode {

  private final KostnadsberegnetBidragBeregning kostnadsberegnetBidragBeregning;

  public KostnadsberegnetBidragPeriodeImpl(KostnadsberegnetBidragBeregning kostnadsberegnetBidragBeregning) {
    this.kostnadsberegnetBidragBeregning = kostnadsberegnetBidragBeregning;
  }

  public BeregnetKostnadsberegnetBidragResultat beregnPerioder(BeregnKostnadsberegnetBidragGrunnlag beregnKostnadsberegnetBidragGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
    var justertUnderholdskostnadPeriodeListe = beregnKostnadsberegnetBidragGrunnlag.getUnderholdskostnadPeriodeListe()
        .stream()
        .map(UnderholdskostnadPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertBPsAndelUnderholdskostnadPeriodeListe = beregnKostnadsberegnetBidragGrunnlag.getBPsAndelUnderholdskostnadPeriodeListe()
        .stream()
        .map(BPsAndelUnderholdskostnadPeriode::new)
        .collect(toCollection(ArrayList::new));

    var justertSamvaersfradragPeriodeListe = beregnKostnadsberegnetBidragGrunnlag.getSamvaersfradragPeriodeListe()
        .stream()
        .map(SamvaersfradragPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    var perioder = new Periodiserer()
        .addBruddpunkt(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertUnderholdskostnadPeriodeListe)
        .addBruddpunkter(justertBPsAndelUnderholdskostnadPeriodeListe)
        .addBruddpunkter(justertSamvaersfradragPeriodeListe)
        .addBruddpunkt(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoFra(), beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    mergeSluttperiode(perioder, beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoTil());

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var underholdskostnad = justertUnderholdskostnadPeriodeListe.stream()
          .filter(underholdskostnadPeriode -> underholdskostnadPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(underholdskostnadPeriode -> new Underholdskostnad(underholdskostnadPeriode.getReferanse(), underholdskostnadPeriode.getBelop()))
          .findFirst()
          .orElse(null);

      var bPsAndelUnderholdskostnad = justertBPsAndelUnderholdskostnadPeriodeListe.stream()
          .filter(bPsAndelUnderholdskostnadPeriode -> bPsAndelUnderholdskostnadPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(periode -> new BPsAndelUnderholdskostnad(periode.getReferanse(), periode.getAndelProsent()))
          .findFirst()
          .orElse(null);

      var samvaersfradrag = justertSamvaersfradragPeriodeListe.stream()
          .filter(samvaersfradragPeriode -> samvaersfradragPeriode.getPeriode().overlapperMed(beregningsperiode))
          .map(samvaersfradragPeriode -> new Samvaersfradrag(samvaersfradragPeriode.getReferanse(), samvaersfradragPeriode.getBelop()))
          .findFirst()
          .orElse(null);

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregning = new GrunnlagBeregning(underholdskostnad, bPsAndelUnderholdskostnad, samvaersfradrag);

      resultatPeriodeListe.add(new ResultatPeriode(
          beregnKostnadsberegnetBidragGrunnlag.getSoknadsbarnPersonId(), beregningsperiode, kostnadsberegnetBidragBeregning.beregn(grunnlagBeregning),
          grunnlagBeregning));
    }

    return new BeregnetKostnadsberegnetBidragResultat(resultatPeriodeListe);
  }


  // Validerer at input-verdier til kostnadsberegnet bidrag er gyldige
  public List<Avvik> validerInput(BeregnKostnadsberegnetBidragGrunnlag grunnlag) {

    // Sjekk perioder for underholdskostnad
    var underholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (UnderholdskostnadPeriode underholdskostnadPeriode : grunnlag.getUnderholdskostnadPeriodeListe()) {
      underholdskostnadPeriodeListe.add(underholdskostnadPeriode.getPeriode());
    }
    var avvikListe = new ArrayList<>(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "underholdskostnadPeriodeListe", underholdskostnadPeriodeListe, true, true, false, true));

    // Sjekk perioder for bps andel av underholdskostnad
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (BPsAndelUnderholdskostnadPeriode bPsAndelUnderholdskostnadPeriode : grunnlag.getBPsAndelUnderholdskostnadPeriodeListe()) {
      bPsAndelUnderholdskostnadPeriodeListe.add(bPsAndelUnderholdskostnadPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
        "bPsAndelUnderholdskostnadPeriodeListe", bPsAndelUnderholdskostnadPeriodeListe, true, true, false, true));

    // Sjekk perioder for samværsfradrag
    var samvaersfradragPeriodeListe = new ArrayList<Periode>();
    for (SamvaersfradragPeriode samvaersfradragPeriode : grunnlag.getSamvaersfradragPeriodeListe()) {
      bPsAndelUnderholdskostnadPeriodeListe.add(samvaersfradragPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(), "samværsfradragPeriodeListe",
        samvaersfradragPeriodeListe, false, false, false, false));

    return avvikListe;
  }
}
