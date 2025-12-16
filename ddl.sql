-- Vælg database
USE bil_db;

-- =========================
-- BILER
-- =========================
CREATE TABLE biler (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       navn VARCHAR(255) NOT NULL,
                       aar INT,
                       startsdato DATE,
                       slutsdato DATE
);

-- =========================
-- KUNDER
-- =========================
CREATE TABLE kunder (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        navn VARCHAR(255) NOT NULL,
                        email VARCHAR(255),
                        telefon VARCHAR(50)
);

-- =========================
-- BRUGERE (internt system / login)
-- =========================
CREATE TABLE brugere (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         navn VARCHAR(255) NOT NULL,
                         alder INT NOT NULL,
                         rolle VARCHAR(50) NOT NULL,
                         password VARCHAR(255) NOT NULL
);

-- =========================
-- ABONNEMENTER / LEJEAFTALER
-- =========================
CREATE TABLE abonnementer (
                              id INT AUTO_INCREMENT PRIMARY KEY,

                              bil_id INT NOT NULL,
                              kunde_id INT NOT NULL,

                              startdato DATE NOT NULL,
                              slutdato DATE,

                              maanedlig_pris DECIMAL(10,2) NOT NULL,
                              status VARCHAR(20) NOT NULL DEFAULT 'AKTIV',

                              kontrakt_type VARCHAR(20) NOT NULL DEFAULT 'LIMITED',
                              kontrakt_varighed_dage INT NOT NULL DEFAULT 150,

                              udleveringssted_type VARCHAR(20) NOT NULL DEFAULT 'BILABONNEMENT',
                              leveringsform VARCHAR(20) NOT NULL DEFAULT 'AFHENTNING',
                              leveringsadresse VARCHAR(255),

                              CONSTRAINT fk_abonnement_bil
                                  FOREIGN KEY (bil_id) REFERENCES biler(id),

                              CONSTRAINT fk_abonnement_kunde
                                  FOREIGN KEY (kunde_id) REFERENCES kunder(id)
);

-- =========================
-- SKADER
-- =========================
CREATE TABLE skader (
                        id INT AUTO_INCREMENT PRIMARY KEY,

                        abonnement_id INT NOT NULL,
                        beskrivelse VARCHAR(255) NOT NULL,
                        pris DECIMAL(10,2) NOT NULL,
                        oprettet_dato DATE NOT NULL,

                        CONSTRAINT fk_skader_abonnement
                            FOREIGN KEY (abonnement_id) REFERENCES abonnementer(id)
);
