package com.springmad.bilabonnement.repository;

import com.springmad.bilabonnement.model.Bruger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

// Repository til brugere med ren SQL via JdbcTemplate.
@Repository
public class BrugerJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public BrugerJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Opret bruger (signup)
    public void opretBruger(Bruger bruger) {
        String sql = "INSERT INTO brugere (navn, alder, rolle, password) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, bruger.getNavn(), bruger.getAlder(), bruger.getRolle(), bruger.getPassword());
    }

    // Find bruger ud fra navn + password (login)
    public Bruger findByNavnOgPassword(String navn, String password) {
        String sql = "SELECT id, navn, alder, rolle, password FROM brugere WHERE navn = ? AND password = ? LIMIT 1";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowTilBruger(rs), navn, password)
                .stream()
                .findFirst()
                .orElse(null);
    }


    private Bruger mapRowTilBruger(ResultSet rs) throws SQLException {
        Bruger b = new Bruger();
        b.setId(rs.getInt("id"));
        b.setNavn(rs.getString("navn"));
        b.setAlder(rs.getInt("alder"));
        b.setRolle(rs.getString("rolle"));
        b.setPassword(rs.getString("password"));
        return b;
    }
}
