package restaurant.client;

import restaurant.model.ProdusMeniu;
import restaurant.model.Mancare;
import java.util.List;

public class PopUpLegislativ {

    public static void afiseazaPopUp(ProdusMeniu produs) {
        System.out.println("\n=== [X] INCHIDE ===");
        System.out.println("Detalii pentru: " + produs.getNume());

        List<String> ingrediente = produs.getIngrediente();
        if (ingrediente == null || ingrediente.isEmpty()) {
            System.out.println("Ingrediente: Nespecificat");
        } else {
            System.out.println("Ingrediente: " + String.join(", ", ingrediente));
        }

        if (produs instanceof Mancare) {
            Mancare mancare = (Mancare) produs;
            System.out.print("Alergeni/Info: ");
            if (mancare.isVegetarian()) System.out.print("[VEGETARIAN] ");
            if (mancare.isPicant()) System.out.print("[PICANT] ");
            System.out.println();
        }
        System.out.println("===================\n");
    }
}
