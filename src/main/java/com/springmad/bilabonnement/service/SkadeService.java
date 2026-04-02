package com.springmad.bilabonnement.service;

import com.springmad.bilabonnement.model.AbonnementOption;
import com.springmad.bilabonnement.repository.AbonnementJdbcRepository;
import com.springmad.bilabonnement.repository.SkadeJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

// @Service: service-lag der ligger mellem controller og repository.
// SkadeService samler forretningslogik for skade-modulet:
//   - Validering af skadeinput (bruger HashSet til at fange dubletter)
//   - Hentning af afsluttede abonnementer
//   - Oprettelse af skader
//   - Hentning af eksisterende skader
@Service
public class SkadeService {

    @Autowired
    private SkadeJdbcRepository skadeJdbcRepository;

    @Autowired
    private AbonnementJdbcRepository abonnementJdbcRepository;

    // Henter afsluttede abonnementer, evt. filtreret paa kunde.
    public List<AbonnementOption> findAfsluttedeAbonnementer(Integer kundeId) {
        return abonnementJdbcRepository.findAfsluttedeAbonnementer(kundeId);
    }

    // Tjekker om et abonnement er afsluttet (server-side validering).
    public boolean erAbonnementAfsluttet(int abonnementId) {
        return abonnementJdbcRepository.erAbonnementAfsluttet(abonnementId);
    }

    // Henter eksisterende skader for et abonnement.
    public List<Map<String, Object>> findSkaderForAbonnement(int abonnementId) {
        return skadeJdbcRepository.findSkaderForAbonnement(abonnementId);
    }

    // Gemmer skader i databasen.
    public void opretSkader(int abonnementId, List<String> beskrivelser, List<BigDecimal> priser) {
        skadeJdbcRepository.opretSkader(abonnementId, beskrivelser, priser);
    }

    // Validerer en liste af skadebeskrivelser og priser.
    // Bruger HashSet til at fange duplikerede beskrivelser.
    // Returnerer true hvis alle skader er gyldige, false hvis noget er forkert.
    public boolean gyldigSkadeliste(List<String> beskrivelser, List<BigDecimal> priser) {

        if (beskrivelser == null || priser == null) return false;
        if (beskrivelser.isEmpty()) return false;
        if (beskrivelser.size() != priser.size()) return false;

        // ===== HashSet =====
        // HashSet bruges til at fange duplikerede beskrivelser.
        // add() returnerer false hvis vaerdien allerede findes i settet.
        HashSet<String> setBeskrivelser = new HashSet<>();

        for (int i = 0; i < beskrivelser.size(); i++) {
            if (beskrivelser.get(i).isBlank()) return false;
            if (priser.get(i) == null || priser.get(i).signum() <= 0) return false;

            boolean erNy = setBeskrivelser.add(beskrivelser.get(i));
            if (!erNy) {
                return false;
            }
        }
        return true;
    }
}
