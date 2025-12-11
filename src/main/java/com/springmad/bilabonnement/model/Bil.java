package com.springmad.bilabonnement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("biler")
// Denne klasse repræsenterer tabellen "biler" i databasen.
// Spring Data JDBC mapper automatisk felterne til kolonner i tabellen.
public class Bil {

    @Id
    // Primærnøgle i tabellen. Genereres automatisk af databasen.
    private Long id;

    // Navnet på bilen. Mappes direkte til kolonnen "navn".
    private String navn;

    @Column("år")
    // Kolonnenavn i databasen indeholder "å", så vi bruger @Column.
    // Feltet i Java hedder aar, da "år" ikke er gyldigt som variabelnavn.
    private Integer aar;

    // Startdato for abonnementsperioden.
    private LocalDate startsdato;

    // Slutdato for abonnementsperioden.
    private LocalDate slutsdato;

    public Bil() {
        // Tom konstruktor som kræves af Spring Data JDBC.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public Integer getAar() {
        return aar;
    }

    public void setAar(Integer aar) {
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
