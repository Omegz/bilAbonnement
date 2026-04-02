package com.springmad.bilabonnement.repository;

import com.springmad.bilabonnement.model.Bil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// Repository til at arbejde med tabellen "biler" i databasen.
// Bruger JdbcTemplate til at koere SQL-queries manuelt.
public class BilRepository {

    // @Autowired fortaeller Spring at den automatisk skal indsaette JdbcTemplate.
    // Vi slipper for selv at oprette objektet med new.
    @Autowired
    private JdbcTemplate jdbc;

    // RowMapper der mapper hver raekke fra ResultSet til et Bil-objekt.
    // Hver kolonne hentes med rs.getXxx() og saettes paa objektet.
    private final RowMapper<Bil> bilRowMapper = (rs, rowNum) -> {
        Bil bil = new Bil();
        bil.setId(rs.getInt("id"));
        bil.setNavn(rs.getString("navn"));
        bil.setAar(rs.getInt("aar"));
        bil.setStartsdato(rs.getDate("startsdato") != null ? rs.getDate("startsdato").toLocalDate() : null);
        bil.setSlutsdato(rs.getDate("slutsdato") != null ? rs.getDate("slutsdato").toLocalDate() : null);
        return bil;
    };

    // Henter alle biler fra databasen.
    // jdbc.query() returnerer en List<Bil> (0, 1 eller flere raekker).
    public List<Bil> findAll() {
        String sql = "SELECT id, navn, aar, startsdato, slutsdato FROM biler";
        return jdbc.query(sql, bilRowMapper);
    }

    // Gemmer en ny bil i databasen med INSERT.
    // jdbc.update() bruges til INSERT, UPDATE og DELETE.
    // Parameteriseret query (?) beskytter mod SQL-injection.
    public void save(Bil bil) {
        String sql = "INSERT INTO biler (navn, aar, startsdato, slutsdato) VALUES (?, ?, ?, ?)";
        jdbc.update(sql, bil.getNavn(), bil.getAar(), bil.getStartsdato(), bil.getSlutsdato());
    }
}
