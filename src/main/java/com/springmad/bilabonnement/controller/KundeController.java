package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Kunde;
import com.springmad.bilabonnement.repository.KundeJdbcRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Controller til at vise og oprette kunder.
@Controller
@RequestMapping("/kunder")
public class KundeController {

    private final KundeJdbcRepository kundeJdbcRepository;

    public KundeController(KundeJdbcRepository kundeJdbcRepository) {
        this.kundeJdbcRepository = kundeJdbcRepository;
    }

    @GetMapping
    // Viser formular til at oprette kunde + liste over alle kunder.
    public String kunderSide(Model model) {
        model.addAttribute("kunde", new Kunde());
        model.addAttribute("kunder", kundeJdbcRepository.findAll());
        return "kunder";
    }

    @PostMapping
    // Modtager data fra formularen og opretter en kunde.
    public String opretKunde(@ModelAttribute("kunde") Kunde kunde) {
        kundeJdbcRepository.opretKunde(kunde);
        return "redirect:/kunder";
    }
}
