package no.nav.bidrag.beregn.felles

import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import java.math.BigDecimal
import java.time.LocalDate

open class FellesBeregning {

    // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
    protected fun byggSjablonResultatListe(
        sjablonNavnVerdiMap: Map<String, BigDecimal>,
        sjablonPeriodeListe: List<SjablonPeriode>
    ): List<SjablonPeriodeNavnVerdi> {
        val sjablonPeriodeNavnVerdiListe = mutableListOf<SjablonPeriodeNavnVerdi>()
        sjablonNavnVerdiMap.forEach { (sjablonNavn: String, sjablonVerdi: BigDecimal) ->
            sjablonPeriodeNavnVerdiListe.add(
                SjablonPeriodeNavnVerdi(
                    periode = hentPeriode(sjablonPeriodeListe = sjablonPeriodeListe, sjablonNavn = sjablonNavn),
                    navn = sjablonNavn,
                    verdi = sjablonVerdi
                )
            )
        }

        return sjablonPeriodeNavnVerdiListe.sortedBy { it.navn }
    }

    private fun hentPeriode(sjablonPeriodeListe: List<SjablonPeriode>, sjablonNavn: String): Periode {
        return sjablonPeriodeListe
            .firstOrNull { it.sjablon.navn == modifiserSjablonNavn(sjablonNavn) }?.getPeriode() ?: Periode(LocalDate.MIN, LocalDate.MAX)
    }

    // Enkelte sjablonnavn må justeres for å finne riktig dato
    private fun modifiserSjablonNavn(sjablonNavn: String): String {
        return when (sjablonNavn) {
            "UnderholdBeløp" -> {
                "Bidragsevne"
            }

            "BoutgiftBeløp" -> {
                "Bidragsevne"
            }

            else -> if (sjablonNavn.startsWith("Trinnvis")) {
                "TrinnvisSkattesats"
            } else {
                sjablonNavn
            }
        }
    }
}
