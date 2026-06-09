package restaurant.comenzi;

/**
 * Starile posibile ale unei comenzi, in ordinea fireasca a fluxului.
 *
 * IN_ASTEPTARE  -> comanda tocmai a fost plasata de client (punctul c)
 * PREPARARE     -> chelnerul a preluat comanda si buctarul o prepara (punctul b)
 * SERVITA       -> comanda a fost adusa la masa; se poate plati (punctul b)
 * PLATITA       -> chelnerul a confirmat plata si s-a emis chitanta (punctul e)
 */
public enum StatusComanda {
    IN_ASTEPTARE("In asteptare"),
    PREPARARE("In preparare"),
    SERVITA("Servita"),
    PLATITA("Platita");

    private final String denumire;

    StatusComanda(String denumire) {
        this.denumire = denumire;
    }

    public String getDenumire() {
        return denumire;
    }
}
