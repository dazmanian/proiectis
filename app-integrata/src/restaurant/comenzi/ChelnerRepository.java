package restaurant.repository;

import restaurant.comenzi.Chelner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acces la date (pattern DAO) pentru chelneri.
 *
 * Managerul adauga personalul (punctul b), dar clasa Chelner si acest
 * repository sunt definite in modulul comenzi deoarece chelnerul este
 * actorul care confirma plata si semneaza chitanta (punctul e).
 *
 * Colegul de la punctul b) poate folosi direct acest repository pentru
 * autentificare si management personal, fara a rescrie schema.
 */
public class ChelnerRepository {

    private final String urlBazaDate;

    public ChelnerRepository(String caleFisierDb) {
        this.urlBazaDate = "jdbc:sqlite:" + caleFisierDb;
    }

    public void initializeazaSchema() {
        String sql = """
            CREATE TABLE IF NOT EXISTS chelner (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                nume     TEXT    NOT NULL,
                username TEXT    NOT NULL UNIQUE,
                parola   TEXT    NOT NULL
            )
            """;
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la initializarea schemei chelner: " + e.getMessage(), e);
        }
    }

    /** Salveaza un chelner nou si seteaza id-ul generat pe obiect. */
    public void salveazaChelner(Chelner chelner) {
        String sql = "INSERT INTO chelner (nume, username, parola) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, chelner.getNume());
            ps.setString(2, chelner.getUsername());
            ps.setString(3, chelner.getParola());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) chelner.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea chelnerului: " + e.getMessage(), e);
        }
    }

    /**
     * Cauta un chelner dupa username si parola.
     * Folosit pentru autentificare (punctul b).
     * Returneaza null daca combinatia username/parola nu exista.
     */
    public Chelner autentifica(String username, String parola) {
        String sql = "SELECT * FROM chelner WHERE username = ? AND parola = ?";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, parola);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return construiesteChelner(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la autentificarea chelnerului: " + e.getMessage(), e);
        }
        return null;
    }

    /** Incarca un chelner dupa id. Returneaza null daca nu exista. */
    public Chelner incarcaChelner(int id) {
        String sql = "SELECT * FROM chelner WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return construiesteChelner(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la incarcarea chelnerului cu id=" + id + ": " + e.getMessage(), e);
        }
        return null;
    }

    /** Returneaza toti chelnerii inregistrati. Folosit de manager (punctul f). */
    public List<Chelner> incarcaTotiChelnerii() {
        List<Chelner> chelneri = new ArrayList<>();
        String sql = "SELECT * FROM chelner ORDER BY nume";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) chelneri.add(construiesteChelner(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la incarcarea chelnerilor: " + e.getMessage(), e);
        }
        return chelneri;
    }

    private Chelner construiesteChelner(ResultSet rs) throws SQLException {
        Chelner c = new Chelner(
                rs.getString("nume"),
                rs.getString("username"),
                rs.getString("parola")
        );
        c.setId(rs.getInt("id"));
        return c;
    }
}
