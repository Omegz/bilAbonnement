# Java Collections i bilAbonnement

Oversigt over alle Collection-typer vi bruger i projektet, **hvor** de bruges, og **hvorfor** netop den type er valgt.

---

## Hvad er Collection Framework?

En del af Java som bruges til at gemme og arbejde med flere objekter samlet (lister af brugere, resultater osv.).
Den bruges naar man ikke kender antallet af objekter paa forhaand, da det er nemt at tilfoeje, fjerne og gennemlobe data.

Der er tre hovedtyper:

| Type | Hvad den goer | Dubletter? | Raekkefoelge? |
|------|--------------|------------|---------------|
| **List** | Ordnet samling | Ja | Ja |
| **Set** | Samling af unikke elementer | Nej | Afhaenger af type |
| **Map** | Gemmer data som noegel-vaerdi par | Unikke noegler | Afhaenger af type |

---

## List-implementeringer

### ArrayList

**Hvad:** En List der internt bruger et array. Hurtig til at **laese** elementer via index (`get(i)`).

**Hvor i projektet:** [`AbonnementJdbcRepository.findAfsluttedeAbonnementer()`](src/main/java/com/springmad/bilabonnement/repository/AbonnementJdbcRepository.java)

**Hvorfor:** Vi bygger en liste af SQL-parametre dynamisk og laeser den til sidst med `toArray()`.

**Faktisk kode fra projektet:**
```java
// Fra AbonnementJdbcRepository.findAfsluttedeAbonnementer():
ArrayList<Object> params = new ArrayList<>();

if (kundeId != null) {
    baseSql += " AND a.kunde_id = ? ";
    params.add(kundeId);
}

return jdbcTemplate.query(baseSql, mapper, params.toArray());
```

---

### LinkedList, Noder og Pointers

**Hvad:** En List der internt er en **kaede af noder**.

En **node** er en byggeblok der indeholder to ting:
1. **Data** — den faktiske vaerdi (fx en String)
2. **Pointer** (reference) — en adresse til den **naeste node** i kaeden

**Hvor i projektet:** [`SkadeJdbcRepository.opretSkader()`](src/main/java/com/springmad/bilabonnement/repository/SkadeJdbcRepository.java)

Naar en bruger registrerer 3 skader ("Ridse i doer", "Bule i koefanger", "Revne i forrude"),
bygger vores kode denne LinkedList internt:

```
Vores LinkedList efter: new LinkedList<>(beskrivelser)

  Node 1                    Node 2                    Node 3
┌────────────────────┐   ┌────────────────────┐   ┌────────────────────┐
│ data: "Ridse i doer"│   │ data: "Bule i      │   │ data: "Revne i    │
│                    │   │        koefanger"  │   │        forrude"   │
│ next: ────────────────>│ next: ────────────────>│ next: null        │
└────────────────────┘   └────────────────────┘   └────────────────────┘
  ^
  head (foerste node)                                  next = null (sidste)
```

Hver **node** indeholder:
- **data**: selve skadebeskrivelsen (en String)
- **next**: en **pointer** (reference/adresse) til den naeste node i kaeden

Den foerste node hedder **head**. Den sidste node har `next = null` (ingen naeste).

**Hvorfor er det hurtigt at tilfoeje/fjerne?**
- Man aendrer bare pointeren i noden foer/efter
- Man behoever IKKE at flytte alle andre elementer (som ArrayList goer)

**ArrayList internt (til sammenligning):**
```
ArrayList efter: new ArrayList<>(beskrivelser)

  index:  [0]              [1]                [2]
  data:   "Ridse i doer"   "Bule i koefanger" "Revne i forrude"

Alle elementer ligger i et array paa raekke.
Hurtigt at laese (get(1) -> "Bule i koefanger"),
men langsomt at indsaette MIDT i arrayet (alle elementer skal flyttes).
```

