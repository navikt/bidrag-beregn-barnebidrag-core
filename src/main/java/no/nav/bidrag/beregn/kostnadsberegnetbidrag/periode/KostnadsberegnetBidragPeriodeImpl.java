package no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.BeregnBPsAndelUnderholdskostnadGrunnlag;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragResultat;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.ResultatPeriode;

public class KostnadsberegnetBidragPeriodeImpl implements KostnadsberegnetBidragPeriode {
  public KostnadsberegnetBidragPeriodeImpl(
      KostnadsberegnetBidragBeregning kostnadsberegnetBidragBeregning) {
    this.kostnadsberegnetBidragBeregning = kostnadsberegnetBidragBeregning;
  }

  private KostnadsberegnetBidragBeregning kostnadsberegnetBidragBeregning;

  public BeregnKostnadsberegnetBidragResultat beregnPerioder(
      BeregnKostnadsberegnetBidragGrunnlag beregnKostnadsberegnetBidragGrunnlag) {

    var resultatPeriodeListe = new ArrayList<ResultatPeriode>();

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
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start-beregning-fra-dato
        .addBruddpunkter(justertUnderholdskostnadPeriodeListe)
        .addBruddpunkter(justertBPsAndelUnderholdskostnadPeriodeListe)
        .addBruddpunkter(justertSamvaersfradragPeriodeListe)
        .addBruddpunkt(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start-beregning-til-dato
        .finnPerioder(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoFra(),
            beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoTil());

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    if (perioder.size() > 1) {
      if ((perioder.get(perioder.size() - 2).getDatoTil().equals(beregnKostnadsberegnetBidragGrunnlag.getBeregnDatoTil())) &&
          (perioder.get(perioder.size() - 1).getDatoTil() == null)) {
        var nyPeriode = new Periode(perioder.get(perioder.size() - 2).getDatoFra(), null);
        perioder.remove(perioder.size() - 1);
        perioder.remove(perioder.size() - 1);
        perioder.add(nyPeriode);
      }
    }

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {

      var underholdskostnadBelop = justertUnderholdskostnadPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(UnderholdskostnadPeriode::getUnderholdskostnadBelop).findFirst().orElse(null);

      var bPsAndelUnderholdskostnadProsent = justertBPsAndelUnderholdskostnadPeriodeListe.stream().filter(
          i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(BPsAndelUnderholdskostnadPeriode::getBPsAndelUnderholdskostnadProsent).findFirst().orElse(null);

      var samvaersfradragBelop = justertSamvaersfradragPeriodeListe.stream().filter(i ->
          i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(SamvaersfradragPeriode::getSamvaersfradrag).findFirst().orElse(null);

      System.out.println("Samværsfradrag: " + samvaersfradragBelop);

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregningPeriodisert = new GrunnlagBeregningPeriodisert(
          underholdskostnadBelop, bPsAndelUnderholdskostnadProsent, samvaersfradragBelop);

      resultatPeriodeListe.add(new ResultatPeriode(beregningsperiode,
          kostnadsberegnetBidragBeregning.beregn(grunnlagBeregningPeriodisert),
          grunnlagBeregningPeriodisert));
    }

    return new BeregnKostnadsberegnetBidragResultat(resultatPeriodeListe);
  }

  // Validerer at input-verdier til kostnadsberegnet bidrag er gyldige
  public List<Avvik> validerInput(BeregnKostnadsberegnetBidragGrunnlag grunnlag) {

    // Sjekk perioder for sjablonliste
    var underholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (UnderholdskostnadPeriode underholdskostnadPeriode : grunnlag
        .getUnderholdskostnadPeriodeListe()) {
      underholdskostnadPeriodeListe.add(underholdskostnadPeriode.getDatoFraTil());
    }
    var avvikListe = new ArrayList<>(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
            "underholdskostnadPeriodeListe",
            underholdskostnadPeriodeListe, true, true, false, true));

    // Sjekk perioder for bps andel av underholdskostnad
    var bPsAndelUnderholdskostnadPeriodeListe = new ArrayList<Periode>();
    for (BPsAndelUnderholdskostnadPeriode bPsAndelUnderholdskostnadPeriode : grunnlag
        .getBPsAndelUnderholdskostnadPeriodeListe()) {
      bPsAndelUnderholdskostnadPeriodeListe.add(bPsAndelUnderholdskostnadPeriode.getDatoFraTil());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
            "bPsAndelUnderholdskostnadPeriodeListe",
            bPsAndelUnderholdskostnadPeriodeListe, true, true, false, true));

    // Sjekk perioder for samværsfradrag
    var samvaersfradragPeriodeListe = new ArrayList<Periode>();
    for (SamvaersfradragPeriode samvaersfradragPeriode : grunnlag
        .getSamvaersfradragPeriodeListe()) {
      bPsAndelUnderholdskostnadPeriodeListe.add(samvaersfradragPeriode.getDatoFraTil());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(grunnlag.getBeregnDatoFra(), grunnlag.getBeregnDatoTil(),
            "samværsfradragPeriodeListe",
            samvaersfradragPeriodeListe, false, false, false, false));

    return avvikListe;
  }

}