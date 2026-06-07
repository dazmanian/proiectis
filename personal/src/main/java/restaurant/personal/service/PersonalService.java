package restaurant.personal.service;

import restaurant.model.Comanda;
import restaurant.model.StatusComanda;
import restaurant.personal.model.Personal;
import restaurant.personal.repository.ComandaRepository;
import restaurant.personal.repository.PersonalRepository;

import java.util.List;

public class PersonalService {

    private final PersonalRepository personalRepo;
    private final ComandaRepository comandaRepo;
    private Personal angajatAutentificat;

    public PersonalService(PersonalRepository personalRepo,
                           ComandaRepository comandaRepo) {
        this.personalRepo = personalRepo;
        this.comandaRepo  = comandaRepo;
    }

    public boolean autentifica(String username, String parola) {
        Personal gasit = personalRepo.gasesteDupaUsername(username);
        if (gasit != null && gasit.verificaParola(parola)) {
            this.angajatAutentificat = gasit;
            return true;
        }
        return false;
    }

    public void deconecteaza() {
        this.angajatAutentificat = null;
    }

    public Personal getAngajatAutentificat() {
        return angajatAutentificat;
    }

    public boolean esteAutentificat() {
        return angajatAutentificat != null;
    }

    public List<Comanda> getComenziNefinalizate() {
        verificaAutentificare();
        return comandaRepo.getComenziNefinalizate();
    }

    public void preiaComanda(int idComanda, int timpEstimatMinute) {
        verificaAutentificare();
        if (timpEstimatMinute <= 0) {
            throw new IllegalArgumentException("Timpul estimat trebuie sa fie un numar pozitiv de minute.");
        }
        Comanda comanda = getComandaValidata(idComanda);
        if (comanda.getStatus() != StatusComanda.IN_ASTEPTARE) {
            throw new IllegalStateException(
                "Comanda #" + idComanda + " nu este in starea IN_ASTEPTARE. " +
                "Status curent: " + comanda.getStatus());
        }
        comanda.setStatus(StatusComanda.PREPARARE);
        comanda.setTimpEstimatMinute(timpEstimatMinute);
        comandaRepo.actualizeaza(comanda);
    }

    public void actualizeazaStatus(int idComanda, StatusComanda statusNou) {
        verificaAutentificare();
        Comanda comanda = getComandaValidata(idComanda);
        StatusComanda statusCurent = comanda.getStatus();
        if (!tranzitieValida(statusCurent, statusNou)) {
            throw new IllegalStateException(
                "Tranzitie invalida: " + statusCurent + " -> " + statusNou +
                ". Ordinea permisa este: IN_ASTEPTARE -> PREPARARE -> SERVITA.");
        }
        comanda.setStatus(statusNou);
        comandaRepo.actualizeaza(comanda);
    }

    private boolean tranzitieValida(StatusComanda de_la, StatusComanda la) {
        return switch (de_la) {
            case IN_ASTEPTARE -> la == StatusComanda.PREPARARE;
            case PREPARARE    -> la == StatusComanda.SERVITA;
            case SERVITA      -> false;
        };
    }

    public void adaugaAngajat(String username, String parola, String numeComplet) {
        Personal angajatNou = new Personal(username, parola, numeComplet);
        personalRepo.adauga(angajatNou);
    }

    public boolean stergeAngajat(String username) {
        return personalRepo.sterge(username);
    }

    public List<Personal> getTotiAngajatii() {
        return personalRepo.getTotiAngajatii();
    }

    private void verificaAutentificare() {
        if (!esteAutentificat()) {
            throw new IllegalStateException("Aceasta operatie necesita autentificare.");
        }
    }

    private Comanda getComandaValidata(int idComanda) {
        Comanda comanda = comandaRepo.gasesteDupaId(idComanda);
        if (comanda == null) {
            throw new IllegalArgumentException("Nu exista nicio comanda cu id-ul " + idComanda + ".");
        }
        return comanda;
    }
}