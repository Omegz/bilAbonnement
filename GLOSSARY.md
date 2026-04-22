# Glossary — bilAbonnement

Alt du har brug for at forstaa i dette projekt, forklaret saa simpelt som muligt.

---

## Java Grundbegreber

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| `public` | Alle kan se og bruge det | Metoder og klasser der skal bruges udefra | En butik med aaben doer — alle kan gaa ind | `public class BilController` |
| `private` | Kun klassen selv kan se det | Felter og metoder der er skjult for andre klasser | En laast skuffe — kun ejeren har noeglen | `private String navn;` i Kunde.java |
| `static` | Tilhoerer klassen, ikke objektet | Der er kun EEN — uanset hvor mange objekter man laver | Et klassevaerelsesnummer — det tilhoerer lokalet, ikke eleverne | `private static RolleDefinitioner instance;` |
| `final` | Kan ikke aendres efter det er sat | Vaerdien er fast for altid | Et tatovering — naar det er der, kan det ikke fjernes | `private final String statusAktiv = "AKTIV";` |
| `void` | Returnerer ingenting | Metoden goer noget men giver ikke data tilbage | At sende et brev uden at forvente svar | `public void save(Bil bil)` i BilService |
| `return` | Send en vaerdi tilbage | Metoden stopper og giver resultatet til den der kaldte den | At aflevere en bestilling til kunden | `return "biler";` sender viewet biler.html |
| `null` | Ingenting / tomt | En variabel der ikke peger paa noget objekt | En tom parkeringsplads — der er plads men ingen bil | `if (bruger == null)` |
| `new` | Opret et nyt objekt | Laver en ny instans af en klasse i hukommelsen | At bygge et nyt hus ud fra en plantegning | `new Bil()` opretter et tomt Bil-objekt |
| `this` | Peger paa det aktuelle objekt | Bruger objektet selv, typisk i konstruktoerer | At pege paa sig selv — "mig her" | Bruges internt i Java-klasser |
| `instanceof` | Tjekker om et objekt er af en bestemt type | Sikrer at vi ikke caster forkert | At spoerge: "Er du en laege?" foer man spoerger om laegeraad | `if (!(obj instanceof Bruger))` |
| `import` | Henter en klasse fra et bibliotek | Goer klassen tilgaengelig i vores fil | At laane en bog fra biblioteket saa du kan bruge den | `import java.util.List;` |
| `package` | Mapper der organiserer klasser | Grupperer relaterede filer (controller, service, model) | Mapper paa din computer: "Billeder", "Dokumenter" | `package com.springmad.bilabonnement.controller;` |

---

## Typer og Generics

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| `String` | Tekst | Gemmer navne, emails, statusser osv. | En saetning skrevet paa papir | `private String navn;` |
| `int` | Heltal | Gemmer tal uden decimaler (id, aar, alder) | At taelle paa fingrene — kun hele tal | `private int aar;` i Bil.java |
| `Integer` | Heltal der kan vaere null | Ligesom int, men kan vaere tom (null) | En boks der KAN indeholde et tal, eller vaere tom | `private Integer id;` i Kunde.java |
| `boolean` | Sand eller falsk | Bruges til ja/nej spoergsmaal | En kontakt: taendt (true) eller slukket (false) | `boolean har2024 = unikkeAar.contains(2024);` |
| `BigDecimal` | Praecist decimaltal | Bruges til penge (undgaar afrundingsfejl) | En regnemaskine der aldrig runder forkert | `private BigDecimal maanedligPris;` |
| `LocalDate` | Dato (uden tid) | Gemmer datoer som 2024-01-15 | En dag i kalenderen | `private LocalDate startdato;` |
| `List<Bil>` | En liste af Bil-objekter | Ordnet samling — kan have dubletter | En indkoebsliste med biler | `List<Bil> biler = bilService.findAll();` |
| `<Bil>` | Generics: "af typen Bil" | Fortaeller Java hvilken type listen indeholder | Et skilt paa en boks: "Denne boks indeholder biler" | `List<Bil>` = liste der KUN kan indeholde Bil-objekter |
| `HashMap<String, Integer>` | Map fra tekst til tal | Gemmer noegel-vaerdi par | En telefonbog: navn -> nummer | `HashMap<String, Integer> statusFordeling` |
| `List<Map<String, Object>>` | Liste af raekker fra databasen | Hver Map er een raekke, hver key er et kolonnenavn | Et regneark: liste af raekker, hver raekke har kolonner | `jdbc.queryForList(sql)` returnerer dette |
| `RowMapper<Bil>` | Mapper database-raekke til Bil-objekt | Konverterer raa data til Java-objekt | En oversaetter der omsaetter database-sprog til Java-sprog | `private final RowMapper<Bil> bilRowMapper = ...` |
| `Set<String>` | Samling af unikke tekster | Ingen dubletter tilladt | En gaesteliste — hvert navn staar kun een gang | `Set<String> alleStatusser = statusFordeling.keySet();` |

