package tp1progreseau.accueil;

import javax.swing.*;
import java.awt.*;
import tp1progreseau.controller.ControllerSnakeGameNetwork;
import tp1progreseau.gameElement.fabrique.TypeSnake;

public class Accueil extends JFrame {

    private JButton playersButton;
    private JButton playerTypeButton;
    private JButton mapSizeButton;
    private JButton wallsButton;
    private JButton startButton;
    private boolean isTwoPlayers = false;
    private String path;
    private TypeSnake joueur1;
    private TypeSnake joueur2;
    private ControllerSnakeGameNetwork controller;

    public Accueil(ControllerSnakeGameNetwork controller) {
        this.controller = controller;

        setTitle("Snake War");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setResizable(false); 
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Snake War", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(Box.createVerticalStrut(80)); 
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(20)); 

        add(titlePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setBackground(Color.BLACK);

        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        Color buttonColor = new Color(200, 200, 200);

        playersButton = createButton("Un joueur", buttonFont, buttonColor);
        playersButton.addActionListener(e -> {
            toggleText(playersButton, "Un joueur", "Deux joueurs");
            togglePlayerTypeOptions();
        });

        playerTypeButton = createButton("Joueur", buttonFont, buttonColor);
        playerTypeButton.addActionListener(e -> {
            if (isTwoPlayers) {
                cycleText(playerTypeButton, new String[]{"Joueur vs Joueur", "Joueur vs Random", "Joueur vs IA", "IA vs IA"});
            } else {
                cycleText(playerTypeButton, new String[]{"Joueur", "Random", "AI"});
            }
        });

        mapSizeButton = createButton("Petite", buttonFont, buttonColor);
        mapSizeButton.addActionListener(e -> cycleText(mapSizeButton, new String[]{"Petite", "Grande"}));

        wallsButton = createButton("Avec murs", buttonFont, buttonColor);
        wallsButton.addActionListener(e -> toggleText(wallsButton, "Avec murs", "Sans murs"));

        startButton = createButton("Lancer la partie", buttonFont, buttonColor);
        startButton.addActionListener(e -> this.Launch());

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(playersButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(playerTypeButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(mapSizeButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(wallsButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createVerticalGlue());

        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        outerPanel.setBackground(Color.BLACK);
        outerPanel.add(buttonPanel, new GridBagConstraints());

        add(outerPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createButton(String text, Font font, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(backgroundColor);
        button.setOpaque(true);
        return button;
    }

    private void toggleText(JButton button, String text1, String text2) {
        button.setText(button.getText().equals(text1) ? text2 : text1);
    }

    private void cycleText(JButton button, String[] texts) {
        String currentText = button.getText();
        for (int i = 0; i < texts.length; i++) {
            if (currentText.equals(texts[i])) {
                button.setText(texts[(i + 1) % texts.length]);
                break;
            }
        }
    }

    private void togglePlayerTypeOptions() {
        isTwoPlayers = !isTwoPlayers;
        if (isTwoPlayers) {
            playerTypeButton.setText("Joueur vs Joueur");
        } else {
            playerTypeButton.setText("Joueur");
        }
    }

    public void Launch() {
        String players = playersButton.getText();
        String mapSize = mapSizeButton.getText();
        String walls = wallsButton.getText();
        String modPlayers = playerTypeButton.getText();
    
        if (players.equals("Un joueur")) {
            switch (mapSize) {
                case "Petite":
                    path = walls.equals("Avec murs") ? "src/tp1progreseau/layouts/small.lay" : "src/tp1progreseau/layouts/smallNoWall.lay";
                    break;
                case "Grande":
                    path = walls.equals("Avec murs") ? "src/tp1progreseau/layouts/alone.lay" : "src/tp1progreseau/layouts/aloneNoWall.lay";
                    break;
            }
        } else if (players.equals("Deux joueurs")) {
            switch (mapSize) {
                case "Petite":
                    path = walls.equals("Avec murs") ? "src/tp1progreseau/layouts/smallArena.lay" : "src/tp1progreseau/layouts/smallArenaNoWall.lay";
                    break;
                case "Grande":
                    path = walls.equals("Avec murs") ? "src/tp1progreseau/layouts/arena.lay" : "src/tp1progreseau/layouts/arenaNoWall.lay";
                    break;
            }
        }

        if (players.equals("Un joueur")) {
            joueur2 = null;
            switch (modPlayers) {
                case "Joueur":
                    joueur1 = TypeSnake.HUMAN;
                    break;
                case "Random":
                    joueur1 = TypeSnake.RANDOM;
                    break;
                case "AI":
                    joueur1 = TypeSnake.IA;
                    break;
            }
        } else {
            switch (modPlayers) {
                case "Joueur vs Joueur":
                    joueur1 = TypeSnake.HUMAN;
                    joueur2 = TypeSnake.HUMAN;
                    break;
                case "Joueur vs Random":
                    joueur1 = TypeSnake.HUMAN;
                    joueur2 = TypeSnake.RANDOM;
                    break;
                case "Joueur vs IA":
                    joueur1 = TypeSnake.HUMAN;
                    joueur2 = TypeSnake.IA;
                    break;
                case "IA vs IA":
                    joueur1 = TypeSnake.IA;
                    joueur2 = TypeSnake.IA;
                    break;
            }
        }

        this.dispose();
    }

    public TypeSnake getJoueur1() {
        return joueur1;
    }

    public TypeSnake getJoueur2() {
        return joueur2;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void dispose() {
        super.dispose();
        System.out.println("ok");
        controller.build(path, joueur1, joueur2);
    }

}
