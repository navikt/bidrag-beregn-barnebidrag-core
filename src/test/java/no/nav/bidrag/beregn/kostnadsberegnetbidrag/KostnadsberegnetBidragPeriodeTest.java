package no.nav.bidrag.beregn.kostnadsberegnetbidrag;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static no.nav.bidrag.beregn.TestUtil.BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SAMVAERSFRADRAG_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.UNDERHOLDSKOSTNAD_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BPsAndelUnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnetKostnadsberegnetBidragResultat;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.SamvaersfradragPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.UnderholdskostnadPeriode;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode.KostnadsberegnetBidragPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test av periodisert beregning av kostnadsberegnet bidrag")
public class KostnadsberegnetBidragPeriodeTest {

  private final KostnadsberegnetBidragPeriode kostnadsberegnetBidragPeriode = KostnadsberegnetBidragPeriode.getInstance();

  @DisplayName("Test av periodisering. Periodene i grunnlaget skal gjenspeiles i resultatperiodene")
  @Test
  void testPeriodisering() {
    var beregnDatoFra = LocalDate.parse("2018-07-01");
    var beregnDatoTil = LocalDate.parse("2020-08-01");
    var soknadsbarnPersonId = 1;

    // Lag input
    var underholdskostnadPeriodeListe = new ArrayList<UnderholdskostnadPeriode>();
    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(UNDERHOLDSKOSTNAD_REFERANSE + "_1",
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-08-01")), BigDecimal.valueOf(10000)));
    underholdskostnadPeriodeListe.add(new UnderholdskostnadPeriode(UNDERHOLDSKOSTNAD_REFERANSE + "_2",
        new Periode(LocalDate.parse("2019-08-01"), LocalDate.parse("2020-08-01")), BigDecimal.valueOf(1000)));

    var bPsAndelUnderholdskostnadPeriodeListe = singletonList(new BPsAndelUnderholdskostnadPeriode(BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")), BigDecimal.valueOf(20)));

    var samvaersfradragPeriodeListe = singletonList(new SamvaersfradragPeriode(SAMVAERSFRADRAG_REFERANSE,
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-08-01")), BigDecimal.valueOf(100)));

    // Sjabloner brukes ikke i beregning av kostnadsberegnet bidrag
    var grunnlag = new BeregnKostnadsberegnetBidragGrunnlag(beregnDatoFra, beregnDatoTil, soknadsbarnPersonId, underholdskostnadPeriodeListe,
        bPsAndelUnderholdskostnadPeriodeListe, samvaersfradragPeriodeListe);

    var resultat = kostnadsberegnetBidragPeriode.beregnPerioder(grunnlag);

    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(2),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2018-07-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultat().getBelop().compareTo(BigDecimal.valueOf(1900))).isZero(),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoFom()).isEqualTo(LocalDate.parse("2019-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getPeriode().getDatoTil()).isEqualTo(LocalDate.parse("2020-08-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultat().getBelop().compareTo(BigDecimal.valueOf(100))).isZero()
    );

    printGrunnlagResultat(resultat);
  }


  private void printGrunnlagResultat(BeregnetKostnadsberegnetBidragResultat beregnetKostnadsberegnetBidragResultat) {
    beregnetKostnadsberegnetBidragResultat.getResultatPeriodeListe().stream()
        .sorted(comparing(pR -> pR.getPeriode().getDatoFom()))
        .forEach(sortedPR -> System.out.println("Dato fra: " + sortedPR.getPeriode().getDatoFom() + "; " +
            "Dato til: " + sortedPR.getPeriode().getDatoTil() + "; " + "Prosentandel: " + sortedPR.getResultat().getBelop()));
  }
}
