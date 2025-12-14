package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.repository.BrugerJdbcRepository;
import com.springmad.bilabonnement.repository.ForretningJdbcRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Dashboard kun til FORRETNING-rollen.
@Controller
@RequestMapping("/dashboard")
public class ForretningController {

    private final ForretningJdbcRepository forretningJdbcRepository;
    private final BrugerJdbcRepository brugerJdbcRepository;

    public ForretningController(ForretningJdbcRepository forretningJdbcRepository,
                                BrugerJdbcRepository brugerJdbcRepository) {
        this.forretningJdbcRepository = forretningJdbcRepository;
        this.brugerJdbcRepository = brugerJdbcRepository;
    }

    @GetMapping
    public String dashboardSide(Model model, HttpSession session) {
        if (!erForretning(session)) {
            return "redirect:/login";
        }

        model.addAttribute("antalAktive", forretningJdbcRepository.antalAktiveUdlejninger());
        model.addAttribute("samletPris", forretningJdbcRepository.samletMaanedligPrisAktive());
        model.addAttribute("aktive", forretningJdbcRepository.aktiveUdlejningerMedJoin());

        return "dashboard";
    }

    // “Svag re-login” – mest for at matche jeres andre moduler (valgfrit)
    @PostMapping("/refresh")
    public String refresh(@RequestParam("medarbejderNavn") String medarbejderNavn,
                          @RequestParam("medarbejderPassword") String medarbejderPassword,
                          Model model,
                          HttpSession session) {

        if (!erForretning(session)) {
            return "redirect:/login";
        }

        Bruger bruger = brugerJdbcRepository.findByNavnOgPassword(medarbejderNavn, medarbejderPassword);
        if (bruger == null || !"FORRETNING".equals(bruger.getRolle())) {
            model.addAttribute("fejl", "Forkert login eller rolle. Kun FORRETNING må se dashboard.");
            model.addAttribute("antalAktive", forretningJdbcRepository.antalAktiveUdlejninger());
            model.addAttribute("samletPris", forretningJdbcRepository.samletMaanedligPrisAktive());
            model.addAttribute("aktive", forretningJdbcRepository.aktiveUdlejningerMedJoin());
            return "dashboard";
        }

        return "redirect:/dashboard";
    }

    private boolean erForretning(HttpSession session) {
        Object obj = session.getAttribute("loggedInUser");
        if (!(obj instanceof Bruger)) return false;
        Bruger b = (Bruger) obj;
        return "FORRETNING".equals(b.getRolle());
    }
}
