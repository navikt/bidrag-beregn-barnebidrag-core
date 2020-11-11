package no.nav.bidrag.beregn.bidragsevne.beregning;

import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.TrinnvisSkattesats;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

//import com.google.common.base.Preconditions;

public class BidragsevneberegningImpl implements Bidragsevneberegning {

  @Override
  public ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

//    System.out.println("Start beregning av bidragsevne");

    // Henter sjablonverdier
    var sjablonNavnVerdiMap = hentSjablonVerdier(grunnlagBeregningPeriodisert.getSjablonListe(), grunnlagBeregningPeriodisert.getBostatusKode(),
        grunnlagBeregningPeriodisert.getSkatteklasse());

    // Beregn minstefradrag
    var minstefradrag = beregnMinstefradrag(grunnlagBeregningPeriodisert,
        sjablonNavnVerdiMap.get(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.getNavn()),
        sjablonNavnVerdiMap.get(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.getNavn()));

    // Legger sammen inntektene
    var inntekt = grunnlagBeregningPeriodisert.getInntektListe().stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

//    System.out.println("Samlede inntekter: " + inntekt);

    // finner 25% av inntekt og omregner til månedlig beløp
    BigDecimal tjuefemProsentInntekt = inntekt
        .divide(BigDecimal.valueOf(4), new MathContext(10, RoundingMode.HALF_UP))
        .divide(BigDecimal.valueOf(12), new MathContext(10, RoundingMode.HALF_UP));

    tjuefemProsentInntekt = tjuefemProsentInntekt.setScale(0, RoundingMode.HALF_UP);

//    System.out.println("25% av inntekt: " + tjuefemProsentInntekt);

    // finner personfradragklasse ut fra angitt skatteklasse
    var personfradrag = BigDecimal.ZERO;
    if (grunnlagBeregningPeriodisert.getSkatteklasse() == 1) {
//      System.out.println("Skatteklasse 1");
      personfradrag = sjablonNavnVerdiMap.get(SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.getNavn());
    } else {
//      System.out.println("Skatteklasse 2");
      personfradrag = sjablonNavnVerdiMap.get(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn());
    }

//    System.out.println("Beregnet personfradrag: " + personfradrag);

    BigDecimal inntektMinusFradrag =
        inntekt.subtract(minstefradrag).subtract(personfradrag);

    // Trekker fra skatt
    BigDecimal forelopigBidragsevne = inntekt.subtract(inntektMinusFradrag.multiply(
        sjablonNavnVerdiMap.get(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn()).divide(BigDecimal.valueOf(100),
            new MathContext(10, RoundingMode.HALF_UP))));

/*    System.out.println("Foreløpig evne etter fratrekk av ordinær skatt (totalt + månedlig beløp) : "
        + forelopigBidragsevne + " " + forelopigBidragsevne.divide(BigDecimal.valueOf(12),
        new MathContext(10, RoundingMode.HALF_UP)));*/

    // Trekker fra trygdeavgift
/*    System.out.println("Trygdeavgift: " + inntekt.multiply(
        sjablonNavnVerdiMap.get(SjablonTallNavn.TRYGDEAVGIFT_PROSENT.getNavn()).divide(BigDecimal.valueOf(100),
            new MathContext(10, RoundingMode.HALF_UP)))); */

    forelopigBidragsevne =
        (forelopigBidragsevne.subtract((inntekt.multiply(
            sjablonNavnVerdiMap.get(SjablonTallNavn.TRYGDEAVGIFT_PROSENT.getNavn()).divide(BigDecimal.valueOf(100),
                new MathContext(10, RoundingMode.HALF_UP))))));
//    System.out.println("Foreløpig evne etter fratrekk av trygdeavgift: " + forelopigBidragsevne);

    // Trekker fra trinnvis skatt
    forelopigBidragsevne = forelopigBidragsevne.subtract(beregnSkattetrinnBelop(grunnlagBeregningPeriodisert));
//    System.out.println("Foreløpig evne etter fratrekk av trinnskatt: " + forelopigBidragsevne);

