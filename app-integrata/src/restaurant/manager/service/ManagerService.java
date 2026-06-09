package restaurant.manager.service;

import restaurant.manager.model.Manager;
import restaurant.model.Meniu;
import restaurant.model.ProdusMeniu;
import restaurant.repository.MeniuRepository;

import java.util.List;

/**
 * Serviciul managerului (punctul f).
 *
 * Responsabilitati conform enuntului:
 *   - autentificarea managerului (admin@restaurant.null / adminRestaurantMagic12);
 *   - adaugarea de noi articole in meniu;
 *   - modificarea articolelor existente (nume, pret, disponibilitate);
 *   - stergerea articolelor.
 *
 * IMPORTANT - INTEGRAREA CU MODULUL COLEGULUI (punctul a):
 * Acest serviciu REUTILIZEAZA, fara sa le modifice, clasele:
 *   - Meniu               (agregatorul de produse, are adaugaProdus / getToateProdusele)
 *   - ProdusMeniu         (are setNume / setPret / setDisponibil)
 *   - MeniuRepository     (persistenta SQLite, are salveazaMeniu / incarcaMeniu)
 *
 * Deoarece MeniuRepository (punctul a) salveaza intregul meniu printr-o singura
 * metoda (salveazaMeniu), modificarile facute de manager se aplica pe obiectul
 * Meniu din memorie, iar persistarea se face prin persista(). Asa NU este nevoie
 * sa modificam codul colegului de la punctul a).
 */
public class ManagerService {

    private final Manager manager;
    private final Meniu meniu;
    private final MeniuRepository meniuRepository; // poate fi null daca nu vrem persistenta
    private boolean autentificat;

    public ManagerService(Manager manager, Meniu meniu, MeniuRepository meniuRepository) {
        if (manager == null || meniu == null) {
            throw new IllegalArgumentException("Manager si Meniu sunt obligatorii.");
        }
        this.manager = manager;
        this.meniu = meniu;
        this.meniuRepository = meniuRepository; // se accepta null pentru lucru doar in memorie / teste
        this.autentificat = false;
    }

    // ---------------- Autentificare ----------------

    public boolean autentifica(String email, String parola) {
        this.autentificat = manager.verificaCredentiale(email, parola);
        return this.autentificat;
    }

    public void deconecteaza() {
        this.autentificat = false;
    }

    public boolean esteAutentificat() {
        return autentificat;
    }

    private void verificaAutentificare() {
        if (!autentificat) {
            throw new IllegalStateException(
                "Managerul trebuie sa fie autentificat pentru aceasta operatie.");
        }
    }

    // ---------------- Gestionarea meniului ----------------

    /**
     * Adauga un produs nou in meniu. Produsul poate fi orice subtip de
     * ProdusMeniu (Aperitiv, FelPrincipal, BauturaSpirtoasa, BauturaNespirtoasa).
     */
    public void adaugaProdusInMeniu(ProdusMeniu produs) {
        verificaAutentificare();
        if (produs == null) {
            throw new IllegalArgumentException("Produsul nu poate fi null.");
        }
        meniu.adaugaProdus(produs);
    }

    /**
     * Modifica numele si pretul unui produs existent (identificat prin nume).
     * Returneaza true daca produsul a fost gasit si modificat.
     */
    public boolean modificaProdus(String numeExistent, String numeNou, double pretNou) {
        verificaAutentificare();
        ProdusMeniu produs = gasesteProdusDupaNume(numeExistent);
        if (produs == null) {
            return false;
        }
        produs.setNume(numeNou);
        produs.setPret(pretNou);
        return true;
    }

    /**
     * Activeaza / dezactiveaza disponibilitatea unui produs.
     */
    public boolean seteazaDisponibilitate(String nume, boolean disponibil) {
        verificaAutentificare();
        ProdusMeniu produs = gasesteProdusDupaNume(nume);
        if (produs == null) {
            return false;
        }
        produs.setDisponibil(disponibil);
        return true;
    }

    /**
     * Gaseste un produs dupa nume (case-insensitive). Returneaza null daca nu exista.
     */
    public ProdusMeniu gasesteProdusDupaNume(String nume) {
        if (nume == null) {
            return null;
        }
        for (ProdusMeniu p : meniu.getToateProdusele()) {
            if (p.getNume().equalsIgnoreCase(nume)) {
                return p;
            }
        }
        return null;
    }

    public List<ProdusMeniu> getToateProdusele() {
        return meniu.getToateProdusele();
    }

    /**
     * Persista intregul meniu in baza de date, folosind MeniuRepository
     * (modulul colegului de la punctul a). Daca repository-ul nu a fost furnizat
     * (null), operatia este ignorata - util pentru teste care ruleaza doar in memorie.
     */
    public void persista() {
        verificaAutentificare();
        if (meniuRepository != null) {
            meniuRepository.salveazaMeniu(meniu);
        }
    }

    // ---------------- Gestionarea personalului ----------------
    //
    // Conform enuntului, managerul adauga personalul. Aceasta operatie se face
    // in modulul colegului (restaurant.personal), prin:
    //   PersonalService.adaugaAngajat(username, parola, numeComplet)
    //
    // In aplicatia finala a echipei, dupa autentificarea managerului aici:
    //
    //   if (managerService.esteAutentificat()) {
    //       personalService.adaugaAngajat("ion", "parola123", "Ion Popescu");
    //   }
    //
    // Nu apelam direct ca sa nu cream o dependenta de compilare intre modulul
    // 'manager' si modulul 'personal'. Legatura se face in clasa principala (Main).
}
