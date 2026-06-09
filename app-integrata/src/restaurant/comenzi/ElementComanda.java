package restaurant.comenzi;

import restaurant.model.ProdusMeniu;

/**
 * Un element dintr-o comanda: produs + cantitate.
 *
 * Design decision - snapshot la pretul unitar:
 *   Stocam pretul la momentul plasarii comenzii, nu o referinta live la
 *   ProdusMeniu. Astfel, daca managerul modifica pretul unui produs (punctul f),
 *   comenzile deja plasate nu sunt afectate retroactiv.
 *
 * Design decision - doua constructori:
 *   - Constructorul cu ProdusMeniu: folosit la plasarea comenzii noi (din UI).
 *   - Constructorul cu campuri primitive: folosit la reconstructia din baza de date,
 *     unde nu avem neaparat obiectul ProdusMeniu disponibil.
 */
public class ElementComanda {

    private int id;
    private int produsId;       // id-ul din tabelul produs_meniu al colegului
    private String numeProdus;  // snapshot la momentul comenzii
    private double pretUnitar;  // snapshot la momentul comenzii (RON)
    private int cantitate;

    /**
     * Constructor folosit la plasarea unei comenzi noi.
     * Extrage automat id-ul, numele si pretul din obiectul ProdusMeniu.
     */
    public ElementComanda(ProdusMeniu produs, int cantitate) {
        if (produs == null) {
            throw new IllegalArgumentException("Produsul nu poate fi null.");
        }
        if (cantitate <= 0) {
            throw new IllegalArgumentException("Cantitatea trebuie sa fie cel putin 1.");
        }
        this.produsId   = produs.getId();
        this.numeProdus = produs.getNume();
        this.pretUnitar = produs.getPret();
        this.cantitate  = cantitate;
    }

    /**
     * Constructor folosit la reconstructia din baza de date (ComandaRepository).
     */
    public ElementComanda(int produsId, String numeProdus, double pretUnitar, int cantitate) {
        if (numeProdus == null || numeProdus.isBlank()) {
            throw new IllegalArgumentException("Numele produsului nu poate fi gol.");
        }
        if (pretUnitar < 0) {
            throw new IllegalArgumentException("Pretul unitar nu poate fi negativ.");
        }
        if (cantitate <= 0) {
            throw new IllegalArgumentException("Cantitatea trebuie sa fie cel putin 1.");
        }
        this.produsId   = produsId;
        this.numeProdus = numeProdus;
        this.pretUnitar = pretUnitar;
        this.cantitate  = cantitate;
    }

    /** Returneaza valoarea totala a acestui rand (pretUnitar * cantitate). */
    public double getSubtotal() {
        return pretUnitar * cantitate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdusId() { return produsId; }

    public String getNumeProdus() { return numeProdus; }

    public double getPretUnitar() { return pretUnitar; }

    public int getCantitate() { return cantitate; }

    public void setCantitate(int cantitate) {
        if (cantitate <= 0) {
            throw new IllegalArgumentException("Cantitatea trebuie sa fie cel putin 1.");
        }
        this.cantitate = cantitate;
    }

    @Override
    public String toString() {
        return String.format("%s x%d = %.2f RON", numeProdus, cantitate, getSubtotal());
    }
}
