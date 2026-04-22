# Domænemodel — bilAbonnement

Alt du behoever at vide om vores domænemodel, forklaret saa simpelt som muligt.
Dette dokument forklarer HVAD modellen viser, HVORFOR vi har tegnet den som vi har, og HVORDAN den folger best practices og normalformer.

---

## Hvad er en domænemodel?

En domænemodel er et **kort over virkeligheden** for vores system. Den viser:

- Hvilke **ting** (klasser) der findes i systemet — fx Kunde, Bil, Abonnement
- Hvilke **egenskaber** (attributter) hver ting har — fx Kunde har navn og email
- Hvordan tingene **haenger sammen** (relationer) — fx "en kunde kan have flere abonnementer"

**Analogi:** En domænemodel er som en plantegning til et hus. Plantegningen viser rum, doere og forbindelser mellem rum — men ikke moeblerne. Paa samme maade viser domænemodellen klasser og relationer, men ikke den konkrete kode eller data.

---

## Klasser i modellen

Hver klasse svarer til en type "ting" i virkeligheden.

| Klasse | Hvad repraesenterer den | I vores kode | Analogi |
|---|---|---|---|
| `Kunde` | En person der lejer en bil | [`Kunde.java`](src/main/java/com/springmad/bilabonnement/model/Kunde.java), tabellen `kunder` | En gaest paa et hotel |
| `Bil` | En bil der kan lejes ud | [`Bil.java`](src/main/java/com/springmad/bilabonnement/model/Bil.java), tabellen `biler` | Et hotelvaerelse |
| `Bruger` | En medarbejder i systemet (intern bruger) | [`Bruger.java`](src/main/java/com/springmad/bilabonnement/model/Bruger.java), tabellen `brugere` | En receptionist paa hotellet |
| `Abonnement` | En lejeaftale mellem kunde og bil | [`Abonnement.java`](src/main/java/com/springmad/bilabonnement/model/Abonnement.java), tabellen `abonnementer` | En booking af hotelvaerelset |
| `Skade` | En skade paa bilen registreret efter leje | [`ddl.sql`](ddl.sql) tabellen `skader` | En regning for oedelagt minibar |
| `Skadesrapport` | En SAMLET oversigt over skader paa en lejeaftale | Ikke en tabel — udledes paa [`skader-opret.html`](src/main/resources/templates/skader-opret.html) | En kvittering der opsummerer alle regninger |
| `FDMRapport` | Ekstern skadevurdering fra FDM | (Konceptuel — ikke implementeret endnu) | Et eksternt syn af bilen |

---

## Attributter — hvad er inde i hver klasse?

### Kunde

| Felt | Type | Forklaring |
|---|---|---|
| id | Integer | Unik identifikation (primaernoegle) |
| navn | String | Kundens fulde navn |
| email | String | Kundens emailadresse |
| telefon | String | Kundens telefonnummer |

### Bil

| Felt | Type | Forklaring |
|---|---|---|
| id | int | Unik identifikation |
| navn | String | Bilens navn (fx "VW Golf") |
| aar | int | Bilens aargang |
| startsdato | LocalDate | Hvornaar bilen blev tilgaengelig |
| slutsdato | LocalDate | Hvornaar bilen ikke laengere er tilgaengelig |

### Bruger

| Felt | Type | Forklaring |
|---|---|---|
| id | Integer | Unik identifikation |
| navn | String | Medarbejderens navn |
| alder | Integer | Medarbejderens alder |
| rolle | Rolle | Hvilken rolle medarbejderen har (se enum nedenfor) |
| password | String | Password (MVP: klar tekst — kun til demo) |

### Abonnement (lejeaftale)

| Felt | Type | Forklaring |
|---|---|---|
| id | Integer | Unik identifikation |
| startdato | LocalDate | Hvornaar lejeaftalen begynder |
| slutdato | LocalDate | Hvornaar lejeaftalen slutter |
| maanedligPris | BigDecimal | Prisen per maaned i DKK |
| status | Status | AKTIV eller AFSLUTTET |
| kontraktType | KontraktType | LIMITED eller UNLIMITED |
| kontraktVarighedDage | int | Hvor mange dage kontrakten loeber |
| udleveringsstedType | UdleveringsstedType | BILABONNEMENT, FDM eller DS |
| leveringsform | Leveringsform | AFHENTNING eller LEVERING |
| leveringsafdeling | String | Hvilken afdeling leveringen sker fra (kun ved LEVERING) |

### Skade

| Felt | Type | Forklaring |
|---|---|---|
| id | int | Unik identifikation |
| beskrivelse | String | Tekst der beskriver skaden |
| pris | BigDecimal | Prisen for udbedring |
| oprettetDato | LocalDate | Dato hvor skaden blev registreret |

---

## UML-notation — linjer og symboler

