//     Denne controller er lavet separat fra PageController, fordi
//        bil-funktionalitet er en selvstændig del af applikationen, der
//             håndterer data (CRUD-lignende operationer) og derfor naturligt
//             skal ligge i sin egen controller.
//        PageController bruges kun til statiske sider som index og about, mens
//             BilController håndterer databasekald, formularer og visning af
//             data for biler.

package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bil;
import com.springmad.bilabonnement.repository.BilRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Controlleren styrer alle HTTP-forespørgsler under /biler.
@Controller
@RequestMapping("/biler")
public class BilController {

    private final BilRepository bilRepository;

    // Repository’et injiceres via constructor injection, så controlleren kan hente og gemme biler i databasen.
    public BilController(BilRepository bilRepository) {
        this.bilRepository = bilRepository;
    }

//    Når brugeren går til /biler, oprettes en tom Bil-instans til formularen, og
//    alle biler hentes fra databasen og sendes til Thymeleaf-skabelonen
//    biler.html.
    @GetMapping
    public String bilerPage(Model model) {
        model.addAttribute("bil", new Bil());
        model.addAttribute("biler", bilRepository.findAll());
        return "biler";
    }
//
//    Når formularen indsendes, modtager controlleren et Bil-objekt, gemmer
//    det i databasen og redirecter tilbage til bil-oversigten.
//
    @PostMapping
    public String createBil(@ModelAttribute("bil") Bil bil) {
        bilRepository.save(bil);
        return "redirect:/biler";
    }
}
