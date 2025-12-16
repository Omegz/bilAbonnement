package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.model.LejeaftaleForm;
import com.springmad.bilabonnement.repository.AbonnementJdbcRepository;
import com.springmad.bilabonnement.repository.BilRepository;
import com.springmad.bilabonnement.repository.BrugerJdbcRepository;
import com.springmad.bilabonnement.repository.KundeJdbcRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

// Controller for dataregistrering.
// Klassen passer ind i MVC ved at:
// - modtage requests (Controller)
// - hente/gemme data via repositories (Model/DB)
// - sende data videre til Thymeleaf templates (View)
@Controller
@RequestMapping("/data")
public class DataregistreringController {

    private final BilRepository bilRepository;
    // Bruges til at hente biler fra tabellen "biler" til dropdown i formularen.

    private final KundeJdbcRepository kundeJdbcRepository;
    // Bruges til at hente kunder fra tabellen "kunder" til dropdown i formularen.

    private final AbonnementJdbcRepository abonnementJdbcRepository;
    // Bruges til at oprette lejeaftaler i databasen via SQL (JdbcTemplate).

    private final BrugerJdbcRepository brugerJdbcRepository;
    // Bruges til "svag" kontrol ved submit (brugernavn + password) og rolle-check.

    public DataregistreringController(BilRepository bilRepository,
                                      KundeJdbcRepository kundeJdbcRepository,
                                      AbonnementJdbcRepository abonnementJdbcRepository,
                                      BrugerJdbcRepository brugerJdbcRepository) {
        // Constructor injection: Spring leverer dependencies automatisk.
        this.bilRepository = bilRepository;
        this.kundeJdbcRepository = kundeJdbcRepository;
        this.abonnementJdbcRepository = abonnementJdbcRepository;
        this.brugerJdbcRepository = brugerJdbcRepository;
    }

    @GetMapping("/lejeaftale/opret")
    // Endpoint: GET /data/lejeaftale/opret
    // Viser en formular til at oprette en lejeaftale.
    // Kun brugere med rolle DATAREGISTRERING maa se denne side.
    public String opretLejeaftaleSide(Model model, HttpSession session) {

        // Rollebeskyttelse via session.
        // loggedInUser saettes ved login i AuthController.
        if (!erDataregistrering(session)) {
            return "redirect:/login";
        }

        // Tomt form-objekt til Thymeleaf data-binding (th:object).
        model.addAttribute("form", new LejeaftaleForm());

        // Dropdown-data: biler og kunder fra databasen.
        model.addAttribute("biler", bilRepository.findAll());
        model.addAttribute("kunder", kundeJdbcRepository.findAll());

        // Dagens dato bruges typisk i HTML til min= paa date input,
        // saa brugeren ikke kan vaelge dato i fortiden.
        model.addAttribute("today", LocalDate.now());

        return "data-lejeaftale-opret";
        // Returnerer view-navn: templates/data-lejeaftale-opret.html
    }

