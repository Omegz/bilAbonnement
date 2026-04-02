package com.springmad.bilabonnement.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// ===== JUnit Test for Bil =====
// Tester at Bil-modellens getters og setters virker korrekt.
//
// Navngivning af testmetoder: beskrivende navne saa man kan se hvad der testes.
// Foelger moensteret: metode_situation_forventetResultat
//
// assertEquals(forventet, faktisk): foerste parameter er FORVENTET vaerdi,
// anden parameter er FAKTISK vaerdi. Man kan ogsaa give en besked som foerste
// parameter: assertEquals("besked", forventet, faktisk) — beskeden vises hvis testen fejler.
class BilTest {

    @Test
    // Tester at vi kan saette og hente navn.
    void setNavn_ogGetNavn_returnererKorrektVaerdi() {
        // Arrange: opret en bil
        Bil bil = new Bil();

        // Act: saet navn
        bil.setNavn("Toyota Yaris");

        // Assert: assertEquals(besked, forventet, faktisk)
        // Beskeden vises KUN hvis testen fejler — goer det nemt at se hvad der gik galt.
        assertEquals("Toyota Yaris", bil.getNavn(),
                "getNavn() skal returnere det navn vi satte med setNavn()");
    }

    @Test
    // Tester at vi kan saette og hente aar.
    void setAar_ogGetAar_returnererKorrektVaerdi() {
        Bil bil = new Bil();

        bil.setAar(2023);

        assertEquals(2023, bil.getAar(),
                "getAar() skal returnere 2023 efter setAar(2023)");
    }

    @Test
    // Tester at vi kan saette og hente datoer.
    void setDatoer_ogGetDatoer_returnererKorrektVaerdi() {
        Bil bil = new Bil();
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate slut = LocalDate.of(2024, 12, 31);

        bil.setStartsdato(start);
        bil.setSlutsdato(slut);

        assertEquals(start, bil.getStartsdato(),
                "startsdato skal vaere 2024-01-01");
        assertEquals(slut, bil.getSlutsdato(),
                "slutsdato skal vaere 2024-12-31");
    }

    @Test
    // Tester at en ny Bil har standardvaerdier (0 for int, null for objekter).
    void nyBil_harStandardvaerdier() {
        Bil bil = new Bil();

        assertEquals(0, bil.getId(),
                "ny bil skal have id = 0 (int default)");
        assertNull(bil.getNavn(),
                "ny bil skal have navn = null (String default)");
        assertEquals(0, bil.getAar(),
                "ny bil skal have aar = 0 (int default)");
        assertNull(bil.getStartsdato(),
                "ny bil skal have startsdato = null (LocalDate default)");
        assertNull(bil.getSlutsdato(),
                "ny bil skal have slutsdato = null (LocalDate default)");
    }
}
