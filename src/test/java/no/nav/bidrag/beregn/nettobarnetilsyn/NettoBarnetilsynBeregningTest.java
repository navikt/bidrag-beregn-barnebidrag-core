package no.nav.bidrag.beregn.nettobarnetilsyn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.nettobarnetilsyn.beregning.NettoBarnetilsynBeregningImpl;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlagPeriodisert;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.FaktiskUtgift;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av beregning Underholdskostnad")
class NettoBarnetilsynBeregningTest {
  private List<FaktiskUtgift> faktiskUtgiftListe = new ArrayList<>();
  private List<Sjablon> sjablonListe = TestUtil.byggSjabloner();

  @DisplayName("Test av beregning av underholdskostnad når barnet er 3 år gammelt")
  @Test
  void testBeregningNettoBarnetilsyn() {

    NettoBarnetilsynBeregningImpl nettoBarnetilsynBeregning = new NettoBarnetilsynBeregningImpl();

    faktiskUtgiftListe.add(new FaktiskUtgift(LocalDate.parse("2010-01-01"),1,2d));

    BeregnNettoBarnetilsynGrunnlagPeriodisert beregnNettoBarnetilsynGrunnlagPeriodisert
        = new BeregnNettoBarnetilsynGrunnlagPeriodisert(faktiskUtgiftListe, sjablonListe
    );

    assertEquals(2d,
        nettoBarnetilsynBeregning.beregn(beregnNettoBarnetilsynGrunnlagPeriodisert).getResultatBeregningListe().get(0)
            .getResultatBelop());
  }


}
