package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.model.RolleDefinitioner;
import com.springmad.bilabonnement.service.BrugerService;
import com.springmad.bilabonnement.service.KundeService;
import com.springmad.bilabonnement.service.SkadeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

// @Controller: haandterer HTTP-requests for skade-registrering.
// Hierarkiet er: Controller -> Service -> Repository
@Controller
@RequestMapping("/skader")
public class SkadeController {

    // Controlleren taler kun med services, aldrig direkte med repositories.
    @Autowired
    private KundeService kundeService;

    @Autowired
    private SkadeService skadeService;

    @Autowired
    private BrugerService brugerService;

    @GetMapping("/opret")
    public String visSide(@RequestParam(required = false) Integer kundeId,
                          @RequestParam(required = false) Integer abonnementId,
                          Model model,
                          HttpSession session) {

        if (!harSkadeAdgang(session)) {
            return "redirect:/login";
        }

        // Controller -> Service -> Repository
        model.addAttribute("kunder", kundeService.findAll());
        model.addAttribute("selectedKundeId", kundeId);
        model.addAttribute("abonnementer", skadeService.findAfsluttedeAbonnementer(kundeId));
        model.addAttribute("selectedAbonnementId", abonnementId);

        if (abonnementId != null) {
            model.addAttribute("skader", skadeService.findSkaderForAbonnement(abonnementId));
        }

        return "skader-opret";
    }

    @PostMapping("/opret")
    public String gem(@RequestParam Integer kundeId,
                      @RequestParam Integer abonnementId,
                      @RequestParam List<String> beskrivelse,
                      @RequestParam List<BigDecimal> pris,
                      @RequestParam String medarbejderNavn,
                      @RequestParam String medarbejderPassword,
                      Model model,
                      HttpSession session) {

        if (!harSkadeAdgang(session)) {
            return "redirect:/login";
        }

        // Controller -> Service -> Repository
        Bruger medarbejder = brugerService.findByNavnOgPassword(medarbejderNavn, medarbejderPassword);

        String kravetRolle = RolleDefinitioner.getInstance().getRolleSkadeOgUdbedring();
        if (medarbejder == null || !kravetRolle.equals(medarbejder.getRolle())) {
            return fejl(model, kundeId, abonnementId, "Forkert login eller manglende rettigheder.");
        }

        if (!skadeService.erAbonnementAfsluttet(abonnementId)) {
            return fejl(model, kundeId, abonnementId, "Du kan kun registrere skader paa afsluttede abonnementer.");
        }

        // Validering via service (bruger HashSet internt til at fange dubletter).
        if (!skadeService.gyldigSkadeliste(beskrivelse, pris)) {
            return fejl(model, kundeId, abonnementId, "Alle skader skal have beskrivelse og positiv pris.");
        }

        skadeService.opretSkader(abonnementId, beskrivelse, pris);

        return "redirect:/skader/opret?kundeId=" + kundeId + "&abonnementId=" + abonnementId;
    }

    private boolean harSkadeAdgang(HttpSession session) {
        Object obj = session.getAttribute("loggedInUser");

        if (!(obj instanceof Bruger)) {
            return false;
        }

        Bruger bruger = (Bruger) obj;
        String kravetRolle = RolleDefinitioner.getInstance().getRolleSkadeOgUdbedring();
        String brugerensRolle = bruger.getRolle();

        return kravetRolle.equals(brugerensRolle);
    }

    private String fejl(Model model, Integer kundeId, Integer abonnementId, String besked) {
        model.addAttribute("fejl", besked);
        model.addAttribute("kunder", kundeService.findAll());
        model.addAttribute("selectedKundeId", kundeId);
        model.addAttribute("abonnementer", skadeService.findAfsluttedeAbonnementer(kundeId));
        model.addAttribute("selectedAbonnementId", abonnementId);
        if (abonnementId != null) {
            model.addAttribute("skader", skadeService.findSkaderForAbonnement(abonnementId));
        }
        return "skader-opret";
    }
}