---

## Spring Annotations

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| `@SpringBootApplication` | "Start hele applikationen" | Aktiverer auto-konfiguration, starter Tomcat-serveren | Taendingsnoegle til en bil — starter alt | `@SpringBootApplication` paa BilAbonnementApplication |
| `@Controller` | "Denne klasse modtager HTTP-requests og returnerer HTML" | Spring ved at klassen haandterer browserforespøergsler | En receptionist der modtager gaester og viser dem til et varelse (HTML-side) | `@Controller` paa BilController |
| `@Service` | "Denne klasse indeholder forretningslogik" | Spring ved at klassen er service-laget | En manager der styrer hvad der skal ske — kalder de rigtige folk | `@Service` paa BilService |
| `@Repository` | "Denne klasse taler med databasen" | Spring ved at klassen haandterer data | En arkivar der henter og gemmer dokumenter i arkivet (databasen) | `@Repository` paa BilRepository |
| `@Autowired` | "Spring, indsaet dette objekt automatisk" | Vi skriver aldrig `new BilService()` — Spring goer det for os | En automatisk doer der aabner naar du kommer — du behoever ikke selv aabne | `@Autowired private BilService bilService;` |
| `@GetMapping("/biler")` | "Naar browseren sender GET /biler, kald denne metode" | Kobler en URL til en Java-metode | Et skilt paa en doer: "Biler — gaa herind for at se biler" | `@GetMapping` paa bilerPage() |
| `@PostMapping` | "Naar en formular sendes (POST), kald denne metode" | Modtager data fra en HTML-formular | En postkasse — modtager breve (formulardata) fra afsender | `@PostMapping` paa createBil() |
| `@RequestMapping("/biler")` | "Alle endpoints i denne controller starter med /biler" | Grupperer relaterede URL'er | Et gadenavn — alle huse (endpoints) har samme gadenavn | `@RequestMapping("/biler")` paa BilController |
| `@RequestParam` | "Hent en vaerdi fra URL'ens query-parametre" | Laeser `?kundeId=3` fra URL'en | At laese et felt paa en formular: "hvad skrev du i feltet kundeId?" | `@RequestParam Integer kundeId` |
| `@PathVariable` | "Hent en vaerdi fra selve URL-stien" | Laeser `/kunder/slet/5` og faar id=5 | Et husnummer — nummeret er en del af adressen selv | `@PathVariable int id` i sletKunde() |
| `@ModelAttribute` | "Bind alle formularfelter til dette Java-objekt" | Spring fylder automatisk objektets felter ud fra formularen | En sekretaer der udfylder en blanket for dig, baseret paa hvad du sagde | `@ModelAttribute("bil") Bil bil` |
| `@Test` | "Denne metode er en JUnit-test" | JUnit koerer alle metoder markeret med @Test | Et "PROEVE" skilt — denne metode tester om noget virker | `@Test void setNavn_ogGetNavn_...()` |

---

## Spring / Web Typer

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| `Model` | En taske der baerer data fra controller til view | Vi putter data i den med `addAttribute`, viewet laeser det | En serverings-bakke — kokken laegger mad paa, tjeneren serverer til gaesten | `model.addAttribute("biler", bilService.findAll())` |
| `model.addAttribute("navn", data)` | Laeg data i tasken med et navn | Viewet kan hente det med `${navn}` | At saette en etiket paa en pakke: "dette er biler" | `model.addAttribute("kunder", kundeService.findAll())` |
| `HttpSession` | Husker hvem der er logget ind paa tvaers af sider | Gemmer bruger-info mellem requests | En noeglering — saa laenge du har den, ved vi hvem du er | `session.setAttribute("loggedInUser", bruger)` |
| `return "biler"` | Returner VIEW-navnet (ikke teksten "biler") | Spring finder templates/biler.html og sender den til browseren | At sige "vis mig varelse 'biler'" — receptionisten (Spring) finder det | `return "biler";` i BilController |
| `return "redirect:/kunder"` | Omdiriger browseren til en anden URL | Undgaar dobbelt-submit naar brugeren refresher efter POST | At sige "gaa hen til /kunder i stedet" — browseren laver en ny GET | `return "redirect:/kunder";` |

---

