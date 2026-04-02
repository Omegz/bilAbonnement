# Singleton Pattern og JUnit Testing i bilAbonnement

---

## Singleton Pattern

### Hvad er Singleton?

Et designmoenstre der sikrer at der kun er **een instans** af en klasse i hele programmet.
Man kan tilgaa den samme instans fra forskellige steder.

### Hvordan sikrer vi at der kun er een instans?

Tre ting arbejder sammen:

```
1. Privat konstruktoer    -> ingen kan skrive "new RolleDefinitioner()" udefra
2. Statisk felt (instance) -> holder den ene instans i hukommelsen
3. getInstance()           -> den eneste maade at faa instansen paa
```

### Hvor bruger vi Singleton i projektet?

**Klasse:** `RolleDefinitioner` (i `model/RolleDefinitioner.java`)

**Problemet UDEN Singleton:**
Roller som "DATAREGISTRERING" og "SKADE_OG_UDBEDRING" var hardcodet som strenge i 3 forskellige controllers.
Hvis man stavede forkert eet sted (fx "DATAREGISTERING" uden det sidste R), ville rolle-checket fejle stille.

**Loesningen MED Singleton:**
Alle roller, statusser og kontrakttyper er samlet i EEN klasse med EEN instans.
Alle controllers henter vaerdierne fra den samme kilde.

### Hvordan ser koden ud?

```java
// I RolleDefinitioner.java:
private static RolleDefinitioner instance;  // Det ene statiske felt

private RolleDefinitioner() { }             // Privat konstruktoer

public static RolleDefinitioner getInstance() {  // Eneste adgangspunkt
    if (instance == null) {
        instance = new RolleDefinitioner();       // Oprettes kun foerste gang
    }
    return instance;                              // Altid samme objekt
}
```

### Hvordan bruges den i controllers?

**Foer (hardcodet streng):**
```java
if (!"DATAREGISTRERING".equals(medarbejder.getRolle())) { ... }
```

**Efter (Singleton):**
```java
if (!RolleDefinitioner.getInstance().getRolleDataregistrering().equals(medarbejder.getRolle())) { ... }
```

`getInstance()` returnerer altid det SAMME objekt — det er kernen i Singleton.

### Hvor i projektet bruges den?

| Fil | Hvad den henter fra Singleton |
|---|---|
| `DataregistreringController` | `getRolleDataregistrering()`, `getKontraktLimited()` |
| `SkadeController` | `getRolleSkadeOgUdbedring()` |
| `ForretningController` | `getRolleForretning()` |
| `AbonnementJdbcRepository` | `getStatusAktiv()` (i SQL queries og INSERT) |
| `ForretningJdbcRepository` | `getStatusAktiv()` (i KPI queries) |
| `BrugerTest` | `getRolleDataregistrering()`, `getRolleSkadeOgUdbedring()`, `getRolleForretning()` |

### Hvorfor Singleton her og ikke bare static variabler?

Med Singleton har vi et **objekt** vi kan teste, udvide og kontrollere.
Static variabler er bare loese vaerdier uden struktur.
Singleton giver os: privat konstruktoer + kontrolleret adgang + een instans.

---

## JUnit Testing

### Hvad er JUnit?

Et testframework i Java. JUnit tests er det mindste og mest automatiserede niveau af test.
Den tester en **unit** (metode) ad gangen.

### Hvad bruger vi til at teste?

**Assert-metoder** sammenligner forventet og faktisk resultat:

| Metode | Hvad den goer | Eksempel |
|--------|--------------|---------|
| `assertEquals(a, b)` | Fejler hvis a og b har **forskellig vaerdi** | `assertEquals("AKTIV", roller.getStatusAktiv())` |
| `assertNotNull(x)` | Fejler hvis x er **null** | `assertNotNull(instans)` |
| `assertSame(a, b)` | Fejler hvis a og b er **forskellige objekter** | `assertSame(instans1, instans2)` |
| `assertThrows(E, code)` | Fejler hvis koden IKKE kaster exception E | `assertThrows(IllegalStateException.class, () -> ...)` |

**Vigtigt:** `assertEquals` tjekker **vaerdi**, `assertSame` tjekker **objekt-reference** (samme sted i hukommelsen).

### Testklasse 1: RolleDefinitionerTest

**Fil:** `src/test/java/.../model/RolleDefinitionerTest.java`

**5 tests der dækker Singleton-patternet:**

| Test | Hvad den tester | Assert-metode |
|------|----------------|---------------|
| `getInstance_returnererIkkeNull` | At getInstance() giver os et objekt | `assertNotNull` |
| `getInstance_returnererSammeInstansHverGang` | At der kun er EEN instans (Singleton-kernen) | `assertSame` |
| `roller_harKorrekteVaerdier` | At rollerne er korrekte | `assertEquals` |
| `statusser_harKorrekteVaerdier` | At AKTIV/AFSLUTTET er korrekte | `assertEquals` |
| `kontraktTyper_harKorrekteVaerdier` | At LIMITED/UNLIMITED er korrekte | `assertEquals` |

### Testklasse 2: AbonnementServiceTest

**Fil:** `src/test/java/.../service/AbonnementServiceTest.java`

**3 tests der dækker forretningslogik (happy flow + exception flow):**

| Test | Hvad den tester | Assert-metode |
|------|----------------|---------------|
| `happyFlow` | At abonnement oprettes naar alt er ok | `verify` (Mockito) |
| `kundenHarAlleredeAktivtAbonnement` | At der kastes fejl ved dublet | `assertThrows` |
| `ugyldigtKundenavn` | At der kastes fejl ved tomt navn | `assertThrows` |

