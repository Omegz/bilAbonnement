package com.springmad.bilabonnement.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// ===== JUnit Test for Bruger =====
// Tester at Bruger-modellens getters og setters virker korrekt.
// Bruger bruges til login, session og rollebaseret adgang i hele systemet.
class BrugerTest {

    @Test
    // Tester at vi kan saette og hente alle felter.
    void setOgGet_alleFelter_returnererKorrekteVaerdier() {
        Bruger bruger = new Bruger();

        bruger.setId(1);
        bruger.setNavn("TestBruger");
        bruger.setAlder(25);
        bruger.setRolle("DATAREGISTRERING");
        bruger.setPassword("hemmeligt");

        assertEquals(1, bruger.getId());
        assertEquals("TestBruger", bruger.getNavn());
        assertEquals(25, bruger.getAlder());
        assertEquals("DATAREGISTRERING", bruger.getRolle());
        assertEquals("hemmeligt", bruger.getPassword());
    }

    @Test
    // Tester at rollen kan saettes til alle tre gyldige roller.
    // Vi bruger Singleton RolleDefinitioner til at hente de korrekte rollenavne.
    void rolle_kanSaettesTilAlleGyldigeRoller() {
        Bruger bruger = new Bruger();
        RolleDefinitioner roller = RolleDefinitioner.getInstance();

        // Test DATAREGISTRERING
        bruger.setRolle(roller.getRolleDataregistrering());
        assertEquals("DATAREGISTRERING", bruger.getRolle());

        // Test SKADE_OG_UDBEDRING
        bruger.setRolle(roller.getRolleSkadeOgUdbedring());
        assertEquals("SKADE_OG_UDBEDRING", bruger.getRolle());

        // Test FORRETNING
        bruger.setRolle(roller.getRolleForretning());
        assertEquals("FORRETNING", bruger.getRolle());
    }
}
