package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Kunde;
import com.springmad.bilabonnement.repository.KundeJdbcRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Controller der haandterer kunder.
// Kunder er en central del af systemet, da abonnementer altid knyttes til en kunde.
// Denne controller understotter simpel CRUD (opret + vis).
@Controller
@RequestMapping("/kunder")
public class KundeController {

    private final KundeJdbcRepository kundeJdbcRepository;
    // Repository der indeholder SQL til tabellen "kunder".

    public KundeController(KundeJdbcRepository kundeJdbcRepository) {
        // Constructor injection sikrer at controlleren altid har adgang til databasen.
        this.kundeJdbcRepository = kundeJdbcRepository;
    }

    @GetMapping
    // Endpoint: GET /kunder
    // Viser:
    // - formular til oprettelse af kunde
    // - tabel med alle eksisterende kunder
    public String kunderSide(Model model) {

        // Tomt Kunde-objekt bruges til formular-binding i Thymeleaf.
        model.addAttribute("kunde", new Kunde());

        // Liste med alle kunder fra databasen.
        // Bruges baade til oversigt og senere som dropdown i andre moduler.
        model.addAttribute("kunder", kundeJdbcRepository.findAll());

        return "kunder";
        // templates/kunder.html
    }

    @PostMapping
    // Endpoint: POST /kunder
    // Modtager data fra formularen og opretter kunden i databasen.
    public String opretKunde(@ModelAttribute("kunde") Kunde kunde) {

        // Gemmer kunden via repository (INSERT SQL).
        kundeJdbcRepository.opretKunde(kunde);

        // Redirect sikrer:
        // - ingen dobbelt-submit
        // - opdateret kundeliste efter oprettelse
        return "redirect:/kunder";
    }
}
