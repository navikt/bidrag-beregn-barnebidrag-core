package no.nav.bidrag.beregn.bidragsevne.beregning;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.bo.TrinnvisSkattesats;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

public class BidragsevneBeregningImpl implements BidragsevneBeregning {

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlagBeregning) {

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregning.getSjablonListe(), grunnlagBeregning.getBostatus().getKode(),
        grunnlagBeregning.getSkatteklasse().getSkatteklasse());

    // Beregn minstefradrag
    var minstefradrag = beregnMinstefradrag(grunnlagBeregning,
        sjablonNavnVerdiMap.get(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.getNavn()),
        sjablonNavnVerdiMap.get(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.getNavn()));

    // Legger sammen inntektene
    var inntekt = grunnlagBeregning.getInntektListe().stream()
        .map(Inntekt::getBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // finner 25% av inntekt og omregner til månedlig beløp
    var tjuefemProsentInntekt = inntekt
        .divide(BigDecimal.valueOf(4), new MathContext(10, RoundingMode.HALF_UP))
        .divide(BigDecimal.valueOf(12), new MathContext(10, RoundingMode.HALF_UP));

    tjuefemProsentInntekt = tjuefemProsentInntekt.setScale(0, RoundingMode.HALF_UP);

    // finner personfradragklasse ut fra angitt skatteklasse
    var personfradrag = BigDecimal.ZERO;
    if (grunnlagBeregning.getSkatteklasse().getSkatteklasse() == 1) {
      personfradrag = sjablonNavnVerdiMap.get(SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.getNavn());
    } else {
      personfradrag = sjablonNavnVerdiMap.get(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn());
    }

    var inntektMinusFradrag = inntekt.subtract(minstefradrag).subtract(personfradrag);

    // Trekker fra skatt
    var forelopigBidragsevne = inntekt.subtract(inntektMinusFradrag.multiply(
        sjablonNavnVerdiMap.get(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn()).divide(BigDecimal.valueOf(100),
            new MathContext(10, RoundingMode.HALF_UP))));

    // Trekker fra trygdeavgift
    forelopigBidragsevne = (forelopigBidragsevne.subtract((inntekt.multiply(
        sjablonNavnVerdiMap.get(SjablonTallNavn.TRYGDEAVGIFT_PROSENT.getNavn()).divide(BigDecimal.valueOf(100),
            new MathContext(10, RoundingMode.HALF_UP))))));

    // Trekker fra trinnvis skatt
    forelopigBidragsevne = forelopigBidragsevne.subtract(beregnSkattetrinnBelop(grunnlagBeregning));

    // Trekker fra boutgifter og midler til eget underhold
    forelopigBidragsevne = forelopigBidragsevne.subtract(
        sjablonNavnVerdiMap.get(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn()).multiply(BigDecimal.valueOf(12)));

    forelopigBidragsevne = forelopigBidragsevne.subtract(
        sjablonNavnVerdiMap.get(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn()).multiply(BigDecimal.valueOf(12)));

    // Trekker fra midler til underhold egne barn i egen husstand
    forelopigBidragsevne = forelopigBidragsevne.subtract(
        sjablonNavnVerdiMap.get(SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.getNavn())
            .multiply(BigDecimal.valueOf(grunnlagBeregning.getBarnIHusstand().getAntallBarn()))
            .multiply(BigDecimal.valueOf(12)));

    // Sjekker om og kalkulerer eventuell fordel særfradrag
    if (grunnlagBeregning.getSaerfradrag().getKode().equals(SaerfradragKode.HELT)) {
      forelopigBidragsevne = forelopigBidragsevne.add(sjablonNavnVerdiMap.get(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn()));
    } else if (grunnlagBeregning.getSaerfradrag().getKode().equals(SaerfradragKode.HALVT)) {
      forelopigBidragsevne = forelopigBidragsevne.add(
          sjablonNavnVerdiMap.get(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn()).divide(BigDecimal.valueOf(2),
              new MathContext(10, RoundingMode.HALF_UP)));
    }

    // Legger til fordel skatteklasse2
    if (grunnlagBeregning.getSkatteklasse().getSkatteklasse() == 2) {
      forelopigBidragsevne = forelopigBidragsevne.add(sjablonNavnVerdiMap.get(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn()));
    }

    // Finner månedlig beløp for bidragsevne
    var maanedligBidragsevne = forelopigBidragsevne
        .divide(BigDecimal.valueOf(12), new MathContext(10, RoundingMode.HALF_UP))
        .setScale(0, RoundingMode.HALF_UP);

    if (maanedligBidragsevne.compareTo(BigDecimal.ZERO) < 0) {
      maanedligBidragsevne = BigDecimal.ZERO;
    }

    return new ResultatBeregning(maanedligBidragsevne, tjuefemProsentInntekt, byggSjablonResultatListe(sjablonNavnVerdiMap,
        grunnlagBeregning.getSjablonListe()));
  }

  private BigDecimal beregnMinstefradrag(GrunnlagBeregning grunnlagBeregning, BigDecimal minstefradragInntektSjablonBelop,
      BigDecimal minstefradragInntektSjablonProsent) {

    // Legger sammen inntektene
    var inntekt = grunnlagBeregning.getInntektListe().stream()
        .map(Inntekt::getBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var minstefradrag = inntekt.multiply(minstefradragInntektSjablonProsent.divide(BigDecimal.valueOf(100),
        new MathContext(2, RoundingMode.HALF_UP)));

    if (minstefradrag.compareTo(minstefradragInntektSjablonBelop) > 0) {
      minstefradrag = minstefradragInntektSjablonBelop;
    }

    minstefradrag = minstefradrag.setScale(0, RoundingMode.HALF_UP);

    return minstefradrag;
  }

  private BigDecimal beregnSkattetrinnBelop(GrunnlagBeregning grunnlagBeregning) {

    // Legger sammen inntektene
    var inntekt = grunnlagBeregning.getInntektListe().stream()
        .map(Inntekt::getBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var sortertTrinnvisSkattesatsListe = SjablonUtil.hentTrinnvisSkattesats(
        grunnlagBeregning.getSjablonListe().stream()
            .map(SjablonPeriode::getSjablon)
            .collect(toList()),
        SjablonNavn.TRINNVIS_SKATTESATS);

    BigDecimal samletSkattetrinnBelop = BigDecimal.ZERO;
    var indeks = 1;

    // Beregn skattetrinnbeløp
    while (indeks < sortertTrinnvisSkattesatsListe.size()) {
      if (inntekt.compareTo(sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense()) > 0) {
        if (inntekt.compareTo(sortertTrinnvisSkattesatsListe.get(indeks).getInntektGrense()) < 0) {
          samletSkattetrinnBelop = samletSkattetrinnBelop.add(inntekt.subtract(
              sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense())
              .multiply(sortertTrinnvisSkattesatsListe.get(indeks - 1).getSats())
              .divide(BigDecimal.valueOf(100), new MathContext(10, RoundingMode.HALF_UP)));
        } else {
          samletSkattetrinnBelop = samletSkattetrinnBelop.add(
              sortertTrinnvisSkattesatsListe.get(indeks).getInntektGrense()
                  .subtract(sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense())
                  .multiply(sortertTrinnvisSkattesatsListe.get(indeks - 1).getSats()
                      .divide(BigDecimal.valueOf(100), new MathContext(10, RoundingMode.HALF_UP))));
        }
      }
      indeks = indeks + 1;
    }

    if (inntekt.compareTo(sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense()) > 0) {
      samletSkattetrinnBelop = samletSkattetrinnBelop.add(
          (inntekt.subtract(sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense())
              .multiply(sortertTrinnvisSkattesatsListe.get(indeks - 1).getSats())
              .divide(BigDecimal.valueOf(100),
                  new MathContext(1, RoundingMode.HALF_UP))));
    }

    samletSkattetrinnBelop = samletSkattetrinnBelop.setScale(0, RoundingMode.HALF_UP);

    return samletSkattetrinnBelop;
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<SjablonPeriode> sjablonPeriodeListe, BostatusKode bostatusKode, int skatteklasse) {

    var sjablonListe = sjablonPeriodeListe.stream()
        .map(SjablonPeriode::getSjablon)
        .collect(toList());

    var sjablonNavnVerdiMap = new HashMap<String, BigDecimal>();

    // Sjablontall
    if (skatteklasse == 1) {
      sjablonNavnVerdiMap.put(SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.getNavn(),
          SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP));
    } else {
      sjablonNavnVerdiMap.put(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn(),
          SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP));
      sjablonNavnVerdiMap.put(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(),
          SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP));
    }
    sjablonNavnVerdiMap.put(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT));
    sjablonNavnVerdiMap.put(SjablonTallNavn.TRYGDEAVGIFT_PROSENT.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.TRYGDEAVGIFT_PROSENT));
    sjablonNavnVerdiMap.put(SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP));
    sjablonNavnVerdiMap.put(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP));
    sjablonNavnVerdiMap.put(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT));
    sjablonNavnVerdiMap.put(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.getNavn(),
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP));

    // Bidragsevne
    var sjablonNokkelVerdi = bostatusKode.equals(BostatusKode.ALENE) ? "EN" : "GS";
    sjablonNavnVerdiMap.put(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn(), SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.BIDRAGSEVNE,
        singletonList(new SjablonNokkel(SjablonNokkelNavn.BOSTATUS.getNavn(), sjablonNokkelVerdi)), SjablonInnholdNavn.BOUTGIFT_BELOP));
    sjablonNavnVerdiMap.put(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn(), SjablonUtil.hentSjablonverdi(sjablonListe, SjablonNavn.BIDRAGSEVNE,
        singletonList(new SjablonNokkel(SjablonNokkelNavn.BOSTATUS.getNavn(), sjablonNokkelVerdi)), SjablonInnholdNavn.UNDERHOLD_BELOP));

    // TrinnvisSkattesats
    var trinnvisSkattesatsListe = SjablonUtil.hentTrinnvisSkattesats(sjablonListe, SjablonNavn.TRINNVIS_SKATTESATS);
    var indeks = 1;
    for (TrinnvisSkattesats trinnvisSkattesats : trinnvisSkattesatsListe) {
      sjablonNavnVerdiMap.put(SjablonNavn.TRINNVIS_SKATTESATS.getNavn() + "InntektGrense" + indeks, trinnvisSkattesats.getInntektGrense());
      sjablonNavnVerdiMap.put(SjablonNavn.TRINNVIS_SKATTESATS.getNavn() + "Sats" + indeks, trinnvisSkattesats.getSats());
      indeks++;
    }

    return sjablonNavnVerdiMap;
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonPeriodeNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap,
      List<SjablonPeriode> sjablonPeriodeListe) {
    var sjablonPeriodeNavnVerdiListe = new ArrayList<SjablonPeriodeNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) ->
        sjablonPeriodeNavnVerdiListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe, sjablonNavn), sjablonNavn, sjablonVerdi)));
    return sjablonPeriodeNavnVerdiListe.stream().sorted(comparing(SjablonPeriodeNavnVerdi::getNavn)).collect(toList());
  }

  private Periode hentPeriode(List<SjablonPeriode> sjablonPeriodeListe, String sjablonNavn) {
    return sjablonPeriodeListe.stream()
        .filter(sjablonPeriode -> sjablonPeriode.getSjablon().getNavn().equals(sjablonNavn))
        .map(SjablonPeriode::getPeriode)
        .findFirst()
        .orElse(new Periode(LocalDate.MIN, LocalDate.MAX));
  }
}
