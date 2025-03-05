package tp1progreseau;

import java.net.*;
import java.awt.Panel;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import tp1progreseau.model.SnakeGame;
import tp1progreseau.utils.PanelBuilder;
import tp1progreseau.view.PanelSnakeGame;
import tp1progreseau.model.InputMap;
import tp1progreseau.gameElement.fabrique.TypeSnake;


public class Serveur {
    
	private static Set<PrintWriter> clients = new HashSet<>();
    private static SnakeGame snakeGame; 
    
    public static void main(String[] args) {
    	ObjectMapper mapper = new ObjectMapper();
        int port = 2545;
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
	        
	        new Thread(new ClientHandler(clientSocket, entree, sortie)).start();
        	Serveur.launchGame();
	        System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());
	      
    	}
        catch (IOException e) {System.out.println("Erreur dans l'ajout du client : "+ e);}

    }
    
    public static synchronized void launchGame()
    {	
    	
		String filename = "src/tp1progreseau/layouts/smallNoWall.lay";
        Serveur.snakeGame = SnakeGame.buildGame(filename,TypeSnake.HUMAN, TypeSnake.HUMAN);
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

     
}