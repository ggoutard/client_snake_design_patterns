package tp1progreseau.view;

import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import tp1progreseau.model.Game;

public abstract class ViewObserver extends JFrame implements Observer {

    protected JLabel affichage;

    public ViewObserver() {
        super("Game Observer");
        this.affichage = new JLabel("Turn : ", SwingConstants.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 200); 
        this.setLocationRelativeTo(null); 
        this.add(affichage); 
    }

    public void plugObserverTarget(Observable obs) {
        obs.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        Game game = (Game) o;
        this.reloadTurn(game.getTurn());
    }

    private void reloadTurn(int turn) {
        this.affichage.setText("Turn : " + turn);
    }

    public void open() {
        this.init();
        this.setVisible(true);
    }

    abstract protected void init();
}
