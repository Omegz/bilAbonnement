# Koncepter — bilAbonnement

Begreber og principper som projektet er bygget paa, og hvordan de haenger sammen.

---

## GUI via Spring (Client-Server)

GUI via Spring betyder at brugergraensefladen (det brugeren ser) koerer i en **webbrowser**, mens al logik, data og databasekommunikation sker paa **serveren**.

```mermaid
graph LR
    subgraph CLIENT["Klient (Browser)"]
        HTML[HTML — struktur]
        CSS[CSS — udseende]
        JS[JavaScript — interaktion]
    end

    subgraph SERVER["Server (Spring Boot)"]
        CTRL["@Controller — modtager requests"]
        SVC["@Service — forretningslogik"]
        REPO["@Repository — databaseadgang"]
    end

    DB[(Database)]

    CLIENT -->|"HTTP GET/POST"| CTRL
    CTRL -->|"Thymeleaf template + data"| CLIENT
    CTRL --> SVC --> REPO --> DB
```

### Hvordan det virker i vores projekt

1. Brugeren aabner `http://localhost:9091` i browseren
2. Browseren sender en HTTP GET-request til serveren
3. Spring Boot modtager requesten i en `@Controller`
4. Controlleren henter data via service og repository
5. Controlleren sender data + Thymeleaf template tilbage
6. Browseren viser den faerdige HTML-side med CSS styling

### Hvorfor bruger vi det?

| Fordel | Forklaring |
|---|---|
| Separat frontend og backend | HTML/CSS i browseren, Java-logik paa serveren |
| Ingen installation | Brugeren aabner bare en URL i browseren |
| Sikkerhed | Databasen er paa serveren, browseren faar aldrig direkte adgang |
| Skalerbarhed | Serveren kan haandtere mange brugere samtidigt |

### Hvad er client vs. server?

```
Client (browser):  Viser GUI'en — HTML, CSS, JavaScript
                   Sender requests til serveren (GET/POST)
                   Modtager svar (HTML-sider med data)

Server (Spring):   Modtager requests fra browseren
                   Haandterer logik, validering, databaseadgang
                   Returnerer HTML-sider via Thymeleaf
```

---

## Packages i Spring

Packages er en maade at organisere koden i projektet paa.
Hver package har et klart ansvarsomraade — det goer projektet overskueligt og modulaert.

### Vores package-struktur

```
src/main/java/com/springmad/bilabonnement/
├── controller/     <- @Controller: haandterer HTTP-requests
├── service/        <- @Service: forretningslogik
├── repository/     <- @Repository: databaseadgang
└── model/          <- Model-klasser (POJO'er): data
```

```mermaid
graph TD
    subgraph "controller/"
        A1[AuthController]
        A2[BilController]
        A3[KundeController]
        A4[AbonnementController]
        A5[DataregistreringController]
        A6[ForretningController]
        A7[SkadeController]
        A8[PageController]
    end

    subgraph "service/"
        B1[BrugerService]
        B2[BilService]
        B3[KundeService]
        B4[AbonnementService]
        B5[ForretningService]
        B6[SkadeService]
    end

    subgraph "repository/"
        C1[BrugerJdbcRepository]
        C2[BilRepository]
        C3[KundeJdbcRepository]
        C4[AbonnementJdbcRepository]
        C5[ForretningJdbcRepository]
        C6[SkadeJdbcRepository]
    end

    subgraph "model/"
        D1[Bruger]
        D2[Bil]
        D3[Kunde]
        D4[Abonnement]
        D5[RolleDefinitioner]
        D6[...]
    end

    A1 --> B1
    A2 --> B2
    A3 --> B3
    A4 --> B4
    A5 --> B4
    A6 --> B5
    A7 --> B6
    B1 --> C1
    B2 --> C2
    B3 --> C3
    B4 --> C4
    B5 --> C5
    B6 --> C6
```

### Hvad goer hver package?

| Package | Annotation | Ansvar | Maa tale med |
|---|---|---|---|
| `controller/` | `@Controller` | Modtager HTTP-requests, sender data til view | Kun services |
| `service/` | `@Service` | Forretningslogik, validering, regler | Kun repositories |
| `repository/` | `@Repository` | Henter og gemmer data i databasen | Kun databasen |
| `model/` | Ingen (POJO) | Repraesenterer data (felter + getters/setters) | Bruges af alle lag |

