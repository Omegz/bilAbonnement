package com.springmad.bilabonnement.service;

import com.springmad.bilabonnement.repository.AbonnementJdbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Testklasse til AbonnementService.
// Viser både happy flow og exception flows.
@ExtendWith(MockitoExtension.class)
class AbonnementServiceTest {

    @Mock
    private AbonnementJdbcRepository abonnementJdbcRepository;

    @InjectMocks
    private AbonnementService abonnementService;

    @Test
    void opretAbonnementHvisMuligt_happyFlow() {
        // Arrange
        String kundeNavn = "Anders Andersen";
        int bilId = 1;
        LocalDate startdato = LocalDate.of(2025, 1, 1);
        LocalDate slutdato = null;
        BigDecimal pris = new BigDecimal("2999.00");

        // Kunden har IKKE et aktivt abonnement
        when(abonnementJdbcRepository.harAktivtAbonnementForKundeNavn(kundeNavn))
                .thenReturn(false);

        // Act
        abonnementService.opretAbonnementHvisMuligt(
                kundeNavn, bilId, startdato, slutdato, pris
        );

        // Assert (happy flow: repository.opretAbonnement bliver kaldt præcis én gang)
        verify(abonnementJdbcRepository, times(1))
                .opretAbonnement(kundeNavn, bilId, startdato, slutdato, pris);
    }

    @Test
    void opretAbonnementHvisMuligt_exceptionFlow_kundenHarAlleredeAktivtAbonnement() {
        // Arrange
        String kundeNavn = "Anders Andersen";
        int bilId = 1;
        LocalDate startdato = LocalDate.of(2025, 1, 1);
        BigDecimal pris = new BigDecimal("2999.00");

        // Kunden HAR et aktivt abonnement → skal give IllegalStateException
        when(abonnementJdbcRepository.harAktivtAbonnementForKundeNavn(kundeNavn))
                .thenReturn(true);

        // Act + Assert
        assertThrows(IllegalStateException.class, () ->
                abonnementService.opretAbonnementHvisMuligt(
                        kundeNavn, bilId, startdato, null, pris
                )
        );

        // Repository må IKKE forsøge at oprette et nyt abonnement
        verify(abonnementJdbcRepository, never())
                .opretAbonnement(anyString(), anyInt(), any(), any(), any());
    }

    @Test
    void opretAbonnementHvisMuligt_exceptionFlow_ugyldigtKundenavn() {
        // Arrange
        String tomtNavn = "   ";
        int bilId = 1;
        LocalDate startdato = LocalDate.of(2025, 1, 1);
        BigDecimal pris = new BigDecimal("2999.00");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () ->
                abonnementService.opretAbonnementHvisMuligt(
                        tomtNavn, bilId, startdato, null, pris
                )
        );

        // Ingen kald til repository ved ugyldigt input
        verifyNoInteractions(abonnementJdbcRepository);
    }
}
