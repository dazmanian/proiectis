package restaurant.comenzi;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reprezinta o comanda plasata de un client la o masa.
 *
 * Ciclul de viata (punctul e):
 *   1. Clientul adauga produse in cos si confirma -> se creeaza Comanda (IN_ASTEPTARE)
 *   2. Personalul preia comanda, seteaza timp estimat -> PREPARARE  (punctul b)
 *   3. Comanda este servita la masa                  -> SERVITA     (punctul b)
 *   4. Chelnerul proceseaza plata                    -> PLATITA     (punctul e)
 *
 * Campul timpEstimatMinute este setat de personal la punctul b), dar apartine
 * modelului Comanda si este salvat de noi in baza de date.
 */
public class Comanda {

    private int id;
    private int numarMasa;
    private String numeClient;
    private final List<ElementComanda> elemente;
    private StatusComanda status;
    private LocalDateTime dataCreare;
    private Integer timpEstimatMinute;  // setat de chelner la preluare (punctul b)

    public Comanda(int numarMasa, String numeClient) {
        if (numarMasa <= 0) {
            throw new IllegalArgumentException("Numarul mesei trebuie sa fie pozitiv.");
        }
        if (numeClient == null || numeClient.isBlank()) {
            throw new IllegalArgumentException("Numele clientului nu poate fi gol.");
        }
        this.numarMasa  = numarMasa;
        this.numeClient = numeClient;
        this.elemente   = new ArrayList<>();
        this.status     = StatusComanda.IN_ASTEPTARE;
        this.dataCreare = LocalDateTime.now();
    }

    /**
     * Adauga un produs cu cantitate in comanda.
     * Apelat de colegul de la punctul c) cand clientul confirma cosul.
     */
    public void adaugaElement(ElementComanda element) {
        if (element == null) {
            throw new IllegalArgumentException("Elementul nu poate fi null.");
        }
        elemente.add(element);
    }

    /** Calculeaza totalul comenzii insumand subtotalurile tuturor elementelor. */
    public double calculeazaTotal() {
        return elemente.stream()
                .mapToDouble(ElementComanda::getSubtotal)
                .sum();
    }

    /** Returneaza o vedere neuditabila a listei de elemente. */
    public List<ElementComanda> getElemente() {
        return Collections.unmodifiableList(elemente);
    }

    public int getNrElemente() {
        return elemente.size();
    }

    // --- Getteri si setteri ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumarMasa() { return numarMasa; }
    public void setNumarMasa(int numarMasa) {
        if (numarMasa <= 0) throw new IllegalArgumentException("Numarul mesei trebuie sa fie pozitiv.");
        this.numarMasa = numarMasa;
    }

    public String getNumeClient() { return numeClient; }
    public void setNumeClient(String numeClient) {
        if (numeClient == null || numeClient.isBlank())
            throw new IllegalArgumentException("Numele clientului nu poate fi gol.");
        this.numeClient = numeClient;
    }

    public StatusComanda getStatus() { return status; }
    public void setStatus(StatusComanda status) {
        if (status == null) throw new IllegalArgumentException("Statusul nu poate fi null.");
        this.status = status;
    }

    public LocalDateTime getDataCreare() { return dataCreare; }
    public void setDataCreare(LocalDateTime dataCreare) { this.dataCreare = dataCreare; }

    public Integer getTimpEstimatMinute() { return timpEstimatMinute; }
    public void setTimpEstimatMinute(Integer timpEstimatMinute) {
        if (timpEstimatMinute != null && timpEstimatMinute < 0) {
            throw new IllegalArgumentException("Timpul estimat nu poate fi negativ.");
        }
        this.timpEstimatMinute = timpEstimatMinute;
    }

    @Override
    public String toString() {
        return String.format("Comanda #%d | Masa %d | Client: %s | Status: %s | Total: %.2f RON",
                id, numarMasa, numeClient, status.getDenumire(), calculeazaTotal());
    }
}
