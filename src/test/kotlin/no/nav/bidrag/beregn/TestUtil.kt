package no.nav.bidrag.beregn

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonNokkelNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import java.math.BigDecimal
import java.time.LocalDate

object TestUtil {
    const val INNTEKT_REFERANSE = "INNTEKT_REFERANSE"
    const val INNTEKT_BP_REFERANSE = "INNTEKT_BP_REFERANSE"
    const val INNTEKT_BM_REFERANSE = "INNTEKT_BM_REFERANSE"
    const val INNTEKT_BB_REFERANSE = "INNTEKT_BB_REFERANSE"
    const val SKATTEKLASSE_REFERANSE = "SKATTEKLASSE_REFERANSE"
    const val BOSTATUS_REFERANSE = "BOSTATUS_REFERANSE"
    const val BARN_I_HUSSTAND_REFERANSE = "BARN_I_HUSSTAND_REFERANSE"
    const val SAERFRADRAG_REFERANSE = "SAERFRADRAG_REFERANSE"
    const val FAKTISK_UTGIFT_REFERANSE = "FAKTISK_UTGIFT_REFERANSE"
    const val SOKNADSBARN_REFERANSE = "SOKNADSBARN_REFERANSE"
    const val BARNETILSYN_MED_STONAD_REFERANSE = "BARNETILSYN_MED_STONAD_REFERANSE"
    const val NETTO_BARNETILSYN_REFERANSE = "NETTO_BARNETILSYN_REFERANSE"
    const val FORPLEINING_UTGIFT_REFERANSE = "FORPLEINING_UTGIFT_REFERANSE"
    const val UNDERHOLDSKOSTNAD_REFERANSE = "UNDERHOLDSKOSTNAD_REFERANSE"
    const val SAMVAERSKLASSE_REFERANSE = "SAMVAERSKLASSE_REFERANSE"
    const val SAMVAERSFRADRAG_REFERANSE = "SAMVAERSFRADRAG_REFERANSE"
    const val BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE = "BP_ANDEL_UNDERHOLDSKOSTNAD_REFERANSE"
    const val BIDRAGSEVNE_REFERANSE = "BIDRAGSEVNE_REFERANSE"
    const val DELT_BOSTED_REFERANSE = "DELT_BOSTED_REFERANSE"
    const val BARNETILLEGG_BP_REFERANSE = "BARNETILLEGG_BP_REFERANSE"
    const val BARNETILLEGG_BM_REFERANSE = "BARNETILLEGG_BM_REFERANSE"
    const val BARNETILLEGG_FORSVARET_REFERANSE = "BARNETILLEGG_FORSVARET_REFERANSE"
    const val ANDRE_LOPENDE_BIDRAG_REFERANSE = "ANDRE_LOPENDE_BIDRAG_REFERANSE"
    const val ORDINAER_BARNETRYGD = "O"

