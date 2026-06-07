package restaurant.model;

/**
 * Subtipurile felurilor principale, conform enuntului:
 * "Felurile principale vor include supe, diferite feluri de carne,
 *  produse vegetariene si garnituri."
 */
public enum TipFelPrincipal {
    SUPA("Supa"),
    CARNE("Fel de carne"),
    VEGETARIAN("Produs vegetarian"),
    GARNITURA("Garnitura");

    private final String denumire;

    TipFelPrincipal(String denumire) {
        this.denumire = denumire;
    }

    public String getDenumire() {
        return denumire;
    }
}
