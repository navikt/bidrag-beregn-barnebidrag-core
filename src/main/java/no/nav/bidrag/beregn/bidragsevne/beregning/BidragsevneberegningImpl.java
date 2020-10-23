package no.nav.bidrag.beregn.bidragsevne.beregning;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.bidragsevne.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNokkelNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;

//import com.google.common.base.Preconditions;

public class BidragsevneberegningImpl implements Bidragsevneberegning {

  private List<SjablonNokkel> sjablonNokkelListe = new ArrayList<>();

  @Override
  public ResultatBeregning beregn(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    System.out.println("Start beregning av bidragsevne");

    Double minstefradrag = beregnMinstefradrag(grunnlagBeregningPeriodisert);

    // Legger sammen inntektene
    var inntekt = grunnlagBeregningPeriodisert.getInntektListe().stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.valueOf(0), BigDecimal::add);

    System.out.println("Samlede inntekter: " + inntekt);

    // finner 25% av inntekt og omregner til månedlig beløp
    BigDecimal tjuefemProsentInntekt = inntekt
        .divide(BigDecimal.valueOf(4), new MathContext(10, RoundingMode.HALF_UP))
        .divide(BigDecimal.valueOf(12), new MathContext(10, RoundingMode.HALF_UP));

     tjuefemProsentInntekt = tjuefemProsentInntekt.setScale(0, RoundingMode.HALF_UP);

//    tjuefemProsentInntekt = tjuefemProsentInntekt.setScale(0, RoundingMode.HALF_UP);

    System.out.println("25% av inntekt: " + tjuefemProsentInntekt);

    // finner personfradragklasse ut fra angitt skatteklasse
    Double personfradrag = 0.0;
    if (grunnlagBeregningPeriodisert.getSkatteklasse() == (1)) {
      System.out.println("Skatteklasse 1");
      personfradrag = SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
          SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP);
    } else {
      System.out.println("Skatteklasse 2");
      personfradrag = SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
          SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP);
    }

    System.out.println("Beregnet personfradrag: " + personfradrag);

    BigDecimal inntektMinusFradrag =
        inntekt.subtract(BigDecimal.valueOf(minstefradrag)).subtract(BigDecimal.valueOf(personfradrag));
