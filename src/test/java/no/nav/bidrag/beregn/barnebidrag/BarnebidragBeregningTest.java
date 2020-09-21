package no.nav.bidrag.beregn.barnebidrag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.Barnetillegg;
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

  @DisplayName("Test av beregning av barnebidrag")
  @Test
  void testBeregning() {
    BarnebidragBeregningImpl barnebidragBeregning = new BarnebidragBeregningImpl();

    var grunnlagBeregningPerBarnListe      = new ArrayList<GrunnlagBeregningPerBarn>();

    double bidragsevne = 10000d;
    var bPsAndelUnderholdskostnad = new BPsAndelUnderholdskostnad(100d, 8000d);
    var barnetilleggBP            = new Barnetillegg(1d, 1d);
    var barnetilleggBM            = new Barnetillegg(1d, 1d);

    grunnlagBeregningPerBarnListe.add(new GrunnlagBeregningPerBarn(1, bPsAndelUnderholdskostnad,
        1d, 1d, false, barnetilleggBP, barnetilleggBM, false));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, grunnlagBeregningPerBarnListe, sjablonListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);


    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatBarnebidragBelop()).isEqualTo(8000d)
    );
  }



}

