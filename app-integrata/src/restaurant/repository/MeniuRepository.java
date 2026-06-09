package restaurant.repository;

import restaurant.model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Acces la date (pattern DAO) pentru meniu, folosind SQLite.
 *
 * Aceasta clasa izoleaza persistenta de modelul de domeniu: clasele din
 * pachetul 'model' nu stiu nimic despre baza de date. Astfel, colegii pot
 * folosi modelul fara a depinde de SQLite, iar daca echipa schimba stocarea
 * se modifica doar aceasta clasa.
 *
 * Stocam toate produsele intr-un singur tabel cu o coloana 'categorie' care
 * discrimineaza tipul (strategie single-table). Coloanele specifice unui
 * anumit tip (grad_alcool, volum_ml, tip_fel etc.) pot fi NULL pentru
 * produsele care nu le folosesc.
 *
 * Necesita driverul JDBC pentru SQLite (org.xerial:sqlite-jdbc).
 */
public class MeniuRepository {

    private final String urlBazaDate;

    public MeniuRepository(String caleFisierDb) {
        this.urlBazaDate = "jdbc:sqlite:" + caleFisierDb;
    }

    /**
     * Creeaza tabelul daca nu exista deja.
     */
    public void initializeazaSchema() {
        String sql = """
            CREATE TABLE IF NOT EXISTS produs_meniu (
                id           INTEGER PRIMARY KEY AUTOINCREMENT,
                nume         TEXT    NOT NULL,
                pret         REAL    NOT NULL,
                categorie    TEXT    NOT NULL,
                disponibil   INTEGER NOT NULL DEFAULT 1,
                ingrediente  TEXT,
                picant       INTEGER,
                vegetarian   INTEGER,
                tip_fel      TEXT,
                volum_ml     INTEGER,
                grad_alcool  REAL,
                carbogazoasa INTEGER
            )
            """;
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la initializarea schemei: " + e.getMessage(), e);
        }
    }

    /**
     * Salveaza in baza de date toate produsele dintr-un meniu.
     */
    public void salveazaMeniu(Meniu meniu) {
        String sql = """
            INSERT INTO produs_meniu
            (nume, pret, categorie, disponibil, ingrediente, picant,
             vegetarian, tip_fel, volum_ml, grad_alcool, carbogazoasa)
            VALUES (?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (ProdusMeniu p : meniu.getToateProdusele()) {
                ps.setString(1, p.getNume());
                ps.setDouble(2, p.getPret());
                ps.setString(3, p.getCategorie().name());
                ps.setInt(4, p.isDisponibil() ? 1 : 0);
                ps.setString(5, String.join(";", p.getIngrediente()));

                if (p instanceof Mancare m) {
                    ps.setInt(6, m.isPicant() ? 1 : 0);
                    ps.setInt(7, m.isVegetarian() ? 1 : 0);
                } else {
                    ps.setNull(6, java.sql.Types.INTEGER);
                    ps.setNull(7, java.sql.Types.INTEGER);
                }

                if (p instanceof FelPrincipal fp) {
                    ps.setString(8, fp.getTip().name());
                } else {
                    ps.setNull(8, java.sql.Types.VARCHAR);
                }

                if (p instanceof Bautura b) {
                    ps.setInt(9, b.getVolumMl());
                } else {
                    ps.setNull(9, java.sql.Types.INTEGER);
                }

                if (p instanceof BauturaSpirtoasa bs) {
                    ps.setDouble(10, bs.getGradAlcool());
                } else {
                    ps.setNull(10, java.sql.Types.REAL);
                }

                if (p instanceof BauturaNespirtoasa bn) {
                    ps.setInt(11, bn.isCarbogazoasa() ? 1 : 0);
                } else {
                    ps.setNull(11, java.sql.Types.INTEGER);
                }

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setId(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvarea meniului: " + e.getMessage(), e);
        }
    }

    /**
     * Incarca toate produsele din baza de date si reconstruieste obiectele
     * din ierarhia de clase, pe baza coloanei 'categorie'.
     */
    public Meniu incarcaMeniu() {
        Meniu meniu = new Meniu();
        String sql = "SELECT * FROM produs_meniu";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CategorieMeniu categorie = CategorieMeniu.valueOf(rs.getString("categorie"));
                ProdusMeniu produs = construiesteProdus(rs, categorie);
                produs.setId(rs.getInt("id"));
                produs.setDisponibil(rs.getInt("disponibil") == 1);

                String ingr = rs.getString("ingrediente");
                if (ingr != null && !ingr.isBlank()) {
                    for (String i : ingr.split(";")) {
                        produs.adaugaIngredient(i);
                    }
                }
                meniu.adaugaProdus(produs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la incarcarea meniului: " + e.getMessage(), e);
        }
        return meniu;
    }

    private ProdusMeniu construiesteProdus(ResultSet rs, CategorieMeniu categorie) throws SQLException {
        String nume = rs.getString("nume");
        double pret = rs.getDouble("pret");

        return switch (categorie) {
            case APERITIVE -> new Aperitiv(nume, pret,
                    rs.getInt("picant") == 1, rs.getInt("vegetarian") == 1);
            case FELURI_PRINCIPALE -> new FelPrincipal(nume, pret,
                    rs.getInt("picant") == 1, rs.getInt("vegetarian") == 1,
                    TipFelPrincipal.valueOf(rs.getString("tip_fel")));
            case BAUTURI_SPIRTOASE -> new BauturaSpirtoasa(nume, pret,
                    rs.getInt("volum_ml"), rs.getDouble("grad_alcool"));
            case BAUTURI_NESPIRTOASE -> new BauturaNespirtoasa(nume, pret,
                    rs.getInt("volum_ml"), rs.getInt("carbogazoasa") == 1);
        };
    }

    /**
     * Numara produsele existente in baza de date.
     */
    public int numaraProduse() {
        String sql = "SELECT COUNT(*) FROM produs_meniu";
        try (Connection conn = DriverManager.getConnection(urlBazaDate);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la numararea produselor: " + e.getMessage(), e);
        }
    }
}
