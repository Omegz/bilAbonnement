package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.service.BrugerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// @Controller: haandterer HTTP-requests for login og signup.
// Hierarkiet er: Controller -> Service -> Repository
@Controller
public class AuthController {

    // Controlleren taler kun med servicen, aldrig direkte med repository.
    @Autowired
    private BrugerService brugerService;

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("bruger", new Bruger());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("bruger") Bruger bruger, Model model) {

        if (bruger.getNavn() == null || bruger.getNavn().isBlank()) {
            model.addAttribute("fejl", "Navn maa ikke vaere tomt.");
            return "signup";
        }
        if (bruger.getAlder() == null || bruger.getAlder() < 0) {
            model.addAttribute("fejl", "Alder skal vaere et positivt tal.");
            return "signup";
        }
        if (bruger.getRolle() == null || bruger.getRolle().isBlank()) {
            model.addAttribute("fejl", "Du skal vaelge en rolle.");
            return "signup";
        }
        if (bruger.getPassword() == null || bruger.getPassword().isBlank()) {
            model.addAttribute("fejl", "Password maa ikke vaere tomt.");
            return "signup";
        }

        // Controller -> Service -> Repository
        brugerService.opretBruger(bruger);
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

        // Controller kalder service (ikke repository direkte)
        Bruger bruger = brugerService.findByNavnOgPassword(navn, password);

        if (bruger == null) {
            model.addAttribute("fejl", "Forkert navn eller password.");
            return "login";
        }

        session.setAttribute("loggedInUser", bruger);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
