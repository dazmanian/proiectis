package restaurant.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import restaurant.manager.model.Manager;
import restaurant.manager.service.ManagerService;
import restaurant.model.Aperitiv;
import restaurant.model.Meniu;
import restaurant.model.ProdusMeniu;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste unitare pentru modulul Manager (punctul f).
 *
 * Verifica:
 *   - autentificarea cu credentialele din enunt;
 *   - respingerea credentialelor gresite;
 *   - adaugarea/modificarea/disponibilitatea produselor doar dupa autentificare;
 *   - integrarea corecta cu clasele Meniu si ProdusMeniu (punctul a).
 *
 * Testele ruleaza doar in memorie (MeniuRepository = null), deci nu ating
 * baza de date.
 */
class ManagerServiceTest {

    private ManagerService service;
    private Meniu meniu;

    @BeforeEach
    void setUp() {
        Manager manager = new Manager(); // credentialele implicite din enunt
        meniu = new Meniu();
        service = new ManagerService(manager, meniu, null); // null => fara persistenta
    }

    // ---------- Autentificare ----------

    @Test
    @DisplayName("Autentificarea reuseste cu credentialele corecte din enunt")
    void testAutentificareCorecta() {
        boolean ok = service.autentifica("admin@restaurant.null", "adminRestaurantMagic12");
        assertTrue(ok);
        assertTrue(service.esteAutentificat());
    }

    @Test
    @DisplayName("Autentificarea esueaza cu parola gresita")
    void testAutentificareParolaGresita() {
        boolean ok = service.autentifica("admin@restaurant.null", "parolaGresita");
        assertFalse(ok);
        assertFalse(service.esteAutentificat());
    }

    @Test
    @DisplayName("Autentificarea esueaza cu email gresit")
    void testAutentificareEmailGresit() {
        assertFalse(service.autentifica("altcineva@restaurant.null", "adminRestaurantMagic12"));
    }

    @Test
    @DisplayName("Deconectarea reseteaza starea de autentificare")
    void testDeconectare() {
        service.autentifica("admin@restaurant.null", "adminRestaurantMagic12");
        service.deconecteaza();
        assertFalse(service.esteAutentificat());
    }

    // ---------- Operatii fara autentificare => exceptie ----------

    @Test
    @DisplayName("Adaugarea unui produs fara autentificare arunca exceptie")
    void testAdaugaFaraAutentificare() {
        Aperitiv ap = new Aperitiv("Test", 10.0, false, true);
        assertThrows(IllegalStateException.class, () -> service.adaugaProdusInMeniu(ap));
    }

    @Test
    @DisplayName("Modificarea fara autentificare arunca exceptie")
    void testModificaFaraAutentificare() {
        assertThrows(IllegalStateException.class,
                () -> service.modificaProdus("X", "Y", 5.0));
    }

    // ---------- Operatii dupa autentificare ----------

    @Test
    @DisplayName("Managerul autentificat poate adauga un produs in meniu")
    void testAdaugaProdus() {
        service.autentifica("admin@restaurant.null", "adminRestaurantMagic12");
        Aperitiv ap = new Aperitiv("Bruschete noi", 20.0, false, true);
        service.adaugaProdusInMeniu(ap);
        assertEquals(1, meniu.getNumarProduse());
        assertNotNull(service.gasesteProdusDupaNume("Bruschete noi"));
    }

    @Test
    @DisplayName("Managerul poate modifica numele si pretul unui produs")
    void testModificaProdus() {
        service.autentifica("admin@restaurant.null", "adminRestaurantMagic12");
        service.adaugaProdusInMeniu(new Aperitiv("Vechi", 10.0, false, true));

        boolean modificat = service.modificaProdus("Vechi", "Nou", 15.5);
        assertTrue(modificat);

        ProdusMeniu p = service.gasesteProdusDupaNume("Nou");
        assertNotNull(p);
        assertEquals(15.5, p.getPret());
        assertNull(service.gasesteProdusDupaNume("Vechi"));
    }

    @Test
    @DisplayName("Modificarea unui produs inexistent returneaza false")
    void testModificaProdusInexistent() {
        service.autentifica("admin@restaurant.null", "adminRestaurantMagic12");
        assertFalse(service.modificaProdus("NuExista", "X", 5.0));
    }

    @Test
    @DisplayName("Managerul poate schimba disponibilitatea unui produs")
    void testSeteazaDisponibilitate() {
        service.autentifica("admin@restaurant.null", "adminRestaurantMagic12");
        service.adaugaProdusInMeniu(new Aperitiv("Produs", 10.0, false, true));

        assertTrue(service.seteazaDisponibilitate("Produs", false));
        assertFalse(service.gasesteProdusDupaNume("Produs").isDisponibil());

        assertTrue(service.seteazaDisponibilitate("Produs", true));
        assertTrue(service.gasesteProdusDupaNume("Produs").isDisponibil());
    }

    // ---------- Model Manager ----------

    @Test
    @DisplayName("Managerul implicit foloseste credentialele exacte din enunt")
    void testCredentialeImplicite() {
        Manager m = new Manager();
        assertEquals("admin@restaurant.null", m.getEmail());
        assertTrue(m.verificaCredentiale("admin@restaurant.null", "adminRestaurantMagic12"));
    }
}
