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
    
    public ClientHandler(Socket socket, BufferedReader entree, PrintWriter sortie) {
    	this.identifiant = ClientHandler.COUNTER++;
        this.socket = socket;
        this.entree = entree;
        this.sortie = sortie;
        this.ready = false;
        this.inGame = false;
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
     	                        
     	                        if(this.identifiant % 2 == 0) Serveur.setMovementJoueur1(commande.getAction());
     	                        else Serveur.setMovementJoueur2(commande.getAction());
     	                        
                    		}
                    	
	                       
                    	


                    }
                }

                if (Serveur.gameIsRunning()) {
                    Serveur.sendMessage(identifiant, Serveur.buildPanelBuilder().toJson(), sortie);
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
            Serveur.removeClient(sortie);
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
    
    public int Id()
    {
    	return this.identifiant;
    }
    
    public boolean inGame()
    {
    	return this.inGame;
    }
    
}
