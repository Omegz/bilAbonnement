package com.springmad.bilabonnement.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// ===== JUnit Test =====
// JUnit er et testframework i Java.
// En JUnit test tester en "unit" (metode) ad gangen.
// Man bruger assert-metoder til at sammenligne forventet og faktisk resultat.
//
// Denne testklasse tester Singleton-patternet i RolleDefinitioner.
// Vi tester:
//   1. At getInstance() aldrig returnerer null
//   2. At getInstance() altid returnerer SAMME instans (Singleton)
//   3. At rollerne har de korrekte vaerdier
//   4. At statusserne har de korrekte vaerdier
//   5. At kontrakt-typerne har de korrekte vaerdier
class RolleDefinitionerTest {

    @Test
    // Tester at getInstance() ikke returnerer null.
    // assertNotNull() fejler hvis vaerdien er null.
    void getInstance_returnererIkkeNull() {
        // Act: hent instansen
        RolleDefinitioner instans = RolleDefinitioner.getInstance();

        // Assert: instansen maa ikke vaere null
        assertNotNull(instans);
    }

    @Test
    // Tester KERNEN i Singleton: at der kun er EEN instans.
    // Hvis vi kalder getInstance() to gange, skal vi faa SAMME objekt.
    // assertSame() tjekker at to variable peger paa SAMME objekt i hukommelsen (==).
    // Det er forskellen paa assertEquals (vaerdi) og assertSame (reference/objekt).
    void getInstance_returnererSammeInstansHverGang() {
        // Act: hent instansen to gange
        RolleDefinitioner instans1 = RolleDefinitioner.getInstance();
        RolleDefinitioner instans2 = RolleDefinitioner.getInstance();

        // Assert: begge variable peger paa SAMME objekt (Singleton)
        assertSame(instans1, instans2);
        // Hvis dette fejler, er Singleton-patternet implementeret forkert,
        // fordi der saa er oprettet mere end een instans.
    }

    @Test
    // Tester at rollerne returnerer de korrekte vaerdier.
    // assertEquals() sammenligner forventet vaerdi med faktisk vaerdi.
    // Foerste parameter = forventet, anden parameter = faktisk.
    void roller_harKorrekteVaerdier() {
        // Arrange: hent den ene instans
        RolleDefinitioner roller = RolleDefinitioner.getInstance();

        // Assert: hver rolle skal matche den forventede streng
        assertEquals("DATAREGISTRERING", roller.getRolleDataregistrering());
        assertEquals("SKADE_OG_UDBEDRING", roller.getRolleSkadeOgUdbedring());
        assertEquals("FORRETNING", roller.getRolleForretning());
    }

    @Test
    // Tester at abonnement-statusser returnerer de korrekte vaerdier.
    void statusser_harKorrekteVaerdier() {
        RolleDefinitioner roller = RolleDefinitioner.getInstance();

        assertEquals("AKTIV", roller.getStatusAktiv());
        assertEquals("AFSLUTTET", roller.getStatusAfsluttet());
    }

    @Test
    // Tester at kontrakt-typer returnerer de korrekte vaerdier.
    void kontraktTyper_harKorrekteVaerdier() {
        RolleDefinitioner roller = RolleDefinitioner.getInstance();

        assertEquals("LIMITED", roller.getKontraktLimited());
        assertEquals("UNLIMITED", roller.getKontraktUnlimited());
    }
}
