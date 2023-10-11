package no.nav.bidrag.beregn;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn;
import no.nav.bidrag.domain.enums.sjablon.SjablonNavn;
import no.nav.bidrag.domain.enums.sjablon.SjablonNokkelNavn;
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn;

public class TestUtil {

  public static final String INNTEKT_REFERANSE = "INNTEKT_REFERANSE";
  public static final String INNTEKT_BP_REFERANSE = "INNTEKT_BP_REFERANSE";
  public static final String INNTEKT_BM_REFERANSE = "INNTEKT_BM_REFERANSE";
  public static final String INNTEKT_BB_REFERANSE = "INNTEKT_BB_REFERANSE";
  public static final String SKATTEKLASSE_REFERANSE = "SKATTEKLASSE_REFERANSE";
  public static final String BOSTATUS_REFERANSE = "BOSTATUS_REFERANSE";
  public static final String BARN_I_HUSSTAND_REFERANSE = "BARN_I_HUSSTAND_REFERANSE";
  public static final String SAERFRADRAG_REFERANSE = "SAERFRADRAG_REFERANSE";
  public static final String FAKTISK_UTGIFT_REFERANSE = "FAKTISK_UTGIFT_REFERANSE";
  public static final String SOKNADSBARN_REFERANSE = "SOKNADSBARN_REFERANSE";
  public static final String BARNETILSYN_MED_STONAD_REFERANSE = "BARNETILSYN_MED_STONAD_REFERANSE";
  public static final String NETTO_BARNETILSYN_REFERANSE = "NETTO_BARNETILSYN_REFERANSE";
  public static final String FORPLEINING_UTGIFT_REFERANSE = "FORPLEINING_UTGIFT_REFERANSE";
  public static final String UNDERHOLDSKOSTNAD_REFERANSE = "UNDERHOLDSKOSTNAD_REFERANSE";
  public static final String SAMVAERSKLASSE_REFERANSE = "SAMVAERSKLASSE_REFERANSE";
  public static final String SAMVAERSFRADRAG_REFERANSE = "SAMVAERSFRADRAG_REFERANSE";
  public static final String BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE = "BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE";
  public static final String BIDRAGSEVNE_REFERANSE = "BIDRAGSEVNE_REFERANSE";
  public static final String DELT_BOSTED_REFERANSE = "DELT_BOSTED_REFERANSE";
  public static final String BARNETILLEGG_BP_REFERANSE = "BARNETILLEGG_BP_REFERANSE";
  public static final String BARNETILLEGG_BM_REFERANSE = "BARNETILLEGG_BM_REFERANSE";
  public static final String BARNETILLEGG_FORSVARET_REFERANSE = "BARNETILLEGG_FORSVARET_REFERANSE";
  public static final String ANDRE_LOPENDE_BIDRAG_REFERANSE = "ANDRE_LOPENDE_BIDRAG_REFERANSE";

  public static final String ORDINAER_BARNETRYGD = "O";

