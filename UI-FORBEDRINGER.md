# UI-forbedringer: Golden Rules og Gestalt-principper

Dette dokument beskriver de konkrete UI-aendringer vi har lavet i BilAbonnement-projektet,
og hvilke designprincipper hver aendring er baseret paa.

---

## Hvilke principper bruger vi?

### Shneidermans 8 Golden Rules (udvalgte)
- **Consistency** — samme struktur og visuelle moenstre paa tvaers af alle sider
- **Reduce short-term memory load** — brugeren skal ikke huske kontekst fra side til side; layoutet hjaelper

### Gestalt-principper (udvalgte)
- **Proximity** — elementer der hoerer sammen, placeres taet paa hinanden
- **Similarity** — elementer med samme funktion ser ens ud; elementer med anden funktion ser anderledes ud
- **Common Region** — en visuel ramme (fx et kort/card) grupperer relateret indhold

---

## Aendringer og hvilke principper de foelger

### 1. Login og Signup: Indsat i et centreret kort

**Foer:** Formularfelter floed frit paa siden uden visuel ramme.

**Efter:** Formular er pakket ind i `.auth-card` — et hvidt kort centreret paa siden.

**Princip: Gestalt — Common Region**

Naar vi tegner en visuel ramme (kortet) rundt om formularen, opfatter hjernen automatisk
alle felter som en samlet gruppe. Uden rammen er felterne bare loese elementer paa en baggrund.

**Filer aendret:**
- `templates/login.html` — tilfoejede `.auth-wrapper` og `.auth-card` wrappere
- `templates/signup.html` — samme struktur som login
- `static/css/style.css` — nye `.auth-wrapper` og `.auth-card` styles

---

### 2. Alle indholdssider bruger `.container` max-width

**Foer:** Dashboard, lejeaftale-opret og skader-opret havde indhold der strakte sig helt ud til kanterne.

**Efter:** Alle sider bruger nu `<main class="container">` som begraenser bredden til 1100px og centrerer indholdet.

**Princip: Golden Rule — Consistency**

Brugeren skal ikke opleve at layoutet skifter fra side til side. Naar alle sider har samme
max-bredde og margin, foeles systemet som en sammenhaengende helhed.

**Filer aendret:**
- `templates/dashboard.html` — tilfoejede `<main class="container">` wrapper
- `templates/data-lejeaftale-opret.html` — samme
- `templates/skader-opret.html` — samme

---

### 3. Slet-knap paa Kunder er nu roed

**Foer:** "Slet"-linket saa ud som et normalt link (mork tekst, ingen farve).

**Efter:** "Slet" har nu klassen `.link-danger` — roed tekst med understregning ved hover.

**Princip: Gestalt — Similarity (og kontrast)**

Similarity-princippet siger at elementer der ligner hinanden opfattes som havende samme funktion.
Omvendt: naar en destruktiv handling (slet) ser anderledes ud end navigation (normal links),
kan brugeren skelne mellem dem paa et oejeblik. Roed signalerer "pas paa" — det er en konvention
brugerne allerede kender.

**Filer aendret:**
- `templates/kunder.html` — tilfoejede `class="link-danger"` paa slet-linket
- `static/css/style.css` — ny `.link-danger` klasse

---

### 4. Indholdsafstand (Proximity) paa index-kort

**Foer:** Kortene paa forsiden (`<article class="card">`) havde ingen intern padding.
Teksten sad helt op ad kanten.

**Efter:** `h3`, `p` og `a` inde i `.card` faar nu padding, saa der er luft mellem indhold og kant.

**Princip: Gestalt — Proximity**

Proximity handler om at elementer der staar taet paa hinanden opfattes som relaterede.
Med ordentlig padding inde i kortet er titel, beskrivelse og link visuelt grupperet — og
samtidig adskilt fra kortets kant, saa kortet foeles som en container og ikke bare en baggrund.

**Filer aendret:**
- `static/css/style.css` — nye regler for `.card > h3`, `.card > p`, `.card > a`

---

### 5. Beskrivende undertekster under sideoverskrifter

**Foer:** Sider som Dashboard, Lejeaftale og Skader havde kun en `<h1>` uden kontekst.

**Efter:** Hver side har nu en `<p class="subtext">` der kort forklarer sidens formaal.

**Princip: Golden Rule — Reduce short-term memory load**

Brugeren skal ikke gaette hvad siden handler om. En kort undertekst giver kontekst med det
samme, saa brugeren kan fokusere paa opgaven i stedet for at orientere sig.

**Filer aendret:**
- `templates/dashboard.html`
- `templates/data-lejeaftale-opret.html`
- `templates/skader-opret.html`

---

### 6. Line-height paa body

**Foer:** Ingen eksplicit line-height — browseren bruger default (ca. 1.2).

**Efter:** `line-height: 1.6` paa `body`.

**Princip: Golden Rule — Reduce short-term memory load**

Hoejere linjeafstand goer tekst lettere at laese fordi oejnene nemmere kan foelge linjen.
Det reducerer den kognitive belastning ved at laese tabeller, formularer og beskrivelser.

**Fil aendret:**
- `static/css/style.css` — tilfoejede `line-height: 1.6` til `body`

---

## Oversigt: Princip til aendring

| Princip | Aendring |
|---|---|
| Gestalt: Common Region | Login/signup pakket i `.auth-card` |
| Gestalt: Proximity | Padding i index-kort, footer-spacing |
| Gestalt: Similarity | Slet-link faar `.link-danger` (roed) |
| Golden Rule: Consistency | Alle sider bruger `.container` max-width |
| Golden Rule: Reduce memory load | Undertekster paa sider, `line-height: 1.6` |
