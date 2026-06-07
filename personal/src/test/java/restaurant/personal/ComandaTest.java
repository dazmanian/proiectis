package restaurant.personal;

import org.junit.jupiter.api.*;
import restaurant.model.Comanda;
import restaurant.model.StatusComanda;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste Comanda (model)")
class ComandaTest {

    private Comanda comanda;

    @BeforeEach
    void setup() {
        comanda = new Comanda(List.of(1, 2, 3), "5");
    }

    @Test
    @DisplayName("Comanda noua are status IN_ASTEPTARE")
    void statusInitialEsteInAsteptare() {
        assertEquals(StatusComanda.IN_ASTEPTARE, comanda.getStatus());
    }

    @Test
    @DisplayName("Comanda noua nu este preluata")
    void comandaNouaNuEstePreluata() {
        assertFalse(comanda.estePreluata());
        assertEquals(0, comanda.getTimpEstimatMinute());
    }

    @Test
    @DisplayName("Comanda noua este nefinalizata")
    void comandaNouaEsteNefinalizata() {
        assertTrue(comanda.esteNefinalizata());
    }

    @Test
    @DisplayName("Dupa setare timp estimat, comanda este preluata")
    void setareTimpEstimatMarcheazaComandaPreluata() {
        comanda.setTimpEstimatMinute(15);
        assertTrue(comanda.estePreluata());
        assertEquals(15, comanda.getTimpEstimatMinute());
    }

    @Test
    @DisplayName("Timp estimat negativ arunca exceptie")
    void timpEstimatNegativArunca() {
        assertThrows(IllegalArgumentException.class,
            () -> comanda.setTimpEstimatMinute(-5));
    }

    @Test
    @DisplayName("Timp estimat 0 arunca exceptie")
    void timpEstimatZeroArunca() {
        assertThrows(IllegalArgumentException.class,
            () -> comanda.setTimpEstimatMinute(0));
    }

    @Test
    @DisplayName("Comanda cu status SERVITA este finalizata")
    void comandaServitaEsteFinalizata() {
        comanda.setStatus(StatusComanda.SERVITA);
        assertFalse(comanda.esteNefinalizata());
    }

    @Test
    @DisplayName("setStatus null arunca exceptie")
    void setStatusNullArunca() {
        assertThrows(IllegalArgumentException.class,
            () -> comanda.setStatus(null));
    }

    @Test
    @DisplayName("Constructor cu lista goala arunca exceptie")
    void constructorListaGoalaArunca() {
        assertThrows(IllegalArgumentException.class,
            () -> new Comanda(List.of(), "1"));
    }

    @Test
    @DisplayName("Constructor cu lista null arunca exceptie")
    void constructorListaNullArunca() {
        assertThrows(IllegalArgumentException.class,
            () -> new Comanda(null, "1"));
    }

    @Test
    @DisplayName("Lista de produse este imutabila din exterior")
    void listaProduseEsteImutabila() {
        List<Integer> externa = comanda.getIdProduse();
        assertThrows(UnsupportedOperationException.class,
            () -> externa.add(99));
    }

    @Test
    @DisplayName("toString contine numarul mesei")
    void toStringContineNumarMasa() {
        assertTrue(comanda.toString().contains("5"));
    }
}