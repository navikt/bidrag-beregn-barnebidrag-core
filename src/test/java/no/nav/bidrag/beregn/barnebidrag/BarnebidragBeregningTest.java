package no.nav.bidrag.beregn.barnebidrag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg;
import no.nav.bidrag.beregn.barnebidrag.bo.Bidragsevne;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPerBarn;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregningImpl;
import no.nav.bidrag.beregn.barnebidrag.bo.GrunnlagBeregningPeriodisert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning av barnebidrag")
public class BarnebidragBeregningTest {

  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();
  private List<GrunnlagBeregningPerBarn> grunnlagBeregningPerBarnListe  = new ArrayList<>();


  @DisplayName("Beregner barnebidrag ved full evne, ett barn, ingen barnetillegg")
  @Test
  void testBeregningEttBarnMedFullEvneIngenBarnetillegg() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne               = new Bidragsevne(10000d, 10000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(80d, 7000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(8000d, resultat.get(0).getResultatBarnebidragBelop());
  }

  @DisplayName("Beregner barnebidrag ved full evne, ett barn, og barnetillegg for BP der barnetillegg er høyere enn beregnet bidrag"
      + "Endelig bidrag skal da settes likt barnetillegg for BP")
  @Test
  void testBeregning1BarnFullEvneBarnetilleggBP() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(10000d, 10000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 1000d),
        0d, 0d, false,
        new Barnetillegg(1700d, 10d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(1700d-(1700d*10d/100), resultat.get(0).getResultatBarnebidragBelop());
  }

  @DisplayName("Beregner barnebidrag ved full evne, to barn")
  @Test
  void testBeregning2BarnFullEvne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(10000d, 10000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 8000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(80d, 7000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(8000d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(7000d, resultat.get(1).getResultatBarnebidragBelop());
  }

  @DisplayName("Beregner barnebidrag for tre barn med for lab bidragsevne")
  @Test
  void testBeregning3BarnBegrensetAvBidragsevne() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var bidragsevne = new Bidragsevne(8000d, 12000d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1,
        new BPsAndelUnderholdskostnad(80d, 5000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(2,
        new BPsAndelUnderholdskostnad(80d, 3000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(3,
        new BPsAndelUnderholdskostnad(80d, 2000d),
        0d, 0d, false,
        new Barnetillegg(0d, 0d),
        new Barnetillegg(0d, 0d),
        false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(4000d, resultat.get(0).getResultatBarnebidragBelop());
    assertEquals(2400d, resultat.get(1).getResultatBarnebidragBelop());
    assertEquals(1600d, resultat.get(2).getResultatBarnebidragBelop());
  }


}

