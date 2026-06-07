package restaurant.personal.repository;

import restaurant.personal.model.Personal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalRepository {

    private final String urlDb;
    private Connection conexiuneTest;

    public PersonalRepository(String urlDb) {
        this.urlDb = urlDb;
        creeazaTabel();
    }

    public PersonalRepository(Connection conexiune) {
        this.urlDb = null;
        this.conexiuneTest = conexiune;
        creeazaTabel();
    }

    private Connection getConexiune() throws SQLException {
        if (conexiuneTest != null) return conexiuneTest;
        return DriverManager.getConnection(urlDb);
    }

    private boolean esteConexiuneExterna() {
        return conexiuneTest != null;
    }

    private void creeazaTabel() {
        String sql = """
            CREATE TABLE IF NOT EXISTS personal (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                username      TEXT    NOT NULL UNIQUE,
                parola        TEXT    NOT NULL,
                nume_complet  TEXT    NOT NULL DEFAULT ''
            )
            """;
        try {
            Connection con = getConexiune();
            Statement stmt = con.createStatement();
            stmt.execute(sql);
            stmt.close();
            if (!esteConexiuneExterna()) con.close();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la crearea tabelului 'personal': " + e.getMessage(), e);
        }
    }

    public void adauga(Personal angajat) {
        String sql = "INSERT INTO personal (username, parola, nume_complet) VALUES (?, ?, ?)";
        try {
            Connection con = getConexiune();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, angajat.getUsername());
            ps.setString(2, angajat.getParola());
            ps.setString(3, angajat.getNumeComplet());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) angajat.setId(rs.getInt(1));
            rs.close(); ps.close();
            if (!esteConexiuneExterna()) con.close();
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                throw new IllegalArgumentException("Username-ul '" + angajat.getUsername() + "' este deja folosit.");
            }
            throw new RuntimeException("Eroare la adaugarea angajatului: " + e.getMessage(), e);
        }
    }

    public boolean sterge(String username) {
        String sql = "DELETE FROM personal WHERE username = ?";
        try {
            Connection con = getConexiune();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            int rows = ps.executeUpdate();
            ps.close();
            if (!esteConexiuneExterna()) con.close();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la stergerea angajatului: " + e.getMessage(), e);
        }
    }

    public Personal gasesteDupaUsername(String username) {
        String sql = "SELECT * FROM personal WHERE username = ?";
        try {
            Connection con = getConexiune();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            Personal rezultat = null;
            if (rs.next()) rezultat = construiestePersonal(rs);
            rs.close(); ps.close();
            if (!esteConexiuneExterna()) con.close();
            return rezultat;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea angajatului: " + e.getMessage(), e);
        }
    }

    public List<Personal> getTotiAngajatii() {
        String sql = "SELECT * FROM personal ORDER BY username ASC";
        List<Personal> rezultat = new ArrayList<>();
        try {
            Connection con = getConexiune();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) rezultat.add(construiestePersonal(rs));
            rs.close(); stmt.close();
            if (!esteConexiuneExterna()) con.close();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea personalului: " + e.getMessage(), e);
        }
        return rezultat;
    }

    private Personal construiestePersonal(ResultSet rs) throws SQLException {
        return new Personal(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("parola"),
            rs.getString("nume_complet")
        );
    }
}