package tp1progreseau;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tp1progreseau.model.SnakeGame;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.PanelBuilder;

public class GameCore {
	
	
	private static int COUNTER  = 42;
	
	private Map<PrintWriter,ClientHandler> clients;
	private SnakeGame snakeGame;
	private boolean gameRunning;
	private boolean solo;
	private int identifiant;
	
	public GameCore()
	{
		this.identifiant = GameCore.COUNTER++;
		solo = false;
	}
	

	public void init(PrintWriter sortie, ClientHandler clientHandler)
	{
		clients = new HashMap<>();
		clientHandler.setId(identifiant);
		clients.put(sortie, clientHandler);
		snakeGame = new SnakeGame();
		new Thread(clientHandler).start();    	
	}
	
	public void updateMap()
	{
		snakeGame.init(getHandlerHost().getPath());
		try {
			if(((!this.getHandlerHost().getPath().contains("arena")) && !(this.getHandlerHost().getPath().contains("Arena")))) solo = true;
		}
		catch (Exception e){
			
		}
	}
	
	public void launchGame() {  
	    snakeGame.initializeGame();
	    snakeGame.Launch();
	    this.gameRunning = true;

	    for (Map.Entry<PrintWriter, ClientHandler> entry : clients.entrySet()) {
	        try {
	            entry.getValue().launchGame();
	        } catch (Exception e) {
	            System.out.println("Erreur lors du lancement pour le client : " + entry.getKey());
	            System.out.println(e.getMessage());
	        }
	    }
	}

	
	public SnakeGame getSnakeGame()
	{
		return this.snakeGame;
	}
    
    public PanelBuilder buildPanelBuilder()
    {
    	return new PanelBuilder(
			snakeGame.MakeFeaturesSnake(),
			snakeGame.MakeFeaturesItem()
		);
    }
    
    public int Id()
    {
    	return identifiant;
    }
    
    public boolean gameIsRunning()
    {
    	return snakeGame.getIsRunning();
    }
    
    public  void setMovementJoueur1(AgentAction action)
    {
    	snakeGame.setMovementJoueur1(action);
    }
    
    public void setMovementJoueur2(AgentAction action)
    {
    	if(clients.size() > 1) 
    		snakeGame.setMovementJoueur2(action);
    }
    
    public Map<PrintWriter,ClientHandler> getHost()
    {
    	Map<PrintWriter,ClientHandler> val = new HashMap<>();
    	Iterator<PrintWriter> iterator = this.clients.keySet().iterator();
    	PrintWriter print = iterator.next();
    	val.put(print, this.clients.get(print));
    	return val;
    }
    
    public PrintWriter getPrinterHost()
    {
    	Iterator<PrintWriter> iterator = this.clients.keySet().iterator();
    	return iterator.next();
    }
    
    public ClientHandler getHandlerHost()
    {
    	Iterator<PrintWriter> iterator = this.clients.keySet().iterator();
    	PrintWriter print = iterator.next();
    	return this.clients.get(print);
    }
    
    public Map<PrintWriter,ClientHandler> getNeighbor()
    {
    	Map<PrintWriter,ClientHandler> val = new HashMap<>();
    	Iterator<PrintWriter> iterator = this.clients.keySet().iterator();
    	iterator.next();
    	PrintWriter print = iterator.next();
    	val.put(print, this.clients.get(print));
    	return val;
    }
    
    public PrintWriter getPrinterNeighbor()
    {
    	Iterator<PrintWriter> iterator = this.clients.keySet().iterator();
    	iterator.next();
    	return iterator.next();
    }
    
    public ClientHandler getHandlerNeighbor()
    {
    	Iterator<PrintWriter> iterator = this.clients.keySet().iterator();
    	iterator.next();
    	PrintWriter print = iterator.next();
	    return this.clients.get(print);
    	
    }
    
    public boolean gameRunning()
    {
    	return  this.gameRunning;
    }
    
    public boolean gameSolo()
    {
		return solo;
    }
    
    public void fusionGame(GameCore gameCore) throws Exception
    {
    	if(!this.gameSolo())
    	{
    		ClientHandler client = gameCore.getHandlerHost();
    		client.setCore(this);
    		client.setId(this.identifiant);
	    	this.clients.put(gameCore.getPrinterHost(), client);
	    	this.launchGame();
	    	
    	}
    	else throw new Exception("Une partie solo ne peux contenir deux joueurs");
    }
    
    
    public boolean isCompatible(GameCore gameCore)
    {
		return 
			getHandlerHost().id() != gameCore.getHandlerHost().id()
		&&
			getHandlerHost().isReady() && gameCore.getHandlerHost().isReady()
		&&
			getHandlerHost().getPath().equals(gameCore.getHandlerHost().getPath());
    }
    
    
    
}
