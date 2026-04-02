package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bil;
import com.springmad.bilabonnement.repository.BilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Controller der haandterer visning og oprettelse af biler.
// Foelger MVC-princippet: controlleren modtager requests og sender data til viewet.
@Controller
@RequestMapping("/biler")
public class BilController {

    // @Autowired: Spring indsaetter BilRepository automatisk (dependency injection).
    // BilRepository bruges til at tilgaa tabellen "biler" i databasen via JdbcTemplate.
    @Autowired
    private BilRepository bilRepository;

    // Endpoint: GET /biler
    // Viser oversigten over alle biler og en formular til at oprette en ny bil.
    @GetMapping
    public String bilerPage(Model model) {
        // Tomt Bil-objekt til Thymeleaf-formularen (data-binding med th:object).
        model.addAttribute("bil", new Bil());
        // Henter alle biler fra databasen og sender dem til viewet.
        model.addAttribute("biler", bilRepository.findAll());
        return "biler";
    }

    // Endpoint: POST /biler
    // Kaldes naar formularen til oprettelse af bil indsendes.
    @PostMapping
    public String createBil(@ModelAttribute("bil") Bil bil) {
        // @ModelAttribute binder formularfelter til Bil-objektet.
        // Gemmer bilen i databasen via JdbcTemplate INSERT.
        bilRepository.save(bil);
        // Redirect undgaar dobbelt-submit ved refresh (Post/Redirect/Get pattern).
        return "redirect:/biler";
    }
}
