package com.springmad.bilabonnement.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

// Serviceklasse der indeholder valideringslogik for skader.
// Vi laver dette som en separat service (i stedet for en privat metode i controlleren)
// fordi vi saa kan teste den med JUnit.
// En privat metode kan man ikke teste udefra — en public metode i en service kan man.
@Service
public class SkadeValideringService {

    // Validerer en liste af skadebeskrivelser og priser.
    // Returnerer true hvis alle skader er gyldige, false hvis noget er forkert.
    //
    // Regler:
    //   - Listerne maa ikke vaere null eller tomme
    //   - Listerne skal have samme laengde (een pris per beskrivelse)
    //   - Ingen beskrivelse maa vaere blank
    //   - Alle priser skal vaere positive (stoerre end 0)
    //   - Ingen duplikerede beskrivelser (tjekkes med HashSet)
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
