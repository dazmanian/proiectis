package restaurant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clasa abstracta de baza pentru orice produs din meniu.
 * Contine atributele comune tuturor produselor (mancare sau bautura).
 *
 * Folosim clasa abstracta (nu interfata) pentru ca dorim sa partajam
 * implementare (constructor, getteri/setteri), nu doar un contract.
 *
 * Atributele 'ingrediente' si 'disponibil' sunt incluse aici desi punctul a)
 * nu le foloseste direct: colegii de la punctele b) (disponibilitate) si
 * d) (lista de ingrediente in pop-up) le vor folosi.
 */
public abstract class ProdusMeniu {

    private int id;                       // id-ul din baza de date SQLite (0 = neatribuit)
    private String nume;
    private double pret;                  // pretul in RON
    private final List<String> ingrediente;
    private boolean disponibil;           // folosit la punctul b) - disponibilitate

    protected ProdusMeniu(String nume, double pret) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Numele produsului nu poate fi gol.");
        }
        if (pret < 0) {
            throw new IllegalArgumentException("Pretul nu poate fi negativ.");
        }
        this.nume = nume;
        this.pret = pret;
        this.ingrediente = new ArrayList<>();
        this.disponibil = true;
    }

    /**
     * Fiecare produs apartine uneia dintre cele 4 categorii principale.
     * Metoda este abstracta: fiecare subclasa decide categoria sa.
     */
    public abstract CategorieMeniu getCategorie();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public double getPret() {
        return pret;
    }

    public void setPret(double pret) {
        if (pret < 0) {
            throw new IllegalArgumentException("Pretul nu poate fi negativ.");
        }
        this.pret = pret;
    }

    public boolean isDisponibil() {
        return disponibil;
    }

    public void setDisponibil(boolean disponibil) {
        this.disponibil = disponibil;
    }

    public List<String> getIngrediente() {
        return Collections.unmodifiableList(ingrediente);
    }

    public void adaugaIngredient(String ingredient) {
        if (ingredient != null && !ingredient.isBlank()) {
            ingrediente.add(ingredient);
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %.2f RON%s",
                nume, getCategorie().getDenumire(), pret,
                disponibil ? "" : " [indisponibil]");
    }
}
