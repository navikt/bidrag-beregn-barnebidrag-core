package no.nav.bidrag.beregn.bidragsevne;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
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
    inntekter.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(1000000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(31859),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert).getResultatEvneBelop());

    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(520000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert2
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(8322),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert2).getResultatEvneBelop());
    assertEquals(BigDecimal.valueOf(10833),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert2).getResultat25ProsentInntekt());

    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(666000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert3
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 3,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(8424),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert3).getResultatEvneBelop());

    // Test på at beregnet bidragsevne blir satt til 0 når evne er negativ
    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(100000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert4
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.MED_ANDRE, 1,
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(BigDecimal.ZERO,
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert4).getResultatEvneBelop());

    // Test at fordel skatteklasse 2 ikke legges til beregnet evne når skatteklasse = 1
    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(666000)));
    sjablonListe.set(0, new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(12000)))));

    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert5
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 3,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(8424),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert5).getResultatEvneBelop());

    // Test at fordel skatteklasse 2 legges til beregnet evne når skatteklasse = 2
    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(666000)));
    sjablonListe.set(0, new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(12000)))));

    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert6
        = new GrunnlagBeregningPeriodisert(inntekter, 2, BostatusKode.ALENE, 3,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(9424),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert6).getResultatEvneBelop());

    // Test at personfradrag skatteklasse 2 brukes hvis skatteklasse 2 er angitt
    sjablonListe.set(0, new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(0)))));

    sjablonListe.set(1, new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn(),emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(24000)))));

    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert7
        = new GrunnlagBeregningPeriodisert(inntekter, 2, BostatusKode.ALENE, 3,
        SaerfradragKode.INGEN, sjablonListe);
    assertEquals(BigDecimal.valueOf(7923),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert7).getResultatEvneBelop());


    // Test av halvt særfradrag
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert8
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 3,
        SaerfradragKode.HALVT, sjablonListe);
    assertEquals(BigDecimal.valueOf(8965),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert8).getResultatEvneBelop());

    // Test av bostatus MED_FLERE
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert9
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.MED_ANDRE, 3,
        SaerfradragKode.HALVT, sjablonListe);
    assertEquals(BigDecimal.valueOf(14253),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert9).getResultatEvneBelop());

  }

  @Test
  void beregnMinstefradrag() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();
    inntekter.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(200000)));

//    beregnBidragsevneGrunnlagPeriodisert.setSjablonPeriodeListe(sjabloner);
    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.HELT, sjablonListe);
    System.out.println(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregningPeriodisert));
    assertEquals(BigDecimal.valueOf(62000),
        bidragsevneberegning.beregnMinstefradrag(grunnlagBeregningPeriodisert));

    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(1000000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert2
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.HELT, sjablonListe);
    System.out.println(bidragsevneberegning.beregnMinstefradrag(grunnlagBeregningPeriodisert2));
    assertEquals(BigDecimal.valueOf(87450),
        bidragsevneberegning.beregnMinstefradrag(grunnlagBeregningPeriodisert2));

  }

  @Test
  void beregnSkattetrinnBelop() {

    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    ArrayList<Inntekt> inntekter = new ArrayList<>();
    inntekter.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(666000)));

    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(BigDecimal.valueOf(1400+16181+3465+0),
        bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregningPeriodisert));

    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(174600)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert2
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(BigDecimal.ZERO,
        bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregningPeriodisert2));

    inntekter.set(0, new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(250000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert3
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 1,
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(BigDecimal.valueOf(1315),
        bidragsevneberegning.beregnSkattetrinnBelop(grunnlagBeregningPeriodisert3));
  }

  @Test
  void TestFraJohn() {

    ArrayList<Inntekt> inntekter = new ArrayList<>();

    BidragsevneberegningImpl bidragsevneberegning = new BidragsevneberegningImpl();

    // Tester beregning med ulike inntekter
    inntekter.add(new Inntekt(InntektType.LONN_SKE, BigDecimal.valueOf(300000)));
    GrunnlagBeregningPeriodisert grunnlagBeregningPeriodisert
        = new GrunnlagBeregningPeriodisert(inntekter, 1, BostatusKode.ALENE, 0,
        SaerfradragKode.HELT, sjablonListe);
    assertEquals(BigDecimal.valueOf(1217),
        bidragsevneberegning.beregn(grunnlagBeregningPeriodisert).getResultatEvneBelop());

  }
}