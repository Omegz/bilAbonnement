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

        // stream() konverterer listen til en stream (en raekke af elementer).
        // findFirst() henter det foerste element.
        // orElse(null) returnerer null hvis listen er tom (ingen bruger fundet).
        //
        // Det samme som:
        //   if (resultater.isEmpty()) {
        //       return null;
        //   } else {
        //       return resultater.get(0);
        //   }
        //
        // Men stream-versionen er kortere:
        return resultater
                .stream()       // lav listen om til en stream
                .findFirst()    // hent foerste element (eller tomt)
                .orElse(null);  // hvis tomt, returner null
    }
}
