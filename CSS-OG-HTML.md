# CSS og HTML — bilAbonnement

Hvordan vi bruger CSS og HTML i projektet, med eksempler fra vores faktiske kode.

---

## Semantic HTML

HTML definerer den **logiske struktur** — hvordan ting haenger semantisk sammen.
Semantiske elementer fortaeller browseren og udvikleren hvad indholdet ER, ikke bare hvordan det SER ud.

### Block-elementer vs inline-elementer

| Type | Hvad den goer | Eksempler |
|---|---|---|
| **Block** | Tager fuld bredde, starter paa ny linje | `<main>`, `<header>`, `<nav>`, `<section>`, `<article>`, `<footer>`, `<aside>`, `<h1>`, `<p>`, `<div>`, `<table>`, `<form>`, `<ul>` |
| **Inline** | Fylder kun sin teksts bredde, starter IKKE ny linje | `<a>`, `<b>`, `<em>`, `<strong>`, `<span>`, `<input>`, `<button>`, `<img>` |

### UDEN semantic HTML (problemet)

```html
<!-- FOER — alt er <div>, ingen mening: -->
<div class="header">
    <div class="title">Kunder</div>
</div>
<div class="content">
    <div class="form">...</div>
    <div class="table">...</div>
</div>
<div class="footer">...</div>
<!-- Problem: browseren, soegemaskiner og skaaermlaaesere kan ikke se
     hvad der er en overskrift, hvad der er navigation, hvad der er indhold. -->
```

### MED semantic HTML (loesningen)

**Faktisk kode fra [`kunder.html`](src/main/resources/templates/kunder.html):**
```html
<!-- Fra kunder.html: -->
<main class="container">           <!-- block: hovedindhold -->
  <header>                         <!-- block: top af sektionen -->
    <h1>Kunder</h1>                <!-- block: overskrift -->
    <p class="subtext">...</p>     <!-- block: brødtekst -->
  </header>

  <section>                        <!-- block: grupperer relateret indhold -->
    <h2>Opret kunde</h2>
    <form>...</form>
  </section>

  <section>                        <!-- block: en anden gruppe -->
    <h2>Alle kunder</h2>
    <table>
      <td>
        <a th:href="...">Slet</a>  <!-- inline: link fylder kun sin tekst -->
      </td>
    </table>
  </section>

  <footer>                         <!-- block: bund af sektionen -->
    <p class="hint">...</p>
  </footer>
</main>
```

**Faktisk kode fra [`index.html`](src/main/resources/templates/index.html):**
```html
<!-- Fra index.html: -->
<main class="container">
  <header class="header">...</header>      <!-- sidens top-sektion -->

  <section class="grid-3">
    <article class="card">...</article>    <!-- selvstaendigt indholdsstykke -->
    <article class="card">...</article>
    <article class="card">...</article>
  </section>

  <footer>...</footer>                     <!-- sidens bund -->
</main>
```

### Alle semantiske elementer vi bruger

| Element | Hvad det betyder | Hvor vi bruger det |
|---|---|---|
| `<main>` | Hovedindholdet paa siden | [`kunder.html`](src/main/resources/templates/kunder.html), [`biler.html`](src/main/resources/templates/biler.html), [`index.html`](src/main/resources/templates/index.html), [`abonnementer.html`](src/main/resources/templates/abonnementer.html) |
| `<nav>` | Navigation (menupunkter) | [`navbar.html`](src/main/resources/templates/fragments/navbar.html) |
| `<header>` | Top af en sektion | [`index.html`](src/main/resources/templates/index.html), [`kunder.html`](src/main/resources/templates/kunder.html) |
| `<section>` | Gruppe af relateret indhold | [`index.html`](src/main/resources/templates/index.html), [`biler.html`](src/main/resources/templates/biler.html), [`abonnementer.html`](src/main/resources/templates/abonnementer.html), [`kunder.html`](src/main/resources/templates/kunder.html) |
| `<article>` | Selvstaendigt indholdsstykke | [`index.html`](src/main/resources/templates/index.html) (kort paa forsiden) |
| `<footer>` | Bund af en sektion | [`index.html`](src/main/resources/templates/index.html), [`kunder.html`](src/main/resources/templates/kunder.html) |
| `<div>` | Standard block-element (ingen semantik) | Overalt hvor intet semantisk element passer |
| `<span>` | Standard inline-element (ingen semantik) | Status-badges i [`abonnementer.html`](src/main/resources/templates/abonnementer.html) |

