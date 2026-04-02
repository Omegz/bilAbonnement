package com.springmad.bilabonnement.service;

import com.springmad.bilabonnement.model.Kunde;
import com.springmad.bilabonnement.repository.KundeJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service: service-lag der ligger mellem controller og repository.
// KundeService haandterer forretningslogik for kunder.
@Service
public class KundeService {

    @Autowired
    private KundeJdbcRepository kundeJdbcRepository;

    // Henter alle kunder fra databasen.
    public List<Kunde> findAll() {
        return kundeJdbcRepository.findAll();
    }

    // Opretter en ny kunde i databasen.
    public void opretKunde(Kunde kunde) {
        kundeJdbcRepository.opretKunde(kunde);
    }

    // Sletter en kunde fra databasen baseret paa id.
    public void sletKunde(int id) {
        kundeJdbcRepository.sletKunde(id);
    }
}
