package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.repository.BrugerJdbcRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Simpel login/signup controller (MVP).
// Bruger gemmes i session som "loggedInUser".
@Controller
public class AuthController {

    private final BrugerJdbcRepository brugerJdbcRepository;

    public AuthController(BrugerJdbcRepository brugerJdbcRepository) {
        this.brugerJdbcRepository = brugerJdbcRepository;
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("bruger", new Bruger());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("bruger") Bruger bruger, Model model) {
        // Minimal validering (MVP)
        if (bruger.getNavn() == null || bruger.getNavn().isBlank()) {
            model.addAttribute("fejl", "Navn må ikke være tomt.");
            return "signup";
        }
        if (bruger.getAlder() == null || bruger.getAlder() < 0) {
            model.addAttribute("fejl", "Alder skal være et positivt tal.");
            return "signup";
        }
        if (bruger.getRolle() == null || bruger.getRolle().isBlank()) {
            model.addAttribute("fejl", "Du skal vælge en rolle.");
            return "signup";
        }
        if (bruger.getPassword() == null || bruger.getPassword().isBlank()) {
            model.addAttribute("fejl", "Password må ikke være tomt.");
            return "signup";
        }

        brugerJdbcRepository.opretBruger(bruger);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("navn") String navn,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        Bruger bruger = brugerJdbcRepository.findByNavnOgPassword(navn, password);

        if (bruger == null) {
            model.addAttribute("fejl", "Forkert navn eller password.");
            return "login";
        }

        // Gemmer bruger i session så vi kan vise rollebaseret UI senere
        session.setAttribute("loggedInUser", bruger);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
