package no.nav.bidrag.beregn.bidragsevne;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.bidragsevne.beregning.BidragsevneberegningImpl;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregningPeriodisert;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BidragsevneBeregningTest")
class BidragsevneberegningTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @Test
  void beregn() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();

    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    // Tester beregning med ulike inntekter
    inntekter.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(1000000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(Double.valueOf(31858d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert).getResultatEvneBelop());

    inntekter.set(0, new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(520000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert2
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(Double.valueOf(8322d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert2).getResultatEvneBelop());
    assertEquals(Double.valueOf(10833d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert2).getResultat25ProsentInntekt());

    inntekter.set(0, new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(666000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert3
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 3,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(Double.valueOf(8424d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert3).getResultatEvneBelop());

    // Test på at beregnet bidragsevne blir satt til 0 når evne er negativ
    inntekter.set(0, new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(100000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert4
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.MED_ANDRE, 1,
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(Double.valueOf(0d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert4).getResultatEvneBelop());

    // Test at fordel skatteklasse 2 ikke legges til beregnet evne når skatteklasse = 1
    inntekter.set(0, new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(666000)));
    sjablonListe.set(0, new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 12000d))));

    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert5
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 3,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(Double.valueOf(8424d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert5).getResultatEvneBelop());

    // Test at fordel skatteklasse 2 legges til beregnet evne når skatteklasse = 2
    inntekter.set(0, new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(666000)));
    sjablonListe.set(0, new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 12000d))));

    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert6
        = new GrunnlagBeregningPeriodisert(inntekter, 2, BostatusKode.ALENE, 3,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(Double.valueOf(9424d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert6).getResultatEvneBelop());

    // Test at personfradrag skatteklasse 2 brukes hvis skatteklasse 2 er angitt
    sjablonListe.set(0, new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 0d))));

    sjablonListe.set(1, new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn(),emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), Double.valueOf(24000d)))));

    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert7
        = new GrunnlagBeregningPeriodisert(inntekter, 2, BostatusKode.ALENE, 3,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(Double.valueOf(7923d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert7).getResultatEvneBelop());


    // Test av halvt særfradrag
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert8
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 3,
        SaerfradragKode.HALVT, sjablonListe);
    assertEquals(Double.valueOf(8965d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert8).getResultatEvneBelop());

    // Test av bostatus MED_FLERE
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert9
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.MED_ANDRE, 3,
        SaerfradragKode.HALVT, sjablonListe);
    assertEquals(Double.valueOf(14253d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert9).getResultatEvneBelop());

  }

  @Test
  void beregnMinstefradrag() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();
    inntekter.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(200000)));

//    beregnBidragsevneGrunnlagPeriodisert.setSjablonPeriodeListe(sjabloner);
    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.HELT, sjablonListe);
    System.out.println(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregningPeriodisert));
    assertTrue((bidragsevneberegning.beregnMinstefradrag(grunnlagBeregningPeriodisert))
        .equals(Double.valueOf(62000)));

    inntekter.set(0, new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(1000000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert2
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.HELT, sjablonListe);
    System.out.println(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregningPeriodisert2));
    assertTrue((bidragsevneberegning.beregnMinstefradrag(grunnlagBeregningPeriodisert2))
        .equals(Double.valueOf(87450)));

  }

  @Test
  void beregnSkattetrinnBelop() {

    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    ArrayList<Inntekt> inntekter = new ArrayList<>();
    inntekter.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(666000)));

    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.HELT, sjablonListe);
    //System.out.println(bidragsevneberegning.beregnSkattetrinnBelop(beregnBidragsevneGrunnlagPeriodisert));
    assertTrue((bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregningPeriodisert))
        .equals(Double.valueOf(1400+16181+3465+0)));

    inntekter.set(0, new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(174600)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert2
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.HELT, sjablonListe);
    assertTrue((bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregningPeriodisert2))
        .equals(Double.valueOf(0)));

    inntekter.set(0, new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(250000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert3
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.HELT, sjablonListe);
    assertTrue((bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregningPeriodisert3))
        .equals(Double.valueOf(1315)));
  }

  @Test
  void TestFraJohn() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();

    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    // Tester beregning med ulike inntekter
    inntekter.add(new Inntekt(InntektType.LØNNSINNTEKT, Double.valueOf(500000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.MED_ANDRE, 0,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(Double.valueOf(16357d),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert).getResultatEvneBelop());

  }
}
