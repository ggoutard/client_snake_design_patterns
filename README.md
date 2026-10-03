# Java Networked Snake - Design Patterns et Architecture

Ce depot contient l'application Client d'une architecture Client/Serveur concue pour un jeu Snake multijoueur. Developpe strictement autour des principes de la programmation orientee objet, ce projet demontre une comprehension approfondie de l'ingenierie logicielle, de la programmation reseau robuste et des patrons de conception (Design Patterns).

Vous pouvez consulter la documentation en ligne du projet, découvrir le backend serveur et tester l'emulateur interactif directement dans le navigateur (aucune installation Java requise) :

[CONSULTER L'ARCHITECTURE CLIENT EN LIGNE](https://ggoutard.github.io/client_snake_design_patterns/)

[CONSULTER L'ARCHITECTURE SERVEUR JAVA EE](https://ayluc.github.io/web_avance_snake/)

[TESTER LE SIMULATEUR DE JEU INTERACTIF EN LIGNE](https://ggoutard.github.io/client_snake_design_patterns/simulator.html)

> **A propos du simulateur** : ce n'est pas une copie du jeu en JavaScript, c'est le vrai code Java
> du projet qui tourne dans la page.
>
> **Comment il a ete construit** : on a repris les classes utiles du client (modele, Fabrique, Etat,
> Strategie, Observateur, rendu `PanelSnakeGame`), sans la partie reseau, et on les a adaptees a
> Java 8. Un nouveau point d'entree, `SnakeWeb`, reunit tout dans une seule fenetre Swing : un accueil
> simplifie (carte, murs, bot), la barre de commandes inspiree de `ViewCommand` et le plateau. Le tout
> est compile en un `.jar`, que la page fait executer par une JVM embarquee dans le simulateur :
> aucune installation, rien a lancer cote serveur.
>
> **Utilisation** : choisissez la carte, les murs et la presence du bot (IA du patron Strategie), puis
> jouez avec les fleches ou ZQSD. La barre Swing permet de redemarrer, mettre en pause, avancer pas a
> pas et regler la vitesse ; le bouton Plein ecran agrandit le jeu. Le premier chargement prend
> quelques secondes. Details dans la section [Version web](#version-web--le-vrai-code-java-dans-le-navigateur).

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

## Version web : le vrai code Java dans le navigateur

Le simulateur en ligne (`docs/simulator.html`) n'est pas une reecriture en JavaScript : c'est le code
Java du jeu (modele, Fabrique, Etat, Strategie, Observateur), compile pour Java 8 et execute par une
JVM OpenJDK 8 embarquee dans la page par le simulateur.
Une seule fenetre : un accueil (carte, murs, bot), la barre de commandes Swing (redemarrer, lecture,
pas a pas, pause, vitesse) et le plateau, joue au clavier (fleches ou ZQSD).

- `web/src` : les classes necessaires du jeu, adaptees a Java 8, et le point d'entree `SnakeWeb`.
- `web/build-jar.sh` : reconstruit `docs/app/snake.jar` (JDK 9 ou plus recent requis pour `--release 8`).
- `docs/simulateur/` : le simulateur et l'image de sa machine virtuelle (Linux, X11, OpenJDK 8).
- `docs/licenses/`, `THIRD_PARTY.md` : licences des composants de l'image.

Tester en local (le dossier doit etre servi en HTTP) :

```bash
cd docs && python3 -m http.server 8000     # puis http://localhost:8000/simulator.html
```

Publication : GitHub Pages, branche `main`, dossier `/docs`.

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

---

## Contact

ggoutard : [contact.ggoutard@gmail.com](mailto:contact.ggoutard@gmail.com)
