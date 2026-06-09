# Proiect 5 - Sistem de management pentru restaurant

Proiect de echipa la disciplina **Ingineria Software**.
Sistem de management pentru un restaurant, dezvoltat in **Java**, cu stocare in
**SQLite**.

## Echipa si impartirea pe cerinte

Proiectul este structurat pe module, fiecare corespunzand unei cerinte din enunt:

| Cerinta | Modul | Descriere |
|---------|-------|-----------|
| a) | `meniu/` | Structura meniului: produse, categorii, persistenta |
| b) | `personal/` | Personalul: autentificare, gestiunea comenzilor |
| c) | `client/` | Vizualizare meniu, cos de cumparaturi |
| d) | `client/` | Pop-up cu informatii despre produse (ingrediente, alergeni) |
| e) | `comenzi/` | Comenzi, chelner, plata, chitanta |
| f) | `modul_manager/` | Managerul: administrarea meniului si a personalului |

In plus:

| Folder | Descriere |
|--------|-----------|
| `app-integrata/` | Aplicatia care leaga toate modulele intr-o consola interactiva ce ruleaza |

## Structura proiectului

```
proiectis/
├── meniu/              (a) structura meniului
├── personal/           (b) personal + comenzi
├── client/             (c, d) cos + pop-up info
├── comenzi/            (e) comenzi, plata, chitanta
├── modul_manager/      (f) administrare manager
├── app-integrata/      aplicatia integrata (consola interactiva)
└── README.md           (acest fisier)
```

## Tehnologii

- **Limbaj:** Java 17+
- **Stocare:** SQLite (prin driverul JDBC org.xerial:sqlite-jdbc)
- **Teste:** JUnit 5
- **Modelare:** diagrame de clase si de secventa (Visual Paradigm / PlantUML)

## Cum se ruleaza aplicatia integrata

Aplicatia completa, care demonstreaza colaborarea tuturor modulelor, se afla in
folderul `app-integrata/`. Vezi `app-integrata/README.md` pentru instructiuni
detaliate de rulare. Pe scurt (Windows PowerShell):

```powershell
cd app-integrata
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out restaurant.Main
```

Credentiale manager: `admin@restaurant.null` / `adminRestaurantMagic12`

## Rularea testelor

Fiecare modul are propriul set de teste in `src/test/`. Modulul `meniu`, de
exemplu, are 17 teste JUnit 5 in `meniu/src/test/java/restaurant/MeniuTest.java`.

## Note despre integrare

Modulele au fost dezvoltate independent de fiecare membru. La integrare au fost
rezolvate cateva conflicte (pachete suprapuse, modele duplicate de comanda) -
detalii in `app-integrata/README.md`.
