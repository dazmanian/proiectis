package restaurant.manager.model;

/**
 * Managerul restaurantului (punctul f).
 *
 * Conform enuntului, managerul se autentifica cu datele:
 *   email:  admin@restaurant.null
 *   parola: adminRestaurantMagic12
 *
 * Aceasta clasa retine doar DATELE managerului (model). Logica de
 * autentificare si actiunile concrete (adaugare/modificare produse,
 * adaugare personal) sunt in ManagerService, ca sa pastram modelul curat
 * si separat de comportament (separarea responsabilitatilor).
 */
public class Manager {

    // Credentialele fixe din enunt. Le tinem aici ca si constante publice
    // ca sa fie usor de gasit si de reutilizat in service / teste.
    public static final String EMAIL_IMPLICIT = "admin@restaurant.null";
    public static final String PAROLA_IMPLICITA = "adminRestaurantMagic12";

    private final String email;
    private final String parola;

    /**
     * Creeaza managerul cu credentialele implicite din enunt.
     */
    public Manager() {
        this(EMAIL_IMPLICIT, PAROLA_IMPLICITA);
    }

    /**
     * Constructor general (util pentru teste sau extindere ulterioara).
     */
    public Manager(String email, String parola) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email-ul managerului nu poate fi gol.");
        }
        if (parola == null || parola.isBlank()) {
            throw new IllegalArgumentException("Parola managerului nu poate fi goala.");
        }
        this.email = email;
        this.parola = parola;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Verifica daca email-ul si parola introduse corespund acestui manager.
     */
    public boolean verificaCredentiale(String emailIntrodus, String parolaIntrodusa) {
        return this.email.equals(emailIntrodus) && this.parola.equals(parolaIntrodusa);
    }

    @Override
    public String toString() {
        return "Manager (" + email + ")";
    }
}
