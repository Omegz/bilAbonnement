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
public class AbonnementController {

    private final AbonnementJdbcRepository abonnementJdbcRepository;
    private final BilRepository bilRepository;

    public AbonnementController(AbonnementJdbcRepository abonnementJdbcRepository,
                                BilRepository bilRepository) {
        this.abonnementJdbcRepository = abonnementJdbcRepository;
        this.bilRepository = bilRepository;
    }

    @GetMapping("/abonnementer")
    // Viser oversigt med JOIN-data.
    public String abonnementOversigt(Model model) {
        model.addAttribute("abonnementer", abonnementJdbcRepository.findAlleMedJoin());
        return "abonnementer";
    }

    @GetMapping("/abonnementer/opret")
    // Viser formular til at oprette abonnement: kunde-navn + valg af bil.
    public String visOpretForm(Model model) {
        model.addAttribute("biler", bilRepository.findAll());
        return "abonnement-opret";
    }

    @PostMapping("/abonnementer/opret")
    // Opretter abonnement, hvis kunden ikke allerede har et aktivt abonnement.
    public String opretAbonnement(@RequestParam("kundeNavn") String kundeNavn,
                                  @RequestParam("bilId") Long bilId,
                                  @RequestParam("startdato")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdato,
                                  @RequestParam(value = "slutdato", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate slutdato,
                                  @RequestParam("maanedligPris") BigDecimal maanedligPris,
                                  Model model) {

        // Tjek: har kunden allerede et aktivt abonnement?
        if (abonnementJdbcRepository.harAktivtAbonnementForKundeNavn(kundeNavn)) {
            model.addAttribute("fejl", "Kunden har allerede et aktivt abonnement.");
            model.addAttribute("biler", bilRepository.findAll());
            return "abonnement-opret";
        }

        try {
            abonnementJdbcRepository.opretAbonnement(
                    kundeNavn,
                    bilId.intValue(),
                    startdato,
                    slutdato,
                    maanedligPris
            );
        } catch (EmptyResultDataAccessException e) {
            // Hvis kunden ikke findes i databasen.
            model.addAttribute("fejl", "Kunden findes ikke. Opret kunden først under /kunder.");
            model.addAttribute("biler", bilRepository.findAll());
            return "abonnement-opret";
        }

        return "redirect:/abonnementer";
    }
}
