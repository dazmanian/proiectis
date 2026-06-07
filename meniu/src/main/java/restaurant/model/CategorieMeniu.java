package restaurant.model;

/**
 * Cele 4 categorii principale de produse cerute la punctul a).
 * Aceste categorii vor fi afisate pe pagina de intrare a clientului (punctul c),
 * implementat de un coleg).
 */
public enum CategorieMeniu {
    APERITIVE("Aperitive"),
    FELURI_PRINCIPALE("Feluri principale"),
    BAUTURI_SPIRTOASE("Bauturi spirtoase"),
    BAUTURI_NESPIRTOASE("Bauturi nespirtoase");

    private final String denumire;

    CategorieMeniu(String denumire) {
        this.denumire = denumire;
    }

    public String getDenumire() {
        return denumire;
    }
}
