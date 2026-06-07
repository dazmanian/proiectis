package restaurant.model;

/**
 * Fel principal - una dintre cele 4 categorii principale.
 * Enuntul cere ca felurile principale sa includa: supe, feluri de carne,
 * produse vegetariene si garnituri. Modelam acest aspect printr-un atribut
 * 'tip' de tipul TipFelPrincipal, in loc de a crea inca 4 subclase separate
 * (evitam o explozie de clase pentru o diferenta care e doar de eticheta).
 */
public class FelPrincipal extends Mancare {

    private TipFelPrincipal tip;

    public FelPrincipal(String nume, double pret, boolean picant,
                        boolean vegetarian, TipFelPrincipal tip) {
        super(nume, pret, picant, vegetarian);
        if (tip == null) {
            throw new IllegalArgumentException("Tipul felului principal este obligatoriu.");
        }
        this.tip = tip;
    }

    public TipFelPrincipal getTip() {
        return tip;
    }

    public void setTip(TipFelPrincipal tip) {
        if (tip == null) {
            throw new IllegalArgumentException("Tipul felului principal este obligatoriu.");
        }
        this.tip = tip;
    }

    @Override
    public CategorieMeniu getCategorie() {
        return CategorieMeniu.FELURI_PRINCIPALE;
    }
}
