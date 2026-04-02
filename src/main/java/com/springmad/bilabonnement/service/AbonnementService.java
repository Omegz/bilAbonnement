package com.springmad.bilabonnement.service;

import com.springmad.bilabonnement.model.AbonnementOversigt;
import com.springmad.bilabonnement.repository.AbonnementJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// @Service: service-lag der ligger mellem controller og repository.
// AbonnementService indeholder forretningslogik for abonnementer.
// Controlleren kalder servicen, og servicen kalder repository.
@Service
public class AbonnementService {

    // @Autowired: Spring indsaetter repository automatisk (dependency injection).
    @Autowired
    private AbonnementJdbcRepository abonnementJdbcRepository;

    // Henter alle abonnementer med JOIN (kunde + bil info).
    public List<AbonnementOversigt> findAlleMedJoin() {
        return abonnementJdbcRepository.findAlleMedJoin();
    }

    // Opretter et abonnement, hvis input er gyldigt og kunden ikke har et aktivt abonnement.
    // Kaster exception hvis noget er forkert (exception flow).
    public void opretAbonnementHvisMuligt(String kundeNavn,
                                          int bilId,
                                          LocalDate startdato,
                                          LocalDate slutdato,
                                          BigDecimal maanedligPris) {

        // Inputvalidering (exception flow nr. 1)
        if (kundeNavn == null || kundeNavn.isBlank()) {
            throw new IllegalArgumentException("Kundenavn maa ikke vaere tomt.");
        }

        if (maanedligPris == null || maanedligPris.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Maanedlig pris skal vaere positiv.");
        }

        // Logisk kontrol (exception flow nr. 2)
        if (abonnementJdbcRepository.harAktivtAbonnementForKundeNavn(kundeNavn)) {
            throw new IllegalStateException("Kunden har allerede et aktivt abonnement.");
        }

        // Happy flow: alt ok -> opretter abonnement via repository.
        abonnementJdbcRepository.opretAbonnement(
                kundeNavn,
                bilId,
                startdato,
                slutdato,
                maanedligPris
        );
    }

    // Tjekker om en kunde allerede har et aktivt abonnement.
    public boolean harAktivtAbonnementForKundeNavn(String kundeNavn) {
        return abonnementJdbcRepository.harAktivtAbonnementForKundeNavn(kundeNavn);
    }

    // Opretter en lejeaftale med alle detaljer (kontrakttype, levering, osv.).
    public void opretLejeaftaleMedDetaljer(int kundeId,
                                            int bilId,
                                            LocalDate startdato,
                                            LocalDate slutdato,
                                            BigDecimal maanedligPris,
                                            String kontraktType,
                                            int kontraktVarighedDage,
                                            String udleveringsstedType,
                                            String leveringsform,
                                            String leveringsadresse) {

        abonnementJdbcRepository.opretLejeaftaleMedDetaljerKundeId(
                kundeId, bilId, startdato, slutdato, maanedligPris,
                kontraktType, kontraktVarighedDage, udleveringsstedType,
                leveringsform, leveringsadresse
        );
    }
}
