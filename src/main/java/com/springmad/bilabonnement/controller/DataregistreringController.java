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

@Controller
@RequestMapping("/data")
public class DataregistreringController {

    private final BilRepository bilRepository;
    private final KundeJdbcRepository kundeJdbcRepository;
    private final AbonnementJdbcRepository abonnementJdbcRepository;
    private final BrugerJdbcRepository brugerJdbcRepository;

    public DataregistreringController(BilRepository bilRepository,
                                      KundeJdbcRepository kundeJdbcRepository,
                                      AbonnementJdbcRepository abonnementJdbcRepository,
                                      BrugerJdbcRepository brugerJdbcRepository) {
        this.bilRepository = bilRepository;
        this.kundeJdbcRepository = kundeJdbcRepository;
        this.abonnementJdbcRepository = abonnementJdbcRepository;
        this.brugerJdbcRepository = brugerJdbcRepository;
    }

    @GetMapping("/lejeaftale/opret")
    public String opretLejeaftaleSide(Model model, HttpSession session) {
        if (!erDataregistrering(session)) {
            return "redirect:/login";
        }

        model.addAttribute("form", new LejeaftaleForm());
        model.addAttribute("biler", bilRepository.findAll());
        model.addAttribute("kunder", kundeJdbcRepository.findAll());
        model.addAttribute("today", LocalDate.now()); // til min= i HTML
        return "data-lejeaftale-opret";
    }

    @PostMapping("/lejeaftale/opret")
    public String opretLejeaftale(@ModelAttribute("form") LejeaftaleForm form,
                                  Model model,
                                  HttpSession session) {

        if (!erDataregistrering(session)) {
            return "redirect:/login";
        }

        // Extra “svag” kontrol: login i formularen
        Bruger medarbejder = brugerJdbcRepository.findByNavnOgPassword(form.getMedarbejderNavn(), form.getMedarbejderPassword());
        if (medarbejder == null || !"DATAREGISTRERING".equals(medarbejder.getRolle())) {
            return fejl(model, "Du har ikke rettigheder til at registrere lejeaftaler (forkert rolle/login).");
        }

        // Valider kunde + bil
        if (form.getKundeId() == null) {
            return fejl(model, "Du skal vælge en kunde.");
        }
        if (form.getBilId() == null) {
            return fejl(model, "Du skal vælge en bil.");
        }

        // Startdato: i dag eller frem (må ikke være fortid)
        if (form.getStartdato() == null) {
            return fejl(model, "Du skal vælge en startdato.");
        }
        if (form.getStartdato().isBefore(LocalDate.now())) {
            return fejl(model, "Startdato må ikke være i fortiden.");
        }

        // Pris
        if (form.getMaanedligPris() == null || form.getMaanedligPris().signum() <= 0) {
            return fejl(model, "Månedlig pris skal være positiv.");
        }

        String kontraktType = (form.getKontraktType() == null || form.getKontraktType().isBlank())
                ? "LIMITED" : form.getKontraktType();

        // Kontraktregler (MVP)
        int varighed;
        if ("LIMITED".equals(kontraktType)) {
            // Limited = 150 dage (5 måneder)
            varighed = 150;
        } else {
            // Unlimited = 3-36 måneder (90-1080 dage)
            varighed = (form.getKontraktVarighedDage() == null) ? 90 : form.getKontraktVarighedDage();
            if (varighed < 90 || varighed > 1080) {
                return fejl(model, "Unlimited-kontrakt skal være mellem 90 og 1080 dage.");
            }
        }

        // Beregn slutdato hvis ikke udfyldt
        LocalDate slutdato = form.getSlutdato();
        if (slutdato == null) {
            slutdato = form.getStartdato().plusDays(varighed);
        } else if (slutdato.isBefore(form.getStartdato())) {
            return fejl(model, "Slutdato må ikke være før startdato.");
        }

        String leveringsform = (form.getLeveringsform() == null || form.getLeveringsform().isBlank())
                ? "AFHENTNING" : form.getLeveringsform();

        // Hvis LEVERING → adresse kræves
        String adresse = form.getLeveringsadresse();
        if ("LEVERING".equals(leveringsform)) {
            if (adresse == null || adresse.isBlank()) {
                return fejl(model, "Leveringsadresse skal udfyldes når leveringsform er LEVERING.");
            }
        } else {
            adresse = null; // ved afhentning gemmer vi ikke adresse
        }

        String udleveringsstedType = (form.getUdleveringsstedType() == null || form.getUdleveringsstedType().isBlank())
                ? "BILABONNEMENT" : form.getUdleveringsstedType();

        // Gem lejeaftale
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

        return "redirect:/abonnementer";
    }

    private String fejl(Model model, String besked) {
        model.addAttribute("fejl", besked);
        model.addAttribute("biler", bilRepository.findAll());
        model.addAttribute("kunder", kundeJdbcRepository.findAll());
        model.addAttribute("today", LocalDate.now());
        return "data-lejeaftale-opret";
    }

    private boolean erDataregistrering(HttpSession session) {
        Object obj = session.getAttribute("loggedInUser");
        if (!(obj instanceof Bruger)) return false;
        Bruger b = (Bruger) obj;
        return "DATAREGISTRERING".equals(b.getRolle());
    }
}
