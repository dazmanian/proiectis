package restaurant.repository;

import restaurant.comenzi.Comanda;
import restaurant.comenzi.ElementComanda;
import restaurant.comenzi.StatusComanda;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Acces la date (pattern DAO) pentru comenzi si elementele lor.
 *
 * Foloseste acelasi fisier .db ca si MeniuRepository al colegului.
 * Schema este separata in tabele proprii (comanda, element_comanda),
 * fara a atinge tabelul 'produs_meniu' al colegului.
 *
 * Stocam comenzile intr-un tabel 'comanda' si elementele intr-un tabel
 * 'element_comanda' (relatie one-to-many), legat prin cheie straina.
 *
 * Salvarea comenzii (comanda + elemente) este atomica: folosim o
 * tranzactie explicita pentru a evita comenzi partiale in baza de date.
 */
public class ComandaRepository {

    private final String urlBazaDate;

    public ComandaRepository(String caleFisierDb) {
        this.urlBazaDate = "jdbc:sqlite:" + caleFisierDb;
    }

    /**
     * Creeaza tabelele necesare daca nu exista deja.
     * Apelat o singura data la pornirea aplicatiei (prin ComandaService).
     */
    public void initializeazaSchema() {
        String sqlComanda = """
            CREATE TABLE IF NOT EXISTS comanda (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                numar_masa      INTEGER NOT NULL,
                nume_client     TEXT    NOT NULL,
                status          TEXT    NOT NULL,
                data_creare     TEXT    NOT NULL,
                timp_estimat    INTEGER
            )
            """;
        String sqlElement = """
            CREATE TABLE IF NOT EXISTS element_comanda (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                comanda_id  INTEGER NOT NULL,
                produs_id   INTEGER NOT NULL,
                nume_produs TEXT    NOT NULL,
                pret_unitar REAL    NOT NULL,
                cantitate   INTEGER NOT NULL,
                FOREIGN KEY (comanda_id) REFERENCES comanda(id)
            )
            """;
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlComanda);
            stmt.execute(sqlElement);
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la initializarea schemei comenzi: " + e.getMessage(), e);
        }
    }

    /**
     * Salveaza o comanda noua impreuna cu toate elementele ei intr-o tranzactie.
     * Seteaza id-urile generate de SQLite pe obiectele Java.
     */
    public void salveazaComanda(Comanda comanda) {
        String sqlC = """
            INSERT INTO comanda (numar_masa, nume_client, status, data_creare, timp_estimat)
            VALUES (?, ?, ?, ?, ?)
            """;
        String sqlE = """
            INSERT INTO element_comanda (comanda_id, produs_id, nume_produs, pret_unitar, cantitate)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection conn = DriverManager.getConnection(urlBazaDate)) {
            conn.setAutoCommit(false);
            try {
                // 1. Insereaza randura din 'comanda'
                try (PreparedStatement ps = conn.prepareStatement(sqlC, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, comanda.getNumarMasa());
                    ps.setString(2, comanda.getNumeClient());
                    ps.setString(3, comanda.getStatus().name());
                    ps.setString(4, comanda.getDataCreare().toString());
                    if (comanda.getTimpEstimatMinute() != null) {
                        ps.setInt(5, comanda.getTimpEstimatMinute());
                    } else {
                        ps.setNull(5, Types.INTEGER);
                    }
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) comanda.setId(keys.getInt(1));
                    }
                }
                // 2. Insereaza fiecare element al comenzii
                try (PreparedStatement ps = conn.prepareStatement(sqlE, Statement.RETURN_GENERATED_KEYS)) {
                    for (ElementComanda e : comanda.getElemente()) {
                        ps.setInt(1, comanda.getId());
                        ps.setInt(2, e.getProdusId());
                        ps.setString(3, e.getNumeProdus());
                        ps.setDouble(4, e.getPretUnitar());
                        ps.setInt(5, e.getCantitate());
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) e.setId(keys.getInt(1));
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea comenzii: " + e.getMessage(), e);
        }
    }

    /**
     * Actualizeaza statusul unei comenzi existente.
     * Apelat de ComandaService la fiecare tranzitie de stare.
     */
    public void actualizeazaStatus(int comandaId, StatusComanda status) {
        String sql = "UPDATE comanda SET status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, comandaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea statusului comenzii: " + e.getMessage(), e);
        }
    }

    /**
     * Actualizeaza timpul estimat al unei comenzi.
     * Apelat de colegul de la punctul b) cand chelnerul preia comanda.
     */
    public void actualizeazaTimpEstimat(int comandaId, int timpMinute) {
        if (timpMinute < 0) throw new IllegalArgumentException("Timpul estimat nu poate fi negativ.");
        String sql = "UPDATE comanda SET timp_estimat = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, timpMinute);
            ps.setInt(2, comandaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea timpului estimat: " + e.getMessage(), e);
        }
    }

    /**
     * Returneaza toate comenzile, ordonate descrescator dupa data crearii.
     * Fiecare comanda vine cu elementele ei incarcate.
     */
    public List<Comanda> incarcaToateComenzile() {
        List<Comanda> comenzi = new ArrayList<>();
        String sql = "SELECT * FROM comanda ORDER BY data_creare DESC";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Comanda c = construiesteComanda(rs);
                incarcaElemente(conn, c);
                comenzi.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la incarcarea tuturor comenzilor: " + e.getMessage(), e);
        }
        return comenzi;
    }

    /**
     * Returneaza o comanda dupa id, cu elementele incarcate.
     * Returneaza null daca nu exista o comanda cu acel id.
     */
    public Comanda incarcaComanda(int id) {
        String sql = "SELECT * FROM comanda WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Comanda c = construiesteComanda(rs);
                    incarcaElemente(conn, c);
                    return c;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la incarcarea comenzii cu id=" + id + ": " + e.getMessage(), e);
        }
        return null;
    }

    // --- Metode private de ajutor ---

    private Comanda construiesteComanda(ResultSet rs) throws SQLException {
        Comanda c = new Comanda(rs.getInt("numar_masa"), rs.getString("nume_client"));
        c.setId(rs.getInt("id"));
        c.setStatus(StatusComanda.valueOf(rs.getString("status")));
        c.setDataCreare(LocalDateTime.parse(rs.getString("data_creare")));
        int timp = rs.getInt("timp_estimat");
        if (!rs.wasNull()) c.setTimpEstimatMinute(timp);
        return c;
    }

    /**
     * Incarca elementele comenzii folosind conexiunea deja deschisa.
     * SQLite JDBC (xerial) permite mai multe PreparedStatement-uri active
     * pe aceeasi conexiune simultan.
     */
    private void incarcaElemente(Connection conn, Comanda comanda) throws SQLException {
        String sql = "SELECT * FROM element_comanda WHERE comanda_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comanda.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ElementComanda e = new ElementComanda(
                            rs.getInt("produs_id"),
                            rs.getString("nume_produs"),
                            rs.getDouble("pret_unitar"),
                            rs.getInt("cantitate")
                    );
                    e.setId(rs.getInt("id"));
                    comanda.adaugaElement(e);
                }
            }
        }
    }
}
