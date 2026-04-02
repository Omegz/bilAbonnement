package com.springmad.bilabonnement.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// ===== JUnit Test for Bil =====
// Tester at Bil-modellens getters og setters virker korrekt.
// Dette er vigtigt fordi Bil bruges til at flytte data mellem:
//   - Databasen (via RowMapper i BilRepository)
//   - Controlleren (via @ModelAttribute)
//   - Viewet (via Thymeleaf th:field)
// Hvis en getter/setter er forkert, vises forkert data i hele systemet.
class BilTest {

    @Test
    // Tester at vi kan saette og hente navn.
    void setNavn_ogGetNavn_returnererKorrektVaerdi() {
        // Arrange: opret en bil
        Bil bil = new Bil();

        // Act: saet navn
        bil.setNavn("Toyota Yaris");

        // Assert: get skal returnere det vi satte
        assertEquals("Toyota Yaris", bil.getNavn());
    }

    @Test
    // Tester at vi kan saette og hente aar.
    void setAar_ogGetAar_returnererKorrektVaerdi() {
        Bil bil = new Bil();

        bil.setAar(2023);

        assertEquals(2023, bil.getAar());
    }

    @Test
    // Tester at vi kan saette og hente datoer.
    void setDatoer_ogGetDatoer_returnererKorrektVaerdi() {
        Bil bil = new Bil();
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate slut = LocalDate.of(2024, 12, 31);

        bil.setStartsdato(start);
        bil.setSlutsdato(slut);

        assertEquals(start, bil.getStartsdato());
        assertEquals(slut, bil.getSlutsdato());
    }

    @Test
    // Tester at en ny Bil har standardvaerdier (0 for int, null for objekter).
    void nyBil_harStandardvaerdier() {
        Bil bil = new Bil();

        assertEquals(0, bil.getId());
        assertNull(bil.getNavn());
        assertEquals(0, bil.getAar());
        assertNull(bil.getStartsdato());
        assertNull(bil.getSlutsdato());
    }
}