### Reglen

```
Controller  →  Service  →  Repository  →  Database
     ↑                                        
  ALDRIG direkte ──────────────────────────────┘
```

En controller maa **aldrig** importere et repository direkte.
Det sikrer separation of concerns — hvert lag har eet ansvar.

---

## Static mappen og Template mappen

Spring Boot har to specielle mapper under `src/main/resources/`:

### `static/` — Statisk indhold

```
src/main/resources/static/
└── css/
    └── style.css
```

- Indeholder alt **statisk** indhold: CSS stylesheets, billeder, JavaScript-filer
- Serveres direkte til browseren uden at Spring bearbejder dem
- Bruges til ressourcer som **ikke aendres** af serveren
- Browseren henter dem direkte via URL (fx `/css/style.css`)

### `templates/` — Dynamiske HTML-sider

```
src/main/resources/templates/
├── fragments/
│   └── navbar.html        <- Genbrugelig navbar (th:fragment)
├── index.html             <- Forside
├── login.html             <- Login-formular
├── signup.html            <- Signup-formular
├── biler.html             <- Bil-oversigt + opret
├── kunder.html            <- Kunde-oversigt + opret
├── abonnementer.html      <- Abonnement-oversigt
├── abonnement-opret.html  <- Opret abonnement
├── data-lejeaftale-opret.html <- Opret lejeaftale
├── dashboard.html         <- KPI-dashboard
├── skader-opret.html      <- Registrer skader
├── about.html             <- Om-siden
└── error.html             <- Fejlside
```

- Indeholder **dynamiske** HTML-sider (Thymeleaf skabeloner)
- Serveres til browseren via en Spring `@Controller` med data indlejret
- Controlleren sender data via `model.addAttribute()`, og Thymeleaf indsaetter det i HTML'en
- Bruges til sider der viser data fra databasen

### Forskellen

```mermaid
graph LR
    BROWSER[Browser]
    
    subgraph "static/"
        CSS[style.css]
    end
    
    subgraph "templates/"
        HTML[biler.html]
    end
    
    CTRL[@Controller]
    
    BROWSER -->|"henter direkte"| CSS
    BROWSER -->|"HTTP GET /biler"| CTRL
    CTRL -->|"model + template"| HTML
    HTML -->|"faerdig HTML"| BROWSER
```

| | `static/` | `templates/` |
|---|---|---|
| **Indhold** | CSS, billeder, JavaScript | HTML-sider (Thymeleaf) |
| **Hvem serverer?** | Browseren henter direkte | Controlleren sender via Thymeleaf |
| **Data fra server?** | Nej — uaendret | Ja — data indlejret med `th:text`, `th:each` osv. |
| **Eksempel** | `style.css` | `biler.html` med alle biler fra databasen |

---

## Application Properties og Environment Variables

### application.properties

Konfigurationsfil for Spring Boot applikationen.
Ligger i `src/main/resources/application.properties`.

