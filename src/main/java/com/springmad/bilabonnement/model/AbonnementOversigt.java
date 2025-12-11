package com.springmad.bilabonnement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO til at holde resultatet af et JOIN mellem abonnementer, kunder og biler.
public class AbonnementOversigt {

    private Integer abonnementId;
    private String kundeNavn;
    private String bilNavn;
    private LocalDate startdato;
    private LocalDate slutdato;
    private BigDecimal maanedligPris;
    private String status;

    public AbonnementOversigt() {
    }

    public Integer getAbonnementId() {
        return abonnementId;
    }

    public void setAbonnementId(Integer abonnementId) {
        this.abonnementId = abonnementId;
    }

    public String getKundeNavn() {
        return kundeNavn;
    }

    public void setKundeNavn(String kundeNavn) {
        this.kundeNavn = kundeNavn;
    }

    public String getBilNavn() {
        return bilNavn;
    }

    public void setBilNavn(String bilNavn) {
        this.bilNavn = bilNavn;
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
