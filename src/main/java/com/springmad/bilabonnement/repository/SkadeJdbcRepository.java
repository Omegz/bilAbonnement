package com.springmad.bilabonnement.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// Repository til skader (ren SQL via JdbcTemplate)
@Repository
public class SkadeJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public SkadeJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Gemmer flere skader på samme abonnement (én række pr. skade med sin egen pris)
    public void opretSkader(int abonnementId, List<String> beskrivelser, List<BigDecimal> priser) {
        String sql = "INSERT INTO skader (abonnement_id, beskrivelse, pris, oprettet_dato) VALUES (?, ?, ?, ?)";

        for (int i = 0; i < beskrivelser.size(); i++) {
            jdbcTemplate.update(sql,
                    abonnementId,
                    beskrivelser.get(i),
                    priser.get(i),
                    LocalDate.now()
            );
        }
    }

    // Henter eksisterende skader for et abonnement (til visning)
    public List<Map<String, Object>> findSkaderForAbonnement(int abonnementId) {
        String sql = "SELECT id, beskrivelse, pris, oprettet_dato FROM skader WHERE abonnement_id = ? ORDER BY id DESC";
        return jdbcTemplate.queryForList(sql, abonnementId);
    }
}
