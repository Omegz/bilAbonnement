package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.repository.BrugerJdbcRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Simpel login/signup controller (MVP).
// Ansvarlig for autentificering og session-haandtering.
@Controller
// Marker klassen som Spring MVC controller, saa den kan haandtere HTTP requests.
public class AuthController {

    private final BrugerJdbcRepository brugerJdbcRepository;
    // Dependency: bruges til at gemme og hente brugere fra databasen via SQL (JdbcTemplate).

    public AuthController(BrugerJdbcRepository brugerJdbcRepository) {
        // Constructor injection: Spring injicerer repository automatisk ved startup.
        this.brugerJdbcRepository = brugerJdbcRepository;
        // Gemmer reference til repository, saa controlleren kan oprette og finde brugere.
    }

    @GetMapping("/signup")
    // Endpoint: GET /signup
    // Viser signup-siden med en tom Bruger-model bundet til formularen.
    public String signupPage(Model model) {
        // Tilfoejer et tomt Bruger-objekt til modellen.
        // Bruges af Thymeleaf til th:object i signup-formularen.
        model.addAttribute("bruger", new Bruger());
        return "signup";
        // Returnerer view-navn: templates/signup.html
    }

    @PostMapping("/signup")
    // Endpoint: POST /signup
    // Kaldes naar signup-formularen bliver submitted.
    // Opretter en ny bruger i databasen (meget simpel validering).
    public String signup(@ModelAttribute("bruger") Bruger bruger, Model model) {
        // @ModelAttribute binder alle inputfelter direkte til Bruger-objektet.

        // Minimal validering
        if (bruger.getNavn() == null || bruger.getNavn().isBlank()) {
            // Tjek at navn er udfyldt.
            model.addAttribute("fejl", "Navn maa ikke vaere tomt.");
            return "signup";
            // Bliver paa signup-siden, saa brugeren kan rette input.
        }

        if (bruger.getAlder() == null || bruger.getAlder() < 0) {
            // Tjek at alder er sat og positiv.
            model.addAttribute("fejl", "Alder skal vaere et positivt tal.");
            return "signup";
        }

        if (bruger.getRolle() == null || bruger.getRolle().isBlank()) {
            // Rollen bruges senere til rollebaseret adgang (Dataregistrering, Skade, Forretning).
            model.addAttribute("fejl", "Du skal vaelge en rolle.");
            return "signup";
        }

        if (bruger.getPassword() == null || bruger.getPassword().isBlank()) {
            // Simpelt password-tjek (ingen hashing i MVP).
            model.addAttribute("fejl", "Password maa ikke vaere tomt.");
            return "signup";
        }

        // Opretter brugeren i databasen.
        // Selve INSERT SQL ligger i BrugerJdbcRepository.
        brugerJdbcRepository.opretBruger(bruger);

        return "redirect:/login";
        // Redirect til login-siden efter succesfuld signup.
        // Redirect bruges for at undgaa dobbelt-submit.
    }

    @GetMapping("/login")
    // Endpoint: GET /login
    // Viser login-siden.
    public String loginPage() {
        return "login";
        // Returnerer view-navn: templates/login.html
    }

    @PostMapping("/login")
    // Endpoint: POST /login
    // Kaldes naar login-formularen bliver submitted.
    // Finder bruger i databasen og gemmer brugeren i session.
    public String login(@RequestParam("navn") String navn,
                        // Laeser brugernavn fra login-formularen.

                        @RequestParam("password") String password,
                        // Laeser password fra login-formularen.

                        HttpSession session,
                        // Bruges til at gemme logged-in bruger paa tværs af requests.

                        Model model) {

        // Forsoger at finde bruger med matchende navn og password.
        Bruger bruger = brugerJdbcRepository.findByNavnOgPassword(navn, password);
        // SQL-logik ligger i repository (SELECT ... WHERE navn = ? AND password = ?).

        if (bruger == null) {
            // Hvis ingen bruger matcher login-oplysningerne.
            model.addAttribute("fejl", "Forkert navn eller password.");
            return "login";
            // Bliver paa login-siden med fejlbesked.
        }

        // Gemmer brugeren i session.
        // Bruges senere til:
        // - rollebaseret adgang
        // - vise korrekt navbar
        // - beskytte endpoints
        session.setAttribute("loggedInUser", bruger);

        return "redirect:/";
        // Redirect til forsiden efter succesfuld login.
    }

    @GetMapping("/logout")
    // Endpoint: GET /logout
    // Logger brugeren ud ved at rydde sessionen.
    public String logout(HttpSession session) {
        // Fjerner alle session-data, inkl. loggedInUser.
        session.invalidate();
        return "redirect:/login";
        // Sender brugeren tilbage til login-siden.
    }
}
