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
import java.util.Set;

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

        // ===== Map-metoder fra Collection Framework =====
        // HashMap gemmer data som noegel-vaerdi par (key-value).
        HashMap<String, Integer> statusFordeling = forretningService.antalAbonnementerPerStatus();

        // put() er allerede brugt i ForretningJdbcRepository til at bygge mappet.
        // Her bruger vi de oevrige Map-metoder til at arbejde med data:

        // size(): returnerer antal noegel-vaerdi par i mappet.
        int antalStatusser = statusFordeling.size();
        model.addAttribute("antalStatusser", antalStatusser);

        // isEmpty(): returnerer true hvis mappet er tomt (ingen statusser fundet).
        boolean harStatusser = !statusFordeling.isEmpty();
        model.addAttribute("harStatusser", harStatusser);

        // containsKey(): tjekker om en bestemt noegel findes i mappet.
        boolean harAktiveAbonnementer = statusFordeling.containsKey(
                RolleDefinitioner.getInstance().getStatusAktiv()
        );
        model.addAttribute("harAktiveAbonnementer", harAktiveAbonnementer);

        // get(): henter vaerdien for en noegel (allerede brugt i repository).
        // Her bruger vi den til at hente antal aktive fra mappet.
        Integer antalAktiveFraMap = statusFordeling.get(
                RolleDefinitioner.getInstance().getStatusAktiv()
        );
        model.addAttribute("antalAktiveFraMap", antalAktiveFraMap);

        // keySet(): returnerer alle noegler som et Set.
        // Vi kan bruge det til at vise hvilke statusser der findes.
        Set<String> alleStatusser = statusFordeling.keySet();
        model.addAttribute("alleStatusser", alleStatusser);

        // values(): returnerer alle vaerdier som en Collection.
        // Vi kan bruge det til at se alle antal paa tvaers af statusser.
        model.addAttribute("alleAntal", statusFordeling.values());

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
