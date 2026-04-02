package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.service.AbonnementService;
import com.springmad.bilabonnement.service.BilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

// @Controller: haandterer HTTP-requests for abonnementer.
// Hierarkiet er: Controller -> Service -> Repository
@Controller
public class AbonnementController {

    // Controlleren taler kun med services, aldrig direkte med repositories.
    @Autowired
    private AbonnementService abonnementService;

    @Autowired
    private BilService bilService;

    @GetMapping("/abonnementer")
    public String abonnementOversigt(Model model) {
        model.addAttribute("abonnementer", abonnementService.findAlleMedJoin());
        return "abonnementer";
    }

    @GetMapping("/abonnementer/opret")
    public String visOpretForm(Model model) {
        model.addAttribute("biler", bilService.findAll());
        return "abonnement-opret";
    }

    @PostMapping("/abonnementer/opret")
    public String opretAbonnement(@RequestParam("kundeNavn") String kundeNavn,
                                  @RequestParam("bilId") Long bilId,
                                  @RequestParam("startdato")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdato,
                                  @RequestParam(value = "slutdato", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate slutdato,
                                  @RequestParam("maanedligPris") BigDecimal maanedligPris,
                                  Model model) {

        // Tjek: har kunden allerede et aktivt abonnement?
        if (abonnementService.harAktivtAbonnementForKundeNavn(kundeNavn)) {
            model.addAttribute("fejl", "Kunden har allerede et aktivt abonnement.");
            model.addAttribute("biler", bilService.findAll());
            return "abonnement-opret";
        }

        // ===== Exception Handling: try, catch, throw, throws, finally =====
        // try: indeholder kode der KAN fejle (fx database-kald).
        // catch: fanger en specifik exception og haandterer den.
        //        Flere catch-blokke kan fange forskellige typer exceptions.
        // finally: koerer ALTID, uanset om der var en exception eller ej.
        //          Bruges til oprydning (fx logning, ressource-frigoerelse).
        // throw: bruges i AbonnementService til at kaste exceptions (se service-laget).
        // throws: bruges i RowMapper-metoder til at erklare at de KAN kaste SQLException.
        try {
            // Controller -> Service -> Repository
            // Servicen kan kaste IllegalArgumentException eller IllegalStateException (throw).
            // Repository kan kaste EmptyResultDataAccessException hvis kunden ikke findes.
            abonnementService.opretAbonnementHvisMuligt(
                    kundeNavn, bilId.intValue(), startdato, slutdato, maanedligPris
            );

        } catch (EmptyResultDataAccessException e) {
            // Catch blok 1: fanger database-fejl (kunden findes ikke i databasen).
            // Programmet crasher IKKE — vi viser en fejlbesked til brugeren i stedet.
            model.addAttribute("fejl", "Kunden findes ikke. Opret kunden foerst under /kunder.");
            model.addAttribute("biler", bilService.findAll());
            return "abonnement-opret";

        } catch (IllegalArgumentException e) {
            // Catch blok 2: fanger ugyldigt input (tomt kundenavn, negativ pris).
            // e.getMessage() returnerer den besked der blev givet med throw.
            model.addAttribute("fejl", e.getMessage());
            model.addAttribute("biler", bilService.findAll());
            return "abonnement-opret";

        } catch (IllegalStateException e) {
            // Catch blok 3: fanger logisk fejl (kunden har allerede et aktivt abonnement).
            model.addAttribute("fejl", e.getMessage());
            model.addAttribute("biler", bilService.findAll());
            return "abonnement-opret";

        } finally {
            // Finally-blokken koerer ALTID — uanset om der var en exception eller ej.
            // Bruges typisk til oprydning: lukke forbindelser, logning osv.
            // Her logger vi at forsoget blev gennemfoert (til fejlsoegning).
            System.out.println("Abonnement-oprettelse forsoegte for kunde: " + kundeNavn);
        }

        return "redirect:/abonnementer";
    }
}
