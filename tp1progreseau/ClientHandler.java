package tp1progreseau;

import java.io.*;
import java.net.*;

import tp1progreseau.utils.Commande;
import tp1progreseau.utils.Message;

public class ClientHandler implements Runnable {
	
	
	private static int COUNTER = 0;
	
	
	private int identifiant;
    private Socket socket;
    private BufferedReader entree;
    private PrintWriter sortie;
    private boolean ready;
    private boolean inGame;
    private String path;
    private GameCore core;
    private int numPlayer;
    
    public ClientHandler(GameCore core,Socket socket, BufferedReader entree, PrintWriter sortie) {
    	this.identifiant = ClientHandler.COUNTER++;
        this.socket = socket;
        this.entree = entree;
        this.sortie = sortie;
        this.ready = false;
        this.inGame = false;
        this.core  = core;
        this.numPlayer = 1;
    }
    
    public void run() {
        try {
            final long interval = 1000 / 60; 

            while (true) {
                long startTime = System.currentTimeMillis();

                if (entree.ready()) { 
                    
                	String json = entree.readLine();
                    
                    if (json != null) 
                    {
                    	
                		if(json.contains("message"))
                		{
                			Message message = Message.fromJson(json);
                			System.out.println(message);
                    		if(message.getMotif().equals("path"))
                    		{
                    			this.path = message.getMessage();
                    	        this.ready = true;
                    	        Serveur.findCommunParty();
                    		}
                		}
                		else 
                		{
                			Commande commande = Commande.fromJson(json);
 	                        
 	                        if(this.numPlayer == 1) core.getSnakeGame().setMovementJoueur1(commande.getAction());
 	                        else core.getSnakeGame().setMovementJoueur2(commande.getAction());
 	                        
                		}

                    }
                }
                

                if (core.gameIsRunning()) {
                    Serveur.sendMessage(identifiant, core.buildPanelBuilder().toJson(), sortie);
                }

                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = interval - elapsedTime;

                if (sleepTime > 0) {
                    try 
                    {
                        Thread.sleep(sleepTime);
                    } 
                    catch (InterruptedException e) 
                    {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void launchGame()
    {
        Serveur.sendMessage(identifiant, "Game start", sortie);
        this.inGame = true;
    }
    
    public boolean isReady()
    {
    	return this.ready;
    }
    
    public String getPath()
    {
    	return this.path;
    }
    
    public int id()
    {
    	return this.identifiant;
    }
    
    public int setId(int identifiant)
    {
    	return this.identifiant = identifiant;
    }
    
    public boolean inGame()
    {
    	return this.inGame;
    }
    
    public PrintWriter getSortie()
    {
    	return this.sortie;
    }
    
    
    public void setCore(GameCore core)
    {
    	this.core  = core;
    	this.numPlayer = 2;
    }
}
