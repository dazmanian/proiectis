package restaurant.client;

import restaurant.model.ProdusMeniu;

public class ArticolCos {
    private ProdusMeniu produs;
    private int cantitate;

    public ArticolCos(ProdusMeniu produs, int cantitate) {
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public ProdusMeniu getProdus() {
        return produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public void adaugaCantitate(int cantitateExtra) {
        this.cantitate += cantitateExtra;
    }

    public double getPretTotal() {
        return produs.getPret() * cantitate;
    }
}
