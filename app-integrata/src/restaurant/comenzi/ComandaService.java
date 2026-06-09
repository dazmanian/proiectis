package restaurant.comenzi;

import restaurant.repository.ChelnerRepository;
import restaurant.repository.ChitantaRepository;
import restaurant.repository.ComandaRepository;

import java.util.List;

/**
 * Serviciu care orchestreaza fluxul complet al punctului e):
 *
 *   1. Plasarea comenzii confirmate de client
 *      -> plasaComanda(Comanda)
 *
 *   2. Vizualizarea comenzilor (pentru UI-ul personalului / punctul b)
 *      -> getToateComenzile()
 *      -> getComanda(int id)
 *
 *   3. Actualizarea statusului comenzii
 *      -> actualizeazaStatus(Comanda, StatusComanda)
 *      -> actualizeazaTimpEstimat(Comanda, int)
 *
 *   4. Procesul de plata (initiata de chelner)
 *      -> proceseazaPlata(Comanda, MetodaPlata, Chelner)
 *      -> returneaza Chitanta emisa si salvata in DB
 *
 * Design decision - doua constructori:
 *   - Constructorul cu String (calea DB): pentru uz normal in aplicatie.
 *     Apeleaza initializeazaSchema() automat.
 *   - Constructorul cu injectie de dependente: pentru teste unitare,
 *     permite injectarea de mock-uri sau repository-uri cu DB temporar.
 */
public class ComandaService {

    private final ComandaRepository  comandaRepo;
    private final ChitantaRepository chitantaRepo;
    private final ChelnerRepository  chelnerRepo;

    /** Constructor pentru uz normal in aplicatie. */
    public ComandaService(String caleFisierDb) {
        this.comandaRepo  = new ComandaRepository(caleFisierDb);
        this.chitantaRepo = new ChitantaRepository(caleFisierDb);
        this.chelnerRepo  = new ChelnerRepository(caleFisierDb);
        initializeazaSchema();
    }

    /** Constructor cu injectie de dependente (pentru teste). */
    public ComandaService(ComandaRepository  comandaRepo,
                          ChitantaRepository chitantaRepo,
                          ChelnerRepository  chelnerRepo) {
        this.comandaRepo  = comandaRepo;
        this.chitantaRepo = chitantaRepo;
        this.chelnerRepo  = chelnerRepo;
    }

    private void initializeazaSchema() {
        comandaRepo.initializeazaSchema();
        chitantaRepo.initializeazaSchema();
        chelnerRepo.initializeazaSchema();
    }

    // -------------------------------------------------------------------------
    //  1. Plasarea comenzii
    // -------------------------------------------------------------------------

    /**
     * Plaseaza o comanda confirmata de client in baza de date.
     * Comanda trebuie sa aiba cel putin un element; statusul initial
     * este IN_ASTEPTARE (setat automat in constructorul Comanda).
     *
     * @throws IllegalStateException daca comanda nu contine niciun produs
     */
    public void plasaComanda(Comanda comanda) {
        if (comanda.getNrElemente() == 0) {
            throw new IllegalStateException(
                "Nu se poate plasa o comanda fara produse. Adaugati cel putin un produs.");
        }
        comandaRepo.salveazaComanda(comanda);
    }

    // -------------------------------------------------------------------------
    //  2. Vizualizarea comenzilor
    // -------------------------------------------------------------------------

    /** Returneaza toate comenzile, cu elementele incarcate. */
    public List<Comanda> getToateComenzile() {
        return comandaRepo.incarcaToateComenzile();
    }

    /**
     * Returneaza o comanda dupa id, cu elementele incarcate.
     * Returneaza null daca nu exista.
     */
    public Comanda getComanda(int id) {
        return comandaRepo.incarcaComanda(id);
    }

    // -------------------------------------------------------------------------
    //  3. Actualizarea statusului (folosit si de punctul b)
    // -------------------------------------------------------------------------

    /**
     * Actualizeaza statusul comenzii atat in memorie cat si in baza de date.
     */
    public void actualizeazaStatus(Comanda comanda, StatusComanda status) {
        comanda.setStatus(status);
        comandaRepo.actualizeazaStatus(comanda.getId(), status);
    }

    /**
     * Seteaza timpul estimat al comenzii (apelat de chelner la preluare).
     */
    public void actualizeazaTimpEstimat(Comanda comanda, int timpMinute) {
        comanda.setTimpEstimatMinute(timpMinute);
        comandaRepo.actualizeazaTimpEstimat(comanda.getId(), timpMinute);
    }

    // -------------------------------------------------------------------------
    //  4. Procesul de plata
    // -------------------------------------------------------------------------

    /**
     * Proceseaza plata unei comenzi si emite chitanta.
     *
     * Precondition: comanda.getStatus() == SERVITA.
     * Postcondition: comanda.getStatus() == PLATITA; chitanta salvata in DB.
     *
     * @param comanda     comanda de platit
     * @param metodaPlata CASH sau CARD
     * @param chelner     chelnerul care confirma plata (trebuie sa aiba id > 0)
     * @return chitanta emisa, cu numarChitanta si id setate din DB
     * @throws IllegalStateException daca comanda nu este in starea SERVITA
     *                               sau chelnerul nu este inregistrat in sistem
     */
    public Chitanta proceseazaPlata(Comanda comanda, MetodaPlata metodaPlata, Chelner chelner) {
        if (comanda.getStatus() != StatusComanda.SERVITA) {
            throw new IllegalStateException(
                "Plata poate fi procesata doar pentru comenzi cu statusul SERVITA. " +
                "Statusul curent: " + comanda.getStatus().getDenumire());
        }
        if (chelner.getId() <= 0) {
            throw new IllegalStateException(
                "Chelnerul trebuie sa fie inregistrat in sistem (id > 0). " +
                "Salvati chelnerul prin ChelnerRepository inainte de a procesa plata.");
        }

        // Tranzitie de stare: SERVITA -> PLATITA
        actualizeazaStatus(comanda, StatusComanda.PLATITA);

        // Emite si salveaza chitanta
        Chitanta chitanta = new Chitanta(comanda, chelner, metodaPlata);
        chitantaRepo.salveazaChitanta(chitanta);
        return chitanta;
    }

    // -------------------------------------------------------------------------
    //  Expunere repository chelneri (pentru colegul de la punctul b)
    // -------------------------------------------------------------------------

    /** Returneaza repository-ul de chelneri, pentru a fi folosit de punctul b. */
    public ChelnerRepository getChelnerRepository() {
        return chelnerRepo;
    }
}
