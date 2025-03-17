package tp1progreseau.view;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.controller.ControllerSnakeGameNetwork;


public class ViewSnakeGame extends ViewObserver implements KeyListener {

    private PanelSnakeGame panel;
    private ControllerSnakeGameNetwork controller;

    public static AgentAction ActionPlayer1 = AgentAction.MOVE_RIGHT;
    public static AgentAction ActionPlayer2 = AgentAction.MOVE_RIGHT;


    public ViewSnakeGame(ControllerSnakeGameNetwork controller,PanelSnakeGame panel ) {

        super();
        this.panel = panel ;
        this.controller = controller;

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
            
            case KeyEvent.VK_RIGHT -> this.controller.notifyCommand(AgentAction.MOVE_RIGHT);
            
            case KeyEvent.VK_LEFT ->  this.controller.notifyCommand(AgentAction.MOVE_LEFT);
            
            case KeyEvent.VK_UP ->  this.controller.notifyCommand(AgentAction.MOVE_UP);
            
            case KeyEvent.VK_DOWN ->  this.controller.notifyCommand(AgentAction.MOVE_DOWN);
            
            case KeyEvent.VK_Z ->  this.controller.notifyCommand(AgentAction.MOVE_UP);
            
            case KeyEvent.VK_Q ->  this.controller.notifyCommand(AgentAction.MOVE_LEFT);
            
            case KeyEvent.VK_S ->  this.controller.notifyCommand(AgentAction.MOVE_DOWN);
            
            case KeyEvent.VK_D ->  this.controller.notifyCommand(AgentAction.MOVE_RIGHT);
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}



    
    
}
