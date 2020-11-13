# bidrag-beregn-barnebidrag-core
![](https://github.com/navikt/bidrag-beregn-barnebidrag-core/workflows/maven%20deploy/badge.svg)

Repo for beregning av barnebidrag-core. Disse erstatter beregninger i BBM.

## Changelog:

Versjon | Endringstype | Beskrivelse
--------|--------------|------------
0.11.3  | Endret       | Erstattet Double med BigDecimal i resterende testklasser
0.11.2  | Endret       | Tatt inn ny versjon av bidrag-beregn-felles og justert noen tester angående inntekter
0.11.1  | Endret       | Sortering av sjabloner som legges ut i resultatgrunnlaget
0.11.0  | Endret       | Forsøk på å fikse problem med utilgjengelige Kotlin dataklasser (ingen endringer, kun ny versjon)
0.10.0  | Endret       | Endret måten sjabloner legges ut på i resultatgrunnlaget (inneholder nå navn/verdi på de som faktisk er brukt)
0.9.1   | Endret       | System.out.println kommentert ut i alle delberegninger 
0.9.0   | Endret       | Alle delberegninger skrevet om til å bruke BigDecimal, sjablonverdier også endret til å mottas som BigDecimal
0.8.9   | Endret       | Beregning av bidragsevne skrevet om til å bruke BigDecimal i stedet for Double, sjabloner er ikke skrevet om ennå
0.8.8   | Endret       | Rettet feil i validering av inntekter
0.8.7   | Opprettet    | Lagt til validering av inntekter
0.8.6   | Endret       | Feilretting i beregning av netto barnetilsyn
0.8.5   | Endret       | Feilretting i BPsAndelUnderholdskostnadCoreImpl
0.8.4   | Endret       | Endret beregning av netto barnetilsyn til å også beregne for barn over 12 år, resultatbeløp settes da til 0.-
0.8.3   | Endret       | Lagt til bryter for å angi at barnet er selvforsørget, bidrag skal da ikke beregnes
0.8.2   | Endret       | Rettet feil i logikk rundt barnetilleggBM
0.8.1   | Endret       | Endret til å sette maks BPs andel av underholdskostnad til 83,3333333333, slik det ligger i Bidragskalkulator
0.8.0   | Endret       | Totalberegning av barnebidrag lagt til
0.7.7   | Endret       | Feilfiks i coredto: Periode -> PeriodeCore
0.7.6   | Endret       | Endringer i input og output beregning av barnebidrag
0.7.5   | Endret       | Endret til å ha samme navn på grunnlagsklasser, samt noen feilfikser og tilpasninger
0.7.4   | Endret       | Lagt til personId for søknadsbarn i input/output for alle delberegninger unntatt beregning av bidragsevne
0.7.3   | Endret       | Rettet feil i håndtering av barn med 0 i tilsynsutgift ved beregning av netto tilsynsutgift
0.7.2   | Endret       | Inntekter inn til beregning av BPs andel av U er nå lister. Underholdskostnad også lagt til i input for samme beregning
0.7.1   | Endret       | Logikk for å beregne underholdskostnad med forhøyet barnetrygd for barn < 6 år lagt til fra 01.07.2021
0.7.0   | Endret       | Beregning av bidragsevne flyttet fra bidrag-beregn-felles, 25% av inntekt lagt til i output fra bidragsevne
0.6.0   | Endret       | Beregning av kostnadsberegnet bidrag lagt til
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
0.0.1   | Opprettet    | Init commit for beregning av underholdskostnad
