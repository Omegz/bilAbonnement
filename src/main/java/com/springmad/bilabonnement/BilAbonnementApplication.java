
package com.springmad.bilabonnement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @SpringBootApplication aktiverer Spring Boot og sørger for
// at projektet starter som en selvstændig applikation
public class BilAbonnementApplication {

    public static void main(String[] args) {
        // Starter hele Spring Boot-applikationen
        SpringApplication.run(BilAbonnementApplication.class, args);
    }

}