## JDBC og Database

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| `JdbcTemplate` | Springs vaerktoj til at koere SQL | Haandterer forbindelse, SQL og lukning automatisk | En chauffør — du siger hvor du vil hen (SQL), han koerer (forbindelse) | `@Autowired private JdbcTemplate jdbc;` |
| `jdbc.query(sql, rowMapper)` | Koer SELECT og faa en liste af objekter | Bruges naar vi forventer 0, 1 eller flere raekker | At spoerge arkivaren: "giv mig alle dokumenter der matcher" | `jdbc.query(sql, bilRowMapper)` i BilRepository |
| `jdbc.queryForObject(sql, type)` | Koer SELECT og faa praecis 1 vaerdi | Bruges naar vi forventer noejagtigt 1 resultat | At spoerge: "hvad er svaret?" — der er kun eet | `jdbc.queryForObject(sql, Integer.class)` |
| `jdbc.update(sql, params)` | Koer INSERT, UPDATE eller DELETE | Aendrer data i databasen | At skrive i arkivet — tilfoejer, aendrer eller sletter dokumenter | `jdbc.update(sql, bil.getNavn(), bil.getAar())` |
| `ResultSet` | Raa data fra databasen | Et 2D-tabel med raekker og kolonner fra SQL-resultatet | Et regneark med database-resultater | `rs.getInt("id")` henter vaerdi fra kolonne "id" |
| `rs.getInt("id")` | Hent et heltal fra kolonne "id" | Manuelt mapping — vi angiver kolonnenavn | At pege paa en kolonne i regnearket og laese vaerdien | `bil.setId(rs.getInt("id"));` |
| `BeanPropertyRowMapper` | Automatisk mapping: kolonne -> felt | Spring matcher kolonnenavne med getters/setters | En automatisk oversaetter — du behoever ikke forklare hvert ord | `new BeanPropertyRowMapper<>(Kunde.class)` |
| `?` i SQL | Parameterplads i SQL-query | Vaerdien indsaettes sikkert (beskytter mod SQL-injection) | En blank linje paa en formular — vaerdien udfyldes bagefter, sikkert | `"SELECT * FROM kunder WHERE id = ?"` |

---

## Collections (Samlinger)

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| `ArrayList` | Liste baseret paa et array — hurtig at LAESE | Gemmer SQL-parametre | En nummereret liste — nemt at finde punkt nr. 3 | `ArrayList<Object> params = new ArrayList<>();` |
| `LinkedList` | Liste baseret paa noder — hurtig at TILFOEJE | Gemmer skadebeskrivelser foer indsaettelse | En kaede af papirklips — nemt at tilfoeje en ny klips | `LinkedList<String> skadeKoe = new LinkedList<>(...)` |
| `HashSet` | Samling uden dubletter — bruger hashing | Fanger duplikerede skadebeskrivelser | En gaesteliste — hvis navnet allerede staar der, afvises det | `HashSet<String> setBeskrivelser = new HashSet<>();` |
| `TreeSet` | Samling uden dubletter — altid sorteret | Finder unikke aargange (2022, 2023, 2024) | En gaesteliste i alfabetisk orden | `TreeSet<Integer> unikkeAar = new TreeSet<>();` |
| `HashMap` | Noegel-vaerdi par — hurtigt opslag via noegel | Taeller abonnementer per status | En telefonbog: slaa op paa navn, faa nummer | `HashMap<String, Integer> resultat = new HashMap<>();` |
| `Iterator` | Objekt der gennemlober en samling | Lober igennem LinkedList uden at vi haandterer noder | En bogmaerke der flytter sig ned ad siden, linje for linje | `Iterator<String> iterator = skadeKoe.iterator();` |
| `.add(vaerdi)` | Tilfoej element til liste/set | Tilfojer en bil-aargang til TreeSet | At skrive et nyt punkt paa listen | `unikkeAar.add(b.getAar());` |
| `.get(index)` | Hent element paa position | Henter element nr. i fra listen | At sige "giv mig punkt nr. 3 fra listen" | `priser.get(index)` |
| `.put(key, value)` | Indsaet noegel-vaerdi par i Map | Gemmer status + antal i HashMap | At skrive et nyt opslag i telefonbogen | `resultat.put(status, antal);` |
| `.contains(vaerdi)` | Er vaerdien i samlingen? | Tjekker om aargang 2024 findes | At spoerge: "staar Omar paa gaestelisten?" | `unikkeAar.contains(2024)` |
| `.containsKey(key)` | Er noeglen i mappet? | Tjekker om "AKTIV" findes som status | At spoerge: "er der en side for Omar i telefonbogen?" | `statusFordeling.containsKey("AKTIV")` |
| `.keySet()` | Alle noegler fra mappet som et Set | Faar alle statusnavne (AKTIV, AFSLUTTET) | At faa en liste over alle navne i telefonbogen | `statusFordeling.keySet()` |
| `.values()` | Alle vaerdier fra mappet | Faar alle antal (5, 12) | At faa en liste over alle telefonnumre i bogen | `statusFordeling.values()` |
| `.size()` | Antal elementer | Taealler hvor mange unikke aar der er | At taelle hvor mange punkter der er paa listen | `unikkeAar.size()` |
| `.isEmpty()` | Er samlingen tom? | Tjekker om der er nogen aar overhovedet | At tjekke om listen er blank | `unikkeAar.isEmpty()` |
| `.hasNext()` | Har iteratoren flere elementer? | Tjekker om der er flere skader at lobe igennem | At spoerge: "er der flere sider i bogen?" | `iterator.hasNext()` |
| `.next()` | Giv mig det naeste element | Henter naeste skadebeskrivelse | At blaadre til naeste side | `iterator.next()` |
| `.toArray()` | Konverter liste til array | Sender parametre til SQL-query | At pakke listen ned i en boks (array) til forsendelse | `params.toArray()` |

