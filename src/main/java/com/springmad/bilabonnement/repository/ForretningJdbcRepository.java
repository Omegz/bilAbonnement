package com.springmad.bilabonnement.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

// Repository der laver KPI-udtræk til forretningsudviklere (ren SQL).
@Repository
public class ForretningJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public ForretningJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // KPI 1: Hvor mange aktive udlejninger (abonnementer) har vi lige nu?
    public int antalAktiveUdlejninger() {
        String sql = "SELECT COUNT(*) FROM abonnementer WHERE status = 'AKTIV'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    // KPI 2: Hvad er samlet månedlig pris på nuværende udlejede biler?
    public BigDecimal samletMaanedligPrisAktive() {
        String sql = "SELECT COALESCE(SUM(maanedlig_pris), 0) FROM abonnementer WHERE status = 'AKTIV'";
        BigDecimal sum = jdbcTemplate.queryForObject(sql, BigDecimal.class);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    // Bonus: Liste over aktive udlejninger (JOIN for at vise kunde + bil + periode)
    public List<Map<String, Object>> aktiveUdlejningerMedJoin() {
        String sql = """
                SELECT a.id AS abonnement_id,
                       k.navn AS kunde_navn,
                       b.navn AS bil_navn,
                       a.startdato,
                       a.slutdato,
                       a.maanedlig_pris
                FROM abonnementer a
                JOIN kunder k ON a.kunde_id = k.id
                JOIN biler b ON a.bil_id = b.id
                WHERE a.status = 'AKTIV'
                ORDER BY a.startdato DESC
                """;
        return jdbcTemplate.queryForList(sql);
    }
}
