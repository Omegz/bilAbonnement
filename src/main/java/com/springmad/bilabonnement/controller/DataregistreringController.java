package com.springmad.bilabonnement.controller;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.model.LejeaftaleForm;
import com.springmad.bilabonnement.model.RolleDefinitioner;
import com.springmad.bilabonnement.service.AbonnementService;
import com.springmad.bilabonnement.service.BilService;
import com.springmad.bilabonnement.service.BrugerService;
import com.springmad.bilabonnement.service.KundeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

// @Controller: haandterer HTTP-requests for dataregistrering (lejeaftaler).
// Hierarkiet er: Controller -> Service -> Repository
@Controller
@RequestMapping("/data")
public class DataregistreringController {

    // Controlleren taler kun med services, aldrig direkte med repositories.
    @Autowired
    private BilService bilService;

    @Autowired
    private KundeService kundeService;

    @Autowired
    private AbonnementService abonnementService;

    @Autowired
    private BrugerService brugerService;

    @GetMapping("/lejeaftale/opret")
    public String opretLejeaftaleSide(Model model, HttpSession session) {
        if (!erDataregistrering(session)) {
            return "redirect:/login";
        }

        model.addAttribute("form", new LejeaftaleForm());
        // Controller -> Service -> Repository
        model.addAttribute("biler", bilService.findAll());
        model.addAttribute("kunder", kundeService.findAll());
        model.addAttribute("today", LocalDate.now());

        return "data-lejeaftale-opret";
    }

    @PostMapping("/lejeaftale/opret")
    public String opretLejeaftale(@ModelAttribute("form") LejeaftaleForm form,
                                  Model model,
                                  HttpSession session) {

        if (!erDataregistrering(session)) {
            return "redirect:/login";
        }

        // Controller -> Service -> Repository
        Bruger medarbejder = brugerService.findByNavnOgPassword(
                form.getMedarbejderNavn(),
                form.getMedarbejderPassword()
        );

        // Singleton: henter rollen fra RolleDefinitioner.
        // Vi gemmer vaerdien i en variabel foerst saa linjen er nemmere at laese.
        String kravetRolle = RolleDefinitioner.getInstance().getRolleDataregistrering();

        if (medarbejder == null || !kravetRolle.equals(medarbejder.getRolle())) {
            return fejl(model, "Du har ikke rettigheder til at registrere lejeaftaler (forkert rolle/login).");
        }

        if (form.getKundeId() == null) {
            return fejl(model, "Du skal vaelge en kunde.");
        }
        if (form.getBilId() == null) {
            return fejl(model, "Du skal vaelge en bil.");
        }
        if (form.getStartdato() == null) {
            return fejl(model, "Du skal vaelge en startdato.");
        }
        if (form.getStartdato().isBefore(LocalDate.now())) {
            return fejl(model, "Startdato maa ikke vaere i fortiden.");
        }
        if (form.getMaanedligPris() == null || form.getMaanedligPris().signum() <= 0) {
            return fejl(model, "Maanedlig pris skal vaere positiv.");
        }

        // Kontrakttype: brug formularens vaerdi, eller standard LIMITED hvis tom.
        String kontraktType;
        if (form.getKontraktType() == null || form.getKontraktType().isBlank()) {
            kontraktType = RolleDefinitioner.getInstance().getKontraktLimited();
        } else {
            kontraktType = form.getKontraktType();
        }

        // Varighed afhaenger af kontrakttypen.
        String limited = RolleDefinitioner.getInstance().getKontraktLimited();
        int varighed;
        if (limited.equals(kontraktType)) {
            varighed = 150;
        } else {
            // Unlimited: brug formularens vaerdi, eller 90 som standard.
            if (form.getKontraktVarighedDage() == null) {
                varighed = 90;
            } else {
                varighed = form.getKontraktVarighedDage();
            }
            if (varighed < 90 || varighed > 1080) {
                return fejl(model, "Unlimited-kontrakt skal vaere mellem 90 og 1080 dage.");
            }
        }

        LocalDate slutdato = form.getSlutdato();
        if (slutdato == null) {
            slutdato = form.getStartdato().plusDays(varighed);
        } else if (slutdato.isBefore(form.getStartdato())) {
            return fejl(model, "Slutdato maa ikke vaere foer startdato.");
        }

        // Leveringsform: brug formularens vaerdi, eller AFHENTNING som standard.
        String leveringsform;
        if (form.getLeveringsform() == null || form.getLeveringsform().isBlank()) {
            leveringsform = "AFHENTNING";
        } else {
            leveringsform = form.getLeveringsform();
        }

        String adresse = form.getLeveringsadresse();
        if ("LEVERING".equals(leveringsform)) {
            if (adresse == null || adresse.isBlank()) {
                return fejl(model, "Leveringsadresse skal udfyldes naar leveringsform er LEVERING.");
            }
        } else {
            adresse = null;
        }

        // Udleveringssted: brug formularens vaerdi, eller BILABONNEMENT som standard.
        String udleveringsstedType;
        if (form.getUdleveringsstedType() == null || form.getUdleveringsstedType().isBlank()) {
            udleveringsstedType = "BILABONNEMENT";
        } else {
            udleveringsstedType = form.getUdleveringsstedType();
        }

        // Controller -> Service -> Repository
        abonnementService.opretLejeaftaleMedDetaljer(
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

        return "redirect:/abonnementer";
    }

    // Hjaelpemetode: genbruger dropdown-data ved fejl.
    private String fejl(Model model, String besked) {
        model.addAttribute("fejl", besked);
        model.addAttribute("biler", bilService.findAll());
        model.addAttribute("kunder", kundeService.findAll());
        model.addAttribute("today", LocalDate.now());
        return "data-lejeaftale-opret";
    }

    private boolean erDataregistrering(HttpSession session) {
        // Hent bruger fra session
        Object obj = session.getAttribute("loggedInUser");

        // instanceof tjekker om objektet er en Bruger
        if (!(obj instanceof Bruger)) {
            return false;
        }

        // Cast: vi ved nu at obj ER en Bruger, saa vi kan caste
        Bruger bruger = (Bruger) obj;

        // Sammenlign rollen med Singleton-vaerdien
        String kravetRolle = RolleDefinitioner.getInstance().getRolleDataregistrering();
        String brugerensRolle = bruger.getRolle();

        return kravetRolle.equals(brugerensRolle);
    }
}
