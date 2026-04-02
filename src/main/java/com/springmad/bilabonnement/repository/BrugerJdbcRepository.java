package com.springmad.bilabonnement.repository;

import com.springmad.bilabonnement.model.Bruger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository for brugere.
// Bruger BeanPropertyRowMapper til automatisk mapping (kolonnenavne matcher feltnavne).
@Repository
public class BrugerJdbcRepository {

    // @Autowired: Spring indsaetter JdbcTemplate automatisk (dependency injection).
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Opretter en bruger i tabellen "brugere".
    // jdbc.update() bruges til INSERT. Parameteriseret query (?) beskytter mod SQL-injection.
    public void opretBruger(Bruger bruger) {
        String sql = "INSERT INTO brugere (navn, alder, rolle, password) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                bruger.getNavn(),
                bruger.getAlder(),
                bruger.getRolle(),
                bruger.getPassword()
        );
    }

    // Finder en bruger baseret paa navn + password.
    // Returnerer enten en Bruger eller null hvis ingen match.
    //
    // ===== BeanPropertyRowMapper =====
    // BeanPropertyRowMapper mapper AUTOMATISK kolonner til felter:
    //   kolonne "id"       -> setId()
    //   kolonne "navn"     -> setNavn()
    //   kolonne "alder"    -> setAlder()
    //   kolonne "rolle"    -> setRolle()
    //   kolonne "password" -> setPassword()
    // Vi slipper for manuelt at skrive rs.getString("navn") osv.
    public Bruger findByNavnOgPassword(String navn, String password) {
        String sql = "SELECT id, navn, alder, rolle, password FROM brugere WHERE navn = ? AND password = ? LIMIT 1";

        List<Bruger> resultater = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(Bruger.class),
                navn, password);

        // stream().findFirst().orElse(null):
        // Returnerer foerste element i listen, eller null hvis listen er tom.
        return resultater
                .stream()
                .findFirst()
                .orElse(null);
    }
}
