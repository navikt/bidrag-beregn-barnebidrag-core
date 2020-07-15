# bidrag-beregn-felles
![](https://github.com/navikt/bidrag-beregn-barnebidrag-core/workflows/maven%20deploy/badge.svg)

Repo for beregning av barnebidrag-core. Disse erstatter beregninger i BBM.

## Changelog:

Versjon | Endringstype | Beskrivelse
--------|--------------|------------
0.2.1   | Endret       | Lagt inn periodisering for beregning av netto barnetilsyn
0.2.0   | Endret       | Endret sjablonPeriodeListe i DTO fra val til var for å få tilgang til setter
0.1.1   | Endret       | Første basic commit for beregning av Netto Barnetilsyn
0.1.0   | Opprettet    | Beregning av underholdskostnad klar for videre test via resttjeneste
0.0.5   | Endret       | Lagt til ekstra test på periodisering og mapping til Core
0.0.4   | Endret       | Logikk for periodisering og mapping mot Core lagt til
0.0.3   | Endret       | Alle beregninger er nå med, bortsett fra netto barnetilsyn
0.0.2   | Endret       | Beregner nå med forbruksutgifter, boutgifter og barnetilsyn med stønad
0.0.1   | Opprettet    | Init commit for beregning av underholdskostnad'