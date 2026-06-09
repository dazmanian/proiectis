package restaurant.repository;

import restaurant.comenzi.*;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Acces la date (pattern DAO) pentru chitante.
 *
 * Numarul de chitanta (numar_chitanta) este distinct de id-ul intern SQLite:
 *   - id: generat automat de SQLite, folosit intern
 *   - numar_chitanta: generat secvential (MAX + 1), vizibil pe documentul fizic
 *
 * Generarea numarului si INSERT-ul se fac in aceeasi tranzactie pentru a evita
 * duplicate in scenarii de acces concurent. Pentru un restaurant cu volum mic
 * (scopul acestui proiect) aceasta abordare este suficienta.
 */
public class ChitantaRepository {

    private final String urlBazaDate;

    public ChitantaRepository(String caleFisierDb) {
        this.urlBazaDate = "jdbc:sqlite:" + caleFisierDb;
    }

    public void initializeazaSchema() {
        String sql = """
            CREATE TABLE IF NOT EXISTS chitanta (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                numar_chitanta  INTEGER NOT NULL UNIQUE,
                comanda_id      INTEGER NOT NULL UNIQUE,
                chelner_id      INTEGER NOT NULL,
                metoda_plata    TEXT    NOT NULL,
                total_plata     REAL    NOT NULL,
                data_emitere    TEXT    NOT NULL,
                FOREIGN KEY (comanda_id) REFERENCES comanda(id),
                FOREIGN KEY (chelner_id) REFERENCES chelner(id)
            )
            """;
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la initializarea schemei chitanta: " + e.getMessage(), e);
        }
    }

    /**
     * Salveaza chitanta in baza de date.
     * Genereaza si seteaza numarChitanta pe obiect inainte de INSERT.
     * Operatia este atomica (tranzactie).
     */
    public void salveazaChitanta(Chitanta chitanta) {
        String sqlNr  = "SELECT COALESCE(MAX(numar_chitanta), 0) + 1 FROM chitanta";
        String sqlIns = """
            INSERT INTO chitanta
              (numar_chitanta, comanda_id, chelner_id, metoda_plata, total_plata, data_emitere)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DriverManager.getConnection(urlBazaDate)) {
            conn.setAutoCommit(false);
            try {
                // Genereaza urmatorul numar de chitanta in cadrul tranzactiei
                int numar;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlNr)) {
                    numar = rs.next() ? rs.getInt(1) : 1;
                }
                chitanta.setNumarChitanta(numar);

                // Insereaza chitanta
                try (PreparedStatement ps = conn.prepareStatement(sqlIns, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, chitanta.getNumarChitanta());
                    ps.setInt(2, chitanta.getComanda().getId());
                    ps.setInt(3, chitanta.getChelner().getId());
                    ps.setString(4, chitanta.getMetodaPlata().name());
                    ps.setDouble(5, chitanta.getTotalPlata());
                    ps.setString(6, chitanta.getDataEmitere().toString());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) chitanta.setId(keys.getInt(1));
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea chitantei: " + e.getMessage(), e);
        }
    }

    /**
     * Incarca chitanta asociata unei comenzi.
     *
     * @param comandaId  id-ul comenzii
     * @param comanda    obiectul Comanda deja incarcat (cu elemente)
     * @param chelner    obiectul Chelner deja incarcat
     * @return chitanta sau null daca comanda nu a fost inca platita
     */
    public Chitanta incarcaChitantaDupaComanda(int comandaId, Comanda comanda, Chelner chelner) {
        String sql = "SELECT * FROM chitanta WHERE comanda_id = ?";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comandaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Folosim constructorul de reconstructie pentru a prelua totalul din DB
                    Chitanta ch = new Chitanta(
                            rs.getInt("numar_chitanta"),
                            comanda,
                            chelner,
                            MetodaPlata.valueOf(rs.getString("metoda_plata")),
                            rs.getDouble("total_plata"),
                            LocalDateTime.parse(rs.getString("data_emitere"))
                    );
                    ch.setId(rs.getInt("id"));
                    return ch;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la incarcarea chitantei: " + e.getMessage(), e);
        }
        return null;
    }

    /** Numara chitantele existente. Util pentru teste. */
    public int numaraChitante() {
        String sql = "SELECT COUNT(*) FROM chitanta";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la numararea chitantelor: " + e.getMessage(), e);
        }
    }
}
