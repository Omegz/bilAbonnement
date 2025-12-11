package com.springmad.bilabonnement.service;

import com.springmad.bilabonnement.repository.AbonnementJdbcRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

// Serviceklasse der indeholder logik til oprettelse af abonnement.
// Gør det nemmere at teste "happy flow" og "exception flow".
@Service
public class AbonnementService {

    private final AbonnementJdbcRepository abonnementJdbcRepository;

    public AbonnementService(AbonnementJdbcRepository abonnementJdbcRepository) {
        this.abonnementJdbcRepository = abonnementJdbcRepository;
    }

    // Opretter et abonnement, hvis input er gyldigt og kunden ikke har et aktivt abonnement.
    public void opretAbonnementHvisMuligt(String kundeNavn,
                                          int bilId,
                                          LocalDate startdato,
                                          LocalDate slutdato,
                                          BigDecimal maanedligPris) {

        // Inputvalidering (exception flow nr. 1)
        if (kundeNavn == null || kundeNavn.isBlank()) {
            throw new IllegalArgumentException("Kundenavn må ikke være tomt.");
        }

        if (maanedligPris == null || maanedligPris.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Månedlig pris skal være positiv.");
        }

        // Logisk kontrol (exception flow nr. 2)
        if (abonnementJdbcRepository.harAktivtAbonnementForKundeNavn(kundeNavn)) {
            throw new IllegalStateException("Kunden har allerede et aktivt abonnement.");
        }

        // Happy flow: alt ok → opretter abonnement via repository.
        abonnementJdbcRepository.opretAbonnement(
                kundeNavn,
                bilId,
                startdato,
                slutdato,
                maanedligPris
        );
    }
}
