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

import java.util.List;
import java.util.TreeSet;

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

        // ===== List =====
        // List er en ordnet samling der tillader dubletter.
        // findAll() returnerer en List<Bil> fra databasen.
        // List har raekkefoelge (foerste bil forbliver foerst), og vi kan have
        // to biler med samme navn (dubletter er tilladt).
        List<Bil> biler = bilRepository.findAll();
        model.addAttribute("biler", biler);

        // ===== TreeSet =====
        // TreeSet er en Set-implementering der:
        //   - IKKE tillader dubletter (ligesom alle Sets)
        //   - Automatisk SORTERER elementerne (mindste foerst)
        //   - Bruger et traee internt (roed-sort-traee)
        // Vi bruger TreeSet her til at finde alle unikke aar-tal fra bilerne.
        // Hvis vi har biler fra 2022, 2024, 2022, 2023 faar vi: [2022, 2023, 2024]
        // - Ingen dubletter (2022 vises kun een gang)
        // - Automatisk sorteret (mindste aar foerst)
        // Dette bruges i viewet til at vise hvilke aargange der findes i systemet.
        TreeSet<Integer> unikkeAar = new TreeSet<>();
        for (Bil b : biler) {
            unikkeAar.add(b.getAar());
            // add() ignorerer automatisk vaerdier der allerede er i settet (ingen dubletter)
        }
        model.addAttribute("unikkeAar", unikkeAar);

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
