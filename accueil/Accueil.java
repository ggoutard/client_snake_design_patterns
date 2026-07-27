package tp1progreseau.accueil;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import tp1progreseau.controller.ControllerSnakeGameNetwork;
import tp1progreseau.utils.Message;

import java.util.ArrayList;
import java.util.Observable;

public class Accueil extends JFrame {
    private JButton mapSizeButton;
    private JButton wallsButton;
    private JButton startButton;
    private JButton modeButton;
    private String path;
    private ControllerSnakeGameNetwork controller;
    private boolean research;
    private JLabel msgChargment; 
    private ArrayList<java.util.Observer> observers;

    public Accueil(ControllerSnakeGameNetwork controller) {
        this.controller = controller;
        this.research = false;
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 1200);
        setResizable(true);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        this.observers = new ArrayList<>();

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(Color.BLACK);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(140, 0, 20, 0));

        ImageIcon titleIcon = new ImageIcon("src/tp1progreseau/images/titre.png");
        Image scaledImage = titleIcon.getImage().getScaledInstance(800, 600, Image.SCALE_SMOOTH);
        JLabel titleLabel = new JLabel(new ImageIcon(scaledImage));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        Font buttonFont = new Font("Monospaced", Font.BOLD, 24);
        Color buttonColor = new Color(255, 255, 255);

        mapSizeButton = createButton("Petite carte", buttonFont, buttonColor);
        mapSizeButton.addActionListener(e -> cycleText(mapSizeButton, new String[]{"Petite carte", "Grande carte"}));
        gbc.gridy = 0;
        buttonPanel.add(mapSizeButton, gbc);

        wallsButton = createButton("Carte avec des murs", buttonFont, buttonColor);
        wallsButton.addActionListener(e -> toggleText(wallsButton, "Carte avec des murs", "Carte sans des murs"));
        gbc.gridy = 1;
        buttonPanel.add(wallsButton, gbc);

        modeButton = createButton("Mode Solo", buttonFont, buttonColor);
        modeButton.addActionListener(e -> toggleText(modeButton, "Mode Solo", "Mode Multi"));
        gbc.gridy = 2;
        buttonPanel.add(modeButton, gbc);

        startButton = createButton("Rechercher une partie", buttonFont, buttonColor);
        startButton.addActionListener(e -> Launch());
        gbc.gridy = 3;
        buttonPanel.add(startButton, gbc);

        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(Color.BLACK);
        outerPanel.add(buttonPanel, new GridBagConstraints());

        add(outerPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createButton(String text, Font font, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setBorder(new RoundedBorder(10));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(400, 60));
        return button;
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

    public void Launch() {
        String mapSize = mapSizeButton.getText();
        String walls = wallsButton.getText();
        String mode = modeButton.getText();

        String modePath = mode.equals("Mode Solo") ? "solo" : "multi";

        switch (mapSize) {
            case "Petite carte":
                path = walls.equals("Carte avec des murs")
                    ? "src/tp1progreseau/layouts/smallArena.lay"
                    : "src/tp1progreseau/layouts/smallArenaNoWall.lay";
                break;
            case "Grande carte":
                path = walls.equals("Carte avec des murs")
                    ? "src/tp1progreseau/layouts/arena.lay"
                    : "src/tp1progreseau/layouts/arenaNoWall.lay";
                break;
        }
        if(mode.equals("Mode Solo")) path  = path.replaceAll("arena", "").replaceAll("Arena", "");
        startSearch();
    }



    private void toggleText(JButton button, String text1, String text2) {
        button.setText(button.getText().equals(text1) ? text2 : text1);
    }

    private void startSearch() {
        getContentPane().removeAll();
        this.notifyObservers(this.buildMessagePath());
        
        this.msgChargment = new JLabel("Recherche en cours ...", JLabel.CENTER);
        this.research = true;
        this.msgChargment.setFont(new Font("Monospaced", Font.BOLD, 32));
        this.msgChargment.setForeground(Color.WHITE);
        getContentPane().add(this.msgChargment, BorderLayout.CENTER);
        revalidate();
        repaint();
    }



    @Override
    public void dispose() {
    	 super.dispose();
         controller.build(path);
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius + 1;
            return insets;
        }
    }
    
    public boolean research()
    {
    	return this.research;
    }
    
    public Message buildMessagePath()
    {
    	return new Message(this.path,"path");
    }
    
    public void addObserver(java.util.Observer o) {
        observers.add(o);
    }

    public void deleteObserver(java.util.Observer o) {
        observers.remove(o);
    }

    public void notifyObservers(Object arg) {
   
        for (java.util.Observer observer : observers) 
        {
            observer.update(null, arg);
        }
            
    }
    
    public void ChangeStateChargement(String texte)
    {
        this.msgChargment.setText(texte);
    }
    

}
