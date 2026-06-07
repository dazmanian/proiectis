# Proiect 5 - Restaurant | Punctul a): Structura Meniului

Modulul de **structura a meniului** (design OOP) din sistemul de management al
restaurantului. Acopera **strict punctul a)** din cerinte.

- **Limbaj:** Java 17+ (testat pe Java 21)
- **Stocare:** SQLite (prin driverul JDBC `org.xerial:sqlite-jdbc`)
- **Teste:** JUnit 5

## Ce acopera acest modul (punctul a)

Cele 4 categorii principale: aperitive, feluri principale, bauturi spirtoase,
bauturi nespirtoase. Produsele obligatorii din enunt sunt instantiate automat
prin `Meniu.incarcaProduseObligatorii()`:

- **Aperitive:** bruschete cu rosii, pesto, bruschete cu somon
- **Bauturi nespirtoase:** apa plata, apa minerala, limonada
- **Feluri principale:** cate un exemplu pentru fiecare subtip cerut
  (supa, carne, vegetarian, garnitura)

## Arhitectura claselor

```
ProdusMeniu (abstract)
├── Bautura (abstract) ── volumMl
│   ├── BauturaSpirtoasa     ── gradAlcool
│   └── BauturaNespirtoasa   ── carbogazoasa
└── Mancare (abstract) ── picant, vegetarian
    ├── Aperitiv
    └── FelPrincipal         ── tip: TipFelPrincipal

CategorieMeniu (enum)      - cele 4 categorii principale
TipFelPrincipal (enum)     - SUPA, CARNE, VEGETARIAN, GARNITURA
Meniu                      - agregator + fabrica de produse obligatorii
MeniuRepository            - persistenta SQLite (pattern DAO)
```

### Decizii de design
- **Clasa abstracta `ProdusMeniu`** (nu interfata): partajeaza implementare
  (constructor, getteri, validari), nu doar un contract.
- **`getCategorie()` abstracta + polimorfism:** fiecare subclasa returneaza
  categoria sa, fara `if`/`switch` pe tip in restul codului.
- **`FelPrincipal` foloseste un enum `tip`** in loc de inca 4 subclase: cele 4
  subtipuri difera doar ca eticheta, nu ca date sau comportament.
- **`MeniuRepository` (DAO)** izoleaza SQLite de model: clasele din `model` nu
  depind de baza de date. Daca echipa schimba stocarea, se modifica doar DAO-ul.
- **Campuri pregatite pentru colegi:** `disponibil` (punctul b), `picant` /
  `vegetarian` / `ingrediente` (punctul d). Eu nu implementez logica lor, dar
  structura le sustine.

## Rulare cu Maven

```bash
mvn test        # ruleaza testele JUnit 5
mvn compile     # compileaza sursele
```

## Rulare fara Maven (manual)

```bash
# 1. Descarca sqlite-jdbc.jar si junit-platform-console-standalone.jar in ./lib
# 2. Compileaza:
javac -d out -cp "lib/*" $(find src/main -name "*.java")
# 3. Compileaza si ruleaza testele:
javac -d out -cp "out:lib/*" src/test/java/restaurant/MeniuTest.java
java -jar lib/junit-platform-console-standalone.jar -cp out --scan-classpath
```

## Diagrama de secventa

`diagrama_secventa.puml` - scenariul "Popularea si afisarea meniului la
pornirea aplicatiei". Se importa in Visual Paradigm (Tools > Code > sau
plugin PlantUML) sau se randeaza pe https://www.plantuml.com/plantuml.
