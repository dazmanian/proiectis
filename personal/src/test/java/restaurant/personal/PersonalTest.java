package restaurant.personal;

import org.junit.jupiter.api.*;
import restaurant.personal.model.Personal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste Personal (model)")
class PersonalTest {

    @Test
    @DisplayName("Constructor valid creeaza angajatul corect")
    void constructorValid() {
        Personal p = new Personal("ion.popescu", "parola123", "Ion Popescu");
        assertEquals("ion.popescu", p.getUsername());
        assertEquals("Ion Popescu", p.getNumeComplet());
        assertEquals(0, p.getId());
    }

    @Test
    @DisplayName("Username gol arunca exceptie")
    void usernameGolArunca() {
        assertThrows(IllegalArgumentException.class,
            () -> new Personal("", "parola123", "Ion"));
    }

    @Test
    @DisplayName("Username null arunca exceptie")
    void usernameNullArunca() {
        assertThrows(IllegalArgumentException.class,
            () -> new Personal(null, "parola123", "Ion"));
    }

    @Test
    @DisplayName("Parola prea scurta arunca exceptie")
    void parolaPreaScurtaArunca() {
        assertThrows(IllegalArgumentException.class,
            () -> new Personal("ion", "abc", "Ion"));
    }

    @Test
    @DisplayName("verificaParola returneaza true pentru parola corecta")
    void verificaParolaCorecta() {
        Personal p = new Personal("maria", "secret99", "Maria");
        assertTrue(p.verificaParola("secret99"));
    }

    @Test
    @DisplayName("verificaParola returneaza false pentru parola gresita")
    void verificaParolaGresita() {
        Personal p = new Personal("maria", "secret99", "Maria");
        assertFalse(p.verificaParola("altaparola"));
    }

    @Test
    @DisplayName("setParola cu valoare valida actualizeaza parola")
    void setParolaValida() {
        Personal p = new Personal("ion", "parola1", "Ion");
        p.setParola("parolaNoua");
        assertTrue(p.verificaParola("parolaNoua"));
    }

    @Test
    @DisplayName("setParola prea scurta arunca exceptie")
    void setParolaPreaScurta() {
        Personal p = new Personal("ion", "parola1", "Ion");
        assertThrows(IllegalArgumentException.class, () -> p.setParola("ab"));
    }

    @Test
    @DisplayName("toString contine username-ul")
    void toStringContineUsername() {
        Personal p = new Personal(1, "test.user", "pass1234", "Test User");
        assertTrue(p.toString().contains("test.user"));
    }

    @Test
    @DisplayName("Nume complet null este tratat ca sir gol")
    void numeCompletNull() {
        Personal p = new Personal("ion", "parola1", null);
        assertEquals("", p.getNumeComplet());
    }
}