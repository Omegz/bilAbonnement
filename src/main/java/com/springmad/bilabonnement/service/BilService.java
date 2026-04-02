package com.springmad.bilabonnement.service;

import com.springmad.bilabonnement.model.Bil;
import com.springmad.bilabonnement.repository.BilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// ===== @Service =====
// @Service markerer denne klasse som et service-lag i Spring.
// Service-laget indeholder forretningslogik og ligger MELLEM controller og repository:
//   Controller -> Service -> Repository
// Controlleren kalder servicen, og servicen kalder repository.
// Det giver separation of concerns: controlleren haandterer HTTP,
// servicen haandterer logik, og repository haandterer databasen.
@Service
public class BilService {

    // @Autowired: Spring indsaetter BilRepository automatisk.
    @Autowired
    private BilRepository bilRepository;

    // Henter alle biler fra databasen via repository.
    public List<Bil> findAll() {
        return bilRepository.findAll();
    }

    // Gemmer en bil i databasen via repository.
    public void save(Bil bil) {
        bilRepository.save(bil);
    }
}
