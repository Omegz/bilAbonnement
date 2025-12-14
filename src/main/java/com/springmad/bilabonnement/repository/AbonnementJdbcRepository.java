package com.springmad.bilabonnement.repository;

import com.springmad.bilabonnement.model.AbonnementOversigt;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

// Repository som bruger JdbcTemplate og ren SQL til abonnementer.
@Repository
public class AbonnementJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public AbonnementJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
    public boolean harAktivtAbonnementForKundeNavn(String kundeNavn) {
        String sql = """
                SELECT COUNT(*)
                FROM abonnementer a
                JOIN kunder k ON a.kunde_id = k.id
                WHERE k.navn = ? AND a.status = 'AKTIV'
                """;

        Integer antal = jdbcTemplate.queryForObject(sql, Integer.class, kundeNavn);
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

        String insertSql = """
                INSERT INTO abonnementer (bil_id, kunde_id, startdato, slutdato, maanedlig_pris, status)
                VALUES (?, ?, ?, ?, ?, 'AKTIV')
                """;

        jdbcTemplate.update(insertSql, bilId, kundeId, startdato, slutdato, maanedligPris);
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


    public void opretLejeaftaleMedDetaljer(String kundeNavn,
                                           int bilId,
                                           java.time.LocalDate startdato,
                                           java.time.LocalDate slutdato,
                                           java.math.BigDecimal maanedligPris,
                                           String kontraktType,
                                           int kontraktVarighedDage,
                                           String udleveringsstedType,
                                           String leveringsform) {

        // Find kunde-id ud fra navn (MVP)
        String findKundeSql = "SELECT id FROM kunder WHERE navn = ?";
        Integer kundeId = jdbcTemplate.queryForObject(findKundeSql, Integer.class, kundeNavn);

        String insertSql = """
            INSERT INTO abonnementer
            (bil_id, kunde_id, startdato, slutdato, maanedlig_pris, status,
             kontrakt_type, kontrakt_varighed_dage, udleveringssted_type, leveringsform)
            VALUES
            (?, ?, ?, ?, ?, 'AKTIV', ?, ?, ?, ?)
            """;

        jdbcTemplate.update(insertSql,
                bilId,
                kundeId,
                startdato,
                slutdato,
                maanedligPris,
                kontraktType,
                kontraktVarighedDage,
                udleveringsstedType,
                leveringsform
        );
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

        String insertSql = """
            INSERT INTO abonnementer
            (bil_id, kunde_id, startdato, slutdato, maanedlig_pris, status,
             kontrakt_type, kontrakt_varighed_dage, udleveringssted_type, leveringsform, leveringsadresse)
            VALUES
            (?, ?, ?, ?, ?, 'AKTIV', ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(insertSql,
                bilId,
                kundeId,
                startdato,
                slutdato,
                maanedligPris,
                kontraktType,
                kontraktVarighedDage,
                udleveringsstedType,
                leveringsform,
                leveringsadresse
        );
    }


    // Henter afsluttede abonnementer (slutdato <= i dag) – kan filtreres på kunde.
    public java.util.List<com.springmad.bilabonnement.model.AbonnementOption> findAfsluttedeAbonnementer(Integer kundeId) {

        String baseSql = """
        SELECT a.id AS abonnementId, k.navn AS kundeNavn, b.navn AS bilNavn, a.slutdato AS slutdato
        FROM abonnementer a
        JOIN kunder k ON a.kunde_id = k.id
        JOIN biler b ON a.bil_id = b.id
        WHERE a.slutdato IS NOT NULL
          AND a.slutdato <= ?
        """;

        java.util.List<Object> params = new java.util.ArrayList<>();
        params.add(java.sql.Date.valueOf(java.time.LocalDate.now()));

        if (kundeId != null) {
            baseSql += " AND a.kunde_id = ? ";
            params.add(kundeId);
        }

        baseSql += " ORDER BY a.slutdato DESC";

        return jdbcTemplate.query(baseSql, (rs, rowNum) ->
                new com.springmad.bilabonnement.model.AbonnementOption(
                        rs.getInt("abonnementId"),
                        rs.getString("kundeNavn"),
                        rs.getString("bilNavn"),
                        rs.getDate("slutdato").toLocalDate()
                ), params.toArray());
    }

    // Bruges til server-side validering: må vi oprette skade på denne kontrakt?
    public boolean erAbonnementAfsluttet(int abonnementId) {
        String sql = """
        SELECT COUNT(*)
        FROM abonnementer
        WHERE id = ?
          AND slutdato IS NOT NULL
          AND slutdato <= ?
        """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,
                abonnementId,
                java.sql.Date.valueOf(java.time.LocalDate.now())
        );
        return count != null && count > 0;
    }


}