    @PostMapping("/lejeaftale/opret")
    // Endpoint: POST /data/lejeaftale/opret
    // Modtager formular-data og opretter lejeaftalen i databasen.
    // Indeholder forretningsregler fra opgaven (varighed, datoer, levering).
    public String opretLejeaftale(@ModelAttribute("form") LejeaftaleForm form,
                                  Model model,
                                  HttpSession session) {

        // Rollebeskyttelse igen, saa man ikke kan poste data uden korrekt rolle.
        if (!erDataregistrering(session)) {
            return "redirect:/login";
        }

        // Ekstra "svag" kontrol: re-login i selve formularen.
        // Formaal: demonstrere kontrol ved dataregistrering (MVP).
        Bruger medarbejder = brugerJdbcRepository.findByNavnOgPassword(
                form.getMedarbejderNavn(),
                form.getMedarbejderPassword()
        );

        // Kontrollere at brugeren findes og har korrekt rolle.
        if (medarbejder == null || !"DATAREGISTRERING".equals(medarbejder.getRolle())) {
            return fejl(model, "Du har ikke rettigheder til at registrere lejeaftaler (forkert rolle/login).");
        }

        // Validering af noedvendige felter (knyttet til database relationer).
        if (form.getKundeId() == null) {
            return fejl(model, "Du skal vaelge en kunde.");
        }
        if (form.getBilId() == null) {
            return fejl(model, "Du skal vaelge en bil.");
        }

        // Startdato-regel: maa ikke vaere i fortiden.
        // Dette matcher opgavens behov for korrekt processtyring.
        if (form.getStartdato() == null) {
            return fejl(model, "Du skal vaelge en startdato.");
        }
        if (form.getStartdato().isBefore(LocalDate.now())) {
            return fejl(model, "Startdato maa ikke vaere i fortiden.");
        }

        // Pris-regel: skal vaere positiv (MVP).
        if (form.getMaanedligPris() == null || form.getMaanedligPris().signum() <= 0) {
            return fejl(model, "Maanedlig pris skal vaere positiv.");
        }

        // Kontrakt-type: hvis intet er valgt, bruger vi standard LIMITED.
        // Dette goer at systemet stadig virker selv ved manglende input.
        String kontraktType = (form.getKontraktType() == null || form.getKontraktType().isBlank())
                ? "LIMITED" : form.getKontraktType();

        // Kontraktregler (MVP) ud fra opgaven:
        // - Limited = fast periode (150 dage)
        // - Unlimited = variabel periode (90-1080 dage)
        int varighed;
        if ("LIMITED".equals(kontraktType)) {
            varighed = 150;
        } else {
            varighed = (form.getKontraktVarighedDage() == null) ? 90 : form.getKontraktVarighedDage();
            if (varighed < 90 || varighed > 1080) {
                return fejl(model, "Unlimited-kontrakt skal vaere mellem 90 og 1080 dage.");
            }
        }

        // Beregner slutdato hvis den ikke er udfyldt.
        // Hvis slutdato er udfyldt, tjekker vi at den ikke ligger foer startdato.
        LocalDate slutdato = form.getSlutdato();
        if (slutdato == null) {
            slutdato = form.getStartdato().plusDays(varighed);
        } else if (slutdato.isBefore(form.getStartdato())) {
            return fejl(model, "Slutdato maa ikke vaere foer startdato.");
        }

        // Leveringsform: standard er AFHENTNING, men kan vaere LEVERING.
        String leveringsform = (form.getLeveringsform() == null || form.getLeveringsform().isBlank())
                ? "AFHENTNING" : form.getLeveringsform();

        // Hvis levering -> adresse er paakraevet.
        String adresse = form.getLeveringsadresse();
        if ("LEVERING".equals(leveringsform)) {
            if (adresse == null || adresse.isBlank()) {
                return fejl(model, "Leveringsadresse skal udfyldes naar leveringsform er LEVERING.");
            }
        } else {
            // Ved afhentning gemmer vi ikke adresse.
            adresse = null;
        }

        // Udleveringssted type: bruges til at modelere om udlevering sker via Bilabonnement eller DS/FDM.
        String udleveringsstedType = (form.getUdleveringsstedType() == null || form.getUdleveringsstedType().isBlank())
                ? "BILABONNEMENT" : form.getUdleveringsstedType();

        // Gemmer lejeaftale i databasen.
        // Her ligger forretningsdata samlet, og repository laver INSERT (og evt relationer) via SQL.
        abonnementJdbcRepository.opretLejeaftaleMedDetaljerKundeId(
                form.getKundeId(),
                form.getBilId().intValue(),
                form.getStartdato(),
                slutdato,
                form.getMaanedligPris(),
                kontraktType,
                varighed,
                udleveringsstedType,
                leveringsform,
                adresse
        );

        // Efter oprettelse sendes brugeren til oversigten.
        // Redirect undgaar dobbelt-submit ved refresh.
        return "redirect:/abonnementer";
    }

    // Hjaelpemetode som genbruger samme side + dropdown-data ved fejl.
    // Det sikrer at brugeren kan rette input uden at miste formularen.
    private String fejl(Model model, String besked) {
        model.addAttribute("fejl", besked);
        model.addAttribute("biler", bilRepository.findAll());
        model.addAttribute("kunder", kundeJdbcRepository.findAll());
        model.addAttribute("today", LocalDate.now());
        return "data-lejeaftale-opret";
    }

    // Rolle-check baseret paa session.
    // Dette er den simple autorisation (MVP) i projektet.
    private boolean erDataregistrering(HttpSession session) {
        Object obj = session.getAttribute("loggedInUser");
        if (!(obj instanceof Bruger)) return false;
        Bruger b = (Bruger) obj;
        return "DATAREGISTRERING".equals(b.getRolle());
    }
}
