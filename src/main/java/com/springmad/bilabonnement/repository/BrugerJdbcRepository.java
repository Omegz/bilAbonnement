package com.springmad.bilabonnement.repository;

import com.springmad.bilabonnement.model.Bruger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

// Repository for brugere.
@Repository
public class BrugerJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    // JdbcTemplate er Spring sin helper til at koere SQL (SELECT/INSERT/UPDATE/DELETE)
    // og samtidig haandtere forbindelser og prepared statements.

    public BrugerJdbcRepository(JdbcTemplate jdbcTemplate) {
        // Constructor injection: Spring sender JdbcTemplate ind automatisk.
        this.jdbcTemplate = jdbcTemplate;
    }

    // Opretter en bruger i tabellen "brugere".
    // Bruges af signup-flowet i AuthController.
    public void opretBruger(Bruger bruger) {

        // INSERT SQL: gemmer de vigtigste felter til login + rollebaseret adgang.
        String sql = "INSERT INTO brugere (navn, alder, rolle, password) VALUES (?, ?, ?, ?)";

        // update() bruges til INSERT/UPDATE/DELETE.
        // ? betyder at vi bruger parameter binding (beskytter mod SQL injection).
        jdbcTemplate.update(
                sql,
                bruger.getNavn(),
                bruger.getAlder(),
                bruger.getRolle(),
                bruger.getPassword()
        );
    }

    // Finder en bruger baseret paa navn + password.
    // Bruges i login-flowet i AuthController.
    // Returnerer enten en Bruger eller null hvis ingen match.
    public Bruger findByNavnOgPassword(String navn, String password) {

        // SELECT SQL: henter alle felter vi skal bruge i sessionen (id + rolle osv.)
        // LIMIT 1 sikrer at vi maks faar 1 raekke tilbage (MVP).
        String sql = "SELECT id, navn, alder, rolle, password FROM brugere WHERE navn = ? AND password = ? LIMIT 1";

        // query() returnerer en liste af resultater.
        // Vi mapper hver raekke om til et Bruger-objekt med mapRowTilBruger().
        // Derefter tager vi foerste element, eller null hvis listen er tom.
        return jdbcTemplate
                .query(sql, (rs, rowNum) -> mapRowTilBruger(rs), navn, password)
                .stream()
                .findFirst()
                .orElse(null);
    }

    // Mapper en ResultSet-raekke til et Bruger-objekt.
    // Dette holder mapping samlet et sted, saa vi ikke gentager kode i flere metoder.
    private Bruger mapRowTilBruger(ResultSet rs) throws SQLException {

        // Opretter et tomt Bruger-objekt.
        Bruger b = new Bruger();

        // Udfylder objektet med kolonner fra databasen.
        // Kolonnenavne skal matche tabellen "brugere".
        b.setId(rs.getInt("id"));
        b.setNavn(rs.getString("navn"));
        b.setAlder(rs.getInt("alder"));
        b.setRolle(rs.getString("rolle"));
        b.setPassword(rs.getString("password"));

        // Returnerer brugeren som controlleren kan gemme i session.
        return b;
    }
}

