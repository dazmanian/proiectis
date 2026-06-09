package restaurant.comenzi;

/**
 * Metodele de plata acceptate la restaurant, conform cerintei punctului e).
 */
public enum MetodaPlata {
    CASH("Cash"),
    CARD("Card");

    private final String denumire;

    MetodaPlata(String denumire) {
        this.denumire = denumire;
    }

    public String getDenumire() {
        return denumire;
    }
}
