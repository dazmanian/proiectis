package restaurant;

import restaurant.client.ArticolCos;
import restaurant.client.CosCumparaturi;
import restaurant.client.PopUpLegislativ;
import restaurant.comenzi.Chelner;
import restaurant.comenzi.Chitanta;
import restaurant.comenzi.Comanda;
import restaurant.comenzi.ElementComanda;
import restaurant.comenzi.MetodaPlata;
import restaurant.comenzi.StatusComanda;
import restaurant.manager.model.Manager;
import restaurant.manager.service.ManagerService;
import restaurant.model.Aperitiv;
import restaurant.model.BauturaNespirtoasa;
import restaurant.model.BauturaSpirtoasa;
import restaurant.model.CategorieMeniu;
import restaurant.model.FelPrincipal;
import restaurant.model.Meniu;
import restaurant.model.ProdusMeniu;
import restaurant.model.TipFelPrincipal;

import java.util.List;
import java.util.Scanner;

/**
 * ============================================================
 *  CONSOLA INTERACTIVA - Sistem de management restaurant
 * ============================================================
 * Interfata pe consola care leaga toate cele 5 module ale echipei intr-o
 * aplicatie navigabila cu meniuri de optiuni (utilizatorul tasteaza cifre).
 *   [a] meniu     - structura produselor
 *   [b] personal  - statusul comenzii in fluxul de preparare
 *   [c] client    - cosul de cumparaturi
 *   [d] client    - pop-up cu informatii despre produse
 *   [e] comenzi   - comanda, chelner, plata, chitanta
 *   [f] manager   - autentificare + administrare meniu
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Meniu meniu = new Meniu();
    private static final CosCumparaturi cos = new CosCumparaturi();
    private static final ManagerService managerService =
            new ManagerService(new Manager(), meniu, null);

    public static void main(String[] args) {
        meniu.incarcaProduseObligatorii();
        afiseazaBanner();
        boolean ruleaza = true;
        while (ruleaza) {
            afiseazaMeniuPrincipal();
            String optiune = citesteText("Alege o optiune");
            switch (optiune) {
                case "1" -> vizualizeazaMeniu();
                case "2" -> infoProdus();
                case "3" -> adaugaInCos();
                case "4" -> vizualizeazaCos();
                case "5" -> plaseazaComanda();
                case "6" -> zonaManager();
                case "0" -> { ruleaza = false; iesire(); }
                default  -> System.out.println("  ! Optiune invalida. Incearca din nou.");
            }
        }
    }

    private static void afiseazaMeniuPrincipal() {
        System.out.println("\n========== MENIU PRINCIPAL ==========");
        System.out.println("  1. Vizualizeaza meniul");
        System.out.println("  2. Informatii despre un produs");
        System.out.println("  3. Adauga un produs in cos");
        System.out.println("  4. Vezi cosul");
        System.out.println("  5. Plaseaza comanda");
        System.out.println("  6. Zona manager (autentificare)");
        System.out.println("  0. Iesire");
        System.out.println("=====================================");
    }

    private static void vizualizeazaMeniu() {
        System.out.println("\n----- MENIUL RESTAURANTULUI -----");
        for (CategorieMeniu cat : CategorieMeniu.values()) {
            System.out.println("\n  === " + cat.getDenumire() + " ===");
            int nr = 1;
            for (ProdusMeniu p : meniu.getProdusePeCategorie(cat)) {
                String ind = p.isDisponibil() ? "" : "  [INDISPONIBIL]";
                System.out.printf("   %d. %-25s %6.2f RON%s%n", nr++, p.getNume(), p.getPret(), ind);
            }
        }
    }

    private static void infoProdus() {
        ProdusMeniu p = alegeProdusDinMeniu("Pentru ce produs vrei informatii?");
        if (p != null) PopUpLegislativ.afiseazaPopUp(p);
    }

    private static void adaugaInCos() {
        ProdusMeniu p = alegeProdusDinMeniu("Ce produs vrei sa adaugi in cos?");
        if (p == null) return;
        int cantitate = citesteNumar("Ce cantitate?");
        if (cantitate <= 0) { System.out.println("  ! Cantitatea trebuie sa fie cel putin 1."); return; }
        if (cos.adaugaInCos(p, cantitate))
            System.out.printf("  + %d x %s adaugat in cos.%n", cantitate, p.getNume());
    }

    private static void vizualizeazaCos() {
        System.out.println("\n----- COSUL TAU -----");
        if (cos.getElemente().isEmpty()) { System.out.println("  Cosul este gol."); return; }
        for (ArticolCos a : cos.getElemente())
            System.out.printf("   %d x %-25s %7.2f RON%n", a.getCantitate(), a.getProdus().getNume(), a.getPretTotal());
        System.out.printf("   %-29s %7.2f RON%n", "TOTAL:", cos.calculeazaTotal());
    }

    private static void plaseazaComanda() {
        if (cos.getElemente().isEmpty()) { System.out.println("  ! Cosul este gol."); return; }
        int masa = citesteNumar("Numarul mesei");
        if (masa <= 0) { System.out.println("  ! Numar de masa invalid."); return; }
        String numeClient = citesteText("Numele tau");
        if (numeClient.isBlank()) numeClient = "Client";

        Comanda comanda = new Comanda(masa, numeClient);
        for (ArticolCos a : cos.getElemente())
            comanda.adaugaElement(new ElementComanda(a.getProdus(), a.getCantitate()));

        comanda.setTimpEstimatMinute(20);
        comanda.setStatus(StatusComanda.PREPARARE);
        comanda.setStatus(StatusComanda.SERVITA);
        System.out.println("\n  Comanda plasata! Status: " + comanda.getStatus().getDenumire());
        System.out.println("  " + comanda);

        System.out.println("\n  Metoda de plata:\n    1. Cash\n    2. Card");
        String mp = citesteText("Alege");
        MetodaPlata metoda = mp.equals("1") ? MetodaPlata.CASH : MetodaPlata.CARD;

        Chelner chelner = new Chelner("Ana Pop", "ana", "parola123");
        Chitanta chitanta = new Chitanta(comanda, chelner, metoda);
        comanda.setStatus(StatusComanda.PLATITA);
        System.out.println(chitanta.genereazaTextChitanta());

        cos.golesteCos();
        System.out.println("  Cosul a fost golit. Multumim!");
    }

    private static void zonaManager() {
        System.out.println("\n----- AUTENTIFICARE MANAGER -----");
        String email = citesteText("Email");
        String parola = citesteText("Parola");
        if (!managerService.autentifica(email, parola)) {
            System.out.println("  ! Credentiale gresite. Acces respins."); return;
        }
        System.out.println("  Autentificare reusita!");
        boolean inManager = true;
        while (inManager) {
            System.out.println("\n  --- PANOU MANAGER ---");
            System.out.println("    1. Adauga produs nou in meniu");
            System.out.println("    2. Modifica pretul unui produs");
            System.out.println("    3. Schimba disponibilitatea unui produs");
            System.out.println("    0. Inapoi (deconectare)");
            String opt = citesteText("Alege");
            switch (opt) {
                case "1" -> managerAdaugaProdus();
                case "2" -> managerModificaPret();
                case "3" -> managerSchimbaDisponibilitate();
                case "0" -> { inManager = false; managerService.deconecteaza(); }
                default  -> System.out.println("  ! Optiune invalida.");
            }
        }
    }

    private static void managerAdaugaProdus() {
        System.out.println("\n  Tip produs:\n    1. Aperitiv\n    2. Fel principal\n    3. Bautura spirtoasa\n    4. Bautura nespirtoasa");
        String tip = citesteText("Alege");
        String nume = citesteText("Nume produs");
        double pret = citesteZecimal("Pret (RON)");
        ProdusMeniu nou;
        switch (tip) {
            case "1" -> nou = new Aperitiv(nume, pret, false, false);
            case "2" -> nou = new FelPrincipal(nume, pret, false, false, TipFelPrincipal.CARNE);
            case "3" -> nou = new BauturaSpirtoasa(nume, pret, 100, 40.0);
            case "4" -> nou = new BauturaNespirtoasa(nume, pret, 500, false);
            default  -> { System.out.println("  ! Tip invalid."); return; }
        }
        managerService.adaugaProdusInMeniu(nou);
        System.out.println("  + Produs adaugat: " + nou.getNume());
    }

    private static void managerModificaPret() {
        ProdusMeniu p = alegeProdusDinMeniu("Ce produs modifici?");
        if (p == null) return;
        double pretNou = citesteZecimal("Pretul nou (RON)");
        boolean ok = managerService.modificaProdus(p.getNume(), p.getNume(), pretNou);
        System.out.println(ok ? "  Pret actualizat." : "  ! Nu s-a putut modifica.");
    }

    private static void managerSchimbaDisponibilitate() {
        ProdusMeniu p = alegeProdusDinMeniu("Ce produs?");
        if (p == null) return;
        boolean nou = !p.isDisponibil();
        managerService.seteazaDisponibilitate(p.getNume(), nou);
        System.out.println("  Disponibilitate: " + (nou ? "DISPONIBIL" : "INDISPONIBIL"));
    }

    private static ProdusMeniu alegeProdusDinMeniu(String prompt) {
        List<ProdusMeniu> toate = meniu.getToateProdusele();
        System.out.println("\n  " + prompt);
        for (int i = 0; i < toate.size(); i++)
            System.out.printf("    %d. %s%n", i + 1, toate.get(i).getNume());
        int idx = citesteNumar("Numarul produsului");
        if (idx < 1 || idx > toate.size()) { System.out.println("  ! Numar invalid."); return null; }
        return toate.get(idx - 1);
    }

    private static String citesteText(String prompt) {
        System.out.print("  " + prompt + ": ");
        return scanner.nextLine().trim();
    }

    private static int citesteNumar(String prompt) {
        System.out.print("  " + prompt + ": ");
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private static double citesteZecimal(String prompt) {
        System.out.print("  " + prompt + ": ");
        try { return Double.parseDouble(scanner.nextLine().trim().replace(",", ".")); }
        catch (NumberFormatException e) { return 0; }
    }

    private static void afiseazaBanner() {
        System.out.println("============================================================");
        System.out.println("     RESTAURANT - SISTEM DE MANAGEMENT (consola)");
        System.out.println("============================================================");
        System.out.println(" Bine ai venit! Foloseste cifrele pentru a naviga.");
    }

    private static void iesire() {
        System.out.println("\n  La revedere! Multumim ca ai folosit aplicatia.");
    }
}
