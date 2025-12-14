package com.springmad.bilabonnement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// Formular-model til oprettelse af lejeaftale (abonnement) i Dataregistrering-rollen.
public class LejeaftaleForm {

    // Vælg kunde fra dropdown (bedre end navn)
    private Integer kundeId;

    // Vælg bil fra DB
    private Long bilId;

    private LocalDate startdato;
    private LocalDate slutdato;

    private BigDecimal maanedligPris;

    // LIMITED / UNLIMITED
    private String kontraktType;

    // Dage (bruges mest til Unlimited i MVP)
    private Integer kontraktVarighedDage;

    // BILABONNEMENT / FDM / DS
    private String udleveringsstedType;

    // AFHENTNING / LEVERING
    private String leveringsform;

    // Kun relevant ved LEVERING
    private String leveringsadresse;

    // “Svag re-login” ved submit (kun til demo)
    private String medarbejderNavn;
    private String medarbejderPassword;

    public LejeaftaleForm() {}

    public Integer getKundeId() { return kundeId; }
    public void setKundeId(Integer kundeId) { this.kundeId = kundeId; }

    public Long getBilId() { return bilId; }
    public void setBilId(Long bilId) { this.bilId = bilId; }

    public LocalDate getStartdato() { return startdato; }
    public void setStartdato(LocalDate startdato) { this.startdato = startdato; }

    public LocalDate getSlutdato() { return slutdato; }
    public void setSlutdato(LocalDate slutsdato) { this.slutdato = slutsdato; }

    public BigDecimal getMaanedligPris() { return maanedligPris; }
    public void setMaanedligPris(BigDecimal maanedligPris) { this.maanedligPris = maanedligPris; }

    public String getKontraktType() { return kontraktType; }
    public void setKontraktType(String kontraktType) { this.kontraktType = kontraktType; }

    public Integer getKontraktVarighedDage() { return kontraktVarighedDage; }
    public void setKontraktVarighedDage(Integer kontraktVarighedDage) { this.kontraktVarighedDage = kontraktVarighedDage; }

    public String getUdleveringsstedType() { return udleveringsstedType; }
    public void setUdleveringsstedType(String udleveringsstedType) { this.udleveringsstedType = udleveringsstedType; }

    public String getLeveringsform() { return leveringsform; }
    public void setLeveringsform(String leveringsform) { this.leveringsform = leveringsform; }

    public String getLeveringsadresse() { return leveringsadresse; }
    public void setLeveringsadresse(String leveringsadresse) { this.leveringsadresse = leveringsadresse; }

    public String getMedarbejderNavn() { return medarbejderNavn; }
    public void setMedarbejderNavn(String medarbejderNavn) { this.medarbejderNavn = medarbejderNavn; }

    public String getMedarbejderPassword() { return medarbejderPassword; }
    public void setMedarbejderPassword(String medarbejderPassword) { this.medarbejderPassword = medarbejderPassword; }


}
