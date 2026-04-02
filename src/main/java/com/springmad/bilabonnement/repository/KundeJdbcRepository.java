package com.springmad.bilabonnement.repository;

import com.springmad.bilabonnement.model.Kunde;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository der bruger JdbcTemplate og ren SQL til kunder.
@Repository
public class KundeJdbcRepository {

    // @Autowired: Spring indsaetter JdbcTemplate automatisk (dependency injection).
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Opretter en ny kunde i databasen.
    // jdbc.update() bruges til INSERT, UPDATE og DELETE.
    // Parameteriseret query (?) beskytter mod SQL-injection.
    public void opretKunde(Kunde kunde) {
        String sql = "INSERT INTO kunder (navn, email, telefon) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, kunde.getNavn(), kunde.getEmail(), kunde.getTelefon());
    }

    // Henter alle kunder fra databasen.
    //
    // ===== BeanPropertyRowMapper =====
    // BeanPropertyRowMapper er en standard-implementering af RowMapper,
    // som AUTOMATISK mapper kolonner i ResultSet til felter i et Java-objekt.
    // Den matcher kolonnenavne med feltnavne (via getters/setters).
    //
    // Eksempel: kolonnen "navn" i databasen -> getNavn()/setNavn() i Kunde-klassen.
    //
    // Fordele:
    //   - Vi slipper for manuelt at skrive rs.getString("navn"), rs.getInt("id") osv.
    //   - Koden bliver kortere og renere
    //   - Mindre risiko for stavefejl i kolonnenavne
    //
    // Kraev:
    //   - Kolonnenavne i databasen SKAL matche feltnavne i Java-klassen
    //   - Java-klassen SKAL have en tom konstruktoer og getters/setters
    //   - Kunde har begge dele (tom konstruktoer + getters/setters)
    //
    // Sammenligning med manuel RowMapper (som vi bruger i BilRepository):
    //
    //   Manuel RowMapper (BilRepository):
    //     return jdbc.query(sql, (rs, rowNum) -> {
    //         Bil bil = new Bil();
    //         bil.setId(rs.getInt("id"));        // manuelt for hvert felt
    //         bil.setNavn(rs.getString("navn"));  // manuelt for hvert felt
    //         return bil;
    //     });
    //
    //   BeanPropertyRowMapper (her):
    //     return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Kunde.class));
    //     // Automatisk — ingen manuel mapping noedvendig
    //
    public List<Kunde> findAll() {
        String sql = "SELECT id, navn, email, telefon FROM kunder";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Kunde.class));
    }

    // Sletter en kunde fra databasen baseret paa id.
    public void sletKunde(int id) {
        String sql = "DELETE FROM kunder WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
