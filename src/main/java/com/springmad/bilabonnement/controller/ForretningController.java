package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.model.RolleDefinitioner;
import com.springmad.bilabonnement.service.BrugerService;
import com.springmad.bilabonnement.service.ForretningService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

// @Controller: haandterer HTTP-requests for dashboard.
// Hierarkiet er: Controller -> Service -> Repository
@Controller
@RequestMapping("/dashboard")
public class ForretningController {

    // Controlleren taler kun med services, aldrig direkte med repositories.
    @Autowired
    private ForretningService forretningService;

    @Autowired
    private BrugerService brugerService;

    @GetMapping
    public String dashboardSide(Model model, HttpSession session) {
        if (!erForretning(session)) {
            return "redirect:/login";
        }

        model.addAttribute("antalAktive", forretningService.antalAktiveUdlejninger());
        model.addAttribute("samletPris", forretningService.samletMaanedligPrisAktive());
        model.addAttribute("aktive", forretningService.aktiveUdlejningerMedJoin());

        // HashMap: antal abonnementer per status (key-value par).
        HashMap<String, Integer> statusFordeling = forretningService.antalAbonnementerPerStatus();
        model.addAttribute("statusFordeling", statusFordeling);

        return "dashboard";
    }

    @PostMapping("/refresh")
    public String refresh(@RequestParam("medarbejderNavn") String medarbejderNavn,
                          @RequestParam("medarbejderPassword") String medarbejderPassword,
                          Model model,
                          HttpSession session) {

        if (!erForretning(session)) {
            return "redirect:/login";
        }

        // Controller -> Service -> Repository
        Bruger bruger = brugerService.findByNavnOgPassword(medarbejderNavn, medarbejderPassword);

        if (bruger == null || !RolleDefinitioner.getInstance().getRolleForretning().equals(bruger.getRolle())) {
            model.addAttribute("fejl", "Forkert login eller rolle. Kun FORRETNING maa se dashboard.");
            model.addAttribute("antalAktive", forretningService.antalAktiveUdlejninger());
            model.addAttribute("samletPris", forretningService.samletMaanedligPrisAktive());
            model.addAttribute("aktive", forretningService.aktiveUdlejningerMedJoin());
            return "dashboard";
        }

        return "redirect:/dashboard";
    }

    private boolean erForretning(HttpSession session) {
        Object obj = session.getAttribute("loggedInUser");
        if (!(obj instanceof Bruger)) return false;
        Bruger b = (Bruger) obj;
        return RolleDefinitioner.getInstance().getRolleForretning().equals(b.getRolle());
    }
}
