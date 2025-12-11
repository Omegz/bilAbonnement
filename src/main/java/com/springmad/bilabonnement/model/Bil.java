package com.springmad.bilabonnement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("biler")
public class Bil {

    @Id
    private Long id;

    private String navn;

    @Column("år")
    private Integer aar;

    private LocalDate startsdato;

    private LocalDate slutsdato;

    public Bil() {
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
