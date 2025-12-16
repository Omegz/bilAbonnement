package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.repository.BrugerJdbcRepository;
import com.springmad.bilabonnement.repository.ForretningJdbcRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Controller for forretningsudviklere.
// Denne rolle har kun laese-adgang og bruges til rapportering og overblik (dashboard).
// Controlleren passer direkte til opgavens krav om:
// - antal udlejede biler
// - samlet pris paa aktive abonnementer
@Controller
@RequestMapping("/dashboard")
public class ForretningController {

    private final ForretningJdbcRepository forretningJdbcRepository;
    // Repository med SQL-queries til KPI-data og JOINs
    // (fx aktive abonnementer, samlet pris, oversigter).

    private final BrugerJdbcRepository brugerJdbcRepository;
    // Bruges til svag re-login kontrol ved refresh af dashboard.

    public ForretningController(ForretningJdbcRepository forretningJdbcRepository,
                                BrugerJdbcRepository brugerJdbcRepository) {
        // Constructor injection sikrer loose coupling og testbarhed.
        this.forretningJdbcRepository = forretningJdbcRepository;
        this.brugerJdbcRepository = brugerJdbcRepository;
    }

    @GetMapping
    // Endpoint: GET /dashboard
    // Viser dashboard for brugere med rollen FORRETNING.
    public String dashboardSide(Model model, HttpSession session) {

        // Rolle-check via session.
        // loggedInUser saettes ved login i AuthController.
        if (!erForretning(session)) {
            return "redirect:/login";
        }

        // KPI-data til dashboardet.
        model.addAttribute("antalAktive", forretningJdbcRepository.antalAktiveUdlejninger());
        // Antal aktive udlejninger (abonnementer uden udloeb).

        model.addAttribute("samletPris", forretningJdbcRepository.samletMaanedligPrisAktive());
        // Samlet maanedlig pris paa alle aktive abonnementer.

        model.addAttribute("aktive", forretningJdbcRepository.aktiveUdlejningerMedJoin());
        // Liste med aktive abonnementer (JOIN mellem kunde, bil og abonnement).

        return "dashboard";
        // templates/dashboard.html
    }

    @PostMapping("/refresh")
    // Endpoint: POST /dashboard/refresh
    // Valgfri "svag re-login", primart for konsistens med andre moduler i systemet.
    public String refresh(@RequestParam("medarbejderNavn") String medarbejderNavn,
                          @RequestParam("medarbejderPassword") String medarbejderPassword,
                          Model model,
                          HttpSession session) {

        // Sikkerhed: brugeren skal stadig have korrekt rolle i session.
        if (!erForretning(session)) {
            return "redirect:/login";
        }

        // Ekstra check mod databasen.
        Bruger bruger = brugerJdbcRepository.findByNavnOgPassword(medarbejderNavn, medarbejderPassword);

        if (bruger == null || !"FORRETNING".equals(bruger.getRolle())) {
            // Hvis login eller rolle er forkert, vis fejl men behold data paa siden.
            model.addAttribute("fejl", "Forkert login eller rolle. Kun FORRETNING maa se dashboard.");

            // Genindlaes data saa siden stadig fungerer.
            model.addAttribute("antalAktive", forretningJdbcRepository.antalAktiveUdlejninger());
            model.addAttribute("samletPris", forretningJdbcRepository.samletMaanedligPrisAktive());
            model.addAttribute("aktive", forretningJdbcRepository.aktiveUdlejningerMedJoin());

            return "dashboard";
        }

        // Ved succes redirectes blot tilbage til dashboard.
        return "redirect:/dashboard";
    }

    // Hjaelpemetode til rolle-check.
    // Bruges flere steder for at holde koden overskuelig.
    private boolean erForretning(HttpSession session) {
        Object obj = session.getAttribute("loggedInUser");
        if (!(obj instanceof Bruger)) return false;
        Bruger b = (Bruger) obj;
        return "FORRETNING".equals(b.getRolle());
    }
}
