package no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class BPsAndelUnderholdskostnadBeregningImpl implements BPsAndelUnderholdskostnadBeregning {

  @Override
  public ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregningPeriodisert.getSjablonListe());

    var andelProsent = BigDecimal.ZERO;
    var andelBelop = BigDecimal.ZERO;
    var barnetErSelvforsorget = false;

    // Legger sammen inntektene
    var inntektBP = grunnlagBeregningPeriodisert.getInntektBPListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

//    System.out.println("BP: " + inntektBP);

    // Legger sammen inntektene
    var inntektBM = grunnlagBeregningPeriodisert.getInntektBMListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
//    System.out.println("BM: " + inntektBM);

    // Legger sammen inntektene
    var inntektBB = grunnlagBeregningPeriodisert.getInntektBBListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
//    System.out.println("BB: " + inntektBB);

    // Test på om barnets inntekt er høyere enn 100 ganger sats for forhøyet forskudd. Hvis så så skal ikke BPs andel regnes ut.
    if (inntektBB.compareTo(
        sjablonNavnVerdiMap.get(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn()).multiply(BigDecimal.valueOf(100))) > 0) {
      barnetErSelvforsorget = true;
    } else {
      inntektBB = inntektBB.subtract(
          sjablonNavnVerdiMap.get(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn()).multiply(BigDecimal.valueOf(30)));

/*      System.out.println(
          "30 * forhøyet forskudd: " + sjablonNavnVerdiMap.get(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn()).multiply(BigDecimal.valueOf(30)));
      System.out.println("InntektBB etter fratrekk av 30 * forhøyet forskudd: " + inntektBB); */

      if (inntektBB.compareTo(BigDecimal.ZERO) < 0) {
        inntektBB = BigDecimal.ZERO;
      }

      andelProsent = (inntektBP.divide(
          inntektBP.add(inntektBM).add(inntektBB),
          new MathContext(10, RoundingMode.HALF_UP))
              .multiply(BigDecimal.valueOf(100)));

      andelProsent = andelProsent.setScale(1, RoundingMode.HALF_UP);
//      System.out.println("andelProsent: " + andelProsent);

      // Utregnet andel skal ikke være større en 5/6
      if (andelProsent.compareTo(BigDecimal.valueOf(83.3333333333)) > 0) {
        andelProsent = BigDecimal.valueOf(83.3333333333);
      }

      andelBelop =
          grunnlagBeregningPeriodisert.getUnderholdskostnadBelop()
              .multiply(andelProsent).divide(BigDecimal.valueOf(100),
              new MathContext(10, RoundingMode.HALF_UP));

      andelBelop = andelBelop.setScale(0, RoundingMode.HALF_UP);

    }

    return new ResultatBeregning(andelProsent, andelBelop, barnetErSelvforsorget, byggSjablonResultatListe(sjablonNavnVerdiMap));
  }

  @Override
  public ResultatBeregning beregnMedGamleRegler(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregningPeriodisert.getSjablonListe());

    var andelProsent = BigDecimal.ZERO;
    var andelBelop = BigDecimal.ZERO;
    var barnetErSelvforsorget = false;

    // Legger sammen inntektene
    var inntektBP = grunnlagBeregningPeriodisert.getInntektBPListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Legger sammen inntektene
    var inntektBM = grunnlagBeregningPeriodisert.getInntektBMListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Legger sammen inntektene
    var inntektBB = grunnlagBeregningPeriodisert.getInntektBBListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Test på om barnets inntekt er høyere enn 100 ganger sats for forhøyet forskudd. Hvis så så skal ikke BPs andel regnes ut.
    if (inntektBB.compareTo(
        sjablonNavnVerdiMap.get(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn()).multiply(BigDecimal.valueOf(100))) > 0) {
      barnetErSelvforsorget = true;
    } else {
      andelProsent = inntektBP.divide(
          inntektBP.add(inntektBM).add(inntektBB),
      new MathContext(10, RoundingMode.HALF_UP))
          .multiply(BigDecimal.valueOf(100));

//      System.out.println("andelprosent: " + andelProsent);

      var sjettedeler = new ArrayList<BigDecimal>();

      sjettedeler.add(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(6),
          new MathContext(12, RoundingMode.HALF_UP))
          .multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf(2).divide(BigDecimal.valueOf(6),
          new MathContext(12, RoundingMode.HALF_UP))
          .multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf(3).divide(BigDecimal.valueOf(6),
          new MathContext(12, RoundingMode.HALF_UP))
          .multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf(4).divide(BigDecimal.valueOf(6),
          new MathContext(12, RoundingMode.HALF_UP))
          .multiply(BigDecimal.valueOf(100)));
      sjettedeler.add(BigDecimal.valueOf(5).divide(BigDecimal.valueOf(6),
          new MathContext(12, RoundingMode.HALF_UP))
          .multiply(BigDecimal.valueOf(100)));

/*      System.out.println("Sjettedel: " + sjettedeler.get(0));
      System.out.println("Sjettedel: " + sjettedeler.get(1));
      System.out.println("Sjettedel: " + sjettedeler.get(2));
      System.out.println("Sjettedel: " + sjettedeler.get(3));
      System.out.println("Sjettedel: " + sjettedeler.get(4));*/

      BigDecimal finalAndel = andelProsent;
      andelProsent = sjettedeler.stream()
          .min(Comparator.comparing(a -> finalAndel.subtract(a).abs()))
          .orElseThrow(() -> new IllegalArgumentException("Empty collection"));

      // Utregnet andel skal ikke være større en 5/6
      if (andelProsent.compareTo(BigDecimal.valueOf(83.3333333333)) >= 0) {
        andelProsent = BigDecimal.valueOf(83.3333333333);
      } else {
        andelProsent = andelProsent.setScale(1, RoundingMode.HALF_UP);
      }

      andelBelop =
          grunnlagBeregningPeriodisert.getUnderholdskostnadBelop()
              .multiply(andelProsent)
              .divide(BigDecimal.valueOf(100),
              new MathContext(10, RoundingMode.HALF_UP));

      andelBelop = andelBelop.setScale(1, RoundingMode.HALF_UP);

    }

    return new ResultatBeregning(andelProsent, andelBelop, barnetErSelvforsorget, byggSjablonResultatListe(sjablonNavnVerdiMap));
    }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<Sjablon> sjablonListe) {

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORSKUDDSSATS_BELOP));

    return sjablonNavnVerdiMap;
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap) {
    var sjablonNavnVerdiListe = new ArrayList<SjablonNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) -> sjablonNavnVerdiListe.add(new SjablonNavnVerdi(sjablonNavn, sjablonVerdi)));
    return sjablonNavnVerdiListe.stream().sorted(comparing(SjablonNavnVerdi::getNavn)).collect(toList());
  }
}