---

## Exceptions (Fejlhaandtering)

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| `try { }` | "Proev denne kode — den KAN fejle" | Omslutter kode der kan kaste en exception | At proeve at gaa over en bro — den KAN holde, den KAN bryde sammen | `try { abonnementService.opretAbonnement(...) }` |
| `catch (Type e) { }` | "Hvis den fejler med denne type fejl, goer dette" | Fanger en specifik exception og viser fejlbesked | En sikkerhedsnet under broen — fanger dig hvis du falder | `catch (EmptyResultDataAccessException e) { ... }` |
| `finally { }` | "Koer dette ALTID, uanset om det fejlede eller ej" | Logger forsoget — sker altid | En oprydnings-person der altid rydder op, uanset hvad der skete | `finally { System.out.println("forsoegte...") }` |
| `throw` | "Kast denne fejl NU" — stopper metoden | Kaster en exception naar input er ugyldigt | At raabe "STOP!" og smide en fejl-bold op i luften | `throw new IllegalArgumentException("Kundenavn tomt")` |
| `throws` | "Denne metode KAN kaste denne fejl" — en advarsel | Staar i metode-signaturen som en erklering | Et skilt paa doeren: "ADVARSEL — der KAN vaere fejl herinde" | `throws SQLException` paa RowMapper-metoder |
| `e.getMessage()` | Hent fejl-beskeden fra exceptionen | Viser den besked vi gav med throw | At laese den besked der stod paa fejl-bolden | `model.addAttribute("fejl", e.getMessage())` |
| `IllegalArgumentException` | "Du gav forkert input" | Ugyldigt kundenavn eller negativ pris | En maskine der afviser forkert moent — "det er ikke en krone!" | `throw new IllegalArgumentException(...)` |
| `IllegalStateException` | "Systemet er i forkert tilstand" | Kunden har allerede et aktivt abonnement | En haaveautomat der siger "du har allerede trukket et nummer" | `throw new IllegalStateException(...)` |

---

## Lambda og Stream

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| `(rs, rowNum) -> { ... }` | En kort maade at skrive en metode paa | Definerer HVAD der skal goeres med hver database-raekke | En opskrift skrevet paa en post-it i stedet for i en hel kogebog | RowMapper i BilRepository |
| `->` | "Goer dette" / piloperator | Separerer parametre fra kode i en lambda | En pil der siger "tag disse input og goer dette" | `(rs, rowNum) -> { Bil bil = new Bil(); ... }` |
| `.stream()` | Konverter liste til en stream (strm af data) | Goer det muligt at bruge findFirst/filter paa listen | At laegge alle bolde paa et rullerbaand saa man kan plukke fra dem | `resultater.stream()` |
| `.findFirst()` | Find det foerste element i streamen | Henter den foerste bruger fra listen | At tage den foerste bold fra rullerbaandet | `resultater.stream().findFirst()` |
| `.orElse(null)` | "Hvis der ikke er noget, brug denne vaerdi" | Returnerer null hvis listen er tom (ingen bruger fundet) | Hvis rullerbaandet er tomt, faa "ingenting" (null) | `.findFirst().orElse(null)` |

---

## Casting (Type-konvertering)

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| `(Bruger) obj` | "Jeg VED at obj er en Bruger — behandl den som en" | Konverterer et generelt Object til en specifik Bruger | At sige "den person ER en laege" saa du kan bede om laegeraad | `Bruger bruger = (Bruger) obj;` |
| `(String) raekke.get("status")` | "Jeg VED at vaerdien er en String" | Konverterer Object til String | At tage en pakke ud af en generel boks og sige "dette er en bog" | `String status = (String) statusObj;` |
| `(Number) raekke.get("antal")` | "Jeg VED at vaerdien er et tal" | Konverterer Object til Number saa vi kan hente int | At tage et objekt og sige "dette er et tal — giv mig heltallet" | `Number antalNumber = (Number) antalObj;` |
| `.intValue()` | Konverter Number til int | Henter heltalsvaerdien fra et Number-objekt | At runde et tal ned til et heltal | `int antal = antalNumber.intValue();` |
| `.toLocalDate()` | Konverter java.sql.Date til LocalDate | Aendrer database-dato til Java-dato | At oversaette en dato fra database-format til Java-format | `startDato.toLocalDate()` |

---

