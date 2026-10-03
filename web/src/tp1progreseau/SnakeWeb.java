package tp1progreseau;

import javax.swing.SwingUtilities;

import tp1progreseau.controller.ControllerSnakeGameLocal;

/**
 * Point d'entree de la version web (simulateur) du client Snake : le vrai code Java du jeu
 * (modele, Fabrique, Etat, Strategie, Observateur) dans une seule fenetre, sans serveur ni accueil.
 * Accueil simplifie (carte, murs, bot), puis la partie : le joueur au clavier, seul ou contre le bot.
 */
public class SnakeWeb {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControllerSnakeGameLocal());
    }
}
