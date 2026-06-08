# Modulul Manager (punctul f)

Implementeaza cerinta de la **punctul f**: managerul se autentifica
(admin@restaurant.null / adminRestaurantMagic12) si poate adauga sau modifica
articolele din meniu. Tot el adauga personalul (operatie delegata modulului
`personal`).

- **Limbaj:** Java 17+
- **Stocare:** SQLite (reutilizeaza `MeniuRepository` din modulul `meniu`)
- **Teste:** JUnit 5 (`ManagerServiceTest`)

## Structura

```
manager/
└── src/main/java/restaurant/manager/
    ├── model/Manager.java          - datele + credentialele managerului
    └── service/ManagerService.java - autentificare + actiuni pe meniu
└── src/test/java/restaurant/manager/
    └── ManagerServiceTest.java     - 11 teste
```

## Integrarea cu celelalte module

- **Cu modulul `meniu` (punctul a):** `ManagerService` primeste un `Meniu` si un
  `MeniuRepository` si lucreaza prin metodele lor publice. NU modifica codul
  colegului de la punctul a).
- **Cu modulul `personal` (punctul b):** adaugarea de personal se face apeland
  `PersonalService.adaugaAngajat(...)` din clasa principala a aplicatiei, dupa ce
  managerul s-a autentificat. Vezi comentariul de la finalul `ManagerService`.

---

## GHID DE PREZENTARE (ce sa spui la laborator)

Daca asistentul intreaba despre aceasta parte, iata ce face fiecare clasa:

**Manager.java** — retine email-ul si parola managerului (cele fixe din enunt,
puse ca si constante `EMAIL_IMPLICIT` / `PAROLA_IMPLICITA`). Metoda
`verificaCredentiale()` compara ce s-a introdus cu datele corecte. Am separat
DATELE (aici) de COMPORTAMENT (in service) — un principiu de design curat.

**ManagerService.java** — "creierul" managerului. Tine minte daca managerul e
autentificat (`autentificat`). Inainte de orice operatie importanta apeleaza
`verificaAutentificare()`, care arunca exceptie daca nu esti logat — asa nimeni
nu poate modifica meniul fara sa fie manager. Metodele principale:
  - `autentifica(email, parola)` - logare
  - `adaugaProdusInMeniu(produs)` - adauga un produs nou
  - `modificaProdus(nume, numeNou, pretNou)` - schimba nume + pret
  - `seteazaDisponibilitate(nume, disponibil)` - ascunde/arata un produs
  - `persista()` - salveaza meniul in baza de date prin MeniuRepository

**De ce nu modific clasele meniului?** Pentru ca punctul a) e responsabilitatea
altui coleg si e deja predat. Eu doar REUTILIZEZ clasele lui (`Meniu`,
`ProdusMeniu`, `MeniuRepository`) — asta arata o buna separare pe module.

**Intrebare posibila: "cum adauga managerul personal?"**
Raspuns: managerul deleaga asta modulului `personal`, apeland
`PersonalService.adaugaAngajat(...)`. Le legam in clasa principala a aplicatiei,
ca sa nu cuplam strans cele doua module.
