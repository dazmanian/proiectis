package restaurant.comenzi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Chitanta emisa de chelner la confirmarea platii (punctul e).
 *
 * Contine toate informatiile cerute:
 *   - numarChitanta: numarul unic, salvat in baza de date
 *   - comanda:       referinta la comanda platita (cu toate elementele)
 *   - chelner:       chelnerul care a confirmat plata
 *   - metodaPlata:   CASH sau CARD
 *   - totalPlata:    suma totala incasata (snapshot la momentul platii)
 *   - dataEmitere:   data si ora la care s-a emis chitanta
 *
 * Design decision - doua constructori:
 *   - Constructorul principal (business): calculeaza totalul din comanda.
 *   - Constructorul de reconstructie (DB): preia totalul direct din baza de date,
 *     pentru a evita divergente daca logica de calcul s-ar schimba in viitor.
 */
public class Chitanta {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private int id;
    private int numarChitanta;
    private Comanda comanda;
    private Chelner chelner;
    private MetodaPlata metodaPlata;
    private double totalPlata;
    private LocalDateTime dataEmitere;

    /**
     * Constructor principal - folosit la emiterea unei chitante noi.
     * Totalul este calculat automat din comanda.
     */
    public Chitanta(Comanda comanda, Chelner chelner, MetodaPlata metodaPlata) {
        if (comanda == null)    throw new IllegalArgumentException("Comanda nu poate fi null.");
        if (chelner == null)    throw new IllegalArgumentException("Chelnerul nu poate fi null.");
        if (metodaPlata == null) throw new IllegalArgumentException("Metoda de plata nu poate fi null.");

        this.comanda     = comanda;
        this.chelner     = chelner;
        this.metodaPlata = metodaPlata;
        this.totalPlata  = comanda.calculeazaTotal();
        this.dataEmitere = LocalDateTime.now();
    }

    /**
     * Constructor pentru reconstructia din baza de date (folosit in ChitantaRepository).
     * Preia totalul stocat in DB in loc sa il recalculeze.
     */
    public Chitanta(int numarChitanta, Comanda comanda, Chelner chelner,
                    MetodaPlata metodaPlata, double totalPlata, LocalDateTime dataEmitere) {
        if (comanda == null)    throw new IllegalArgumentException("Comanda nu poate fi null.");
        if (chelner == null)    throw new IllegalArgumentException("Chelnerul nu poate fi null.");
        if (metodaPlata == null) throw new IllegalArgumentException("Metoda de plata nu poate fi null.");
        if (totalPlata < 0)     throw new IllegalArgumentException("Totalul nu poate fi negativ.");

        this.numarChitanta = numarChitanta;
        this.comanda       = comanda;
        this.chelner       = chelner;
        this.metodaPlata   = metodaPlata;
        this.totalPlata    = totalPlata;
        this.dataEmitere   = dataEmitere;
    }

    /**
     * Genereaza reprezentarea textuala completa a chitantei.
     * Poate fi folosita pentru afisare pe ecran sau pentru printare.
     */
    public String genereazaTextChitanta() {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("          RESTAURANT - CHITANTA\n");
        sb.append("==========================================\n");
        sb.append(String.format("  Nr. chitanta : #%d%n", numarChitanta));
        sb.append(String.format("  Data emitere : %s%n", dataEmitere.format(FORMATTER)));
        sb.append(String.format("  Masa         : %d%n", comanda.getNumarMasa()));
        sb.append(String.format("  Client       : %s%n", comanda.getNumeClient()));
        sb.append(String.format("  Chelner      : %s%n", chelner.getNume()));
        sb.append("------------------------------------------\n");
        for (ElementComanda e : comanda.getElemente()) {
            sb.append(String.format("  %-22s x%-3d %6.2f RON%n",
                    e.getNumeProdus(), e.getCantitate(), e.getSubtotal()));
        }
        sb.append("------------------------------------------\n");
        sb.append(String.format("  TOTAL         :          %7.2f RON%n", totalPlata));
        sb.append(String.format("  Metoda plata  : %s%n", metodaPlata.getDenumire()));
        sb.append("==========================================\n");
        sb.append("       Va multumim! Pofta buna!\n");
        sb.append("==========================================\n");
        return sb.toString();
    }

    // --- Getteri si setteri ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumarChitanta() { return numarChitanta; }
    public void setNumarChitanta(int numarChitanta) {
        if (numarChitanta <= 0)
            throw new IllegalArgumentException("Numarul chitantei trebuie sa fie pozitiv.");
        this.numarChitanta = numarChitanta;
    }

    public Comanda getComanda()         { return comanda; }
    public Chelner getChelner()         { return chelner; }
    public MetodaPlata getMetodaPlata() { return metodaPlata; }
    public double getTotalPlata()       { return totalPlata; }
    public LocalDateTime getDataEmitere() { return dataEmitere; }
    public void setDataEmitere(LocalDateTime dataEmitere) { this.dataEmitere = dataEmitere; }
}
