package restaurant.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restaurant.model.FelPrincipal;
import restaurant.model.TipFelPrincipal;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CosCumparaturiTest {

    private CosCumparaturi cos;
    private FelPrincipal pizza;
    private FelPrincipal supa;

    // Se asigura un mediu curat inainte de fiecare test
    @BeforeEach
    void setUp() {
        cos = new CosCumparaturi();

        pizza = new FelPrincipal("Pizza", 30.0, true, false, TipFelPrincipal.CARNE);

        pizza.setDisponibil(true);

        pizza.adaugaIngredient("Aluat");
        pizza.adaugaIngredient("Sos");
        pizza.adaugaIngredient("Branza");

        supa = new FelPrincipal("Supa", 15.0, false, true, TipFelPrincipal.SUPA);
        supa.setDisponibil(false); // setam pe false ca sa testam ca nu se adauga in cos
    }

    @AfterEach
    void tearDown() {
        cos = null;
    }

    @Test
    void testAdaugaInCosProdusDisponibil() {
        boolean rezultat = cos.adaugaInCos(pizza, 2);
        assertTrue(rezultat, "Produsul ar trebui adaugat cu succes.");
        assertEquals(1, cos.getElemente().size());
    }

    @Test
    void testNuAdaugaProdusIndisponibil() {
        boolean rezultat = cos.adaugaInCos(supa, 1);
        assertFalse(rezultat, "Nu ar trebui sa adauge un produs indisponibil.");
        assertEquals(0, cos.getElemente().size());
    }

    @Test
    void testCalculareTotal() {
        cos.adaugaInCos(pizza, 2);
        assertEquals(60.0, cos.calculeazaTotal(), 0.01);
    }

    // Testare White Box pentru verificarea exceptiilor si a ramurilor de cod
    @Test
    void testPlaseazaComandaGoalaAruncaExceptie() {
        assertThrows(IllegalStateException.class, () -> cos.plaseazaComanda());
    }
}