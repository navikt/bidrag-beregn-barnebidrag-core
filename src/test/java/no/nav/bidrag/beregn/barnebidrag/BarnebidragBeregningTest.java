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
    var bPsAndelUnderholdskostnad = new BPsAndelUnderholdskostnad(80d, 8000d);
    var barnetilleggBP            = new Barnetillegg(0d, 0d);
    var barnetilleggBM            = new Barnetillegg(0d, 0d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1, bPsAndelUnderholdskostnad,
        0d, 0d, false, barnetilleggBP, barnetilleggBM, false));

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

    var bidragsevne               = new Bidragsevne(10000d, 10000d);
    var bPsAndelUnderholdskostnad = new BPsAndelUnderholdskostnad(80d, 1000d);
    var barnetilleggBP            = new Barnetillegg(1700d, 10d);
    var barnetilleggBM            = new Barnetillegg(0d, 0d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1, bPsAndelUnderholdskostnad,
        0d, 0d, false, barnetilleggBP, barnetilleggBM, false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    List<ResultatBeregning> resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);
    assertEquals(1700d-(1700d*10d/100), resultat.get(0).getResultatBarnebidragBelop());
  }

}

