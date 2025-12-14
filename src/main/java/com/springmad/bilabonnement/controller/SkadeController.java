package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.repository.AbonnementJdbcRepository;
import com.springmad.bilabonnement.repository.BrugerJdbcRepository;
import com.springmad.bilabonnement.repository.KundeJdbcRepository;
import com.springmad.bilabonnement.repository.SkadeJdbcRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/skader")
public class SkadeController {

    private final KundeJdbcRepository kundeJdbcRepository;
    private final AbonnementJdbcRepository abonnementJdbcRepository;
    private final SkadeJdbcRepository skadeJdbcRepository;
    private final BrugerJdbcRepository brugerJdbcRepository;

    public SkadeController(KundeJdbcRepository kundeJdbcRepository,
                           AbonnementJdbcRepository abonnementJdbcRepository,
                           SkadeJdbcRepository skadeJdbcRepository,
                           BrugerJdbcRepository brugerJdbcRepository) {
        this.kundeJdbcRepository = kundeJdbcRepository;
        this.abonnementJdbcRepository = abonnementJdbcRepository;
        this.skadeJdbcRepository = skadeJdbcRepository;
        this.brugerJdbcRepository = brugerJdbcRepository;
    }

    @GetMapping("/opret")
    public String opretSkadeSide(@RequestParam(value = "kundeId", required = false) Integer kundeId,
                                 @RequestParam(value = "abonnementId", required = false) Integer abonnementId,
                                 Model model,
                                 HttpSession session) {

        if (!erSkadeRolle(session)) {
            return "redirect:/login";
        }

        model.addAttribute("kunder", kundeJdbcRepository.findAll());
        model.addAttribute("selectedKundeId", kundeId);

        // Dropdown med "afsluttede" kontrakter (slutdato <= i dag)
        model.addAttribute("abonnementer", abonnementJdbcRepository.findAfsluttedeAbonnementer(kundeId));
        model.addAttribute("selectedAbonnementId", abonnementId);

        // Vis eksisterende skader hvis der er valgt kontrakt
        if (abonnementId != null) {
            model.addAttribute("skader", skadeJdbcRepository.findSkaderForAbonnement(abonnementId));
        }

        return "skader-opret";
    }

    @PostMapping("/opret")
    public String gemSkader(@RequestParam("kundeId") Integer kundeId,
                            @RequestParam("abonnementId") Integer abonnementId,
                            @RequestParam("beskrivelse") List<String> beskrivelser,
                            @RequestParam("pris") List<BigDecimal> priser,
                            @RequestParam("medarbejderNavn") String medarbejderNavn,
                            @RequestParam("medarbejderPassword") String medarbejderPassword,
                            Model model,
                            HttpSession session) {

        if (!erSkadeRolle(session)) {
            return "redirect:/login";
        }

        // Ekstra “svag” re-login ved submit
        Bruger medarbejder = brugerJdbcRepository.findByNavnOgPassword(medarbejderNavn, medarbejderPassword);
        if (medarbejder == null || !"SKADE_OG_UDBEDRING".equals(medarbejder.getRolle())) {
            return fejl(model, kundeId, abonnementId, "Du har ikke rettigheder til at registrere skader (forkert rolle/login).");
        }

        // Server-side check: kontrakten skal være afsluttet
        if (!abonnementJdbcRepository.erAbonnementAfsluttet(abonnementId)) {
            return fejl(model, kundeId, abonnementId, "Du kan kun registrere skader på kontrakter hvor lejeperioden er overstået (slutdato <= i dag).");
        }

        // Valider liste input (MVP)
        if (beskrivelser == null || priser == null || beskrivelser.size() != priser.size() || beskrivelser.isEmpty()) {
            return fejl(model, kundeId, abonnementId, "Du skal tilføje mindst én skade med beskrivelse og pris.");
        }

        for (int i = 0; i < beskrivelser.size(); i++) {
            String b = beskrivelser.get(i);
            BigDecimal p = priser.get(i);

            if (b == null || b.isBlank()) {
                return fejl(model, kundeId, abonnementId, "Skade-beskrivelse må ikke være tom.");
            }
            if (p == null || p.signum() <= 0) {
                return fejl(model, kundeId, abonnementId, "Pris pr. skade skal være et positivt tal.");
            }
        }

        // Gem skader (én DB-række pr. skade)
        skadeJdbcRepository.opretSkader(abonnementId, beskrivelser, priser);

        // Redirect tilbage til samme side så du kan se listen
        return "redirect:/skader/opret?kundeId=" + kundeId + "&abonnementId=" + abonnementId;
    }

    private String fejl(Model model, Integer kundeId, Integer abonnementId, String besked) {
        model.addAttribute("fejl", besked);
        model.addAttribute("kunder", kundeJdbcRepository.findAll());
        model.addAttribute("selectedKundeId", kundeId);
        model.addAttribute("abonnementer", abonnementJdbcRepository.findAfsluttedeAbonnementer(kundeId));
        model.addAttribute("selectedAbonnementId", abonnementId);
        if (abonnementId != null) {
            model.addAttribute("skader", skadeJdbcRepository.findSkaderForAbonnement(abonnementId));
        }
        return "skader-opret";
    }

    private boolean erSkadeRolle(HttpSession session) {
        Object obj = session.getAttribute("loggedInUser");
        if (!(obj instanceof Bruger)) return false;
        Bruger b = (Bruger) obj;
        return "SKADE_OG_UDBEDRING".equals(b.getRolle());
    }
}