    // Trekker fra boutgifter og midler til eget underhold
    forelopigBidragsevne = forelopigBidragsevne.subtract(
        sjablonNavnVerdiMap.get(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn()).multiply(BigDecimal.valueOf(12)));

//    System.out.println(
//        "Foreløpig evne etter fratrekk av boutgifter bor alene: " + forelopigBidragsevne);

    System.out.println(
        "Foreløpig evne etter fratrekk av boutgifter, bostatus " + grunnlagBeregningPeriodisert.getBostatusKode().toString() + ": "
            + forelopigBidragsevne);

    forelopigBidragsevne = forelopigBidragsevne.subtract(
        sjablonNavnVerdiMap.get(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn()).multiply(BigDecimal.valueOf(12)));

/*    System.out.println(
        "Foreløpig evne etter fratrekk av midler til eget underhold, bostatus " + grunnlagBeregningPeriodisert.getBostatusKode().toString() + ": "
            + forelopigBidragsevne); */

    // Trekker fra midler til underhold egne barn i egen husstand
    forelopigBidragsevne = forelopigBidragsevne.subtract(
        sjablonNavnVerdiMap.get(SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.getNavn())
            .multiply(BigDecimal.valueOf(grunnlagBeregningPeriodisert.getAntallEgneBarnIHusstand()))
            .multiply(BigDecimal.valueOf(12)));
//    System.out.println("Foreløpig evne etter fratrekk av underhold for egne barn i egen husstand: "
//        + forelopigBidragsevne);

    // Sjekker om og kalkulerer eventuell fordel særfradrag
    if (grunnlagBeregningPeriodisert.getSaerfradragkode().equals(SaerfradragKode.HELT)) {
      forelopigBidragsevne = forelopigBidragsevne.add(
          sjablonNavnVerdiMap.get(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn()));
//      System.out.println("Foreløpig evne etter tillegg for særfradrag: " + forelopigBidragsevne);
    } else if (grunnlagBeregningPeriodisert.getSaerfradragkode().equals(SaerfradragKode.HALVT)) {
      forelopigBidragsevne = forelopigBidragsevne.add(
          sjablonNavnVerdiMap.get(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn()).divide(BigDecimal.valueOf(2),
              new MathContext(10, RoundingMode.HALF_UP)));
//      System.out.println("Foreløpig evne etter tillegg for halvt særfradrag: " + forelopigBidragsevne);
    }

    // Legger til fordel skatteklasse2
    if (grunnlagBeregningPeriodisert.getSkatteklasse() == 2) {
      forelopigBidragsevne = forelopigBidragsevne.add(
          sjablonNavnVerdiMap.get(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn()));
//      System.out.println("Foreløpig evne etter tillegg for fordel skatteklasse2: " + forelopigBidragsevne);
    }

    // Finner månedlig beløp for bidragsevne
    BigDecimal maanedligBidragsevne = forelopigBidragsevne.divide(BigDecimal.valueOf(12),
        new MathContext(10, RoundingMode.HALF_UP));

    maanedligBidragsevne = maanedligBidragsevne.setScale(0, RoundingMode.HALF_UP);

//    System.out.println("Endelig beregnet bidragsevne: " + maanedligBidragsevne);

    if (maanedligBidragsevne.compareTo(BigDecimal.ZERO) < 0) {
//      System.out.println("Beregnet bidragsevne er mindre enn 0, settes til 0");
      maanedligBidragsevne = BigDecimal.ZERO;
//      System.out.println("Korrigert bidragsevne: " + maanedligBidragsevne);
    }
//    System.out.println("------------------------------------------------------");

