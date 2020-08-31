# bidrag-beregn-barnebidrag-core
![](https://github.com/navikt/bidrag-beregn-barnebidrag-core/workflows/maven%20deploy/badge.svg)

Repo for beregning av barnebidrag-core. Disse erstatter beregninger i BBM.

## Changelog:

Versjon | Endringstype | Beskrivelse
--------|--------------|------------
0.6.0   | Endret       | Beregning av samværsfradrag lagt til
0.5.0   | Endret       | Endret sjablonPeriodeListe i DTO for samværsfradrag fra val til var for å få tilgang til setter
0.4.3   | Endret       | Forbedret input-kontroll på datoer for samværsfradrag og barnebidrag
0.4.2   | Endret       | Forbedret input-kontroll på datoer
0.4.1   | Endret       | Beregning av samværsfradrag lagt til
0.4.0   | Endret       | Endring for Beregn-til-dato lagt inn for alle beregninger. Beregning av BPs andel av underholdskostnad lagt til.
0.3.4   | Endret       | Beregn-til-dato legges nå med i perioder som skal beregnes for å sikre minst én resultatperiode der det er ingen bruddpunkter i andre parametre
0.3.3   | Endret       | Logikk lagt til for å håndtere to sett med regler
0.3.2   | Endret       | Slått av test på overlapp og opphold i faktisk utgift-perioder pga flere barn i input
0.3.1   | Endret       | Lagt til test på summering av faktiske utgifter per barn
0.3.0   | Endret       | Forenklet DTO for netto barnetilsyn og åpnet for set-metoder på noen av input-variablene
0.2.6   | Endret       | Lagt til summering av faktiske utgifter pr barn i beregning
0.2.5   | Endret       | Rettet navn i resultat på beregning av underholdskostnad
0.2.4   | Endret       | Litt flere tester på periodisering
0.2.3   | Endret       | Ryddet i koden og lagt til noen flere tester under periodisering
0.2.2   | Endret       | Rettet feil, forpleiningsutgifter skal trekkes fra i beregning av underholdskostnad, ikke legges til
0.2.1   | Endret       | Lagt inn periodisering for beregning av netto barnetilsyn
0.2.0   | Endret       | Endret sjablonPeriodeListe i DTO fra val til var for å få tilgang til setter
0.1.1   | Endret       | Første basic commit for beregning av Netto Barnetilsyn
0.1.0   | Opprettet    | Beregning av underholdskostnad klar for videre test via resttjeneste
0.0.5   | Endret       | Lagt til ekstra test på periodisering og mapping til Core
0.0.4   | Endret       | Logikk for periodisering og mapping mot Core lagt til
0.0.3   | Endret       | Alle beregninger er nå med, bortsett fra netto barnetilsyn
0.0.2   | Endret       | Beregner nå med forbruksutgifter, boutgifter og barnetilsyn med stønad
0.0.1   | Opprettet    | Init commit for beregning av underholdskostnad'