package com.springmad.bilabonnement.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/*
 * Repository for haandtering af skader.
 *
 * Denne klasse har ANSVAR for:
 *  - Al databaseadgang relateret til skader
 *  - Udførsel af SQL via JdbcTemplate
 *
 * Designvalg:
 *  - Ingen Spring Data JPA
 *  - Ren SQL (JDBC) for maksimal kontrol og gennemsigtighed
 *
 * Dette matcher eksamenskravet:
 *  "Repository med databaseadgang vha. JDBC eller JdbcTemplate"
 */
@Repository
public class SkadeJdbcRepository {

    /*
     * JdbcTemplate er Spring's abstraction ovenpaa JDBC.
     * Den haandterer:
     *  - Connection haandtering
     *  - PreparedStatements
     *  - Exception-oversaettelse
     */
    private final JdbcTemplate jdbcTemplate;

    /*
     * Constructor injection.
     * JdbcTemplate bliver automatisk leveret af Spring.
     */
    public SkadeJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*
     * Opretter flere skader paa samme abonnement.
     *
     * Der oprettes EN database-raekke pr. skade.
     * Dette afspejler virkeligheden:
     *  - Hver skade har sin egen beskrivelse
     *  - Hver skade har sin egen pris
     *
     * Parametre:
     *  - abonnementId: reference til lejeaftalen
     *  - beskrivelser: liste af tekstbeskrivelser
     *  - priser: liste af priser (matcher index i beskrivelser)
     */
    public void opretSkader(int abonnementId,
                            List<String> beskrivelser,
                            List<BigDecimal> priser) {

        /*
         * SQL statement med placeholders (?).
         * Forhindrer SQL injection.
         */
        String sql =
                "INSERT INTO skader (abonnement_id, beskrivelse, pris, oprettet_dato) " +
                        "VALUES (?, ?, ?, ?)";

        /*
         * Itererer over alle skader fra formularen.
         * Hver iteration opretter en ny skade i databasen.
         */
        for (int i = 0; i < beskrivelser.size(); i++) {
            jdbcTemplate.update(
                    sql,
                    abonnementId,
                    beskrivelser.get(i),
                    priser.get(i),
                    LocalDate.now() // registreringsdato = dags dato
            );
        }
    }

    /*
     * Henter alle skader for et specifikt abonnement.
     *
     * Bruges til:
     *  - Visning paa skade-oversigtssiden
     *  - Dokumentation af allerede registrerede skader
     *
     * Return type:
     *  - List<Map<String,Object>> for fleksibilitet i view-laget
     *  - Ingen tung mapping til model (MVP-loesning)
     */
    public List<Map<String, Object>> findSkaderForAbonnement(int abonnementId) {

        String sql =
                "SELECT id, beskrivelse, pris, oprettet_dato " +
                        "FROM skader " +
                        "WHERE abonnement_id = ? " +
                        "ORDER BY id DESC";

        return jdbcTemplate.queryForList(sql, abonnementId);
    }
}
