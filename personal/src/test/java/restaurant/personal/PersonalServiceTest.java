package restaurant.personal;

import org.junit.jupiter.api.*;
import restaurant.model.Comanda;
import restaurant.model.StatusComanda;
import restaurant.personal.model.Personal;
import restaurant.personal.repository.ComandaRepository;
import restaurant.personal.repository.PersonalRepository;
import restaurant.personal.service.PersonalService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste PersonalService")
class PersonalServiceTest {

    private Connection conexiune;
    private PersonalService service;
    private ComandaRepository comandaRepo;

    @BeforeEach
    void setup() throws Exception {
        conexiune = DriverManager.getConnection("jdbc:sqlite::memory:");
        PersonalRepository personalRepo = new PersonalRepository(conexiune);
        comandaRepo = new ComandaRepository(conexiune);
        service = new PersonalService(personalRepo, comandaRepo);
        personalRepo.adauga(new Personal("test.user", "parola123", "Test User"));
    }

    @AfterEach
    void teardown() throws Exception {
        conexiune.close();
    }

    @Test
    @DisplayName("autentifica() cu date corecte returneaza true")
    void autentificareCorecta() {
        assertTrue(service.autentifica("test.user", "parola123"));
        assertTrue(service.esteAutentificat());
        assertEquals("test.user", service.getAngajatAutentificat().getUsername());
    }

    @Test
    @DisplayName("autentifica() cu parola gresita returneaza false")
    void autentificareParolaGresita() {
        assertFalse(service.autentifica("test.user", "gresita"));
        assertFalse(service.esteAutentificat());
    }

    @Test
    @DisplayName("autentifica() cu username inexistent returneaza false")
    void autentificareUsernameInexistent() {
        assertFalse(service.autentifica("nu.exista", "orice"));
    }

    @Test
    @DisplayName("deconecteaza() sterge angajatul activ")
    void deconectare() {
        service.autentifica("test.user", "parola123");
        service.deconecteaza();
        assertFalse(service.esteAutentificat());
        assertNull(service.getAngajatAutentificat());
    }

    @Test
    @DisplayName("getComenziNefinalizate() fara autentificare arunca exceptie")
    void comenziFaraAutentificare() {
        assertThrows(IllegalStateException.class,
            () -> service.getComenziNefinalizate());
    }

    @Test
    @DisplayName("preiaComanda() fara autentificare arunca exceptie")
    void preluareFaraAutentificare() {
        assertThrows(IllegalStateException.class,
            () -> service.preiaComanda(1, 10));
    }

    @Test
    @DisplayName("preiaComanda() muta comanda in PREPARARE si seteaza timpul")
    void preluareComandaValida() {
        service.autentifica("test.user", "parola123");
        Comanda c = new Comanda(List.of(1, 2), "5");
        comandaRepo.salveaza(c);
        service.preiaComanda(c.getId(), 20);
        Comanda dupa = comandaRepo.gasesteDupaId(c.getId());
        assertEquals(StatusComanda.PREPARARE, dupa.getStatus());
        assertEquals(20, dupa.getTimpEstimatMinute());
    }

    @Test
    @DisplayName("preiaComanda() cu timp 0 arunca exceptie")
    void preluareTimpZeroArunca() {
        service.autentifica("test.user", "parola123");
        Comanda c = new Comanda(List.of(1), "1");
        comandaRepo.salveaza(c);
        assertThrows(IllegalArgumentException.class,
            () -> service.preiaComanda(c.getId(), 0));
    }

    @Test
    @DisplayName("preiaComanda() pe comanda deja in PREPARARE arunca exceptie")
    void preluareComandaDejaPreluat() {
        service.autentifica("test.user", "parola123");
        Comanda c = new Comanda(List.of(1), "2");
        comandaRepo.salveaza(c);
        service.preiaComanda(c.getId(), 15);
        assertThrows(IllegalStateException.class,
            () -> service.preiaComanda(c.getId(), 10));
    }

    @Test
    @DisplayName("preiaComanda() cu id inexistent arunca exceptie")
    void preluareIdInexistent() {
        service.autentifica("test.user", "parola123");
        assertThrows(IllegalArgumentException.class,
            () -> service.preiaComanda(9999, 10));
    }

    @Test
    @DisplayName("actualizeazaStatus() muta comanda din PREPARARE in SERVITA")
    void actualizeazaStatusServita() {
        service.autentifica("test.user", "parola123");
        Comanda c = new Comanda(List.of(1), "3");
        comandaRepo.salveaza(c);
        service.preiaComanda(c.getId(), 15);
        service.actualizeazaStatus(c.getId(), StatusComanda.SERVITA);
        assertEquals(StatusComanda.SERVITA, comandaRepo.gasesteDupaId(c.getId()).getStatus());
    }

    @Test
    @DisplayName("actualizeazaStatus() nu permite trecerea inapoi")
    void actualizeazaStatusInapoiArunca() {
        service.autentifica("test.user", "parola123");
        Comanda c = new Comanda(List.of(1), "4");
        comandaRepo.salveaza(c);
        service.preiaComanda(c.getId(), 10);
        service.actualizeazaStatus(c.getId(), StatusComanda.SERVITA);
        assertThrows(IllegalStateException.class,
            () -> service.actualizeazaStatus(c.getId(), StatusComanda.PREPARARE));
    }

    @Test
    @DisplayName("getComenziNefinalizate() nu include comenzile SERVITE")
    void comenziNefinalizateCorecte() {
        service.autentifica("test.user", "parola123");
        Comanda c1 = new Comanda(List.of(1), "1");
        Comanda c2 = new Comanda(List.of(2), "2");
        comandaRepo.salveaza(c1);
        comandaRepo.salveaza(c2);
        c2.setStatus(StatusComanda.SERVITA);
        comandaRepo.actualizeaza(c2);
        List<Comanda> nefinalizate = service.getComenziNefinalizate();
        assertEquals(1, nefinalizate.size());
        assertEquals(c1.getId(), nefinalizate.get(0).getId());
    }

    @Test
    @DisplayName("adaugaAngajat() adauga un angajat nou in sistem")
    void adaugaAngajat() {
        service.adaugaAngajat("nou.angajat", "parolanoua", "Angajat Nou");
        assertTrue(service.autentifica("nou.angajat", "parolanoua"));
    }

    @Test
    @DisplayName("stergeAngajat() elimina angajatul si returneaza true")
    void stergeAngajatExistent() {
        assertTrue(service.stergeAngajat("test.user"));
        assertFalse(service.autentifica("test.user", "parola123"));
    }

    @Test
    @DisplayName("getTotiAngajatii() returneaza lista corecta")
    void getTotiAngajatii() {
        service.adaugaAngajat("angajat2", "pass2222", "Angajat Doi");
        assertEquals(2, service.getTotiAngajatii().size());
    }
}