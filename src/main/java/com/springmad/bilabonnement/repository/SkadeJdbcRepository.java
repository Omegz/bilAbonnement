package com.springmad.bilabonnement.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// Repository til skader (ren SQL via JdbcTemplate)
@Repository
public class SkadeJdbcRepository {

    // @Autowired: Spring indsaetter JdbcTemplate automatisk (dependency injection).
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Gemmer flere skader paa samme abonnement (een raekke pr. skade med sin egen pris).
    //
    // ===== LinkedList =====
    // LinkedList er en List-implementering der er hurtig til at TILFOEJE og FJERNE elementer.
    // Internt er den en kaede af noder, hvor hver node har data og en pointer til naeste node.
    // Vi bruger LinkedList her fordi vi tilfojer skader een ad gangen (mange add-operationer).
    // Til sammenligning:
    //   - ArrayList: hurtig til at LAESE via index (get(i))
    //   - LinkedList: hurtig til at TILFOEJE og FJERNE elementer
    //
    // ===== Iterator =====
    // Iterator er et objekt der bruges til at gennemlobe en samling (collection).
    // Den har tre vigtige metoder:
    //   - hasNext(): returnerer true hvis der er flere elementer
    //   - next(): returnerer det naeste element
    //   - remove(): fjerner det sidst hentede element (sikker fjernelse under iteration)
    // Vi bruger Iterator her i stedet for en for-loop, fordi Iterator giver
    // sikker gennemlobning og undgaar ConcurrentModificationException.
    public void opretSkader(int abonnementId, List<String> beskrivelser, List<BigDecimal> priser) {
        String sql = "INSERT INTO skader (abonnement_id, beskrivelse, pris, oprettet_dato) VALUES (?, ?, ?, ?)";

        // Samler alle skadebeskrivelser i en LinkedList (hurtig tilfoejelse).
        LinkedList<String> skadeKoe = new LinkedList<>(beskrivelser);

        // Bruger Iterator til at gennemlobe LinkedListen.
        // Vi holder styr paa index manuelt for at hente matchende pris.
        Iterator<String> iterator = skadeKoe.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            // next() henter det naeste element fra listen
            String beskrivelse = iterator.next();

            jdbcTemplate.update(sql,
                    abonnementId,
                    beskrivelse,
                    priser.get(index),
                    LocalDate.now()
            );
            index++;
        }
    }

    // Henter eksisterende skader for et abonnement (til visning)
    public List<Map<String, Object>> findSkaderForAbonnement(int abonnementId) {
        String sql = "SELECT id, beskrivelse, pris, oprettet_dato FROM skader WHERE abonnement_id = ? ORDER BY id DESC";
        return jdbcTemplate.queryForList(sql, abonnementId);
    }
}
