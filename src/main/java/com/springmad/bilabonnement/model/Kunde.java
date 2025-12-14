package com.springmad.bilabonnement.model;

/*
 * Modelklasse der repræsenterer en kunde.
 *
 * Denne klasse matcher direkte tabellen "kunder" i databasen.
 *
 * Design:
 *  - Simpel POJO (Plain Old Java Object)
 *  - Ingen forretningslogik
 *  - Kun data + getters/setters
 *
 * Bruges i:
 *  - Repositories (JDBC mapping)
 *  - Controllers
 *  - Views (Thymeleaf)
 */
public class Kunde {

    /*
     * Primærnøgle i databasen.
     * AUTO_INCREMENT i MySQL.
     */
    private Integer id;

    /*
     * Kundens fulde navn.
     * Bruges i dropdowns, oversigter og rapporter.
     */
    private String navn;

    /*
     * Kundens emailadresse.
     * Kan bruges til kommunikation og identifikation.
     */
    private String email;

    /*
     * Kundens telefonnummer.
     * Bruges ved kontakt og administration.
     */
    private String telefon;

    /*
     * Tom constructor.
     *
     * Kræves af:
     *  - Spring
     *  - JDBC mapping
     *  - Formular-binding (Thymeleaf)
     */
    public Kunde() {
    }

    /* -------------------- Getters & Setters -------------------- */

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
