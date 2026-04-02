package com.springmad.bilabonnement.service;

import com.springmad.bilabonnement.repository.ForretningJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Service: service-lag der ligger mellem controller og repository.
// ForretningService haandterer KPI-data og rapportering for forretningsudviklere.
@Service
public class ForretningService {

    @Autowired
    private ForretningJdbcRepository forretningJdbcRepository;

    // KPI: antal aktive udlejninger.
    public int antalAktiveUdlejninger() {
        return forretningJdbcRepository.antalAktiveUdlejninger();
    }

    // KPI: samlet maanedlig pris paa aktive abonnementer.
    public BigDecimal samletMaanedligPrisAktive() {
        return forretningJdbcRepository.samletMaanedligPrisAktive();
    }

    // HashMap: antal abonnementer per status (fx {"AKTIV" -> 5, "AFSLUTTET" -> 12}).
    public HashMap<String, Integer> antalAbonnementerPerStatus() {
        return forretningJdbcRepository.antalAbonnementerPerStatus();
    }

    // Liste over aktive udlejninger med JOIN (kunde + bil + periode).
    public List<Map<String, Object>> aktiveUdlejningerMedJoin() {
        return forretningJdbcRepository.aktiveUdlejningerMedJoin();
    }
}
