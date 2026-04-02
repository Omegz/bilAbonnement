package com.springmad.bilabonnement.model;

import java.time.LocalDate;

// Model-klasse der repraesenterer en bil i systemet.
// Felterne svarer til kolonnerne i tabellen "biler" i databasen.
public class Bil {

    private int id;
    private String navn;
    private int aar;
    private LocalDate startsdato;
    private LocalDate slutsdato;

    // Tom konstruktor (bruges af Spring og BeanPropertyRowMapper).
    public Bil() {
    }

    // Getters og setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public int getAar() {
        return aar;
    }

    public void setAar(int aar) {
        this.aar = aar;
    }

    public LocalDate getStartsdato() {
        return startsdato;
    }

    public void setStartsdato(LocalDate startsdato) {
        this.startsdato = startsdato;
    }

    public LocalDate getSlutsdato() {
        return slutsdato;
    }

    public void setSlutsdato(LocalDate slutsdato) {
        this.slutsdato = slutsdato;
    }
}
