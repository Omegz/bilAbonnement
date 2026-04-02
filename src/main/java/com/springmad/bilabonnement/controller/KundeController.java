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
    // @PostMapping haandterer HTTP POST-requests — typisk til at oprette data.
    // @ModelAttribute binder formdata fra HTML-formularen til et Kunde-objekt.
    @PostMapping
    public String opretKunde(@ModelAttribute("kunde") Kunde kunde) {
        kundeService.opretKunde(kunde);
        return "redirect:/kunder";
    }

    // Endpoint: GET /kunder/slet/{id}
    // ===== @PathVariable =====
    // @PathVariable henter vaerdier direkte fra URL-stien.
    // Hvis URL'en er /kunder/slet/5, saa bliver {id} = 5.
    // Det er anderledes end @RequestParam, som henter fra query-parametre (?id=5).
    //
    // @PathVariable bruges typisk til:
    //   - Slet: /kunder/slet/{id}
    //   - Vis detaljer: /kunder/{id}
    //   - Opdater: /kunder/opdater/{id}
    @GetMapping("/slet/{id}")
    public String sletKunde(@PathVariable int id) {
        // Controller -> Service -> Repository
        kundeService.sletKunde(id);
        return "redirect:/kunder";
    }
}
