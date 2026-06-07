package restaurant.personal.ui;

import restaurant.model.Comanda;
import restaurant.model.StatusComanda;
import restaurant.personal.model.Personal;
import restaurant.personal.service.PersonalService;

import java.util.List;
import java.util.Scanner;

public class ConsolaPersonal {

    private final PersonalService service;
    private final Scanner scanner;

    public ConsolaPersonal(PersonalService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void porneste() {
        System.out.println("===========================================");
        System.out.println("  SISTEM MANAGEMENT RESTAURANT - PERSONAL ");
        System.out.println("===========================================");

        while (true) {
            if (!service.esteAutentificat()) {
                if (!ecranAutentificare()) {
                    System.out.println("Iesire din aplicatie. La revedere!");
                    break;
                }
            } else {
                afiseazaMeniu();
                int optiune = citesteInt("Alegeti optiunea: ");
                proceseazaOptiune(optiune);
            }
        }
    }

    private boolean ecranAutentificare() {
        System.out.println("\n--- AUTENTIFICARE ---");
        System.out.print("Username (sau 'exit' pentru iesire): ");
        String username = scanner.nextLine().trim();
        if (username.equalsIgnoreCase("exit")) return false;

        System.out.print("Parola: ");
        String parola = scanner.nextLine();

        if (service.autentifica(username, parola)) {
            Personal angajat = service.getAngajatAutentificat();
            System.out.println("\nBun venit, " +
                (angajat.getNumeComplet().isBlank() ? angajat.getUsername() : angajat.getNumeComplet()) + "!");
            return true;
        } else {
            System.out.println("Username sau parola incorecta. Incercati din nou.");
            return true;
        }
    }

    private void afiseazaMeniu() {
        System.out.println("\n--- MENIU PRINCIPAL ---");
        System.out.println("1. Vizualizeaza comenzile nefinalizate");
        System.out.println("2. Preia o comanda (seteaza timp estimat)");
        System.out.println("3. Marcheaza comanda ca SERVITA");
        System.out.println("4. Actualizeaza disponibilitate produs meniu");
        System.out.println("5. Deconectare");
        System.out.println("-------------------------------");
    }

    private void proceseazaOptiune(int optiune) {
        switch (optiune) {
            case 1 -> afiseazaComenziNefinalizate();
            case 2 -> preiaComanda();
            case 3 -> marcheazaServita();
            case 4 -> actualizeazaDisponibilitateProdus();
            case 5 -> {
                service.deconecteaza();
                System.out.println("Ati fost deconectat.");
            }
            default -> System.out.println("Optiune invalida. Alegeti un numar intre 1 si 5.");
        }
    }

    private void afiseazaComenziNefinalizate() {
        List<Comanda> comenzi = service.getComenziNefinalizate();
        System.out.println("\n--- COMENZI NEFINALIZATE ---");
        if (comenzi.isEmpty()) {
            System.out.println("Nu exista comenzi nefinalizate in acest moment.");
            return;
        }
        for (Comanda c : comenzi) {
            System.out.println(formateazaComanda(c));
        }
        System.out.println("Total: " + comenzi.size() + " comanda(zi).");
    }

    private void preiaComanda() {
        afiseazaComenziNefinalizate();
        int idComanda = citesteInt("\nIntroduceti ID-ul comenzii de preluat: ");
        int timp = citesteInt("Timp estimat de finalizare (minute): ");
        try {
            service.preiaComanda(idComanda, timp);
            System.out.println("Comanda #" + idComanda + " preluata. Timp estimat: " + timp + " minute.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void marcheazaServita() {
        afiseazaComenziNefinalizate();
        int idComanda = citesteInt("\nIntroduceti ID-ul comenzii de marcat ca SERVITA: ");
        try {
            service.actualizeazaStatus(idComanda, StatusComanda.SERVITA);
            System.out.println("Comanda #" + idComanda + " marcata ca SERVITA.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void actualizeazaDisponibilitateProdus() {
        int idProdus = citesteInt("Introduceti ID-ul produsului: ");
        System.out.print("Disponibil? (da/nu): ");
        String raspuns = scanner.nextLine().trim().toLowerCase();
        boolean disponibil = raspuns.equals("da") || raspuns.equals("d");
        try {
            System.out.println("Disponibilitatea produsului #" + idProdus +
                " setata la: " + (disponibil ? "DISPONIBIL" : "INDISPONIBIL"));
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private String formateazaComanda(Comanda c) {
        String timpStr = c.estePreluata() ? c.getTimpEstimatMinute() + " min" : "nesetat";
        return String.format(
            "  [#%d] Masa: %-6s | Status: %-12s | Timp estimat: %-10s | Produse (id): %s",
            c.getId(), c.getNumarMasa(), c.getStatus(), timpStr, c.getIdProduse()
        );
    }

    private int citesteInt(String mesaj) {
        while (true) {
            System.out.print(mesaj);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Introduceti un numar intreg valid.");
            }
        }
    }
}