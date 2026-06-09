package restaurant.model;

/**
 * Aperitiv - una dintre cele 4 categorii principale.
 * Produsele obligatorii (bruschete cu rosii, pesto, bruschete cu somon)
 * sunt instantiate automat in clasa Meniu.
 */
public class Aperitiv extends Mancare {

    public Aperitiv(String nume, double pret, boolean picant, boolean vegetarian) {
        super(nume, pret, picant, vegetarian);
    }

    @Override
    public CategorieMeniu getCategorie() {
        return CategorieMeniu.APERITIVE;
    }
}
