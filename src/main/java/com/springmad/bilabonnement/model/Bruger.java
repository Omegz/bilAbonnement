package com.springmad.bilabonnement.model;

// Model til tabellen "brugere" (intern login - MVP)
public class Bruger {

    private Integer id;
    private String navn;
    private Integer alder;

    // Roller i systemet (bruges til at vise forskellige menuer)
    // DATAREGISTRERING | SKADE_OG_UDBEDRING | FORRETNING
    private String rolle;

    // MVP: password i klar tekst (kun til demo)
    private String password;

    public Bruger() {
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

    public Integer getAlder() {
        return alder;
    }

    public void setAlder(Integer alder) {
        this.alder = alder;
    }

    public String getRolle() {
        return rolle;
    }

    public void setRolle(String rolle) {
        this.rolle = rolle;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
