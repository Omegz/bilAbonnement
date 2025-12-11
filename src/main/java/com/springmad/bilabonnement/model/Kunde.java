package com.springmad.bilabonnement.model;

// Simpel modelklasse der matcher tabellen "kunder" i databasen.
public class Kunde {

    private Integer id;      // Primærnøgle
    private String navn;
    private String email;
    private String telefon;

    public Kunde() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
}
