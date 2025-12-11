-- INSERT script til bil-abonnement system
-- Sørg for at køre: USE bil_db; før du kører dette.

USE bil_db;

-- Indsæt eksempeldata i kunder
INSERT INTO kunder (navn, email, telefon) VALUES
                                              ('Anders Andersen', 'anders@example.com', '11111111'),
                                              ('Birgitte Biler', 'birgitte@example.com', '22222222'),
                                              ('Carl Kunde', 'carl@example.com', '33333333');

-- Indsæt eksempeldata i biler
INSERT INTO biler (navn, `år`, startsdato, slutsdato) VALUES
                                                          ('Toyota Corolla', 2020, '2024-01-01', '2024-12-31'),
                                                          ('Volkswagen Golf', 2019, '2024-03-01', '2024-09-30'),
                                                          ('Tesla Model 3', 2022, '2024-05-15', NULL);

-- Nu antager vi:
-- 1) kunder.id = 1,2,3 i samme rækkefølge som ovenfor
-- 2) biler.id = 1,2,3 i samme rækkefølge som ovenfor

-- Indsæt eksempeldata i abonnementer
INSERT INTO abonnementer (bil_id, kunde_id, startdato, slutdato, maanedlig_pris, status) VALUES
                                                                                             -- Aktivt abonnement til Anders på Toyota
                                                                                             (1, 1, '2025-01-01', NULL, 2999.00, 'AKTIV'),
                                                                                             -- Afsluttet abonnement til Birgitte på Golf
                                                                                             (2, 2, '2024-01-01', '2024-12-31', 2599.00, 'AFSLUTTET'),
                                                                                             -- Aktivt abonnement til Carl på Tesla
                                                                                             (3, 3, '2025-02-01', NULL, 3999.00, 'AKTIV');

-- Indsæt eksempeldata i skader
INSERT INTO skader (bil_id, beskrivelse, registreret_dato, pris_estimat) VALUES
                                                                             (1, 'Ridse i højre dør', '2024-06-10', 1500.00),
                                                                             (2, 'Stenslag i forrude', '2024-07-21', 2200.00),
                                                                             (3, 'P-skade på bagkofanger', '2024-09-05', 3800.00);
