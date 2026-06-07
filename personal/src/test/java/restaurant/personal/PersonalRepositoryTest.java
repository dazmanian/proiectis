package restaurant.personal;

import org.junit.jupiter.api.*;
import restaurant.personal.model.Personal;
import restaurant.personal.repository.PersonalRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste PersonalRepository")
class PersonalRepositoryTest {

    private Connection conexiune;
    private PersonalRepository repo;

    @BeforeEach
    void setup() throws Exception {
        conexiune = DriverManager.getConnection("jdbc:sqlite::memory:");
        repo = new PersonalRepository(conexiune);
    }

    @AfterEach
    void teardown() throws Exception {
        conexiune.close();
    }

    @Test
    @DisplayName("adauga() salveaza angajatul si seteaza id-ul generat")
    void adaugaSalveaza() {
        Personal p = new Personal("ion", "parola1", "Ion Popescu");
        repo.adauga(p);
        assertTrue(p.getId() > 0);
    }

    @Test
    @DisplayName("gasesteDupaUsername() returneaza angajatul corect")
    void gasesteDupaUsername() {
        repo.adauga(new Personal("maria", "pass1234", "Maria Ion"));
        Personal gasit = repo.gasesteDupaUsername("maria");
        assertNotNull(gasit);
        assertEquals("maria", gasit.getUsername());
        assertEquals("Maria Ion", gasit.getNumeComplet());
    }

    @Test
    @DisplayName("gasesteDupaUsername() returneaza null pentru username inexistent")
    void gasesteDupaUsernameInexistent() {
        assertNull(repo.gasesteDupaUsername("inexistent"));
    }

    @Test
    @DisplayName("adauga() cu username duplicat arunca exceptie")
    void adaugaDuplicatArunca() {
        repo.adauga(new Personal("ion", "parola1", "Ion"));
        assertThrows(IllegalArgumentException.class,
            () -> repo.adauga(new Personal("ion", "parola2", "Alt Ion")));
    }

    @Test
    @DisplayName("getTotiAngajatii() returneaza toti angajatii adaugati")
    void getTotiAngajatii() {
        repo.adauga(new Personal("user1", "pass1111", "User Unu"));
        repo.adauga(new Personal("user2", "pass2222", "User Doi"));
        repo.adauga(new Personal("user3", "pass3333", "User Trei"));
        List<Personal> lista = repo.getTotiAngajatii();
        assertEquals(3, lista.size());
    }

    @Test
    @DisplayName("sterge() elimina angajatul si returneaza true")
    void stergeAngajatExistent() {
        repo.adauga(new Personal("de.sters", "pass9999", "De Sters"));
        boolean rezultat = repo.sterge("de.sters");
        assertTrue(rezultat);
        assertNull(repo.gasesteDupaUsername("de.sters"));
    }

    @Test
    @DisplayName("sterge() returneaza false pentru username inexistent")
    void stergeAngajatInexistent() {
        assertFalse(repo.sterge("nu.exista"));
    }

    @Test
    @DisplayName("getTotiAngajatii() returneaza lista goala cand nu exista angajati")
    void getTotiAngajatiiListaGoala() {
        assertTrue(repo.getTotiAngajatii().isEmpty());
    }
}