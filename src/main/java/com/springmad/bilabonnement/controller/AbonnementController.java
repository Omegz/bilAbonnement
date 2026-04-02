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

        try {
            // Controller -> Service -> Repository
            abonnementService.opretAbonnementHvisMuligt(
                    kundeNavn, bilId.intValue(), startdato, slutdato, maanedligPris
            );
        } catch (EmptyResultDataAccessException e) {
            model.addAttribute("fejl", "Kunden findes ikke. Opret kunden foerst under /kunder.");
            model.addAttribute("biler", bilService.findAll());
            return "abonnement-opret";
        }

        return "redirect:/abonnementer";
    }
}