## Thymeleaf (HTML med data)

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| `th:each="k : ${kunder}"` | "For hver kunde k i listen kunder, gentag dette HTML" | Looper og laver een tabel-raekke per kunde | En kopimaskine der laver een kopi per person paa listen | `<tr th:each="k : ${kunder}">` |
| `th:text="${k.navn}"` | "Vis vaerdien af k.getNavn() her" | Indsaetter kundens navn i HTML | At udfylde et felt paa en blanket med personens navn | `<td th:text="${k.navn}"></td>` |
| `th:field="*{navn}"` | "Bind dette input-felt til objektets navn-felt" | Naar formularen sendes, fyldes objektets setNavn() automatisk | At forbinde en formular-boks med et felt i databasen | `<input th:field="*{navn}">` |
| `th:object="${kunde}"` | "Bind hele denne formular til Kunde-objektet" | Alle th:field felter i formularen udfylder Kunde-objektet | At sige "alle felter i denne formular handler om denne kunde" | `<form th:object="${kunde}">` |
| `th:if="${fejl}"` | "Vis kun dette element hvis 'fejl' ikke er null" | Viser fejlbesked kun naar der ER en fejl | En alarm der kun lyser naar der er et problem | `<div th:if="${fejl}">` |
| `th:href="@{/kunder}"` | "Lav et link til URL'en /kunder" | Genererer korrekt URL (virker ogsaa med context path) | En GPS der laver den rigtige rute, uanset hvor du starter | `<a th:href="@{/kunder}">` |
| `th:action="@{/kunder}"` | "Send formularen til URL'en /kunder" | Fortaeller browseren HVOR den skal sende formulardata | Adressen paa en konvolut — hvor brevet skal sendes hen | `<form th:action="@{/kunder}">` |
| `th:replace="fragments/navbar :: navbar"` | "Erstat dette element med navbar-fragmentet" | Indsaetter den genbrugelige navbar paa alle sider | At klistre den samme header paa toppen af hvert brev | `<div th:replace="fragments/navbar :: navbar">` |
| `${...}` | "Hent denne vaerdi fra Model" | Laeser data der blev sat med model.addAttribute() | At aabne den pakke controlleren sendte og tage indholdet ud | `${kunder}` henter listen af kunder |
| `*{...}` | "Hent dette felt fra det bundne objekt (th:object)" | Laeser et felt fra det objekt formularen er bundet til | At laese et felt fra den blanket der allerede ligger foran dig | `*{navn}` henter Kunde.getNavn() |
| `@{...}` | "Generer denne URL" | Laver korrekte URL'er med context path | En GPS der beregner den fulde adresse automatisk | `@{/css/style.css}` |

---

## Designmoenstre

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi | Eksempel fra vores kode |
|---|---|---|---|---|
| Singleton | Der maa kun vaere EEN instans af denne klasse | RolleDefinitioner — een kilde til sandhed for alle roller | Der er kun EEN rektor paa skolen — alle spoerger den SAMME person | `RolleDefinitioner.getInstance()` |
| `getInstance()` | "Giv mig den ene instans" | Returnerer altid det SAMME objekt | At ringe til rektors kontor — du faar altid fat i den SAMME rektor | `RolleDefinitioner.getInstance()` |
| MVC | Model-View-Controller adskiller data, visning og logik | Controller modtager request, Service haandterer logik, View viser HTML | En restaurant: Tjener (Controller) -> Kok (Service) -> Tallerken (View) | Hele projektet foelger dette |
| POJO | Plain Old Java Object — simpel klasse med felter og getters/setters | Bil, Kunde, Bruger er alle POJO'er | En formular med felter — den indeholder bare data, ingen logik | `public class Kunde { private String navn; ... }` |
| DTO | Data Transfer Object — objekt der baerer data mellem lag | AbonnementOversigt baerer JOIN-data fra repository til view | En kurér der transporterer en pakke fra eet sted til et andet | `AbonnementOversigt` med kundeNavn + bilNavn |

---

## Operatorer

| Udtryk | Hvad det betyder | Eksempel fra vores kode |
|---|---|---|
| `==` | Er lig med (for primitive typer: int, boolean) | `if (antal == 0)` |
| `!=` | Er IKKE lig med | `if (bruger != null)` |
| `.equals()` | Er lig med (for objekter: String, Integer) | `"AKTIV".equals(status)` |
| `&&` | OG — begge sider skal vaere true | `if (bruger == null \|\| bruger.getRolle() == null)` |
| `\|\|` | ELLER — mindst een side skal vaere true | `if (navn == null \|\| navn.isBlank())` |
| `!` | IKKE — vender true til false og omvendt | `if (!kravetRolle.equals(brugerensRolle))` |
| `<`, `>`, `<=`, `>=` | Mindre end, stoerre end, osv. | `if (varighed < 90 \|\| varighed > 1080)` |
| `%` | Modulo — resten ved division (bruges i hashing) | `hashCode() % arrayLength` (internt i HashSet) |

---

## SQL i Java