//        inntekt - minstefradrag - personfradrag;

    // Trekker fra skatt
    BigDecimal forelopigBidragsevne = inntekt.subtract(inntektMinusFradrag.multiply(
        BigDecimal.valueOf(SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
        SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT)).divide(BigDecimal.valueOf(100),
            new MathContext(10, RoundingMode.HALF_UP))));

    System.out.println("Foreløpig evne etter fratrekk av ordinær skatt (totalt + månedlig beløp) : "
        + forelopigBidragsevne + " " + forelopigBidragsevne.divide(BigDecimal.valueOf(12),
        new MathContext(10, RoundingMode.HALF_UP)));

    // Trekker fra trygdeavgift
    System.out.println("Trygdeavgift: " + inntekt.multiply(BigDecimal.valueOf(
        SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
        SjablonTallNavn.TRYGDEAVGIFT_PROSENT)).divide(BigDecimal.valueOf(100),
        new MathContext(10, RoundingMode.HALF_UP))));

    forelopigBidragsevne =
        (forelopigBidragsevne.subtract((inntekt.multiply(BigDecimal.valueOf(
            SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
                SjablonTallNavn.TRYGDEAVGIFT_PROSENT)).divide(BigDecimal.valueOf(100),
            new MathContext(10, RoundingMode.HALF_UP))))));
    System.out.println("Foreløpig evne etter fratrekk av trygdeavgift: " + forelopigBidragsevne);

    // Trekker fra trinnvis skatt
    forelopigBidragsevne = forelopigBidragsevne.subtract(beregnSkattetrinnBelop(grunnlagBeregningPeriodisert));
    System.out.println("Foreløpig evne etter fratrekk av trinnskatt: " + forelopigBidragsevne);


    // Trekker fra boutgifter og midler til eget underhold
    if (grunnlagBeregningPeriodisert.getBostatusKode().equals(BostatusKode.ALENE)) {
      sjablonNokkelListe.clear();
      sjablonNokkelListe.add(new SjablonNokkel(SjablonNokkelNavn.BOSTATUS.getNavn(), "EN"));

      forelopigBidragsevne = forelopigBidragsevne.subtract(BigDecimal.valueOf(
          SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
              SjablonNavn.BIDRAGSEVNE,
              sjablonNokkelListe,
              SjablonInnholdNavn.BOUTGIFT_BELOP))).multiply(BigDecimal.valueOf(12));

      System.out.println(
          "Foreløpig evne etter fratrekk av boutgifter bor alene: " + forelopigBidragsevne);

      forelopigBidragsevne = forelopigBidragsevne.subtract(BigDecimal.valueOf(
          SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
              SjablonNavn.BIDRAGSEVNE,
              sjablonNokkelListe,
              SjablonInnholdNavn.UNDERHOLD_BELOP)).multiply(BigDecimal.valueOf(12));

      System.out.println(
          "Foreløpig evne etter fratrekk av midler til eget underhold bor alene: "
              + forelopigBidragsevne);

    } else {
      sjablonNokkelListe.clear();
      sjablonNokkelListe.add(new SjablonNokkel(SjablonNokkelNavn.BOSTATUS.getNavn(), "GS"));

      forelopigBidragsevne = forelopigBidragsevne.subtract(BigDecimal.valueOf(
          SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
              SjablonNavn.BIDRAGSEVNE,
              sjablonNokkelListe,
              SjablonInnholdNavn.BOUTGIFT_BELOP)).multiply(BigDecimal.valueOf(12));

      System.out.println(
          "Foreløpig evne etter fratrekk av boutgifter gift/samboer: " + forelopigBidragsevne);

      forelopigBidragsevne = forelopigBidragsevne.subtract(BigDecimal.valueOf(
          SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
              SjablonNavn.BIDRAGSEVNE,
              sjablonNokkelListe,
              SjablonInnholdNavn.UNDERHOLD_BELOP)).multiply(BigDecimal.valueOf(12)));

      System.out.println(
          "Foreløpig evne etter fratrekk av midler til eget underhold gift/samboer: "
              + forelopigBidragsevne);
    }

    // Trekker fra midler til underhold egne barn i egen husstand
    forelopigBidragsevne = forelopigBidragsevne.subtract(
        BigDecimal.valueOf(SjablonUtil.hentSjablonverdi(grunnlagBeregningPeriodisert.getSjablonListe(),
        SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP))
            .multiply(BigDecimal.valueOf(grunnlagBeregningPeriodisert.getAntallEgneBarnIHusstand())
                .multiply(BigDecimal.valueOf(12))));
    System.out.println("Foreløpig evne etter fratrekk av underhold for egne barn i egen husstand: "
        + forelopigBidragsevne);

    // Sjekker om og kalkulerer eventuell fordel særfradrag
    if (grunnlagBeregningPeriodisert.getSaerfradragkode().equals(SaerfradragKode.HELT)) {
      forelopigBidragsevne = forelopigBidragsevne.add(BigDecimal.valueOf(SjablonUtil.hentSjablonverdi(
          grunnlagBeregningPeriodisert.getSjablonListe(),
          SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP)));

      System.out.println("Foreløpig evne etter tillegg for særfradrag: " + forelopigBidragsevne);
    } else {
      if (grunnlagBeregningPeriodisert.getSaerfradragkode().equals(SaerfradragKode.HALVT)) {
        forelopigBidragsevne = forelopigBidragsevne.add(BigDecimal.valueOf(
            SjablonUtil.hentSjablonverdi(
                grunnlagBeregningPeriodisert.getSjablonListe(),
                SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP)).divide(BigDecimal.valueOf(2),
            new MathContext(10, RoundingMode.HALF_UP)));
        System.out
            .println("Foreløpig evne etter tillegg for halvt særfradrag: " + forelopigBidragsevne);
      }
    }

    // Legger til fordel skatteklasse2
    if (grunnlagBeregningPeriodisert.getSkatteklasse() == (2)) {
      forelopigBidragsevne = forelopigBidragsevne.add(BigDecimal.valueOf(SjablonUtil.hentSjablonverdi(
          grunnlagBeregningPeriodisert.getSjablonListe(),
          SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP)));
      System.out
          .println("Foreløpig evne etter tillegg for fordel skatteklasse2: " + forelopigBidragsevne);
    }

    // Finner månedlig beløp for bidragsevne
    BigDecimal maanedligBidragsevne = forelopigBidragsevne.divide(BigDecimal.valueOf(12));
    System.out.println("Endelig beregnet bidragsevne: " + maanedligBidragsevne);

    if (maanedligBidragsevne.compareTo(BigDecimal.valueOf(0)) < 0){
      System.out.println("Beregnet bidragsevne er mindre enn 0, settes til 0");
      maanedligBidragsevne = BigDecimal.valueOf(0);
      System.out.println("Korrigert bidragsevne: " + maanedligBidragsevne);
    }
    System.out.println("------------------------------------------------------");

    return new ResultatBeregning(maanedligBidragsevne, tjuefemProsentInntekt);

  }

  @Override
  public BigDecimal beregnMinstefradrag(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    // Legger sammen inntektene
    BigDecimal inntekt = grunnlagBeregningPeriodisert.getInntektListe().stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.valueOf(0), BigDecimal::add);

    BigDecimal minstefradrag = inntekt.multiply(BigDecimal.valueOf(
        SjablonUtil.hentSjablonverdi(
            grunnlagBeregningPeriodisert.getSjablonListe(),
            SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT)).divide(BigDecimal.valueOf(100));
    if (minstefradrag.compareTo(BigDecimal.valueOf(
        SjablonUtil.hentSjablonverdi(
            grunnlagBeregningPeriodisert.getSjablonListe(),
            SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP))) > 0) {
      minstefradrag = BigDecimal.valueOf(SjablonUtil.hentSjablonverdi(
          grunnlagBeregningPeriodisert.getSjablonListe(),
          SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP));
    }
    System.out.println("Beregnet minstefradrag: " + minstefradrag);
    return minstefradrag;
  }

  @Override
  public BigDecimal beregnSkattetrinnBelop(
      GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert) {

    // Legger sammen inntektene
    var inntekt = grunnlagBeregningPeriodisert.getInntektListe().stream()
        .map(Inntekt::getInntektBelop)
        .reduce(BigDecimal.valueOf(0), BigDecimal::add);

    var sortertTrinnvisSkattesatsListe = SjablonUtil.hentTrinnvisSkattesats(
        grunnlagBeregningPeriodisert.getSjablonListe(),
        SjablonNavn.TRINNVIS_SKATTESATS);

    BigDecimal samletSkattetrinnBelop = BigDecimal.valueOf(0);
    var indeks = 1;

    // Beregn skattetrinnbeløp
    while (indeks < sortertTrinnvisSkattesatsListe.size()) {
      if (inntekt.compareTo(BigDecimal.valueOf(
          sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense())) > 0) {
        if (inntekt.compareTo(BigDecimal.valueOf(sortertTrinnvisSkattesatsListe.get(indeks).getInntektGrense())) < 0) {
          samletSkattetrinnBelop = samletSkattetrinnBelop.add(BigDecimal.valueOf(
              inntekt.subtract(BigDecimal.valueOf(sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense())
                  .multiply(BigDecimal.valueOf(
                  (sortertTrinnvisSkattesatsListe.get(indeks - 1).getSats()))).divide(BigDecimal.valueOf(100),
                      new MathContext(10, RoundingMode.HALF_UP)));

          System.out.println("samletSkattetrinnBelop: " + samletSkattetrinnBelop);
        } else {
          samletSkattetrinnBelop = samletSkattetrinnBelop.add(BigDecimal.valueOf(
              (sortertTrinnvisSkattesatsListe.get(indeks).getInntektGrense() -
                  sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense()) * (
                  sortertTrinnvisSkattesatsListe.get(indeks - 1).getSats())).divide(BigDecimal.valueOf(100),
              new MathContext(10, RoundingMode.HALF_UP)));

          System.out.println("samletSkattetrinnBelop: " + samletSkattetrinnBelop);

        }
      }
      indeks = indeks + 1;
    }

    if (inntekt.compareTo(BigDecimal.valueOf(sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense())) > 0) {
      samletSkattetrinnBelop = samletSkattetrinnBelop.add (
          (inntekt.subtract(BigDecimal.valueOf(sortertTrinnvisSkattesatsListe.get(indeks - 1).getInntektGrense())
              .multiply(BigDecimal.valueOf(sortertTrinnvisSkattesatsListe.get(indeks - 1).getSats())
                  .divide(BigDecimal.valueOf(100))))));

      System.out.println("samletSkattetrinnBelop: " + samletSkattetrinnBelop);

    }


    System.out.println("Totalt skattetrinnbeløp: " + samletSkattetrinnBelop);

    return samletSkattetrinnBelop;
  }
}

