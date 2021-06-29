package no.nav.bidrag.beregn.bidragsevne;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.TestUtil.BARN_I_HUSSTAND_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.BOSTATUS_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.INNTEKT_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SAERFRADRAG_REFERANSE;
import static no.nav.bidrag.beregn.TestUtil.SKATTEKLASSE_REFERANSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.TestUtil;
import no.nav.bidrag.beregn.bidragsevne.beregning.BidragsevneBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.BarnIHusstand;
import no.nav.bidrag.beregn.bidragsevne.bo.Bostatus;
import no.nav.bidrag.beregn.bidragsevne.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.Inntekt;
import no.nav.bidrag.beregn.bidragsevne.bo.Saerfradrag;
import no.nav.bidrag.beregn.bidragsevne.bo.Skatteklasse;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SaerfradragKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BidragsevneBeregningTest")
class BidragsevneBeregningTest {

  private final List<Sjablon> sjablonListe = TestUtil.byggSjabloner();
  private List<SjablonPeriode> sjablonPeriodeListe;

  private final BidragsevneBeregning bidragsevneBeregning = BidragsevneBeregning.getInstance();

  @BeforeEach
  void byggSjablonPeriodeListe() {
    sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();
  }

  @Test
  @DisplayName("Skal beregne bidragsevne med inntekt 1000000")
  void skalBeregneBidragsevneMedInntekt1000000() {
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(1000000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertThat(BigDecimal.valueOf(31859)).isEqualTo(resultat.getBelop());
  }

  @Test
  @DisplayName("Skal beregne bidragsevne med inntekt 520000")
  void skalBeregneBidragsevneMedInntekt520000() {
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(520000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertAll(
        () -> assertThat(BigDecimal.valueOf(8322)).isEqualTo(resultat.getBelop()),
        () -> assertThat(BigDecimal.valueOf(10833)).isEqualTo(resultat.getInntekt25Prosent())
    );
  }

  @Test
  @DisplayName("Skal beregne bidragsevne med inntekt 600000")
  void skalBeregneBidragsevneMedInntekt600000() {
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(600000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 0),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertThat(BigDecimal.valueOf(17617)).isEqualTo(resultat.getBelop());
  }

  @Test
  @DisplayName("Skal beregne bidragsevne med inntekt 666000")
  void skalBeregneBidragsevneMedInntekt666000() {
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertThat(BigDecimal.valueOf(8424)).isEqualTo(resultat.getBelop());
  }

  @Test
  @DisplayName("Skal beregne bidragsevne lik 0 når evne er negativ")
  void skalBeregneBidragsevneLik0NaarEvneErNegativ() {
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(100000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 1),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HELT),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertThat(BigDecimal.ZERO).isEqualTo(resultat.getBelop());
  }

  @Test
  @DisplayName("Skal ikke legge fordel skatteklasse 2 til beregnet evne for skatteklasse lik 1")
  void skalIkkeLeggeFordelSkatteklasse2TilBeregnetEvneForSkatteklasseLik1() {
    sjablonPeriodeListe.add(0, new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(12000))))));
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertThat(BigDecimal.valueOf(8424)).isEqualTo(resultat.getBelop());
  }

  @Test
  @DisplayName("Skal legge fordel skatteklasse 2 til beregnet evne for skatteklasse lik 2")
  void skalLeggeFordelSkatteklasse2TilBeregnetEvneForSkatteklasseLik2() {
    sjablonPeriodeListe.add(0, new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(12000))))));
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 2),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertThat(BigDecimal.valueOf(9424)).isEqualTo(resultat.getBelop());
  }

  @Test
  @DisplayName("Skal bruke halvt barn pga delt bosted")
  void skalBrukeHalvtBarnPgaDeltBosted() {
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 2),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 0.5),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertThat(BigDecimal.valueOf(23314)).isEqualTo(resultat.getBelop());
  }

  @Test
  @DisplayName("Skal bruke personfradrag skatteklasse 2 hvis skatteklasse 2 er angitt")
  void skalBrukePersonfradragSkatteklasse2HvisSkatteklasse2ErAngitt() {
    sjablonPeriodeListe.add(0, new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(24000))))));
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 2),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.INGEN),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertThat(BigDecimal.valueOf(7923)).isEqualTo(resultat.getBelop());
  }

  @Test
  @DisplayName("Skal bruke halvt særfradrag")
  void skalBrukeHalvtSaerfradrag() {
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.ALENE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HALVT),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertThat(BigDecimal.valueOf(8965)).isEqualTo(resultat.getBelop());
  }

  @Test
  @DisplayName("Skal bruke bostatus med andre")
  void skalBrukeBostatusMedAndre() {
    var grunnlagBeregning = new GrunnlagBeregning(
        singletonList(new Inntekt(INNTEKT_REFERANSE, InntektType.LONN_SKE, BigDecimal.valueOf(666000))),
        new Skatteklasse(SKATTEKLASSE_REFERANSE, 1),
        new Bostatus(BOSTATUS_REFERANSE, BostatusKode.MED_ANDRE),
        new BarnIHusstand(BARN_I_HUSSTAND_REFERANSE, 3),
        new Saerfradrag(SAERFRADRAG_REFERANSE, SaerfradragKode.HALVT),
        sjablonPeriodeListe
    );
    var resultat = bidragsevneBeregning.beregn(grunnlagBeregning);
    assertThat(BigDecimal.valueOf(14253)).isEqualTo(resultat.getBelop());
  }

  @Test
  @DisplayName("Skal hente sjablonverdier for trinnvis skattesats")
  void skalhenteSjablonverdierForTrinnvisSkattesats() {
    var sjablonVerdiSkattesats = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT);
    var sortertTrinnvisSkattesatsListe = SjablonUtil.hentTrinnvisSkattesats(sjablonListe, SjablonNavn.TRINNVIS_SKATTESATS);
    assertAll(
        () -> assertThat(sjablonVerdiSkattesats).isEqualTo(BigDecimal.valueOf(22)),
        () -> assertThat(sortertTrinnvisSkattesatsListe.size()).isEqualTo(4),
        () -> assertThat(sortertTrinnvisSkattesatsListe.get(0).getInntektGrense()).isEqualTo(BigDecimal.valueOf(180800)),
        () -> assertThat(sortertTrinnvisSkattesatsListe.get(0).getSats()).isEqualTo(BigDecimal.valueOf(1.9))
    );
  }
}
