package no.nav.bidrag.beregn.bpsandelunderholdskostnad.beregning;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.Inntekt;
import no.nav.bidrag.beregn.bpsandelunderholdskostnad.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.FellesBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class BPsAndelUnderholdskostnadBeregningImpl extends FellesBeregning implements BPsAndelUnderholdskostnadBeregning {

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning, Boolean beregnMedNyeRegler) {

    var andelProsent = BigDecimal.ZERO;
    var andelBelop = BigDecimal.ZERO;
    var barnetErSelvforsorget = false;

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregning.getSjablonListe());

    // Legger sammen inntektene
    var inntektBP = grunnlagBeregning.getInntektBPListe()
        .stream()
        .map(Inntekt::getBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var inntektBM = grunnlagBeregning.getInntektBMListe()
        .stream()
        .map(Inntekt::getBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var inntektBB = grunnlagBeregning.getInntektBBListe()
        .stream()
        .map(Inntekt::getBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var forskuddssatsBelop = sjablonNavnVerdiMap.get(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn());

    // Test på om barnets inntekt er høyere enn 100 ganger sats for forhøyet forskudd. Hvis så så skal ikke BPs andel regnes ut.
    if (inntektBB.compareTo(forskuddssatsBelop.multiply(BigDecimal.valueOf(100))) > 0) {
      barnetErSelvforsorget = true;

    } else {
      if (beregnMedNyeRegler) {
        andelProsent = beregnMedNyeRegler(inntektBP, inntektBM, inntektBB, forskuddssatsBelop);
      } else {
        andelProsent = beregnMedGamleRegler(inntektBP, inntektBM, inntektBB);
      }
      andelBelop = grunnlagBeregning.getUnderholdskostnad().getBelop()
          .multiply(andelProsent)
          .divide(BigDecimal.valueOf(100), new MathContext(10, RoundingMode.HALF_UP))
          .setScale(0, RoundingMode.HALF_UP);
    }

    return new ResultatBeregning(andelProsent, andelBelop, barnetErSelvforsorget,
        byggSjablonResultatListe(sjablonNavnVerdiMap, grunnlagBeregning.getSjablonListe()));
  }

  private BigDecimal beregnMedNyeRegler(BigDecimal inntektBP, BigDecimal inntektBM, BigDecimal inntektBB, BigDecimal forskuddsatsBelop) {

    inntektBB = inntektBB.subtract(forskuddsatsBelop.multiply(BigDecimal.valueOf(30)));

    if (inntektBB.compareTo(BigDecimal.ZERO) < 0) {
      inntektBB = BigDecimal.ZERO;
    }

    var andelProsent = (inntektBP
        .divide(inntektBP.add(inntektBM).add(inntektBB), new MathContext(10, RoundingMode.HALF_UP))
        .multiply(BigDecimal.valueOf(100)))
        .setScale(1, RoundingMode.HALF_UP);

    // Utregnet andel skal ikke være større en 5/6
    if (andelProsent.compareTo(BigDecimal.valueOf(83.3333333333)) > 0) {
      andelProsent = BigDecimal.valueOf(83.3333333333);
    }

    return andelProsent;
  }

  private BigDecimal beregnMedGamleRegler(BigDecimal inntektBP, BigDecimal inntektBM, BigDecimal inntektBB) {

    var andelProsent = inntektBP
        .divide(inntektBP.add(inntektBM).add(inntektBB), new MathContext(10, RoundingMode.HALF_UP))
        .multiply(BigDecimal.valueOf(100));

    var sjettedeler = new ArrayList<BigDecimal>();

    sjettedeler.add(BigDecimal.valueOf(1)
        .divide(BigDecimal.valueOf(6), new MathContext(12, RoundingMode.HALF_UP))
        .multiply(BigDecimal.valueOf(100)));
    sjettedeler.add(BigDecimal.valueOf(2)
        .divide(BigDecimal.valueOf(6), new MathContext(12, RoundingMode.HALF_UP))
        .multiply(BigDecimal.valueOf(100)));
    sjettedeler.add(BigDecimal.valueOf(3)
        .divide(BigDecimal.valueOf(6), new MathContext(12, RoundingMode.HALF_UP))
        .multiply(BigDecimal.valueOf(100)));
    sjettedeler.add(BigDecimal.valueOf(4)
        .divide(BigDecimal.valueOf(6), new MathContext(12, RoundingMode.HALF_UP))
        .multiply(BigDecimal.valueOf(100)));
    sjettedeler.add(BigDecimal.valueOf(5)
        .divide(BigDecimal.valueOf(6), new MathContext(12, RoundingMode.HALF_UP))
        .multiply(BigDecimal.valueOf(100)));

    var finalAndel = andelProsent;
    andelProsent = sjettedeler.stream()
        .min(comparing(a -> finalAndel.subtract(a).abs()))
        .orElseThrow(() -> new IllegalArgumentException("Empty collection"));

    // Utregnet andel skal ikke være større enn 5/6
    if (andelProsent.compareTo(BigDecimal.valueOf(83.3333333333)) >= 0) {
      andelProsent = BigDecimal.valueOf(83.3333333333);
    } else {
      andelProsent = andelProsent.setScale(1, RoundingMode.HALF_UP);
    }

    return andelProsent;
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<SjablonPeriode> sjablonPeriodeListe) {

    var sjablonListe = sjablonPeriodeListe.stream()
        .map(SjablonPeriode::getSjablon)
        .collect(toList());

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    sjablonNavnVerdiMap.put(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORSKUDDSSATS_BELOP));

    return sjablonNavnVerdiMap;
  }
}
