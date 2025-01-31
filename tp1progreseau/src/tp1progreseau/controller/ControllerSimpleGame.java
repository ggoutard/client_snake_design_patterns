package tp1progreseau.controller;

import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tp1progreseau.etat.*;
import tp1progreseau.model.SimpleGame;
import tp1progreseau.view.ViewCommand;
import tp1progreseau.view.ViewSimpleGame;


public class ControllerSimpleGame extends AbstractController {

    private final SimpleGame game;
    private final ViewSimpleGame view; 
    private final ViewCommand viewCommand;
    private ActionListener pauseEvent;
    private ActionListener playEvent;
    private ActionListener restartEvent;
    private ActionListener stepEvent;

    public ControllerSimpleGame(int maxturn)
    {
        this.game = new SimpleGame(maxturn);
        this.view = new ViewSimpleGame();
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
                    ControllerSimpleGame.this.setSpeed(maxTime/diviseur);
                }
            }
        );

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

    @Override
    public void setSpeed(double speed)
    {
        this.configActions();
        game.setTime(speed);
    }

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

}
