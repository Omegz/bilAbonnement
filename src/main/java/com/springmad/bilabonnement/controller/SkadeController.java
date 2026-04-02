package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.model.RolleDefinitioner;
import com.springmad.bilabonnement.repository.AbonnementJdbcRepository;
import com.springmad.bilabonnement.repository.BrugerJdbcRepository;
import com.springmad.bilabonnement.repository.KundeJdbcRepository;
import com.springmad.bilabonnement.repository.SkadeJdbcRepository;
import com.springmad.bilabonnement.service.SkadeValideringService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    // @Autowired: Spring indsaetter alle repositories automatisk (dependency injection).
    // Hver repository har ansvar for eet domaene.

    @Autowired
    private KundeJdbcRepository kunder;

    @Autowired
    private AbonnementJdbcRepository abonnementer;

    @Autowired
    private SkadeJdbcRepository skader;

    @Autowired
    private BrugerJdbcRepository brugere;

    // @Autowired: Spring indsaetter SkadeValideringService automatisk.
    // Valideringslogikken er flyttet til en service saa den kan testes med JUnit.
    @Autowired
    private SkadeValideringService skadeValidering;

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
                !RolleDefinitioner.getInstance().getRolleSkadeOgUdbedring().equals(medarbejder.getRolle())) {

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
        // og at alle beskrivelser og priser er gyldige.
        // Valideringen er flyttet til SkadeValideringService saa den kan testes med JUnit.
        if (!skadeValidering.gyldigSkadeliste(beskrivelse, pris)) {
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
                RolleDefinitioner.getInstance().getRolleSkadeOgUdbedring().equals(((Bruger) user).getRolle());
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
