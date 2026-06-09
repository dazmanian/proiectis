# Aplicatia integrata - Sistem management restaurant

Aceasta este versiunea INTEGRATA a proiectului: toate cele 5 module ale echipei
legate intr-o singura aplicatie care ruleaza, cu o **consola interactiva**
(`Main.java`) prin care utilizatorul navigheaza tastand cifre.

## Cum se ruleaza

ATENTIE: aplicatia citeste de la tastatura, deci trebuie rulata intr-un
terminal real (nu cu butonul Run din unele configurari VS Code).

### Windows (PowerShell)
```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out restaurant.Main
```

### Windows (Command Prompt / CMD)
```bat
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out restaurant.Main
```

### Mac / Linux
```bash
javac -d out $(find src -name "*.java")
java -cp out restaurant.Main
```

## Meniul consolei interactive

La rulare poti naviga tastand cifre:

  1. Vizualizeaza meniul (pe cele 4 categorii)
  2. Informatii despre un produs (pop-up cu ingrediente, vegetarian/picant)
  3. Adauga un produs in cos
  4. Vezi cosul
  5. Plaseaza comanda (alegi masa, nume, metoda de plata -> primesti chitanta)
  6. Zona manager (autentificare, apoi administrarea meniului)
  0. Iesire

Credentiale manager (din enunt):
  email:  admin@restaurant.null
  parola: adminRestaurantMagic12

## Structura

Toate modulele sunt sub un singur arbore de pachete `restaurant.*`:

```
src/restaurant/
├── Main.java                  - consola interactiva, leaga toate modulele
├── model/        (a) meniu    - ProdusMeniu, Meniu, enum-uri
├── repository/   (a) meniu    - MeniuRepository (SQLite)
├── client/       (c, d)       - CosCumparaturi, ArticolCos, PopUpLegislativ
├── comenzi/      (e)          - Comanda, Chelner, Chitanta, plata
├── personal/     (b)          - Personal, PersonalService
└── manager/      (f)          - Manager, ManagerService
```

Cum colaboreaza modulele in consola:
  [a] meniul ofera produsele -> [c] clientul le pune in cos ->
  [d] poate cere info (pop-up) -> [c->e] din cos se creeaza Comanda ->
  [b] statusul comenzii trece prin preparare/servita ->
  [e] chelnerul incaseaza si emite chitanta.
  [f] managerul (autentificat) administreaza meniul.

## Ce a fost nevoie pentru integrare (de stiut la prezentare)

Modulele au fost dezvoltate independent, deci au existat cateva conflicte
rezolvate la integrare:

1. **Conflict de pachete:** modulul `personal` isi pusese clasele `Comanda` si
   `StatusComanda` in pachetul `restaurant.model` (pachetul modulului `meniu`).
   La integrare au fost mutate in `restaurant.personal.model`, ca sa nu intre in
   coliziune cu modulul meniu.

2. **Doua modele de comanda:** atat `personal` cat si `comenzi` aveau propria
   clasa `Comanda`. In aplicatia integrata, fluxul principal foloseste `Comanda`
   din modulul `comenzi` (mai completa, cu status PLATITA si plata). Modulul
   `personal` isi pastreaza propriul model pentru functiile lui interne.

3. **Persistenta (SQLite):** fiecare modul are propriul Repository pentru SQLite.
   Consola ruleaza cu obiectele in memorie, ca sa functioneze si fara driverul
   sqlite-jdbc instalat. Pentru salvare reala in baza de date, se adauga driverul
   in classpath si se activeaza apelurile catre Repository.

## Nota

Folderele `out/` si `sources.txt` sunt generate la compilare si NU trebuie urcate
pe GitHub (sunt rezultate, nu cod sursa).