**Faktisk kode fra SkadeJdbcRepository.opretSkader():**
```java
// Opretter en LinkedList fra de indsendte beskrivelser.
// Internt bygges kaeden af noder som vist i diagrammet ovenfor.
LinkedList<String> skadeKoe = new LinkedList<>(beskrivelser);

// Iterator gennemlober noderne — vi slipper for selv at haandtere pointers.
// Internt foelger Iterator pointer-kaeden: head -> node2 -> node3 -> null.
Iterator<String> iterator = skadeKoe.iterator();
int index = 0;

while (iterator.hasNext()) {          // Er der en naeste node? (next != null)
    String beskrivelse = iterator.next(); // Hent data fra noden, flyt pointer til naeste

    jdbcTemplate.update(sql,
            abonnementId,
            beskrivelse,              // "Ridse i doer", saa "Bule i koefanger", saa "Revne i forrude"
            priser.get(index),
            LocalDate.now()
    );
    index++;
}
```

**Hvad sker der i koden trin for trin:**
```
Trin 1: iterator.hasNext() -> true  (node 1 har next != null)
        iterator.next()    -> "Ridse i doer"  (henter data fra node 1)
        INSERT INTO skader ... "Ridse i doer"

Trin 2: iterator.hasNext() -> true  (node 2 har next != null)
        iterator.next()    -> "Bule i koefanger"  (henter data fra node 2)
        INSERT INTO skader ... "Bule i koefanger"

Trin 3: iterator.hasNext() -> true  (node 3 eksisterer)
        iterator.next()    -> "Revne i forrude"  (henter data fra node 3)
        INSERT INTO skader ... "Revne i forrude"

Trin 4: iterator.hasNext() -> false (node 3 har next = null, ingen flere)
        while-loop stopper
```

**Sammenligning:**

| | ArrayList | LinkedList |
|---|---|---|
| Laese via index | Hurtig | Langsom |
| Tilfoeje/fjerne | Langsom (midt i listen) | Hurtig |
| Internt | Array | Kaede af noder med pointers |

---

## Iterator

**Hvad:** Et objekt der bruges til at gennemlobe en samling (collection)
uden selv at haandtere noder og pointers.

Naar vi har en LinkedList, er data gemt i noder forbundet med pointers.
Uden Iterator skulle vi selv foelge pointer-kaeden (`node.next.next...`).
Iterator **skjuler** den kompleksitet — vi kalder bare `hasNext()` og `next()`.

**Hvor i projektet:** [`SkadeJdbcRepository.opretSkader()`](src/main/java/com/springmad/bilabonnement/repository/SkadeJdbcRepository.java)

**Tre vigtige metoder:**

| Metode | Hvad den goer | Hvad der sker internt i LinkedList |
|--------|--------------|------|
| `hasNext()` | Returnerer `true` hvis der er flere elementer | Tjekker om nuvaerende nodes `next` pointer != null |
| `next()` | Returnerer det naeste element | Henter `data` fra noden, flytter intern pointer til `next` |
| `remove()` | Fjerner det sidst hentede element sikkert | Opdaterer pointers saa noden springes over i kaeden |

**Hvorfor Iterator i stedet for en for-loop?**
- En for-each loop (`for (String x : liste)`) virker fint til at laese
- Men hvis man fjerner elementer under iteration, faar man `ConcurrentModificationException`
- Iterator's `remove()` metode undgaar dette problem fordi den opdaterer pointerne korrekt

---

## Hashing — teorien bag HashSet og HashMap

HashSet og HashMap er hurtige fordi de bruger **hashing** internt.
Denne sektion forklarer HVORDAN det virker under motorhjelmen.

### Hvad er hashing?

**Hash:** At konvertere en vaerdi til et heltal (index i et array).
**Hash table:** Et array der gemmer elementer via hashing.
**Hash function:** En algoritme der mapper vaerdier til indekser.