    fun byggSjabloner(): List<Sjablon> {
        val sjablonListe = ArrayList<Sjablon>()

        // Barnetilsyn
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonListe.add(
            Sjablon(
                SjablonNavn.BARNETILSYN.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.navn, "64"),
                    SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.navn, "DO")
                ),
                listOf(SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.navn, BigDecimal.valueOf(358)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.BARNETILSYN.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.navn, "64"),
                    SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.navn, "DU")
                ),
                listOf(SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.navn, BigDecimal.valueOf(257)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.BARNETILSYN.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.navn, "64"),
                    SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.navn, "HO")
                ),
                listOf(SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.navn, BigDecimal.valueOf(589)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.BARNETILSYN.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.navn, "64"),
                    SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.navn, "HU")
                ),
                listOf(SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.navn, BigDecimal.valueOf(643)))
            )
        )

        // Bidragsevne
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonListe.add(
            Sjablon(
                SjablonNavn.BIDRAGSEVNE.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.BOSTATUS.navn, "EN")),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.navn, BigDecimal.valueOf(9764)),
                    SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.navn, BigDecimal.valueOf(9818))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.BIDRAGSEVNE.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.BOSTATUS.navn, "GS")),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.navn, BigDecimal.valueOf(5981)),
                    SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.navn, BigDecimal.valueOf(8313))
                )
            )
        )

        // Forbruksutgifter
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonListe.add(
            Sjablon(
                SjablonNavn.FORBRUKSUTGIFTER.navn,
                listOf(
                    SjablonNokkel(
                        SjablonNokkelNavn.ALDER_TOM.navn,
                        "18"
                    )
                ),
                listOf(SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, BigDecimal.valueOf(7953)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.FORBRUKSUTGIFTER.navn,
                listOf(
                    SjablonNokkel(
                        SjablonNokkelNavn.ALDER_TOM.navn,
                        "5"
                    )
                ),
                listOf(SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, BigDecimal.valueOf(4228)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.FORBRUKSUTGIFTER.navn,
                listOf(
                    SjablonNokkel(
                        SjablonNokkelNavn.ALDER_TOM.navn,
                        "99"
                    )
                ),
                listOf(SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, BigDecimal.valueOf(7953)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.FORBRUKSUTGIFTER.navn,
                listOf(
                    SjablonNokkel(
                        SjablonNokkelNavn.ALDER_TOM.navn,
                        "10"
                    )
                ),
                listOf(SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, BigDecimal.valueOf(5710)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.FORBRUKSUTGIFTER.navn,
                listOf(
                    SjablonNokkel(
                        SjablonNokkelNavn.ALDER_TOM.navn,
                        "14"
                    )
                ),
                listOf(SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, BigDecimal.valueOf(6913)))
            )
        )

        // Maks fradrag
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_FRADRAG.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "1")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(2083.33)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_FRADRAG.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "2")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(3333)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_FRADRAG.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "3")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(4583)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_FRADRAG.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "4")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(5833)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_FRADRAG.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "5")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(7083)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_FRADRAG.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "6")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(8333)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_FRADRAG.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "7")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(9583)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_FRADRAG.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "8")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(10833)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_FRADRAG.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "99")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(12083)))
            )
        )

        // Maks tilsyn
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_TILSYN.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "1")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.navn, BigDecimal.valueOf(6333)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_TILSYN.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "2")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.navn, BigDecimal.valueOf(8264)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.MAKS_TILSYN.navn,
                listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "99")),
                listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.navn, BigDecimal.valueOf(9364)))
            )
        )

        // Samvaersfradrag
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "00"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(1)),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(1)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.ZERO)
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "01"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "5")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(3)),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(3)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(256))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "01"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "10")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(3)),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(3)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(353))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "01"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "14")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(3)),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(3)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(457))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "01"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "18")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(3)),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(3)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(528))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "01"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(3)),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(3)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(528))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "02"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "5")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(8)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(849))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "02"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "10")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(8)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(1167))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "02"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "14")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(8)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(1513))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "02"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "18")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(8)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(1749))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "02"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(8)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(1749))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "03"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "5")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(13)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(2272))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "03"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "10")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(13)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(2716))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "03"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "14")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(13)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(3199))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "03"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "18")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(13)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(3528))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "03"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(13)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(3528))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "04"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "5")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(15)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(2852))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "04"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "10")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(15)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(3410))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "04"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "14")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(15)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(4016))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "04"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "18")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(15)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(4429))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.SAMVAERSFRADRAG.navn,
                listOf(
                    SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "04"),
                    SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")
                ),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                    SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(15)),
                    SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(4429))
                )
            )
        )

        // Sjablontall
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(1054)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.ORDINAER_SMAABARNSTILLEGG_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.ZERO))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(2825)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(31)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(87450)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(51300)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(51300)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(22)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.TRYGDEAVGIFT_PROSENT.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(8.2)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(12977)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.ZERO))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(3841)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(25.05)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(1670)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(5667)))
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.navn,
                emptyList(),
                listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(2334)))
            )
        )

        // Trinnvis skattesats
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonListe.add(
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn, BigDecimal.valueOf(999550)),
                    SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.navn, BigDecimal.valueOf(16.2))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn, BigDecimal.valueOf(254500)),
                    SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.navn, BigDecimal.valueOf(4.2))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn, BigDecimal.valueOf(639750)),
                    SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.navn, BigDecimal.valueOf(13.2))
                )
            )
        )
        sjablonListe.add(
            Sjablon(
                SjablonNavn.TRINNVIS_SKATTESATS.navn,
                emptyList(),
                listOf(
                    SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn, BigDecimal.valueOf(180800)),
                    SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.navn, BigDecimal.valueOf(1.9))
                )
            )
        )
        return sjablonListe
    }

    fun byggSjablonPeriodeListe(): List<SjablonPeriode> {
        val sjablonPeriodeListe = ArrayList<SjablonPeriode>()

        // Barnetilsyn
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.BARNETILSYN.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.navn, "64"),
                        SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.navn, "DO")
                    ),
                    listOf(SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.navn, BigDecimal.valueOf(358)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.BARNETILSYN.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.navn, "64"),
                        SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.navn, "DU")
                    ),
                    listOf(SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.navn, BigDecimal.valueOf(257)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.BARNETILSYN.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.navn, "64"),
                        SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.navn, "HO")
                    ),
                    listOf(SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.navn, BigDecimal.valueOf(589)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.BARNETILSYN.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.STONAD_TYPE.navn, "64"),
                        SjablonNokkel(SjablonNokkelNavn.TILSYN_TYPE.navn, "HU")
                    ),
                    listOf(SjablonInnhold(SjablonInnholdNavn.BARNETILSYN_BELOP.navn, BigDecimal.valueOf(643)))
                )
            )
        )

        // Bidragsevne
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.BIDRAGSEVNE.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.BOSTATUS.navn, "EN")),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.navn, BigDecimal.valueOf(9764)),
                        SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.navn, BigDecimal.valueOf(9818))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.BIDRAGSEVNE.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.BOSTATUS.navn, "GS")),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.BOUTGIFT_BELOP.navn, BigDecimal.valueOf(5981)),
                        SjablonInnhold(SjablonInnholdNavn.UNDERHOLD_BELOP.navn, BigDecimal.valueOf(8313))
                    )
                )
            )
        )

        // Forbruksutgifter
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.FORBRUKSUTGIFTER.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "18")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, BigDecimal.valueOf(7953)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.FORBRUKSUTGIFTER.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "5")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, BigDecimal.valueOf(4228)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.FORBRUKSUTGIFTER.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, BigDecimal.valueOf(7953)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.FORBRUKSUTGIFTER.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "10")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, BigDecimal.valueOf(5710)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.FORBRUKSUTGIFTER.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "14")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.FORBRUK_TOTAL_BELOP.navn, BigDecimal.valueOf(6913)))
                )
            )
        )

        // Maks fradrag
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_FRADRAG.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "1")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(2083.33)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_FRADRAG.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "2")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(3333)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_FRADRAG.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "3")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(4583)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_FRADRAG.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "4")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(5833)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_FRADRAG.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "5")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(7083)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_FRADRAG.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "6")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(8333)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_FRADRAG.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "7")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(9583)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_FRADRAG.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "8")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(10833)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_FRADRAG.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "99")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_FRADRAG_BELOP.navn, BigDecimal.valueOf(12083)))
                )
            )
        )

        // Maks tilsyn
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_TILSYN.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "1")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.navn, BigDecimal.valueOf(6333)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_TILSYN.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "2")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.navn, BigDecimal.valueOf(8264)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.MAKS_TILSYN.navn,
                    listOf(SjablonNokkel(SjablonNokkelNavn.ANTALL_BARN_TOM.navn, "99")),
                    listOf(SjablonInnhold(SjablonInnholdNavn.MAKS_TILSYN_BELOP.navn, BigDecimal.valueOf(9364)))
                )
            )
        )

        // Samvaersfradrag
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "00"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(1)),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(1)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.ZERO)
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "01"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "5")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(3)),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(3)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(256))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "01"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "10")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(3)),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(3)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(353))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "01"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "14")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(3)),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(3)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(457))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "01"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "18")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(3)),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(3)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(528))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "01"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.valueOf(3)),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(3)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(528))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "02"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "5")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(8)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(849))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "02"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "10")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(8)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(1167))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "02"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "14")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(8)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(1513))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "02"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "18")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(8)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(1749))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "02"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(8)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(1749))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "03"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "5")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(13)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(2272))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "03"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "10")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(13)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(2716))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "03"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "14")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(13)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(3199))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "03"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "18")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(13)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(3528))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "03"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(13)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(3528))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "04"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "5")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(15)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(2852))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "04"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "10")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(15)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(3410))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "04"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "14")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(15)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(4016))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "04"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "18")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(15)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(4429))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.SAMVAERSFRADRAG.navn,
                    listOf(
                        SjablonNokkel(SjablonNokkelNavn.SAMVAERSKLASSE.navn, "04"),
                        SjablonNokkel(SjablonNokkelNavn.ALDER_TOM.navn, "99")
                    ),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_DAGER_TOM.navn, BigDecimal.ZERO),
                        SjablonInnhold(SjablonInnholdNavn.ANTALL_NETTER_TOM.navn, BigDecimal.valueOf(15)),
                        SjablonInnhold(SjablonInnholdNavn.FRADRAG_BELOP.navn, BigDecimal.valueOf(4429))
                    )
                )
            )
        )

        // Sjablontall
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.ORDINAER_BARNETRYGD_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(1054)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.ORDINAER_SMAABARNSTILLEGG_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.ZERO))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.BOUTGIFTER_BIDRAGSBARN_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(2825)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.MINSTEFRADRAG_INNTEKT_PROSENT.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(31)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.MINSTEFRADRAG_INNTEKT_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(87450)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.PERSONFRADRAG_KLASSE1_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(51300)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.PERSONFRADRAG_KLASSE2_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(51300)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.SKATTESATS_ALMINNELIG_INNTEKT_PROSENT.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(22)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.TRYGDEAVGIFT_PROSENT.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(8.2)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.FORDEL_SAERFRADRAG_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(12977)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.FORDEL_SKATTEKLASSE2_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.ZERO))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.UNDERHOLD_EGNE_BARN_I_HUSSTAND_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(3841)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.SKATT_ALMINNELIG_INNTEKT_PROSENT.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(25.05)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.FORSKUDDSSATS_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(1670)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.BARNETILLEGG_FORSVARET_FORSTE_BARN_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(5667)))
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonTallNavn.BARNETILLEGG_FORSVARET_OVRIGE_BARN_BELOP.navn,
                    emptyList(),
                    listOf(SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.navn, BigDecimal.valueOf(2334)))
                )
            )
        )

        // Trinnvis skattesats
        // Oppdatert med sjablonverdier gyldig fra 01.07.2020
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    emptyList(),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn, BigDecimal.valueOf(999550)),
                        SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.navn, BigDecimal.valueOf(16.2))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    emptyList(),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn, BigDecimal.valueOf(254500)),
                        SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.navn, BigDecimal.valueOf(4.2))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    emptyList(),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn, BigDecimal.valueOf(639750)),
                        SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.navn, BigDecimal.valueOf(13.2))
                    )
                )
            )
        )
        sjablonPeriodeListe.add(
            SjablonPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("9999-12-31")),
                Sjablon(
                    SjablonNavn.TRINNVIS_SKATTESATS.navn,
                    emptyList(),
                    listOf(
                        SjablonInnhold(SjablonInnholdNavn.INNTEKTSGRENSE_BELOP.navn, BigDecimal.valueOf(180800)),
                        SjablonInnhold(SjablonInnholdNavn.SKATTESATS_PROSENT.navn, BigDecimal.valueOf(1.9))
                    )
                )
            )
        )
        return sjablonPeriodeListe
    }
}
