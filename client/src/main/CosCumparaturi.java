package restaurant.client;

import restaurant.model.ProdusMeniu;
import java.util.ArrayList;
import java.util.List;

public class CosCumparaturi {
    private List<ArticolCos> elemente = new ArrayList<>();

    public boolean adaugaInCos(ProdusMeniu produs, int cantitate) {
        if (!produs.isDisponibil()) {
            System.out.println("Produsul " + produs.getNume() + " nu este disponibil.");
            return false;
        }

        for (ArticolCos articol : elemente) {
            if (articol.getProdus().getNume().equals(produs.getNume())) {
                articol.adaugaCantitate(cantitate);
                return true;
            }
        }

        elemente.add(new ArticolCos(produs, cantitate));
        return true;
    }

    public double calculeazaTotal() {
        return elemente.stream().mapToDouble(ArticolCos::getPretTotal).sum();
    }

    public List<ArticolCos> getElemente() {
        return elemente;
    }

    public void golesteCos() {
        elemente.clear();
    }

    public void plaseazaComanda() {
        if (elemente.isEmpty()) {
            throw new IllegalStateException("Coșul este gol!");
        }

        System.out.println("Comanda a fost trimisă către modulul de Comenzi! Total: " + calculeazaTotal());
        this.golesteCos();
    }
}