| Udtryk | Hvad det betyder | Hvad det goer i vores kode | Analogi |
|---|---|---|---|
| `SELECT ... FROM ...` | Hent data fra en tabel | Henter alle biler, kunder osv. | At spoerge arkivaren: "vis mig alle dokumenter i denne mappe" |
| `INSERT INTO ... VALUES` | Tilfoj ny raekke i tabellen | Opretter ny bil, kunde osv. | At laegge et nyt dokument i arkivet |
| `DELETE FROM ... WHERE` | Slet raekke(r) fra tabellen | Sletter en kunde | At fjerne et dokument fra arkivet |
| `JOIN` | Kombiner data fra flere tabeller | Viser abonnement + kundenavn + bilnavn i een tabel | At laegge to mapper ved siden af hinanden og kombinere info |
| `WHERE` | Filtrer raekker | Kun aktive abonnementer, eller en specifik kunde | At sige "vis mig KUN de roede dokumenter" |
| `COUNT(*)` | Tael antal raekker | Antal aktive abonnementer (KPI) | At taelle hvor mange dokumenter der er i mappen |
| `SUM()` | Laeg alle vaerdier sammen | Samlet maanedlig pris (KPI) | At laegge alle tal i en kolonne sammen |
| `GROUP BY` | Grupper raekker og tael per gruppe | Antal abonnementer per status | At sortere dokumenter i bunker og taelle hver bunke |
| `ORDER BY` | Sorter resultatet | Nyeste foerst | At sortere dokumenterne efter dato |
| `COALESCE(vaerdi, 0)` | Hvis vaerdi er null, brug 0 i stedet | Undgaar null naar der ingen aktive abonnementer er | En fallback — "hvis der ikke er noget svar, sig 0" |
| `CURDATE()` | Dagens dato i SQL | Tjekker om slutdato er passeret | At spoerge "hvad er datoen i dag?" |
| `LIMIT 1` | Maks 1 raekke | Henter kun foerste bruger ved login | At sige "giv mig KUN det foerste resultat" |

---

## HTML Grundbegreber

| Udtryk | Hvad det betyder | Analogi |
|---|---|---|
| `<!DOCTYPE html>` | "Dette er et HTML5-dokument" | Forsiden paa en bog: "dette er en roman" |
| `<html>` | Rod-elementet — alt andet er inde i det | Bogomslaget — alt indhold er inden i |
| `<head>` | Meta-information (titel, CSS-link) — ikke synligt | Bagsiden af bogen — info OM bogen, ikke selve historien |
| `<body>` | Alt synligt indhold | Selve siderne i bogen — det laeseren ser |
| `<link rel="stylesheet" href="...">` | Link til eksternt CSS-stylesheet | En henvisning: "se denne stilguide for udseendet" |
| `<form method="post">` | Formular der sender data til serveren | En blanket man udfylder og sender med posten |
| `<input type="text">` | Tekst-felt i en formular | En tom linje paa blanketten |
| `<select>` / `<option>` | Dropdown-menu | En rulle-menu man vaelger fra |
| `<table>` / `<tr>` / `<td>` | Tabel / raekke / celle | Et Excel-regneark med raekker og kolonner |
| `<main>`, `<header>`, `<footer>`, `<nav>`, `<section>`, `<article>` | Semantiske elementer — fortaeller hvad indholdet ER | Skilte paa doerene: "Koekken", "Stue", "Soveværelse" |
| `<div>` | Generisk block-boks (ingen semantik) | En tom boks — kan indeholde hvad som helst |
| `<span>` | Generisk inline-boks (ingen semantik) | En highlighter — markerer tekst uden at bryde linjen |
| Block-element | Tager fuld bredde, starter paa ny linje | En hel hylde i reolen |
| Inline-element | Fylder kun sin tekst, starter IKKE ny linje | En bog paa hylden — flere boeger ved siden af hinanden |

---

## CSS Grundbegreber

| Udtryk | Hvad det betyder | Eksempel fra style.css |
|---|---|---|
| Tag selector: `body { }` | Stil ALLE body-elementer | `body { font-family: ...; background: ...; }` |
| Class selector: `.card { }` | Stil alle elementer med `class="card"` | `.card { background: white; border-radius: 14px; }` |
| ID selector: `#welcome { }` | Stil det ENE element med `id="welcome"` | `#welcome { color: red; }` |
| Descendant: `.topbar-menu a { }` | Stil alle `<a>` INDE I `.topbar-menu` | `.topbar-menu a { font-size: 14px; }` |
| `display: flex` | Placer elementer paa een raekke | `.row { display: flex; gap: 16px; }` |
| `display: grid` | Placer elementer i et gitter | `.grid-2 { display: grid; grid-template-columns: 1fr 1fr; }` |
| `position: absolute` | Placer elementet praecist (ud af normal flow) | `.topbar-menu a::after { position: absolute; }` |
| `margin` | Afstand UDENFOR elementet | `h1 { margin-bottom: 8px; }` |
| `padding` | Afstand INDENFOR elementet | `.card-body { padding: 16px; }` |
| `:hover` | Stil naar musen er over elementet | `.btn-primary:hover { transform: translateY(-1px); }` |
| `::after` | Tilfoej indhold EFTER elementet | `.topbar-menu a::after { ... }` (underline-animation) |
| `:root { --primary: #2563eb; }` | CSS variabel — genbruges i hele filen | `color: var(--primary);` bruger vaerdien |
| `var(--primary)` | Brug vaerdien fra CSS-variablen | `background: var(--primary);` |

