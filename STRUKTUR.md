# Systemstruktur — bilAbonnement

Oversigt over hvordan systemet er bygget op, hvem der taler med hvem, og hvilke teknologier der bruges hvor.

---

## Spring Annotations og Lagdeling

Spring bruger annotations (@) til at fortaelle hvad hver klasse goer i systemet.
Hierarkiet er altid:

```
Controller  ->  Service  ->  Repository  ->  Database
```

| Annotation | Lag | Ansvar |
|---|---|---|
| `@Controller` | Praesentationslag | Haandterer HTTP-requests (GET/POST), modtager data fra browseren, sender data til viewet (Thymeleaf) |
| `@Service` | Forretningslogik | Indeholder logik, validering og regler. Traekker data sammen fra repositories |
| `@Repository` | Dataadgang | Henter og gemmer data i databasen via JdbcTemplate og SQL |

**Vigtig regel:** En controller maa ALDRIG tale direkte med et repository.
Controlleren kalder altid servicen, og servicen kalder repository.

---

## Diagram: Oversigt over hele systemet

```mermaid
graph TD
    BROWSER[Browser / Klient]

    subgraph CONTROLLER["@Controller — Haandterer HTTP"]
        PAGE[PageController]
        AUTH[AuthController]
        BIL_C[BilController]
        KUNDE_C[KundeController]
        ABON_C[AbonnementController]
        DATA_C[DataregistreringController]
        FORRET_C[ForretningController]
        SKADE_C[SkadeController]
    end

    subgraph SERVICE["@Service — Forretningslogik"]
        BRUGER_S[BrugerService]
        BIL_S[BilService]
        KUNDE_S[KundeService]
        ABON_S[AbonnementService]
        FORRET_S[ForretningService]
        SKADE_S[SkadeService]
    end

    subgraph REPOSITORY["@Repository — Databaseadgang"]
        BRUGER_R[BrugerJdbcRepository]
        BIL_R[BilRepository]
        KUNDE_R[KundeJdbcRepository]
        ABON_R[AbonnementJdbcRepository]
        FORRET_R[ForretningJdbcRepository]
        SKADE_R[SkadeJdbcRepository]
    end

    DB[(Database - TiDB/MySQL)]

    BROWSER -->|HTTP GET/POST| CONTROLLER
    CONTROLLER -->|kalder| SERVICE
    SERVICE -->|kalder| REPOSITORY
    REPOSITORY -->|SQL via JdbcTemplate| DB
```

---

## Diagram: Hvem taler med hvem?

```mermaid
graph LR
    AUTH[AuthController] --> BRUGER_S[BrugerService]
    BRUGER_S --> BRUGER_R[BrugerJdbcRepository]

    BIL_C[BilController] --> BIL_S[BilService]
    BIL_S --> BIL_R[BilRepository]

    KUNDE_C[KundeController] --> KUNDE_S[KundeService]
    KUNDE_S --> KUNDE_R[KundeJdbcRepository]

    ABON_C[AbonnementController] --> ABON_S[AbonnementService]
    ABON_C --> BIL_S
    ABON_S --> ABON_R[AbonnementJdbcRepository]

    DATA_C[DataregistreringController] --> ABON_S
    DATA_C --> BIL_S
    DATA_C --> KUNDE_S
    DATA_C --> BRUGER_S

    FORRET_C[ForretningController] --> FORRET_S[ForretningService]
    FORRET_C --> BRUGER_S
    FORRET_S --> FORRET_R[ForretningJdbcRepository]

    SKADE_C[SkadeController] --> SKADE_S[SkadeService]
    SKADE_C --> KUNDE_S
    SKADE_C --> BRUGER_S
    SKADE_S --> SKADE_R[SkadeJdbcRepository]
    SKADE_S --> ABON_R
```

---

## Diagram: Request-flow (hvad sker der naar brugeren klikker?)

```mermaid
sequenceDiagram
    participant B as Browser
    participant C as Controller
    participant S as Service
    participant R as Repository
    participant DB as Database

    B->>C: HTTP GET /biler
    C->>S: bilService.findAll()
    S->>R: bilRepository.findAll()
    R->>DB: SELECT * FROM biler (JdbcTemplate)
    DB-->>R: ResultSet (raekker fra databasen)
    R-->>S: List af Bil-objekter (via RowMapper)
    S-->>C: List af Bil-objekter
    C-->>B: biler.html (Thymeleaf template med data)
```

---

## Teknologier brugt i hvert lag

### Controller-laget

| Teknologi | Hvad den goer |
|---|---|
| `@Controller` | Markerer klassen som en controller |
| `@GetMapping` | Haandterer HTTP GET-requests (hente data) |
| `@PostMapping` | Haandterer HTTP POST-requests (sende data) |
| `@RequestParam` | Henter vaerdier fra URL eller formular |
| `@ModelAttribute` | Binder formdata til et Java-objekt |
| `@PathVariable` | Henter vaerdier fra URL-stien |
| `Model` | Sender data fra controller til view (Thymeleaf) |
| `model.addAttribute()` | Tilfoejer data til modellen |
| `HttpSession` | Gemmer login-information paa tvaers af requests |
| `redirect:/` | Post/Redirect/Get pattern (undgaar dobbelt-submit) |

### Service-laget

| Teknologi | Hvad den goer |
|---|---|
| `@Service` | Markerer klassen som en service |
| `@Autowired` | Spring indsaetter dependencies automatisk (dependency injection) |
| Singleton pattern | RolleDefinitioner — een instans med alle roller og statusser |
| HashSet | Bruges i SkadeService til at fange duplikerede skadebeskrivelser |
| HashMap | Bruges i ForretningService til at taelle abonnementer per status |
| try/catch | Haandterer exceptions saa programmet ikke crasher |

