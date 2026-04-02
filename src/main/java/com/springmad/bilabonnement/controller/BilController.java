package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bil;
import com.springmad.bilabonnement.service.BilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.TreeSet;

// @Controller: haandterer HTTP-requests og sender data til viewet.
// Controlleren kalder SERVICEN (ikke repository direkte).
// Hierarkiet er: Controller -> Service -> Repository
@Controller
@RequestMapping("/biler")
public class BilController {

    // @Autowired: Spring indsaetter BilService automatisk (dependency injection).
    // Controlleren taler kun med servicen, aldrig direkte med repository.
    @Autowired
    private BilService bilService;

    // Endpoint: GET /biler
    @GetMapping
    public String bilerPage(Model model) {
        model.addAttribute("bil", new Bil());

        // ===== List =====
        // List er en ordnet samling der tillader dubletter.
        // Service-laget henter data fra repository og returnerer en List<Bil>.
        List<Bil> biler = bilService.findAll();
        model.addAttribute("biler", biler);

        // ===== Set-metoder fra Collection Framework =====
        // TreeSet: ingen dubletter + automatisk sortering.
        // Finder unikke aargange fra bilerne til visning i viewet.
        TreeSet<Integer> unikkeAar = new TreeSet<>();

        // add(): tilfojer en vaerdi til settet (ignorerer dubletter).
        for (Bil b : biler) {
            unikkeAar.add(b.getAar());
        }

        // contains(): returnerer true hvis vaerdien findes i settet.
        boolean har2024 = unikkeAar.contains(2024);
        model.addAttribute("har2024", har2024);

        // size(): returnerer antal elementer i settet.
        int antalUnikkeAar = unikkeAar.size();
        model.addAttribute("antalUnikkeAar", antalUnikkeAar);

        // isEmpty(): returnerer true hvis settet er tomt.
        boolean harAar = !unikkeAar.isEmpty();
        model.addAttribute("harAar", harAar);

        model.addAttribute("unikkeAar", unikkeAar);

        return "biler";
    }

    // Endpoint: POST /biler
    @PostMapping
    public String createBil(@ModelAttribute("bil") Bil bil) {
        // Controlleren kalder servicen, servicen kalder repository.
        bilService.save(bil);
        return "redirect:/biler";
    }
}
