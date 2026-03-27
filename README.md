# FootballHub

FootballHub is een webapplicatie voor voetballiefhebbers en recreatieve spelers.
Gebruikers kunnen voetbalclubs, spelers en stadions ontdekken en een stadium reserveren.
Admins kunnen clubs, spelers, stadions en reservaties beheren.

---

## Inhoud

* [Productvisie](#productvisie)
* [Productbeschrijving](#productbeschrijving)
* [Feedback verwerkt](#feedback-verwerkt)
* [User Story Map](#user-story-map)
* [Personas](#personas)
* [Conceptueel model](#conceptueel-model)
* [Functionele analyse](#functionele-analyse)
* [Wireframes](#wireframes)
* [Use Case Diagrammen](#use-case-diagrammen)
* [Activity Diagrammen](#activity-diagrammen)
* [Sequence Diagram](#sequence-diagram)
* [Class Diagram](#class-diagram)
* [State Transition Diagram](#state-transition-diagram)

---

# Productvisie

FootballHub wil een centraal platform zijn waar gebruikers eenvoudig informatie kunnen vinden over voetbalclubs, spelers en stadions, én waar ze op een gebruiksvriendelijke manier een stadium kunnen reserveren.

De applicatie combineert informatieve content met praktische functionaliteit via een reservatiesysteem. Voor admins biedt het platform ook een centrale beheeromgeving.

---

# Productbeschrijving

## Wat?

FootballHub is een webapplicatie waarin gebruikers:

* voetbalclubs kunnen bekijken
* spelers kunnen bekijken en filteren
* stadions kunnen bekijken
* een stadium kunnen reserveren op datum en tijd

Admins kunnen daarnaast:

* clubs beheren
* spelers beheren
* stadions beheren
* alle reservaties bekijken en verwijderen

## Voor wie?

FootballHub is bedoeld voor:

* recreatieve voetballers of vriendengroepen die een stadium willen huren
* voetbalfans die informatie willen bekijken over clubs en spelers
* beheerders/admins die het platform beheren

## Waarom?

FootballHub centraliseert informatie en reservaties in één platform.

**Voordelen voor gebruikers:**

* snel een stadium vinden en reserveren
* overzicht van eigen reservaties
* voetbalinformatie op één plek

**Voordelen voor beheerder:**

* eenvoudige centrale administratie van clubs, spelers, stadions en reservaties
* minder manueel werk
* minder fouten door automatische validaties zoals reservatieconflicten

---

# Feedback verwerkt

Tijdens de tussentijdse voorstelling en lessen werd de analyse verder verfijnd.

## Verwerkte feedback

* De **User Story Map** werd duidelijker opgesplitst in backbone, activiteiten en drie slices: **MVP**, **must-have** en **nice-to-have**.
* De **persona’s** werden concreter gemaakt en beter gekoppeld aan echte gebruikersdoelen.
* De **wireframes** werden aangepast zodat de reservatieflow duidelijker zichtbaar is.
* De **Use Case Diagrammen** werden opgesplitst in twee delen voor betere leesbaarheid:

    * gast + gebruiker
    * admin
* De **reservatieflow** werd extra uitgewerkt omdat dit de belangrijkste business flow van de applicatie is.

---

# User Story Map

![User Story Map](docs/images/user-story-map.png)

## Backbone

* Ontdekken
* Account beheren
* Reserveren
* Beheren (admin)

## Activiteiten en user stories

### Ontdekken

* Als gast wil ik clubs bekijken zodat ik informatie kan ontdekken.
* Als gast wil ik spelers bekijken zodat ik spelers kan ontdekken.
* Als gast wil ik spelers kunnen filteren zodat ik sneller relevante spelers vind.
* Als gast wil ik stadions bekijken zodat ik een geschikt stadium kan kiezen.

### Account beheren

* Als bezoeker wil ik me registreren zodat ik reservaties kan maken.
* Als gebruiker wil ik kunnen inloggen zodat ik toegang krijg tot mijn reservaties.

### Reserveren

* Als gebruiker wil ik een stadium kunnen kiezen zodat ik een reservatie kan maken.
* Als gebruiker wil ik datum, startuur en duur kunnen kiezen zodat ik mijn reservatie kan plannen.
* Als gebruiker wil ik geen overlappende reservatie kunnen maken zodat dubbele boekingen vermeden worden.
* Als gebruiker wil ik mijn reservaties kunnen bekijken zodat ik overzicht heb.
* Als gebruiker wil ik mijn reservatie kunnen verwijderen zodat ik flexibel blijf.

### Beheren (admin)

* Als admin wil ik clubs kunnen beheren.
* Als admin wil ik spelers kunnen beheren.
* Als admin wil ik stadions kunnen beheren.
* Als admin wil ik alle reservaties kunnen bekijken.

## Slices

### MVP

* clubs, spelers en stadions bekijken
* registreren en inloggen
* reservatie maken
* mijn reservaties bekijken

### Must-have

* spelers filteren
* reservatievalidatie
* overlapcontrole
* admin CRUD voor clubs, spelers en stadions
* admin overzicht van alle reservaties

### Nice-to-have

* reservatiestatussen
* reservatiehistoriek
* notificaties

---

# Personas

## Persona 1 – Milan (recreatieve speler)

**Leeftijd:** 22 jaar
**Profiel:** Student die wekelijks met vrienden voetbal speelt.
**Doel:** Snel en eenvoudig een stadium reserveren.

### Behoeften

* duidelijke reservatieflow
* overzicht van eigen reservaties
* duidelijke prijsinformatie

### Pijnpunten

* manueel reserveren is omslachtig
* miscommunicatie over reservaties

---

## Persona 2 – Sarah (voetbalfan)

**Leeftijd:** 28 jaar
**Profiel:** Voetbalfan die graag clubs en spelers bekijkt.
**Doel:** Informatie over clubs, spelers en stadions op één plek vinden.

### Behoeften

* overzichtelijke lijsten
* filters
* duidelijke detailpagina’s

### Pijnpunten

* informatie staat vaak verspreid over verschillende websites

---

## Persona 3 – Tom (admin)

**Leeftijd:** 35 jaar
**Profiel:** Beheerder van het platform.
**Doel:** Clubs, spelers, stadions en reservaties efficiënt beheren.

### Behoeften

* eenvoudige beheerformulieren
* overzicht van alle reservaties
* centrale administratie

### Pijnpunten

* manuele administratie kost tijd
* fouten in data zorgen voor verwarring

---

# Conceptueel model

## Belangrijkste concepten

### Club

Een voetbalclub met naam, stad, stadion en oprichtingsjaar.

### Player

Een speler met naam, positie, leeftijd, nationaliteit en een gekoppelde club.

### Stadium

Een stadium dat gereserveerd kan worden. Bevat naam, stad, capaciteit, prijs per uur en beschrijving.

### Reservation

Een reservatie van een stadium door een gebruiker op een bepaalde datum en tijd, met duur en totale prijs.

### SiteUser

Een geregistreerde gebruiker van het systeem met een gebruikersnaam, wachtwoord en rol.

## Relaties

* Een **Club** heeft meerdere **Players**
* Een **Player** behoort tot exact één **Club**
* Een **SiteUser** heeft meerdere **Reservations**
* Een **Reservation** behoort tot exact één **SiteUser**
* Een **Stadium** heeft meerdere **Reservations**
* Een **Reservation** behoort tot exact één **Stadium**

![Conceptueel Model](docs/images/conceptueel-model.png)

---

# Functionele analyse

## 1. Authenticatie

* gebruiker kan registreren
* gebruiker kan inloggen
* gebruiker kan uitloggen
* toegang is rolgebaseerd (`ROLE_USER`, `ROLE_ADMIN`)

## 2. Publieke informatie

* clubs bekijken
* clubdetails bekijken
* spelers bekijken
* spelers filteren op zoekwoord, club, positie en leeftijd
* spelerdetails bekijken
* stadions bekijken
* stadiumdetails bekijken

## 3. Reservaties

* gebruiker kiest stadium
* gebruiker kiest datum, startuur en duur
* systeem valideert invoer
* systeem controleert overlap met bestaande reservaties
* systeem berekent totale prijs
* gebruiker ziet eigen reservaties
* gebruiker kan eigen reservatie verwijderen

## 4. Admin beheer

* clubs aanmaken, aanpassen en verwijderen
* spelers aanmaken, aanpassen en verwijderen
* stadions aanmaken, aanpassen en verwijderen
* alle reservaties bekijken
* reservaties verwijderen

---

# Wireframes

## Wireframe 1 – Homepagina

![Wireframe Home](docs/images/wireframe-home.png)

### Korte uitleg

De homepagina is het startpunt van de applicatie.
Van hieruit kan de gebruiker navigeren naar clubs, spelers en stadions.

---

## Wireframe 2 – Reservatiepagina

![Wireframe Reservation](docs/images/wireframe-reservation.png)

### Korte uitleg

De reservatiepagina is een van de belangrijkste schermen van de applicatie.
Hier kiest een gebruiker een stadium, datum, startuur en duur om een reservatie te maken.

---

# Use Case Diagrammen

## Use Case Diagram 1 – Gast en gebruiker

![Use Case User](docs/images/use-case-user.png)

## Use Case Diagram 2 – Admin

![Use Case Admin](docs/images/use-case-admin.png)

---

# Activity Diagrammen

## Activity Diagram 1 – Reservatie maken

![Activity Reservation](docs/images/activity-reservation.png)

## Activity Diagram 2 – Admin beheert stadium

![Activity Admin Stadium](docs/images/activity-admin-stadium.png)

---

# Sequence Diagram

## Sequence Diagram – Reservatie maken

![Sequence Reservation](docs/images/sequence-reservation.png)

---

# Class Diagram

![Class Diagram](docs/images/class-diagram.png)

---

# State Transition Diagram

## State Transition Diagram – Reservation

![State Reservation](docs/images/state-reservation.png)