---

## CSS Selectors

CSS bestar af to dele: **selectors** (hvilke elementer) og **properties** (hvilken style).

### Alle selector-typer vi bruger

**Faktisk kode fra [`style.css`](src/main/resources/static/css/style.css):**

| Selector-type | Syntaks | Faktisk eksempel fra style.css | Hvad den rammer |
|---|---|---|---|
| **Tag selector** | `element` | `body { font-family: ... }` | ALLE body-elementer |
| **Class selector** | `.class` | `.card { background: ... }` | Alle elementer med class="card" |
| **ID selector** | `#id` | `#welcome { color: red; }` | Det ENE element med id="welcome" |
| **Descendant selector** | `parent child` | `.topbar-menu a { font-size: 14px; }` | Alle `<a>` INDE I et element med class="topbar-menu" |

### UDEN descendant selector (problemet)

```css
/* FOER — man giver HVERT link sin egen klasse: */
.topbar-link { font-size: 14px; color: #475569; }
/* Problem: man skal tilfoeje class="topbar-link" paa HVERT <a> element i HTML. */
```

### MED descendant selector (loesningen)

**Faktisk kode fra [`style.css`](src/main/resources/static/css/style.css):**
```css
/* Fra style.css: */
.topbar-menu a { font-size: 14px; font-weight: 600; color: var(--muted); }
/* Loesning: rammer ALLE <a> elementer inde i .topbar-menu automatisk.
   Ingen ekstra klasser noedvendige i HTML. */
```

---

## CSS Layout-systemer

CSS bestemmer helt og holdent hvordan ting skal placeres visuelt.

### Flow layout (standard)

Standarden naar vi ikke angiver noget andet. Block-elementer stables oven paa hinanden, inline-elementer flyder ved siden af hinanden.

Bruges overalt i vores templates som standard — tekst, overskrifter, formularer.

### Flexbox (`display: flex`)

Naar det skal vaere fleksibelt — elementer placeres i een raekke eller kolonne.

**Faktisk kode fra [`style.css`](src/main/resources/static/css/style.css):**
```css
/* Fra style.css — bruges til navbar-menuen: */
.topbar-menu {
    display: flex;       /* elementer placeres i een raekke */
    gap: 8px;            /* mellemrum mellem elementer */
    justify-content: center;
    flex-wrap: wrap;     /* gaar til naeste linje hvis pladsen ikke raekker */
}

/* Fra style.css — bruges til formular-raekker: */
.row {
    display: flex;
    gap: 16px;
    margin-bottom: 18px;
}
```

### Grid (`display: grid`)

Naar det skal vaere struktureret — elementer placeres i et gitter med raekker og kolonner.

**Faktisk kode fra [`style.css`](src/main/resources/static/css/style.css):**
```css
/* Fra style.css — 2-kolonne grid til KPI-kort paa dashboard: */
.grid-2 {
    display: grid;
    grid-template-columns: 1fr 1fr;    /* to lige brede kolonner */
    gap: 24px;
}

/* Fra style.css — 3-kolonne grid til kort paa forsiden: */
.grid-3 {
    display: grid;
    grid-template-columns: repeat(3, 1fr);  /* tre lige brede kolonner */
    gap: 12px;
}

/* Fra style.css — formular + tabel side om side: */
.grid-form {
    display: grid;
    grid-template-columns: 420px 1fr;  /* formular: fast bredde, tabel: resten */
    gap: 14px;
    align-items: start;
}
```