```
Eksempel: hash function for heltal = vaerdi % array-laengde

add("Ridse")   -> "Ridse".hashCode() % 10 = 3
add("Bule")    -> "Bule".hashCode() % 10 = 7
add("Revne")   -> "Revne".hashCode() % 10 = 1

index:  [0]    [1]      [2]    [3]      [4]    [5]    [6]    [7]     [8]    [9]
value:  null   "Revne"  null   "Ridse"  null   null   null   "Bule"  null   null

Opslag: contains("Bule") -> hashCode() % 10 = 7 -> kig paa index 7 -> fundet!
Derfor er HashSet hurtig: vi behoever IKKE at gennemlobe hele arrayet.
```

### hashCode() i Java

Alle Java-objekter har en `hashCode()` metode der returnerer et heltal.
Naar vi kalder `setBeskrivelser.add("Ridse")` i vores kode, sker dette internt:

```
1. Java kalder "Ridse".hashCode()         -> faar et heltal (fx 78773847)
2. HashSet beregner index: 78773847 % arrayLength  -> faar fx index 3
3. HashSet gemmer "Ridse" paa index 3 i sit interne array
```

**I vores projekt:** naar [`SkadeService`](src/main/java/com/springmad/bilabonnement/service/SkadeService.java) kalder `setBeskrivelser.add(beskrivelser.get(i))`, bruger Java `hashCode()` paa String-objektet til at finde index.

### Kollisioner (collisions)

**Kollision:** Naar to vaerdier faar samme index.

```
add("Ridse")   -> hashCode() % 10 = 3
add("Flænge")  -> hashCode() % 10 = 3   <- KOLLISION! Samme index som "Ridse"
```

### Kollisionsloesning: Linear Probing

**Probing:** Flyt til naeste ledige index.

```
FOER kollision:
index:  [0]    [1]    [2]    [3]      [4]    [5]
value:  null   null   null   "Ridse"  null   null

EFTER linear probing (flyt til index 4):
index:  [0]    [1]    [2]    [3]      [4]       [5]
value:  null   null   null   "Ridse"  "Flænge"  null

Problem: CLUSTERING — klumper af elementer ved siden af hinanden.
Soegning bliver langsom fordi man skal tjekke mange naboer.
```

### Kollisionsloesning: Chaining (det Java bruger)

**Chaining:** Gem en **liste** paa hvert index i stedet for en enkelt vaerdi.

```
Med chaining (Javas HashSet og HashMap bruger dette):
index:  [0]    [1]    [2]    [3]                    [4]    [5]
value:  null   null   null   ["Ridse", "Flænge"]    null   null
                              ^--- liste med begge vaerdier

Fordel: man loeber aldrig toer for indekser.
Listerne er korte, saa det er stadig hurtigt.
```

### Hvornaar bruger vi hashing i projektet?

| Klasse | Bruger hashing | Hvad sker internt |
|---|---|---|
| `HashSet` i [`SkadeService`](src/main/java/com/springmad/bilabonnement/service/SkadeService.java) | `add()` kalder `hashCode()` paa String | Finder index, tjekker om vaerdien allerede er der (dublet) |
| `HashMap` i [`ForretningJdbcRepository`](src/main/java/com/springmad/bilabonnement/repository/ForretningJdbcRepository.java) | `put()` kalder `hashCode()` paa String-noeglen | Finder index for noeglen, gemmer noegel-vaerdi par |

### Forskel paa hashing af heltal vs objekter

```
Heltal:   hashCode() for 49 = 49.  Index = 49 % 10 = 9.  (simpelt)
String:   hashCode() for "Ridse" = summen af bogstavernes ASCII-vaerdier (+ mere).
Objekter: man kan skrive sin egen hashCode() metode i sine klasser.
```

---

## Set-implementeringer

### HashSet

**Hvad:** En Set der bruger hashing internt (se sektionen ovenfor). Ingen dubletter, ingen garanteret raekkefoelge, meget hurtig opslag.

