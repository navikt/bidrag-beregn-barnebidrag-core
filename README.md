# bidrag-beregn-barnebidrag-core
![](https://github.com/navikt/bidrag-beregn-barnebidrag-core/workflows/maven%20deploy/badge.svg)

Repo for beregning av barnebidrag-core. Det gjøres seks delberegninger der resultatet går inn i en endelig beregning av barnebidrag.
Disse erstatter beregninger i BBM.
<br>
<br>Disse beregningene gjøres:

<b>BeregnBidragsevne - Returnerer periodisert liste med BPs bidragsevne</b>

| Felt                               | Kilde          | Beskrivelse                                                                                                   |
|------------------------------------|----------------|---------------------------------------------------------------------------------------------------------------|
| beregnDatoFra                      | Bisys          | Dato satt i Bisys, beregner fra denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| beregnDatoTil                      | Bisys          | Dato satt i Bisys, beregner til denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| inntektPeriodeListe                | Bisys          | Liste med BPs inntekter, periodisert                                                                          |
| skatteklassePeriodeListe           | Bisys          | Liste med skatteklasse for BP, periodisert                                                                    |
| bostatusPeriodeListe               | Bisys          | Liste med BPs bostatus, periodisert                                                                           |
| antallBarnIEgetHusholdPeriodeListe | Bisys          | Liste med antall barn i BPs husholdning, periodisert                                                          |
| saerfradragPeriodeListe            | Bisys          | Liste over særfradrag, periodisert                                                                            |
| sjablonPeriodeListe                | bidrag-sjablon | Sjabloner for beregningsperioden                                                                              |

<br>
<b>BeregnNettoBarnetilsyn - Returnerer periodisert liste med beregnet netto barnetilsyn for alle barn i søknaden</b>

| Felt                      | Kilde          | Beskrivelse                                                                                                   |
|---------------------------|----------------|---------------------------------------------------------------------------------------------------------------|
| beregnDatoFra             | Bisys          | Dato satt i Bisys, beregner fra denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| beregnDatoTil             | Bisys          | Dato satt i Bisys, beregner til denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| faktiskUtgiftPeriodeListe | Bisys          | Liste med personId, fødselsdato for  og beløp med faktiske utgifter for alle barn i søknaden, periodisert     |
| sjablonPeriodeListe       | bidrag-sjablon | Sjabloner for beregningsperioden                                                                              |

<br>
<b>BeregnUnderholdskostnad - Returnerer periodisert liste med beregnet underholdskostnad for angitt barn</b>

| Felt                             | Kilde                  | Beskrivelse                                                                                                   |
|----------------------------------|------------------------|---------------------------------------------------------------------------------------------------------------|
| soknadsbarnPersonId              | Bisys                  | PersonId for søknadsbarnet                                                                                    |
| beregnDatoFra                    | Bisys                  | Dato satt i Bisys, beregner fra denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| beregnDatoTil                    | Bisys                  | Dato satt i Bisys, beregner til denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| soknadBarnFodselsdato            | Bisys                  | Fødselsdato for angitt barn, barnets alder brukes i innhenting av sjablonverdier                              |
| barnetilsynMedStonadPeriodeListe | Bisys                  | Tilsyntype og stønadtype (fra Infotrygd per 2020), brukes for å hente sjablonverdier, periodisert             |
| nettoBarnetilsynPeriodeListe     | beregnNettoBarnetilsyn | Beregnet netto barnetilsynbeløp, periodisert                                                                  |
| forpleiningUtgiftPeriodeListe    | Bisys                  | Forpleiningsutgifter, periodisert                                                                             |
| sjablonPeriodeListe              | bidrag-sjablon         | Sjabloner for beregningsperioden                                                                              |

<br>
<b>BeregnBPsAndelUnderholdskostnad - Returnerer periodisert liste med BPs andel av underholdskostnad for angitt barn</b>

| Felt                          | Kilde                   | Beskrivelse                                                                                                   |
|-------------------------------|-------------------------|---------------------------------------------------------------------------------------------------------------|
| beregnDatoFra                 | Bisys                   | Dato satt i Bisys, beregner fra denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| beregnDatoTil                 | Bisys                   | Dato satt i Bisys, beregner til denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| soknadsbarnPersonId           | Bisys                   | PersonId for angitt barn                                                                                      |
| underholdskostnadPeriodeListe | beregnUnderholdskostnad | Liste med beregnet underholdskostnad for angitt barn, periodisert.                                            |
| inntektBPPeriodeListe         | Bisys                   | Liste med inntekter for BP, periodisert                                                                       |
| inntektBMPeriodeListe         | Bisys                   | Liste med inntekter for BM, periodisert                                                                       |
| inntektBBPeriodeListe         | Bisys                   | Liste med inntekter for BB (bidragsbarn), periodisert                                                         |
| sjablonPeriodeListe           | bidrag-sjablon          | Sjabloner for beregningsperioden                                                                              |

<br>
<b>BeregnSamvaersfradrag - Returnerer periodisert liste med beregnede samværsfradragbeløp for angitt barn</b>

| Felt                       | Kilde          | Beskrivelse                                                                                                   |
|----------------------------|----------------|---------------------------------------------------------------------------------------------------------------|
| beregnDatoFra              | Bisys          | Dato satt i Bisys, beregner fra denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| beregnDatoTil              | Bisys          | Dato satt i Bisys, beregner til denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| soknadsbarnPersonId        | Bisys          | PersonId for angitt barn                                                                                      |
| soknadsbarnFodselsdato     | Bisys          | Fødselsdato for angitt barn                                                                                   |
| samvaersklassePeriodeListe | Bisys          | Liste med samværsklasser for søknadsbarn, periodisert                                                         |
| sjablonPeriodeListe        | bidrag-sjablon | Sjabloner for beregningsperioden                                                                              |

<br>
<b>BeregnKostnadsberegnetBidrag - Returnerer periodisert liste med beregnet kostnadsberegnet bidrag - denne kan antagelig fjernes</b>

| Felt                                  | Kilde                           | Beskrivelse                                                                                                   |
|---------------------------------------|---------------------------------|---------------------------------------------------------------------------------------------------------------|
| beregnDatoFra                         | Bisys                           | Dato satt i Bisys, beregner fra denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| beregnDatoTil                         | Bisys                           | Dato satt i Bisys, beregner til denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| soknadsbarnPersonId                   | Bisys                           | PersonId for angitt barn                                                                                      |
| underholdskostnadPeriodeListe         | beregnUnderholdskostnad         | Liste med beregnet underholdskostnad for angitt barn, periodisert.                                            |
| bPsAndelUnderholdskostnadPeriodeListe | beregnBPsAndelUnderholdskostnad | Liste med beregnet BPs andel av underholdskostnad for angitt barn, periodisert                                |
| samvaersfradragPeriodeListe           | beregnSamvaersfradrag           | Liste med beregnet samværsfradrag for angitt barn, periodisert                                                |
| sjablonPeriodeListe                   | bidrag-sjablon                  | Sjabloner for beregningsperioden                                                                              |

<br>
<b>BeregnBarnebidrag - Sluttberegning som returnerer barnebidragsberegningsresultat for alle barn i søknaden med tilhørende resultatkode. Data fra delberegninger for alle barn i saken sendes inn som input</b>

| Felt                                  | Kilde                           | Beskrivelse                                                                                                                                                                                   |
|---------------------------------------|---------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| beregnDatoFra                         | Bisys                           | Dato satt i Bisys, beregner fra denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene                                                                                 |
| beregnDatoTil                         | Bisys                           | Dato satt i Bisys, beregner til denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene                                                                                 |
| bidragsevnePeriodeListe               | beregnBidragsevne               | Liste med BPs bidragsevne, periodisert                                                                                                                                                        |
| bPsAndelUnderholdskostnadPeriodeListe | beregnBPsAndelUnderholdskostnad | Liste med BPs andel av underholdskostnad, periodisert                                                                                                                                         |
| samvaersfradragPeriodeListe           | beregnSamvaersfradrag           | Liste med beregnede samværsfradrag for alle barn i søknaden, periodisert                                                                                                                      |
| deltBostedPeriodeListe                | Bisys                           | Liste som sier om det er delt bosted eller ikke for alle barn i søknaden, periodisert. Delt bosted innebærer at beregnet bidrag reduseres med 50%                                             |
| barnetilleggBPPeriodeListe            | Bisys                           | Liste med beløp og skatteprosent for barnetillegg for BP, periodisert                                                                                                                         |
| barnetilleggBMPeriodeListe            | Bisys                           | Liste med beløp og skatteprosent for barnetillegg for BM, periodisert                                                                                                                         |
| barnetilleggForsvaretPeriodeListe     | Bisys                           | Liste som sier om det finnes barnetillegg fra forsvaret eller ikke, periodisert, hvis ja så overstyres all utregning av barnebidrag i perioden                                                |
| andreLopendeBidragPeriodeListe        | Bisys                           | Liste med bidragsbeløp og samværsfradrag for alle andre av BPs saker. Disse beløpene går inn i vurdering av bidragsevne og brukes for å finne ut om det må gjøres en forholdsmessig fordeling |
| sjablonPeriodeListe                   | bidrag-sjablon                  | Sjabloner for beregningsperioden                                                                                                                                                              |

<br>
<b>BeregnForholdsmessigFordeling - Delberegning som kalles ved behov etter å ha beregnet alle bidragssaker på nytt. Returnerer nye bidragsbeløp forholdsmessig fordelt mellom alle saker</b>

| Felt                       | Kilde             | Beskrivelse                                                                                                   |
|----------------------------|-------------------|---------------------------------------------------------------------------------------------------------------|
| beregnDatoFra              | Bisys             | Dato satt i Bisys, beregner fra denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| beregnDatoTil              | Bisys             | Dato satt i Bisys, beregner til denne datoen, kan bli flere resultatperioder ved endringer i grunnlagsdataene |
| bidragsevnePeriodeListe    | beregnBidragsevne | Liste med BPs bidragsevne, periodisert                                                                        |
| beregnetBidragPeriodeListe | Bisys             | Periodisert liste med alle saker som skal inngå i forholdsmessig fordeling                                    |

## Changelog:

| Versjon | Endringstype | Beskrivelse                                                                                                                                     |
|---------|--------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| 0.18.8  | Endret       | Oppdaterte avhegigheter (Snyk)                                                                                                                  |
| 0.18.7  | Endret       | Endret andelProsent av BPsAndelUnderholdskostnad til å oppgis som et tall mellom 0 og 1                                                         |
| 0.18.6  | Endret       | Testing av opplasting til Nexus                                                                                                                 |
| 0.18.5  | Endret       | Ny versjon av bidrag-beregn-felles og BarnetilleggBM og BarnetilleggBP gjort valgfrie (optional)                                                |
| 0.18.4  | Endret       | Ny versjon av bidrag-beregn-felles                                                                                                              |
| 0.18.3  | Endret       | Ved bruk av forhøyet barnetrygd skal fødselsdato settes til 1/7                                                                                 |
| 0.18.2  | Endret       | Oppdatert til Java 17 + oppdatert andre avhengigheter (nytt forsøk)                                                                             |
| 0.18.1  | Endret       | Oppdatert til Java 17 + oppdatert andre avhengigheter                                                                                           |
| 0.18.0  | Endret       | Endret dto og bo for forholdsmessig fordeling                                                                                                   |
| 0.17.0  | Endret       | Fjernet liste i variabelnavn i dto                                                                                                              |
| 0.16.0  | Endret       | Endret dto for barnebidrag og netto barnetilsyn (barn flyttet ett nivå opp)                                                                     |
| 0.15.2  | Endret       | Lagt inn sortering på referanselister i output fra core                                                                                         |
| 0.15.1  | Endret       | Liten endring for å finne riktig dato på sjabloner + noen kosmetiske endringer                                                                  |
| 0.15.0  | Endret       | Oppdatert beregninger (referanser, sjabloner, inntekter). Enklere navn på felter. Omstrukturert tester++                                        |
| 0.14.4  | Endret       | Lagt til test fra John med flere barn per sak i forholdsmessig fordeling                                                                        |
| 0.14.3  | Endret       | Rettet feil i mapping av AndreLopendeBidrag                                                                                                     |
| 0.14.2  | Endret       | Flere tester av forholdsmessig fordeling periodisering pluss tester fra John                                                                    |
| 0.14.1  | Endret       | Rettet feil og lagt til flere tester forholdsmessig fordeling                                                                                   |
| 0.14.0  | Endret       | Ny delberegning, Forholdsmessig fordeling                                                                                                       |
| 0.13.3  | Endret       | Endret avrunding for kostnadsberegnet bidrag                                                                                                    |
| 0.13.2  | Endret       | Lagt til beskrivelse av beregningene som gjøres                                                                                                 |
| 0.13.1  | Endret       | Rettet feil logikk returkode for delt bosted                                                                                                    |
| 0.13.0  | Endret       | Endret antallBarnIEgenHusstand fra Integer til BigDecimal                                                                                       |
| 0.12.2  | Endret       | Kluss med versjoner                                                                                                                             |
| 0.12.1  | Endret       | Ny versjon av bidrag-beregn-felles. Rettet test som feilet pga strengere kontroll av inntektstyper.                                             |
| 0.12.0  | Opprettet    | Lagt til logikk for utvidet barnetrygd (inntekt BM). Utvidet Inntekt DTO og BO med nye felter.                                                  |
| 0.11.3  | Endret       | Erstattet Double med BigDecimal i resterende testklasser                                                                                        |
| 0.11.2  | Endret       | Tatt inn ny versjon av bidrag-beregn-felles og justert noen tester angående inntekter                                                           |
| 0.11.1  | Endret       | Sortering av sjabloner som legges ut i resultatgrunnlaget                                                                                       |
| 0.11.0  | Endret       | Forsøk på å fikse problem med utilgjengelige Kotlin dataklasser (ingen endringer, kun ny versjon)                                               |
| 0.10.0  | Endret       | Endret måten sjabloner legges ut på i resultatgrunnlaget (inneholder nå navn/verdi på de som faktisk er brukt)                                  |
| 0.9.1   | Endret       | System.out.println kommentert ut i alle delberegninger                                                                                          |
| 0.9.0   | Endret       | Alle delberegninger skrevet om til å bruke BigDecimal, sjablonverdier også endret til å mottas som BigDecimal                                   |
| 0.8.9   | Endret       | Beregning av bidragsevne skrevet om til å bruke BigDecimal i stedet for Double, sjabloner er ikke skrevet om ennå                               |
| 0.8.8   | Endret       | Rettet feil i validering av inntekter                                                                                                           |
| 0.8.7   | Opprettet    | Lagt til validering av inntekter                                                                                                                |
| 0.8.6   | Endret       | Feilretting i beregning av netto barnetilsyn                                                                                                    |
| 0.8.5   | Endret       | Feilretting i BPsAndelUnderholdskostnadCoreImpl                                                                                                 |
| 0.8.4   | Endret       | Endret beregning av netto barnetilsyn til å også beregne for barn over 12 år, resultatbeløp settes da til 0.-                                   |
| 0.8.3   | Endret       | Lagt til bryter for å angi at barnet er selvforsørget, bidrag skal da ikke beregnes                                                             |
| 0.8.2   | Endret       | Rettet feil i logikk rundt barnetilleggBM                                                                                                       |
| 0.8.1   | Endret       | Endret til å sette maks BPs andel av underholdskostnad til 83,3333333333, slik det ligger i Bidragskalkulator                                   |
| 0.8.0   | Endret       | Totalberegning av barnebidrag lagt til                                                                                                          |
| 0.7.7   | Endret       | Feilfiks i coredto: Periode -> PeriodeCore                                                                                                      |
| 0.7.6   | Endret       | Endringer i input og output beregning av barnebidrag                                                                                            |
| 0.7.5   | Endret       | Endret til å ha samme navn på grunnlagsklasser, samt noen feilfikser og tilpasninger                                                            |
| 0.7.4   | Endret       | Lagt til personId for søknadsbarn i input/output for alle delberegninger unntatt beregning av bidragsevne                                       |
| 0.7.3   | Endret       | Rettet feil i håndtering av barn med 0 i tilsynsutgift ved beregning av netto tilsynsutgift                                                     |
| 0.7.2   | Endret       | Inntekter inn til beregning av BPs andel av U er nå lister. Underholdskostnad også lagt til i input for samme beregning                         |
| 0.7.1   | Endret       | Logikk for å beregne underholdskostnad med forhøyet barnetrygd for barn < 6 år lagt til fra 01.07.2021                                          |
| 0.7.0   | Endret       | Beregning av bidragsevne flyttet fra bidrag-beregn-felles, 25% av inntekt lagt til i output fra bidragsevne                                     |
| 0.6.0   | Endret       | Beregning av kostnadsberegnet bidrag lagt til                                                                                                   |
| 0.5.0   | Endret       | Endret sjablonPeriodeListe i DTO for samværsfradrag fra val til var for å få tilgang til setter                                                 |
| 0.4.3   | Endret       | Forbedret input-kontroll på datoer for samværsfradrag og barnebidrag                                                                            |
| 0.4.2   | Endret       | Forbedret input-kontroll på datoer                                                                                                              |
| 0.4.1   | Endret       | Beregning av samværsfradrag lagt til                                                                                                            |
| 0.4.0   | Endret       | Endring for Beregn-til-dato lagt inn for alle beregninger. Beregning av BPs andel av underholdskostnad lagt til.                                |
| 0.3.4   | Endret       | Beregn-til-dato legges nå med i perioder som skal beregnes for å sikre minst én resultatperiode der det er ingen bruddpunkter i andre parametre |
| 0.3.3   | Endret       | Logikk lagt til for å håndtere to sett med regler                                                                                               |
| 0.3.2   | Endret       | Slått av test på overlapp og opphold i faktisk utgift-perioder pga flere barn i input                                                           |
| 0.3.1   | Endret       | Lagt til test på summering av faktiske utgifter per barn                                                                                        |
| 0.3.0   | Endret       | Forenklet DTO for netto barnetilsyn og åpnet for set-metoder på noen av input-variablene                                                        |
| 0.2.6   | Endret       | Lagt til summering av faktiske utgifter pr barn i beregning                                                                                     |
| 0.2.5   | Endret       | Rettet navn i resultat på beregning av underholdskostnad                                                                                        |
| 0.2.4   | Endret       | Litt flere tester på periodisering                                                                                                              |
| 0.2.3   | Endret       | Ryddet i koden og lagt til noen flere tester under periodisering                                                                                |
| 0.2.2   | Endret       | Rettet feil, forpleiningsutgifter skal trekkes fra i beregning av underholdskostnad, ikke legges til                                            |
| 0.2.1   | Endret       | Lagt inn periodisering for beregning av netto barnetilsyn                                                                                       |
| 0.2.0   | Endret       | Endret sjablonPeriodeListe i DTO fra val til var for å få tilgang til setter                                                                    |
| 0.1.1   | Endret       | Første basic commit for beregning av Netto Barnetilsyn                                                                                          |
| 0.1.0   | Opprettet    | Beregning av underholdskostnad klar for videre test via resttjeneste                                                                            |
| 0.0.5   | Endret       | Lagt til ekstra test på periodisering og mapping til Core                                                                                       |
| 0.0.4   | Endret       | Logikk for periodisering og mapping mot Core lagt til                                                                                           |
| 0.0.3   | Endret       | Alle beregninger er nå med, bortsett fra netto barnetilsyn                                                                                      |
| 0.0.2   | Endret       | Beregner nå med forbruksutgifter, boutgifter og barnetilsyn med stønad                                                                          |
| 0.0.1   | Opprettet    | Init commit for beregning av underholdskostnad                                                                                                  |
