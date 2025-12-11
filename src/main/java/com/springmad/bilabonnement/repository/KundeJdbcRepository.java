package com.springmad.bilabonnement.repository;

import com.springmad.bilabonnement.model.Kunde;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// Repository der bruger JdbcTemplate og ren SQL til kunder.
@Repository
public class KundeJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public KundeJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Opretter en ny kunde i databasen.
    public void opretKunde(Kunde kunde) {
        String sql = "INSERT INTO kunder (navn, email, telefon) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, kunde.getNavn(), kunde.getEmail(), kunde.getTelefon());
    }

    // Henter alle kunder.
    public List<Kunde> findAll() {
        String sql = "SELECT id, navn, email, telefon FROM kunder";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowTilKunde(rs));
    }

    private Kunde mapRowTilKunde(ResultSet rs) throws SQLException {
        Kunde k = new Kunde();
        k.setId(rs.getInt("id"));
        k.setNavn(rs.getString("navn"));
        k.setEmail(rs.getString("email"));
        k.setTelefon(rs.getString("telefon"));
        return k;
    }
}
