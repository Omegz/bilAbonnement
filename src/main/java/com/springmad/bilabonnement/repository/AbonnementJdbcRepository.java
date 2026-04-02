package com.springmad.bilabonnement.repository;

import com.springmad.bilabonnement.model.AbonnementOversigt;
import com.springmad.bilabonnement.model.RolleDefinitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Repository som bruger JdbcTemplate og ren SQL til abonnementer.
@Repository
public class AbonnementJdbcRepository {

    // @Autowired: Spring indsaetter JdbcTemplate automatisk (dependency injection).
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Henter en oversigt over alle abonnementer med JOIN på kunder og biler.
    public List<AbonnementOversigt> findAlleMedJoin() {
        String sql = """
                SELECT 
                    a.id AS abonnement_id,
                    k.navn AS kunde_navn,
                    b.navn AS bil_navn,
                    a.startdato,
                    a.slutdato,
                    a.maanedlig_pris,
                    a.status
                FROM abonnementer a
                JOIN kunder k ON a.kunde_id = k.id
                JOIN biler b ON a.bil_id = b.id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowTilAbonnementOversigt(rs));
    }

    // Tjekker om en kunde med givet navn har et aktivt abonnement.
    // Singleton: status-vaerdien hentes fra RolleDefinitioner i stedet for at hardcode 'AKTIV'.
    // Vi bruger ? som parameter i SQL saa vaerdien sendes sikkert (beskytter mod SQL-injection).
    public boolean harAktivtAbonnementForKundeNavn(String kundeNavn) {
        String sql = """
                SELECT COUNT(*)
                FROM abonnementer a
                JOIN kunder k ON a.kunde_id = k.id
                WHERE k.navn = ? AND a.status = ?
                """;

        String statusAktiv = RolleDefinitioner.getInstance().getStatusAktiv();
        Integer antal = jdbcTemplate.queryForObject(sql, Integer.class, kundeNavn, statusAktiv);
        return antal != null && antal > 0;
    }

    // Opretter et nyt abonnement for en kunde ud fra kundens navn.
    public void opretAbonnement(String kundeNavn,
                                int bilId,
                                LocalDate startdato,
                                LocalDate slutdato,
                                BigDecimal maanedligPris) {

        // Find kunde-id ud fra navn
        String findKundeSql = "SELECT id FROM kunder WHERE navn = ?";
        Integer kundeId = jdbcTemplate.queryForObject(findKundeSql, Integer.class, kundeNavn);
        if (kundeId == null) {
            throw new EmptyResultDataAccessException("Kunde ikke fundet", 1);
        }

        // Singleton: status hentes fra RolleDefinitioner saa vi undgaar hardcodede strenge.
        String insertSql = """
                INSERT INTO abonnementer (bil_id, kunde_id, startdato, slutdato, maanedlig_pris, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String statusAktiv = RolleDefinitioner.getInstance().getStatusAktiv();
        jdbcTemplate.update(insertSql, bilId, kundeId, startdato, slutdato, maanedligPris, statusAktiv);
    }

    // Mapper én række fra ResultSet til et AbonnementOversigt-objekt.
    private AbonnementOversigt mapRowTilAbonnementOversigt(ResultSet rs) throws SQLException {
        AbonnementOversigt dto = new AbonnementOversigt();

        dto.setAbonnementId(rs.getInt("abonnement_id"));
        dto.setKundeNavn(rs.getString("kunde_navn"));
        dto.setBilNavn(rs.getString("bil_navn"));
        dto.setStartdato(rs.getDate("startdato").toLocalDate());

        if (rs.getDate("slutdato") != null) {
            dto.setSlutdato(rs.getDate("slutdato").toLocalDate());
        }

        dto.setMaanedligPris(rs.getBigDecimal("maanedlig_pris"));
        dto.setStatus(rs.getString("status"));

        return dto;
    }


    public void opretLejeaftaleMedDetaljerKundeId(int kundeId,
                                                  int bilId,
                                                  java.time.LocalDate startdato,
                                                  java.time.LocalDate slutdato,
                                                  java.math.BigDecimal maanedligPris,
                                                  String kontraktType,
                                                  int kontraktVarighedDage,
                                                  String udleveringsstedType,
                                                  String leveringsform,
                                                  String leveringsadresse) {

        // Singleton: status hentes fra RolleDefinitioner i stedet for at hardcode 'AKTIV'.
        String insertSql = """
            INSERT INTO abonnementer
            (bil_id, kunde_id, startdato, slutdato, maanedlig_pris, status,
             kontrakt_type, kontrakt_varighed_dage, udleveringssted_type, leveringsform, leveringsadresse)
            VALUES
            (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        String statusAktiv = RolleDefinitioner.getInstance().getStatusAktiv();
        jdbcTemplate.update(insertSql,
                bilId,
                kundeId,
                startdato,
                slutdato,
                maanedligPris,
                statusAktiv,
                kontraktType,
                kontraktVarighedDage,
                udleveringsstedType,
                leveringsform,
                leveringsadresse
        );
    }


    // Henter afsluttede abonnementer (slutdato <= i dag) – kan filtreres på kunde.
    public List<com.springmad.bilabonnement.model.AbonnementOption> findAfsluttedeAbonnementer(Integer kundeId) {

        String baseSql = """
        SELECT a.id AS abonnementId,
               k.navn AS kundeNavn,
               b.navn AS bilNavn,
               a.slutdato AS slutdato
        FROM abonnementer a
        JOIN kunder k ON a.kunde_id = k.id
        JOIN biler b ON a.bil_id = b.id
        WHERE a.slutdato IS NOT NULL
          AND a.slutdato <= CURDATE()
        """;

        // ===== ArrayList =====
        // ArrayList er en List-implementering der er hurtig til at LAESE elementer via index.
        // Internt gemmer den data i et array, saa opslag med get(i) er meget hurtigt.
        // Vi bruger ArrayList her til at bygge en liste af SQL-parametre dynamisk.
        // Vi tilfojer kun kundeId hvis det er sat, saa listen kan vaere tom eller have 1 element.
        // ArrayList er det rigtige valg fordi:
        //   - Vi laeser listen til sidst med toArray() (hurtig index-adgang)
        //   - Vi tilfojer maks 1 element (ingen forskel til LinkedList ved saa faa elementer,
        //     men ArrayList er standard-valget naar man primaert laeser data)
        ArrayList<Object> params = new ArrayList<>();

        // Hvis der er valgt kunde, filtrer paa kunde
        if (kundeId != null) {
            baseSql += " AND a.kunde_id = ? ";
            params.add(kundeId);
        }

        baseSql += " ORDER BY a.slutdato DESC";

        return jdbcTemplate.query(
                baseSql,
                (rs, rowNum) -> new com.springmad.bilabonnement.model.AbonnementOption(
                        rs.getInt("abonnementId"),
                        rs.getString("kundeNavn"),
                        rs.getString("bilNavn"),
                        rs.getDate("slutdato").toLocalDate()
                ),
                params.toArray()
        );
    }

    // Bruges til server-side validering: må vi oprette skade på denne kontrakt?
    public boolean erAbonnementAfsluttet(int abonnementId) {
        String sql = """
        SELECT COUNT(*)
        FROM abonnementer
        WHERE id = ?
          AND slutdato IS NOT NULL
          AND slutdato <= CURDATE()
        """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, abonnementId);
        return count != null && count > 0;
    }


}