Denne testklasse bruger **Mockito** til at lave en falsk (mock) udgave af repository.
Det goer at vi kan teste service-logikken UDEN at ramme databasen.

### Hvad er forskellen paa happy flow og exception flow?

```
Happy flow:     Alt input er korrekt -> metoden koerer succesfuldt
Exception flow: Noget er forkert -> metoden kaster en exception (fejl)
```

Vi tester BEGGE for at sikre at metoden baade virker korrekt OG fejler korrekt.

### Saadan korer du tests

I terminalen:
```bash
./mvnw test
```

I VS Code: tryk paa den groenne "play" knap ved siden af en testmetode (kraever Java Extension Pack).

### Alle 26 tests i projektet

#### RolleDefinitionerTest (5 tests — Singleton)

| # | Testmetode | Assert | Hvad den tester |
|---|---|---|---|
| 1 | `getInstance_returnererIkkeNull` | `assertNotNull` | At getInstance() giver os et objekt |
| 2 | `getInstance_returnererSammeInstansHverGang` | `assertSame` | At der kun er EEN instans (Singleton-kernen) |
| 3 | `roller_harKorrekteVaerdier` | `assertEquals` | DATAREGISTRERING, SKADE_OG_UDBEDRING, FORRETNING |
| 4 | `statusser_harKorrekteVaerdier` | `assertEquals` | AKTIV, AFSLUTTET |
| 5 | `kontraktTyper_harKorrekteVaerdier` | `assertEquals` | LIMITED, UNLIMITED |

#### BilTest (4 tests — Model getters/setters)

| # | Testmetode | Assert | Hvad den tester |
|---|---|---|---|
| 6 | `setNavn_ogGetNavn_returnererKorrektVaerdi` | `assertEquals` | Navn saettes og hentes korrekt |
| 7 | `setAar_ogGetAar_returnererKorrektVaerdi` | `assertEquals` | Aar saettes og hentes korrekt |
| 8 | `setDatoer_ogGetDatoer_returnererKorrektVaerdi` | `assertEquals` | Start- og slutdato |
| 9 | `nyBil_harStandardvaerdier` | `assertEquals`, `assertNull` | Ny bil har 0/null vaerdier |

#### KundeTest (2 tests — Model getters/setters)

| # | Testmetode | Assert | Hvad den tester |
|---|---|---|---|
| 10 | `setOgGet_alleFelter_returnererKorrekteVaerdier` | `assertEquals` | Navn, email, telefon |
| 11 | `nyKunde_harNullVaerdier` | `assertNull` | Integer-felter er null (ikke 0) |

#### BrugerTest (2 tests — Model + Singleton)

| # | Testmetode | Assert | Hvad den tester |
|---|---|---|---|
| 12 | `setOgGet_alleFelter_returnererKorrekteVaerdier` | `assertEquals` | Navn, alder, rolle, password |
| 13 | `rolle_kanSaettesTilAlleGyldigeRoller` | `assertEquals` | Alle 3 roller via Singleton |

#### AbonnementServiceTest (3 tests — Forretningslogik med Mockito)

| # | Testmetode | Assert | Hvad den tester |
|---|---|---|---|
| 14 | `happyFlow` | `verify` | Abonnement oprettes naar alt er ok |
| 15 | `kundenHarAlleredeAktivtAbonnement` | `assertThrows` | IllegalStateException ved dublet |
| 16 | `ugyldigtKundenavn` | `assertThrows` | IllegalArgumentException ved tomt navn |

#### SkadeValideringServiceTest (9 tests — Validering med HashSet)

| # | Testmetode | Assert | Hvad den tester |
|---|---|---|---|
| 17 | `enGyldigSkade_returnererTrue` | `assertTrue` | Happy flow: een skade |
| 18 | `flereGyldigeSkader_returnererTrue` | `assertTrue` | Happy flow: tre skader |
| 19 | `nullBeskrivelser_returnererFalse` | `assertFalse` | Null input |
| 20 | `tomListe_returnererFalse` | `assertFalse` | Tom liste |
| 21 | `forskelligtAntal_returnererFalse` | `assertFalse` | 2 beskrivelser, 1 pris |
| 22 | `blankBeskrivelse_returnererFalse` | `assertFalse` | Kun mellemrum |
| 23 | `negativPris_returnererFalse` | `assertFalse` | -100.00 |
| 24 | `nulPris_returnererFalse` | `assertFalse` | 0.00 |
| 25 | `duplikeretBeskrivelse_returnererFalse` | `assertFalse` | HashSet fanger dublet |

#### BilAbonnementApplicationTests (1 test — Spring Boot)

| # | Testmetode | Hvad den tester |
|---|---|---|
| 26 | `contextLoads` | At Spring Boot starter korrekt |

### Alle assert-metoder brugt i projektet

| Metode | Antal gange | Hvad den tjekker |
|--------|------------|------------------|
| `assertEquals` | 18 | Forventet vaerdi == faktisk vaerdi |
| `assertNull` | 6 | Vaerdien er null |
| `assertNotNull` | 1 | Vaerdien er IKKE null |
| `assertSame` | 1 | Samme objekt i hukommelsen (Singleton) |
| `assertTrue` | 2 | Vaerdien er true |
| `assertFalse` | 7 | Vaerdien er false |
| `assertThrows` | 2 | Koden kaster en bestemt exception |