```properties
spring.application.name=bilAbonnement

# PORT har en fallback (9091) fordi porten ikke er foelsom data.
server.port=${PORT:9091}

# Database-credentials har INGEN fallback — de SKAL komme fra .env filen.
# Passwords maa aldrig staa hardcodet i application.properties.
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# Driveren er ikke foelsom, saa den staar direkte her.
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

| Indstilling | Hvad den goer | Foelsom? |
|---|---|---|
| `server.port` | Hvilken port serveren lytter paa (default 9091) | Nej — har fallback |
| `spring.datasource.url` | URL til databasen (TiDB/MySQL) | Ja — fra .env |
| `spring.datasource.username` | Brugernavn til databasen | Ja — fra .env |
| `spring.datasource.password` | Password til databasen | Ja — fra .env |
| `spring.datasource.driver-class-name` | Hvilken database-driver der bruges | Nej — staar direkte |

### Environment Variables (Miljoevariabler)

System-variabler der laeses af applikationen under koersel.
Ligger i `.env` filen som er i `.gitignore` og **aldrig committes til Git**.

```
# .env filen (holdes lokalt, aldrig i Git)
SPRING_DATASOURCE_URL=jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/bil_db?sslMode=VERIFY_IDENTITY
SPRING_DATASOURCE_USERNAME=dit_brugernavn
SPRING_DATASOURCE_PASSWORD=dit_password
```

### Syntaksen: `${ENV_VAR}` og `${ENV_VAR:fallback}`

```
${PORT:9091}              -> brug PORT hvis sat, ellers 9091 (MED fallback)
${SPRING_DATASOURCE_URL}  -> brug variablen, fejl hvis den ikke er sat (UDEN fallback)
```

**Regel:** Foelsomme data (passwords, brugernavne) skal ALDRIG have en fallback-vaerdi,
fordi fallback-vaerdien staar i `application.properties` som committes til Git.

### Hvorfor bruger vi begge?

| Formaal | Forklaring |
|---|---|
| Adskille kode og konfiguration | Passwords staar i .env, ikke i Java-koden eller application.properties |
| Fleksibilitet paa forskellige miljoer | Lokal udvikling bruger port 9091, produktion bruger en anden |
| Beskytte foelsomme data | .env er i .gitignore — passwords og noegler committes aldrig |

### Hvad goer .gitignore?

`.gitignore` filen fortaeller Git hvilke filer der IKKE skal committes:

```
# Fra vores .gitignore:
.env
.env.*
*.env
```

Det betyder at `.env` aldrig bliver pushet til GitHub, selv hvis den ligger i projektmappen.

---

## Hvordan det hele haenger sammen

```mermaid
graph TD
    subgraph "BROWSER (Client)"
        USER[Bruger klikker]
        VIEW["HTML + CSS (static/ + templates/)"]
    end

    subgraph "SPRING BOOT (Server)"
        subgraph "controller/"
            CTRL["@Controller<br/>Modtager HTTP GET/POST<br/>Sender data til view"]
        end
        subgraph "service/"
            SVC["@Service<br/>Forretningslogik<br/>Validering"]
        end
        subgraph "repository/"
            REPO["@Repository<br/>JdbcTemplate<br/>SQL queries"]
        end
        subgraph "model/"
            MOD["POJO<br/>Getters/Setters<br/>Singleton"]
        end
    end

    subgraph "DATABASE"
        DB[(TiDB / MySQL)]
    end

    subgraph "KONFIGURATION"
        PROPS[application.properties]
        ENV[.env]
    end

    USER -->|HTTP request| CTRL
    CTRL -->|kalder| SVC
    SVC -->|kalder| REPO
    REPO -->|SQL via JdbcTemplate| DB
    DB -->|ResultSet| REPO
    REPO -->|List af objekter| SVC
    SVC -->|data| CTRL
    CTRL -->|model + template| VIEW
    VIEW -->|HTML| USER
    MOD -.->|bruges af alle lag| CTRL
    MOD -.->|bruges af alle lag| SVC
    MOD -.->|bruges af alle lag| REPO
    PROPS -.->|konfigurerer| DB
    ENV -.->|passwords| PROPS
```

---

## Spring Annotations

Annotations er smaa maerker (@) i koden som fortaeller Spring hvordan en klasse eller metode skal bruges.
De erstatter XML-konfiguration og goer koden enklere.

### Alle annotations vi bruger i projektet

| Annotation | Hvad den goer | Hvor vi bruger den |
|---|---|---|
| `@Controller` | Markerer en klasse der haandterer HTTP-requests og returnerer **HTML views** | Alle controllers (BilController, AuthController osv.) |
| `@RestController` | Markerer en klasse der returnerer **data (JSON)** i stedet for HTML | ApiController (`/api/biler`, `/api/kunder`, `/api/dashboard`) |
| `@Service` | Markerer en klasse som forretningslogik-lag | Alle services (BilService, KundeService osv.) |
| `@Repository` | Markerer en klasse som databaseadgangs-lag | Alle repositories (BilRepository, KundeJdbcRepository osv.) |
| `@Autowired` | Spring indsaetter en dependency automatisk (dependency injection) | Alle felter i controllers, services og repositories |
| `@GetMapping` | Haandterer HTTP GET-requests (hente data, vise sider) | Alle GET-endpoints |
| `@PostMapping` | Haandterer HTTP POST-requests (sende/oprette data) | Alle POST-endpoints |
| `@RequestMapping` | Saetter en basis-URL for alle endpoints i en controller | `@RequestMapping("/biler")`, `@RequestMapping("/api")` osv. |
| `@RequestParam` | Henter vaerdier fra query-parametre i URL'en (fx `?status=active`) | Login-formular, skade-filtrering osv. |
| `@PathVariable` | Henter vaerdier direkte fra URL-stien (fx `/kunder/slet/5` -> id=5) | Slet-endpoint i KundeController |
| `@ModelAttribute` | Binder formdata fra HTML til et Java-objekt automatisk | Opret bil, opret kunde, signup osv. |

### @Controller vs @RestController

```
@Controller     -> returnerer et VIEW (HTML-side via Thymeleaf)
@RestController -> returnerer DATA (JSON) direkte til browseren

