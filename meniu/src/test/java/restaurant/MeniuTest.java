package restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import restaurant.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste unitare pentru punctul a) - structura meniului.
 *
 * Verifica:
 *  1. crearea corecta a obiectelor (atribute, validari);
 *  2. atribuirea corecta a categoriilor (polimorfism getCategorie);
 *  3. incarcarea automata a produselor obligatorii cerute in enunt.
 *
 * Framework: JUnit 5 (Jupiter).
 */
class MeniuTest {

    private Meniu meniu;

    @BeforeEach
    void setUp() {
        meniu = new Meniu();
    }

    // ---------- 1. Crearea corecta a obiectelor ----------

    @Test
    @DisplayName("Aperitivul se creeaza cu atributele corecte si categoria APERITIVE")
    void testCreareAperitiv() {
        Aperitiv ap = new Aperitiv("Bruschete cu rosii", 18.0, false, true);
        assertEquals("Bruschete cu rosii", ap.getNume());
        assertEquals(18.0, ap.getPret());
        assertTrue(ap.isVegetarian());
        assertFalse(ap.isPicant());
        assertEquals(CategorieMeniu.APERITIVE, ap.getCategorie());
    }

    @Test
    @DisplayName("Felul principal retine subtipul (supa, carne, vegetarian, garnitura)")
    void testCreareFelPrincipalCuTip() {
        FelPrincipal supa = new FelPrincipal("Supa de legume", 15.0, false, true, TipFelPrincipal.SUPA);
        assertEquals(TipFelPrincipal.SUPA, supa.getTip());
        assertEquals(CategorieMeniu.FELURI_PRINCIPALE, supa.getCategorie());
    }

    @Test
    @DisplayName("Bautura spirtoasa retine gradul de alcool si categoria corecta")
    void testCreareBauturaSpirtoasa() {
        BauturaSpirtoasa vin = new BauturaSpirtoasa("Vin rosu", 14.0, 150, 13.5);
        assertEquals(13.5, vin.getGradAlcool());
        assertEquals(150, vin.getVolumMl());
        assertEquals(CategorieMeniu.BAUTURI_SPIRTOASE, vin.getCategorie());
    }

    @Test
    @DisplayName("Bautura nespirtoasa retine atributul carbogazoasa si categoria corecta")
    void testCreareBauturaNespirtoasa() {
        BauturaNespirtoasa apaMinerala = new BauturaNespirtoasa("Apa minerala", 6.0, 500, true);
        assertTrue(apaMinerala.isCarbogazoasa());
        assertEquals(CategorieMeniu.BAUTURI_NESPIRTOASE, apaMinerala.getCategorie());
    }

    @Test
    @DisplayName("Ingredientele se pot adauga si lista returnata este imutabila")
    void testAdaugaIngredientSiImutabilitate() {
        Aperitiv ap = new Aperitiv("Pesto", 16.0, false, true);
        ap.adaugaIngredient("busuioc");
        ap.adaugaIngredient("parmezan");
        assertEquals(2, ap.getIngrediente().size());
        assertThrows(UnsupportedOperationException.class,
                () -> ap.getIngrediente().add("ilegal"));
    }

    // ---------- Validari (defensive programming) ----------

    @Test
    @DisplayName("Numele gol arunca exceptie")
    void testNumeGolArunca() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aperitiv("", 10.0, false, false));
    }

    @Test
    @DisplayName("Pretul negativ arunca exceptie")
    void testPretNegativArunca() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aperitiv("Test", -5.0, false, false));
    }

    @Test
    @DisplayName("Gradul de alcool in afara intervalului (0,100] arunca exceptie")
    void testGradAlcoolInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> new BauturaSpirtoasa("Test", 10.0, 100, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new BauturaSpirtoasa("Test", 10.0, 100, 150));
    }

    @Test
    @DisplayName("Felul principal fara tip arunca exceptie")
    void testFelPrincipalFaraTipArunca() {
        assertThrows(IllegalArgumentException.class,
                () -> new FelPrincipal("Test", 10.0, false, false, null));
    }

    // ---------- 2. Categoriile ----------

    @Test
    @DisplayName("Exista exact 4 categorii principale")
    void testNumarCategorii() {
        assertEquals(4, CategorieMeniu.values().length);
    }

    @Test
    @DisplayName("getProdusePeCategorie filtreaza corect produsele")
    void testFiltrarePeCategorie() {
        meniu.incarcaProduseObligatorii();
        List<ProdusMeniu> aperitive = meniu.getProdusePeCategorie(CategorieMeniu.APERITIVE);
        assertEquals(3, aperitive.size());
        for (ProdusMeniu p : aperitive) {
            assertEquals(CategorieMeniu.APERITIVE, p.getCategorie());
        }
    }

    // ---------- 3. Produsele obligatorii ----------

    @Test
    @DisplayName("Aperitivele obligatorii din enunt sunt incarcate")
    void testAperitiveObligatorii() {
        meniu.incarcaProduseObligatorii();
        List<String> nume = meniu.getProdusePeCategorie(CategorieMeniu.APERITIVE)
                .stream().map(ProdusMeniu::getNume).toList();
        assertTrue(nume.contains("Bruschete cu rosii"));
        assertTrue(nume.contains("Pesto"));
        assertTrue(nume.contains("Bruschete cu somon"));
    }

    @Test
    @DisplayName("Bauturile nespirtoase obligatorii din enunt sunt incarcate")
    void testBauturiNespirtoaseObligatorii() {
        meniu.incarcaProduseObligatorii();
        List<String> nume = meniu.getProdusePeCategorie(CategorieMeniu.BAUTURI_NESPIRTOASE)
                .stream().map(ProdusMeniu::getNume).toList();
        assertTrue(nume.contains("Apa plata"));
        assertTrue(nume.contains("Apa minerala"));
        assertTrue(nume.contains("Limonada"));
    }

    @Test
    @DisplayName("Felurile principale acopera toate cele 4 subtipuri cerute")
    void testToateSubtipurileFelPrincipal() {
        meniu.incarcaProduseObligatorii();
        List<TipFelPrincipal> tipuri = meniu.getProdusePeCategorie(CategorieMeniu.FELURI_PRINCIPALE)
                .stream()
                .map(p -> ((FelPrincipal) p).getTip())
                .toList();
        assertTrue(tipuri.contains(TipFelPrincipal.SUPA));
        assertTrue(tipuri.contains(TipFelPrincipal.CARNE));
        assertTrue(tipuri.contains(TipFelPrincipal.VEGETARIAN));
        assertTrue(tipuri.contains(TipFelPrincipal.GARNITURA));
    }

    @Test
    @DisplayName("Toate cele 4 categorii principale sunt reprezentate dupa incarcare")
    void testToateCategoriileReprezentate() {
        meniu.incarcaProduseObligatorii();
        for (CategorieMeniu c : CategorieMeniu.values()) {
            assertFalse(meniu.getProdusePeCategorie(c).isEmpty(),
                    "Categoria " + c + " ar trebui sa aiba cel putin un produs.");
        }
    }

    @Test
    @DisplayName("adaugaProdus(null) arunca exceptie")
    void testAdaugaProdusNull() {
        assertThrows(IllegalArgumentException.class, () -> meniu.adaugaProdus(null));
    }
}
