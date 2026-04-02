package com.springmad.bilabonnement.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// ===== JUnit Test for SkadeValideringService =====
// Tester forretningslogikken for validering af skader.
// Vi tester baade happy flow (gyldige input) og exception flow (ugyldige input).
//
// Grunden til at valideringen ligger i en service (og ikke i controlleren):
// En privat metode i en controller kan man IKKE teste med JUnit.
// Ved at flytte logikken til en public metode i en service, kan vi teste den.
class SkadeValideringServiceTest {

    // Vi opretter servicen direkte med new (den har ingen dependencies).
    // Ingen Mockito nødvendig her, fordi servicen ikke bruger databasen.
    private final SkadeValideringService service = new SkadeValideringService();

    // ===== HAPPY FLOW TESTS =====

    @Test
    // Tester at een gyldig skade returnerer true.
    // assertEquals(forventet, faktisk) sammenligner vaerdierne.
    void gyldigSkadeliste_enGyldigSkade_returnererTrue() {
        // Arrange: opret lister med een gyldig skade
        List<String> beskrivelser = new ArrayList<>();
        beskrivelser.add("Ridse i doer");

        List<BigDecimal> priser = new ArrayList<>();
        priser.add(new BigDecimal("500.00"));

        // Act: kald metoden
        boolean resultat = service.gyldigSkadeliste(beskrivelser, priser);

        // Assert: resultatet skal vaere true (gyldig)
        assertTrue(resultat);
    }

    @Test
    // Tester at flere gyldige skader returnerer true.
    void gyldigSkadeliste_flereGyldigeSkader_returnererTrue() {
        // Arrange
        List<String> beskrivelser = new ArrayList<>();
        beskrivelser.add("Ridse i doer");
        beskrivelser.add("Bule i koefanger");
        beskrivelser.add("Revne i forrude");

        List<BigDecimal> priser = new ArrayList<>();
        priser.add(new BigDecimal("500.00"));
        priser.add(new BigDecimal("1200.00"));
        priser.add(new BigDecimal("3000.00"));

        // Act
        boolean resultat = service.gyldigSkadeliste(beskrivelser, priser);

        // Assert
        assertTrue(resultat);
    }

    // ===== EXCEPTION FLOW TESTS =====

    @Test
    // Tester at null beskrivelser returnerer false.
    void gyldigSkadeliste_nullBeskrivelser_returnererFalse() {
        List<BigDecimal> priser = new ArrayList<>();
        priser.add(new BigDecimal("500.00"));

        // Act: send null som beskrivelser
        boolean resultat = service.gyldigSkadeliste(null, priser);

        // Assert: skal vaere false (ugyldig)
        assertFalse(resultat);
    }

    @Test
    // Tester at tom liste returnerer false.
    void gyldigSkadeliste_tomListe_returnererFalse() {
        List<String> beskrivelser = new ArrayList<>();
        List<BigDecimal> priser = new ArrayList<>();

        boolean resultat = service.gyldigSkadeliste(beskrivelser, priser);

        assertFalse(resultat);
    }

    @Test
    // Tester at forskellige antal beskrivelser og priser returnerer false.
    // Fx 2 beskrivelser men kun 1 pris = ugyldig.
    void gyldigSkadeliste_forskelligtAntal_returnererFalse() {
        List<String> beskrivelser = new ArrayList<>();
        beskrivelser.add("Ridse");
        beskrivelser.add("Bule");

        List<BigDecimal> priser = new ArrayList<>();
        priser.add(new BigDecimal("500.00"));
        // Mangler en pris til "Bule"

        boolean resultat = service.gyldigSkadeliste(beskrivelser, priser);

        assertFalse(resultat);
    }

    @Test
    // Tester at blank beskrivelse returnerer false.
    void gyldigSkadeliste_blankBeskrivelse_returnererFalse() {
        List<String> beskrivelser = new ArrayList<>();
        beskrivelser.add("   "); // Blank (kun mellemrum)

        List<BigDecimal> priser = new ArrayList<>();
        priser.add(new BigDecimal("500.00"));

        boolean resultat = service.gyldigSkadeliste(beskrivelser, priser);

        assertFalse(resultat);
    }

    @Test
    // Tester at negativ pris returnerer false.
    void gyldigSkadeliste_negativPris_returnererFalse() {
        List<String> beskrivelser = new ArrayList<>();
        beskrivelser.add("Ridse i doer");

        List<BigDecimal> priser = new ArrayList<>();
        priser.add(new BigDecimal("-100.00")); // Negativ pris

        boolean resultat = service.gyldigSkadeliste(beskrivelser, priser);

        assertFalse(resultat);
    }

    @Test
    // Tester at pris paa nul returnerer false (skal vaere positiv).
    void gyldigSkadeliste_nulPris_returnererFalse() {
        List<String> beskrivelser = new ArrayList<>();
        beskrivelser.add("Ridse i doer");

        List<BigDecimal> priser = new ArrayList<>();
        priser.add(BigDecimal.ZERO);

        boolean resultat = service.gyldigSkadeliste(beskrivelser, priser);

        assertFalse(resultat);
    }

    @Test
    // Tester at duplikerede beskrivelser returnerer false.
    // Her bruger metoden HashSet internt til at fange dubletter.
    void gyldigSkadeliste_duplikeretBeskrivelse_returnererFalse() {
        List<String> beskrivelser = new ArrayList<>();
        beskrivelser.add("Ridse i doer");
        beskrivelser.add("Ridse i doer"); // Duplikat!

        List<BigDecimal> priser = new ArrayList<>();
        priser.add(new BigDecimal("500.00"));
        priser.add(new BigDecimal("500.00"));

        boolean resultat = service.gyldigSkadeliste(beskrivelser, priser);

        assertFalse(resultat);
    }
}
