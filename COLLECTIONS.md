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

**Hvor i projektet:** `AbonnementJdbcRepository.findAfsluttedeAbonnementer()`

**Hvorfor:** Vi bygger en liste af SQL-parametre og laeser den til sidst med `toArray()`. ArrayList er optimal her fordi vi primaert laeser data (hurtig index-adgang).

```java
// ArrayList: hurtig til at laese elementer via index
ArrayList<Object> params = new ArrayList<>();
if (kundeId != null) {
    params.add(kundeId);
}
// toArray() laeser alle elementer - hurtigt med ArrayList
jdbcTemplate.query(sql, mapper, params.toArray());
```

**Hvornaar man vaelger ArrayList:**
- Naar man laeser data ofte (loop, get(i), toArray)
- Naar man kender ca. antal elementer paa forhaand
- Standard-valget for de fleste List-behov

---

### LinkedList

**Hvad:** En List der internt er en kaede af noder. Hver node har data + en pointer til naeste node. Hurtig til at **tilfoeje og fjerne** elementer.

**Hvor i projektet:** `SkadeJdbcRepository.opretSkader()`

**Hvorfor:** Vi tilfoeger skadebeskrivelser een ad gangen og gennemlober dem med en Iterator. LinkedList er optimal naar man laver mange tilfoejelser.

```java
// LinkedList: hurtig til at tilfoeje elementer
LinkedList<String> skadeKoe = new LinkedList<>(beskrivelser);

// Gennemlob med Iterator (se nedenfor)
Iterator<String> iterator = skadeKoe.iterator();
while (iterator.hasNext()) {
    String beskrivelse = iterator.next();
    // gem i database...
}
```

**Hvornaar man vaelger LinkedList:**
- Naar man ofte tilfojer eller fjerner elementer
- Naar man ikke har brug for hurtig index-adgang

**Sammenligning:**

| | ArrayList | LinkedList |
|---|---|---|
| Laese via index | Hurtig | Langsom |
| Tilfoeje/fjerne | Langsom (midt i listen) | Hurtig |
| Internt | Array | Kaede af noder med pointers |

---

## Set-implementeringer

### HashSet

**Hvad:** En Set der bruger hashing internt. Ingen dubletter, ingen garanteret raekkefoelge, men meget hurtig til opslag.

**Hvor i projektet:** `SkadeController.gyldigSkadeliste()`

**Hvorfor:** Vi tjekker om brugeren har skrevet den samme skadebeskrivelse to gange. HashSet fanger dubletter automatisk fordi `add()` returnerer `false` naar vaerdien allerede findes.

```java
// HashSet: ingen dubletter, hurtig via hashing
HashSet<String> setBeskrivelser = new HashSet<>();

for (String beskrivelse : beskrivelser) {
    boolean erNy = setBeskrivelser.add(beskrivelse);
    if (!erNy) {
        // Duplikat fundet! add() returnerede false
        return false;
    }
}
```

**Hvornaar man vaelger HashSet:**
- Naar man vil sikre unikke elementer
- Naar raekkefoelge ikke er vigtig
- Naar man har brug for hurtige opslag (contains, add)

---

### TreeSet

**Hvad:** En Set der bruger et traee (roed-sort-traee) internt. Ingen dubletter, men elementerne er altid **sorteret**.

**Hvor i projektet:** `BilController.bilerPage()`

**Hvorfor:** Vi finder alle unikke aargange fra bilerne. TreeSet sikrer at aargangene baade er unikke OG sorteret (fx 2022, 2023, 2024), saa vi kan vise dem paent i viewet.

```java
// TreeSet: ingen dubletter + automatisk sortering
TreeSet<Integer> unikkeAar = new TreeSet<>();
for (Bil b : biler) {
    unikkeAar.add(b.getAar());
    // 2022, 2024, 2022, 2023 -> [2022, 2023, 2024]
}
```

**Hvornaar man vaelger TreeSet:**
- Naar man vil have unikke elementer i sorteret raekkefoelge
- Lidt langsommere end HashSet (traee vs. hashing)

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

**Hvad:** Gemmer data som noegel-vaerdi par (key-value). Hver noegel er unik, og man kan hurtigt slaa en vaerdi op via noeglen.

**Hvor i projektet:** `ForretningJdbcRepository.antalAbonnementerPerStatus()`

**Hvorfor:** Vi taeller antal abonnementer per status. Noeglen er status-teksten (fx "AKTIV"), vaerdien er antallet. HashMap er perfekt til dette fordi vi laver key-value opslag.

```java
// HashMap: noegel-vaerdi par (key-value)
HashMap<String, Integer> resultat = new HashMap<>();

// put() indsaetter et noegel-vaerdi par
resultat.put("AKTIV", 5);
resultat.put("AFSLUTTET", 12);

// get() henter vaerdien for en noegel
int aktive = resultat.get("AKTIV");  // -> 5
```

**Vigtige Map-metoder:**

| Metode | Hvad den goer |
|--------|--------------|
| `put(key, value)` | Indsaetter eller overskriver en vaerdi |
| `get(key)` | Henter vaerdien for en noegel |
| `containsKey(key)` | Tjekker om noeglen findes |
| `keySet()` | Returnerer alle noegler som et Set |
| `values()` | Returnerer alle vaerdier |
| `size()` | Antal noegel-vaerdi par |
| `isEmpty()` | Er mappet tomt? |
| `remove(key)` | Fjerner et noegel-vaerdi par |

---

## Iterator

**Hvad:** Et objekt der bruges til at gennemlobe en samling (collection) paa en sikker maade.

**Hvor i projektet:** `SkadeJdbcRepository.opretSkader()`

**Hvorfor:** Vi gennemlober en LinkedList af skadebeskrivelser. Iterator er sikrere end en for-each loop hvis man vil fjerne elementer under iteration (undgaar ConcurrentModificationException).

```java
// Iterator: sikker gennemlobning af en collection
Iterator<String> iterator = skadeKoe.iterator();

while (iterator.hasNext()) {       // Er der flere elementer?
    String element = iterator.next(); // Hent naeste element
    // iterator.remove();            // Sikker fjernelse (valgfrit)
}
```

**Tre vigtige metoder:**

| Metode | Hvad den goer |
|--------|--------------|
| `hasNext()` | Returnerer `true` hvis der er flere elementer |
| `next()` | Returnerer det naeste element |
| `remove()` | Fjerner det sidst hentede element (sikkert) |

**Hvorfor ikke bare en for-loop?**
- En for-each loop (`for (X x : liste)`) virker fint til at laese
- Men hvis man fjerner elementer under iteration, faar man `ConcurrentModificationException`
- Iterator's `remove()` metode undgaar dette problem

---

## Oversigt: Hvor bruges hvad i projektet?

| Collection | Fil | Metode | Hvorfor |
|---|---|---|---|
| **ArrayList** | `AbonnementJdbcRepository` | `findAfsluttedeAbonnementer()` | Bygger SQL-parametre, laeser med `toArray()` |
| **LinkedList** | `SkadeJdbcRepository` | `opretSkader()` | Tilfojer skader een ad gangen (hurtig add) |
| **TreeSet** | `BilController` | `bilerPage()` | Unikke + sorterede aargange |
| **HashSet** | `SkadeController` | `gyldigSkadeliste()` | Fanger duplikerede skadebeskrivelser |
| **HashMap** | `ForretningJdbcRepository` | `antalAbonnementerPerStatus()` | Taeller abonnementer per status (key-value) |
| **Iterator** | `SkadeJdbcRepository` | `opretSkader()` | Sikker gennemlobning af LinkedList |
