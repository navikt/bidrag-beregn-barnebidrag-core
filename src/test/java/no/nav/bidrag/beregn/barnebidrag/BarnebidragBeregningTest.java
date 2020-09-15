package no.nav.bidrag.beregn.barnebidrag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.barnebidrag.bo.BPsAndelUnderholdskostnad;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggBM;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggBP;
import no.nav.bidrag.beregn.barnebidrag.bo.BarnetilleggForsvaret;
import no.nav.bidrag.beregn.barnebidrag.bo.DeltBosted;
import no.nav.bidrag.beregn.barnebidrag.bo.KostnadsberegnetBidrag;
import no.nav.bidrag.beregn.barnebidrag.bo.ResultatBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.Samvaersfradrag;
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

    double bidragsevne = 10000d;
    var bPsAndelUnderholdskostnadListe = new ArrayList<BPsAndelUnderholdskostnad>();
    var kostnadsberegnetBidragListe    = new ArrayList<KostnadsberegnetBidrag>();
    var samvaersfradragListe           = new ArrayList<Samvaersfradrag>();
    var deltBostedListe                = new ArrayList<DeltBosted>();
    var barnetilleggBPListe            = new ArrayList <BarnetilleggBP>();
    var barnetilleggBMListe            = new ArrayList<BarnetilleggBM>();
    var barnetilleggForsvaretListe     = new ArrayList<BarnetilleggForsvaret>();
    int antallBarn                     = 1;

    bPsAndelUnderholdskostnadListe.add(new BPsAndelUnderholdskostnad(1, 80d, 8000d));
    kostnadsberegnetBidragListe.add(new KostnadsberegnetBidrag(1, 10000d));
    samvaersfradragListe.add(new Samvaersfradrag(1, 0d));
    deltBostedListe.add(new DeltBosted(1, false));
    barnetilleggBPListe.add(new BarnetilleggBP(1, 0d, 17d));
    barnetilleggBMListe.add(new BarnetilleggBM(1, 0d, 17d));
    barnetilleggForsvaretListe.add(new BarnetilleggForsvaret(1, false, antallBarn));

    var grunnlagBeregningPeriodisert =  new GrunnlagBeregningPeriodisert(
        bidragsevne, bPsAndelUnderholdskostnadListe, kostnadsberegnetBidragListe, samvaersfradragListe,
        deltBostedListe, barnetilleggBPListe, barnetilleggBMListe, barnetilleggForsvaretListe, sjablonListe);

    ResultatBeregning resultat = barnebidragBeregning.beregn(grunnlagBeregningPeriodisert);


    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatBarnebidragBelop()).isEqualTo(8000d)
    );
  }



}