    return new ResultatBeregning(maanedligBidragsevne, tjuefemProsentInntekt, byggSjablonResultatListe(sjablonNavnVerdiMap));
  }

  @Override
  public BigDecimal beregnMinstefradrag(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert, BigDecimal minstefradragInntektSjablonBelop,
      BigDecimal minstefradragInntektSjablonProsent) {

    // Legger sammen inntektene
    var inntekt = grunnlagBeregningPeriodisert.getInntektListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var minstefradrag = inntekt.multiply(
        minstefradragInntektSjablonProsent
            .divide(BigDecimal.valueOf(100),
                new MathContext(2, RoundingMode.HALF_UP)));

//    System.out.println("Beregnet minstefradrag før sjekk mot minstefradrag: " + minstefradrag);

    if (minstefradrag.compareTo(minstefradragInntektSjablonBelop) > 0) {
      minstefradrag = minstefradragInntektSjablonBelop;
    }

    minstefradrag = minstefradrag.setScale(0, RoundingMode.HALF_UP);
//    System.out.println("Endelig beregnet minstefradrag: " + minstefradrag);

    return minstefradrag;
  }

  @Override
  public BigDecimal beregnSkattetrinnBelop(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    // Legger sammen inntektene
    var inntekt = grunnlagBeregningPeriodisert.getInntektListe()
        .stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var sortertTrinnvisSkattesatsListe = SjablonUtil.hentTrinnvisSkattesats(
        grunnlagBeregningPeriodisert.getSjablonListe(),
        SjablonNavn.TRINNVIS_SKATTESATS);

    BigDecimal samletSkattetrinnBelop = BigDecimal.ZERO;
    var indeks = 1;

    // Beregn skattetrinnbeløp
    while (indeks < sortertTrinnvisSkattesatsListe.size()) {
      if (inntekt.compareTo(
          sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense()) > 0) {
        if (inntekt.compareTo(
            sortertTrinnvisSkattesatsListe.get(indeks).getInntektGrense()) < 0) {
          samletSkattetrinnBelop = samletSkattetrinnBelop.add(
              inntekt.subtract(
                  sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense())
                  .multiply(sortertTrinnvisSkattesatsListe.get(indeks - 1).getSats())
                  .divide(BigDecimal.valueOf(100),
                      new MathContext(10, RoundingMode.HALF_UP)));

        } else {
          samletSkattetrinnBelop = samletSkattetrinnBelop.add(
              sortertTrinnvisSkattesatsListe.get(indeks).getInntektGrense()
                  .subtract(sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense())
                  .multiply(sortertTrinnvisSkattesatsListe.get(indeks - 1).getSats()
                      .divide(BigDecimal.valueOf(100),
                          new MathContext(10, RoundingMode.HALF_UP))));

        }

//        System.out.println("samletSkattetrinnBelop: " + samletSkattetrinnBelop);
      }
      indeks = indeks + 1;
    }

    if (inntekt.compareTo(sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense()) > 0) {
      samletSkattetrinnBelop = samletSkattetrinnBelop.add (
          (inntekt.subtract(sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense())
              .multiply(sortertTrinnvisSkattesatsListe.get(indeks - 1).getSats())
              .divide(BigDecimal.valueOf(100),
                  new MathContext(1, RoundingMode.HALF_UP))));

//      System.out.println("samletSkattetrinnBelop: " + samletSkattetrinnBelop);

    }

    samletSkattetrinnBelop = samletSkattetrinnBelop.setScale(0, RoundingMode.HALF_UP);

//    System.out.println("Totalt skattetrinnbeløp: " + samletSkattetrinnBelop);

    return samletSkattetrinnBelop;
  }

  // Henter sjablonverdier
  private Map<String, BigDecimal> hentSjablonVerdier(List<Sjablon> sjablonListe, BostatusKode bostatusKode, int skatteklasse) {

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
  private List<SjablonNavnVerdi> byggSjablonResultatListe(Map<String, BigDecimal> sjablonNavnVerdiMap) {
    var sjablonNavnVerdiListe = new ArrayList<SjablonNavnVerdi>();
    sjablonNavnVerdiMap.forEach((sjablonNavn, sjablonVerdi) -> sjablonNavnVerdiListe.add(new SjablonNavnVerdi(sjablonNavn, sjablonVerdi)));
    return sjablonNavnVerdiListe;
  }
}
