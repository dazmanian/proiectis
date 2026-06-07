package restaurant.model;

/**
 * Bautura nespirtoasa - una dintre cele 4 categorii principale.
 * Produsele obligatorii din aceasta categorie (apa plata, apa minerala,
 * limonada) sunt instantiate automat in clasa Meniu.
 */
public class BauturaNespirtoasa extends Bautura {

    private boolean carbogazoasa;

    public BauturaNespirtoasa(String nume, double pret, int volumMl, boolean carbogazoasa) {
        super(nume, pret, volumMl);
        this.carbogazoasa = carbogazoasa;
    }

    public boolean isCarbogazoasa() {
        return carbogazoasa;
    }

    public void setCarbogazoasa(boolean carbogazoasa) {
        this.carbogazoasa = carbogazoasa;
    }

    @Override
    public CategorieMeniu getCategorie() {
        return CategorieMeniu.BAUTURI_NESPIRTOASE;
    }
}