### Absolute positioning (`position: absolute`)

Naar man selv vil placere noget praecist — elementet tages ud af normal flow.

**Faktisk kode fra [`style.css`](src/main/resources/static/css/style.css):**
```css
/* Fra style.css — underline-animation paa navbar-links: */
.topbar-menu a::after {
    content: "";
    position: absolute;    /* placeres praecist i forhold til parent */
    left: 16px;
    right: 16px;
    bottom: 6px;
    height: 2px;
    background: linear-gradient(90deg, transparent, var(--primary-dark), transparent);
    opacity: 0;
    transform: scaleX(0.6);
}

.topbar-menu a:hover::after {
    opacity: 1;
    transform: scaleX(1);  /* underline glider ud naar man hover */
}
```

### Float layout

**Bruges IKKE** i vores projekt. Float er en foraeldet udvidelse af flow-layout og **skal helst undgaas**. Vi bruger Flex og Grid i stedet.

### Sammenligning

| Layout | Hvornaar | Faktisk brug i projektet |
|---|---|---|
| **Flow** | Tekst, standard layout | Alt der ikke har flex/grid |
| **Flex** | Elementer paa raekke | Navbar-menu, formular-raekker, knap-grupper |
| **Grid** | Struktureret gitter | Dashboard KPI-kort, forside-kort, bil-formular + tabel |
| **Absolute** | Praecis placering | Navbar link hover-animation |
| **Float** | **Undgaas** | Bruges ikke |

---

## CSS Properties

### Display

Bestemmer hvordan et element opfoerer sig:

**Faktisk kode fra [`style.css`](src/main/resources/static/css/style.css):**
```css
/* Fra style.css: */
.topbar-menu { display: flex; }           /* flexbox layout */
.grid-2 { display: grid; }               /* grid layout */
.badge { display: inline-block; }         /* inline men med width/height */
.hidden { display: none; }               /* skjult — bruges til leveringsadresse */
```

### Margin og padding

**Margin** = afstand UDENFOR elementet (til andre elementer).
**Padding** = afstand INDENFOR elementet (til indholdet).

```
┌─────────── margin ───────────┐
│  ┌──────── border ────────┐  │
│  │  ┌──── padding ────┐   │  │
│  │  │   Indhold       │   │  │
│  │  └─────────────────┘   │  │
│  └────────────────────────┘  │
└──────────────────────────────┘
```

**Faktisk kode fra [`style.css`](src/main/resources/static/css/style.css):**
```css
/* Fra style.css: */
* { margin: 0; padding: 0; }             /* reset: fjern al standard-spacing */
.container { margin: 0 auto; padding: 22px 16px 60px; }  /* centreret med indre plads */
.card-elevated { padding: 28px; }         /* indre plads i kort */
h1 { margin-bottom: 8px; }               /* afstand under overskrift */
```

---

## Spring Boot Opstart (Bootstrap)

### Hvad er "Boot" og "Bootstrap"?

- **Boot** = goer det nemt at starte Spring-applikationer med minimal konfiguration
- **Bootstrap** = selve opstartsprocessen der initialiserer alt

### UDEN Spring Boot (problemet)

```java
// FOER — klassisk Spring kraever manuel konfiguration:
// - XML-filer til beans
// - Manuel opsaetning af Tomcat webserver
// - Manuel konfiguration af datasource
// - Manuel import af alle dependencies
// Problem: hundredvis af linjer konfiguration foer applikationen kan starte.
```

### MED Spring Boot (loesningen)

**Faktisk kode fra [`BilAbonnementApplication.java`](src/main/java/com/springmad/bilabonnement/BilAbonnementApplication.java):**
```java
// Fra BilAbonnementApplication.java:
@SpringBootApplication
public class BilAbonnementApplication {
    public static void main(String[] args) {
        SpringApplication.run(BilAbonnementApplication.class, args);
    }
}
```

**Hvad sker der naar `SpringApplication.run()` koeres:**

