package com.springmad.bilabonnement.service;

import com.springmad.bilabonnement.model.Bruger;
import com.springmad.bilabonnement.repository.BrugerJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// @Service: service-lag der ligger mellem controller og repository.
// BrugerService haandterer forretningslogik for brugere (login, signup).
@Service
public class BrugerService {

    @Autowired
    private BrugerJdbcRepository brugerJdbcRepository;

    // Opretter en ny bruger i databasen.
    public void opretBruger(Bruger bruger) {
        brugerJdbcRepository.opretBruger(bruger);
    }

    // Finder en bruger baseret paa navn og password.
    // Returnerer brugeren hvis fundet, ellers null.
    public Bruger findByNavnOgPassword(String navn, String password) {
        return brugerJdbcRepository.findByNavnOgPassword(navn, password);
    }
}
