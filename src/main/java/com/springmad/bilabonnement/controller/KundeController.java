package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Kunde;
import com.springmad.bilabonnement.service.KundeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// @Controller: haandterer HTTP-requests for kunder.
// Hierarkiet er: Controller -> Service -> Repository
@Controller
@RequestMapping("/kunder")
public class KundeController {

    // Controlleren taler kun med servicen, aldrig direkte med repository.
    @Autowired
    private KundeService kundeService;

    // Endpoint: GET /kunder
    @GetMapping
    public String kunderSide(Model model) {
        model.addAttribute("kunde", new Kunde());
        model.addAttribute("kunder", kundeService.findAll());
        return "kunder";
    }

    // Endpoint: POST /kunder
    @PostMapping
    public String opretKunde(@ModelAttribute("kunde") Kunde kunde) {
        kundeService.opretKunde(kunde);
        return "redirect:/kunder";
    }
}
