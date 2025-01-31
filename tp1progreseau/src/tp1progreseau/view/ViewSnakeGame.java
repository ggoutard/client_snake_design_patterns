package tp1progreseau.view;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import tp1progreseau.utils.AgentAction;


public class ViewSnakeGame extends ViewObserver implements KeyListener {

    PanelSnakeGame panel;

    public static AgentAction ActionPlayer1 = AgentAction.MOVE_RIGHT;
    public static AgentAction ActionPlayer2 = AgentAction.MOVE_RIGHT;


    public ViewSnakeGame(PanelSnakeGame panel ) {

        super();
        this.panel = panel ;

    }

    
    protected void init()
    {
        super.setTitle("Snake Game");
        super.setSize(this.panel.getSizeX() * 100, this.panel.getSizeY() * 100);
        super.add(this.panel);
        super.addKeyListener(this);
        super.requestFocus();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        switch (e.getKeyCode()) {
            
            case KeyEvent.VK_RIGHT -> ViewSnakeGame.ActionPlayer2 = AgentAction.MOVE_RIGHT;
            
            case KeyEvent.VK_LEFT -> ViewSnakeGame.ActionPlayer2 = AgentAction.MOVE_LEFT;
            
            case KeyEvent.VK_UP -> ViewSnakeGame.ActionPlayer2 = AgentAction.MOVE_UP;
            
            case KeyEvent.VK_DOWN -> ViewSnakeGame.ActionPlayer2 = AgentAction.MOVE_DOWN;
            
            case KeyEvent.VK_Z -> ViewSnakeGame.ActionPlayer1 = AgentAction.MOVE_UP;
            
            case KeyEvent.VK_Q -> ViewSnakeGame.ActionPlayer1 = AgentAction.MOVE_LEFT;
            
            case KeyEvent.VK_S -> ViewSnakeGame.ActionPlayer1 = AgentAction.MOVE_DOWN;
            
            case KeyEvent.VK_D -> ViewSnakeGame.ActionPlayer1 = AgentAction.MOVE_RIGHT;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}



    
    
}
