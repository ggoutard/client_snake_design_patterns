# Java Networked Snake - Design Patterns et Architecture

Ce depot contient l'application Client d'une architecture Client/Serveur concue pour un jeu Snake multijoueur. Developpe strictement autour des principes de la programmation orientee objet, ce projet demontre une comprehension approfondie de l'ingenierie logicielle, de la programmation reseau robuste et des patrons de conception (Design Patterns).

Vous pouvez tester l'emulateur interactif directement dans le navigateur (aucune installation Java requise) :

[TESTER LA DEMONSTRATION WEB ICI](http://localhost:5500/client_snake_design_patterns/)

---

## Vue d'ensemble de l'Architecture

L'application separe strictement les responsabilites en utilisant une approche MVC (Modele-Vue-Controleur) sur mesure, renforcee par des patrons de conception standards de l'industrie pour garantir la modularite, l'extensibilite et la maintenabilite.

### Patrons de Conception (Design Patterns) Impliques

1. Patron Fabrique (Factory) dans gameElement/fabrique/
   - Centralise l'instanciation des entites du jeu (Snake, Item, MysteryBox).
   - Garantit que l'ajout de nouveaux elements de jeu ne necessite aucune modification de la logique de base, respectant strictement le principe Ouvert/Ferme (Open/Closed Principle).

2. Patron Etat (State) dans etat/
   - Gere le cycle de vie du jeu (EtatPause, EtatRunning).
   - Elimine les boucles de verification d'etat complexes (if/else), en encapsulant les comportements specifiques a chaque etat de maniere elegante.

3. Patron Strategie (Strategy) dans movement/strategie/
   - Decouple la logique de mouvement des entites.
   - Permet de basculer dynamiquement entre AiMovement (Bots), HumanMovement (Joueurs) et des postures specifiques de l'intelligence artificielle (ModeAttaque, ModeDefense) en cours d'execution.

4. Patron Observateur (Observer)
   - Etablit un flux de donnees reactif entre la couche reseau (ThreadInput/ThreadOutput), le Controleur et la Vue.
   - Garantit que l'interface utilisateur reste fluide et se met a jour automatiquement lors de la reception de donnees JSON depuis le Serveur.

---

## Protocole Reseau (Client - Serveur)

L'application communique via des Sockets TCP en utilisant un protocole JSON serialise (gere par Jackson).
- Synchronisation de l'Etat : Le serveur agit comme la source de verite faisant autorite, calculant la physique et diffusant l'etat du plateau.
- DTO (Data Transfer Objects) : Le client recoit un objet PanelBuilder contenant des tableaux de FeaturesSnake et FeaturesItem, ce qui permet un rendu dynamique de l'etat exact.

Remarque : La logique dediee au Serveur reside dans un depot distinct afin d'imposer une frontiere stricte entre la presentation et la validation de l'etat ou de la physique.

---

## Demarrage (Developpement Java)

### Prerequis
- Java 23 ou superieur
- Maven

### Compilation et Execution
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="tp1progreseau.ClientSnake"
```

Ce projet met en evidence les capacites modernes de Java, une conception architecturale rigoureuse et la capacite a construire des applications de bureau en reseau reactives.