Huskeregl: "Controller viser sider, RestController giver data."
```

**@Controller eksempel** (returnerer HTML):
```java
@Controller
public class BilController {
    @GetMapping("/biler")
    public String bilerPage(Model model) {
        model.addAttribute("biler", bilService.findAll());
        return "biler";  // -> templates/biler.html
    }
}
```

**@RestController eksempel** (returnerer JSON):
```java
@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/biler")
    public List<Bil> alleBiler() {
        return bilService.findAll();  // -> JSON: [{"id":1,"navn":"Toyota",...}]
    }
}
```

### @RequestParam vs @PathVariable

Begge henter vaerdier fra URL'en, men paa forskellige maader:

```
@RequestParam:  /kunder?id=5        -> henter fra query-parametre
@PathVariable:  /kunder/slet/5      -> henter fra URL-stien

@RequestParam bruges til: filtrering, soegning, formulardata
@PathVariable bruges til: slet, vis detaljer, opdater (med id i URL'en)
```

**@RequestParam eksempel:**
```java
@GetMapping("/opret")
public String visSide(@RequestParam(required = false) Integer kundeId) {
    // URL: /skader/opret?kundeId=3  ->  kundeId = 3
}
```

**@PathVariable eksempel:**
```java
@GetMapping("/slet/{id}")
public String sletKunde(@PathVariable int id) {
    // URL: /kunder/slet/5  ->  id = 5
    kundeService.sletKunde(id);
    return "redirect:/kunder";
}
```

### @GetMapping vs @PostMapping

```
@GetMapping:  HTTP GET  -> hente data, vise sider (ingen sideeffekter)
@PostMapping: HTTP POST -> oprette, aendre, slette data (har sideeffekter)
```

| Handling | HTTP-metode | Annotation | Eksempel |
|---|---|---|---|
| Vis alle biler | GET | `@GetMapping` | `GET /biler` |
| Opret ny bil | POST | `@PostMapping` | `POST /biler` |
| Vis opret-formular | GET | `@GetMapping` | `GET /abonnementer/opret` |
| Gem abonnement | POST | `@PostMapping` | `POST /abonnementer/opret` |
| Slet kunde | GET | `@GetMapping` | `GET /kunder/slet/{id}` |

---

## Autowiring (Dependency Injection)

Autowiring er Spring's maade at automatisk "indsaette" afhaengigheder i en klasse,
uden at man selv behoever at oprette objekterne med `new`.

### Problemet UDEN @Autowired

```java
// UDEN Autowired — man opretter objekter manuelt med new
public class BilController {
    private BilService bilService = new BilService();  // FORKERT
    // Problemer:
    //   - Vi skal selv styre alle objekter
    //   - BilService har selv brug for BilRepository — hvem opretter det?
    //   - Svært at teste (vi kan ikke bytte til en mock)
}
```

### Loesningen MED @Autowired

```java
// MED Autowired — Spring opretter og indsaetter objektet automatisk
public class BilController {
    @Autowired
    private BilService bilService;  // Spring saetter dette felt automatisk
    // Fordele:
    //   - Vi skriver aldrig "new BilService()"
    //   - Spring styrer objektets livscyklus
    //   - Spring indsaetter ogsaa BilRepository i BilService automatisk
}
```

### Hele kaeden med @Autowired

Spring indsaetter afhaengigheder i hele kaeden automatisk:

```
Spring ser at BilController har @Autowired BilService
  -> Spring opretter BilService
  -> Spring ser at BilService har @Autowired BilRepository
     -> Spring opretter BilRepository
     -> Spring ser at BilRepository har @Autowired JdbcTemplate
        -> Spring opretter JdbcTemplate (fra application.properties)
```

Vi skriver aldrig `new` for nogen af disse objekter. Spring goer det hele.

### Hvor bruger vi @Autowired i projektet?

| Lag | Klasse | Hvad der injiceres | Hvorfor |
|---|---|---|---|
| Controller | BilController | BilService | Controller taler med service |
| Controller | KundeController | KundeService | Controller taler med service |
| Controller | AuthController | BrugerService | Controller taler med service |
| Controller | AbonnementController | AbonnementService, BilService | Controller taler med services |
| Controller | DataregistreringController | AbonnementService, BilService, KundeService, BrugerService | Controller taler med services |
| Controller | ForretningController | ForretningService, BrugerService | Controller taler med services |
| Controller | SkadeController | SkadeService, KundeService, BrugerService | Controller taler med services |
| Controller | ApiController | BilService, KundeService, ForretningService | RestController taler med services |
| Service | BilService | BilRepository | Service taler med repository |
| Service | KundeService | KundeJdbcRepository | Service taler med repository |
| Service | BrugerService | BrugerJdbcRepository | Service taler med repository |
| Service | AbonnementService | AbonnementJdbcRepository | Service taler med repository |
| Service | ForretningService | ForretningJdbcRepository | Service taler med repository |
| Service | SkadeService | SkadeJdbcRepository, AbonnementJdbcRepository | Service taler med repositories |
| Repository | BilRepository | JdbcTemplate | Repository taler med database |
| Repository | KundeJdbcRepository | JdbcTemplate | Repository taler med database |
| Repository | BrugerJdbcRepository | JdbcTemplate | Repository taler med database |
| Repository | AbonnementJdbcRepository | JdbcTemplate | Repository taler med database |
| Repository | ForretningJdbcRepository | JdbcTemplate | Repository taler med database |
| Repository | SkadeJdbcRepository | JdbcTemplate | Repository taler med database |

### Hvor bruger vi IKKE @Autowired?

| Klasse | Hvorfor ingen @Autowired |
|---|---|
| PageController | Har ingen afhaengigheder — returnerer kun view-navne ("index", "about") |
| Bil, Kunde, Bruger (models) | Er POJO'er — de er ikke Spring-styrede, de oprettes med `new` i controllers |
| RolleDefinitioner (Singleton) | Styres af Singleton-patternet (privat konstruktoer + getInstance()), ikke af Spring |

---

## Tjekliste: Foelger vi reglerne?

| Regel | Status |
|---|---|
| Controller taler kun med Service | Ja — ingen repository-imports i controllers |
| Service taler kun med Repository | Ja — services importerer kun repositories |
| Repository taler kun med Database | Ja — via JdbcTemplate og SQL |
| Model bruges af alle lag | Ja — POJO'er deles paa tvaers |
| Packages er organiseret (controller, service, repository, model) | Ja |
| Static mappen indeholder CSS | Ja — `static/css/style.css` (1 ekstern fil) |
| Template mappen indeholder Thymeleaf HTML | Ja — 13 templates + fragments |
| CSS er external (ikke inline) | Ja — alle templates linker til `style.css`, nul `<style>` blokke |
| @Controller bruges til HTML views | Ja — 7 controllers returnerer Thymeleaf templates |
| @RestController bruges til JSON data | Ja — ApiController returnerer JSON paa `/api/*` |
| @PathVariable bruges til URL-sti vaerdier | Ja — `KundeController: /kunder/slet/{id}` |
| @RequestParam bruges til query-parametre | Ja — login, skade-filtrering, abonnement-opret |
| @ModelAttribute bruges til formular-binding | Ja — opret bil, opret kunde, signup |
| @GetMapping og @PostMapping bruges korrekt | Ja — GET henter data, POST opretter data |
| Passwords er i .env (ikke i koden) | Ja — `application.properties` laeser fra miljoevariabler |
| @Autowired bruges til dependency injection | Ja — paa alle felter i controllers, services og repositories |
| Singleton bruges til delte definitioner | Ja — `RolleDefinitioner.getInstance()` |
