package tp1progreseau.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Observable;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import tp1progreseau.controller.ControllerSnakeGameLocal;
import tp1progreseau.gameElement.Snake;
import tp1progreseau.model.SnakeGame;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;

/**
 * Fenetre unique de la version web, deux ecrans :
 *  - accueil (comme Accueil) : choix de la carte, des murs et du bot ;
 *  - partie : en haut les commandes de ViewCommand (redemarrer, lecture, pas a pas, pause, vitesse, tour),
 *    en dessous le plateau (PanelSnakeGame).
 * Observateur du modele, comme les autres vues (patron Observateur).
 */
public class ViewSnakeGameLocal extends ViewObserver {

    private static final long serialVersionUID = 1L;

    private final ControllerSnakeGameLocal controller;

    private final CardLayout cards = new CardLayout();
    private final JPanel screens = new JPanel(cards);
    private final JPanel board = new JPanel(new BorderLayout());
    private PanelSnakeGame panel;

    private final JButton btMenu = new JButton("Menu");
    private final JButton btRestart = commandButton("icon_restart", "Redemarrer");
    private final JButton btPlay = commandButton("icon_play", "Lecture");
    private final JButton btStep = commandButton("icon_step", "Pas a pas");
    private final JButton btPause = commandButton("icon_pause", "Pause");
    private final JSlider slider = new JSlider(1, 10, 5);

    private final JButton mapSizeButton = menuButton("Grande carte");
    private final JButton wallsButton = menuButton("Carte sans murs");
    private final JButton botButton = menuButton("Sans bot");

    public ViewSnakeGameLocal(ControllerSnakeGameLocal controller)
    {
        super();
        this.controller = controller;
    }

    private static JButton commandButton(String icon, String tip)
    {
        Image img = new ImageIcon(ViewSnakeGameLocal.class.getResource("/tp1progreseau/icons/" + icon + ".png")).getImage();
        JButton button = new JButton(new ImageIcon(img.getScaledInstance(26, 26, Image.SCALE_SMOOTH)));
        button.setToolTipText(tip);
        button.setPreferredSize(new Dimension(44, 36));
        button.setFocusable(false); // les fleches et Espace restent au jeu
        return button;
    }