**Hvor i projektet:** [`SkadeService.gyldigSkadeliste()`](src/main/java/com/springmad/bilabonnement/service/SkadeService.java)

**UDEN HashSet (problemet):**
```java
// FOER — nested loop til at finde dubletter:
for (int i = 0; i < beskrivelser.size(); i++) {
    for (int j = i + 1; j < beskrivelser.size(); j++) {
        if (beskrivelser.get(i).equals(beskrivelser.get(j))) {
            return false;  // Duplikat
        }
    }
}
// Problem: to loops inde i hinanden = langsomt (O(n^2)).
// Med 100 skader = 10.000 sammenligninger.
```

**MED HashSet (loesningen):**

**Faktisk kode fra [`SkadeService.java`](src/main/java/com/springmad/bilabonnement/service/SkadeService.java):**
```java
// Fra SkadeService.gyldigSkadeliste():
HashSet<String> setBeskrivelser = new HashSet<>();

for (int i = 0; i < beskrivelser.size(); i++) {
    if (beskrivelser.get(i).isBlank()) return false;
    if (priser.get(i) == null || priser.get(i).signum() <= 0) return false;

    boolean erNy = setBeskrivelser.add(beskrivelser.get(i));
    if (!erNy) {
        return false;  // Duplikat fundet
    }
}
return true;
// Loesning: add() returnerer false ved dublet. Kun 1 loop (O(n)).
// Med 100 skader = 100 opslag i stedet for 10.000.
```

---

### TreeSet

**Hvad:** En Set der bruger et traee (roed-sort-traee) internt. Ingen dubletter, altid **sorteret**.

**Hvor i projektet:** [`BilController.bilerPage()`](src/main/java/com/springmad/bilabonnement/controller/BilController.java)

**Hvorfor:** Finder unikke aargange fra bilerne, sorteret automatisk.

**Faktisk kode fra [`BilController.java`](src/main/java/com/springmad/bilabonnement/controller/BilController.java):**
```java
// Fra BilController.bilerPage():
TreeSet<Integer> unikkeAar = new TreeSet<>();

// add(): tilfojer en vaerdi (ignorerer dubletter)
for (Bil b : biler) {
    unikkeAar.add(b.getAar());
}
// Resultat: fx [2022, 2023, 2024] — sorteret, ingen dubletter

// contains(): tjekker om en vaerdi findes i settet
boolean har2024 = unikkeAar.contains(2024);

// size(): antal elementer
int antalUnikkeAar = unikkeAar.size();

// isEmpty(): er settet tomt?
boolean harAar = !unikkeAar.isEmpty();
```

**Sammenligning:**

| | HashSet | TreeSet |
|---|---|---|
| Dubletter | Nej | Nej |
| Raekkefoelge | Ingen garanti | Altid sorteret |
| Hastighed | Meget hurtig | Lidt langsommere |
| Internt | Hashing | Roed-sort-traee |

---

## Map-implementering

### HashMap

**Hvad:** Gemmer data som noegel-vaerdi par (key-value). Hver noegel er unik.

**Hvor i projektet:** [`ForretningJdbcRepository.antalAbonnementerPerStatus()`](src/main/java/com/springmad/bilabonnement/repository/ForretningJdbcRepository.java)

**Hvorfor:** Taeller antal abonnementer per status. Noeglen er statusteksten, vaerdien er antallet.

**UDEN HashMap (problemet):**
```java
// FOER — separate variabler for hver status:
int antalAktive = 0;
int antalAfsluttede = 0;
// Problem: hvis der kommer en ny status (fx "PAUSET"), skal vi tilfoeje en ny variabel.
// Vi ved ikke paa forhaand hvilke statusser der findes i databasen.
```

**MED HashMap (loesningen):**

