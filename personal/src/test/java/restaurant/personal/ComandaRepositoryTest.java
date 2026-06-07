package restaurant.personal;

import org.junit.jupiter.api.*;
import restaurant.model.Comanda;
import restaurant.model.StatusComanda;
import restaurant.personal.repository.ComandaRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste ComandaRepository")
class ComandaRepositoryTest {

    private Connection conexiune;
    private ComandaRepository repo;

    @BeforeEach
    void setup() throws Exception {
        conexiune = DriverManager.getConnection("jdbc:sqlite::memory:");
        repo = new ComandaRepository(conexiune);
    }

    @AfterEach
    void teardown() throws Exception {
        conexiune.close();
    }

    @Test
    @DisplayName("salveaza() persisteaza comanda si seteaza id-ul generat")
    void salveazaComanda() {
        Comanda c = new Comanda(List.of(1, 2), "3");
        repo.salveaza(c);
        assertTrue(c.getId() > 0);
    }

    @Test
    @DisplayName("gasesteDupaId() returneaza comanda salvata corect")
    void gasesteDupaId() {
        Comanda c = new Comanda(List.of(5, 6), "10");
        repo.salveaza(c);
        Comanda gasita = repo.gasesteDupaId(c.getId());
        assertNotNull(gasita);
        assertEquals(c.getId(), gasita.getId());
        assertEquals("10", gasita.getNumarMasa());
        assertEquals(StatusComanda.IN_ASTEPTARE, gasita.getStatus());
        assertEquals(List.of(5, 6), gasita.getIdProduse());
    }

    @Test
    @DisplayName("gasesteDupaId() returneaza null pentru id inexistent")
    void gasesteDupaIdInexistent() {
        assertNull(repo.gasesteDupaId(9999));
    }

    @Test
    @DisplayName("actualizeaza() salveaza noul status in DB")
    void actualizeazaStatus() {
        Comanda c = new Comanda(List.of(1), "2");
        repo.salveaza(c);
        c.setStatus(StatusComanda.PREPARARE);
        c.setTimpEstimatMinute(20);
        repo.actualizeaza(c);
        Comanda dupa = repo.gasesteDupaId(c.getId());
        assertEquals(StatusComanda.PREPARARE, dupa.getStatus());
        assertEquals(20, dupa.getTimpEstimatMinute());
    }

    @Test
    @DisplayName("getComenziNefinalizate() nu include comenzile SERVITE")
    void nefinalizateExcludeServite() {
        Comanda c1 = new Comanda(List.of(1), "1");
        Comanda c2 = new Comanda(List.of(2), "2");
        repo.salveaza(c1);
        repo.salveaza(c2);
        c2.setStatus(StatusComanda.SERVITA);
        repo.actualizeaza(c2);
        List<Comanda> nefinalizate = repo.getComenziNefinalizate();
        assertEquals(1, nefinalizate.size());
        assertEquals(c1.getId(), nefinalizate.get(0).getId());
    }

    @Test
    @DisplayName("getComenziNefinalizate() include comenzi IN_ASTEPTARE si PREPARARE")
    void nefinalizateIncludeAmbeleStatusuri() {
        Comanda c1 = new Comanda(List.of(1), "1");
        Comanda c2 = new Comanda(List.of(2), "2");
        repo.salveaza(c1);
        repo.salveaza(c2);
        c2.setStatus(StatusComanda.PREPARARE);
        c2.setTimpEstimatMinute(10);
        repo.actualizeaza(c2);
        assertEquals(2, repo.getComenziNefinalizate().size());
    }

    @Test
    @DisplayName("getComenziNefinalizate() returneaza lista goala daca nu exista comenzi")
    void nefinalizateListaGoala() {
        assertTrue(repo.getComenziNefinalizate().isEmpty());
    }

    @Test
    @DisplayName("getToateComenzi() returneaza inclusiv comenzile SERVITE")
    void toateComenziIncludeServite() {
        Comanda c1 = new Comanda(List.of(1), "1");
        Comanda c2 = new Comanda(List.of(2), "2");
        repo.salveaza(c1);
        repo.salveaza(c2);
        c2.setStatus(StatusComanda.SERVITA);
        repo.actualizeaza(c2);
        assertEquals(2, repo.getToateComenzi().size());
    }
}