---

## Sammensatte Udtryk — hele linjer forklaret ord for ord

Disse er linjer fra vores kode der kombinerer flere begreber paa een gang. Laes diagrammerne oppefra og ned.

---

### `public List<Map<String, Object>> aktiveUdlejningerMedJoin()`

Fra [`ForretningService.java`](src/main/java/com/springmad/bilabonnement/service/ForretningService.java):

```
public List<Map<String, Object>> aktiveUdlejningerMedJoin()
│      │    │   │       │        │
│      │    │   │       │        └── metodens navn
│      │    │   │       └── Object = vaerdien kan vaere hvad som helst (String, Integer, Date...)
│      │    │   └── String = noeglen er altid en tekst (kolonnenavnet fra databasen)
│      │    └── Map<String, Object> = eet noegel-vaerdi par = EEN kolonne i een raekke
│      └── List<...> = en liste af mange Maps = MANGE raekker fra databasen
└── public = alle kan kalde denne metode
```

**Analogi:** Et Excel-regneark.
- `List` = alle raekker i regnearket
- `Map<String, Object>` = een raekke, hvor kolonnenavnet (String) peger paa vaerdien (Object)
- Fx raekke 1: `{"kunde_navn" -> "Anders", "bil_navn" -> "Toyota", "maanedlig_pris" -> 2999}`

---

### `private final RowMapper<Bil> bilRowMapper = (rs, rowNum) -> { ... }`

Fra [`BilRepository.java`](src/main/java/com/springmad/bilabonnement/repository/BilRepository.java):

```
private final RowMapper<Bil> bilRowMapper = (rs, rowNum) -> {
│       │     │              │               │    │         │
│       │     │              │               │    │         └── lambda-pil: "goer dette"
│       │     │              │               │    └── raekkenummer (bruges ikke, men kraeves)
│       │     │              │               └── rs = ResultSet (raa data fra databasen)
│       │     │              └── variabelnavnet
│       │     └── RowMapper<Bil> = "en opskrift paa at lave en Bil fra en database-raekke"
│       └── final = kan ikke aendres efter den er sat
└── private = kun denne klasse kan se den
```

**Analogi:** En opskrift.
- `RowMapper<Bil>` er opskriften: "saadan laver du en Bil ud af en database-raekke"
- `(rs, rowNum) -> { ... }` er selve trinene i opskriften
- `rs` er ingredienserne (raa data fra databasen)
- Resultatet er et faerdigt Bil-objekt

---

### `new BeanPropertyRowMapper<>(Kunde.class)`

Fra [`KundeJdbcRepository.java`](src/main/java/com/springmad/bilabonnement/repository/KundeJdbcRepository.java):

```
new BeanPropertyRowMapper<>(Kunde.class)
│   │                     │  │
│   │                     │  └── Kunde.class = "jeg vil lave Kunde-objekter"
│   │                     └── <> = Java finder selv typen (Kunde) fra konteksten
│   └── BeanPropertyRowMapper = automatisk RowMapper (matcher kolonnenavne til feltnavne)
└── new = opret et nyt objekt
```

**Analogi:** En automatisk oversaetter — i stedet for at skrive `rs.getString("navn")` for HVERT felt, siger vi bare "her er typen, goer det selv."

---

### `return resultater.stream().findFirst().orElse(null);`

Fra [`BrugerJdbcRepository.java`](src/main/java/com/springmad/bilabonnement/repository/BrugerJdbcRepository.java):

```
return resultater.stream().findFirst().orElse(null);
│      │          │        │           │
│      │          │        │           └── orElse(null) = "hvis tomt, returner null"
│      │          │        └── findFirst() = "tag foerste element"
│      │          └── stream() = "lav listen om til en strom af data"
│      └── resultater = en List<Bruger> fra databasen
└── return = send resultatet tilbage
```

**Samme ting som if/else:**
```java
if (resultater.isEmpty()) {
    return null;
} else {
    return resultater.get(0);
}
```

---

### `@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdato`

Fra [`AbonnementController.java`](src/main/java/com/springmad/bilabonnement/controller/AbonnementController.java):

```
@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdato
│                    │                          │         │
│                    │                          │         └── parameternavnet
│                    │                          └── typen: en dato (fx 2024-01-15)
│                    └── ISO.DATE = formatet er yyyy-MM-dd
└── @DateTimeFormat = "konverter tekst fra formularen til en dato"
```

