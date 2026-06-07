package restaurant.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Agregatorul tuturor produselor din restaurant.
 *
 * Responsabilitatile cheie ale punctului a):
 *  - tine evidenta produselor grupate pe cele 4 categorii;
 *  - instantiaza AUTOMAT produsele obligatorii cerute in enunt
 *    (metoda incarcaProduseObligatorii).
 *
 * Colegii vor folosi metodele getProduse... pentru a afisa meniul (punctul c),
 * a-l filtra etc. Persistenta in SQLite este izolata in MeniuRepository.
 */
public class Meniu {

    private final List<ProdusMeniu> produse = new ArrayList<>();

    /**
     * Adauga un produs in meniu. Returneaza produsul (util pentru lant/teste).
     */
    public ProdusMeniu adaugaProdus(ProdusMeniu produs) {
        if (produs == null) {
            throw new IllegalArgumentException("Produsul nu poate fi null.");
        }
        produse.add(produs);
        return produs;
    }

    public List<ProdusMeniu> getToateProdusele() {
        return new ArrayList<>(produse);
    }

    /**
     * Returneaza toate produsele dintr-o categorie data.
     * Va fi folosit de colegul de la punctul c) pentru afisarea pe categorii.
     */
    public List<ProdusMeniu> getProdusePeCategorie(CategorieMeniu categorie) {
        List<ProdusMeniu> rezultat = new ArrayList<>();
        for (ProdusMeniu p : produse) {
            if (p.getCategorie() == categorie) {
                rezultat.add(p);
            }
        }
        return rezultat;
    }

    public int getNumarProduse() {
        return produse.size();
    }

    /**
     * Instantiaza si adauga in meniu produsele OBLIGATORII cerute la punctul a):
     *
     *  Aperitive obligatorii:
     *    - bruschete cu rosii
     *    - pesto
     *    - bruschete cu somon
     *
     *  Bauturi nespirtoase obligatorii:
     *    - apa plata
     *    - apa minerala
     *    - limonada
     *
     * In plus, adaugam cate un exemplu pentru fiecare subtip de fel principal
     * (supa, carne, vegetarian, garnitura) si o bautura spirtoasa, pentru ca
     * toate cele 4 categorii principale sa fie reprezentate la pornire.
     * Preturile/ingredientele sunt valori demonstrative; managerul (punctul f)
     * le va putea modifica ulterior.
     */
    public void incarcaProduseObligatorii() {
        // --- Aperitive obligatorii ---
        Aperitiv bruscheteRosii = new Aperitiv("Bruschete cu rosii", 18.0, false, true);
        bruscheteRosii.adaugaIngredient("paine prajita");
        bruscheteRosii.adaugaIngredient("rosii");
        bruscheteRosii.adaugaIngredient("busuioc");
        adaugaProdus(bruscheteRosii);

        Aperitiv pesto = new Aperitiv("Pesto", 16.0, false, true);
        pesto.adaugaIngredient("busuioc");
        pesto.adaugaIngredient("parmezan");
        pesto.adaugaIngredient("ulei de masline");
        pesto.adaugaIngredient("nuci de pin");
        adaugaProdus(pesto);

        Aperitiv bruscheteSomon = new Aperitiv("Bruschete cu somon", 24.0, false, false);
        bruscheteSomon.adaugaIngredient("paine prajita");
        bruscheteSomon.adaugaIngredient("somon afumat");
        bruscheteSomon.adaugaIngredient("crema de branza");
        adaugaProdus(bruscheteSomon);

        // --- Bauturi nespirtoase obligatorii ---
        adaugaProdus(new BauturaNespirtoasa("Apa plata", 6.0, 500, false));
        adaugaProdus(new BauturaNespirtoasa("Apa minerala", 6.0, 500, true));

        BauturaNespirtoasa limonada = new BauturaNespirtoasa("Limonada", 12.0, 400, false);
        limonada.adaugaIngredient("lamaie");
        limonada.adaugaIngredient("apa");
        limonada.adaugaIngredient("zahar");
        limonada.adaugaIngredient("menta");
        adaugaProdus(limonada);

        // --- Feluri principale (cate un exemplu pentru fiecare subtip cerut) ---
        adaugaProdus(new FelPrincipal("Supa de legume", 15.0, false, true, TipFelPrincipal.SUPA));
        adaugaProdus(new FelPrincipal("Snitel de pui", 28.0, false, false, TipFelPrincipal.CARNE));
        adaugaProdus(new FelPrincipal("Tocanita de legume", 22.0, false, true, TipFelPrincipal.VEGETARIAN));
        adaugaProdus(new FelPrincipal("Cartofi prajiti", 10.0, false, true, TipFelPrincipal.GARNITURA));

        // --- Bautura spirtoasa (pentru a reprezenta a patra categorie principala) ---
        adaugaProdus(new BauturaSpirtoasa("Vin rosu (pahar)", 14.0, 150, 13.5));
    }
}
