package restaurant.model;

/**
 * Bautura spirtoasa - una dintre cele 4 categorii principale.
 * Adauga gradul de alcool (% vol).
 */
public class BauturaSpirtoasa extends Bautura {

    private double gradAlcool; // procent volumic, ex: 40.0

    public BauturaSpirtoasa(String nume, double pret, int volumMl, double gradAlcool) {
        super(nume, pret, volumMl);
        if (gradAlcool <= 0 || gradAlcool > 100) {
            throw new IllegalArgumentException("Gradul de alcool trebuie sa fie in intervalul (0, 100].");
        }
        this.gradAlcool = gradAlcool;
    }

    public double getGradAlcool() {
        return gradAlcool;
    }

    public void setGradAlcool(double gradAlcool) {
        if (gradAlcool <= 0 || gradAlcool > 100) {
            throw new IllegalArgumentException("Gradul de alcool trebuie sa fie in intervalul (0, 100].");
        }
        this.gradAlcool = gradAlcool;
    }

    @Override
    public CategorieMeniu getCategorie() {
        return CategorieMeniu.BAUTURI_SPIRTOASE;
    }
}
