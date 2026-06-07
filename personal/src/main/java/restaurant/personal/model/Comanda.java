package restaurant.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Comanda {

    private int id;
    private List<Integer> idProduse;
    private StatusComanda status;
    private int timpEstimatMinute;
    private LocalDateTime momentPlasat;
    private String numarMasa;

    public Comanda(List<Integer> idProduse, String numarMasa) {
        if (idProduse == null || idProduse.isEmpty()) {
            throw new IllegalArgumentException("O comanda trebuie sa contina cel putin un produs.");
        }
        this.idProduse = new ArrayList<>(idProduse);
        this.numarMasa = numarMasa;
        this.status = StatusComanda.IN_ASTEPTARE;
        this.timpEstimatMinute = 0;
        this.momentPlasat = LocalDateTime.now();
    }

    public Comanda(int id, List<Integer> idProduse, StatusComanda status,
                   int timpEstimatMinute, LocalDateTime momentPlasat, String numarMasa) {
        this.id = id;
        this.idProduse = new ArrayList<>(idProduse);
        this.status = status;
        this.timpEstimatMinute = timpEstimatMinute;
        this.momentPlasat = momentPlasat;
        this.numarMasa = numarMasa;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public List<Integer> getIdProduse() { return Collections.unmodifiableList(idProduse); }
    public StatusComanda getStatus() { return status; }
    public int getTimpEstimatMinute() { return timpEstimatMinute; }
    public LocalDateTime getMomentPlasat() { return momentPlasat; }
    public String getNumarMasa() { return numarMasa; }

    public void setStatus(StatusComanda status) {
        if (status == null) throw new IllegalArgumentException("Statusul nu poate fi null.");
        this.status = status;
    }

    public void setTimpEstimatMinute(int minute) {
        if (minute <= 0) throw new IllegalArgumentException("Timpul estimat trebuie sa fie pozitiv.");
        this.timpEstimatMinute = minute;
    }

    public boolean esteNefinalizata() { return this.status != StatusComanda.SERVITA; }
    public boolean estePreluata() { return this.timpEstimatMinute > 0; }

    @Override
    public String toString() {
        return String.format(
            "Comanda #%d | Masa: %s | Status: %s | Timp estimat: %s min | Plasat: %s | Produse (id): %s",
            id, numarMasa, status,
            timpEstimatMinute > 0 ? String.valueOf(timpEstimatMinute) : "nesestat",
            momentPlasat, idProduse
        );
    }
}