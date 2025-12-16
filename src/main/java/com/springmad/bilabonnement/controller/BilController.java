// PageController bruges kun til simple, statiske sider
// (fx index og about), mens BilController haandterer
// funktionalitet med databaseadgang, formularer og visning
// af data relateret til biler.
//
// Dette foelger MVC-princippet, hvor hver controller
// har et klart ansvarsomraade.

package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bil;
import com.springmad.bilabonnement.repository.BilRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Marker klassen som en Spring MVC controller.
@Controller
// Alle endpoints i denne controller starter med /biler.
@RequestMapping("/biler")
public class BilController {

    private final BilRepository bilRepository;
    // Dependency: BilRepository bruges til at tilgaa tabellen "biler" i databasen.
    // Repository'et indeholder CRUD-funktionalitet via Spring Data.

    // Constructor injection: Spring injicerer BilRepository automatisk.
    public BilController(BilRepository bilRepository) {
        this.bilRepository = bilRepository;
        // Gemmer repository-referencen, saa controlleren kan bruge den i endpoints.
    }

    // Endpoint: GET /biler
    // Viser oversigten over alle biler samt en formular til at oprette en ny bil.
    @GetMapping
    public String bilerPage(Model model) {

        // Tilfoejer en tom Bil-instans til modellen.
        // Denne bruges af Thymeleaf-formularen til data-binding.
        model.addAttribute("bil", new Bil());

        // Henter alle biler fra databasen og sender dem til viewet.
        // bruges til at vise en tabel med eksisterende biler.
        model.addAttribute("biler", bilRepository.findAll());

        return "biler";
        // Returnerer view-navn: templates/biler.html
    }

    // Endpoint: POST /biler
    // Kaldes naar formularen til oprettelse af bil indsendes.
    @PostMapping
    public String createBil(@ModelAttribute("bil") Bil bil) {
        // @ModelAttribute binder formularfelter direkte til Bil-objektet.

        // Gemmer bilen i databasen.
        // save() er en standard CRUD-metode fra CrudRepository.
        bilRepository.save(bil);

        return "redirect:/biler";
        // Redirect bruges for at:
        // - undgaa dobbelt-submit ved refresh
        // - opdatere listen over biler efter oprettelse
    }
}
