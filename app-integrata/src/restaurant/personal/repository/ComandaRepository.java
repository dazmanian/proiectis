package restaurant.personal.repository;

import restaurant.personal.model.Comanda;
import restaurant.personal.model.StatusComanda;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComandaRepository {

    private final String urlDb;
    private Connection conexiuneTest;

    public ComandaRepository(String urlDb) {
        this.urlDb = urlDb;
        creeazaTabel();
    }

    public ComandaRepository(Connection conexiune) {
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
            CREATE TABLE IF NOT EXISTS comenzi (
                id                   INTEGER PRIMARY KEY AUTOINCREMENT,
                id_produse           TEXT    NOT NULL,
                status               TEXT    NOT NULL DEFAULT 'IN_ASTEPTARE',
                timp_estimat_minute  INTEGER NOT NULL DEFAULT 0,
                moment_plasat        TEXT    NOT NULL,
                numar_masa           TEXT    NOT NULL
            )
            """;
        try {
            Connection con = getConexiune();
            Statement stmt = con.createStatement();
            stmt.execute(sql);
            stmt.close();
            if (!esteConexiuneExterna()) con.close();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la crearea tabelului 'comenzi': " + e.getMessage(), e);
        }
    }

    public void salveaza(Comanda comanda) {
        String sql = """
            INSERT INTO comenzi (id_produse, status, timp_estimat_minute, moment_plasat, numar_masa)
            VALUES (?, ?, ?, ?, ?)
            """;
        try {
            Connection con = getConexiune();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, serializeListaIduri(comanda.getIdProduse()));
            ps.setString(2, comanda.getStatus().name());
            ps.setInt(3, comanda.getTimpEstimatMinute());
            ps.setString(4, comanda.getMomentPlasat().toString());
            ps.setString(5, comanda.getNumarMasa());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) comanda.setId(rs.getInt(1));
            rs.close(); ps.close();
            if (!esteConexiuneExterna()) con.close();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea comenzii: " + e.getMessage(), e);
        }
    }

    public void actualizeaza(Comanda comanda) {
        String sql = """
            UPDATE comenzi
            SET status = ?, timp_estimat_minute = ?
            WHERE id = ?
            """;
        try {
            Connection con = getConexiune();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, comanda.getStatus().name());
            ps.setInt(2, comanda.getTimpEstimatMinute());
            ps.setInt(3, comanda.getId());
            ps.executeUpdate();
            ps.close();
            if (!esteConexiuneExterna()) con.close();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea comenzii: " + e.getMessage(), e);
        }
    }

    public List<Comanda> getComenziNefinalizate() {
        String sql = "SELECT * FROM comenzi WHERE status != 'SERVITA' ORDER BY moment_plasat ASC";
        return executaQuery(sql);
    }

    public List<Comanda> getToateComenzi() {
        String sql = "SELECT * FROM comenzi ORDER BY moment_plasat ASC";
        return executaQuery(sql);
    }

    public Comanda gasesteDupaId(int id) {
        String sql = "SELECT * FROM comenzi WHERE id = ?";
        try {
            Connection con = getConexiune();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Comanda rezultat = null;
            if (rs.next()) rezultat = construiesteComanda(rs);
            rs.close(); ps.close();
            if (!esteConexiuneExterna()) con.close();
            return rezultat;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la cautarea comenzii: " + e.getMessage(), e);
        }
    }

    private List<Comanda> executaQuery(String sql) {
        List<Comanda> rezultat = new ArrayList<>();
        try {
            Connection con = getConexiune();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) rezultat.add(construiesteComanda(rs));
            rs.close(); stmt.close();
            if (!esteConexiuneExterna()) con.close();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea comenzilor: " + e.getMessage(), e);
        }
        return rezultat;
    }

    private Comanda construiesteComanda(ResultSet rs) throws SQLException {
        return new Comanda(
            rs.getInt("id"),
            deserializeListaIduri(rs.getString("id_produse")),
            StatusComanda.valueOf(rs.getString("status")),
            rs.getInt("timp_estimat_minute"),
            LocalDateTime.parse(rs.getString("moment_plasat")),
            rs.getString("numar_masa")
        );
    }

    private String serializeListaIduri(List<Integer> iduri) {
        return iduri.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private List<Integer> deserializeListaIduri(String sir) {
        if (sir == null || sir.isBlank()) return new ArrayList<>();
        return Arrays.stream(sir.split(","))
                     .map(String::trim)
                     .map(Integer::parseInt)
                     .collect(Collectors.toList());
    }
}