### Repository-laget

| Teknologi | Hvad den goer |
|---|---|
| `@Repository` | Markerer klassen som et repository |
| `JdbcTemplate` | Spring-vaerktoj til at koere SQL paa en sikker maade |
| `jdbc.query()` | Koerer SELECT og returnerer en liste (0, 1 eller flere raekker) |
| `jdbc.queryForObject()` | Koerer SELECT og returnerer praecis 1 vaerdi |
| `jdbc.update()` | Koerer INSERT, UPDATE eller DELETE |
| RowMapper | Mapper hver raekke fra ResultSet til et Java-objekt |
| Parameteriseret query (?) | Beskytter mod SQL-injection |
| ArrayList | Bygger dynamiske SQL-parametre (hurtig index-adgang) |
| LinkedList + Iterator | Gennemlober skader ved indsaettelse (hurtig tilfoejelse) |

### Model-laget

| Teknologi | Hvad den goer |
|---|---|
| Plain POJO | Simpelt Java-objekt med felter, getters og setters |
| Tom konstruktoer | Kraeves af Spring til formular-binding og JDBC mapping |
| Singleton (RolleDefinitioner) | Privat konstruktoer + getInstance() = een instans |
| TreeSet | Bruges i BilController til unikke sorterede aargange |

### View-laget (templates)

| Teknologi | Hvad den goer |
|---|---|
| Thymeleaf | Template engine der genererer HTML med data fra controlleren |
| `th:each` | Looper gennem en liste (fx alle biler) |
| `th:text` | Viser en vaerdi i HTML |
| `th:field` | Binder et input-felt til et Java-objekt |
| `th:object` | Binder en hel formular til et Java-objekt |
| `th:action` | Angiver URL som formularen sender til |
| HTML | Strukturerer indholdet paa siden |
| CSS | Styler udseendet (farver, layout, skrifttyper) |

---

## Alle filer i projektet

### Controllers (7 filer)

| Fil | Taler med services | Endpoints |
|---|---|---|
| `PageController` | Ingen (statiske sider) | GET `/`, GET `/about` |
| `AuthController` | BrugerService | GET/POST `/login`, GET/POST `/signup`, GET `/logout` |
| `BilController` | BilService | GET/POST `/biler` |
| `KundeController` | KundeService | GET/POST `/kunder` |
| `AbonnementController` | AbonnementService, BilService | GET/POST `/abonnementer`, GET/POST `/abonnementer/opret` |
| `DataregistreringController` | AbonnementService, BilService, KundeService, BrugerService | GET/POST `/data/lejeaftale/opret` |
| `ForretningController` | ForretningService, BrugerService | GET `/dashboard`, POST `/dashboard/refresh` |
| `SkadeController` | SkadeService, KundeService, BrugerService | GET/POST `/skader/opret` |

### Services (6 filer)

| Fil | Taler med repositories | Forretningslogik |
|---|---|---|
| `BrugerService` | BrugerJdbcRepository | Login, signup |
| `BilService` | BilRepository | Hent og gem biler |
| `KundeService` | KundeJdbcRepository | Hent og opret kunder |
| `AbonnementService` | AbonnementJdbcRepository | Opret abonnement med validering |
| `ForretningService` | ForretningJdbcRepository | KPI-data og rapporter |
| `SkadeService` | SkadeJdbcRepository, AbonnementJdbcRepository | Skadevalidering (HashSet) og oprettelse |

### Repositories (6 filer)

| Fil | Tabel i databasen | Teknologi |
|---|---|---|
| `BrugerJdbcRepository` | brugere | JdbcTemplate, RowMapper |
| `BilRepository` | biler | JdbcTemplate, RowMapper |
| `KundeJdbcRepository` | kunder | JdbcTemplate, RowMapper |
| `AbonnementJdbcRepository` | abonnementer | JdbcTemplate, RowMapper, JOIN |
| `ForretningJdbcRepository` | abonnementer (KPI) | JdbcTemplate, HashMap, COUNT, SUM |
| `SkadeJdbcRepository` | skader | JdbcTemplate, LinkedList, Iterator |

### Models (8 filer)

| Fil | Hvad den repraesenterer |
|---|---|
| `Bil` | En bil i systemet |
| `Kunde` | En kunde |
| `Bruger` | En medarbejder (login + rolle) |
| `Abonnement` | Et abonnement |
| `AbonnementOversigt` | DTO til visning (JOIN resultat) |
| `AbonnementOption` | DTO til dropdown i skade-modul |
| `LejeaftaleForm` | Formular-binding til lejeaftale-oprettelse |
| `RolleDefinitioner` | Singleton med alle roller, statusser og kontrakttyper |

---

## Hvorfor denne struktur?

| Princip | Hvad det betyder | Eksempel |
|---|---|---|
| **Separation of concerns** | Hvert lag har eet ansvar | Controller = HTTP, Service = logik, Repository = database |
| **Dependency injection** | Spring opretter og indsaetter objekter automatisk | `@Autowired` paa service-felter i controlleren |
| **Singleton pattern** | Een instans af RolleDefinitioner i hele systemet | `RolleDefinitioner.getInstance()` |
| **MVC pattern** | Model-View-Controller adskiller data, visning og logik | Model = POJO, View = Thymeleaf, Controller = @Controller |
