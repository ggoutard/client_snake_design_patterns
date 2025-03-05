package tp1progreseau.controller;

import tp1progreseau.gameElement.fabrique.TypeSnake;
import tp1progreseau.model.InputMap;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.view.*;
import tp1progreseau.utils.Commande;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;

import java.util.ArrayList;
import java.util.Observable;

public class ControllerSnakeGameNetwork extends Observable {
    private PanelSnakeGame panel;
    private ViewSnakeGame view;
    private TypeSnake joueur1;
    private TypeSnake joueur2;

    public ControllerSnakeGameNetwork() {}

    private void init(String filename) {
        System.out.println(filename);
        InputMap map = new InputMap(filename);
        this.panel = new PanelSnakeGame(map.getSizeX(),map.getSizeY(),map.get_walls(),map.getStart_snakes(),map.getStart_items());
        this.view = new ViewSnakeGame(this,this.panel);
        this.view.open();
    }

    public void build(String path, TypeSnake joueur1, TypeSnake joueur2) 
    {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.init(path);
        this.view.open();
    }
    
    public void update(ArrayList<FeaturesSnake> featureSnake, ArrayList<FeaturesItem> featureItem)
    {
    	if(panel != null)
    	{
    		this.panel.updateInfoGame(featureSnake,featureItem);
    		this.panel.repaint();
    	}
    }

    public void notifyCommand(AgentAction action) {
        Commande commande = new Commande(action);
        setChanged();
        notifyObservers(commande);
       
    }
}
