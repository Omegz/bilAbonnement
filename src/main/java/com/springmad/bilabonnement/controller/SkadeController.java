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

/*
 * Controller for skade og udbedring-rollen.
 *
 * Denne controller haandterer:
 *  - Visning af siden til skade-registrering
 *  - Filtrering paa kunde og abonnement
 *  - Validering af brugerrolle
 *  - Oprettelse af en eller flere skader paa en afsluttet lejeaftale
 *
 * Controlleren anvender klassisk Spring MVC:
 *  - @Controller
 *  - @GetMapping / @PostMapping
 *  - Model til dataoverfoersel til Thymeleaf
 *  - HttpSession til simpel login/rollehaandtering
 */
@Controller
@RequestMapping("/skader")
public class SkadeController {

    /*
     * Repositories anvendes til databaseadgang via JDBC.
     * Hver repository har ansvar for eet domæne.
     */
    private final KundeJdbcRepository kunder;
    private final AbonnementJdbcRepository abonnementer;
    private final SkadeJdbcRepository skader;
    private final BrugerJdbcRepository brugere;

    /*
     * Constructor injection.
     * Spring indsætter automatisk de nødvendige repositories.
     */
    public SkadeController(KundeJdbcRepository kunder,
                           AbonnementJdbcRepository abonnementer,
                           SkadeJdbcRepository skader,
                           BrugerJdbcRepository brugere) {
        this.kunder = kunder;
        this.abonnementer = abonnementer;
        this.skader = skader;
        this.brugere = brugere;
    }

    /*
     * GET-endpoint der viser skade-registreringssiden.
     *
     * Parametre:
     *  - kundeId: valgfri, bruges til dropdown-filtrering
     *  - abonnementId: valgfri, bruges til at vise eksisterende skader
     *
     * Flow:
     *  1) Tjekker om brugeren er logget ind og har korrekt rolle
     *  2) Henter alle kunder
     *  3) Henter afsluttede abonnementer (slutdato <= dags dato)
     *  4) Viser eksisterende skader hvis abonnement er valgt
     */
    @GetMapping("/opret")
    public String visSide(@RequestParam(required = false) Integer kundeId,
                          @RequestParam(required = false) Integer abonnementId,
                          Model model,
                          HttpSession session) {

        // Adgangskontrol: kun SKADE_OG_UDBEDRING-rollen maa bruge siden
        if (!harSkadeAdgang(session)) {
            return "redirect:/login";
        }

        // Dropdown med alle kunder
        model.addAttribute("kunder", kunder.findAll());
        model.addAttribute("selectedKundeId", kundeId);

        // Dropdown med afsluttede abonnementer (kun dem der maa registreres skader paa)
        model.addAttribute("abonnementer",
                abonnementer.findAfsluttedeAbonnementer(kundeId));
        model.addAttribute("selectedAbonnementId", abonnementId);

        // Hvis der er valgt et abonnement, vis allerede registrerede skader
        if (abonnementId != null) {
            model.addAttribute("skader",
                    skader.findSkaderForAbonnement(abonnementId));
        }

        return "skader-opret";
    }

    /*
     * POST-endpoint der gemmer skader i databasen.
     *
     * Der kan registreres flere skader paa samme abonnement i eet submit.
     * Derfor modtages lister af beskrivelser og priser.
     *
     * Ekstra sikkerhed:
     *  - Brugeren skal genindtaste login (svag autentificering)
     *  - Abonnementet skal vaere afsluttet
     */
    @PostMapping("/opret")
    public String gem(@RequestParam Integer kundeId,
                      @RequestParam Integer abonnementId,
                      @RequestParam List<String> beskrivelse,
                      @RequestParam List<BigDecimal> pris,
                      @RequestParam String medarbejderNavn,
                      @RequestParam String medarbejderPassword,
                      Model model,
                      HttpSession session) {

        // Session-baseret rollecheck
        if (!harSkadeAdgang(session)) {
            return "redirect:/login";
        }

        // Svag re-login ved submit (MVP-loesning)
        Bruger medarbejder =
                brugere.findByNavnOgPassword(medarbejderNavn, medarbejderPassword);

        if (medarbejder == null ||
                !"SKADE_OG_UDBEDRING".equals(medarbejder.getRolle())) {

            return fejl(model, kundeId, abonnementId,
                    "Forkert login eller manglende rettigheder.");
        }

        // Server-side validering:
        // Skader maa kun registreres paa afsluttede abonnementer
        if (!abonnementer.erAbonnementAfsluttet(abonnementId)) {
            return fejl(model, kundeId, abonnementId,
                    "Du kan kun registrere skader paa afsluttede abonnementer.");
        }

        // Validerer at der findes mindst een skade,
        // og at alle beskrivelser og priser er gyldige
        if (!gyldigSkadeliste(beskrivelse, pris)) {
            return fejl(model, kundeId, abonnementId,
                    "Alle skader skal have beskrivelse og positiv pris.");
        }

        // Gemmer skaderne i databasen
        // (een raekke pr. skade)
        skader.opretSkader(abonnementId, beskrivelse, pris);

        // Redirect for at undgaa dobbelt-submit
        return "redirect:/skader/opret?kundeId=" + kundeId +
                "&abonnementId=" + abonnementId;
    }

    /* -------------------- Hjælpe-metoder -------------------- */

    /*
     * Tjekker om den nuvaerende session indeholder
     * en bruger med korrekt rolle.
     */
    private boolean harSkadeAdgang(HttpSession session) {
        Object user = session.getAttribute("loggedInUser");
        return user instanceof Bruger &&
                "SKADE_OG_UDBEDRING".equals(((Bruger) user).getRolle());
    }

    /*
     * Validerer input fra formularen.
     * Alle skader skal:
     *  - Have en beskrivelse
     *  - Have en pris > 0
     *  - Have matchende antal felter
     */
    private boolean gyldigSkadeliste(List<String> beskrivelser,
                                     List<BigDecimal> priser) {

        if (beskrivelser == null || priser == null) return false;
        if (beskrivelser.isEmpty()) return false;
        if (beskrivelser.size() != priser.size()) return false;

        for (int i = 0; i < beskrivelser.size(); i++) {
            if (beskrivelser.get(i).isBlank()) return false;
            if (priser.get(i) == null || priser.get(i).signum() <= 0) return false;
        }
        return true;
    }

    /*
     * Genopbygger modellen ved fejl,
     * saa brugeren forbliver paa samme side
     * med dropdowns og allerede registrerede skader.
     */
    private String fejl(Model model,
                        Integer kundeId,
                        Integer abonnementId,
                        String besked) {

        model.addAttribute("fejl", besked);
        model.addAttribute("kunder", kunder.findAll());
        model.addAttribute("selectedKundeId", kundeId);
        model.addAttribute("abonnementer",
                abonnementer.findAfsluttedeAbonnementer(kundeId));
        model.addAttribute("selectedAbonnementId", abonnementId);

        if (abonnementId != null) {
            model.addAttribute("skader",
                    skader.findSkaderForAbonnement(abonnementId));
        }

        return "skader-opret";
    }
}
