package tp1progreseau.controller;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tp1progreseau.etat.EtatPause;
import tp1progreseau.etat.EtatRunning;
import tp1progreseau.gameElement.fabrique.TypeSnake;
import tp1progreseau.model.InputMap;
import tp1progreseau.model.SnakeGame;
import tp1progreseau.view.*;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;




public class ControllerSnakeGame extends AbstractController implements Observer {
    
    private PanelSnakeGame panel;
    private SnakeGame game;
    private ViewSnakeGame view;
    private final ViewCommand viewCommand;
    private ActionListener pauseEvent;
    private ActionListener playEvent;
    private ActionListener restartEvent;
    private ActionListener stepEvent;
    private TypeSnake joueur1;
    private TypeSnake joueur2;

    private void configActions()
    {
        this.configPlay();
        this.configPause();
        this.configRestart();
        this.configStep();
        if(super.getEtat() instanceof EtatRunning) viewCommand.getBtPause().setEnabled(true);
        if(super.getEtat() instanceof EtatPause) 
        {
            viewCommand.getBtPlay().setEnabled(true);
            viewCommand.getBtStep().setEnabled(true);

        }
        this.plugObserverTarget((Observable)game);

    }

     private void configPause() {
        if (this.pauseEvent != null) viewCommand.getBtPause().removeActionListener(this.pauseEvent);
        this.pauseEvent = event -> super.getEtat().pause();
        viewCommand.getBtPause().addActionListener(this.pauseEvent);
    }

    private void configPlay()
    {
        if (this.playEvent != null) viewCommand.getBtPlay().removeActionListener(this.playEvent);
        this.playEvent = event -> super.getEtat().play();
        viewCommand.getBtPlay().addActionListener(this.playEvent);
    }

    private void configRestart()
    {
        if (this.restartEvent != null) viewCommand.getBtRestart().removeActionListener(this.restartEvent);
        this.restartEvent = event -> super.getEtat().restart();
        viewCommand.getBtRestart().addActionListener(this.restartEvent);
    }

    private void configStep()
    {
        if (this.stepEvent != null) viewCommand.getBtStep().removeActionListener(this.stepEvent);
        this.stepEvent = event -> super.getEtat().step();
        viewCommand.getBtStep().addActionListener(this.stepEvent);
    }

    public ViewCommand getViewCommand()
    {
        return viewCommand;
    }

    public ControllerSnakeGame(int maxturn, String filename, TypeSnake joueur1, TypeSnake joueur2) 
    {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.init(filename);
        this.viewCommand = new ViewCommand();
        this.view.plugObserverTarget(game);
        this.viewCommand.plugObserverTarget(game);
        this.view.open();
        this.viewCommand.open();
        this.configActions();
        viewCommand.getBtPlay().setEnabled(false);
        viewCommand.getBtStep().setEnabled(false);
        super.setEtat(new EtatRunning(this));
        this.game.Launch();

        this.viewCommand.getSlider().addChangeListener(
            new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e){
                    JSlider source  = (JSlider) e.getSource();
                    double maxTime = 1000;
                    double diviseur = (double)source.getValue();
                    ControllerSnakeGame.this.setSpeed(maxTime/diviseur);
                }
            }
        );

    }
    
    @Override
    public void setSpeed(double speed)
    {
        this.configActions();
        game.setTime(speed);
    }

    @Override
    public void restart()
    {   
        game.restart();
        this.configActions();
        viewCommand.getBtPlay().setEnabled(false);
        viewCommand.getBtStep().setEnabled(false);

    }

    @Override
    public void step()
    {   
        double time = game.getTime();
        game.setTime(0);
        game.step();
        game.setTime(time);
        this.configActions();
    }
    

    @Override
    public void play()
    {
        game.Launch();
        this.configActions();
        viewCommand.getBtPlay().setEnabled(false);
        viewCommand.getBtStep().setEnabled(false);
    }

    @Override
    public void pause()
    {
        game.pause();
        this.configActions();
        viewCommand.getBtPause().setEnabled(false);
    }
    
    private void init(String filename)
    {
        InputMap map = new InputMap(filename);
        this.panel = new PanelSnakeGame(map.getSizeX(),map.getSizeY(),map.get_walls(),map.getStart_snakes(),map.getStart_items());
        this.game = SnakeGame.buildGame(this.panel,filename,this.joueur1, this.joueur2);

        this.game.initializeGame();

        this.view = new ViewSnakeGame(this, this.panel);
        this.view.open();
    }


    public void update(Observable o, Object arg) 
    {
        this.panel.updateInfoGame(game.MakeFeaturesSnake(),game.MakeFeaturesItem());

        this.panel.repaint();

    }

    public void plugObserverTarget(Observable obs) 
    {
        obs.addObserver(this);
    }

    
}
