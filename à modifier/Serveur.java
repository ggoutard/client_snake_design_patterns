package tp1progreseau;

import java.net.*;
import java.awt.Panel;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import tp1progreseau.model.SnakeGame;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.Commande;
import tp1progreseau.utils.Message;
import tp1progreseau.utils.PanelBuilder;
import tp1progreseau.view.PanelSnakeGame;
import tp1progreseau.model.InputMap;
import tp1progreseau.gameElement.fabrique.TypeSnake;


public class Serveur {
    
	private static Set<PrintWriter> clients = new HashSet<>();
    private static SnakeGame snakeGame; 
    private static ArrayList<ClientHandler> handlerClients = new ArrayList<>();
    private static boolean inGame = false;

    
    public static void main(String[] args) {
        int port = 2545;
        String filename = "src/tp1progreseau/layouts/smallArenaNoWall.lay";
        Serveur.snakeGame = SnakeGame.buildGame(filename,TypeSnake.HUMAN, TypeSnake.HUMAN);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            
        	System.out.println("Le serveur écoute sur le port " + port);
            
            while (true) 
            {
            	Socket clientSocket = serverSocket.accept();
            	Serveur.addClient(clientSocket);
            }
        } 
        
        catch (IOException e) {System.out.println("Erreur dans la tentative d'ouverture du port  : "+ e);}
    }
    
    public synchronized static void sendMessage(int identifiant, String message, PrintWriter sender) {
        synchronized (clients) {
            for (PrintWriter client : clients) 
            {
                client.println(message);
            }
        }
    }
    
    public synchronized static void removeClient(PrintWriter client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }
    
    public synchronized static void addClient(Socket clientSocket) {
    	try {
	    	PrintWriter sortie = new PrintWriter(clientSocket.getOutputStream(), true);
	        BufferedReader entree = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        
	        synchronized (clients) {
	            clients.add(sortie);
	        }
	        Serveur.handlerClients.add(new ClientHandler(clientSocket, entree, sortie));
	        new Thread(Serveur.handlerClients.getLast()).start();
	        System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());
	      
    	}
        catch (IOException e) {System.out.println("Erreur dans l'ajout du client : "+ e);}

    }
    
    public static synchronized void launchGame(String fileName)
    {	
    	Serveur.snakeGame.setFilename(fileName);
        Serveur.snakeGame.initializeGame();
        Serveur.snakeGame.Launch();
	        
    }
    
    public static synchronized PanelBuilder buildPanelBuilder()
    {
    	return new PanelBuilder(
			Serveur.snakeGame.MakeFeaturesSnake(),
			Serveur.snakeGame.MakeFeaturesItem()
		);
    }
    
    public static synchronized boolean gameIsRunning()
    {
    	return Serveur.snakeGame.getIsRunning();
    }
    
    public static synchronized void setMovementJoueur1(AgentAction action)
    {
    	Serveur.snakeGame.setMovementJoueur1(action);
    }
    
    public static synchronized void setMovementJoueur2(AgentAction action)
    {
    	Serveur.snakeGame.setMovementJoueur2(action);
    }
    
    public static  void findCommunParty()
    {
    	if(Serveur.handlerClients.size() > 1 && ! Serveur.inGame)
    	{
        	System.out.println("Recherche de joueurs ...");
        	
    		for(ClientHandler joueur_1 : Serveur.handlerClients)
    		{
    			if(!joueur_1.inGame())
    			{
	    			for(ClientHandler joueur_2 : Serveur.handlerClients)
	        		{
	    				if(!joueur_2.inGame())
	    				{
		    				if(
									joueur_1.Id() != joueur_2.Id()
								&&
									joueur_1.isReady() && joueur_2.isReady()
								&&
									joueur_1.getPath().equals(joueur_2.getPath())
		    				)
		    			
			    				{
		    						System.out.println("partie Lancer");
		    				    	Serveur.launchGame(joueur_1.getPath());
			    					joueur_1.launchGame();
			    					joueur_2.launchGame();
			    					Serveur.inGame = true;
			    					break;
		
			    				}
	    				}
		    					
	        		}
    			}
    		}
    	}
    }
    
}