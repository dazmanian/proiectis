package restaurant.model;

/**
 * Clasa abstracta pentru bauturi. Adauga un atribut comun bauturilor:
 * volumul (in ml). Subclasele concrete sunt BauturaSpirtoasa si
 * BauturaNespirtoasa.
 */
public abstract class Bautura extends ProdusMeniu {

    private int volumMl;

    protected Bautura(String nume, double pret, int volumMl) {
        super(nume, pret);
        if (volumMl <= 0) {
            throw new IllegalArgumentException("Volumul trebuie sa fie pozitiv.");
        }
        this.volumMl = volumMl;
    }

    public int getVolumMl() {
        return volumMl;
    }

    public void setVolumMl(int volumMl) {
        if (volumMl <= 0) {
            throw new IllegalArgumentException("Volumul trebuie sa fie pozitiv.");
        }
        this.volumMl = volumMl;
    }
}