Dette er **den vigtigste del af dokumentet**. Hver streg og hvert symbol betyder noget specifikt.

### 1. Linjetyper (streger mellem klasser)

| Symbol | Navn | Hvad betyder det | Analogi |
|---|---|---|---|
| `────────` | Heltrukken streg (solid) | **Strukturel relation** — klasserne kender hinanden fast, og forholdet er gemt i databasen (typisk via en foreign key) | En ægteskabsring — officielt bundet sammen |
| `- - - - -` | Stiplet streg (dashed) | **Dependency / afhaengighed** — en klasse bruger en anden, men er IKKE fast bundet til den i DB'en | Et handshake — de kender hinanden, men ingen ring |

**I vores model:**
- `Kunde ──── Abonnement` er heltrukken fordi `abonnementer.kunde_id` er en foreign key i DB'en
- `Bruger - - -> Abonnement` er stiplet fordi Bruger autoriserer oprettelsen, men der er INGEN `bruger_id` kolonne i `abonnementer`

### 2. Pilehoveder (endepunkter paa stregen)

Her kommer det interessante — **formen** og **fyldet** fortaeller hvor staerk relationen er.

| Symbol | Navn | Hvad betyder det | Analogi |
|---|---|---|---|
| `─────` (ingen pil) | Association | Begge klasser kender hinanden, ligevaerdig relation | To venner — ligevaerdige |
| `────▶` | Navigation (aaben pil) | A kan finde B, men ikke omvendt | En envejsgade — du kan koere den ene vej |
| `────◇` | **Aggregering** (tom/hvid diamant) | "Har en" — A har B, men B kan eksistere UDEN A | Et fodboldhold har spillere — hvis holdet oploses, lever spillerne videre |
| `────◆` | **Komposition** (fyldt/sort diamant) | "Er en del af" — B KAN IKKE eksistere uden A. Hvis A slettes, forsvinder B | Et hus har vaerelser — hvis huset rives ned, forsvinder vaerelserne |
| `────▷` | Arv / generalisering (tom trekant) | B ER en type af A (fx Hund ER et Dyr) | Et familietrae — "baby" er et "menneske" |
| `- - -▷` | Realisering (stiplet + trekant) | B implementerer interfacet A | At underskrive en kontrakt — du lover at opfylde kravene |

### 3. Specielt fokus: tom diamant `◇` vs. fyldt diamant `◆`

Dette er det vigtigste skel i UML. Det er ogsaa det billede du spurgte om!

```
                   Diamanten sidder paa den side
                   der "ejer" den anden klasse.

   ◇ eller ◆ ────────── (uden diamant)
   (ejer-siden)         (den ejede side)
```

| Spoergsmaal | Aggregering `◇` (hvid/tom) | Komposition `◆` (sort/fyldt) |
|---|---|---|
| Kan "delen" leve alene uden "helheden"? | **JA** | **NEJ** |
| Hvis helheden slettes, hvad sker der? | Delen lever videre | Delen slettes ogsaa |
| Kan delen deles af flere helheder? | JA (fx en spiller kan skifte klub) | NEJ (et vaerelse hoerer til eet hus) |
| Analogi | Fodboldklub `◇` Spiller | Hus `◆` Vaerelse |
| Kraftighed af ejerskab | Svag | Staerk |

**Faktisk eksempel fra vores model:**

```
Skadesrapport ◇──────── Skade
(hvid diamant — aggregering)
```

Betyder: Skadesrapport er en **opsamling** af skader. Men en Skade er IKKE oedelagt hvis rapporten slettes — skaden ligger stadig i `skader`-tabellen og hoerer direkte til et abonnement. Rapporten er bare en visning.

**Modsat eksempel (findes IKKE i vores model, men hvis vi havde):**

```
Ordre ◆──────── Ordrelinje
(sort diamant — komposition)
```

Her ville ordrelinjerne IKKE kunne eksistere uden ordren. Hvis ordren slettes → alle linjer slettes ogsaa.

### 4. Multiplicitet (tallene paa stregen)

Tallene fortaeller **hvor mange** der kan vaere paa hver side.

| Symbol | Betydning | Eksempel |
|---|---|---|
| `1` | Praecis een | En person har 1 paskort |
| `0..1` | Nul eller een (valgfri) | En person har 0 eller 1 aegtefaelle |
| `0..*` | Nul eller flere | En person har 0, 1 eller mange boeger |
| `1..*` | Mindst een | En bog har mindst 1 side |
| `n..m` | Mellem n og m | En kamp har 11..11 spillere |

**Faktisk eksempel:**

```
Kunde "1" ──────── "0..*" Abonnement
```

Laes det saaledes: **een** Kunde kan have **0, 1 eller mange** Abonnementer. En kunde kan altsaa godt staa uden aftale, eller have flere.

---

## Relationer i VORES model — linje for linje

