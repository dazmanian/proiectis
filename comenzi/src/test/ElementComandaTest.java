package restaurant.comenzi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElementComandaTest {

    // --- Constructor cu campuri primitive ---

    @Test
    void constructorPrimitiv_valid() {
        ElementComanda e = new ElementComanda(7, "Supa de legume", 15.0, 2);

        assertEquals(7,               e.getProdusId());
        assertEquals("Supa de legume", e.getNumeProdus());
        assertEquals(15.0,            e.getPretUnitar(), 0.001);
        assertEquals(2,               e.getCantitate());
    }

    @Test
    void constructorPrimitiv_cantitateZero_arunca() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElementComanda(1, "Supa", 15.0, 0));
    }

    @Test
    void constructorPrimitiv_cantitateNegativa_arunca() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElementComanda(1, "Supa", 15.0, -3));
    }

    @Test
    void constructorPrimitiv_pretNegativ_arunca() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElementComanda(1, "Supa", -1.0, 1));
    }

    @Test
    void constructorPrimitiv_numeGol_arunca() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElementComanda(1, "", 10.0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ElementComanda(1, "  ", 10.0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ElementComanda(1, null, 10.0, 1));
    }

    // --- getSubtotal ---

    @Test
    void getSubtotal_calculeazaCorect() {
        ElementComanda e = new ElementComanda(1, "Supa", 15.0, 3);
        // 15.0 * 3 = 45.0
        assertEquals(45.0, e.getSubtotal(), 0.001);
    }

    @Test
    void getSubtotal_cantitateUnu_egalacu_pretUnitar() {
        ElementComanda e = new ElementComanda(1, "Supa", 22.50, 1);
        assertEquals(22.50, e.getSubtotal(), 0.001);
    }

    // --- setCantitate ---

    @Test
    void setCantitate_valoareValida_actualizeazaSubtotalul() {
        ElementComanda e = new ElementComanda(1, "Supa", 15.0, 1);
        e.setCantitate(5);

        assertEquals(5,    e.getCantitate());
        assertEquals(75.0, e.getSubtotal(), 0.001);
    }

    @Test
    void setCantitate_valoareInvalida_arunca() {
        ElementComanda e = new ElementComanda(1, "Supa", 15.0, 1);
        assertThrows(IllegalArgumentException.class, () -> e.setCantitate(0));
        assertThrows(IllegalArgumentException.class, () -> e.setCantitate(-2));
    }

    // --- setId ---

    @Test
    void setId_seteazaIdulDinDb() {
        ElementComanda e = new ElementComanda(1, "Supa", 15.0, 1);
        assertEquals(0, e.getId()); // neatribuit initial
        e.setId(42);
        assertEquals(42, e.getId());
    }

    // --- toString ---

    @Test
    void toString_contineNumeSiCantitateTotal() {
        ElementComanda e = new ElementComanda(1, "Snitel de pui", 28.0, 2);
        String s = e.toString();
        assertTrue(s.contains("Snitel de pui"));
        assertTrue(s.contains("2"));
        assertTrue(s.contains("56"));
    }
}