    private static JButton menuButton(String text)
    {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(360, 52));
        return button;
    }

    @Override
    protected void init()
    {
        super.setTitle("Snake - Java embarque");
        super.setSize(880, 520);
        super.getContentPane().removeAll();
        super.setLayout(new BorderLayout());

        board.add(this.commandBar(), BorderLayout.NORTH);
        screens.add(this.accueil(), "accueil");
        screens.add(board, "partie");
        super.add(screens, BorderLayout.CENTER);

        btMenu.addActionListener(e -> controller.menu());
        btRestart.addActionListener(e -> controller.getEtat().restart());
        btPlay.addActionListener(e -> controller.getEtat().play());
        btStep.addActionListener(e -> controller.getEtat().step());
        btPause.addActionListener(e -> controller.getEtat().pause());
        slider.addChangeListener(e -> controller.setSpeed(speed()));

        // Clavier capte quel que soit le composant qui a le focus
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keys);

        // Le simulateur garde la JVM entre deux lancements : fenetre fermee pour de bon => on arrete la partie
        new Timer(500, e -> {
            hiddenChecks = isDisplayable() ? 0 : hiddenChecks + 1;
            if (hiddenChecks >= 3) {
                ((Timer) e.getSource()).stop();
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keys);
                controller.stop();
            }
        }).start();
    }

    /** Ecran d'accueil : version simplifiee d'Accueil (carte, murs, bot), sans recherche de partie. */
    private JPanel accueil()
    {
        JPanel accueil = new JPanel(new GridBagLayout());
        accueil.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 6, 0);

        Image titre = new ImageIcon(ViewSnakeGameLocal.class.getResource("/tp1progreseau/images/titre.png")).getImage();
        gbc.gridy = 0;
        accueil.add(new JLabel(new ImageIcon(titre.getScaledInstance(330, 231, Image.SCALE_SMOOTH))), gbc);

        mapSizeButton.addActionListener(e -> toggleText(mapSizeButton, "Petite carte", "Grande carte"));
        wallsButton.addActionListener(e -> toggleText(wallsButton, "Carte avec des murs", "Carte sans murs"));
        botButton.addActionListener(e -> toggleText(botButton, "Avec un bot", "Sans bot"));
        JButton startButton = menuButton("Jouer");
        startButton.setBackground(new Color(74, 222, 128));
        startButton.addActionListener(e -> controller.start(chosenLayout(), botButton.getText().equals("Avec un bot")));

        gbc.gridy = 1; accueil.add(mapSizeButton, gbc);
        gbc.gridy = 2; accueil.add(wallsButton, gbc);
        gbc.gridy = 3; accueil.add(botButton, gbc);
        gbc.gridy = 4; accueil.add(startButton, gbc);
        return accueil;
    }

    private void toggleText(JButton button, String text1, String text2)
    {
        button.setText(button.getText().equals(text1) ? text2 : text1);
    }

    /** Carte choisie, comme Accueil.Launch() : avec bot => carte a deux serpents (arena). */
    private String chosenLayout()
    {
        boolean petite = mapSizeButton.getText().equals("Petite carte");
        boolean murs = wallsButton.getText().equals("Carte avec des murs");
        boolean bot = botButton.getText().equals("Avec un bot");
        String name = bot ? (petite ? "smallArena" : "arena") : (petite ? "small" : "alone");
        return "/tp1progreseau/layouts/" + name + (murs ? "" : "NoWall") + ".lay";
    }

    /** Barre de commandes : les boutons et le curseur de ViewCommand, sur une ligne. */
    private JPanel commandBar()
    {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        btMenu.setFocusable(false);
        btMenu.setPreferredSize(new Dimension(76, 36));
        buttons.add(btMenu);
        buttons.add(btRestart);
        buttons.add(btPlay);
        buttons.add(btStep);
        buttons.add(btPause);
        bar.add(buttons, BorderLayout.WEST);

        JPanel speed = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        JLabel label = new JLabel("Tours par seconde");
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setFocusable(false);
        slider.setPreferredSize(new Dimension(160, 36));
        speed.add(label);
        speed.add(slider);
        bar.add(speed, BorderLayout.CENTER);

        super.affichage.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        super.affichage.setForeground(new Color(40, 40, 40));
        super.affichage.setPreferredSize(new Dimension(90, 36));
        bar.add(super.affichage, BorderLayout.EAST);
        return bar;
    }

    public double speed()
    {
        return 1000.0 / slider.getValue();
    }

    public void showAccueil()
    {
        cards.show(screens, "accueil");
    }

    /** Affiche le plateau d'une nouvelle partie. */
    public void showPartie(PanelSnakeGame panel)
    {
        if (this.panel != null) board.remove(this.panel);
        this.panel = panel;
        board.add(panel, BorderLayout.CENTER);
        board.revalidate();
        cards.show(screens, "partie");
        board.repaint();
    }

    /** Boutons actifs selon l'etat, comme ControllerSimpleGame.configActions(). */
    public void configActions(boolean running, boolean finished)
    {
        btPlay.setEnabled(!running && !finished);
        btStep.setEnabled(!running && !finished);
        btPause.setEnabled(running && !finished);
    }

    private final KeyEventDispatcher keys = e -> {
        if (e.getID() == KeyEvent.KEY_PRESSED && isDisplayable()) keyPressed(e.getKeyCode());
        return false;
    };

    private int hiddenChecks;

    private void keyPressed(int code)
    {
        switch (code) {
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: controller.direction(AgentAction.MOVE_RIGHT); break;
            case KeyEvent.VK_LEFT:  case KeyEvent.VK_Q: controller.direction(AgentAction.MOVE_LEFT); break;
            case KeyEvent.VK_UP:    case KeyEvent.VK_Z: controller.direction(AgentAction.MOVE_UP); break;
            case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: controller.direction(AgentAction.MOVE_DOWN); break;
            case KeyEvent.VK_SPACE: case KeyEvent.VK_P: controller.togglePause(); break;
            default: break;
        }
    }

    /** Appele par le modele a chaque tour (thread du jeu). */
    @Override
    public void update(Observable o, Object arg)
    {
        super.update(o, arg); // "Turn : n"
        SnakeGame game = (SnakeGame) o;
        PanelSnakeGame panel = this.panel;
        if (panel == null) return;

        // Copie de l'etat pour que le dessin ne lise pas des listes modifiees par le thread du jeu
        ArrayList<FeaturesSnake> snakes = new ArrayList<FeaturesSnake>();
        for (FeaturesSnake f : game.MakeFeaturesSnake())
            snakes.add(new FeaturesSnake(new ArrayList<>(f.getPositions()), f.getLastAction(), f.getColorSnake(), f.isInvincible(), f.isSick()));
        ArrayList<FeaturesItem> items = new ArrayList<FeaturesItem>(game.MakeFeaturesItem());

        boolean joueurVivant = false;
        for (Snake snake : new ArrayList<Snake>(game.getSnakes()))
            if (snake.getId() == 0) joueurVivant = true;

        panel.updateInfoGame(snakes, items);
        panel.repaint();
        if (!joueurVivant) SwingUtilities.invokeLater(() -> controller.gameOver(game));
    }
}