**Hvorfor?** HTML sender datoer som tekst ("2024-01-15"). `@DateTimeFormat` fortaeller Spring: konverter den tekst til et LocalDate-objekt.

---

### `session.setAttribute("loggedInUser", bruger)` og `session.getAttribute("loggedInUser")`

Fra [`AuthController.java`](src/main/java/com/springmad/bilabonnement/controller/AuthController.java):

```
session.setAttribute("loggedInUser", bruger)   // GEM bruger i session
session.getAttribute("loggedInUser")           // HENT bruger fra session
session.invalidate()                           // SLET hele sessionen (log ud)
```

**Analogi:** En garderobemaerke.
- `setAttribute` = faa et maerke med dit navn paa (login)
- `getAttribute` = vis dit maerke (hvem er du?)
- `invalidate` = aflever dit maerke (logout)

---

### `maanedligPris.compareTo(BigDecimal.ZERO) <= 0`

Fra [`AbonnementService.java`](src/main/java/com/springmad/bilabonnement/service/AbonnementService.java):

```
maanedligPris.compareTo(BigDecimal.ZERO) <= 0
```

**compareTo returnerer:**
- `-1` = prisen er MINDRE end 0 (negativ)
- `0` = prisen ER 0
- `1` = prisen er STOERRE end 0 (positiv)

**Saa `<= 0` betyder:** "er prisen nul eller negativ?"

**Hvorfor ikke bare `maanedligPris <= 0`?** Fordi BigDecimal er et objekt, ikke et tal. Man kan ikke bruge `<=` paa objekter.

---

### `.signum()` — hurtig positiv/negativ/nul check

Fra [`SkadeService.java`](src/main/java/com/springmad/bilabonnement/service/SkadeService.java):

`priser.get(i).signum()` returnerer: `-1` (negativt), `0` (nul), `1` (positivt)

Saa `signum() <= 0` = "er prisen nul eller negativ?" (kortere version af compareTo)

---

### `SpringApplication.run(BilAbonnementApplication.class, args)`

Fra [`BilAbonnementApplication.java`](src/main/java/com/springmad/bilabonnement/BilAbonnementApplication.java):

```
SpringApplication.run(BilAbonnementApplication.class, args)
│                 │   │                          │      │
│                 │   │                          │      └── kommandolinje-argumenter
│                 │   │                          └── .class = "denne klasses type"
│                 │   └── vores hovedklasse
│                 └── run() = start hele applikationen
└── Spring's startmotor
```

**Analogi:** At dreje taendingsnoglen — starter Tomcat, laeser application.properties, finder alle @Controller/@Service/@Repository, saetter @Autowired op.

---

### `th:each="k : ${kunder}"` og `th:href="@{/kunder/slet/{id}(id=${k.id})}"`

Fra [`kunder.html`](src/main/resources/templates/kunder.html):

```
th:each="k : ${kunder}"
         │   │
         │   └── ${kunder} = listen fra controlleren (model.addAttribute)
         └── k = variabelnavn for HVERT element (vi vaelger selv navnet)
```

```
th:href="@{/kunder/slet/{id}(id=${k.id})}"
                        │       │
                        │       └── erstat {id} med kundens faktiske id
                        └── pladseholder i URL'en
```

Hvis `k.id` er 5, bliver URL'en `/kunder/slet/5`.

---

### Getter og Setter — hvorfor?

```java
private String navn;              // LAAST — ingen kan tilgaa udefra

public String getNavn() {         // LAESE-doer — alle kan kigge
    return navn;
}

public void setNavn(String navn) { // SKRIVE-doer — alle kan aendre
    this.navn = navn;              // this.navn = klassens felt, navn = parameteren
}
```

**Hvorfor ikke bare `kunde.navn`?** Fordi feltet er `private`. Getters/setters er de offentlige doere ind til private data.

---

### `new ArrayList<>()` — hvad er `<>`?

```
ArrayList<Object> params = new ArrayList<>();
          │                              │
          │                              └── <> = "diamond operator" — Java kopierer typen fra venstre side
          └── typen staar her (Object)
```

`<>` er en forkortelse for `<Object>`. Java ved hvad typen er fra venstre side af `=`.

---

### `for (Bil b : biler)` — for-each loop

```
for (Bil b : biler)    // "for hver Bil b i listen biler"
```

**Samme ting som:**
```java
for (int i = 0; i < biler.size(); i++) {
    Bil b = biler.get(i);
}
```

---

### `Integer` vs `int`

| | `int` | `Integer` |
|---|---|---|
| Type | Primitiv | Objekt |
| Kan vaere null? | **Nej** — standardvaerdi er 0 | **Ja** — standardvaerdi er null |
| Brug | Naar der ALTID er en vaerdi | Naar vaerdien KAN mangle |
| Eksempel | `int aar` i Bil (altid et tal) | `Integer id` i Kunde (null foer gemt i DB) |
