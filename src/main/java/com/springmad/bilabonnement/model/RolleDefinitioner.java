package com.springmad.bilabonnement.model;

// ===== Singleton Pattern =====
// Singleton er et designmoenstre der sikrer at der kun er EEN instans af en klasse
// i hele programmet. Man kan tilgaa den samme instans fra forskellige steder.
//
// Hvorfor Singleton her?
// Roller og statusser bruges i mange controllers (DataregistreringController,
// SkadeController, ForretningController). Uden Singleton kunne man komme til
// at skrive "DATAREGISTRERING" forkert eet sted, og saa virker rolle-checket ikke.
// Med Singleton har vi EEN kilde til sandhed for alle roller og statusser.
//
// Hvordan sikrer vi at der kun er een instans?
// 1. Privat konstruktoer: ingen kan skrive "new RolleDefinitioner()" udefra
// 2. Statisk felt (instance): holder den ene instans i hukommelsen
// 3. getInstance(): den eneste maade at faa fat i instansen paa
//
// Privat konstruktoer + statisk metode = standard maade at implementere Singleton.
public class RolleDefinitioner {

    // Det statiske felt der holder den ene instans.
    // static betyder at feltet tilhoerer klassen, ikke et objekt.
    // Der er kun eet static felt, uanset hvor mange gange man kalder getInstance().
    private static RolleDefinitioner instance;

    // ===== Roller =====
    // Disse felter definerer de tre roller i systemet.
    // De er IKKE static, fordi de tilhoerer instansen (Singleton-objektet).
    // Man tilgaar dem via getInstance().getRolleDataregistrering() osv.
    private final String rolleDataregistrering = "DATAREGISTRERING";
    private final String rolleSkadeOgUdbedring = "SKADE_OG_UDBEDRING";
    private final String rolleForretning = "FORRETNING";

    // ===== Abonnement-statusser =====
    private final String statusAktiv = "AKTIV";
    private final String statusAfsluttet = "AFSLUTTET";

    // ===== Kontrakt-typer =====
    private final String kontraktLimited = "LIMITED";
    private final String kontraktUnlimited = "UNLIMITED";

    // Privat konstruktoer: sikrer at INGEN kan oprette instanser udefra.
    // Hvis nogen skriver "new RolleDefinitioner()" faar de en kompileringsfejl.
    // Det er kernen i Singleton: kun klassen selv kan oprette sin instans.
    private RolleDefinitioner() {
    }

    // getInstance() er den ENESTE maade at faa fat i instansen paa.
    // Foerste gang den kaldes, oprettes instansen (lazy initialization).
    // Alle efterfoelgende kald returnerer den SAMME instans.
    //
    // Eksempel:
    //   RolleDefinitioner r1 = RolleDefinitioner.getInstance();
    //   RolleDefinitioner r2 = RolleDefinitioner.getInstance();
    //   // r1 og r2 er SAMME objekt (r1 == r2 er true)
    public static RolleDefinitioner getInstance() {
        if (instance == null) {
            // Foerste gang: opret den ene instans
            instance = new RolleDefinitioner();
        }
        // Returnerer altid den samme instans
        return instance;
    }

    // Getters til roller
    public String getRolleDataregistrering() {
        return rolleDataregistrering;
    }

    public String getRolleSkadeOgUdbedring() {
        return rolleSkadeOgUdbedring;
    }

    public String getRolleForretning() {
        return rolleForretning;
    }

    // Getters til statusser
    public String getStatusAktiv() {
        return statusAktiv;
    }

    public String getStatusAfsluttet() {
        return statusAfsluttet;
    }

    // Getters til kontrakt-typer
    public String getKontraktLimited() {
        return kontraktLimited;
    }

    public String getKontraktUnlimited() {
        return kontraktUnlimited;
    }
}
