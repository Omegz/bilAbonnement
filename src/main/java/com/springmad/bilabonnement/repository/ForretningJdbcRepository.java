package com.springmad.bilabonnement.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Repository der laver KPI-udtræk til forretningsudviklere (ren SQL).
@Repository
public class ForretningJdbcRepository {

    // @Autowired: Spring indsaetter JdbcTemplate automatisk (dependency injection).
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    // ===== HashMap =====
    // HashMap er en Map-implementering der gemmer data som noegel-vaerdi par (key-value).
    // Hver noegel er unik, og man kan hurtigt slaa en vaerdi op via noeglen.
    // Vi bruger HashMap her til at taelle antal abonnementer per status.
    // Eksempel: {"AKTIV" -> 5, "AFSLUTTET" -> 12}
    // - Noeglen (key) er status-teksten (fx "AKTIV")
    // - Vaerdien (value) er antallet
    // HashMap bruger hashing internt, saa opslag via key er meget hurtigt.
    //
    // Vigtige Map-metoder:
    //   - put(key, value): indsaetter eller overskriver en vaerdi
    //   - get(key): henter vaerdien for en noegel
    //   - containsKey(key): tjekker om noeglen findes
    //   - keySet(): returnerer alle noegler som et Set
    public HashMap<String, Integer> antalAbonnementerPerStatus() {
        String sql = "SELECT status, COUNT(*) AS antal FROM abonnementer GROUP BY status";

        // jdbc.queryForList() returnerer en List<Map<String, Object>>,
        // hvor hver Map er een raekke fra databasen.
        List<Map<String, Object>> raekker = jdbcTemplate.queryForList(sql);

        // Opretter et tomt HashMap til at samle resultatet.
        HashMap<String, Integer> resultat = new HashMap<>();

        // Gennemlober alle raekker og laegger dem ind i vores HashMap.
        for (Map<String, Object> raekke : raekker) {
            String status = (String) raekke.get("status");
            int antal = ((Number) raekke.get("antal")).intValue();

            // put() indsaetter noegel-vaerdi parret i HashMap.
            // Hvis noeglen allerede findes, overskrives den gamle vaerdi.
            resultat.put(status, antal);
        }

        return resultat;
        // Resultat: fx {"AKTIV" -> 5, "AFSLUTTET" -> 12}
        // Man kan hente en vaerdi med: resultat.get("AKTIV") -> 5
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