  public static List<Sjablon> byggSjabloner() {

    var sjablonListe = new ArrayList<Sjablon>();

    // Barnetilsyn
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonListe.add(new Sjablon(SjablonNavn.BARNETILSYN.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.getNavn(), "64"),
            new SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.getNavn(), "DO")),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.getNavn(), BigDecimal.valueOf(358)))));
    sjablonListe.add(new Sjablon(SjablonNavn.BARNETILSYN.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.getNavn(), "64"),
            new SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.getNavn(), "DU")),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.getNavn(), BigDecimal.valueOf(257)))));
    sjablonListe.add(new Sjablon(SjablonNavn.BARNETILSYN.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.getNavn(), "64"),
            new SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.getNavn(), "HO")),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.getNavn(), BigDecimal.valueOf(589)))));
    sjablonListe.add(new Sjablon(SjablonNavn.BARNETILSYN.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.getNavn(), "64"),
            new SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.getNavn(), "HU")),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.getNavn(), BigDecimal.valueOf(643)))));

    // Bidragsevne
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.BIDRAGSEVNE.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.BOSTATUS.getNavn(), "EN")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn(), BigDecimal.valueOf(9764)),
                new SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn(), BigDecimal.valueOf(9818)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.BIDRAGSEVNE.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.BOSTATUS.getNavn(), "GS")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn(), BigDecimal.valueOf(5981)),
                new SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn(), BigDecimal.valueOf(8313)))));

    // Forbruksutgifter
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonListe
        .add(new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), singletonList(new SjablonNokkel(
            SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(7953)))));
    sjablonListe
        .add(new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), singletonList(new SjablonNokkel(
            SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(4228)))));
    sjablonListe
        .add(new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), singletonList(new SjablonNokkel(
            SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(7953)))));
    sjablonListe
        .add(new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), singletonList(new SjablonNokkel(
            SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(5710)))));
    sjablonListe
        .add(new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(), singletonList(new SjablonNokkel(
            SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(6913)))));

    // Maks fradrag
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_FRADRAG.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "1")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(2083.33)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_FRADRAG.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "2")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(3333)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_FRADRAG.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "3")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(4583)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_FRADRAG.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "4")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(5833)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_FRADRAG.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "5")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(7083)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_FRADRAG.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "6")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(8333)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_FRADRAG.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "7")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(9583)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_FRADRAG.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "8")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(10833)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_FRADRAG.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "99")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(12083)))));

    // Maks tilsyn
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_TILSYN.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "1")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(), BigDecimal.valueOf(6333)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_TILSYN.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "2")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(), BigDecimal.valueOf(8264)))));
    sjablonListe
        .add(new Sjablon(
            SjablonNavn.MAKS_TILSYN.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "99")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(), BigDecimal.valueOf(9364)))));

    // Samvaersfradrag
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "00"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(1)),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(1)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.ZERO))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(256)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(353)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(457)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(528)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(528)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(849)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1167)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(), Arrays
        .asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1513)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1749)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1749)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "03"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(13)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(2272)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "03"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(13)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(2716)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "03"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(13)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(3199)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "03"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(13)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(3528)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "03"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(13)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(3528)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "04"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(15)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(2852)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "04"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(15)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(3410)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "04"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(15)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(4016)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "04"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(15)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(4429)))));
    sjablonListe.add(new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
        Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "04"),
            new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
            new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(15)),
            new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(4429)))));

    // Sjablontall
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonListe.add(new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1054)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.ORDINAER_SMAABARNSTILLEGG_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.ZERO))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(2825)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(31)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(87450)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(51300)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(51300)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.TRYGDEAVGIFT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(8.2)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(12977)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.ZERO))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(3841)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(25.05)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1670)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(5667)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(2334)))));

    // Trinnvis skattesats
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonListe.add(new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(), BigDecimal.valueOf(999550)),
            new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(), BigDecimal.valueOf(16.2)))));
    sjablonListe.add(new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(), BigDecimal.valueOf(254500)),
            new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(), BigDecimal.valueOf(4.2)))));
    sjablonListe.add(new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(), BigDecimal.valueOf(639750)),
            new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(), BigDecimal.valueOf(13.2)))));
    sjablonListe.add(new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
        Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(), BigDecimal.valueOf(180800)),
            new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(), BigDecimal.valueOf(1.9)))));

    return sjablonListe;
  }

  public static List<SjablonPeriode> byggSjablonPeriodeListe() {

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    // Barnetilsyn
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.BARNETILSYN.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.getNavn(), "64"),
                new SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.getNavn(), "DO")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.getNavn(), BigDecimal.valueOf(358))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.BARNETILSYN.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.getNavn(), "64"),
                new SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.getNavn(), "DU")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.getNavn(), BigDecimal.valueOf(257))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.BARNETILSYN.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.getNavn(), "64"),
                new SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.getNavn(), "HO")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.getNavn(), BigDecimal.valueOf(589))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.BARNETILSYN.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.getNavn(), "64"),
                new SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.getNavn(), "HU")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.getNavn(), BigDecimal.valueOf(643))))));

    // Bidragsevne
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.BIDRAGSEVNE.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.BOSTATUS.getNavn(), "EN")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn(), BigDecimal.valueOf(9764)),
                new SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn(), BigDecimal.valueOf(9818))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.BIDRAGSEVNE.getNavn(), singletonList(new SjablonNokkel(SjablonNokkelNavn.BOSTATUS.getNavn(), "GS")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.getNavn(), BigDecimal.valueOf(5981)),
                new SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.getNavn(), BigDecimal.valueOf(8313))))));

    // Forbruksutgifter
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(7953))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(4228))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(7953))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(5710))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.FORBRUKSUTGIFTER.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.getNavn(), BigDecimal.valueOf(6913))))));

    // Maks fradrag
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "1")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(2083.33))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "2")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(3333))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "3")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(4583))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "4")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(5833))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "5")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(7083))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "6")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(8333))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "7")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(9583))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "8")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(10833))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_FRADRAG.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "99")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(12083))))));

    // Maks tilsyn
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_TILSYN.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "1")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(), BigDecimal.valueOf(6333))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_TILSYN.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "2")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(), BigDecimal.valueOf(8264))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.MAKS_TILSYN.getNavn(),
            singletonList(new SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.getNavn(), "99")),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.getNavn(), BigDecimal.valueOf(9364))))));

    // Samvaersfradrag
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "00"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(1)),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(1)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.ZERO)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(256))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(353))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(457))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(528))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "01"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(3)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(528))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(849))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1167))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1513))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1749))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "02"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(8)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(1749))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "03"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(13)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(2272))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "03"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(13)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(2716))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "03"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(13)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(3199))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "03"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(13)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(3528))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "03"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(13)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(3528))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "04"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "5")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(15)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(2852))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "04"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "10")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(15)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(3410))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "04"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "14")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(15)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(4016))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "04"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "18")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(15)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(4429))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.SAMVAERSFRADRAG.getNavn(),
            Arrays.asList(new SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.getNavn(), "04"),
                new SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.getNavn(), "99")),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.getNavn(), BigDecimal.ZERO),
                new SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.getNavn(), BigDecimal.valueOf(15)),
                new SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.getNavn(), BigDecimal.valueOf(4429))))));

    // Sjablontall
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1054))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.ORDINAER_SMAABARNSTILLEGG_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.ZERO)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(2825))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(31))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(87450))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(51300))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(51300))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(22))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.TRYGDEAVGIFT_PROSENT.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(8.2))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(12977))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.ZERO)))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(3841))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(25.05))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1670))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(5667))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(2334))))));

    // Trinnvis skattesats
    // Oppdatert med sjablonverdier gyldig fra 01.07.2020
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(), BigDecimal.valueOf(999550)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(), BigDecimal.valueOf(16.2))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(), BigDecimal.valueOf(254500)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(), BigDecimal.valueOf(4.2))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(), BigDecimal.valueOf(639750)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(), BigDecimal.valueOf(13.2))))));
    sjablonPeriodeListe.add(new SjablonPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
        new Sjablon(SjablonNavn.TRINNVIS_SKATTESATS.getNavn(), emptyList(),
            Arrays.asList(new SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.getNavn(), BigDecimal.valueOf(180800)),
                new SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.getNavn(), BigDecimal.valueOf(1.9))))));

    return sjablonPeriodeListe;
  }
}
