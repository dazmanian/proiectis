package restaurant.comenzi;

/**
 * Reprezinta un chelner (membru al personalului restaurantului).
 *
 * Autentificarea completa si managementul personalului sunt in grija
 * colegului de la punctul b). Includem aceasta clasa in modulul comenzi
 * deoarece chelnerul este entitatea care:
 *   - actualizeaza statusul comenzilor (referinta folosita la punctul b)
 *   - confirma plata si semneaza chitanta (punctul e)
 *
 * Colegul de la punctul b) poate extinde aceasta clasa sau poate folosi
 * direct campurile existente (username + parola) pentru autentificare.
 *
 * NOTA securitate: parola ar trebui hash-uita (ex: BCrypt) intr-o aplicatie
 * reala. O lasam in clar pentru scopul acestui proiect.
 */
public class Chelner {

    private int id;
    private String nume;
    private String username;
    private String parola;

    public Chelner(String nume, String username, String parola) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Numele chelnerului nu poate fi gol.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username-ul nu poate fi gol.");
        }
        if (parola == null || parola.isBlank()) {
            throw new IllegalArgumentException("Parola nu poate fi goala.");
        }
        this.nume     = nume;
        this.username = username;
        this.parola   = parola;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) {
        if (nume == null || nume.isBlank())
            throw new IllegalArgumentException("Numele chelnerului nu poate fi gol.");
        this.nume = nume;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username-ul nu poate fi gol.");
        this.username = username;
    }

    public String getParola() { return parola; }
    public void setParola(String parola) {
        if (parola == null || parola.isBlank())
            throw new IllegalArgumentException("Parola nu poate fi goala.");
        this.parola = parola;
    }

    @Override
    public String toString() {
        return String.format("Chelner #%d: %s (@%s)", id, nume, username);
    }
}