```
1. SpringApplication.run() starter hele Spring-konteksten
2. Den laeser application.properties (port, database osv.)
3. Den starter en indlejret webserver (Tomcat) paa port 9091
4. Den finder alle @Controller, @Service, @Repository klasser
5. Den opretter alle objekter og indsaetter @Autowired dependencies
6. Applikationen er klar til at modtage HTTP-requests
```

**Konfiguration** vi bruger i [`application.properties`](src/main/resources/application.properties):
- `server.port=${PORT:9091}` — hvilken port serveren lytter paa
- `spring.datasource.*` — databaseforbindelse (fra .env)

**Dependencies** vi bruger i `pom.xml` (Maven):
- `spring-boot-starter-webmvc` — Spring Web (controllere, HTTP)
- `spring-boot-starter-thymeleaf` — Thymeleaf (HTML templates)
- `spring-boot-starter-data-jdbc` — JDBC (JdbcTemplate til database)
- `mysql-connector-j` — MySQL driver (til TiDB)

---

## Tjekliste

| Koncept | Status | Hvor |
|---|---|---|
| Block-elementer (`<main>`, `<h1>`, `<div>`, `<table>`) | Ja | Alle templates |
| Inline-elementer (`<a>`, `<span>`, `<input>`, `<button>`) | Ja | Alle templates |
| Semantic HTML (`<main>`, `<nav>`, `<header>`, `<section>`, `<article>`, `<footer>`) | Ja | [`index.html`](src/main/resources/templates/index.html), [`kunder.html`](src/main/resources/templates/kunder.html), [`biler.html`](src/main/resources/templates/biler.html), [`navbar.html`](src/main/resources/templates/fragments/navbar.html) |
| Tag selector | Ja | `body`, `table`, `th`, `td`, `label`, `input` i [`style.css`](src/main/resources/static/css/style.css) |
| Class selector | Ja | `.card`, `.btn`, `.fejl`, `.topbar` i [`style.css`](src/main/resources/static/css/style.css) |
| ID selector | Ja | `#welcome` i [`style.css`](src/main/resources/static/css/style.css) |
| Descendant selector | Ja | `.topbar-menu a`, `.card-head h2` i [`style.css`](src/main/resources/static/css/style.css) |
| Display property | Ja | `flex`, `grid`, `inline-block`, `none` i [`style.css`](src/main/resources/static/css/style.css) |
| Margin & padding | Ja | Overalt i [`style.css`](src/main/resources/static/css/style.css) |
| Flexbox | Ja | `.topbar-menu`, `.row`, `.actions` i [`style.css`](src/main/resources/static/css/style.css) |
| Grid | Ja | `.grid-2`, `.grid-3`, `.grid-form` i [`style.css`](src/main/resources/static/css/style.css) |
| Absolute positioning | Ja | `.topbar-menu a::after` i [`style.css`](src/main/resources/static/css/style.css) |
| Float | Nej — korrekt undgaaet | Foraeldet, vi bruger flex/grid i stedet |
| External CSS | Ja | Alle templates linker til [`style.css`](src/main/resources/static/css/style.css) |
| `@SpringBootApplication` | Ja | [`BilAbonnementApplication.java`](src/main/java/com/springmad/bilabonnement/BilAbonnementApplication.java) |
| `SpringApplication.run()` | Ja | [`BilAbonnementApplication.java`](src/main/java/com/springmad/bilabonnement/BilAbonnementApplication.java) |
| Maven (pom.xml) | Ja | Spring Web, Thymeleaf, JDBC, MySQL dependencies |
| templates folder | Ja | 13 HTML-filer + fragments |
| index.html | Ja | [`index.html`](src/main/resources/templates/index.html) |
| Error handling (try/catch) | Ja | [`AbonnementController.java`](src/main/java/com/springmad/bilabonnement/controller/AbonnementController.java) |
| throw/throws | Ja | [`AbonnementService.java`](src/main/java/com/springmad/bilabonnement/service/AbonnementService.java) |
