package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.repository.AbonnementJdbcRepository;
import com.springmad.bilabonnement.repository.BilRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

// Controller der viser og opretter abonnementer.
@Controller
// Binder denne klasse til Spring MVC saa den kan modtage requests.
public class AbonnementController {

    private final AbonnementJdbcRepository abonnementJdbcRepository;
    // Dependency: kalder SQL-logik for abonnementer (JOIN, insert, checks). Ligger i repository laget.

    private final BilRepository bilRepository;
    // Dependency: henter biler fra tabellen "biler" (typisk via CrudRepository). Bruges i opret-formular.

    public AbonnementController(AbonnementJdbcRepository abonnementJdbcRepository,
                                BilRepository bilRepository) {
        // Constructor injection: Spring giver os instanserne automatisk ved startup.
        this.abonnementJdbcRepository = abonnementJdbcRepository;
        // Gemmer reference til abonnement repository, saa controller kan bruge det i endpoints.
        this.bilRepository = bilRepository;
        // Gemmer reference til bil repository, saa vi kan hente biler til view.
    }

    @GetMapping("/abonnementer")
    // Endpoint: GET /abonnementer
    // Bruges til at vise en oversigt over alle abonnementer i systemet.
    public String abonnementOversigt(Model model) {
        // Laver et model-attribut "abonnementer" som Thymeleaf siden kan loope over i en tabel.
        model.addAttribute("abonnementer", abonnementJdbcRepository.findAlleMedJoin());
        // findAlleMedJoin() ligger i AbonnementJdbcRepository og indeholder SQL med JOIN.
        return "abonnementer";
        // Returnerer view-navn: templates/abonnementer.html
    }

    @GetMapping("/abonnementer/opret")
    // Endpoint: GET /abonnementer/opret
    // Viser formularen hvor man kan oprette et nyt abonnement.
    // Her hentes biler fra databasen, saa brugeren kan vaelge en bil i dropdown.
    public String visOpretForm(Model model) {
        // Sender en liste af biler til viewet, typisk brugt til <select> i Thymeleaf.
        model.addAttribute("biler", bilRepository.findAll());
        // bilRepository.findAll() henter alle biler fra tabellen "biler".
        return "abonnement-opret";
        // Returnerer view-navn: templates/abonnement-opret.html
    }

    @PostMapping("/abonnementer/opret")
    // Endpoint: POST /abonnementer/opret
    // Kaldes naar formularen bliver submitted fra abonnement-opret.html
    // Opretter abonnement i databasen, men kun hvis kunden ikke allerede har et aktivt abonnement.
    public String opretAbonnement(@RequestParam("kundeNavn") String kundeNavn,
                                  // Læser input-feltet "kundeNavn" fra formularen.

                                  @RequestParam("bilId") Long bilId,
                                  // Læser valgt bilId fra dropdown (typisk <select name="bilId">).

                                  @RequestParam("startdato")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdato,
                                  // Læser startdato fra formularen og parser ISO-format (yyyy-mm-dd) til LocalDate.

                                  @RequestParam(value = "slutdato", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate slutdato,
                                  // Læser slutdato fra formularen (valgfri). required=false betyder feltet maa mangle.

                                  @RequestParam("maanedligPris") BigDecimal maanedligPris,
                                  // Læser maanedlig pris som decimal. BigDecimal er bedst til penge.

                                  Model model) {
        // Model bruges til at sende fejlbeskeder og data tilbage til view, hvis noget gaar galt.

        // Tjek: har kunden allerede et aktivt abonnement?
        if (abonnementJdbcRepository.harAktivtAbonnementForKundeNavn(kundeNavn)) {
            // Kald til repository (SQL check). Formaal: forretningsregel i systemet.
            model.addAttribute("fejl", "Kunden har allerede et aktivt abonnement.");
            // Sender fejlbesked til viewet, saa brugeren kan se hvad der gik galt.
            model.addAttribute("biler", bilRepository.findAll());
            // Sender biler igen, saa dropdown stadig virker, naar siden vises med fejl.
            return "abonnement-opret";
            // Bliver paa samme formular-side (ingen redirect), saa brugeren kan rette input.
        }

        try {
            abonnementJdbcRepository.opretAbonnement(
                    // Kald til repository der laver INSERT i tabellen "abonnementer".
                    kundeNavn,
                    // Bruges til at finde kundens id i databasen (typisk SELECT id FROM kunder WHERE navn = ?).
                    bilId.intValue(),
                    // Converter fra Long til int fordi repository metoden forventer int.
                    startdato,
                    // Startdato gemmes i DB (DATE).
                    slutdato,
                    // Slutdato kan vaere null (fx hvis "unlimited" eller ubestemt periode).
                    maanedligPris
                    // Pris gemmes i DB (DECIMAL).
            );
        } catch (EmptyResultDataAccessException e) {
            // Hvis kunden ikke findes i databasen.
            // Typisk fordi repository laver en query som forventer at finde kunden via navn, men ingen raekke findes.
            model.addAttribute("fejl", "Kunden findes ikke. Opret kunden først under /kunder.");
            // Fejlbesked guider brugeren til at oprette kunden foerst.
            model.addAttribute("biler", bilRepository.findAll());
            // Sender biler igen, saa dropdown stadig virker i formularen.
            return "abonnement-opret";
            // Returnerer tilbage til samme side med fejl.
        }

        return "redirect:/abonnementer";
        // Redirect efter succes:
        // - Undgaar dobbelt-submit hvis brugeren refresher
        // - Sender brugeren tilbage til oversigten
    }
}