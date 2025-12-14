package com.springmad.bilabonnement.model;

import java.time.LocalDate;

// Lille DTO kun til dropdown/visning af abonnementer i skade-modulet.
public class AbonnementOption {

    private Integer abonnementId;
    private String kundeNavn;
    private String bilNavn;
    private LocalDate slutdato;

    public AbonnementOption() {}

    public AbonnementOption(Integer abonnementId, String kundeNavn, String bilNavn, LocalDate slutdato) {
        this.abonnementId = abonnementId;
        this.kundeNavn = kundeNavn;
        this.bilNavn = bilNavn;
        this.slutdato = slutdato;
    }

    public Integer getAbonnementId() { return abonnementId; }
    public void setAbonnementId(Integer abonnementId) { this.abonnementId = abonnementId; }

    public String getKundeNavn() { return kundeNavn; }
    public void setKundeNavn(String kundeNavn) { this.kundeNavn = kundeNavn; }

    public String getBilNavn() { return bilNavn; }
    public void setBilNavn(String bilNavn) { this.bilNavn = bilNavn; }

    public LocalDate getSlutdato() { return slutdato; }
    public void setSlutdato(LocalDate slutdato) { this.slutdato = slutdato; }
}
