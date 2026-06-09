package restaurant.model;

/**
 * Clasa abstracta pentru mancare. Adauga atributele 'picant' si 'vegetarian',
 * care vor fi folosite la punctul d) (pop-up cu informatii) - implementat de
 * un coleg. Le punem aici pentru ca sunt comune oricarei mancari.
 *
 * Subclasele concrete sunt Aperitiv si FelPrincipal.
 */
public abstract class Mancare extends ProdusMeniu {

    private boolean picant;
    private boolean vegetarian;

    protected Mancare(String nume, double pret, boolean picant, boolean vegetarian) {
        super(nume, pret);
        this.picant = picant;
        this.vegetarian = vegetarian;
    }

    public boolean isPicant() {
        return picant;
    }

    public void setPicant(boolean picant) {
        this.picant = picant;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }
}
