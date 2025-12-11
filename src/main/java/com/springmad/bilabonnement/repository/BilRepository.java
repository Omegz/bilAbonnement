package com.springmad.bilabonnement.repository;

import com.springmad.bilabonnement.model.Bil;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
// Dette repository giver adgang til CRUD-operationer på Bil-objekter.
// Spring Data JDBC genererer automatisk metoder til at gemme, hente,
// opdatere og slette data i "biler"-tabellen.
public interface BilRepository extends CrudRepository<Bil, Long> {
    // CrudRepository<Bil, Long> betyder:
    // Bil = typen der arbejdes med
    // Long = typen på primærnøglen (id)
}
