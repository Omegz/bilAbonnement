USE bil_db;

-- =========================
-- BILER
-- =========================
INSERT INTO biler (navn, aar, startsdato, slutsdato) VALUES
                                                         ('VW Golf', 2022, '2024-01-01', NULL),
                                                         ('Tesla Model 3', 2023, '2024-02-01', NULL),
                                                         ('BMW i4', 2021, '2023-11-15', NULL),
                                                         ('Audi A4', 2020, '2023-10-01', NULL);

-- =========================
-- KUNDER
-- =========================
INSERT INTO kunder (navn, email, telefon) VALUES
                                              ('Anders Jensen', 'anders@mail.dk', '20112233'),
                                              ('Maria Hansen', 'maria@mail.dk', '22334455'),
                                              ('Peter Nielsen', 'peter@mail.dk', '44556677');

-- =========================
-- BRUGERE (interne roller)
-- =========================
INSERT INTO brugere (navn, alder, rolle, password) VALUES
                                                       ('data1', 28, 'DATAREGISTRERING', '1234'),
                                                       ('skade1', 35, 'SKADE_OG_UDBEDRING', '1234'),
                                                       ('forretning1', 42, 'FORRETNING', '1234');

-- =========================
-- ABONNEMENTER / LEJEAFTALER
-- =========================
-- Aktiv LIMITED kontrakt
INSERT INTO abonnementer (
    bil_id, kunde_id, startdato, slutdato,
    maanedlig_pris, status,
    kontrakt_type, kontrakt_varighed_dage,
    udleveringssted_type, leveringsform, leveringsadresse
) VALUES (
             1, 1, '2024-10-01', '2025-03-01',
             3499.00, 'AKTIV',
             'LIMITED', 150,
             'BILABONNEMENT', 'AFHENTNING', NULL
         );

-- Aktiv UNLIMITED kontrakt
INSERT INTO abonnementer (
    bil_id, kunde_id, startdato, slutdato,
    maanedlig_pris, status,
    kontrakt_type, kontrakt_varighed_dage,
    udleveringssted_type, leveringsform, leveringsadresse
) VALUES (
             2, 2, '2024-06-01', '2025-06-01',
             4299.00, 'AKTIV',
             'UNLIMITED', 365,
             'FDM', 'LEVERING', 'Roskildevej 12, 4000 Roskilde'
         );

-- Afsluttet kontrakt (bruges til skader)
INSERT INTO abonnementer (
    bil_id, kunde_id, startdato, slutdato,
    maanedlig_pris, status,
    kontrakt_type, kontrakt_varighed_dage,
    udleveringssted_type, leveringsform, leveringsadresse
) VALUES (
             3, 3, '2024-01-01', '2024-09-01',
             3999.00, 'AFSLUTTET',
             'LIMITED', 240,
             'DS', 'AFHENTNING', NULL
         );

-- =========================
-- SKADER (kun paa afsluttede abonnementer)
-- =========================
INSERT INTO skader (abonnement_id, beskrivelse, pris, oprettet_dato) VALUES
                                                                         (3, 'Ridse i venstre doer', 2500.00, CURDATE()),
                                                                         (3, 'Slidte daek', 1800.00, CURDATE());