**Faktisk kode fra [`ForretningJdbcRepository.java`](src/main/java/com/springmad/bilabonnement/repository/ForretningJdbcRepository.java):**
```java
// Fra ForretningJdbcRepository.antalAbonnementerPerStatus():
HashMap<String, Integer> resultat = new HashMap<>();

for (Map<String, Object> raekke : raekker) {
    String status = (String) raekke.get("status");
    int antal = ((Number) raekke.get("antal")).intValue();

    resultat.put(status, antal);
}

return resultat;
// Resultat fra databasen: fx {"AKTIV" -> 5, "AFSLUTTET" -> 12}
```

**Alle Map-metoder vi bruger — faktisk kode fra [`ForretningController.java`](src/main/java/com/springmad/bilabonnement/controller/ForretningController.java):**

| Metode | Hvad den goer | Faktisk kode fra vores projekt |
|--------|--------------|------|
| `put(key, value)` | Indsaetter noegel-vaerdi par | `resultat.put(status, antal)` i ForretningJdbcRepository |
| `get(key)` | Henter vaerdi for noegel | `statusFordeling.get("AKTIV")` i ForretningController |
| `containsKey(key)` | Tjekker om noeglen findes | `statusFordeling.containsKey("AKTIV")` i ForretningController |
| `keySet()` | Returnerer alle noegler som et Set | `statusFordeling.keySet()` i ForretningController |
| `values()` | Returnerer alle vaerdier | `statusFordeling.values()` i ForretningController |
| `size()` | Antal noegel-vaerdi par | `statusFordeling.size()` i ForretningController |
| `isEmpty()` | Er mappet tomt? | `statusFordeling.isEmpty()` i ForretningController |
| `remove(key)` | Fjerner et noegel-vaerdi par | Ikke brugt (vi sletter ikke statusser) |

**Alle Set-metoder vi bruger — faktisk kode fra [`BilController.java`](src/main/java/com/springmad/bilabonnement/controller/BilController.java):**

| Metode | Hvad den goer | Faktisk kode fra vores projekt |
|--------|--------------|------|
| `add(value)` | Tilfojer vaerdi (ignorerer dublet) | `unikkeAar.add(b.getAar())` i BilController |
| `contains(value)` | Tjekker om vaerdi findes | `unikkeAar.contains(2024)` i BilController |
| `size()` | Antal elementer | `unikkeAar.size()` i BilController |
| `isEmpty()` | Er settet tomt? | `unikkeAar.isEmpty()` i BilController |
| `add()` returnerer `false` ved dublet | Fanger dubletter | `setBeskrivelser.add(beskrivelse)` i SkadeService |

---

## Oversigt: Hvor bruges hvad i projektet?

| Collection | Fil | Metode | Hvorfor |
|---|---|---|---|
| **ArrayList** | [`AbonnementJdbcRepository`](src/main/java/com/springmad/bilabonnement/repository/AbonnementJdbcRepository.java) | `findAfsluttedeAbonnementer()` | Bygger SQL-parametre, laeser med `toArray()` |
| **LinkedList** | [`SkadeJdbcRepository`](src/main/java/com/springmad/bilabonnement/repository/SkadeJdbcRepository.java) | `opretSkader()` | Tilfojer skader een ad gangen (hurtig add via noder) |
| **TreeSet** | [`BilController`](src/main/java/com/springmad/bilabonnement/controller/BilController.java) | `bilerPage()` | Unikke + sorterede aargange |
| **HashSet** | [`SkadeService`](src/main/java/com/springmad/bilabonnement/service/SkadeService.java) | `gyldigSkadeliste()` | Fanger duplikerede skadebeskrivelser |
| **HashMap** | [`ForretningJdbcRepository`](src/main/java/com/springmad/bilabonnement/repository/ForretningJdbcRepository.java) | `antalAbonnementerPerStatus()` | Taeller abonnementer per status (key-value) |
| **Iterator** | [`SkadeJdbcRepository`](src/main/java/com/springmad/bilabonnement/repository/SkadeJdbcRepository.java) | `opretSkader()` | Sikker gennemlobning af LinkedList (skjuler noder/pointers) |
