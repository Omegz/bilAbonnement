package com.springmad.bilabonnement.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// ===== JUnit Test for Kunde =====
// Tester at Kunde-modellens getters og setters virker korrekt.
// Kunde bruges i abonnementer, skader og dropdowns — data skal vaere korrekt.
class KundeTest {

    @Test
    // Tester at vi kan saette og hente alle felter.
    void setOgGet_alleFelter_returnererKorrekteVaerdier() {
        // Arrange
        Kunde kunde = new Kunde();

        // Act
        kunde.setId(1);
        kunde.setNavn("Anders Andersen");
        kunde.setEmail("anders@test.dk");
        kunde.setTelefon("12345678");

        // Assert: hvert felt skal matche det vi satte
        assertEquals(1, kunde.getId());
        assertEquals("Anders Andersen", kunde.getNavn());
        assertEquals("anders@test.dk", kunde.getEmail());
        assertEquals("12345678", kunde.getTelefon());
    }

    @Test
    // Tester at en ny Kunde har null som standardvaerdi for alle felter.
    // Integer-felter er null (ikke 0), fordi de er Integer-objekter (ikke int).
    void nyKunde_harNullVaerdier() {
        Kunde kunde = new Kunde();

        assertNull(kunde.getId());
        assertNull(kunde.getNavn());
        assertNull(kunde.getEmail());
        assertNull(kunde.getTelefon());
    }
}
