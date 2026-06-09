package restaurant.personal.model;

public class Personal {

    private int id;
    private String username;
    private String parola;
    private String numeComplet;

    public Personal(String username, String parola, String numeComplet) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username-ul nu poate fi gol.");
        }
        if (parola == null || parola.length() < 4) {
            throw new IllegalArgumentException("Parola trebuie sa aiba cel putin 4 caractere.");
        }
        this.username = username.trim();
        this.parola = parola;
        this.numeComplet = (numeComplet != null) ? numeComplet.trim() : "";
    }

    public Personal(int id, String username, String parola, String numeComplet) {
        this(username, parola, numeComplet);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getParola() { return parola; }
    public String getNumeComplet() { return numeComplet; }

    public void setParola(String parolaNoua) {
        if (parolaNoua == null || parolaNoua.length() < 4) {
            throw new IllegalArgumentException("Parola trebuie sa aiba cel putin 4 caractere.");
        }
        this.parola = parolaNoua;
    }

    public void setNumeComplet(String numeComplet) {
        this.numeComplet = (numeComplet != null) ? numeComplet.trim() : "";
    }

    public boolean verificaParola(String parolaIntrodusa) {
        return this.parola.equals(parolaIntrodusa);
    }

    @Override
    public String toString() {
        return String.format("Personal #%d | Username: %s | Nume: %s",
            id, username, numeComplet.isBlank() ? "(nespecificat)" : numeComplet);
    }
}