| Relation | UML-syntaks | Forklaring | Hvorfor den form? |
|---|---|---|---|
| Kunde `1` ──▶ `0..*` Abonnement (`har`) | heltrukken + aaben pil | En kunde kan have mange abonnementer | Heltrukken fordi `fk_abonnement_kunde` er FK i DB ([ddl.sql:61-62](ddl.sql#L61-L62)) |
| Bil `1` ──▶ `0..*` Abonnement (`bruges i`) | heltrukken + aaben pil | En bil kan udlejes mange gange | Heltrukken fordi `fk_abonnement_bil` er FK i DB ([ddl.sql:58-59](ddl.sql#L58-L59)) |
| Abonnement `1` ──▶ `0..*` Skade (`paadrager`) | heltrukken + aaben pil | Et abonnement kan have mange skader | Heltrukken fordi `fk_skader_abonnement` er FK i DB ([ddl.sql:76-77](ddl.sql#L76-L77)) |
| Abonnement `1` - - ▶ `1` Skadesrapport (`vises som`) | **stiplet** + aaben pil | Et abonnement har en konceptuel rapport | Stiplet fordi Skadesrapport IKKE er en tabel — den udledes i view'et |
| Skadesrapport `1` ◇── `0..*` Skade (`indeholder`) | heltrukken + **hvid diamant** | En rapport er en opsamling af skader | Hvid diamant fordi skader eksisterer uden rapporten (de er knyttet til abonnement, ikke rapport) |
| Skadesrapport `0..1` - - ▶ `0..1` FDMRapport (`baseret paa`) | stiplet + aaben pil | Rapporten kan valgfrit vaere baseret paa FDM's vurdering | Stiplet fordi FDMRapport ikke er implementeret endnu (konceptuel) |
| Bruger - - ▶ Abonnement (`autoriserer`) | stiplet + aaben pil | Bruger godkender oprettelse, men er ikke gemt sammen med aftalen | Stiplet fordi ingen `bruger_id` FK findes i `abonnementer` |
| Bruger - - ▶ Skade (`autoriserer`) | stiplet + aaben pil | Samme princip — bruger autoriserer men gemmes ikke | Stiplet fordi ingen `bruger_id` FK findes i `skader` |

---

## Normalformer — 1NF til 5NF

Normalformer er regler for hvordan man organiserer data i tabeller saa man undgaar:
- **Redundans** (samme data flere steder)
- **Anomalier** (inkonsistent data naar man opdaterer/sletter)

Reglerne bygger paa hinanden: 2NF kraever 1NF, 3NF kraever 2NF osv. Der er syv niveauer ialt, men i praksis gaar man ofte til 3NF eller BCNF.

### Kort oversigt med analogier

| Form | Regel (simpel) | Analogi | Status i vores DB |
|---|---|---|---|
| **1NF** | Hver celle indeholder en enkelt vaerdi. Ingen lister, arrays eller kommasepareret data i en celle. | Et regneark hvor hver celle har EET tal, ikke "5, 10, 15" | ✅ |
| **2NF** | 1NF + alle ikke-noegle-kolonner afhaenger af HELE primaernoeglen (kun relevant ved sammensat PK) | Hvis din noegle er (bil_id, dato), saa maa en kolonne ikke kun afhaenge af bil_id | ✅ (vi bruger single-column PK'er, saa 2NF er triviel) |
| **3NF** | 2NF + ingen transitive afhaengigheder (en ikke-noegle maa ikke afhaenge af en anden ikke-noegle) | Hvis du gemmer kunde_id OG kundens_navn i samme tabel, er det 3NF-brud — navnet hoerer i `kunder`-tabellen | ✅ |
| **BCNF** | 3NF + hver determinant skal vaere en kandidatnoegle (stoerre udgave af 3NF) | Samme princip, strengere | ✅ |
| **4NF** | BCNF + ingen multi-valued dependencies (en raekke maa ikke kode flere uafhaengige lister) | Ikke "en laerer underviser i (matematik, fysik) OG taler (dansk, engelsk)" i samme tabel | ✅ |
| **5NF** | 4NF + ingen join-afhaengigheder (kan tabellen IKKE splittes op uden tab af info?) | Meget sjaelden i praksis — relevant for komplekse N-vejs-relationer | ✅ |

### 1NF — atomic values

**Regel:** Hver celle maa kun indeholde en enkelt vaerdi.

**Eksempel paa brud (IKKE 1NF):**
```
| kunde_id | navn    | telefoner            |
|----------|---------|----------------------|
| 1        | Anders  | 12345678, 87654321   |  <- flere numre i EEN celle
```

**Vores DB (er 1NF):**
```
| id | navn   | telefon   |
|----|--------|-----------|
| 1  | Anders | 12345678  |
| 2  | Anders | 87654321  |
```

Se [ddl.sql:18-23](ddl.sql#L18-L23): alle kolonner i `kunder` har enkelt-vaerdier.

### 2NF — ingen partielle afhaengigheder

**Regel:** Hvis primaernoeglen bestaar af flere kolonner (fx (bil_id, dato)), saa maa en kolonne IKKE kun afhaenge af en DEL af noeglen.

**Hvorfor er vi trivielt 2NF?** Alle vores tabeller har en single-column PK (`id AUTO_INCREMENT`). Der er INGEN sammensat noegle at lave partielle afhaengigheder paa. Se fx [ddl.sql:8](ddl.sql#L8) — `id INT AUTO_INCREMENT PRIMARY KEY`.

### 3NF — ingen transitive afhaengigheder

**Regel:** En ikke-noegle-kolonne maa kun afhaenge af PRIMAERNOEGLEN, ikke af en anden ikke-noegle.

**Eksempel paa brud (IKKE 3NF):**
```
| abonnement_id | kunde_id | kunde_navn | kunde_email |
```
Her afhaenger `kunde_navn` og `kunde_email` af `kunde_id`, IKKE af `abonnement_id`. Det er en transitiv afhaengighed.

**Vores DB (er 3NF):**
```
| id | bil_id | kunde_id | startdato | slutdato | maanedlig_pris | ... |
```
Se [ddl.sql:39-63](ddl.sql#L39-L63). `abonnementer` indeholder KUN FK'er, ikke kopier af kunde-navn eller bil-navn. Hvis vi vil have navnet, laver vi et JOIN.

### BCNF, 4NF, 5NF — kort

Vores model er simpel nok til at disse niveauer er automatisk opfyldt:
- **BCNF:** vi har kun simple PK'er og ingen komplekse funktionelle afhaengigheder
- **4NF:** ingen tabel indeholder flere uafhaengige lister
- **5NF:** ingen tabel kan splittes op uden tab af information

---

## Best practices vi foelger

| Princip | Hvordan vi foelger det | Hvor i koden |
|---|---|---|
| **Single source of truth** | Hver info findes KUN eet sted. Kundens navn er kun i `kunder`, ikke kopieret | [ddl.sql](ddl.sql) — ingen duplikerede kolonner |
| **Foreign keys for integritet** | Abonnement refererer kunde via `kunde_id` FK, ikke som tekststreng | [ddl.sql:58-62](ddl.sql#L58-L62) |
| **Separation of concerns** | Domænemodel handler om forretningsbegreber — ikke teknikken | Koden deler sig i model/service/controller |
| **Nullable kun hvor det giver mening** | `slutdato` er NULL for loebende aftaler; `leveringsafdeling` er NULL naar ikke LEVERING | [ddl.sql:46,56](ddl.sql#L46) |
| **Enums / konstanter som strenge** | Status (AKTIV/AFSLUTTET), KontraktType (LIMITED/UNLIMITED) — gemt som VARCHAR og centraliseret i Singleton | [RolleDefinitioner.java](src/main/java/com/springmad/bilabonnement/model/RolleDefinitioner.java) |
| **View vs entity** | Skadesrapport er en afledt visning, ikke en tabel — det markerer vi med `<<view>>` stereotype | [skader-opret.html](src/main/resources/templates/skader-opret.html) |
| **Eksplicit stiplet streg for ikke-persisterede relationer** | Bruger → Abonnement er stiplet fordi det IKKE er en FK | Modellen viser sandheden om DB'en |

---

## UML-cheatsheet (til hurtig reference)

```
──────────   heltrukken streg     = strukturel relation (fast, typisk FK i DB)
- - - - - -  stiplet streg         = dependency (loese afhaengighed, ikke gemt)

────▶       aaben pil             = navigation ("A kender B")
────◇       hvid diamant          = aggregering ("A har B, B kan leve alene")
────◆       sort diamant          = komposition ("B er en del af A, doer med A")
────▷       hvid trekant          = arv (B arver fra A)
- - ▷       stiplet + trekant     = realisering (B implementerer interfacet A)

1            = praecis een
0..1         = nul eller een
0..*         = nul eller flere (ingen oevre graense)
1..*         = mindst een
```

---

## Sammenfatning

Vores domænemodel er:

1. **Sand** — den matcher den kode og den DB vi faktisk har (med FDMRapport som undtagelse, markeret som konceptuel)
2. **Normaliseret** — vi er paa 5NF (overholder alle normalformer op til og med 5NF)
3. **Laeselig** — vi bruger standard UML-notation med korrekte linjer og pilehoveder
4. **Aerlig** — stiplede linjer hvor der IKKE er FK i DB, heltrukne hvor der ER, saa diagrammet ikke lyver om implementeringen

Det goer den til et godt arbejdsvaerktoej for baade udvikling, dokumentation og undervisning.
