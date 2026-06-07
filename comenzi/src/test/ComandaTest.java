package restaurant.comenzi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComandaTest {

    // --- Constructor ---

    @Test
    void constructorValid_seteazaCampurileCorecte() {
        Comanda c = new Comanda(3, "Ion Popescu");

        assertEquals(3, c.getNumarMasa());
        assertEquals("Ion Popescu", c.getNumeClient());
        assertEquals(StatusComanda.IN_ASTEPTARE, c.getStatus());
        assertNotNull(c.getDataCreare());
        assertEquals(0, c.getNrElemente());
        assertNull(c.getTimpEstimatMinute());
    }

    @Test
    void constructor_numarMasaNegativ_arunca() {
        assertThrows(IllegalArgumentException.class, () -> new Comanda(0, "Ion"));
        assertThrows(IllegalArgumentException.class, () -> new Comanda(-5, "Ion"));
    }

    @Test
    void constructor_numeClientGol_arunca() {
        assertThrows(IllegalArgumentException.class, () -> new Comanda(1, ""));
        assertThrows(IllegalArgumentException.class, () -> new Comanda(1, "   "));
        assertThrows(IllegalArgumentException.class, () -> new Comanda(1, null));
    }

    // --- adaugaElement si calculeazaTotal ---

    @Test
    void adaugaElement_cresterNumarSiTotal() {
        Comanda c = new Comanda(1, "Test");
        c.adaugaElement(new ElementComanda(1, "Supa de legume", 15.0, 2));
        c.adaugaElement(new ElementComanda(2, "Snitel de pui",  28.0, 1));

        assertEquals(2, c.getNrElemente());
        // 2*15 + 1*28 = 58
        assertEquals(58.0, c.calculeazaTotal(), 0.001);
    }

    @Test
    void adaugaElement_null_arunca() {
        Comanda c = new Comanda(1, "Test");
        assertThrows(IllegalArgumentException.class, () -> c.adaugaElement(null));
    }

    @Test
    void calculeazaTotal_comenziGoala_returneazaZero() {
        Comanda c = new Comanda(1, "Test");
        assertEquals(0.0, c.calculeazaTotal(), 0.001);
    }

    @Test
    void getElemente_returneazaVedereImuabila() {
        Comanda c = new Comanda(1, "Test");
        assertThrows(UnsupportedOperationException.class,
                () -> c.getElemente().add(new ElementComanda(1, "X", 10.0, 1)));
    }

    // --- Status ---

    @Test
    void setStatus_statusNull_arunca() {
        Comanda c = new Comanda(1, "Test");
        assertThrows(IllegalArgumentException.class, () -> c.setStatus(null));
    }

    @Test
    void setStatus_actualizeazaStatusul() {
        Comanda c = new Comanda(1, "Test");
        c.setStatus(StatusComanda.PREPARARE);
        assertEquals(StatusComanda.PREPARARE, c.getStatus());
    }

    // --- Timp estimat ---

    @Test
    void setTimpEstimat_valoareNegativa_arunca() {
        Comanda c = new Comanda(1, "Test");
        assertThrows(IllegalArgumentException.class, () -> c.setTimpEstimatMinute(-1));
    }

    @Test
    void setTimpEstimat_zero_estePermis() {
        Comanda c = new Comanda(1, "Test");
        assertDoesNotThrow(() -> c.setTimpEstimatMinute(0));
        assertEquals(0, c.getTimpEstimatMinute());
    }

    @Test
    void setTimpEstimat_null_stergeValoarea() {
        Comanda c = new Comanda(1, "Test");
        c.setTimpEstimatMinute(30);
        c.setTimpEstimatMinute(null);
        assertNull(c.getTimpEstimatMinute());
    }

    // --- toString ---

    @Test
    void toString_contineInformatiiEsentiale() {
        Comanda c = new Comanda(5, "Maria Ionescu");
        c.adaugaElement(new ElementComanda(1, "Supa", 15.0, 1));

        String s = c.toString();
        assertTrue(s.contains("5"),             "Trebuie sa contina numarul mesei");
        assertTrue(s.contains("Maria Ionescu"), "Trebuie sa contina numele clientului");
        assertTrue(s.contains("asteptare"),     "Trebuie sa contina statusul");
        assertTrue(s.contains("15"),            "Trebuie sa contina totalul");
    }
}
