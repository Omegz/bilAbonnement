-- DDL script til bil-abonnement system
-- Opretter database og alle tabeller.

-- Opret database (kun hvis den ikke findes)
CREATE DATABASE IF NOT EXISTS bil_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Brug databasen
USE bil_db;

-- Tabel: biler
-- Indeholder information om de biler, der kan indgå i abonnementer.
CREATE TABLE IF NOT EXISTS biler (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     navn VARCHAR(255) NOT NULL,
    `år` INT,
    startsdato DATE,
    slutsdato DATE
    );

-- Tabel: kunder
-- Indeholder information om kunder, der kan have abonnementer.
CREATE TABLE IF NOT EXISTS kunder (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      navn VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    telefon VARCHAR(50)
    );

-- Tabel: abonnementer
-- Binder biler og kunder sammen og repræsenterer selve aftalen.
CREATE TABLE IF NOT EXISTS abonnementer (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            bil_id INT NOT NULL,
                                            kunde_id INT NOT NULL,
                                            startdato DATE NOT NULL,
                                            slutdato DATE,
                                            maanedlig_pris DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT 'AKTIV',

    CONSTRAINT fk_abonnement_bil
    FOREIGN KEY (bil_id) REFERENCES biler(id),
    CONSTRAINT fk_abonnement_kunde
    FOREIGN KEY (kunde_id) REFERENCES kunder(id)
    );

-- Tabel: skader
-- Registrerer skader på biler.
CREATE TABLE IF NOT EXISTS skader (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      bil_id INT NOT NULL,
                                      beskrivelse TEXT NOT NULL,
                                      registreret_dato DATE NOT NULL,
                                      pris_estimat DECIMAL(10,2),

    CONSTRAINT fk_skade_bil
    FOREIGN KEY (bil_id) REFERENCES biler(id)
    );
