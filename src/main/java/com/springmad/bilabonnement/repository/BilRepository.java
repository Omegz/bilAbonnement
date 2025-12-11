package com.springmad.bilabonnement.repository;

import com.springmad.bilabonnement.model.Bil;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BilRepository extends CrudRepository<Bil, Long> {
}
