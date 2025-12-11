package com.springmad.bilabonnement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// Modelklasse som matcher tabellen "abonnementer".
public class Abonnement {

    private Integer id;              // Primærnøgle
    private Integer bilId;           // FK til biler.id
    private Integer kundeId;         // FK til kunder.id
    private LocalDate startdato;
    private LocalDate slutdato;
    private BigDecimal maanedligPris;
    private String status;

    public Abonnement() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBilId() {
        return bilId;
    }

    public void setBilId(Integer bilId) {
        this.bilId = bilId;
    }

    public Integer getKundeId() {
        return kundeId;
    }

    public void setKundeId(Integer kundeId) {
        this.kundeId = kundeId;
    }

    public LocalDate getStartdato() {
        return startdato;
    }

    public void setStartdato(LocalDate startdato) {
        this.startdato = startdato;
    }

    public LocalDate getSlutdato() {
        return slutdato;
    }

    public void setSlutdato(LocalDate slutdato) {
        this.slutdato = slutdato;
    }

    public BigDecimal getMaanedligPris() {
        return maanedligPris;
    }

    public void setMaanedligPris(BigDecimal maanedligPris) {
        this.maanedligPris = maanedligPris;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
