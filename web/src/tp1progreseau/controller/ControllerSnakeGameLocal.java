package tp1progreseau.controller;

import tp1progreseau.etat.EtatPause;
import tp1progreseau.etat.EtatRunning;
import tp1progreseau.gameElement.fabrique.TypeSnake;
import tp1progreseau.model.InputMap;
import tp1progreseau.model.SnakeGame;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.view.PanelSnakeGame;
import tp1progreseau.view.ViewSnakeGameLocal;

/**
 * Controleur de la version web : la partie tourne dans la meme JVM (pas de serveur), une seule fenetre.
 * Le joueur (vert) joue au clavier, seul ou contre le bot (rouge, patron Strategie), sur la carte choisie a l'accueil.
 * Les commandes passent par le patron Etat, comme ControllerSimpleGame : la partie attend en EtatPause
 * jusqu'a la premiere direction (ou le bouton lecture).
 */
public class ControllerSnakeGameLocal extends AbstractController {

    private final ViewSnakeGameLocal view;
    private SnakeGame game;
    private boolean finished;
    private boolean threadRunning;

    public ControllerSnakeGameLocal()
    {
        this.view = new ViewSnakeGameLocal(this);
        this.view.open();
        this.view.showAccueil();
    }

    /** Bouton "Jouer" de l'accueil. */
    public void start(String layout, boolean bot)
    {
        stopThread();
        if (game != null) game.deleteObserver(view);

        game = new SnakeGame();
        game.init(layout);
        game.setJoueurs(TypeSnake.HUMAN, bot ? TypeSnake.IA : null);
        game.initializeGame();
        game.setTime(view.speed());
        finished = false;

        view.showPartie(new PanelSnakeGame(game.getX(), game.getY(), new InputMap(layout).get_walls(),
                game.MakeFeaturesSnake(), game.MakeFeaturesItem()));
        view.plugObserverTarget(game);
        view.update(game, null);
        super.setEtat(new EtatPause(this));
        configActions();
    }

    /** Bouton "Menu" : retour a l'accueil. */
    public void menu()
    {
        stopThread();
        finished = true;
        view.showAccueil();
    }

    @Override
    public void restart()
    {
        stopThread();
        finished = false;
        game.initializeGame();
        view.update(game, null);
        super.setEtat(new EtatPause(this));
        configActions();
    }

    @Override
    public void step()
    {
        if (finished) return;
        double time = game.getTime();
        game.setTime(0);
        game.step();
        game.setTime(time);
    }

    @Override
    public void play()
    {
        if (finished) {
            super.setEtat(new EtatPause(this));
            return;
        }
        game.Launch();
        threadRunning = true;
        configActions();
    }

    @Override
    public void pause()
    {
        stopThread();
        configActions();
    }

    @Override
    public void setSpeed(double speed)
    {
        if (game != null) game.setTime(speed);
    }

    private void stopThread()
    {
        if (threadRunning) game.pause();
        threadRunning = false;
    }

    private void configActions()
    {
        view.configActions(threadRunning, finished);
    }

    /** Fleches / ZQSD : la premiere direction lance (ou relance) la partie. */
    public void direction(AgentAction action)
    {
        if (game == null || finished || game.getSnakes().isEmpty() || game.getSnakes().get(0).getId() != 0) return;
        game.setMovementJoueur1(action);
        if (super.getEtat() instanceof EtatPause) super.getEtat().play();
    }

    /** Espace : pause / reprise. */
    public void togglePause()
    {
        if (game == null || finished) return;
        if (super.getEtat() instanceof EtatRunning) super.getEtat().pause();
        else super.getEtat().play();
    }

    /** Appele sur le thread Swing quand le serpent du joueur est mort. */
    public void gameOver(SnakeGame ended)
    {
        if (ended != game || finished) return;
        finished = true;
        super.setEtat(new EtatPause(this));
        stopThread();
        configActions();
    }

    /** Fenetre fermee : arrete le thread du jeu. */
    public void stop()
    {
        finished = true;
        stopThread();
    }
}
